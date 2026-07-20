# Architectural Styles

> Style is a deployment and dependency topology choice — pick it for constraints, not fashion.

---

## Purpose

Compare Layered, Clean, Hexagonal/Ports-and-Adapters, Onion, Modular Monolith, Microservices, Event-Driven, and Serverless as **production topologies** with explicit advantages, disadvantages, fit, and anti-fit — so you can defend a style choice under Lead/Architect scrutiny.

---

## How to read styles in interviews

Architectural style answers fail when they are taxonomy dumps. Strong answers: (1) state the primary constraint (team topology, consistency, scale unit, regulatory isolation, release cadence); (2) name the style that fits; (3) list what you give up; (4) describe the migration path if constraints change. Styles compose: a modular monolith can be hexagonal inside; microservices can be event-driven between contexts and layered within a service.

---

## Layered Architecture

### Structure

Classic n-tier: Presentation → Application/Service → Domain → Persistence (variants exist). Dependencies point downward. Common in Spring Boot “controller → service → repository” codebases.

### Advantages

- Easy to teach; matches Spring defaults and many enterprise code generators.
- Clear technical roles for junior onboarding.
- Straightforward transaction boundaries on the service layer.
- Works well for CRUD-heavy, single-team systems.

### Disadvantages

- Package-by-layer often destroys domain cohesion (“all repositories together”).
- Domain becomes anemic; business rules leak into services.
- Changes fan out across all layers for one feature.
- Downward-only layering does not by itself protect domain from infrastructure details if entities are JPA-centric.

### When to use

- Single deployable, one primary team, moderate domain complexity.
- Brownfield systems already layered — improve cohesion inside layers before rewriting style.
- Admin/back-office tools where domain richness is low.

### When NOT to use

- Multiple teams needing independent release of capabilities (without modular boundaries).
- Domain-heavy cores where infrastructure leaking into “domain entities” is already painful.
- As the *only* story when you actually need ports at external boundaries.

### Tradeoffs

Simplicity and hiring familiarity vs feature cohesion and long-term change cost. Layering is a technical split; many failures need a domain split (modules/contexts) *inside* or *instead of* pure layers.

---

## Clean Architecture

### Structure

Concentric rings: Entities → Use Cases → Interface Adapters → Frameworks & Drivers. Dependency rule: source code dependencies point inward. Use cases orchestrate; entities hold enterprise rules; adapters translate.

### Advantages

- Explicit use-case layer improves application-service clarity.
- Frameworks (Spring, JPA) become replaceable at the outer ring.
- Strong testability of use cases with fakes at boundaries.
- Forces articulation of application API (input/output ports).

### Disadvantages

- Ceremony cost: mappers at every boundary, many types per feature.
- Teams cargo-cult folder names without enforcing the dependency rule.
- Overkill for simple CRUD; under-specified for distributed runtime concerns (timeouts, sagas).
- “Enterprise entities” vs “application business rules” distinction confuses teams in practice.

### When to use

- Long-lived core domains (payments, policy, claims) with stable use cases and volatile delivery mechanisms.
- Organizations standardizing on use-case-driven application services.
- Codebases that must remain testable without Spring context for domain logic.

### When NOT to use

- Short-lived products, prototypes, thin BFF aggregation services.
- Teams that will not invest in dependency enforcement (ArchUnit) — structure without enforcement decays.
- Mistaking Clean folders for microservice readiness.

### Tradeoffs

High maintainability of policy vs mapping overhead and slower feature scaffolding. Best when use-case stability amortizes ceremony.

---

## Hexagonal Architecture (Ports and Adapters)

### Relationship clarification

**Hexagonal Architecture** (Cockburn) and **Ports and Adapters** are the same idea: application core at the center; **ports** are the API the core exposes or requires; **adapters** implement technology-specific I/O (REST, Kafka, JDBC, SMTP). Hexagonal is the style name; ports and adapters are the mechanism. Do not treat them as competing styles in interviews — treat Ports and Adapters as the operational vocabulary of Hexagonal.

Onion and Clean are close cousins: all invert dependencies so domain/application do not depend on frameworks. Differences are mostly emphasis and layer naming, not opposing worldviews.

### Structure

- **Driving (primary) adapters:** inbound — Controllers, Kafka listeners, schedulers call inbound ports (use cases).
- **Driven (secondary) adapters:** outbound — implement outbound ports (repositories, gateways, publishers).
- Core contains domain model + application services; no Spring/JPA types inward.

### Advantages

- Natural fit for Spring Boot: interfaces as ports, `@Component` adapters, constructor injection.
- Swappable providers (payment, SMS, document store) without core rewrites.
- Clear test seams; contract tests at ports.
- Aligns with ACL patterns at integration edges.

