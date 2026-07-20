# System Design Fundamentals

> Requirements, quality attributes, and consistency models that frame every senior design loop.

## Functional vs Non-functional Requirements

### Explanation

Functional requirements define what the system must do: create payment, settle ledger, provision SIM, emit webhook. Non-functional requirements (NFRs) define how well it must do those things under load, failure, and change: latency budgets, availability targets, durability, auditability, cost ceilings, and operability. Interviews and design reviews fail when candidates invent APIs without binding NFRs to measurable SLOs and failure behavior.

NFRs are not soft preferences. A “fast” payments API without p99 latency, timeout budgets, and backpressure policy is incomplete. An “available” core banking read path without RPO/RTO, failover semantics, and degraded-mode contracts invites silent data loss. Senior design starts by separating must-have behaviors from quality attributes, then allocating those attributes across services, data stores, and edge layers.

Cross-cutting NFRs (security, compliance, observability, multi-tenancy) often dominate architecture more than feature lists. Traceability from requirement → SLO → test → runbook is how you prove a design is shippable, not just drawable.

### Why interviewers ask it

- Separates feature storytelling from production-grade system thinking.
- Reveals whether candidates can turn vague asks into measurable constraints.
- Tests prioritization under conflicting stakeholder pressure (product vs risk vs cost).
- Surfaces ownership of degradation and “what breaks first” under stress.

### Production examples

- Card authorization: functional = approve/decline; NFRs = p99 < 80ms at edge, 99.99% availability, PCI scope isolation, immutable audit trail.
- Telecom provisioning: functional = activate MSISDN; NFRs = exactly-once effect across BSS/OSS, retry-safe callbacks, 15-minute RTO for control plane.
- Loan origination: functional = decision + disbursement; NFRs = regulatory retention, explainability of decision path, dual-control for limit changes.

### Common mistakes

- Treating NFRs as “later optimization” instead of design inputs.
- Writing SLAs without error budgets, measurement method, or exclusion clauses.
- Mixing product wishlists with architectural constraints in one undifferentiated list.
- Ignoring operability: deployability, debuggability, and on-call cognitive load.

### Senior Engineer discussion

Own the translation from stakeholder language to testable constraints. For each critical path, write latency class, consistency class, durability class, and failure mode. Push back when product asks for “real-time everywhere” without naming the consistency or cost implications. Instrument the NFR early: if you cannot measure it in staging under realistic load, you do not have a requirement—you have a hope.

### Lead Engineer discussion

Facilitate requirement triage across teams: which NFRs are platform standards vs product-specific. Encode defaults in ADRs and templates (timeouts, idempotency, auth, logging). Protect the org from unbounded scope by making tradeoffs explicit in roadmaps—e.g., “99.99% for money movement, 99.9% for reporting.” Align SRE, security, and product on error budgets so feature velocity does not silently burn reliability.

### Tradeoffs

- Strict NFRs increase build and run cost; weak NFRs shift cost into incidents and rework.
- Over-specifying every attribute creates analysis paralysis; under-specifying creates thrash in production.
- Shared platform NFRs reduce variance but constrain product teams that need exceptions.
- Measuring more dimensions improves control but increases observability cost and alert noise.

### Interview Challenge

1. Product wants “instant balance updates everywhere.” Translate into functional + non-functional requirements with measurable SLOs.
2. Two stakeholders disagree: marketing wants 100% feature completeness on day one; risk wants audit-complete money movement. How do you structure the requirements conversation?
3. How do you verify an NFR before launch when production traffic does not yet exist?

### Suggested Answer

1. Functional: post transaction, read balance, reconcile. NFRs: write path linearizable within account shard; read-your-writes for the initiating client within 1s; eventual consistency for cross-channel displays with max staleness SLO (e.g., 5s p99); durability = commit to quorum before ACK; define degraded UI when replica lag exceeds budget.
2. Split MVP into risk-critical path (ledger + audit) vs growth features. Bind money movement to hard NFRs and compliance gates; time-box marketing features behind flags. Document acceptance criteria and launch blockers separately so disagreement becomes sequenced delivery, not binary deadlock.
3. Use synthetic load with production-like data skew, chaos/failover drills, contract tests for latency budgets, and canary with mirrored traffic or shadow reads. Define go/live gates on p99, error rate, and recovery time—not on “feels fine.”


