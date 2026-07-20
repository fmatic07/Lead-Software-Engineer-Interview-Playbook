# Databases

> Storage choices under consistency, scale, and operational cost constraints.

## SQL vs NoSQL

### Explanation

Relational engines enforce schemas, joins, and ACID transactions; document/key-value/wide-column stores optimize for flexible documents, keyed access, or write-heavy partitions. The real decision is access pattern + consistency boundary + operational model — not “SQL is old.” PostgreSQL/MySQL remain default for money, inventory, and identity. DynamoDB/Cassandra/Mongo fit when you can denormalize around a primary key and tolerate weaker cross-key consistency.

### Why interviewers ask it

- Separates buzzword preference from workload-driven selection.
- Tests whether you can name failure modes of each model under growth.
- Reveals if you default to polyglot complexity without a consistency plan.

### Production examples

- Order + payment + ledger stay in Postgres; catalog search via Elasticsearch with eventual projection.
- Session/rate-limit counters in Redis; durable entitlements in SQL.
- Multi-tenant config in Mongo for schema variance; billing remains relational with constraints.

### Common mistakes

- Choosing NoSQL to “scale later” while still needing multi-row transactions.
- Modeling many-to-many graphs in documents and discovering N+1 application joins.
- Ignoring backup/PITR/migration tooling differences across engines.

### Senior Engineer discussion

Start from queries, write rate, cardinality, and consistency SLOs. Prefer one primary store until a measured bottleneck forces a second. Dual-write only with source of truth, outbox/CDC, and repair jobs.

### Lead Engineer discussion

Own polyglot cost: hiring, on-call, migrations, lock-in. Require ADRs and load-tested access patterns before new stores. Align schema ownership with bounded contexts.

### Tradeoffs

- SQL: strong integrity, mature ops; vertical/join cost at extreme scale.
- NoSQL: partition-friendly writes; weak multi-entity consistency, harder ad-hoc analytics.
- One store: simpler ops; may force awkward models.
- Polyglot: fit-for-purpose; dual-write and skill fragmentation risk.

### Interview Challenge

You need orders, inventory reservations, and a product catalog with frequent attribute changes. Propose stores and consistency boundaries.

### Suggested Answer

Orders/reservations/stock in Postgres with locks or optimistic versions. Catalog JSONB + search index for discovery. Stock authoritative in SQL; CDC to search — never let search own inventory.


## Sharding

### Explanation

Sharding partitions data across nodes by a shard key so no single primary holds the full working set. Cross-shard queries and transactions become distributed problems. Hot keys, resharding, and orphaned secondary indexes dominate real pain — not the first hash ring diagram.

### Why interviewers ask it

- Distinguishes vertical scaling knowledge from horizontal design maturity.
- Probes for hot-key and rebalancing awareness.
- Tests whether candidates invent sharding before exhausting simpler options.

### Production examples

- Tenant-id sharding for B2B SaaS so large tenants isolate I/O.
- User-id hash sharding for feeds; celebrity accounts become hot shards.
- Vitess/Citus/ProxySQL to keep MySQL/Postgres SQL mostly intact under shards.

### Common mistakes

- Sharding by auto-increment id then needing global sequences and range scans.
- Mutable shard keys (email, region) that force data moves.
- App-level sharding without routing layer, migration tooling, or cross-shard budgets.

### Senior Engineer discussion

Shard key = dominant transaction boundary (tenant/aggregate). Plan split/merge and dual-write cutover. Secondary indexes local or via search. Measure p99 per shard.

### Lead Engineer discussion

Delay sharding until connection/WAL/storage or blast-radius forces it. Budget resharding as multi-quarter work; own routing config and drain-shard playbooks.

### Tradeoffs

- Hash sharding: even load, poor range queries.
- Range sharding: range scans, hotspot risk at edges.
- Tenant sharding: isolation, uneven tenant sizes.
- Early sharding: operational tax; late sharding: painful emergency.

### Interview Challenge

A single Postgres primary hits IOPS limits on `orders`. How do you decide between read replicas, partitioning, and sharding?

### Suggested Answer

First: archive, indexes, pooling, read replicas. Time-partition if retention/queries are time-scoped. Shard only if write volume or tenant blast radius remains; key by tenant/customer aligned to order TX.


## Replication

### Explanation

Replication copies changes from primary to replicas for HA and read scale. Sync replication waits for replica ack (durability/RPO); async favors lag and throughput. Failover correctness depends on fencing, promotion fencing tokens, and application connection routing — not just “replicas exist.”

### Why interviewers ask it

- HA soundbites are common; split-brain and lag incidents are what panels want.
- Tests RPO/RTO reasoning under failover.
- Separates managed-cloud checkbox knowledge from operational ownership.

### Production examples