### Disadvantages

- Port explosion if every class gets an interface.
- Teams place JPA entities in “domain” and call it hexagonal.
- Does not define module boundaries across a large monolith by itself.
- Runtime distributed issues (partial failure) still need explicit design beyond ports.

### When to use

- Systems with multiple adapters for the same capability (dual-run providers, batch + API entry).
- Domain cores that must stay free of framework lock-in.
- Modular monolith interiors and individual microservice interiors.

### When NOT to use

- Trivial services with one REST adapter and one DB — a thin layered service may suffice.
- When the team will not keep adapters thin (business logic leaking into controllers/listeners).

### Tradeoffs

Isolation and evolvability of integrations vs indirection and mapper cost. Highest ROI at **external** and **volatile** boundaries.

### Ports and Adapters — practice notes (non-duplicative)

- Name ports after **capabilities** (`FraudCheckPort`), not technologies (`HttpClientPort`).
- Keep adapters free of domain branching; branch in domain/application.
- One port may have multiple adapters (primary + secondary provider); version adapters independently.
- Messaging adapters must define idempotency, deserialization failure, and DLQ behavior — not only “implement port.”

---

## Onion Architecture

### Structure

Domain model at center; domain services; application services; then infrastructure outer layers. Similar dependency rule to Clean/Hexagonal; historically emphasizes domain model purity and infrastructure as outer skin.

### Advantages

- Strong domain-model focus useful for rich domains.
- Clear mental model for DDD-heavy teams.
- Infrastructure replaceability comparable to hexagonal.

### Disadvantages

- Easy to confuse with Clean/Hexagonal in interviews — panels may ask for distinctions; answer with emphasis (domain model centrality) not theology.
- Same ceremony risks as Clean.
- “Domain services” layer often becomes a dumping ground without aggregate discipline.

### When to use

- DDD-centric cores where ubiquitous language and aggregates dominate design.
- Teams already standardized on onion teaching materials.

### When NOT to use

- Integration/BFF services with little domain.
- When the org already standardized on hexagonal vocabulary — do not rename folders for purity.

### Tradeoffs

Domain clarity vs framework of layers that can obscure application workflow if use cases are not explicit. Prefer one vocabulary per org (hexagonal *or* onion *or* clean), enforced consistently.

---

## Modular Monolith

### Structure

Single deployable, multiple modules with enforced boundaries (Maven/Gradle modules, JPMS, ArchUnit, Spring Modulith). Modules align to bounded contexts or capabilities. In-process calls; one primary DB possible with schema ownership per module — or separate schemas in one cluster.

### Advantages

- Single deployment, single trace, ACID transactions across modules when intentionally allowed.
- Refactoring across boundaries is a PR, not a distributed project.
- Lower ops burden than microservices; excellent default for most enterprise Java products.
- Can extract modules to services later if boundaries were real.

### Disadvantages

- Requires discipline; without enforcement, becomes a big ball of mud.
- Scaling is vertical or whole-app horizontal — cannot scale one module’s CPU independently easily.
- Team contention on one pipeline/release if ownership is unclear.
- Technology diversity constrained (one runtime/version).

### When to use

- One product, few teams (or well-partitioned module ownership), strong consistency needs.
- Early/mid-stage products before operational maturity for distributed systems.
- Regulated cores where distributed sagas are a compliance nightmare.

### When NOT to use

- Hard org separation requiring independent deploy and failure isolation (different criticality tiers).
- Need for independent scale/tech per capability proven by metrics.
- Multiple products with different SLOs sharing one release blast radius unacceptably.

### Tradeoffs

Operational simplicity and transactional integrity vs independent scale/deploy. The winning move in many Lead interviews: “modular monolith first, extract on evidence.”

---

## Microservices

### Structure

Independently deployable services, typically database-per-service, communicating via APIs/events. Each service owns a bounded context (aspirationally). Platform: containers, Kubernetes, service discovery, gateway, observability mesh.

### Advantages

- Independent deploy and scale per service.
- Failure isolation when bulkheads are real (not shared DB/thread pools).
- Team autonomy aligned to Conway’s Law.
- Technology heterogeneity where justified.

### Disadvantages

- Distributed failure, eventual consistency, operational complexity.
- Latency and chatty APIs; N+1 service calls.
- Cross-cutting platform cost (auth, tracing, CI, schemas).
- Wrong cuts create distributed monoliths worse than a modular monolith.

### When to use

- Multiple teams, clear bounded contexts, independent release pressure.
- Distinct scale/SLO profiles (ingestion vs interactive auth).
- Regulatory or tenancy isolation requiring separate data planes.

### When NOT to use

