# Messaging

> Asynchronous boundaries, delivery guarantees, and operational failure modes.

## Kafka

### Explanation

Kafka is a distributed commit log: producers append to partitioned topics; consumers pull by offset in consumer groups. Ordering is per partition, not global. Durability depends on replication (`acks`, `min.insync.replicas`). Retention is time/size-based — Kafka is not a database, though log compaction approximates latest-value stores. Throughput comes from sequential disk and batching; correctness comes from idempotent producers, transactional outbox, and careful consumer commit semantics.

### Why interviewers ask it

- Default backbone in Java microservice estates; panels expect failure-mode fluency.
- Distinguishes “we use Kafka” from partition, lag, and exactly-once nuance.
- Tests whether you know when Kafka is overkill.

### Production examples

- Order events topic partitioned by `orderId` for per-order sequencing.
- Consumer lag spike after bad deploy; rewind offsets for replay.
- Compacted `customer-profile` topic as broadcast state for services.
- Producer `acks=1` during broker blip → silent loss — postmortem fuel.

### Common mistakes

- Assuming global order across partitions.
- Committing offsets before side effects succeed.
- Huge partitions or single-partition topics as hidden bottlenecks.
- Unbounded retries without DLQ poisoning the partition.
- Using Kafka as synchronous RPC transport.

### Senior Engineer discussion

Key design: partition key = ordering/affinity boundary. Prefer idempotent producer + transactional outbox from DB. Consumers: process then commit (at-least-once) with idempotent handlers; or transactions if the ecosystem supports end-to-end. Monitor lag, produce error rate, ISR shrink, and request latency. Size consumer parallelism to partition count.

### Lead Engineer discussion

Platform standards for topic naming, retention, ACLs, and schema registry compatibility (FORWARD/BACKWARD). Capacity-plan partitions — increasing is easy, decreasing is not. Ban shared “god topics.” Define ownership and replay runbooks per domain. Cost: storage retention vs rebuild-from-DB strategy.

### Tradeoffs

- More partitions: parallelism vs fan-out overhead and rebalance cost.
- Long retention: easy replay vs storage cost.
- `acks=all`: durability vs latency.
- Compaction: latest-value convenience vs harder full-history analytics.

### Interview Challenge

Payments consumer must not double-charge on rebalance/retry. Design the handler.

### Suggested Answer

Idempotency key = `eventId` or `(paymentId, attempt)`. Persist “processed” in same DB transaction as ledger insert (or unique constraint on payment intent). Commit Kafka offset only after DB commit. On rebalance, duplicates hit unique constraint and no-op. DLQ poison messages after N failures.


## RabbitMQ

### Explanation

RabbitMQ is a smart broker: exchanges route to queues via bindings (direct, topic, fanout, headers). Competing consumers share a queue; acknowledgments remove messages. Prefetch controls in-flight work. It excels at task distribution, routing patterns, and shorter-lived messaging — not multi-TB replay logs. Mirrored/quorum queues address HA; classic mirrored queues have known footguns — prefer quorum queues for durability.

### Why interviewers ask it

- Still common in Spring AMQP estates alongside or instead of Kafka.
- Tests routing and ack semantics literacy.
- Compares work-queue model vs log model deliberately.

### Production examples

- Image-resize workers competing on a durable queue.
- Topic exchange `order.*` routing to billing and email queues.
- Publisher confirms + durable queues + persistent messages for at-least-once.
- Unacked message pile-up after consumer crash with high prefetch.

### Common mistakes

- Auto-ack before processing completes.
- Non-durable queues/messages for critical work.
- Unbounded prefetch → memory blowup and unfair load.
- Using Rabbit as infinite event store (no long retention/replay story).
- Blocking consumer threads on downstream HTTP without timeouts.

### Senior Engineer discussion

Declare topology as code (exchanges/queues/bindings). Use publisher confirms and consumer manual ack. Prefer quorum queues for durability. Dead-letter after retry policies. Keep payloads small; store blobs in object storage and pass references. Spring AMQP: tune concurrency and prefetch to downstream capacity.

