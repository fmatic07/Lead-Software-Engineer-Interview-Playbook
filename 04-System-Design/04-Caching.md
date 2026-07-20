# Caching

> Latency and load reduction with explicit staleness and failure semantics.

## Redis

### Explanation

Redis is an in-memory data structure server used as cache, ephemeral store, lock manager, rate limiter, and sometimes lightweight queue. Durability (AOF/RDB) is configurable but not a substitute for a system of record. Cluster mode shards by hash slot; hot keys and large values dominate production pain. Spring Cache with Redis is convenience — eviction, serialization, and stampede control still need design.

### Why interviewers ask it

- Nearly every Java microservice shop uses Redis; panels probe failure modes.
- Distinguishes “put it in Redis” from capacity, TTL, and consistency thinking.
- Tests awareness that Redis is not free HA.

### Production examples

- Session tokens and OAuth state with short TTL.
- Product detail cache keyed by `product:{id}:v{version}`.
- Distributed locks with Redisson/Token bucket rate limits at API gateway.
- Redis outage: app fails open to DB and melts primary — missing degradation plan.

### Common mistakes

- Storing unbounded collections or multi-MB JSON blobs.
- No memory policy (`allkeys-lru` etc.) → OOM writes fail.
- Using `KEYS` in production; blocking commands on big datasets.
- Treating Redis persistence as durable ledger for money.
- Single hot key for global counters without sharding/stripings.

### Senior Engineer discussion

Size for working set + headroom; set `maxmemory` and eviction explicitly. Prefer hash tags carefully in cluster. Compress or slim payloads. Monitor evictions, hit ratio, blocked clients, replication lag, and hotspot commands. Failures: timeout budgets, circuit breakers, and DB protection.

### Lead Engineer discussion

Standardize Redis usage patterns (cache vs lock vs queue) so teams do not overload one cluster with mixed SLOs. Separate clusters by criticality when blast radius demands it. Require runbooks for failover and cold-cache restart. Cost reviews: memory is the bill.

### Tradeoffs

- In-memory speed vs volatility and cost.
- Cluster scale vs cross-slot transaction limits.
- AOF durability vs fsync latency.
- Shared cluster efficiency vs noisy-neighbor risk.

### Interview Challenge

Checkout reads price from Redis; after a promo update, some users still see old prices for minutes. Diagnose and harden.

### Suggested Answer

Likely long TTL + missed invalidation or versionless keys. Use short TTL with versioned keys or explicit delete on write path; publish invalidation; for money display, read-through from source on checkout confirm. Add metrics for stale-serve rate and invalidation lag.


## Cache Aside

### Explanation

Application reads cache; on miss, loads DB, populates cache. Writes update DB then invalidate (or update) cache. Simplest pattern and default for Spring services. Correctness hinges on invalidate-after-commit ordering and stampede control on popular keys.

### Why interviewers ask it

- Baseline pattern every senior must implement correctly under concurrency.
- Surfaces stampede, thundering herd, and race-after-write bugs.

### Production examples

- User profile cache: miss → DB → `SET key json EX 300`.
- After profile update: DB commit then `DEL` — concurrent reader may repopulate stale if delete races.
- Lock-per-key or singleflight around miss population under launch traffic.
- Negative caching for missing IDs to stop DB hammering.

### Common mistakes

- Populate cache before DB commit visible.
- Update cache in place with non-atomic multi-field writes.
- No TTL → permanent poison values.
- Identical TTL on all keys → synchronized expiry herd.
- Caching mutable aggregates without a coherence strategy.

### Senior Engineer discussion

Prefer invalidate-on-write over write-to-cache unless you can prove atomicity. Jitter TTLs. Use request coalescing for hot misses. Version keys (`id:vN`) when invalidation is unreliable across nodes. Measure hit ratio by keyspace, not global average.

### Lead Engineer discussion

Provide a shared cache library with stampede protection, metrics, and serialization standards. Ban ad-hoc Redis template usage for entity caches. Define which entities may be cached and max staleness per domain.

### Tradeoffs

- Simple and resilient to cache loss vs more DB load on miss.
- Invalidate-after-write: possible brief stale vs complex transactional cache update.
- Short TTL: fresher data vs lower hit ratio.
- Negative cache: protects DB vs delayed visibility of new rows.

### Interview Challenge

Under Black Friday load, DB CPU spikes whenever a celebrity product expires from cache. Fix without removing the cache.

### Suggested Answer

Coalesce misses (singleflight/lock), soft TTL with background refresh, jittered expiry, and possibly replica reads for fill. Pre-warm known hot SKUs. Cap concurrent fills per key. Alert on miss storms.


## Write Through

### Explanation

Writes go to cache and datastore together from the application’s perspective: cache is updated as part of the write path before success returns. Improves read-your-writes for cached keys but couples write latency to cache availability and requires careful failure handling if one side succeeds.