- Small team, unclear domain boundaries, shared DB “for now.”
- Strong consistency workflows you are unwilling to redesign as sagas.
- Before platform readiness (CI, observability, on-call, contract testing).

### Tradeoffs

Autonomy and isolation vs consistency and cognitive/ops load. Extract services for **organizational and scale evidence**, not for resume-driven design. (Deep dive: `04-Microservice-Architecture.md`.)

---

## Event-Driven Architecture (EDA)

### Structure

Components communicate by emitting and consuming events via a broker (Kafka, SNS/SQS, RabbitMQ). Styles: notification events, event-carried state transfer, event sourcing, CQRS read models. Choreography vs orchestration for multi-step workflows.

### Advantages

- Temporal decoupling; producers need not know consumers.
- Natural fit for fan-out (notify, index, analytics, audit).
- Smooths load; enables async resilience patterns.
- Supports evolving consumers without producer redeploys (with schema discipline).

### Disadvantages

- End-to-end debugging harder; need correlation IDs and causal graphs.
- Eventual consistency UX and reconciliation requirements.
- Ordering, duplication, poison messages, schema evolution are first-class problems.
- Risk of “event spaghetti” without ownership of topics and consumer SLAs.

### When to use

- Cross-context integration where sync coupling would create availability chains.
- High ingest / stream processing (CDR, clickstream, payment webhooks).
- Side effects that must not block the command path (email, search index).

### When NOT to use

- UX requiring immediate strong consistency across all readers without a sync path.
- Simple request/response CRUD with one writer and one reader.
- Teams without schema registry, DLQ practice, and replay runbooks.

### Tradeoffs

Loose coupling and scale vs observability and consistency cost. Prefer EDA between contexts; keep sync commands inside a context where invariants demand it.

---

## Serverless

### Structure

Functions-as-a-service (AWS Lambda, etc.), managed triggers (HTTP, queues, schedules), pay-per-use. Often paired with managed data (DynamoDB, S3, managed Kafka). In enterprise Java: SnapStart/custom runtimes matter for cold starts; many orgs still prefer containers for long-lived Spring Boot.

### Advantages

- Elastic scale to zero; reduced idle cost for spiky workloads.
- Less server ops for the product team (shifted to cloud provider).
- Natural fit for event handlers, webhooks, batch transforms, glue code.

### Disadvantages

- Cold starts, execution time limits, local development friction (especially heavy Spring).
- Vendor constraints; distributed sprawl of tiny functions without architecture.
- Connection pool / DB saturation anti-patterns from high concurrency.
- Observability and versioning discipline often weaker than for services.

### When to use

- Spiky, short-lived compute; event processors; scheduled jobs; edge transforms.
- Glue between SaaS systems; low-duty-cycle integrations.
- Teams with strong cloud platform support and function standards.

### When NOT to use

- Long-lived, latency-sensitive core transactional APIs with heavy JVM frameworks — unless heavily optimized.
- Workloads needing persistent connections, large in-memory state, or specialized networking.
- When function mesh recreates microservices complexity without service-level engineering practices.

### Tradeoffs

Ops leverage and elasticity vs execution constraints and architectural fragmentation. Hybrid is common: containerized Spring cores + serverless for async edges.

---

## Style composition matrix (Lead framing)

| Interior of a deployable | Between deployables |
|--------------------------|---------------------|
| Layered or Hexagonal/Clean packages | Sync APIs and/or Events |
| Modular monolith modules | Microservices + gateway |
| Rich domain (DDD) | ACL + events between contexts |

Default recommendation for many enterprise Java orgs: **Modular monolith + hexagonal modules**; extract **microservices** where team/SLO/data isolation demands; integrate with **events** for non-critical path coupling; use **serverless** at the rim.

---

## Senior vs Lead framing

| Senior | Lead / Architect |
|--------|------------------|
| Implements a style correctly inside a service | Chooses style for constraints and org topology |
| Refactors anemic layers toward ports | Prevents style thrash and vocabulary fragmentation |
| Knows hexagonal ≠ microservices | Times extraction with platform readiness |
| Operates Kafka consumers well | Owns event taxonomy and consistency UX standards |

---

## Why this matters in production

Style choices lock in **failure modes and cost curves**. A premature microservice mesh increases incident surface before team maturity. A mud monolith blocks parallel delivery. EDA without replay/DLQ practice creates silent data drift. Serverless without concurrency controls melts Postgres. Architecture interviews probe whether you pick styles as **risk management**, not identity.

---

## Engineering tradeoffs

