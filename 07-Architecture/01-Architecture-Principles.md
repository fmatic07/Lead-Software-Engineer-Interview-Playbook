# Architecture Principles

> Principles as decision filters for change cost, blast radius, and team velocity — not slogans on a slide.

---

## Purpose

Demonstrate that you apply Separation of Concerns, SOLID, DRY, KISS, YAGNI, cohesion/coupling, composition, dependency inversion, and design-for-change as **production judgment**, including when you deliberately violate them with documented rationale.

---

## Separation of Concerns

### Explanation (production terms)

Separate axes of change so one reason to change does not force edits across unrelated surfaces. In enterprise Java, typical concerns: HTTP/API shape, authn/authz, domain invariants, persistence, messaging, observability, and orchestration. Mixing them produces controllers that open transactions, call Kafka, map DTOs, and enforce business rules in one method — untestable and unowned.

### When to apply

- Distinct ownership (platform vs domain team).
- Different change rates (API versioning vs ledger rules).
- Different failure modes (network vs invariant violation).
- Need for independent testing or deployment of a concern.

### When to violate deliberately

- Tiny CRUD modules with one owner and no credible second concern.
- Prototypes with a hard kill date (document the merge-back plan).
- Performance-critical hot paths where layering adds measurable latency *and* you have profiles proving it — rare; prefer packaging, not god-methods.

### Anti-patterns

- “Utility” classes that mix parsing, persistence, and business policy.
- Cross-cutting concerns copy-pasted into every service instead of platform libraries.
- “Shared kernel” JAR that becomes a dumping ground for every concern.

### Interview angle

Panels ask whether you can draw boundaries that match **change and ownership**, not textbook layers. Cite a refactor that reduced blast radius when an API contract changed without touching ledger logic.

---

## SOLID

### Single Responsibility (SRP)

One module should have one reason to change — framed as **one stakeholder axis**, not “one method does one thing.” A Spring `@Service` that both prices orders and publishes audit events has two change drivers: pricing product owners and compliance.

**Apply:** Pricing vs settlement, auth vs business rules, read models vs write models.  
**Violate:** Trivial adapters where splitting creates empty indirection.  
**Anti-pattern:** 2,000-line “OrderService” owning validation, inventory, payments, and notifications.  
**Interview:** “What is the reason this class changes?” — name the stakeholder.

### Open/Closed (OCP)

Extend behavior without modifying stable cores. In Spring: Strategy beans, Spring events, plugin registries, sealed hierarchies with new subtypes — not endless `if` ladders in a core class.

**Apply:** Fee calculation policies, payment providers, notification channels.  
**Violate:** One-off branches that will never recur; sealed types with two known cases.  
**Anti-pattern:** Framework of plugins for two static tax rules.  
**Interview:** Show you open at **volatile** boundaries, close the invariant core.

### Liskov Substitution (LSP)

Subtypes must honor the contract of the type they replace. Breaking LSP in Java shows up as `UnsupportedOperationException` in overrides, narrowed preconditions, or weakened postconditions — especially with JPA inheritance and “base repository” designs.

**Apply:** Shared interfaces for interchangeable adapters (e.g., `PaymentGateway`).  
**Violate:** Inheritance used only for code reuse (prefer composition).  
**Anti-pattern:** `ReadOnlyList.add` throwing; “special” subclasses that skip validation.  
**Interview:** Contracts and tests for substitutability beat UML inheritance trees.

### Interface Segregation (ISP)

Clients should not depend on methods they do not use. Fat ports force mock hell and accidental coupling.

**Apply:** Split `OrderReader` / `OrderWriter`; separate admin vs customer APIs.  
**Violate:** One interface with three methods all used by the same client.  
**Anti-pattern:** `IOrderManager` with 40 methods “for flexibility.”  
**Interview:** Ports shaped by **callers**, not by implementing classes.

### Dependency Inversion (DIP)

High-level policy depends on abstractions; details depend on those same abstractions. Domain defines `PaymentPort`; infrastructure implements Stripe/Adyen adapters. Spring DI wires the direction; packages enforce it (ArchUnit).

**Apply:** External systems, databases, clocks, ID generators at domain edges.  
**Violate:** Internal helpers with one implementation and no test seam need.  
**Anti-pattern:** Domain importing Spring Data entities and Feign clients.  
**Interview:** Draw the dependency arrow; wrong direction is an architecture smell.

---

## DRY (Don't Repeat Yourself)

### Explanation