## Scalability

### Explanation

Scalability is the ability to increase capacity by adding resources while preserving correctness and acceptable latency. Vertical scaling buys simplicity until hardware, blast radius, or cost ceilings intervene. Horizontal scaling requires partitioning, statelessness at the edge, and careful handling of shared mutable state (databases, caches, locks, identity issuers).

True scalability is rarely “add more pods.” Bottlenecks migrate: connection pools, hot keys, sequential IDs, single-leader writes, cross-region chatty calls, and synchronous fan-out. Capacity planning must include headroom for retries, rebalances, and partial failures—otherwise scale tests pass and production collapses under correlated load.

For Java/Spring microservices, scalability design includes pool sizing (DB, HTTP, thread/virtual-thread), bulkheads, backpressure, and data model choices that avoid global coordination. Interview answers should name the bottleneck class and the scale unit (customer, account, tenant, shard key).

### Why interviewers ask it

- Distinguishes “Kubernetes will scale it” from bottleneck identification.
- Tests data partitioning intuition and hot-spot awareness.
- Reveals experience with capacity planning and load-shape (diurnal, bursts, thundering herds).

### Production examples

- Payment auth scales by merchant+BIN affinity and local risk cache; global fraud graph remains a constrained service with async enrichment.
- Telco CDR ingestion scales with Kafka partitions keyed by network element; downstream billing aggregations use windowed reduce, not per-event DB writes.
- Multi-tenant SaaS isolates noisy neighbors via per-tenant quotas and separate write paths for top-N tenants.

### Common mistakes

- Scaling stateless app tiers while leaving a single primary DB as the choke point.
- Choosing partition keys that create hot partitions (popular merchants, celebrity accounts).
- Ignoring retry amplification as a load multiplier.
- Equating autoscaling with elasticity under JVM warm-up and connection establishment cost.

### Senior Engineer discussion

Profile before prescribing. Measure queue depth, lock wait, cache hit ratio, and downstream saturation. Design the scale unit and prove rebalancing does not break invariants. Prefer async boundaries for work that does not need to be on the request path. Document what does *not* scale and what manual intervention looks like at 10×.

### Lead Engineer discussion

Set org standards for scale reviews: load test gates, shard-key reviews, and “hot key” checklists in design templates. Invest in platform primitives (quota, rate limit, partition-aware routing) so every team does not reinvent them poorly. Align cost with growth: unit economics per request/tenant should be visible to product leads, not only infra.

### Tradeoffs

- More shards increase throughput but raise operational complexity and cross-shard query cost.
- Caching improves read scale but adds invalidation and consistency risk.
- Async decoupling scales producers but complicates UX and exactly-once effects.
- Over-provisioning reduces incident risk but wastes budget; aggressive autoscaling risks cold-start latency.

### Interview Challenge

1. A Spring Boot service is CPU-light but DB CPU is pegged at 2k RPS. What scalability moves do you make first?
2. Design scale-out for account balance reads that are 100:1 vs writes, with strong freshness for the owner.
3. How do you prevent a Black Friday traffic spike from taking down shared dependencies?

### Suggested Answer

1. Stop treating app replicas as the fix. Add read replicas or CQRS for read-heavy queries, eliminate N+1 and lock contention, introduce caching for immutable/reference data, batch writes, and move non-critical work off the request path. Cap pool sizes to protect the DB; load-test with production query mix.
2. Owner path: primary or session-sticky read-your-writes (read-after-write token / primary read). Other readers: replica or materialized view with staleness SLO. Cache owner balance with short TTL plus write-through/invalidation on mutation. Partition by accountId.
3. Admission control at edge, per-tenant/per-route limits, bulkheads and circuit breakers, degrade non-critical features, pre-warm pools/caches, and shed load with clear client signals (429/503 + Retry-After). Protect shared DB/Kafka with budgets, not hope.


