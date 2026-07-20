# Microservices

> Decomposition, communication, and resilience under partial failure.

## Service Discovery

### Explanation

Service discovery maps logical service names to healthy instances as topology changes under deploy, scale, and failure. Resolution may use DNS, a registry (Eureka, Consul), Kubernetes Endpoints/DNS, or a service mesh control plane. On Kubernetes, ClusterIP + kube-proxy/IPVS or CoreDNS is usually sufficient; a separate registry mainly helps hybrid fleets or multi-cluster overlays.

Discovery without health is fiction. Liveness ≠ readiness: traffic must reach only ready endpoints. Clients and load balancers cache resolutions; stale caches after drains are a first-class failure mode. Spring Cloud LoadBalancer / mesh sidecars only work if readiness reflects real dependency fitness for the request class being served.

### Why interviewers ask it

- Tests dynamic topology literacy vs hardcoded URLs.
- Reveals whether probe design and stale-cache behavior are explicit.
- Distinguishes platform-native thinking from cargo-cult Netflix stacks.

### Production examples

- Pods pass liveness but fail readiness; traffic still hits them because `/actuator/health` ignores DB/cache.
- Client-side Eureka cache serves drained instances until TTL expires after a region evacuate.
- Empty instance lists after a namespace misconfig; callers “fail open” into a fallback that writes divergent state.

### Common mistakes

- Using discovery as a substitute for API contracts and versioning.
- Treating DNS TTLs as instant failover.
- Health checks that only prove the JVM process is up.
- Mixing client-side discovery and mesh routing without one source of truth.

### Senior Engineer discussion

Prefer platform DNS/Endpoints on Kubernetes. Keep client-side discovery only when you own non-orchestrated fleets. Make readiness dependency-aware and fail closed for write paths. Bound cache TTLs, observe empty-instance rates, and load-test rolling deploys with connection draining and warm-up gates.

### Lead Engineer discussion

Standardize one discovery strategy so teams do not invent registries. Define SLOs for endpoint freshness and deploy-time error budgets. Push mesh/gateway ownership to platform when mTLS, retries, and auth are org-wide concerns—product teams should not each own edge policy.

### Tradeoffs

- Client-side discovery: lower hop latency, more client complexity and stale caches.
- Server-side (LB/mesh): simpler clients, extra hop and platform dependency.
- Aggressive unhealthy removal: faster isolation, more flapping under transient blips.
- Deep readiness (include DB): fewer bad routes, coupling deploys to dependency health.

### Interview Challenge

1. A Spring Boot service on EKS intermittently 503s for 30–90s after deploys. Discovery shows instances; readiness is green. Diagnose.
2. When would you still run Eureka/Consul on Kubernetes?

### Suggested Answer

1. Check connection draining, EndpointSlice sync lag, Spring warm-up after ready, and whether readiness excludes critical deps that fail under first traffic. Correlate with in-flight requests and LB target deregistration. Fix with `preStop`, longer readiness/startup probes, warm caches before ready, and canaries measuring error rate during rollouts.
2. Hybrid/multi-cluster without shared kube DNS, migration off VMs, or non-K8s consumers that cannot use ClusterIP. Otherwise prefer native Endpoints/DNS/mesh and delete the extra control plane.

## API Gateway

### Explanation

An API gateway is the edge policy and routing plane: TLS termination, authn/authz, rate limits, request shaping, protocol translation, and routing. It is not a dumping ground for unbounded business logic. Common choices: Spring Cloud Gateway, Kong, Envoy, AWS API Gateway/ALB+WAF. BFF gateways per client type are valid when aggregation and auth differ by channel.

Edge policy must compose with service-level authorization. A gateway that validates JWT but then forwards forgeable `X-User-Id` into a flat trust network recreates the vulnerability it claimed to solve. Timeouts and deadline propagation belong at the edge and in every hop.

### Why interviewers ask it

- Probes edge vs domain ownership boundaries.
- Tests perimeter abuse controls and identity handoff.
- Separates “smart gateway” fashion from disciplined platform design.

### Production examples

- JWT at gateway plus fine-grained authz in services; opaque tokens to every backend expand blast radius when stolen.
- A “smart” gateway aggregates five services synchronously and becomes the latency/availability bottleneck.
- WAF/rate-limit rules stop credential stuffing before identity services melt.

### Common mistakes

- Embedding domain workflows and long-lived orchestration in the gateway.
- Dual auth: gateway trusts headers any internal caller can forge.
- No timeout/budget propagation to backends.
- Using the gateway as the only observability choke point.

### Senior Engineer discussion