DRY targets **knowledge** duplication, not character duplication. Two similar-looking mappers that encode different bounded-context meanings are not DRY violations. Copy-paste of the *same* business rule in three services is.

### When to apply

- Single authoritative invariant (fee formula, eligibility rule).
- Shared platform concerns (correlation IDs, problem+json, idempotency filters).
- Schema/contracts owned by one team and consumed by many.

### When to violate deliberately

- Similar code across bounded contexts that must evolve independently (prefer duplication over shared library coupling).
- Temporary dual-running during migrations.
- Team autonomy where a shared module would create release coupling worse than duplication cost.

### Anti-patterns

- Shared “common” JAR that every service depends on for domain types.
- Premature abstraction after seeing two similar lines.
- Microservices sharing a database “to avoid duplicating data.”

### Interview angle

“I duplicated deliberately across contexts” is a strong Lead answer when you name the coupling you avoided and the reconciliation strategy.

---

## KISS (Keep It Simple)

### Explanation

Simplest design that meets current NFRs and credible near-term change. Complexity tax is paid in onboarding, incidents, and change latency. In Spring shops, simplicity often means a modular monolith with clear packages before a mesh of services.

### When to apply

- Always as the default; complexity requires a named beneficiary (scale, isolation, team topology).
- When operational maturity cannot yet support distributed failure modes.

### When to violate deliberately

- Regulatory isolation requiring separate deployables/data stores.
- Proven scale ceilings that a simple design cannot meet.
- Explicit investment in platform (event backbone) that multiple products will share.

### Anti-patterns

- Event sourcing for a CRUD admin tool.
- Saga orchestration for a single-DB transaction.
- Custom actor frameworks where Spring’s thread model and DB transactions suffice.

### Interview angle

Show a design you *rejected* for being clever. Panels trust engineers who can say no to fashionable complexity.

---

## YAGNI (You Aren't Gonna Need It)

### Explanation

Do not build speculative extension points, multi-region active-active, or plugin systems without evidence. YAGNI is not “never design for change” — it is “do not pay for unused options.”

### When to apply

- Speculative microservice splits “for scale someday.”
- Abstract factories with one implementation.
- Configurable rule engines before rules exist.

### When to violate deliberately

- Known regulatory roadmap with fixed deadlines (build audit hooks now).
- Platform APIs where breaking changes are politically expensive — invest in versioning early.
- Capacity you know arrives (Black Friday) within the planning horizon.

### Anti-patterns

- Hexagonal ceremony with no second adapter forever.
- “Future-proof” Kafka topics with unused event types.
- Premature CQRS because “reads might get heavy.”

### Interview angle

Distinguish **options** (cheap seams: interfaces at boundaries) from **implementations** (expensive infrastructure). Prefer cheap options.

---

## High Cohesion

### Explanation

Elements that change together live together. A cohesive `Billing` module contains invoice calculation, tax application, and invoice persistence rules — not random “helpers.” Package-by-feature with internal layers often beats package-by-layer sprawl for cohesion.

### When to apply

- Module and package design, aggregate boundaries, team ownership maps.
- Deciding what belongs in a service vs a library.

### When to violate deliberately

- Cross-cutting platform libraries (logging, metrics) that are intentionally low domain cohesion.
- Temporary strangler façades that glue old and new during migration.

### Anti-patterns

- “Utils” / “Helpers” packages as cohesion black holes.
- Anemic domain + procedural services scattering related rules.
- Feature logic split across API, “manager,” and random listeners with no home.

### Interview angle

Walk a change (“add withholding tax”) and show how many modules touch — cohesion shows in blast radius.

---

## Low Coupling

### Explanation

Minimize knowledge one module needs of another’s internals. Prefer contracts (APIs, events, ports) over shared tables, shared mutable libraries, and reach-ins. Coupling is not binary: temporal, deployment, data, and semantic coupling all matter.

### When to apply

- Service boundaries, library APIs, event schemas, ACL between contexts.
- Any integration with a team you do not control.

### When to violate deliberately

- Tight coupling inside an aggregate or a single deployable for transactional integrity.
- Performance-critical co-location of chatty collaborators in-process.

### Anti-patterns

- Shared database across “microservices.”
- Distributed monolith: separate deploys, synchronous mesh of calls, shared release train.
- Leaking foreign keys and join queries across context boundaries.

### Interview angle

Name the *type* of coupling you accept and why (e.g., sync HTTP for UX-critical reads; async events for side effects).

---

## Composition vs Inheritance

### Explanation

