# Microservice Architecture

> Independently deployable units of business capability — valuable only when boundaries, data ownership, and failure design are real.

---

## Purpose

Reason about service boundaries, API gateways, discovery, data ownership, communication, consistency (including sagas and choreography/orchestration), and the engineering tradeoffs that dominate Lead/Architect interviews for enterprise Java platforms (Spring Boot, PostgreSQL/MySQL, Kafka, Redis, Kubernetes).

---

## Service Boundaries

### Explanation

A service boundary should align to a **bounded context** or business capability with clear data ownership, ubiquitous language, and team ownership — not to a technical layer or a single table. Good boundaries minimize chatty cross-service calls for core invariants and maximize independent deployability.

### How to find boundaries

- Domain language seams and invariant clusters (DDD).
- Change frequency and release cadence differences.
- Scale/SLO asymmetry (ingestion vs interactive).
- Regulatory or security blast-radius isolation.
- Team topology (Conway): one primarily accountable team per service.

### Production examples

- Split **Payments Authorization** from **Settlement** when latency SLOs and consistency needs diverge.
- Keep **Ledger** cohesive; do not split debit and credit posting into two services.
- Extract **Notifications** early — different criticality, async by nature.

### Tradeoffs

Fine-grained services increase autonomy and isolation but raise distributed transaction and latency costs. Coarse services reduce ops overhead but recreate delivery contention. Wrong cuts produce a **distributed monolith**: separate deploys with tight sync coupling and shared release pain.

### Anti-patterns

- Service-per-entity / service-per-table.
- “UserService,” “AdminService” technical splits.
- Boundaries drawn around frameworks (one service for “all Kafka consumers”).

### Interview angle

Describe extraction criteria you used (metrics, team, language conflict) and a split you refused.

---

## API Gateway

### Explanation

Edge entry for external clients: TLS, authn, routing, rate limits, request validation, protocol translation. May include BFF variants per channel. Must stay thin on domain logic. Common stacks: Spring Cloud Gateway, Kong, Envoy, cloud gateways + WAF.

### Responsibilities that belong at the gateway

- Authentication (token validation), coarse authorization, tenancy routing.
- Rate limiting, IP allowlists, WAF integration.
- Path-based routing, canary/header routing, request size limits.
- Correlation ID injection, deadline budgets at the edge.

### Responsibilities that do not belong

- Core business workflows and multi-step money movement.
- Long-lived orchestration across many domains (prefer application services/sagas).
- Fine-grained domain authorization alone without service-side checks.

### Tradeoffs

Central gateway: consistent policy, single choke point and config blast radius. Per-BFF: client-optimized aggregation, duplicated edge concerns. Heavy edge aggregation reduces client round-trips but couples availability to the gateway’s fan-out.

### Production examples

- JWT validated at gateway; services still enforce authz on resources.
- Partner API gateway with stricter quotas separate from mobile BFF.
- Bad: gateway synchronously calls 8 services to build a “dashboard” — becomes latency and failure amplifier.

### Anti-patterns

- Forgeable internal identity headers without mTLS/network policy.
- Business rules in gateway filters.
- No timeout/deadline propagation to origins.

### Interview angle

“Thin gateway, fat enough platform” — identity, abuse controls, routing; domain stays in services.

---

## Service Discovery

### Explanation

Maps logical service names to healthy instances under churn. On Kubernetes, CoreDNS + Endpoints/EndpointSlices + Services usually suffice. Client-side registries (Eureka/Consul) appear in hybrid/VM estates or migrations. Discovery without readiness is a lie.

### Production practices

- Readiness ≠ liveness: include dependency fitness appropriate to the traffic class.
- Connection draining (`preStop`), graceful shutdown, warm-up before Ready.
- Bound client-side caches; observe empty-instance and stale-route rates.
- Prefer mesh/LB server-side discovery for uniform policy when platform-owned.

### Tradeoffs

Client-side discovery: fewer hops, more stale-cache complexity. Server-side: simpler clients, platform dependency. Deep readiness prevents bad routes but couples deploy success to dependency health.

### Anti-patterns

