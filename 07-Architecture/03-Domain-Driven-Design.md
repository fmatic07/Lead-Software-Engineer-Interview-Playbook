# Domain-Driven Design

> DDD is a collaboration and boundary tool for complex domains — not a folder template or entity stereotype checklist.

---

## Purpose

Use Ubiquitous Language, Bounded Contexts, Aggregates, Entities, Value Objects, Repositories, Domain/Application Services, and Anti-Corruption Layers as **production design instruments** in enterprise Java (Spring Boot, JPA, Kafka), with interview-ready judgment on when DDD pays off and when it is ceremony.

---

## When DDD earns its keep

Apply strategic + tactical DDD when domain rules are complex, multi-team, and long-lived (payments, insurance policy, telecom BSS, lending). Skip deep tactical DDD for CRUD admin tools, pure BFFs, and ETL glue — a simple transactional service model is enough. Lead-level skill is **scoping DDD investment** to the core domain, not spraying aggregates across every service.

---

## Ubiquitous Language

### Explanation

A shared language between domain experts and engineers, reflected in code names, APIs, events, and docs. Inconsistent language (“Customer” vs “Subscriber” vs “Account Holder” for different meanings) creates defect-prone translations and wrong invariants.

### Production examples

- Banking: `Account`, `LedgerEntry`, `Hold`, `Settlement` — not generic `Transaction` for everything.
- Telecom: `MSISDN`, `Provision`, `Suspend` with precise lifecycle meanings agreed with BSS PMs.
- Lending: `Obligation` vs `Facility` vs `Disbursement` — collapsing them into `Loan` caused incorrect interest posting.

### Interview discussion

Show a glossary you maintained, or a rename you forced when code said `User` but experts said `Policyholder`. Panels listen for whether language drives model boundaries or is after-the-fact naming polish.

### When language work is mandatory vs optional

- **Mandatory:** money movement, eligibility, lifecycle states, regulatory terms — wrong synonyms create wrong code paths.
- **Optional polish:** internal DTO field names in a throwaway admin importer — do not block delivery for glossary perfection.

### Anti-patterns

- Technical names in domain APIs (`CustomerDTOManager`).
- One word, multiple meanings across modules without context qualification.
- Language owned only by engineers; experts never see the model.
- Translating expert language into “more technical” names for perceived professionalism.

### Senior vs Lead on language

Senior enforces consistent naming in PRs and refactors misleading types. Lead runs model exploration with experts, owns the glossary as a living artifact, and stops enterprise synonym collapse (“one Customer table to rule them all”).

---

## Bounded Context

### Explanation

A boundary within which a model and ubiquitous language are consistent. Different contexts may use the same word differently (`Order` in Sales vs Fulfillment vs Billing). Integration happens via published languages, APIs, events, and ACLs — not via one enterprise mega-model.

### Production examples

- E-commerce: Catalog, Cart, Checkout, Fulfillment, Billing — each with its own `Product`/`Price` semantics.
- Bank: Customer Onboarding vs Payments vs Collections — “Customer status” means different things; do not share one status enum DB table.
- Telco: CRM “Customer” vs Network “Subscriber” integrated through explicit mapping.

### Interview discussion

Draw context maps (partner, customer-supplier, conformist, ACL). Explain a painful shared database that forced a split into contexts. Senior: models a context well. Lead: negotiates context boundaries with team topology (Conway).

### When to split / merge

- Split when language conflicts, release cadence diverges, or invariants differ.
- Merge when two “contexts” always change together and share one language — accidental split tax.

### Anti-patterns

- Enterprise data model spanning all contexts.
- Microservices per table without language boundaries.
- Shared libraries of domain entities across contexts.

---

## Aggregate

### Explanation

A cluster of entities/value objects treated as a consistency boundary with a root. Invariants are enforced inside the aggregate; external references prefer IDs of roots, not deep object graphs. Transactional rule of thumb: **one aggregate per transaction** for strong consistency; coordinate multiple aggregates via domain events/processes.

### Production examples

- `Account` aggregate: balance, holds, overdraft policy — cannot overdraw beyond policy in one commit.
- `PurchaseOrder` aggregate: lines, tax snapshot — quantity changes recalculate totals atomically.
- Bad aggregate: entire `Customer` graph including all orders/payments — impossible concurrency and load.

### Interview discussion

How do you size aggregates? Prefer small aggregates for throughput; enlarge only when invariants truly span the cluster. Discuss optimistic locking (`@Version`) and contention hotspots (single aggregate root for global counters).

### Anti-patterns

- Aggregates that are JPA entity graphs loaded lazily across the world.
- Modifying multiple aggregates in one `@Transactional` service “just in case.”
- Anemic aggregates with all rules in services.
- Using aggregates as a reason to eager-fetch entire object graphs for every use case.