Composition assembles behavior from collaborators; inheritance specializes types. In modern Java (records, sealed types, Spring injection), composition is the default for reuse and variation. Inheritance remains useful for framework extension points and narrow type hierarchies with true subtype semantics.

### When to apply composition

- Policy variation (pricing, risk), decorator-style cross-cutting (metrics, retries), adapting external APIs.
- Replacing Template Method deep trees with Strategy + small helpers.

### When inheritance is justified

- Framework contracts (`OncePerRequestFilter`, JPA mapped inheritance with clear LSP).
- Closed hierarchies (sealed) expressing a domain sum type.

### When to violate the “prefer composition” rule

- Not a violation — inheritance is fine when LSP holds and the hierarchy is shallow and stable.

### Anti-patterns

- Deep “BaseService” inheritance for shared transactional code.
- Inheritance for code reuse of unrelated behaviors.
- God base classes that force overrides of half their methods.

### Interview angle

“Composition localizes change; inheritance couples lifecycles.” Offer a refactor from Template Method to Strategy you led.

---

## Dependency Inversion (deep dive)

### Explanation

DIP is the structural backbone of hexagonal/clean designs in Spring Boot: domain/application define ports; adapters implement them. Constructor injection makes dependencies explicit; field injection and static singletons hide them.

### Production examples

- `Clock` / `TimeProvider` port for interest accrual determinism in tests and replay.
- `IdempotencyStore` port with Redis adapter in prod, in-memory in tests.
- Outbox port so domain commits and message durability share one transaction boundary.

### When to apply

- Any dependency that is I/O, time, randomness, or an external system.
- Boundaries you will mock/fake in component tests.

### When to violate

- Pure functions and value objects — no port needed.
- Spring MVC controllers depending on application services directly (framework edge).

### Anti-patterns

- Ports for every class (“interface per class” cargo cult).
- Returning JPA entities through ports into API layers.
- Circular dependencies “fixed” with `@Lazy`.

### Interview angle

Show package structure and ArchUnit rules. Dependency direction is an enforceable fitness function, not a diagram.

---

## Designing for Change

### Explanation

Design for the changes you can **credibly forecast**: regulatory rules, provider swaps, channel growth, scale unit shifts. Use seams (ports, events, versioned APIs), not speculative microservices. Evolutionary architecture uses ADRs and fitness functions (ArchUnit, contract tests, SLO burn) to keep the design honest under change.

### Axes of change to map explicitly

| Axis | Typical seam |
|------|----------------|
| Provider / vendor | Adapter + ACL |
| Channel (mobile/web/partner) | BFF / API versioning |
| Business policy | Strategy / rules module |
| Scale / tenancy | Partition key, shard, quota |
| Consistency needs | Sync vs async boundary |
| Team ownership | Bounded context / module |

### When to apply

- Multi-year product lines, regulated domains, multi-provider integrations.
- Organizations where Conway’s Law will reshape the system anyway — align modules to teams early.

### When not to over-invest

- Short-lived campaigns, internal tools with one user group, greenfield spikes.

### Anti-patterns

- Big Bang rewrite “to make it flexible.”
- Abstracting every noun into an interface on day one.
- Ignoring operability as a change cost (you cannot evolve what you cannot observe).

### Interview angle

Tell a story: predicted change → seam placed → change arrived → blast radius was X. Or: predicted change never came → you removed the abstraction (also a win).

---

## Senior vs Lead framing

| Senior | Lead / Architect |
|--------|------------------|
| Applies principles in module design and reviews | Sets org defaults, ADRs, and fitness functions |
| Refactors local SRP/DIP violations | Negotiates boundaries across teams and contexts |
| Knows when to duplicate vs share | Owns coupling budget between services |
| Improves cohesion in a package | Aligns module boundaries with team topology |

---

## Why this matters in production

Principles determine **where defects cluster** and **how expensive tomorrow’s change is**. Cohesion and coupling show up in incident MTTR (how many repos to patch). DIP and SoC show up in test reliability. YAGNI/KISS show up in on-call cognitive load. Interview panels and architecture boards are probing whether your principles reduce operational risk — not whether you can recite acronyms.

---

## Engineering tradeoffs

- Strict layering improves testability and ownership clarity; excess layers add indirection and slower delivery on simple paths.
- DRY via shared libraries reduces inconsistency; shared domain libraries create release and semantic coupling across teams.
- DIP enables substitution and testing; port proliferation increases ceremony and navigation cost.
- Designing for change preserves options; unused options rot into accidental complexity.
- Composition increases wiring visibility; deep inheritance can reduce boilerplate at the cost of fragile base classes.
- Enforcing principles with ArchUnit/CI prevents drift; over-strict rules block pragmatic exceptions needed for strangler migrations.