### Why interviewers ask it

- Checks whether candidates know patterns beyond cache-aside slogans.
- Probes dual-write failure handling.

### Production examples

- Session store where Redis is primary for reads and DB is backup snapshot.
- Configuration service writing to Redis + Postgres in sequence with compensating delete.
- CDN origin shields are analogous at HTTP layer, not app memory.

### Common mistakes

- Declaring write-through while only updating cache and async-writing DB.
- No rollback when cache set fails after DB commit (or vice versa).
- Assuming write-through eliminates invalidation needs in multi-key graphs.

### Senior Engineer discussion

Define success: both stores ack, or DB wins and cache is best-effort with invalidate. Prefer DB commit then cache set; on cache failure, invalidate and rely on aside. Keep payloads identical to read model to avoid skew.

### Lead Engineer discussion

Allow write-through only where read-your-writes is a product requirement and cache miss cost is high. Document failure matrix. Prefer platform-managed patterns over per-service invention.

### Tradeoffs

- Fresher cache on write path vs higher write latency and complexity.
- Stronger read-your-writes vs availability coupling to Redis.
- Simpler reads vs harder multi-key coherence.

### Interview Challenge

Write-through updates Redis successfully but DB write fails; user sees new data then it vanishes. Redesign.

### Suggested Answer

Invert: commit DB first, then set cache; on cache failure delete key. Return success only after DB commit. Optionally transactional outbox to a cache-warmer. Never treat cache as authority for durable state.


## Write Back

### Explanation

Writes hit cache (or buffer) and acknowledge early; persistence to DB happens asynchronously. Maximizes write throughput and smooths spikes; risks data loss on crash and complex reconciliation. Rarely appropriate for financial ledgers; common for metrics, counters, and ephemeral session aggregates.

### Why interviewers ask it

- Separates performance theater from durability literacy.
- Tests whether candidates know when this is unacceptable.

### Production examples

- View-count increments batched flush every N seconds.
- Game leaderboards or typing indicators.
- Write-back abandoned after node death lost unpaid cart mutations — postmortem classic.

### Common mistakes

- Using write-back for payments, entitlements, or inventory without WAL.
- No fsync/replication of the write-back buffer.
- Unbounded buffer growth under DB outage.
- Missing idempotent flush → double apply after retry.

### Senior Engineer discussion

If used, persist the write-ahead log of pending mutations, bound queue size, shed or fail writes when full, and expose lag metrics. Prefer write-behind with at-least-once flush and idempotent upserts. Document RPO explicitly.

### Lead Engineer discussion

Default deny write-back for domain data. Require ADR + RPO sign-off. Offer batching at DB layer (COPY, bulk upsert) as safer alternative for ingest spikes.

### Tradeoffs

- Write latency/throughput vs durability and complexity.
- Spike absorption vs loss window on crash.
- Operational simplicity of sync writes vs buffer machinery.

### Interview Challenge

Product wants “instant” like counters at 100k QPS. Propose a safe design.

### Suggested Answer

Redis `INCR`/`HINCRBY` for hot path with TTL or periodic snapshot; async workers flush aggregates to DB using idempotent upserts keyed by (entity, window). Accept approximate counts in UI; reconcile daily. Do not write-back authoritative balance.


## Cache Invalidation

### Explanation

Invalidation removes or versions stale entries when source data changes. Hard because of concurrent writers, multiple cache tiers, and partial failures. Strategies: TTL-only, explicit delete, pub/sub invalidation, versioned keys, and event-driven eviction via CDC.

### Why interviewers ask it

- Famous hard problem; seniors must show concrete tactics, not jokes.
- Reveals distributed systems judgment across app instances and regions.

### Production examples

- Admin updates SKU → service deletes key; other pods still serve until TTL if local Caffeine also caches.
- Redis pub/sub or Redis Streams fanout local L1 eviction.
- Version column in DB embedded in cache key; writers bump version.
- CDC from Debezium invalidates or updates read models.

### Common mistakes

- L2 Redis invalidated, L1 heap cache forgotten.
- Fire-and-forget delete without retry/metric.
- Invalidating only one of several derived keys (list + detail + search).
- Relying solely on long TTL for rapidly changing entities.

### Senior Engineer discussion

Inventory all keys derived from an entity. Prefer versioned keys for broadcast-heavy domains. For multi-tier: on write, bump version or broadcast eviction. Make invalidation idempotent and observable. Accept TTL as backstop, not sole mechanism, when freshness SLOs are tight.

### Lead Engineer discussion

Standardize key naming and invalidation events per bounded context. Avoid N independent caches of the same entity across teams — designate an owner or shared read API. Tie freshness SLOs to product commitments.

### Tradeoffs

