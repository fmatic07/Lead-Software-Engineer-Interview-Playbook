# Enterprise Integration Patterns

> Reliable cross-service communication is less about brokers and more about failure semantics, ordering, and who owns recovery.

---

## Purpose

Train Lead/Architect judgment on the integration patterns that keep enterprise Java estates alive under partial failure — queues, buses, request/reply, pub/sub, outbox/CDC, and resilience primitives — with Spring Boot, Kafka/RabbitMQ, Redis, PostgreSQL/MySQL, API gateway, and Kubernetes as the default frame.

---

## Topics Covered

- [ ] Message Queue
- [ ] Event Bus
- [ ] Request Reply
- [ ] Publish Subscribe
- [ ] Outbox Pattern
- [ ] CDC (Change Data Capture)
- [ ] Retry
- [ ] Circuit Breaker
- [ ] Bulkhead
- [ ] Rate Limiter

---

## Message Queue

### What it is in production

A **message queue** is a durable work buffer between producers and consumers: producers enqueue commands or tasks; competing consumers pull and process. In Java estates this is typically Kafka (partitioned log), RabbitMQ (AMQP queues), or SQS/Azure Service Bus. The queue absorbs spikes, decouples availability, and defines **delivery guarantees** (at-most-once, at-least-once, effectively-once with idempotency).

### Production use cases

- **Async command processing:** image resize, PDF generation, KYC document OCR — HTTP returns `202` + correlation id; workers drain the queue.
- **Workload leveling:** payment capture spikes at month-end; queue smooths DB write pressure.
- **Cross-team handoff:** Order Service publishes `ReserveInventory`; Inventory workers own retries and DLQ without blocking checkout.
- **Kubernetes backpressure:** HPA on consumer lag (Kafka lag / Rabbit queue depth) instead of raw CPU.

### Failure semantics, ordering, idempotency

| Concern | Production rule |
|---------|-----------------|
| Delivery | Assume **at-least-once**. Design handlers idempotent. |
| Ordering | Kafka: per partition key. Rabbit: per queue with single consumer or careful prefetch. Never promise global order across shards. |
| Ack timing | Commit/ack **after** side effects succeed (or in the same DB transaction via outbox). |
| Poison messages | Bounded retries → DLQ → alert. Do not block the partition forever. |
| Observability | Produce rate, consume rate, lag/depth, retry count, DLQ rate, processing latency p99. |

Spring Boot: prefer Spring Kafka / Spring AMQP with explicit ack modes; avoid auto-ack for money paths. Partition key = aggregate id (`orderId`, `accountId`) when order matters.

---

## Event Bus

### What it is in production

An **event bus** is a broadcast medium for domain facts (“something happened”) rather than directed work items. Consumers subscribe by interest; producers do not know the fan-out. Kafka topics with many consumer groups, Redis Streams fan-out, or in-process Spring `ApplicationEvent` (single JVM only) all play this role at different scales.

### Production use cases

- **Domain notification:** `CustomerVerified` → CRM sync, welcome email, risk scoring — none owned by the verifier.
- **Platform integration:** audit/compliance bus that every service emits to; SIEM consumes independently.
- **UI refresh / SSE fan-out:** gateway subscribes to bus and pushes to browser (careful with volume).
- **Multi-tenant SaaS:** tenant-scoped topics or headers so isolation and ACLs stay enforceable.

### Distinctions that matter

- **Queue** = work distribution (competing consumers, one processing).
- **Bus** = fan-out (each subscriber processes independently).
- Misusing a bus as a command queue causes “who owns the DLQ?” ownership fights.
- In-process buses die with the pod; do not use Spring `ApplicationEvent` for cross-service reliability.

Schema registry (Avro/JSON Schema/Protobuf) + compatibility mode (BACKWARD/FORWARD) is mandatory once more than two teams share the bus.

---

## Request Reply

### What it is in production

**Request/reply over messaging** correlates a request message with a response on a reply queue/topic (or temporary reply destination), often with a timeout. Used when async transport is required but the caller still needs an answer — legacy ESB patterns, long-running jobs with status, or bridging sync APIs to async backends.

### Production use cases