### Lead Engineer discussion

Choose Rabbit when routing flexibility and work queues matter more than replay/compaction. Standardize cluster ops and quorum usage. If both Kafka and Rabbit exist, document which patterns belong where to stop dual sprawl.

### Tradeoffs

- Smart routing vs operational complexity of topology.
- Queue competing consumers: simple workers vs harder ordered fanout.
- Quorum durability vs latency/throughput vs classic queues.
- Broker-centric model vs Kafka’s consumer-controlled offsets/replay.

### Interview Challenge

Email sender loses messages on broker restart. What to check?

### Suggested Answer

Ensure durable queue, persistent messages, publisher confirms, and quorum/replicated storage. Consumers must ack after send success (or store outbox). Verify disk alarms and policy that messages are not transient. Add monitoring for unacked depth and confirm failures.


## SQS

### Explanation

SQS is managed queue-as-a-service: standard queues offer nearly unlimited throughput with at-least-once and best-effort ordering; FIFO queues provide per-message-group order and exactly-once processing semantics within constraints (dedup window, throughput limits). Visibility timeout hides in-flight messages; failure to delete before timeout causes redelivery. DLQ is first-class via redrive policy.

### Why interviewers ask it

- AWS-heavy enterprises expect cloud messaging literacy.
- Visibility timeout bugs are a classic production failure class.
- Tests cost/ops tradeoff vs self-managed Kafka/Rabbit.

### Production examples

- Async PDF generation: API enqueues, workers delete on success.
- FIFO with `MessageGroupId=accountId` for per-account serialization.
- Visibility timeout too short → duplicate parallel processing of same message.
- Lambda + SQS with batch item failures for partial success.

### Common mistakes

- Ignoring in-flight / visibility tuning under variable processing time.
- Using standard queue where per-key order is required.
- No DLQ → infinite poison retry burning money.
- Giant payloads (use S3 claim check).
- Assuming FIFO “exactly-once” removes need for idempotent business logic.

### Senior Engineer discussion

Set visibility ≥ p99 processing with heartbeat/extend for long jobs. Idempotent deletes and handlers. Prefer FIFO only when needed — lower throughput, higher cost complexity. Redrive to DLQ with alarms and replay tooling. Combine with SNS for fanout (SNS→SQS).

### Lead Engineer discussion

Default to managed SQS when ops headcount is the constraint and replay-from-log is not required. Gate FIFO usage. Provide shared worker libraries for extend-visibility and idempotency. Track cost by queue.

### Tradeoffs

- Standard: scale/price vs ordering/duplicates.
- FIFO: order/dedup vs throughput limits.
- Managed ops vs less control (no consumer lag log semantics like Kafka).
- Long visibility: fewer duplicates vs slower recovery after crash.

### Interview Challenge

Worker processing takes 2–15 minutes. Messages reappear and duplicate side effects. Fix.

### Suggested Answer

Extend visibility heartbeat while working; set base timeout above typical duration. Make side effects idempotent. On success delete promptly; on failure let visibility expire or route to DLQ after maxReceiveCount. Metrics on approximate receive count.


## Event Driven Architecture

### Explanation

EDA decouples producers from consumers via events representing facts (`OrderPlaced`) rather than commands to a known callee. Services update local state and publish; others project read models or trigger workflows. Success depends on contract discipline (schemas), delivery guarantees, idempotency, and clear consistency boundaries — not on bus product choice alone.

### Why interviewers ask it

- Lead/architect loops live here: coupling, ownership, and failure cascades.
- Distinguishes choreography hype from operable systems.

### Production examples

- Checkout emits `OrderPlaced`; inventory, loyalty, and analytics react independently.
- Saga choreography: each step emits next event; compensation events on failure.
- Tight coupling reintroduced via shared DB or synchronous “just checking” calls between event handlers.
- Schema change breaks three consumers — no compatibility rules.

### Common mistakes