Keep the gateway thin: identity, routing, quotas, schema validation, coarse authz. Propagate correlation IDs and deadlines. Prefer mTLS/network policy for east-west. Version external APIs independently of internal service versions. Instrument edge 4xx/5xx separately from origin errors.

### Lead Engineer discussion

Decide gateway ownership (platform vs product) and change-control for edge policies. Establish contract testing for public APIs and deprecation policy. Measure the gateway as a product: p99, error budget, config rollback time, and blast radius of a bad route push.

### Tradeoffs

- Central gateway: consistent policy, single choke point and release coupling.
- Per-BFF: client-optimized APIs, duplicated edge concerns.
- Heavy edge aggregation: fewer client round-trips, worse failure coupling.
- WAF at edge: abuse resistance, false positives and rule-ops cost.

### Interview Challenge

1. Design the edge for mobile BFF, partner OpenAPI, and internal admin APIs.
2. Where should JWT validation live—gateway, service, or both?

### Suggested Answer

1. Separate ingress classes: partners with mTLS/OAuth client credentials and strict quotas; mobile BFF with user tokens and device risk signals; admin behind VPN/SSO and step-up. Shared platform for TLS/WAF/rate limits; BFFs own aggregation. Never share the public edge with admin. Isolate deployments and SLOs per surface.
2. Gateway validates signature/`aud`/`exp` for early reject and threat reduction; services still enforce object-level authz. Do not skip service checks. Prefer forwarding the token or a sealed internal principal—not raw forgeable headers.

## Circuit Breaker

### Explanation

A circuit breaker stops calling a failing dependency after an error/slow-call threshold, failing fast or serving fallback, then probing recovery (closed → open → half-open). Resilience4j and mesh outbound policies are common. Breakers protect callers and shared pools; they do not heal the dependency.

Composition matters: retries behind an open-breaker window, or half-open stampede, can re-collapse a recovering service. Scope breakers per dependency and operation class; a single global breaker mixes unrelated criticality and hides useful signal.

### Why interviewers ask it

- Checks failure-mode design under partial outage.
- Distinguishes retries (amplification) from isolation (containment).
- Expects correct signals: failure rate, slow calls, concurrent calls—not vibes.

### Production examples

- DB latency exhausts Hikari + request threads; repository breaker sheds load and preserves read-only health.
- Half-open allows a herd of probes; recovering inventory dies again.
- Fallback returns cached prices; long outage creates financial disputes on stale quotes.

### Common mistakes

- Breakers on non-idempotent writes without idempotency keys.
- Thresholds tuned on averages instead of SLO percentiles.
- Fallbacks that write divergent business state.
- One breaker for all tenants/operations.

### Senior Engineer discussion

Combine timeouts, bulkheads, bounded jittered retries, and breakers. Emit state-change metrics and alerts. Prefer fail-fast on non-critical paths; on critical paths define explicit degraded modes and runbooks. Prove policies with latency-injection tests, not unit mocks alone.

### Lead Engineer discussion

Codify resilience defaults in shared Spring starters so every team does not invent thresholds. Require failure tests in CI for Tier-1 integrations. Treat sustained open circuits as dependency SLO incidents—not silent success via fallback.

### Tradeoffs

- Aggressive opening: protects caller, more false shedding.
- Soft fallbacks: better UX, hidden inconsistency risk.
- Mesh policies: centralized control, less app-specific semantics.
- Per-operation breakers: precision, more config surface.

### Interview Challenge

1. Checkout calls inventory, pricing, and payments. Inventory is slow. Where do breakers go?
2. Why can a fallback make an outage worse?

### Suggested Answer

1. Timeouts + breakers on inventory/pricing with defined degrade (deny purchase vs delayed reservation). Payments: no optimistic “success” fallback; fail closed with idempotent retry later. Bulkhead pools per dependency; one request deadline from the API. Chaos-test inventory latency without exhausting checkout.
2. Fallback that confirms orders, serves wrong prices, or writes alternate stores creates split-brain that outlasts the outage. Prefer explicit degradation and reconciliation over silent lies.

## Distributed Transactions

### Explanation

ACID across services is rarely available without a coordinator. 2PC/XA couples availability to every participant and is operationally hostile in microservices. Prefer a single-writer transaction inside one service boundary, then asynchronous consistency (outbox, saga, reconciliation). In interviews, “distributed transaction” usually means: how do you avoid needing one?

Dual-writes (DB + broker, DB + search) without transactional outbox/CDC are a leading cause of silent drift. Kafka “exactly once” does not equal business exactly-once effects—handlers still need idempotency and unique constraints.

### Why interviewers ask it

- Exposes whether candidates still reach for XA by default.
- Tests data-ownership clarity and consistency models.
- Links dual-write failure to durable design.