- Hardcoded pod IPs / hostports in config.
- Health checks that only prove the JVM process is alive.
- Mixing multiple discovery sources of truth.

### Interview angle

Diagnose post-deploy 503s via draining, readiness, and cache TTLs — not “Kubernetes is broken.”

---

## Database per Service

### Explanation

Each service owns its persistence schema and access path. Other services integrate via APIs/events, not by reading foreign tables. Ownership includes migrations, backups, and consistency semantics for that data.

### Advantages

- Independent schema evolution and deploy.
- Encapsulation of invariants in one place.
- Failure and performance isolation (noisy neighbor queries stay local).
- Clearer team accountability for data quality.

### Disadvantages

- Cross-service queries become compositions (APIs, join via events, CQRS read models).
- Referential integrity across services is application-level, not FK-level.
- Higher ops cost (many databases/schemas to observe and backup).

### When to use

- True microservice intent with independent release and team ownership.
- Different data stores per capability (Redis sessions vs Postgres ledger vs OpenSearch).

### When NOT to use (yet)

- Modular monolith phase where separate schemas in one cluster may be enough.
- When the org cannot operate N databases safely.

### Tradeoffs

Autonomy and isolation vs distributed query complexity and dual-write hazards at boundaries. Pair with outbox/inbox and explicit read models for reporting.

### Anti-patterns

- “DB per service” claimed while ETL jobs join raw foreign schemas in place of APIs.
- Shared ORM models across services pointing at different DBs but same types — false safety.

---

## Shared Database

### Explanation

Multiple services read/write the same database (often the same schemas/tables). Common transitional state; dangerous as a steady architecture for microservices.

### Why it appears

- Faster initial split of apps without data migration.
- Reporting needs.
- Fear of distributed transactions.

### Advantages (limited)

- Easy joins and ACID across “services.”
- Lower short-term migration cost.

### Disadvantages

- Hidden coupling: schema change breaks many deployables.
- Ownership ambiguity; conflicting migrations.
- Independent scale/deploy becomes fiction.
- Encourages bypassing service APIs.

### When to use

- Explicit modular monolith (single app, shared DB is normal).
- Short-lived strangler phase with clear end state and ACL around legacy DB.
- Read-only replicas for reporting owned by a analytics path — still careful with contracts.

### When NOT to use

- As the long-term model for independently deployed microservices.
- When two teams need divergent schema evolution on the same tables.

### Tradeoffs

Transactional convenience vs coupling. In interviews, calling shared DB “pragmatic microservices” is a red flag unless framed as temporary with a migration plan.

### Anti-patterns

- Multiple Spring Boot apps, one Postgres, cross-service JOINs in production forever.
- Shared DB plus sync call mesh — worst of both worlds.

---

## Communication Patterns

### Overview

| Pattern | Typical use | Failure mode to design |
|---------|-------------|------------------------|
| Sync HTTP/gRPC | Query/command needing immediate response | Timeouts, retries, cascading failure |
| Async messaging | Notifications, integration, decoupling | Duplication, ordering, lag |
| Request/reply over messaging | Async command with correlation | Orphan replies, timeout UX |
| Batch/file | Partner settlements, legacy | Late data, reconciliation |
| Shared DB (avoid) | Legacy | Schema coupling |

### Design rules

- Prefer **sync within a context** for hard invariants; **async between contexts** for side effects.
- Idempotency keys on commands that may retry.
- Explicit payloads: avoid chatty multi-round conversations for one user action when an aggregate command suffices.
- Version contracts (OpenAPI/AsyncAPI); consumer-driven contract tests where multiple consumers exist.

### Tradeoffs

Sync: simpler mental model, tighter availability coupling. Async: resilience and scale, eventual consistency and ops complexity. Hybrid is normal: sync command to owning service, async fan-out afterward (outbox).

---

## Synchronous vs Asynchronous

### Synchronous

Caller waits for response. Spring MVC/WebClient/gRPC. Use for reads/writes where UX or invariant needs immediate confirmation from the owner service.

**Risks:** thread/pool exhaustion, retry storms, latency tail amplification through call chains, cascading outages without bulkheads/circuit breakers.