## Availability

### Explanation

Availability is the fraction of time a system correctly serves requests within agreed conditions. It is not “uptime of a process”—a process can be up while serving errors, stale data beyond SLO, or timeouts. Express availability as SLO (e.g., 99.9% successful requests per month) with explicit success criteria and exclusion rules (client errors, planned maintenance windows if contractually allowed).

High availability is achieved through redundancy, fast failure detection, failover, and graceful degradation—not by eliminating failure. Correlated failures (AZ outage, bad config push, certificate expiry, dependency brownout) dominate real outages. Multi-AZ and multi-region help only if data replication, traffic shifting, and stateful failover are designed and rehearsed.

For money and identity systems, availability without safety is unacceptable: prefer controlled unavailability over split-brain writes. Interviews expect you to discuss error budgets, dependency blast radius, and what the user sees when a dependency is down.

### Why interviewers ask it

- Tests whether candidates confuse process health with user-visible success.
- Probes failover design, degradation, and dependency topology.
- Distinguishes aspirational “five nines” talk from error-budget math.

### Production examples

- Card switch: if fraud service times out, fail open or closed per policy with monitoring—never hang the auth path.
- Core ledger: prefer reject writes during quorum loss over accepting divergent balances.
- Customer portal: read-only mode from cache/replica when write API is unavailable, with explicit UI banner and suppressed mutations.

### Common mistakes

- Counting “pods Ready” as availability without success-rate SLIs.
- Active-active writes across regions without conflict strategy.
- Single shared dependency (DNS, secret store, auth) becoming a hidden SPOF.
- Never practicing failover; runbooks that only work on paper.

### Senior Engineer discussion

Draw the critical path and mark each dependency’s failure mode: timeout, error, slow. Define degrade/fail behavior per dependency. Implement health checks that reflect *serving* ability (dependency depth limited), not just process liveness. Track SLO burn and page on budget consumption, not on every blip.

### Lead Engineer discussion

Drive multi-team dependency governance: who may be on the critical path, timeout standards, and chaos/failover game days. Negotiate SLOs with product that match business criticality and staffing. Make availability a product conversation—feature flags and degrade switches are release mechanisms, not only ops tools.

### Tradeoffs

- More redundancy improves availability but increases cost and consistency complexity.
- Fail-open preserves availability but can increase fraud/loss; fail-closed preserves safety but hurts conversion.
- Multi-region raises resilience and latency/cost; single-region is simpler and cheaper.
- Aggressive health checks speed failover but risk flapping; slow checks prolong outages.

### Interview Challenge

1. Calculate roughly how much downtime 99.9% vs 99.99% allows per month, and what that implies for on-call practice.
2. Your auth dependency is down. Design availability behavior for a payments API and a marketing content API.
3. How do you improve availability when the database primary fails over in 60–90 seconds?

### Suggested Answer

1. 99.9% ≈ 43 minutes/month; 99.99% ≈ 4.3 minutes/month. Higher SLO demands automated failover, tighter change controls, and faster detection—human-only recovery cannot reliably hit four nines.
2. Payments: fail closed or carefully limited fail-open per risk policy; never proceed without authz proof for money movement. Marketing content: serve cached/public content, skip personalization, remain available. Different criticality, different degrade paths.
3. Reduce MTTR: faster detection, connection reaping, retry with jitter against new primary, read-only degrade during failover, and eliminate long transactions that block failover. Optionally use multi-primary or proxy routing (with clear consistency costs). Practice failover under load.


## Reliability

### Explanation

Reliability is correct operation over time—including under stress, partial failure, and bad input. Availability asks “are we up?”; reliability asks “do we keep our promises?” A flaky payment capture that eventually succeeds after double-charging is available and unreliable. Reliability engineering covers fault tolerance, durable execution, idempotency, poison-message handling, and controlled retries.

In distributed systems, reliability is designed via explicit failure domains, timeouts, retries with budgets, compensating actions, and verifiable invariants (ledger conservation, unique constraints). Chaos and fault injection validate assumptions; metrics like MTBF/MTTR and customer-impact incident rate operationalize it.