- Short TTL: simple vs load and staleness bound only statistically.
- Explicit invalidation: fresher vs missed events leave poison.
- Versioned keys: safe reads vs storage churn.
- Pub/sub eviction: fast L1 sync vs at-most-once message loss unless logged.

### Interview Challenge

Two-tier cache (Caffeine + Redis) shows stale auth permissions after role change. Fix.

### Suggested Answer

On role change, bump `permVersion` for user; keys include version. Broadcast L1 eviction via bus/pubsub; Redis delete by key/version. Authz checks refuse mismatched version. Short TTL backstop. Audit “permission changed → enforced” latency.


## Distributed Cache

### Explanation

A shared remote cache (Redis Cluster, Hazelcast, Memcached) gives a coherent(ish) view across nodes versus per-node local memory. Consistency is still TTL/invalidation-based unless you build stronger protocols. Network hops add latency; partial cluster failures create split views and amplified DB load.

### Why interviewers ask it

- Horizontal pod scaling makes local-only cache correctness fragile.
- Tests cluster, hotspot, and failure amplification understanding.

### Production examples

- Kubernetes rolling deploy with local-only cache → inconsistent feature flags until TTL.
- Redis Cluster resharding causes brief MOVED redirects and timeouts.
- Near-cache (L1) + Redis (L2) for ultra-hot keys with invalidation channel.
- Memcached for simple blobs; Redis when structures/TTL/pubsub needed.

### Common mistakes

- Assuming distributed cache provides linearizability.
- No timeout/circuit breaker → Redis blips stall all threads (especially before virtual threads care).
- Huge connection pools per pod × many pods exhausting Redis.
- Caching non-idempotent computation results without keys tied to inputs.

### Senior Engineer discussion

Set aggressive timeouts, bulkheads, and fallback to DB with admission control. Size client pools globally. Prefer idempotent cache fills. For consistency-sensitive reads, skip cache or use version checks. Test behavior under Redis failover and slot migration.

### Lead Engineer discussion

Platform-own the Redis clusters, SLOs, and client libraries. Separate caches by domain criticality. Capacity-plan for cold start after flush. Decide L1 policy organization-wide to prevent coherence bugs.

### Tradeoffs

- Shared cache coherence vs network dependency.
- L1+L2 speed vs invalidation complexity.
- Large cluster capacity vs operational complexity.
- Fail-open to DB vs protect DB with fail-closed/degraded mode.

### Interview Challenge

After Redis failover, latency triples and DB saturates. What controls do you add?

### Suggested Answer

Client timeouts + circuit breaker; limit concurrent DB fallbacks; serve stale-if-error where safe; shed noncritical traffic; pre-warm top keys; verify connection pool stampede. Post-incident: chaos test failovers regularly.


## CDN

### Explanation

CDN caches HTTP responses at edge POPs: static assets, and carefully chosen API GETs. Cache-Control, ETag, Vary, and signed URLs define correctness. Origin shield reduces origin storms. CDNs do not understand your DB consistency — purge APIs and short TTLs are your contract.

### Why interviewers ask it

- Full-stack and platform leads own web performance; panels expect header literacy.
- Separates “CloudFront checkbox” from purge/security design.

### Production examples

- Fingerprinted JS/CSS with immutable long TTL.
- Product images with purge on replace; URL versioning preferred over emergency purge.
- Edge-cached public GET `/api/catalog` with 30s TTL; personalized GETs `Cache-Control: private, no-store`.
- Accidental caching of `Authorization` responses via mis-set `Vary` — security incident class.

### Common mistakes

- Caching authenticated responses at shared edge.
- Purge-by-path missing query-string variants.
- No immutable fingerprinting → thrash purge.
- Origin without shield → global POP stampede on expiry.
- Ignoring `Set-Cookie` making responses uncacheable unexpectedly.

### Senior Engineer discussion

Default: cache only public, versioned, or explicitly TTL’d content. Prefer versioned URLs over purge. For APIs, separate public vs private routes. Monitor origin request rate, cache hit ratio, and purge lag. Use signed cookies/URLs for gated assets.

### Lead Engineer discussion

Own cache header standards with security. Ban “CDN in front of everything” without review. Cost and purge rights belong to platform; app teams get clear contracts. Tie CDN strategy to release fingerprinting in CI.

### Tradeoffs

- Long edge TTL: fast/cheap vs purge complexity.
- Versioned assets: reliable vs build pipeline discipline.
- Edge API caching: latency wins vs personalization/security constraints.
- Aggressive purge: fresher vs origin load and purge API limits.

### Interview Challenge

Users see old logo after deploy despite CDN purge. Root cause and prevention?

### Suggested Answer

Likely browser cache, alternate URL, or purge missed hashed path. Ship content-hashed filenames with `immutable`; HTML short TTL or no-store for shell. Purge HTML entry points; never rely on purge alone for assets. Verify `Cache-Control` on both.