**Controls:** timeouts, budgets/deadlines, bulkheads, circuit breakers, rate limits, bounded retries with jitter, idempotency.

### Asynchronous

Caller publishes or enqueues; work completes later. Kafka/SQS/Rabbit. Use for decoupled integration, spike absorption, multi-consumer fan-out.

**Risks:** dual-write without outbox, poison messages, consumer lag SLO breaches, unclear user-visible state.

**Controls:** transactional outbox, inbox/dedup, DLQ, lag monitors, schema compatibility, replay runbooks, user-facing status resources.

### Decision heuristic

- User must know outcome now from the **owner** of the data → sync to that owner.
- Other contexts need to react → async events after commit.
- Long-running multi-service workflow → saga (below), not a blocking HTTP chain of five services.

### Interview angle

Never say “we are async so we are decoupled” without naming consistency UX and failure recovery.

---

## Distributed Transactions

### Explanation

Classic 2PC across services/databases is usually rejected in microservice estates: blocking, operationally brittle, and poorly supported across heterogeneous stores. Microservices accept **no global ACID** and redesign workflows around local transactions + coordination patterns.

### What you still need

- Local ACID inside a service boundary (Postgres transactions + aggregate invariants).
- Explicit coordination for multi-service business processes.
- Reconciliation for inevitable drift.

### Anti-patterns

- XA/2PC across many services as default.
- Ignoring partial failure (“hope all Feign calls succeed”).
- Retries without idempotency creating double posts.

### Tradeoffs

Giving up global ACID buys autonomy and polyglot persistence; costs complexity in workflow design and support tooling (repair jobs, admin compensations).

---

## Saga Pattern

### Explanation

A saga is a sequence of **local transactions** where each step publishes an outcome that triggers the next, with **compensating actions** on failure. Saga is the primary substitute for distributed transactions in business workflows (order → payment → reservation → shipment).

### Design elements

- Steps with clear owners and local commits.
- Compensations that are safe to retry (ideally idempotent).
- State tracking (saga log / process state) for recovery.
- Timeouts and human intervention paths for non-compensable steps.
- Observability: saga ID correlated across services.

### Production examples

- Travel booking: reserve inventory → charge → confirm; compensate by release + refund.
- Lending: approve → disburse → register obligation; compensate by reversing provisional states.
- Telco: reserve MSISDN → provision HLR → activate billing; compensate with deprovision sequence.

### Tradeoffs

Sagas enable cross-service workflows without 2PC; they demand careful compensation design and accept temporary inconsistency. Some steps are hard to compensate (irreversible external effects) — require pending states, deferred side effects, or manual ops playbooks.

### Anti-patterns

- Saga that is actually a synchronous Feign chain with no compensations.
- Non-idempotent compensations.
- Giant saga spanning unrelated contexts because boundaries were wrong (fix boundaries first).

---

## Event Choreography

### Explanation

Each service reacts to domain events and emits new events; **no central coordinator**. Workflow emerges from the event web. Example: `OrderPlaced` → Payment service charges → `PaymentCaptured` → Inventory ships → `OrderCompleted`.

### Advantages

- Loose coupling; easy to add consumers.
- No single orchestrator service to scale/own.
- Natural fit for simple happy paths with clear event chains.

### Disadvantages

- Flow is implicit — hard to see end-to-end in code.
- Cyclic events and unclear ownership of “who fixes stuck flows.”
- Timeout/compensation logic scattered.
- Debugging requires strong tracing and documentation of the choreography graph.

### When to use

- Simple, stable workflows with few steps.
- Fan-out side effects (index, notify, analytics) off a primary event.
- Teams experienced with event ownership and lag SLOs.

### When NOT to use

- Complex branching compensations with many failure modes.
- Regulatory need for an explicit, auditable process definition in one place.
- When stuck-state recovery is already painful.

### Tradeoffs

Autonomy and extensibility vs visibility and control. Choreography complexity grows non-linearly with steps.

---

## Event Orchestration

### Explanation

A **orchestrator / process manager** directs the workflow: tells participants what to do (commands), listens for replies/events, decides next steps and compensations. Can be implemented as a dedicated service, state machine (e.g., Temporal, Camunda), or application module.

### Advantages