### Production examples

- Order commits DB then dies before Kafka publish → lost event; fixed with transactional outbox.
- XA across order and payment DBs deadlocks under partition; replaced by saga + idempotent payment API.
- Dual write to DB and Elasticsearch drifts; search rebuilt from the event stream.

### Common mistakes

- Dual-write without outbox or CDC.
- Assuming broker EOS equals business exactly-once.
- Chatty sync chains pretending to be a distributed commit.
- Ignoring reconciliation jobs for inevitable drift.

### Senior Engineer discussion

Keep a strong transaction around one aggregate/database. Publish via outbox/CDC. Make consumers idempotent. Classify invariants as strong vs eventually consistent. For money, prefer ledger-style append-only records with compensating entries over distributed locks.

### Lead Engineer discussion

Enforce bounded contexts and data ownership in design reviews. Ban cross-service joins and shared DBs as “temporary.” Fund reconciliation and audit tooling as product requirements—drift detection is not optional for financial domains.

### Tradeoffs

- 2PC: strong consistency, poor availability and ops complexity.
- Local TX + async: availability and scale, temporary inconsistency windows.
- Shared DB: easy joins, destroys independent deployability.
- Reconcile jobs: correctness safety net, operational and UX lag.

### Interview Challenge

1. Place order, reserve inventory, charge payment—three services. How do you “commit”?
2. When is XA still defensible?

### Suggested Answer

1. No 2PC. Create order `Pending`, emit outbox events, orchestrate/choreograph reservation and payment with idempotency keys. Success → `Confirmed`; failure → compensate inventory → `Cancelled`. Persist a state machine; sweeper resolves stuck `Pending` by querying payment status. Customer-visible status matches the consistency model.
2. Rare: tightly coupled legacy modules co-located with a mature transaction manager and no independent deploy need. Even then, prefer extracting a single writer. Do not choose XA for new microservice boundaries.

## Saga Pattern

### Explanation

A saga is a sequence of local transactions with compensations (or retriable pivots) that advances a workflow across services. Orchestration uses a central coordinator; choreography uses event reactions. Both need idempotency, timeouts, visible state, and dead-letter handling. Compensation is semantic undo—not always a DB rollback (refunds, vouchers, ops queues).

Irreversible steps (physical shipment, irreversible notifications) force design of pivots, human intervention states, and customer communication—not infinite automated undo.

### Why interviewers ask it

- Core microservice consistency topic at senior+.
- Distinguishes event buzzwords from recoverable workflows.
- Probes compensation design and partial-failure UX.

### Production examples

- Travel booking: reserve flight → hotel → charge; compensate hotel if charge fails.
- Orchestrator times out on payment callback; sweeper queries payment status instead of blind compensate.
- Choreographed saga drops an event; workflow stalls without timeout + status query path.

### Common mistakes

- Non-idempotent or impossible compensations.
- No saga state store; debugging via log archaeology.
- Infinite retries without a terminal business state.
- Blocking the user thread across many sync saga steps.

### Senior Engineer discussion

Model explicit states and transitions. Prefer orchestration for complex branching/SLA-sensitive flows; choreography for simple linear reactions. Pair events with query APIs. Design compensations first for irreversible steps. Observe stuck-saga age as a first-class SLO signal.

### Lead Engineer discussion

Standardize workflow tooling (Temporal/Camunda/custom) to avoid N snowflake sagas. Define orchestrator ownership and step-contract versioning. Require dashboards for stuck sagas by age and reason; fund ops queues for `NeedsIntervention`.

### Tradeoffs

- Orchestration: control/visibility, coordinator hotspot and coupling.
- Choreography: loose coupling, harder end-to-end visibility.
- Automated compensate vs human ops: speed vs safety for high-value actions.
- Long-running sagas: flexibility, more state and timeout complexity.

### Interview Challenge

1. Payment succeeds; inventory compensation fails repeatedly. What do you do?
2. Orchestration or choreography for loan disbursement with compliance holds?

### Suggested Answer

1. Stop blind retry amplification. Park saga in `NeedsIntervention` with paging. Inventory exposes reconcile API; run repair. Customer comms: order held, not silently charged without fulfillment. Make compensation prioritized, idempotent, capacity-tested; add invariant monitors (paid ∩ unreserved).
2. Prefer orchestration: explicit holds, audits, timeouts, and human gates. Choreography obscures compliance sequencing and evidence. Persist every decision with reason codes.

## Service Communication

### Explanation

Sync (HTTP/gRPC): request/response, tight latency coupling, easier per-call debugging. Async (messaging/events): temporal decoupling, buffering, fan-out, harder end-to-end reasoning. Choose by consistency needs, latency budget, and failure isolation—not fashion. Contracts: OpenAPI/Protobuf, schema registry, consumer-driven contracts.