---

## Common anti-patterns

- Principle theater: SOLID posters while OrderService does everything.
- Distributed monolith justified as “microservices for SRP.”
- Shared database called “pragmatic DRY.”
- Interface-per-class DIP without a second implementation or test seam.
- Premature hexagonal folders in a 3-endpoint service.
- Inheritance hierarchies for cross-cutting concerns instead of decorators/AOP/filters.
- “Utils” packages destroying cohesion.
- Copying principles from Clean Architecture books without mapping to team ownership and deploy topology.

---

## Best practices

- Name modules and packages after **domain capabilities**, not technical layers alone.
- Put abstractions at **volatile boundaries**; keep stable cores concrete and boring.
- Prefer duplication across bounded contexts over shared mutable domain models.
- Enforce dependency direction with ArchUnit (or equivalent) in CI.
- Record deliberate violations in ADRs with expiry or revisit criteria.
- Measure principle health via change blast radius, escaped defects, and onboarding time — not class counts.
- Keep Spring controllers thin; push policy to domain/application services; keep adapters at the edges.
- Review PRs for coupling type introduced (data, temporal, deployment), not only style.

---

## Architecture review checklist

- [ ] Can you name the primary change axes for this system in the next 12 months?
- [ ] Do package/module boundaries match ownership and change axes?
- [ ] Are dependency arrows from domain → infrastructure, never the reverse?
- [ ] Is each public module’s reason-to-change articulable in one sentence?
- [ ] Are shared libraries limited to stable platform concerns (not domain types)?
- [ ] Are deliberate principle violations documented with owners and revisit dates?
- [ ] Do fitness functions (ArchUnit, contract tests) encode the non-negotiables?
- [ ] Is complexity justified by a current NFR or credible near-term requirement?
- [ ] Would a new engineer find the “home” for a business rule without tribal knowledge?
- [ ] Does the design keep transactional integrity where the business requires it (not split for purity)?

---

## Interview Challenge

A payments team proposes: (1) a shared `finance-commons` JAR with Money, AccountId, FeeRules, and JPA entities; (2) deep `BaseTransactionalService` inheritance for all write services; (3) splitting notification, ledger, and API into three microservices next quarter “for SOLID,” while keeping one PostgreSQL database. Product expects two fee-rule variants this year and a second payment provider next year. How do you respond as Lead/Architect?

### Suggested Answer

Reject the shared domain JAR for FeeRules and JPA entities — that couples bounded contexts and deploy cycles; keep Money as a careful, immutable shared kernel *only* if semantics are identical and versioned, otherwise duplicate value objects per context. Replace BaseTransactionalService with composition (explicit application services + Spring `@Transactional` on narrow use-case methods) or a small transactional template helper without inheritance. Block the three-way split while a shared DB remains — that is a distributed monolith; either keep a modular monolith with clear packages/modules or split **with** database-per-service and explicit integration (events/ACL). Invest seams where change is real: Strategy/port for fee rules now; PaymentGateway port + ACL for the second provider; keep notifications as async consumers behind an outbox. Write ADRs for rejected shared DB split and accepted provider port. Define ArchUnit rules for module dependencies and a fitness check that domain packages do not import adapters.

---

## Architecture Reflection Questions

1. Which principle violation in your current system costs the most in incident time — and what seam would cut that cost?
2. Where have you deliberately duplicated knowledge across contexts, and what coupling did that prevent?
3. What abstraction did you remove because the expected variation never arrived?
4. How do you teach juniors the difference between knowledge DRY and code DRY?
5. If Conway’s Law redrew your org tomorrow, which module boundaries would still hold?

---

## Interview Confidence Checklist

- [ ] Can explain each principle with a production apply / violate pair
- [ ] Distinguishes knowledge DRY from character duplication
- [ ] Draws dependency direction and enforces it in CI
- [ ] Prefers composition; justifies inheritance with LSP
- [ ] Rejects microservice splits that retain shared DB coupling
- [ ] Frames YAGNI as “cheap options vs expensive implementations”
- [ ] Ties principles to blast radius, MTTR, and team ownership
- [ ] Has at least one ADR story about a deliberate violation
- [ ] Can run an architecture review using the checklist above cold
- [ ] Senior vs Lead: can describe how you institutionalize principles, not only follow them