Spring/Java systems often fail reliably poorly: unbounded retries, ignored timeouts, dual-writes without outbox, and “at-least-once” consumers without idempotent handlers. Senior answers name the failure model and the recovery story.

### Why interviewers ask it

- Separates happy-path designers from failure-mode owners.
- Tests grasp of at-least-once delivery, poison pills, and compensation.
- Reveals incident-driven learning vs theoretical slogans.

### Production examples

- Settlement batch: checkpoint progress, replay safely, quarantine bad records without blocking the file.
- Webhook delivery: durable outbox, exponential backoff, dead-letter after N attempts, replay tooling for support.
- Flaky downstream KYC API: circuit breaker + manual review queue rather than infinite synchronous retry on onboarding path.

### Common mistakes

- Retries without idempotency keys, jitter, or deadlines.
- Dual-writing DB and Kafka without transactional outbox / inbox patterns.
- Treating “redeploy fixed it” as root-cause closure.
- No poison-message strategy; one bad event stalls the partition forever.

### Senior Engineer discussion

For each write path, define success criteria, retry class, and compensation. Prefer making effects idempotent over inventing exactly-once myths. Build replay and reconciliation tools before you need them. During incidents, stabilize first, then prove invariants (balances, inventories) with reconciliation jobs.

### Lead Engineer discussion

Institutionalize reliability patterns: outbox library, standard retry interceptor, DLQ playbooks, and incident review quality bars. Measure reliability outcomes (repeat incidents, customer credits, SLO burn) not just activity. Allocate capacity for reconciliation and repair tooling—these are product features for trust in fintech/telecom.

### Tradeoffs

- Stronger reliability mechanisms (quorum writes, sync replication) increase latency and cost.
- More retries improve transient success rates but amplify load and duplicate risk.
- Manual repair flexibility helps ops but risks unsafe ad-hoc data fixes without audit.
- Strict poison quarantine protects throughput but needs human/process bandwidth to drain DLQs.

### Interview Challenge

1. Kafka consumer processes payments at-least-once and occasionally double-posts. How do you make the system reliable?
2. A nightly batch fails at 80%. What reliability design prevents full reprocessing pain?
3. Differentiate reliability work you’d prioritize for a ledger vs a recommendation engine.

### Suggested Answer

1. Idempotency keys unique per payment intent, transactional outbox, dedupe store with TTL/unique constraint, and handler designed for safe replay. Monitor duplicates via ledger uniqueness violations. Fix producer retries and consumer commit semantics together.
2. Chunked checkpoints, per-record status, resume tokens, and quarantine for poison rows. Make steps idempotent so restart continues from checkpoint. Alert on lag and failure rate, not only job exit code.
3. Ledger: durability, invariants, reconciliation, controlled failover—correctness over availability. Recommendations: degrade to popular defaults, eventual refresh—availability and freshness over perfect accuracy.


## Maintainability

### Explanation

Maintainability is the cost and risk of changing the system safely over years: understanding, modifying, testing, deploying, and diagnosing. Architecture that cannot be changed under organizational reality is a liability regardless of how elegant the first version looked. Coupling, unclear ownership, missing contracts, and absent observability destroy maintainability faster than “bad code style.”

In microservice estates, maintainability includes contract stability, migration paths, local reasonability of services, and operable defaults. A Lead’s maintainability agenda is often more about boundaries and platform standards than about individual class design—though both matter.

Greenfield purity that ignores migration, feature flags, and dual-run periods fails in enterprise settings. Designs should assume multiple teams, staggered releases, and long-lived data.

### Why interviewers ask it

- Predicts whether the candidate’s designs survive contact with a real org.
- Tests modularity, ownership, and evolutionary architecture thinking.
- Surfaces experience with migrations, deprecations, and technical debt triage.

### Production examples

- Expanding payment methods via strategy plugins behind a stable PaymentIntent API rather than forking controllers per scheme.
- Telecom rating engine: versioned tariff packages with dual-run comparison before cutover.
- Shared “core library” that became a distributed monolith—replaced by copy-paste-tolerant contracts and a thin platform SDK.