Idempotency keys, correlation IDs, and deadline propagation are mandatory either way. Backpressure (bounded queues, 429, gRPC resource exhausted) prevents polite services from being murdered by eager peers.

### Why interviewers ask it

- Everyday microservice design decision.
- Tests contract maturity beyond REST-vs-Kafka preference.
- Links versioning, backpressure, and team autonomy.

### Production examples

- gRPC for internal low-latency fan-in; REST/JSON for public/partner APIs.
- Kafka for order lifecycle fan-out; sync only for payment authorization on the user path.
- Contract tests fail in CI when a field is removed—protects mobile release trains.

### Common mistakes

- Chatty sync meshes that amplify tail latency.
- Events used as RPC without clear ownership.
- No schema evolution rules.
- Ignoring poison messages and consumer lag as SLOs.

### Senior Engineer discussion

Default to sync for user-waiting authorization with strict timeouts; async for propagation. Prefer additive schema changes. Apply backpressure and load shedding. Document delivery guarantees and consumer responsibilities. Trace across both HTTP and messaging.

### Lead Engineer discussion

Publish communication standards: when to use events, sync, and workflow engines. Own schema registry and breaking-change policy. Align team boundaries with communication patterns to minimize cross-team sync chains that serialize delivery.

### Tradeoffs

- Sync: simpler mental model, failure coupling and cascading latency.
- Async: resilience and scale, eventual consistency and ops complexity.
- gRPC: efficiency and strict contracts, harder browser/partner interop.
- Schema rigidity: safety, slower exploratory change.

### Interview Challenge

1. Notification after order confirmation—sync or async?
2. How do you evolve an event payload without breaking five consumers?

### Suggested Answer

1. Async from order-confirmed outbox event. Notification failure must not roll back payment. Idempotent on `orderId`. Separate SLO for notification delay; page on lag/errors, not order API. Provide user “resend” via idempotent command.
2. Additive fields; schema registry compatibility mode; consumer-driven contracts; dual-publish or versioned topics only when necessary; deprecate with metrics on old field usage before removal.

## Resilience Patterns

### Explanation

Resilience is a stack: timeouts, retries with jitter, bulkheads, circuit breakers, rate limiting, load shedding, caching, idempotency, and graceful degradation. Wrong composition (synchronized retries, stacked timeouts longer than the client budget) worsens outages. Deadline propagation beats independent per-hop timeouts that sum past user patience.

Chaos engineering and game days validate assumptions. Library defaults are not a strategy—policies must match idempotency and business criticality.

### Why interviewers ask it

- Holistic production-readiness signal for lead candidates.
- Separates name-dropping from coherent failure design.
- Connects app behavior to capacity and SLO economics.

### Production examples

- Retry-After + jitter on 503; no retry on 400/401.
- Shed recommendations under CPU/queue pressure; keep checkout.
- Cache-aside with soft/hard TTL and single-flight prevents stampedes.

### Common mistakes

- Unlimited retries on write paths.
- One shared thread/connection pool for all downstreams.
- Cascading timeouts longer than client patience.
- Degraded modes that violate compliance/audit rules.

### Senior Engineer discussion

Design for the dependency’s worst day: queue depth, pool saturation, recovery herd. Every remote call declares timeout, retry policy, and idempotency. Prefer adaptive concurrency limits when traffic is bursty. Instrument attempt vs logical-operation latency separately.

### Lead Engineer discussion

Bake resilience into the golden path (starters, mesh defaults). Require failure-mode sections in ADRs. Track MTTR and error-budget burn from dependency incidents; fund chaos for Tier-1 journeys. Cap organizational retry amplification via shared policy.

### Tradeoffs

- Fail fast: preserves capacity, worse immediate UX.
- Retry: hides blips, risks amplification.
- Deep degradation: availability, potential business/compliance cost.
- Adaptive limits: better under shift, harder to reason about statically.

### Interview Challenge

1. All services use 3 retries × 1s backoff. A regional blip becomes a site outage. Why and how fix?
2. How do you set timeouts across gateway → BFF → order → payment?

### Suggested Answer

1. Synchronized retries create a thundering herd; traffic multiplies into recovering deps. Add jitter, cap attempts, retry only idempotent/safe ops, breakers/bulkheads, edge shedding. Align retries to remaining deadline. Load-test dependency 500/latency recovery.
2. Propagate one absolute deadline (or remaining budget). Each hop uses a fraction, never independent full budgets that sum past the client. Gateway enforces the outer limit; inner calls check remaining time before retry.