- Fat events with entire DB rows vs thin IDs forcing chatty follow-up calls — both extremes without thought.
- Event names as commands (`DoCharge`) blurring responsibility.
- No correlation/causation IDs → undebuggable flows.
- Dual-write without outbox.
- Infinite cyclic event chains.

### Senior Engineer discussion

Model domain events from aggregates; publish after commit via outbox/CDC. Version schemas (Avro/Protobuf/JSON Schema) with compatibility checks in CI. Consumers own their projections. Prefer orchestration (workflow engine) when compensations and visibility matter more than pure choreography.

### Lead Engineer discussion

Own the event catalog and bounded-context map. Decide choreography vs orchestration per flow. Fund correlation tooling and distributed tracing. Prevent “notification topics” from becoming undocumented RPCs. Align team ownership with event producers.

### Tradeoffs

- Decoupling vs eventual consistency and harder UX.
- Choreography: autonomy vs opaque control flow.
- Orchestration: clarity vs orchestrator coupling/availability.
- Fat events: fewer callbacks vs version fragility and PII sprawl.

### Interview Challenge

Three services must complete onboarding: create user, wallet, welcome email. Compare choreography vs orchestration.

### Suggested Answer

Choreography: `UserCreated` → wallet service creates → `WalletCreated` → email service. Failure paths need compensations and are harder to visualize. Orchestration: workflow service steps through APIs/events with explicit state, retries, and timeouts — better for compliance onboarding. Use outbox at each durable step; email last and least critical.


## Pub/Sub

### Explanation

Pub/sub fans one message out to many subscribers (Kafka consumer groups are pull-based multicast; SNS, Redis Pub/Sub, Rabbit fanout are variants). Subscribers are independent; publisher should not know concrete receivers. Delivery guarantees vary wildly: Redis Pub/Sub is fire-and-forget; Kafka/SNS+SQS can be durable.

### Why interviewers ask it

- Basic pattern with large semantic gaps between implementations.
- Tests whether candidates pick durable vs ephemeral fanout correctly.

### Production examples

- SNS topic to multiple SQS queues for inventory, CRM, data lake.
- Kafka topic with three consumer groups: search indexer, fraud, metrics.
- Redis Pub/Sub for cache invalidation (loss acceptable with TTL backstop).
- Mistaken Redis Pub/Sub for billing events — lost notifications under reconnect.

### Common mistakes

- Using ephemeral pub/sub for business-critical facts.
- One subscriber’s slowness affecting others (shared queue anti-pattern).
- No per-subscriber isolation or DLQ.
- Fanout exploding cost without filtering (SNS filter policies / topic design).

### Senior Engineer discussion

Match durability to loss tolerance. Isolate subscribers with separate queues/groups. Filter at broker when possible. For invalidation channels, pair with TTL. Document subscriber SLAs so producer retention/replay covers the slowest critical consumer.

### Lead Engineer discussion

Prefer managed fanout (SNS→SQS or Kafka groups) as platform default. Ban Redis Pub/Sub for domain events. Review new subscribers for blast radius and PII.

### Tradeoffs

- Durable fanout: reliability vs cost/ops.
- Ephemeral: simplicity vs loss.
- Broker filtering: efficiency vs broker complexity.
- Many topics vs few topics with filters: discoverability vs overload.

### Interview Challenge

Cache invalidation uses Redis Pub/Sub; some pods miss messages during reconnect. Is that OK?

### Suggested Answer

Acceptable if keys have TTL and versioned reads; missed invalidation only extends staleness within TTL. Not OK for security permission revocation without short TTL or version checks. Improve with reliable bus + L1 eviction or versioned keys.


## Dead Letter Queue

### Explanation

A DLQ holds messages that exceed retry limits or fail validation — preserving poison pills so healthy traffic continues. Without DLQ, one bad payload blocks a partition/queue or burns infinite retries. DLQ is useless without ownership, alerting, inspection tooling, and a replay path.

### Why interviewers ask it