- **Long job with timeout:** client `POST /reports` → enqueue → worker replies on `replyTo` with result location; API polls or waits with deadline.
- **Legacy mainframe bridge:** Spring Integration or Kafka request topics with correlation headers (`correlationId`, `replyTopic`).
- **Orchestrator step:** saga step needs confirmation before next action without holding HTTP threads.

### Failure semantics

- **Timeouts are first-class.** Without them you leak waiters and inflate thread pools (MVC) or pile up reactive subscriptions (WebFlux).
- Duplicate replies: ignore late replies after timeout; state machine must be idempotent on transition.
- Prefer **async API + status resource** (`GET /jobs/{id}`) over holding request/reply across pods unless you have sticky routing or shared reply state.
- Observability: correlation id end-to-end, timeout rate, orphaned replies.

---

## Publish Subscribe

### What it is in production

**Pub/sub** is fan-out subscription: one publish, N independent consumers. Kafka consumer groups give *competing* within a group and *fan-out* across groups. Rabbit fanout/topic exchanges bind multiple queues. Redis Pub/Sub is fire-and-forget (no durability) — fine for cache invalidation, wrong for ledger events.

### Production use cases

- **Cache invalidation:** `ProductPriceChanged` → Redis key delete across regions (tolerate loss; reconcile via TTL).
- **Search projection:** orders topic → Elasticsearch indexer consumer group.
- **Notification channels:** email, SMS, push as separate groups on same event.
- **Partner webhooks:** fan-out to outbound webhook workers with per-partner rate limits (bulkhead).

### Ordering and idempotency

- Same as queues: order per key; consumers must tolerate duplicates and out-of-order *across* keys.
- Event-carried state transfer vs notification-only: notification forces consumers to call back (chatty, coupled); carried state risks large payloads and PII spread — pick deliberately.
- Compacted topics for “latest state” projections; chronologically retained topics for audit/replay.

---

## Outbox Pattern

### What it is in production

The **transactional outbox** writes business state and an outbound message row in the **same DB transaction**. A relay (polling publisher or CDC) publishes to Kafka/Rabbit, then marks the outbox row processed. This eliminates dual-write inconsistency between PostgreSQL/MySQL and the broker.

### Production use cases

- Order placed + `OrderCreated` event must not diverge.
- Wallet debit + `BalanceChanged` for downstream fraud engines.
- Any Spring `@Transactional` service that previously “saved then `kafkaTemplate.send`” and lost events on broker blip.

### Failure semantics

- Relay crash: rows remain; republish is safe if consumers are idempotent and events have stable ids.
- Exactly-once *to Kafka* still needs idempotent producer / transactions; consumers remain at-least-once.
- Outbox table growth: partition/archive; index on `unpublished` status; alert on age of oldest unpublished row.
- Prefer Debezium CDC on outbox table over naive high-frequency polling at scale.

Anti-pattern twin: **inbox** table for deduplicating inbound events by `eventId` unique constraint in the same transaction as business write.

---

## CDC (Change Data Capture)

### What it is in production

**CDC** streams DB commit log changes (WAL/binlog) to Kafka via Debezium/connectors. Consumers rebuild read models, sync search, feed warehouses, or drive outbox relays without invasive application dual-writes.

### Production use cases

- **Read model sync:** Postgres orders → Elasticsearch / Redis projection.
- **Legacy extraction:** main OLTP MySQL → analytics without batch ETL windows.
- **Outbox relay:** CDC on `outbox` table → Kafka (low app intrusion).
- **Cache warm:** CDC to Redis for hot keys (with careful TTL and delete semantics).

### Failure semantics and caveats

- CDC is **not** a free domain event model — schema is table-shaped; join/business meaning is lost unless you emit domain events from app or transform carefully.
- Ordering follows DB commit order per table/partition key mapping; multi-table aggregates need correlation.
- Schema changes break connectors; treat migrations as dual deploy (expand/contract).
- PII amplification: every column change becomes a stream — classify and redact.
- Snapshot + streaming: initial load can crush Kafka and consumers; throttle and backfill deliberately.
- Observability: connector lag, snapshot progress, transform errors, DLQ for poison rows.

When **not** to use CDC as public integration: cross-team contracts should be domain events, not raw table diffs — otherwise consumers couple to your physical schema.