- Postgres streaming replication with Patroni/RDS Multi-AZ; writes only via writer endpoint.
- Async replica lag causes “missing order” after read redirect.
- Cross-region async replica for DR with explicit multi-minute RPO.

### Common mistakes

- Strongly consistent reads from async replicas without lag checks.
- Autofailover without fencing → dual writers.
- Treating Multi-AZ as multi-region DR; ignoring replication slot disk growth.

### Senior Engineer discussion

Classify reads as lag-tolerant or not. Primary for read-your-writes; monitor lag/slots; clients tolerate read-only windows on promotion.

### Lead Engineer discussion

Define RPO/RTO with product/compliance. Game-day failovers. Own runbooks for lag SLO breach and fence-after-promote.

### Tradeoffs

- Sync: lower RPO, higher write latency and outage coupling.
- Async: faster writes, nonzero data loss window.
- More replicas: read scale and HA; more lag variance and cost.
- Automatic failover: faster RTO; higher split-brain risk if fencing is weak.

### Interview Challenge

After failover, some writes disappear and some appear twice. What went wrong?

### Suggested Answer

Async promotion with unreplicated commits and/or non-idempotent retries, or brief dual-primary. Fix: fence old primary, sync/semi-sync for critical commits, idempotent keys, fail-closed routing on ambiguous primary.


## Partitioning

### Explanation

Partitioning splits a table within one database (range/list/hash) for pruneable queries, cheaper retention deletes, and vacuum/maintenance locality. It is not sharding: one catalog, one transaction domain (with caveats). Wrong partition key destroys prune and creates planning pain.

### Why interviewers ask it

- Checks whether candidates confuse partition with shard.
- Tests time-series and retention design maturity.
- Probes for lock and maintenance awareness during detach/drop.

### Production examples

- `events` monthly range partitions; drop old partitions instead of DELETE.
- Hash partition on `account_id` when range pruning does not help.
- BRIN on large append-only time partitions for cheap indexing.

### Common mistakes

- Partition key absent from queries → scans across children.
- Too many partitions → planner/memory overhead.
- Unique constraints / FKs that ignore partition-key rules.

### Senior Engineer discussion

Align partition key with filters and retention. Automate create/detach/drop; benchmark planner with realistic partition counts.

### Lead Engineer discussion

Treat partitioning as a capacity project with owners/calendars. Reject “partition everything” without query evidence.

### Tradeoffs

- Time partitions: excellent retention; hot current partition still bottlenecks.
- Hash partitions: balance; weak time pruning.
- Fine partitions: better prune; operational and planner cost.
- Coarse partitions: simpler; larger maintenance windows.

### Interview Challenge

A 4TB audit table DELETE job locks and never finishes. Redesign.

### Suggested Answer

Range-partition by month; retain via `DETACH`/`DROP`. Queries must include time. Archive cold storage async; uniqueness on `(id, period)` if needed.


## Read/Write Separation

### Explanation

Writes go to primary; reads fan out to replicas. Gains capacity only for lag-tolerant reads. Sticky sessions, causal tokens, or “read your writes” routing close the consistency gap for UX-critical paths.

### Why interviewers ask it

- Common Spring/`@Transactional(readOnly)` interview bait with sharp edges.
- Tests understanding of replication lag as a product bug, not infra noise.

### Production examples

- Reporting on replicas; checkout on primary.
- Post-create GET hits replica and 404s — classic race.
- CQRS: SQL write model, denormalized read model via CDC.

### Common mistakes

- Global read-only routing without classifying endpoints.
- Assuming `readOnly=true` guarantees replica routing.
- No lag-based shedding; dual pools without connection budgets.

### Senior Engineer discussion

Annotate APIs with consistency needs. After writes: primary or wait-for-LSN. Circuit-break replica reads past lag SLO; separate pools so replicas cannot exhaust primary.

### Lead Engineer discussion

Codify read policy in platform libraries. Lag is a product SLO. CQRS only when replica reads are insufficient.

### Tradeoffs

- Replica reads: scale and isolation; stale data risk.
- Always-primary reads: correct; primary saturation.
- CQRS: tailored read models; dual-write/CDC complexity.
- Sticky routing: better UX; weaker load distribution.

### Interview Challenge

Design read/write routing for a Spring order service with “place order then show order” UX.

### Suggested Answer

Writes and immediate GET-by-id on primary (or wait-for-LSN). List/history on replicas with max-lag gate. Idempotent create keys. Metrics: primary QPS, lag, 404-after-create.


## Indexing

### Explanation

Indexes trade write amplification and storage for selective read paths. B-tree dominates equality/range; GIN/GiST for JSON/full-text; covering indexes avoid heap fetches. The optimizer chooses plans from statistics — indexes without selectivity or matching predicates are dead weight.

### Why interviewers ask it

- Performance incidents are often missing/wrong indexes or too many indexes.
- Distinguishes `EXPLAIN` literacy from cargo-cult `CREATE INDEX`.