### Design heuristics

1. Start from the invariant: “What must be true together after one user command?”
2. Prefer IDs for cross-aggregate references; load collaborators explicitly when needed.
3. Measure write contention on the root; split if hot without invariant necessity.
4. Publish domain events *after* successful local commit (outbox) for other aggregates/contexts.

---

## Entity

### Explanation

An object defined by **identity** continuity over time, not by attribute equality. Identities are stable (UUID/ULID/business IDs). Lifecycle and state transitions matter. In JPA, entities map naturally — but JPA entity ≠ DDD entity automatically; persistence concerns can corrupt the domain model if unchecked.

### Production examples

- `PaymentId` remaining constant while status moves Authorized → Captured → Settled.
- `Shipment` identity stable across carrier handoffs; attributes change.

### Interview discussion

Equality by ID vs value; careful with JPA `equals/hashCode`. Prefer domain identity over database surrogate leakage in APIs where business IDs exist.

### Anti-patterns

- Mutable equals based on attributes causing Set/map corruption.
- Exposing raw entities through REST without a published language.
- God entities with dozens of unrelated fields from multiple contexts.

---

## Value Object

### Explanation

Defined by **attributes**; immutable; interchangeable when values equal. Examples: `Money(amount, currency)`, `DateRange`, `Address`, `MSISDN`. Encapsulate validation and domain operations (`Money.add` rejects currency mismatch).

### Production examples

- `Money` preventing floating-point ledger bugs; currency-checked arithmetic.
- `Email` value object validating format once; used across commands.
- `TaxRate` as value object inside invoice lines snapshotted at booking time.

### Interview discussion

Why immutability matters under concurrency and in event payloads. Prefer value objects over primitive obsession (`BigDecimal` scattered with unclear currency).

### Anti-patterns

- Mutable “value objects.”
- Anemic primitives for core domain concepts.
- JPA `@Embeddable` treated as optional sugar without domain behavior.

---

## Repository

### Explanation

Abstraction for retrieving and persisting aggregates. Speaks domain language: `accountRepository.findById`, `save(account)`. Hides storage technology. In Spring Data, resist letting `JpaRepository` sprawl become the domain API — custom repository ports keep query intent domain-named and prevent infrastructure leakage into application services.

### Production examples

- `PaymentRepository` with `findAuthorizedByMerchantSince(...)` expressing domain questions.
- Outbox stored alongside aggregate in the same repository transaction for reliable publishing.

### Interview discussion

Repository per aggregate root, not per table. Collections-like semantics; no UI paging concerns leaking into domain ports (application layer may add queries). Distinguish read models (queries) from write repositories in CQRS-ish designs.

### Anti-patterns

- Repositories returning DTOs for every screen (confuses write model).
- Specs/queries that bypass aggregate invariants by updating tables directly.
- One giant `EntityManager` service with no repository vocabulary.

---

## Domain Service

### Explanation

Domain operations that do not naturally belong to a single entity/value object — still pure domain policy, no application workflow or I/O. Example: FX conversion policy using two `Money` values and a rate table abstraction; eligibility scoring across multiple aggregates’ data already loaded.

### Production examples

- `OverdraftPolicyService.canAuthorize(account, amount, riskGrade)` coordinating value objects/entities without owning persistence.
- `TariffCalculator` in telecom rating as domain service with pure inputs/outputs.

### Interview discussion

When to prefer domain service vs method on aggregate: if the rule is about one aggregate, put it on the root; if it spans multiple domain objects without being a workflow, domain service. Avoid “XxxDomainService” becoming application layer in disguise.

### Anti-patterns

- Domain services that call repositories/HTTP (those are application/infrastructure).
- Stateless service classes holding all logic while entities are bags of getters (anemic domain).

---

## Application Service

### Explanation

Use-case / workflow orchestration: transaction boundary, load aggregates via repositories, call domain logic, publish events, invoke ports. No business invariants buried here if avoidable — **coordinates**, does not own rules. In Spring: `@Service` + `@Transactional` on command methods is the usual home.

### Production examples

- `CapturePaymentService.capture(command)`: load payment, verify state transition, save, write outbox.
- `OpenAccountService`: create aggregate, assign IDs, persist, emit `AccountOpened`.

### Interview discussion

Difference from domain service is a classic panel question — answer with **I/O and transaction/workflow vs pure domain policy**. Controllers should be thinner than application services; application services thinner on rules than aggregates/domain services.

### Anti-patterns

- 1,000-line application services with embedded business rules and SQL.
- Transactions opened in controllers.
- Domain events emitted without persistence atomicity (no outbox).

---