---

## Retry

### What it is in production

**Retry** re-attempts failed operations under transient faults (network blips, 503, lock timeouts). In Spring: Resilience4j Retry, Spring Retry, Kafka retry topics, Rabbit DLX delayed retries. Retries without policy create thundering herds and amplify outages.

### Production use cases

- Idempotent HTTP calls to payment acquirer with exponential backoff + jitter.
- Kafka consumer retry topics (`main` → `retry-1` → `retry-2` → `DLQ`) with increasing delays.
- Flyway/Liquibase-adjacent migration jobs that hit lock wait timeouts.

### Policy that interviewers expect

| Parameter | Guidance |
|-----------|----------|
| Max attempts | Small for user-facing sync (2–3); larger for async jobs with DLQ |
| Backoff | Exponential + **full jitter** |
| Retryable errors | Timeouts, 429/503, connection reset — **not** 400/401/403/validation |
| Idempotency | Mandatory if side effects possible |
| Budget | Total retry time < upstream SLA / pod termination grace |

Never retry non-idempotent POSTs without an idempotency key. Never retry forever in the request thread.

---

## Circuit Breaker

### What it is in production

A **circuit breaker** stops calling a failing dependency after an error threshold, failing fast (open), then probing (half-open). Resilience4j CircuitBreaker is the Spring Boot default. Protects thread pools, connection pools, and cascading latency.

### Production use cases

- Product service calls Pricing; Pricing p99 explodes → open circuit → serve cached price or degrade.
- API gateway → downstream; prevent one bad service from saturating gateway workers.
- Kafka producer to optional analytics cluster — open circuit, drop/ defer non-critical events.

### Failure semantics

- Open ≠ silent success. Return explicit fallback, cached data, or `503` with `Retry-After`.
- Configure on **dependency + operation** (bulkhead sibling), not one global breaker.
- Metrics: state transitions, failure rate, slow call rate, fallback invocations.
- Half-open concurrency must be low or you re-stampede.
- Pair with timeouts; breakers without timeouts wait until thread death.

---

## Bulkhead

### What it is in production

**Bulkhead** isolates resources so one slow dependency cannot exhaust the whole process: separate thread pools, connection pools, queue depths, or Kubernetes deployment/CPU limits per workload class. Resilience4j Bulkhead / TimeLimiter; servlet vs reactive isolation; separate Hikari pools for critical vs batch paths (rare but valid).

### Production use cases

- Checkout path: dedicated pool for Payments; Browse uses another — payment outage does not freeze catalog.
- Kafka consumers: separate consumer groups/apps for critical ledger vs email notifications.
- Gateway: per-route concurrency limits so partner webhooks cannot starve mobile API.
- Redis: separate connection pools for cache vs rate-limit counters under pressure.

Without bulkheads, one `@Async` executor or one Tomcat pool becomes a shared fate domain.

---

## Rate Limiter

### What it is in production

**Rate limiting** caps accepted work per key (IP, client_id, tenant, user) to protect capacity and enforce fairness. Token bucket / sliding window at API gateway, Redis (`INCR` + TTL or Lua), Resilience4j RateLimiter, or Envoy/Kong/AWS API Gateway policies.

### Production use cases

- Public API: 100 RPS per `client_id`; return `429` + `Retry-After`.
- Multi-tenant SaaS: noisy neighbor control per `tenantId`.
- Login / OTP endpoints: brute-force resistance (with care for distributed counters).
- Outbound partner calls: protect *their* SLA and your IP reputation.

### Design notes

- Local in-memory limits fail under multiple pods — use Redis or gateway-central limits for global caps; local is fine for coarse protection.
- Distinguish **ingress** (protect us) vs **egress** (protect dependency).
- Idempotent clients must respect `429`; document it in OpenAPI.
- Observability: throttle count by key class (never log raw PII keys at high cardinality without aggregation).

---

## Pattern selection cheat sheet