- Explicit flow — easier to reason, test, and audit.
- Centralized timeouts, retries, compensations.
- Clear ownership of stuck saga recovery.

### Disadvantages

- Orchestrator can become a god service / hotspot.
- Risk of over-centralizing domain logic that belongs in participants.
- Extra moving part to deploy and HA.

### When to use

- Complex sagas with branching compensations.
- Business needs visible process state (ops consoles, compliance).
- Multi-step workflows spanning many services with SLAs.

### When NOT to use

- Trivial one-event fan-out (choreography/notifications suffice).
- When orchestration duplicates logic already correctly owned by a single aggregate — maybe your split was wrong.

### Tradeoffs

Visibility and control vs coupling to the orchestrator’s availability and the temptation to anemic participants. Prefer orchestration for **process**, keep **invariants** in owning services.

### Choreography vs orchestration — interview summary

| | Choreography | Orchestration |
|--|--------------|---------------|
| Flow location | Distributed in consumers | Central process manager |
| Visibility | Low without tooling | High |
| Coupling | Via event contracts | Via commands + events to orchestrator |
| Best for | Simple/fan-out | Complex sagas |
| Failure handling | Scattered | Centralized |

Hybrid is common: orchestrate the money path; choreograph notifications and projections.

---

## End-to-end production skeleton (Java/Spring)

1. Client → API Gateway (auth, rate limit) → Service A (command).
2. Service A: load aggregate, domain rules, persist + **outbox** in one DB transaction.
3. Publisher relays outbox to Kafka; consumers in Service B/C use **inbox/idempotency**.
4. Sync queries go to owning service or to a **read model** fed by events.
5. Cross-service workflow: orchestrated saga or choreography with compensations.
6. Observability: trace IDs across HTTP and messaging; lag/DLQ dashboards; saga state admin tools.

---

## Senior vs Lead framing

| Senior | Lead / Architect |
|--------|------------------|
| Implements idempotent consumers, timeouts, outbox | Sets boundary rules and extraction criteria |
| Chooses sync vs async for a feature | Owns org consistency patterns (saga standard) |
| Operates a service well | Prevents distributed monolith via reviews/ADRs |
| Uses gateway correctly | Defines gateway vs BFF vs service ownership |

---

## Why this matters in production

Microservice failures are usually **integration failures**: shared DBs, unbounded sync chains, non-idempotent retries, undrainable consumers, sagas without compensations. Autonomy promised on slides becomes coupled outages on weekends. Lead engineers are hired to make boundaries and consistency models match business risk — especially for money, identity, and provisioning flows.

---

## Engineering tradeoffs

- More services: more deploy independence, more partial-failure surface.
- DB per service: better encapsulation, harder cross-entity reporting.
- Shared DB: easier joins, destroys independence.
- Sync calls: immediate consistency UX, cascading latency/availability risk.
- Async events: decoupling and scale, eventual consistency and reconciliation cost.
- Choreography: flexible extension, opaque control flow.
- Orchestration: clear process, central dependency.
- Saga compensations: workable distributed workflow, complex edge cases and support tools.
- Gateway aggregation: fewer client calls, fat edge and coupled backends.

---

## Common anti-patterns

- Distributed monolith (sync mesh + shared DB + lockstep releases).
- Chatty services requiring 10 hops per user click.
- Gateway as enterprise ESB/orchestrator of record.
- Dual writes (DB + Kafka) without outbox.
- “At-least-once” consumers without idempotency.
- 2PC nostalgia as architecture strategy.
- Saga without compensations or without timeout/repair paths.
- Premature split before platform readiness (tracing, CI, on-call, contracts).
- Shared “common-domain” JAR coupling all services’ releases.
- Using Redis as a shared integration database between services.

---

## Best practices

- Split on bounded contexts and team ownership; prove in a modular monolith first when possible.
- Database per service (or per module schema with enforcement) as the autonomy litmus test.
- Outbox + idempotent consumers for reliable integration.
- Timeouts, bulkheads, and circuit breakers on all sync calls; never unlimited retries.
- Prefer orchestration for complex sagas; choreography for fan-out side effects.
- Version APIs/events; contract-test critical consumers.
- Make readiness/draining correct; discover via platform DNS on Kubernetes.
- Keep gateway thin; enforce authz in services; mTLS/network policy east-west.
- Build reconciliation jobs and admin repair tools for saga/event drift.
- Document consistency UX with product (what users see when state is pending).
- Measure: sync depth, consumer lag, change failure rate, MTTR, cross-service PR coupling.