## Anti-Corruption Layer (ACL)

### Explanation

A translation layer protecting your model from an external context’s model (legacy core, vendor API, partner SOAP). Prevents foreign concepts from leaking into your ubiquitous language. Implemented as adapters/mappers/facades at the boundary — often the driven/driving adapter in hexagonal terms.

### Production examples

- Mapping vendor “CustomerAccountStatus=4” to domain `CollectionState.ACTIVE_ARRANGEMENT`.
- Strangler: ACL in front of mainframe account inquiry while new ledger owns the language.
- Partner ISO8583 messages translated to internal authorization commands.

### Interview discussion

ACL cost (mapping, dual models) vs corruption cost (legacy fields everywhere). Version ACLs; test contracts with consumer-driven or golden message fixtures. Lead: decide where ACL lives (separate module/service) and who owns mapping defects.

### Anti-patterns

- Exposing vendor DTOs throughout the domain.
- “Shared canonical model” that is actually the vendor’s model renamed.
- ACL that performs heavy business workflows (should stay translation + simple adaptation).
- Hand-written one-off mappers with no golden fixtures — silent semantic drift on vendor upgrades.

### ACL placement options

| Placement | Fit |
|-----------|-----|
| In-process adapter module | Same deployable, strong protection, simplest ops |
| Dedicated anti-corruption service | Isolates legacy volatility; extra hop and ownership |
| BFF-only mapping | Only if corruption risk is UI-shaped, not domain-shaped — rare for cores |

---

## Domain events (tactical companion)

Domain events record something meaningful that happened in the domain (`PaymentCaptured`, `AccountOverdrawn`). Inside a monolith they may be in-process; across processes they require durable publication. They are not a substitute for aggregates: invariants still commit locally first.

**Interview distinction:** domain event (business meaning) vs integration event (published language for other contexts) — sometimes identical payloads, sometimes ACL-translated. Do not broadcast internal entity state dumps as public contracts.

---

## JPA / Spring friction points (production)

- Lazy-loading outside transaction → surprises; prefer explicit fetch plans per use case.
- Bidirectional graphs that recreate mega-aggregates via convenience associations.
- `@Entity` in the API layer leaking persistence and JSON shape coupling.
- Spring Data method names becoming an accidental query language that bypasses domain verbs.
- Optimistic lock failures under contention — treat as expected; retry policy must be idempotent at application level.

Mitigation: map JPA persistence models to domain aggregates at the repository adapter when friction is high; or keep a carefully constrained “domain-oriented” persistence model with ArchUnit guards — pick one approach per context and enforce it.

---

## Tactical building blocks — quick reference

| Building block | Identity | Consistency | I/O |
|----------------|----------|-------------|-----|
| Entity | Yes | Part of aggregate | Via repository |
| Value Object | No (by value) | Inside aggregate | Embedded |
| Aggregate | Root ID | Transaction boundary | Repository |
| Domain Service | N/A | Pure policy | None |
| Application Service | N/A | Orchestrates TX | Yes |
| Repository | N/A | Persistence | Yes |
| ACL | N/A | Boundary translation | Yes |

---

## Strategic design (Lead essentials)

- **Core / Supporting / Generic** domains: invest DDD depth in core; buy or keep thin for generic (auth adapters, notification gateways, file storage).
- **Context mapping patterns:**
  - *Partnership* — two contexts succeed/fail together; joint language evolution.
  - *Customer–Supplier* — downstream influences upstream backlog with explicit contracts.
  - *Conformist* — downstream adopts upstream model (cheap, corrosive if upstream is legacy mess).
  - *ACL* — translate; prefer over conformist when upstream language is hostile.
  - *Published Language / OHS* — stable integration dialect (events/API schemas) owned deliberately.
  - *Shared Kernel* — tiny shared model; high discipline required or it becomes a dumping ground.
- **Team topology:** one team per context where possible; platform teams own shared ACLs to mainframes carefully with SLAs.
- **Evolutionary path:** glossary → context map → modular monolith packages → extract services only when autonomy evidence exists.

---

## Senior vs Lead framing

| Senior | Lead / Architect |
|--------|------------------|
| Models aggregates and VOs correctly | Sets context map and investment in core domain |
| Keeps app services transactional and thin on rules | Negotiates language with domain experts and PMs |
| Implements ACL mappers | Decides strangler sequence and published language |
| Uses repositories per aggregate | Prevents shared-kernel domain JAR sprawl |

---

## Why this matters in production

Wrong boundaries create chronic defects: double settlement, incorrect fee application, corrupted partner status mappings. Aggregates sized wrong create hot-row contention or broken invariants. Missing ACLs turn vendor outages and schema changes into domain outages. DDD done well reduces coordination cost between teams by making language and ownership explicit; DDD theater slows delivery without protecting invariants.