| Need | Prefer | Avoid |
|------|--------|-------|
| Compete for work | Queue / Kafka consumer group | Fanout without competing semantics |
| Fan-out facts | Pub/sub / event bus | Point-to-point queue per consumer (ops explosion) |
| Sync answer, short | HTTP + timeout + retry | Messaging request/reply across pods |
| DB + broker atomicity | Outbox (+ CDC relay) | Dual write |
| Raw DB sync / projections | CDC | App dual-write for every table |
| Dependency meltdown | Circuit breaker + timeout + bulkhead | Infinite retry |
| Abuse / fairness | Rate limit at edge | Only app-level hope |

---

## Why this matters in production

Enterprise outages rarely start as “Kafka is down.” They start as **dual-write inconsistency**, **unbounded retries**, **shared thread pools**, **unordered consumers on financial aggregates**, or **CDC coupling to table schemas**. Interview panels for Lead roles probe whether you can name the failure mode, the user-visible symptom, and the operability cost of the pattern — not whether you can recite EIP icons.

Integration is where **consistency, latency, and team autonomy** collide. Choosing pub/sub without idempotency multiplies every producer bug across N consumers. Choosing sync REST for everything couples availability. Choosing microservices without outbox guarantees creates silent drift that finance notices weeks later.

---

## Engineering tradeoffs

| Axis | Lean one way | Lean the other |
|------|----------------|----------------|
| Sync vs async | Simpler debugging, tighter latency SLOs | Better isolation, spike absorption, harder UX consistency |
| At-least-once vs buffering loss | Safer business invariants with idempotency cost | Simpler code, unacceptable for money/inventory |
| Domain events vs CDC | Stable contracts, app complexity | Fast projections, schema coupling |
| Shared bus vs many queues | Discoverability, governance | Isolation, clearer ownership |
| Aggressive retries | Higher success under blips | Amplifies outages; needs jitter + breaker |
| Fine-grained breakers | Precise degradation | Config sprawl, alert noise |
| Gateway rate limits | Central enforcement | Coarse keys; app still needs business quotas |

Reversibility: adding a queue is easy; removing an event contract used by five teams is not. Prefer **expand/contract** on schemas and explicit deprecation windows.

---

## Common anti-patterns

1. **Dual write** without outbox/CDC — DB commit succeeds, produce fails (or vice versa).
2. **Kafka as RPC** — sync request/reply over topics without timeouts, DLQ, or correlation discipline.
3. **Global ordering assumptions** across partitions or queues.
4. **Ack-before-side-effect** — message lost on crash after ack.
5. **Infinite retry in request thread** — turns dependency latency into your outage.
6. **One thread pool / one Hikari pool for all** — no bulkhead; noisy neighbor inside the JVM.
7. **Redis Pub/Sub for durable domain events** — silent loss on subscriber disconnect.
8. **CDC as public API** — consumers couple to column renames.
9. **Shared “god topic”** with unrelated event types and no ownership.
10. **Circuit breaker without fallback semantics** — open circuit returns empty success; corruption follows.
11. **Rate limit only in app** behind horizontally scaled pods — ineffective global cap.
12. **DLQ without runbook** — poison messages rot; no replay ownership.

---

## Best practices

1. Default to **at-least-once + idempotent consumers** (inbox unique key or natural business key).
2. Use **transactional outbox** for any state change that must emit a message.
3. Partition/order by **aggregate id**; document the ordering boundary.
4. Bound retries with **jitter**; escalate to DLQ; page on DLQ rate.
5. Pair every remote call with **timeout + circuit breaker + bulkhead** (Resilience4j modules).
6. Put **rate limits at the gateway** for abuse; enforce **tenant quotas** in the domain service.
7. Propagate **trace id / correlation id** across HTTP and messaging (Micrometer Tracing / OpenTelemetry).
8. Own **schema compatibility** in CI (schema registry compatibility checks).
9. Alert on **lag, oldest outbox age, breaker open, throttle rate** — not only CPU.
10. Prefer **domain events** for inter-team contracts; reserve CDC for projections and relays.
11. Load-test consumers with poison and duplicate messages before go-live.
12. Write **replay runbooks**: how to reset offsets, redrive DLQ, and rebuild read models.

---

## Architecture review checklist