### Production examples

- Composite `(tenant_id, created_at)` matching filter+sort.
- Partial index `WHERE status = 'OPEN'` for hot queues.
- Unique index enforcing invariants under concurrency.

### Common mistakes

- Indexing low-cardinality columns alone; wrong composite column order.
- Duplicate overlapping indexes eating write IOPS.
- Function on column without matching expression index; ignoring write amplification.

### Senior Engineer discussion

Every index needs a query owner and `EXPLAIN (ANALYZE, BUFFERS)` proof. Watch unused indexes; prefer constraints that also accelerate lookups.

### Lead Engineer discussion

Gate indexes like schema changes. Track index-vs-table size; educate on write amplification under peak load.

### Tradeoffs

- More indexes: faster reads; slower writes, larger backups.
- Covering indexes: great reads; wide rows and churn.
- Partial indexes: smaller/faster; fragile if predicates change.
- Denormalized columns for indexing: speed; sync complexity.

### Interview Challenge

p99 of `GET /orders?tenant=&status=&from=` is 2s. Outline diagnosis and fix.

### Suggested Answer

Slow query + `EXPLAIN ANALYZE`. Add composite `(tenant_id, status, created_at)` or partial by status. Keyset pagination, not deep `OFFSET`. Concurrent index build; check write overhead.


## Transactions

### Explanation

Transactions define atomicity and isolation. Read Committed is common default; Repeatable Read/Serializable prevent more anomalies at concurrency cost. In Spring, transaction boundaries belong at use-case services, not repositories. Distributed transactions (XA/2PC) rarely survive microservice scale — prefer local transactions + outbox.

### Why interviewers ask it

- Core of correctness under concurrency for lead-level backend roles.
- Tests awareness of lost updates, write skew, and isolation levels.
- Surfaces whether candidates reach for 2PC too quickly.

### Production examples

- Lost update on inventory without version column or `SELECT FOR UPDATE`.
- Long TX holding locks during HTTP calls → timeout cascades.
- Outbox row written in same TX as business data; relay publishes to Kafka.

### Common mistakes

- Swallowing exceptions inside `@Transactional` (no rollback).
- Self-invocation / private `@Transactional` bypassing Spring proxies.
- Wide transactions spanning remote calls.

### Senior Engineer discussion

Short, pure, local TX. Optimistic for low contention; pessimistic for tight inventory. Idempotency keys in same TX. Saga/outbox over XA.

### Lead Engineer discussion

Rules: no remote I/O in TX; explicit isolation when needed; retry on serialization failures. Invest in outbox as platform capability.

### Tradeoffs

- Higher isolation: fewer anomalies; more aborts/latency.
- Pessimistic locks: simple correctness; lock contention.
- Optimistic versions: scale reads; retry storms under hot keys.
- 2PC: strong atomicity; availability and ops hazard.

### Interview Challenge

Reserve stock then charge payment across Inventory and Payment services. Design without XA.

### Suggested Answer

Inventory TX: reserve + outbox `StockReserved`. Payment consumes, charges idempotently, emits captured/failed. Inventory finalizes or releases. Timeouts → compensate. Never hold DB TX during payment HTTP.


## Eventual Consistency

### Explanation

Replicas, caches, CQRS projections, and async workflows converge over time rather than in one atomic commit. Correct systems make staleness visible, bounded, and recoverable — with idempotent consumers, reconciliation, and explicit user-visible semantics (“pending,” “processing”).

### Why interviewers ask it

- Microservices force this topic; panels want maturity, not denial.
- Tests whether you design for repair, not only happy-path messaging.

### Production examples

- Search index lags catalog; UI treats discovery as slightly stale.
- Wallet projected from events; nightly reconcile vs ledger.
- Order status advanced by events; clients poll/watch pending states.

### Common mistakes

- Dual-write DB + bus without transactional outbox.
- No idempotency / DLQ → duplicates or infinite poison retries.
- Hiding pending states from UX and support tools.

### Senior Engineer discussion

Define convergence SLO; metrics for lag/backlog/reconcile diffs. Readers tolerate absence/staleness; prefer monotonic versions over wall clocks.

### Lead Engineer discussion

Product must accept eventual semantics per flow. Fund reconciliation and support playbooks. Fewer async hops on money paths.

### Tradeoffs

- Eventual: availability and decoupling; complex UX and repair.
- Strong: simpler mental model; coupling and latency.
- Sync RPC chains: easier tracing; cascading failure risk.
- Async+reconcile: resilient; operational machinery required.

### Interview Challenge

Catalog service updates price in DB and publishes `PriceChanged`. Sometimes bus message never fires. Fix the pattern.

### Suggested Answer

Write price + outbox in one TX. Publisher polls/CDC with at-least-once. Consumers idempotent on event id/version. Reconcile DB vs projection; remove dual-write from app paths.