---

## Engineering tradeoffs

- Rich domain models improve invariant safety; increase modeling skill requirements and mapping away from JPA.
- Small aggregates improve concurrency; push more coordination into eventual consistency.
- Large aggregates simplify invariants; increase contention and load cost.
- Strict context isolation protects language; requires integration and duplication of reference data.
- ACLs protect the model; add latency and mapping maintenance.
- Tactical DDD everywhere raises ceremony; selective DDD on core maximizes ROI.

---

## Common anti-patterns

- Anemic domain model with all rules in application services.
- One “Company” bounded context for the entire enterprise.
- Aggregate = entire database schema reachable by FKs.
- Shared JPA entities as enterprise canonical model.
- Domain services doing I/O and transactions.
- Event storming stickies that never become enforced boundaries.
- Microservices split by technical layer (Controller service, Repository service).
- Ubiquitous language in a wiki only — code uses different names.

---

## Best practices

- Start with event storming / domain storytelling for core flows; name aggregates from invariants.
- Keep aggregates small; reference other roots by ID; use domain events for cross-aggregate work.
- Prefer immutable value objects for measurements, money, ranges, codes.
- Application services own transactions and orchestration; domain owns rules.
- Enforce context boundaries with modules and ArchUnit; no illegal imports of another context’s internals.
- Publish integration contracts (AsyncAPI/OpenAPI) as published language.
- Use outbox for domain events crossing process boundaries.
- Snapshot values that must not change historically (prices, tax rates) inside aggregates.
- Dual-write avoidance: ACL + explicit sync or events, not silent shared tables.
- Review naming in PRs as a first-class design concern.

---

## Architecture review checklist

- [ ] Is there a written context map for the product area?
- [ ] Does each context have an owned ubiquitous language glossary?
- [ ] Are aggregate boundaries justified by invariants (not tables)?
- [ ] Is “one aggregate per transaction” the default write path?
- [ ] Are cross-context integrations via API/events/ACL only?
- [ ] Do repositories speak domain questions for write models?
- [ ] Are value objects used for money/codes/ranges with validation?
- [ ] Is application vs domain service responsibility clear in code reviews?
- [ ] Are ACLs tested against real vendor fixtures and versioned?
- [ ] Is DDD depth proportional to domain complexity (core vs generic)?

---

## Interview Challenge

You join a Spring Boot lending platform. `Loan` JPA entity has 80 fields spanning origination, disbursement, repayment, collections, and document checklist. A single `LoanService` (3k LOC) updates any field in one `@Transactional` method. Collections vendors receive nightly CSV; their status codes are stored directly on `Loan`. Product wants “microservices for Collections next quarter.” What is your DDD-led plan?

### Suggested Answer

Treat current `Loan` as a **big ball of mud**, not an aggregate. Run collaborative modeling to split bounded contexts: Origination, Servicing/Repayment, Collections, Documents — each with its own language (`DelinquencyState` ≠ `OriginationStatus`). Introduce aggregates sized to invariants (e.g., `RepaymentAccount` for balances/schedules; `CollectionCase` for arrangements). Extract Collections behind an ACL that translates vendor codes into domain states; stop persisting raw vendor codes on a mega-entity. Short-term: modularize the monolith with package/module boundaries and ArchUnit before any network split. Application services per use case (`RecordRepayment`, `StartArrangement`) with one aggregate per transaction; domain events for cross-context notifications. Only extract Collections to a microservice when the context boundary is stable, data ownership is separable, and integration (events + ACL) is proven in-process first. ADR the rejection of “split the 80-field entity by deploying it twice.”

---

## Architecture Reflection Questions

1. Where does your ubiquitous language still disagree with domain experts — and what defect did that cause?
2. Which aggregate is too large, and what invariant actually justifies its current size?
3. What foreign model leaked into your core, and what would an ACL have prevented?
4. How do you decide core vs supporting domain investment for the next year?
5. When did you correctly refuse tactical DDD for a CRUD surface?

---

## Interview Confidence Checklist

- [ ] Explains bounded context with a real naming conflict example
- [ ] Sizes aggregates from invariants, not ER diagrams
- [ ] Distinguishes entity vs value object with equality semantics
- [ ] Separates domain service vs application service cleanly
- [ ] Describes repository as aggregate persistence, not table DAO soup
- [ ] Designs ACL with mapping ownership and tests
- [ ] Maps DDD to Spring/JPA without equating annotations to domain design
- [ ] Uses context mapping vocabulary in architecture conversations
- [ ] Scopes DDD to core domain; avoids ceremony on generic subs
- [ ] Lead framing: aligns contexts with team topology and integration strategy