- Hexagonal/Clean protect the domain; ceremony slows trivial features.
- Modular monolith optimizes change speed and transactions; limits independent scale.
- Microservices optimize team autonomy and isolation; tax consistency and ops.
- EDA optimizes coupling in time; taxes debugging and user-visible consistency.
- Serverless optimizes idle cost and glue; taxes cold path latency and coherence of the estate.
- One org-wide style vocabulary reduces cognitive load; forced uniformity can mis-fit outlier domains.

---

## Common anti-patterns

- “We do Clean Architecture” with JPA entities as enterprise models and use cases as pass-throughs.
- Calling every Spring service hexagonal because an interface exists.
- Microservices + shared database + sync call chains (distributed monolith).
- Event-driven everything, including hard invariants that needed a single aggregate transaction.
- Rewriting a working modular monolith into services to “modernize” without boundary evidence.
- Mixing Clean/Onion/Hexagonal folder schemes in one repo without dependency rules.
- Serverless functions calling each other synchronously in deep chains (distributed monolith on Lambda).
- Treating style diagrams as done architecture — omitting SLOs, data ownership, and operability.

---

## Best practices

- Choose style from constraints: team count, consistency, scale unit, regulation, platform maturity.
- Standardize one interior style (prefer hexagonal ports at edges) across services.
- Enforce module/port dependency rules in CI.
- Prefer modular monolith until extraction criteria are met and written in an ADR.
- For EDA: schema registry, idempotent consumers, outbox, correlation, DLQ, replay ownership.
- Document style in ADRs with rejected alternatives.
- Measure: deploy frequency, change failure rate, MTTR, cross-module PR rate, sync call depth.
- Keep adapters thin; keep workflows visible in application services.

---

## Architecture review checklist

- [ ] What primary constraint drove this style choice?
- [ ] What style was rejected and why (ADR)?
- [ ] Are interior dependency rules enforced automatically?
- [ ] For monoliths: are module boundaries real (no illegal imports / shared tables abuse)?
- [ ] For microservices: is data ownership per service explicit?
- [ ] For EDA: are schema compatibility, idempotency, and DLQ proven?
- [ ] For serverless: are concurrency, cold start, and downstream pool limits designed?
- [ ] Is the end-to-end consistency model explained to product (not only engineers)?
- [ ] Can you extract or consolidate later without a rewrite?
- [ ] Does observability match the style (traces across sync hops; causal event views)?

---

## Interview Challenge

A fintech core banking ledger team of 14 engineers currently runs a Spring Boot modular monolith with clear packages (Accounts, Payments, Statements). Peak load is manageable on a primary PostgreSQL with read replicas. Leadership asks for “microservices and event-driven on Kafka by Q4” to attract talent and prepare for scale. A second team wants to build Cards as a separate product with a different release cadence. How do you shape the architecture decision?

### Suggested Answer

Separate **talent fashion** from **constraints**. Keep the ledger as a modular monolith (or extract only if regulatory/scale evidence appears) — money movement benefits from ACID aggregates and simple operability. For Cards, evaluate a **separate bounded context/deployable** because of different cadence and team — with an ACL and events (card posted, authorization advice) into ledger, not shared tables. Introduce Kafka where async integration and fan-out help (notifications, analytics, statement materialization), not as a replacement for ledger invariants. Write ADRs: (1) ledger remains modular monolith; (2) Cards is a separate service with own DB; (3) integration via events + explicit sync APIs for auth-time needs; (4) extraction criteria for any future Payments split (team topology, SLO divergence, scale metrics). Invest in Spring Modulith/ArchUnit and contract tests so “prepare for scale” means **real boundaries**, not a death-march rewrite. Offer a talent narrative: modern modular + hexagonal practices inside the monolith.

---

## Architecture Reflection Questions

1. Which style in your estate is mismatched to its constraints — and what is the cheapest corrective move?
2. Where did you successfully extract a module to a service, and what evidence triggered it?
3. How does your team explain eventual consistency to product managers for EDA paths?
4. If you standardized vocabulary tomorrow, would you pick Clean, Onion, or Hexagonal — and why one only?
5. What would make you consolidate two microservices back into a modular monolith?

---

## Interview Confidence Checklist

- [ ] Explains Hexagonal ≡ Ports and Adapters without treating them as rivals
- [ ] Distinguishes Clean/Onion/Hexagonal as cousins with different emphasis
- [ ] Defends modular monolith as a first-class production style
- [ ] States clear when-NOT-to-use for microservices, EDA, and serverless
- [ ] Composes styles (hexagonal inside, events between)
- [ ] Ties style to Conway, SLOs, and data ownership
- [ ] Has ADR examples for style choice and rejected alternatives
- [ ] Can diagnose distributed monolith symptoms
- [ ] Avoids folder-structure cargo cult in answers
- [ ] Lead framing: sets org default style and extraction criteria