---

## Architecture review checklist

- [ ] Is each service’s business capability and data ownership one sentence clear?
- [ ] Can services deploy independently without coordinated schema freezes?
- [ ] Is shared database usage absent — or explicitly transitional with an end date?
- [ ] Are sync call chains depth-bounded with budgets and bulkheads?
- [ ] Are async paths covered by outbox, idempotency, DLQ, and lag SLOs?
- [ ] For multi-service workflows: saga type chosen (choreography vs orchestration) with compensations?
- [ ] Is the API gateway free of core domain orchestration?
- [ ] Do discovery/readiness practices survive rolling deploys?
- [ ] Are contracts versioned and tested?
- [ ] Are operability artifacts present (runbooks, saga repair, correlation dashboards)?
- [ ] Would collapsing two services reduce distributed pain without hurting team cadence?

---

## Interview Challenge

An e-commerce platform has Checkout, Payment, Inventory, and Shipping as separate Spring Boot services on EKS, each with its own PostgreSQL. Checkout calls Payment and Inventory synchronously inside one user request, then publishes `OrderPlaced` to Kafka for Shipping. Under load, Checkout p99 explodes; occasional double charges appear when Payment times out and the user retries; Inventory reserved units sometimes leak when Payment fails after reserve. Leadership asks for “a saga.” Design the target workflow and the interim mitigations.

### Suggested Answer

**Interim mitigations:** idempotency keys on Payment charge and Checkout create; tighten timeouts; stop unbounded retries from the client; add inventory reservation TTLs / release jobs; circuit-break Payment to fail fast; improve readiness so bad pods do not take traffic.

**Target workflow:** Treat place-order as a **saga** owned by an orchestrator (Checkout process manager or dedicated OrderWorkflow): (1) Checkout creates `Order` in `PENDING` locally; (2) command Inventory `Reserve` (local TX) with reservation ID; (3) command Payment `Charge` with idempotency key; (4) on payment success, mark `PAID` and emit `OrderPaid` for Shipping; (5) on payment fail, compensate Inventory `Release`. Prefer **orchestration** here because compensations and timeouts are non-trivial; use choreography only for Shipping/notifications after `OrderPaid`. Persist saga state; make every step idempotent; use outbox for emitted events. Do not wrap Payment+Inventory in 2PC. Expose order status to UX (`PENDING_PAYMENT`, `PAID`, `FAILED`) so eventual consistency is visible. Add reconciliation: reservations without payment after T minutes auto-release; payments without order transition alert. ADR the rejection of sync multi-service transactions in the request thread for the happy path — either shorten the sync path to one owner or move to explicit async saga with status polling/websocket.

---

## Architecture Reflection Questions

1. Which of your services would hurt least if merged tomorrow — and what does that say about the original boundary?
2. Where do you still have shared-database coupling disguised as microservices?
3. What is your worst sync call chain, and what SLO does it threaten?
4. For your hardest cross-service workflow, is choreography or orchestration the better fit — and why?
5. What repair tooling exists when a saga stops mid-compensation?

---

## Interview Confidence Checklist

- [ ] Defines service boundaries via capability/context, not tables
- [ ] Explains API gateway responsibilities and anti-responsibilities
- [ ] Contrasts Kubernetes discovery vs legacy registries with readiness nuance
- [ ] Defends database-per-service and diagnoses shared-DB distributed monoliths
- [ ] Chooses sync vs async with UX and failure semantics
- [ ] Rejects 2PC as default; designs saga compensations
- [ ] Compares event choreography vs orchestration with concrete fit
- [ ] Includes outbox/idempotency/DLQ in async designs
- [ ] Ties microservice cost to platform maturity and team topology
- [ ] Lead framing: extraction criteria, ADRs, and consistency standards for the org