### Common mistakes

- Premature microservices that increase change cost without clear boundaries.
- Shared DB tables across “services,” blocking independent deploy.
- Undocumented tribal knowledge as the integration contract.
- Big-bang rewrites without strangler/facade incrementalism.

### Senior Engineer discussion

Optimize for change: clear module boundaries, explicit APIs, tests at the right layer, and runnable diagnostics. Leave the codebase easier to operate—feature flags, migration notes, and delete paths for dead code. Resist cleverness that only the author can modify under pressure.

### Lead Engineer discussion

Assign ownership (team ↔ service ↔ SLO). Enforce contract testing and API review for cross-team surfaces. Budget continuous modernization: dependency upgrades, runtime upgrades, and deprecation of shadow systems. Maintainability metrics that matter: lead time for change, change fail rate, and time to understand a sev-1 path.

### Tradeoffs

- More abstraction can ease extension or obscure behavior—measure by change cost, not diagram beauty.
- Strict platform standards improve consistency but slow exceptional product needs.
- Microservices improve team autonomy when boundaries are right; otherwise they multiply distributed failure and cognitive load.
- High test coverage helps refactors but slows delivery if tests are brittle and over-integrated.

### Interview Challenge

1. Three teams must change one workflow. How do you improve maintainability without a rewrite?
2. A “shared kernel” JAR is blocking independent release. What do you do?
3. How do you decide whether to fix a debt item now vs later?

### Suggested Answer

1. Extract a stable API/events contract for the workflow spine; let teams own steps behind that contract. Add contract tests and a single orchestration owner. Use strangler pattern for legacy pieces—incremental extraction beats freeze-and-rewrite.
2. Split into versioned, minimal client SDKs or pure API contracts; stop shipping business logic in the shared JAR. Establish ownership and release cadence per artifact; prefer generated clients from OpenAPI/AsyncAPI where possible.
3. Use risk × frequency × blast radius: debt on the money path or sev-1 debug path jumps the queue. Tie debt work to feature delivery when touching the area; track explicitly in roadmap so it is not only “whenever.”


## Performance

### Explanation

Performance is meeting latency and throughput goals under a defined workload. Tail latency (p95/p99) usually matters more than averages for user-facing and payment paths. Performance work is empirical: measure, hypothesize bottleneck, change one variable, re-measure. Premature optimization without a budget wastes time; ignoring budgets until launch creates emergencies.

End-to-end latency is a sum of queues: network, thread pool, DB locks, GC, serialization, and downstream waits. In Java services, watch allocation rates, pool saturation, cache locality, and N+1 queries. Synchronous fan-out multiplies tail risk; hedging and parallel calls need careful cancellation and duplicate control.

Performance SLOs must specify payload mix, concurrency, and cold vs warm state. A design interview answer that never mentions measurement method is incomplete.

### Why interviewers ask it

- Checks whether candidates reason about tails, queues, and amplification—not just Big-O trivia.
- Tests ability to prioritize high-leverage fixes.
- Reveals production profiling experience (APM, JFR, EXPLAIN, access logs).

### Production examples

- Authorization path: in-process cache for keys/rules, local fraud features, bounded downstream calls with strict timeouts.
- Statement download: precompute/async generate large PDFs; API returns job id rather than blocking 30s.
- Search: denormalized read models; avoid joins across microservices at request time.

### Common mistakes

- Optimizing CPU while waiting on DB/network.
- Missing indexes / over-indexing without write-cost analysis.
- Unlimited page sizes and unbounded `SELECT`s.
- Ignoring GC and allocation in hot serializers/mappers.

### Senior Engineer discussion

Establish latency budgets per hop and enforce with timeouts. Profile under production-like skew. Fix the dominant bottleneck; avoid shotgun caching. Make performance regressions visible in CI for critical endpoints (latency smoke + query plans for hot paths where feasible).

### Lead Engineer discussion