- [ ] Dual-write paths identified; outbox or CDC required for critical emits
- [ ] Delivery guarantee stated per flow; idempotency strategy concrete (key + store)
- [ ] Ordering boundary documented (partition key / single-threaded queue)
- [ ] Retry policy: max, backoff, retryable errors, DLQ, owner
- [ ] Timeouts on all outbound I/O; no unbounded waits
- [ ] Circuit breakers scoped per dependency; fallbacks explicit
- [ ] Bulkheads for critical vs non-critical paths (pools, deployments, or consumer apps)
- [ ] Rate limits at edge + business quotas for multi-tenant
- [ ] Schema registry / contract tests for shared events
- [ ] Observability: lag, DLQ, breaker state, retry, correlation in logs
- [ ] PII/retention reviewed on bus topics and CDC streams
- [ ] Kubernetes: HPA on lag; PDB and termination grace ≥ drain time
- [ ] Failure injection notes in the design (broker down, DB down, poison message)

---

## Interview Challenge

You own Order Service (Spring Boot, PostgreSQL) and Inventory Service. Checkout must reserve stock and emit `OrderPlaced` for Email, Fraud, and Search. Payments can timeout. Design the integration so that:

1. DB and Kafka never diverge on order creation.
2. Inventory is not double-reserved on retries.
3. Email outage cannot take down checkout.
4. Fraud can lag minutes without blocking the customer.
5. You can explain what the customer sees if Inventory is down.

Propose components, patterns, and failure UX.

---

## Suggested Answer

**Write path:** `POST /orders` with `Idempotency-Key`. In one Postgres transaction: insert order (`PENDING_RESERVATION` or `PLACED` per policy), insert outbox row `OrderPlaced` (stable `eventId`). Return `201`/`202` with order id.

**Outbox relay:** Debezium or publisher polls outbox → Kafka topic `order.events` keyed by `orderId`.

**Inventory:** Prefer sync reservation inside the checkout transaction boundary *or* a dedicated command queue with idempotent `reservationId`. If sync: timeout + circuit breaker; on Inventory down, fail checkout with clear `503`/`409` — do **not** silently place unreserved orders unless product accepts backorder workflow (then compensate via saga).

**Idempotency:** Unique `(tenantId, idempotencyKey)` on orders; unique `reservationId` / event inbox on Inventory.

**Fan-out:** Email, Fraud, Search = separate consumer groups. Email failures → retry/DLQ; checkout unaffected (bulkhead via separate apps). Fraud lag is eventual; UI does not wait. Search projection via CDC or same events — rebuildable.

**Payments timeout:** Idempotent capture with payment intent id; client retries safely; state machine ignores duplicate acks.

**Customer UX:** If Inventory down and reservation required → “Please retry”; if accepted for async reservation → “Order received, confirming stock” with polling/SSE on order status. Never show “paid” without clear payment state.

**Observability:** outbox oldest age, Kafka lag per group, Inventory breaker state, payment duplicate rate.

---

## Architecture Reflection Questions

1. Where in your systems have you seen dual-write bugs, and what pattern fixed them?
2. What is your default ordering guarantee when you say “we use Kafka”?
3. How do you explain eventual consistency to a product manager for fraud scoring lag?
4. When would you reject CDC as the integration contract between two bounded contexts?
5. Which resilience module (retry, breaker, bulkhead, rate limit) is missing most often in code you review — and what incident pattern follows?
6. How do you decide between sync HTTP and a queue for a new cross-service call?
7. What metrics would you put on an architecture review dashboard for integration health?

---

## Interview Confidence Checklist

- [ ] Can contrast queue vs bus vs pub/sub with ownership and failure semantics
- [ ] Can whiteboard transactional outbox and why dual-write fails
- [ ] Can explain CDC use cases and schema-coupling risk
- [ ] Can design idempotent consumers with inbox/unique constraints
- [ ] Can configure a sensible retry + DLQ policy verbally
- [ ] Can place circuit breaker, bulkhead, and rate limiter in a request path diagram
- [ ] Can discuss ordering boundaries without claiming global order
- [ ] Can describe observability signals for lag, DLQ, and breaker state
- [ ] Can defend sync vs async choice with UX and operability arguments
- [ ] Has at least one production story (or honest proxy) involving poison messages or dual-write

---

## Notes

<!-- Your outbox/CDC/Kafka lag/DLQ incidents and decisions -->