- Operational maturity signal; juniors omit DLQ, leads design the process.
- Tests poison-message and schema-evolution awareness.

### Production examples

- Kafka: error topic after N retries with original payload + headers (exception, attempts).
- SQS redrive policy to DLQ; CloudWatch alarm on `ApproximateNumberOfMessagesVisible`.
- Bad schema lands in DLQ; fix consumer; replay.
- DLQ silently filling for weeks — nobody owns it.

### Common mistakes

- Retry forever with no DLQ.
- DLQ without alerts or runbooks.
- Dropping payloads on failure “to keep lag zero.”
- Replaying DLQ blindly without fixing root cause → instant re-poison.
- Mixing transient and permanent failures in one retry policy.

### Senior Engineer discussion

Classify errors: transient (timeouts) vs permanent (validation). Retry only transient with jitter. DLQ permanent quickly. Include enough headers for triage. Build safe replay (rate-limited, idempotent). Never auto-delete DLQ without audit.

### Lead Engineer discussion

Mandate DLQ + alarm + owner per consumer. Track DLQ age as SLO. Budget time for poison triage in on-call. Schema compatibility gates reduce DLQ floods from deploys.

### Tradeoffs

- Aggressive DLQ: protect lag vs more manual triage.
- Long retries: absorb blips vs delay detection of poison.
- Shared DLQ: simple vs noisy multi-tenant triage.
- Dropping messages: lag aesthetics vs silent data loss.

### Interview Challenge

Lag is fine but business data is missing. Where do you look?

### Suggested Answer

DLQ depth/age, consumer error metrics, and “skipped” handlers. Correlate with deploy times and schema changes. Triage samples, fix consumer or producer, then controlled replay. Add alerts that page on DLQ growth, not only lag.


## Retry Strategies

### Explanation

Retries recover from transient faults; unbounded or synchronized retries amplify outages. Patterns: exponential backoff, full jitter, capped attempts, idempotent operations, and deadlines/budgets so total work respects SLOs. Distinguish retry at HTTP client, message consumer, and saga step — each needs its own policy.

### Why interviewers ask it

- Incident amplifiers are often retry storms.
- Separates resilience library name-dropping from production-safe policy design.

### Production examples

- Downstream 500s → synchronized retries from 200 pods → complete meltdown.
- Kafka consumer retry-in-place blocking partition vs retry topic with delay.
- Spring Retry/Resilience4j without jitter on checkout.
- Giving up too fast on brownouts vs hammering past deadline.

### Common mistakes

- Immediate retries with fixed delay (thundering herd).
- Retrying non-idempotent POSTs without keys.
- Infinite message retries without DLQ.
- Retrying validation errors.
- Ignoring overall deadline (retry past user/API timeout).

### Senior Engineer discussion

Default: exponential backoff + full jitter, max attempts, error classification, idempotency keys. For messaging: delayed retry topics or visibility extension, then DLQ. Propagate deadlines (`X-Request-Deadline`). Circuit breakers stop calls when dependency is down; retries alone are not protection. Load-test failure modes, not only happy path.

### Lead Engineer discussion

Standardize retry/circuit policies in platform HTTP and messaging libraries. Ban copy-paste retry loops. Make retry metrics (attempts, successes after retry, give-ups) part of service golden signals. Review policies in ADR for money paths.

### Tradeoffs

- More retries: higher success under blips vs longer tails and herd risk.
- Fail-fast: protects system vs worse user success rate.
- In-place retry: simple vs head-of-line blocking.
- Delayed retry queues: isolation vs complexity and ordering changes.

### Interview Challenge

Dependency p99 spikes from 50ms to 2s. Your service timeouts fire and retries double traffic. Stabilize.

### Suggested Answer

Cut retry count, add jitter, engage circuit breaker/bulkhead, shed load, extend timeout only within user deadline budget, and scale or degrade noncritical features. Prefer hedging only with idempotency and cancellation. Chaos-test latency injection so retries cannot amplify QPS beyond budget.