Define org latency classes (interactive, deferred, batch) and default budgets. Provide shared caching/CDN guidance and ban anti-patterns in review (sync cross-service joins, unbounded lists). Tie performance to capacity cost so product sees that “make it faster” sometimes means “store a projection.”

### Tradeoffs

- Caching lowers latency but risks staleness and invalidation bugs.
- Denormalization speeds reads and complicates writes/consistency.
- More aggressive timeouts protect tails but increase partial failure rates.
- Precomputation improves UX at the cost of storage and freshness lag.

### Interview Challenge

1. p50 is fine, p99 is 2s on a Spring endpoint. How do you investigate?
2. Throughput must grow 5× without 5× DB hardware. Options?
3. When is “make it async” the wrong performance fix?

### Suggested Answer

1. Break down p99 by dependency and stage (app, DB, GC, downstream). Check pool saturation, lock waits, slow query logs, GC pauses, and outlier payloads. Look for rare code paths, cold caches, and retry storms. Fix the dominant contributor; verify with histograms, not averages.
2. Reduce DB work: cache, batch, CQRS/read models, better indexes/queries, move non-critical writes async, partition. Scale app tier only after DB/chatty patterns are addressed.
3. When the client truly needs the result to continue (authz, payment capture ack), or when async shifts complexity into unreliable reconciliation without UX/product support. Async must include status model, retries, and observability.


## CAP Theorem

### Explanation

CAP states that in the presence of a network partition, a distributed system cannot simultaneously provide strong Consistency and Availability; you must choose which to sacrifice for that partition. Partition tolerance is not optional on real networks—so the practical choice is CP vs AP behavior during partitions (and often a spectrum of weaker models when healthy).

CAP is frequently misused as “pick two forever.” Healthy systems may offer strong consistency and high availability until a partition or quorum loss forces a mode switch. The interview-grade discussion is about *which* operations are CP or AP, how clients detect degraded mode, and how conflicts resolve when AP systems heal.

For fintech ledgers, CP (or PACELC: when not partitioned, prefer latency vs consistency tradeoffs) often dominates writes. For session/feature stores and public content, AP with conflict resolution may dominate. Naming the unit of consistency (document, row, shard) matters as much as the slogan.

### Why interviewers ask it

- Filters memorized buzzwords from operators who have lived split-brain risk.
- Forces explicit partition behavior, not only sunny-day diagrams.
- Connects to concrete stores (Raft/Paxos quorum, Dynamo-style, consensus vs gossip).

### Production examples

- ZooKeeper/etcd/Consul-style config: CP—refuse updates without quorum.
- Cassandra-style telemetry: AP with tunable consistency; repair and read repair on heal.
- Payment primary region: CP writes; secondary region read-only or async replica until failover elects new primary under fencing.

### Common mistakes

- Claiming CA systems in asynchronous networks.
- Applying one CAP choice to the entire product surface.
- Ignoring client behavior: retries that create divergence under AP.
- Equating “eventual consistency” with “no consistency design.”

### Senior Engineer discussion

Per operation, state partition behavior: reject, serve stale, or queue. Use fencing tokens/leases for leadership. Test what happens when half the cluster cannot reach the other half—including disk and clock weirdness. Document recovery: rejoin, repair, and conflict policy.

### Lead Engineer discussion

Prevent teams from casually choosing multi-region active-active writes for money paths. Standardize patterns: single-leader CP for ledgers, AP with CRDTs/LWW only where business allows. Educate product on user-visible consequences of AP (temporary divergence) so they do not treat CAP as an infra-only concern.

### Tradeoffs

- CP during partition: safer invariants, more user-visible errors/unavailability.
- AP during partition: better availability, conflict/merge cost and possible anomalies.
- Sync cross-region replication: stronger consistency, higher write latency.
- Async replication: lower latency, higher RPO and failover complexity.

### Interview Challenge

1. Can you have a CA system on a real WAN? Explain.
2. Design partition behavior for account transfer vs product catalog browse.
3. How does PACELC extend CAP for day-to-day latency decisions?

### Suggested Answer

1. Not meaningfully: partitions happen. Systems that appear CA usually assume no partition or stop being available/consistent when one occurs. Prefer stating CP/AP under partition plus healthy-path behavior.
2. Transfer: CP—reject if quorum/primary unreachable; never fork balances. Catalog browse: AP—serve last known from local replica/cache; reconcile later.
3. PACELC: if Partitioned, choose A or C; Else (normal operation), choose Latency vs Consistency. Even without partitions, sync replication vs async is a daily latency/consistency tradeoff.


## Consistency Models

### Explanation

Consistency models define what reads may return relative to writes and other readers. Linearizability (strong consistency) makes each operation appear instantaneous at a single point in real time—expensive across distance. Sequential consistency preserves program order per process without real-time guarantees. Causal consistency preserves cause-effect. Eventual consistency promises convergence absent new writes, without bounding staleness unless you add SLOs.

Session-oriented guarantees matter in product UX: read-your-writes, monotonic reads, and monotonic writes often satisfy users without global linearizability. “Strong consistency” in vendor marketing may mean primary reads, quorum reads, or true linearizability—verify semantics.

In microservice architectures, end-to-end consistency is composed: DB transaction scope, cache TTL, message delivery, and UI expectations. Dual writes create inconsistency windows unless you use single source of truth + projections, outbox, or distributed transactions sparingly.

### Why interviewers ask it

- Moves beyond CAP slogans into client-visible guarantees.
- Tests ability to pick the weakest model that meets the business need.
- Connects storage settings (quorum, RYW sticky sessions) to API design.

### Production examples

- Banking: linearizable (or serializable) ledger postings within account shard; statements may be eventual with “as of” timestamps.
- Shopping cart: read-your-writes for the session; inventory reservation may be eventually reconciled with oversell controls.
- Telecom balance: main balance CP; promotional counters eventually consistent with bounded reconciliation.

### Common mistakes

- Caching strongly consistent data without invalidation/versioning strategy.
- Assuming replica reads are safe after write without RYW mechanism.
- Using distributed 2PC by default instead of local transactions + async projection.
- Promising “immediate consistency everywhere” across regions without latency budget.

### Senior Engineer discussion

Specify consistency per read API: “may lag up to Xs” vs “reflects write W.” Return versions/etags so clients can detect staleness. Prefer single-writer boundaries and deterministic conflict resolution. Prove invariants with reconciliation jobs even when the online path is strongly consistent—bugs and partial outages still happen.

### Lead Engineer discussion

Create a vocabulary shared by product and engineering: strong, RYW, bounded staleness, eventual. Encode defaults in API guidelines (e.g., money reads vs feed reads). Review cross-service flows for accidental consistency upgrades that serialize the estate. Invest in platform support for versioned resources and idempotent writes so teams can compose safely.

### Tradeoffs

- Stronger models simplify app logic but increase latency and reduce availability under failure.
- Weaker models improve scale/latency but push complexity into UX, merges, and support tooling.
- Sticky sessions enable RYW cheaply but hurt load balancing and failover cleanliness.
- Quorum reads/writes raise confidence and cost; leader reads couple availability to leader health.

### Interview Challenge

1. After a successful POST /transfers, a subsequent GET on another device doesn’t show the result. Which consistency gap is this, and how do you fix it product-wise and technically?
2. Pick consistency models for: fraud feature store, ledger write, and analytics dashboard.
3. How do you explain monotonic reads to a product manager with a concrete failure story?

### Suggested Answer

1. Likely missing read-your-writes / cross-device propagation delay (eventual replica). Product: show “pending” on the writing device; don’t claim global immediacy. Technical: primary read for a grace window, version tokens, or wait-for-replication on read path for the actor; define cross-device staleness SLO.
2. Fraud features: causal/eventual with freshness SLOs—favor availability and speed. Ledger write: linearizable/serializable within the consistency boundary. Analytics: eventual, time-travel/`as_of` explicit—optimize for throughput.
3. Without monotonic reads, a user can see a new transaction then refresh and see an older balance—trust-breaking. Sticky replica, version barriers, or primary reads prevent time going backwards for that session.
