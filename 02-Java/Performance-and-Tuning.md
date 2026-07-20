# Performance and Tuning

> Evidence-led optimization of end-to-end service outcomes.

## Performance Optimization

### Explanation

Performance is a workload-specific constraint problem across latency distribution, throughput, resource cost, and correctness. Optimize against an SLO and business objective, not a single microbenchmark. Queueing means utilization near capacity can disproportionately increase tail latency; service time, wait time, dependency time, and coordinated effects must be separated.

A sound method is: define the regression and baseline, reproduce or observe safely, form a hypothesis from telemetry, profile the constrained resource, make one material change, canary, and compare statistically meaningful tails and cost. CPU, allocation, locks, I/O, database plans, network, scheduler, GC, and cgroup throttling are candidate domains.

### Why interviewers ask it

- Tests scientific diagnosis under uncertainty and production pressure.
- Reveals whether the candidate protects SLOs before pursuing elegant optimizations.
- Connects JVM-level findings to architecture, capacity, and business tradeoffs.

### Production examples

- JFR shows JSON serialization dominating CPU; changing payload shape and caching encoded immutable responses beats JVM flag tuning.
- An N+1 query is invisible in average latency but saturates the connection pool at peak.
- A cache improves median latency while synchronized expiry stampedes worsen p99.9.
- CPU profiles are misleading because cgroup throttling and off-CPU connection waits are the real constraint.

### Common mistakes

- Profiling only a laptop workload or optimizing the hottest method without end-to-end impact.
- Comparing averages while ignoring tails, errors, warm-up, and throughput.
- Running JMH without consuming results, proper forks/warm-up, or realistic data distributions.
- Load testing with coordinated omission or an unrealistic closed workload.
- Changing GC, pool sizes, SQL, and code together, making causality unknowable.

### Follow-up questions

1. How do you distinguish CPU saturation from queueing or off-CPU delay?
2. When is JMH appropriate, and what can invalidate it?
3. How do you prove a cache improves the system rather than one endpoint?
4. What makes a load test representative?

### Senior-level discussion

Lead with impact and guardrails: affected SLI, scope, release correlation, error budget, and rollback threshold. Compare good and bad cohorts by version, tenant, endpoint, payload, node, and dependency. Use tracing for critical path, RED/USE metrics for service and resources, JFR for JVM evidence, async-profiler for CPU/allocation/lock or wall-clock views, and database/network tools at their own boundaries.

Benchmark rigor requires controlled environment, warm-up, multiple forks/runs, confidence ranges, result consumption, realistic input distributions, and awareness of dead-code elimination, constant folding, tiered compilation, and GC. Microbenchmarks establish local mechanism; canaries and load tests establish production value.

### Tradeoffs

- Caching trades latency and dependency load for staleness, invalidation, memory, and stampede risk.
- Batching improves throughput but adds wait latency and larger failure units.
- More concurrency hides I/O latency until it creates queueing and downstream saturation.
- Denormalization/read models accelerate reads at consistency and operational cost.
- Horizontal scaling buys time but may amplify a constrained shared dependency.

### Best practices

- Define target SLO, workload, baseline, and rollback before tuning.
- Profile in production safely with continuous JFR and controlled async-profiler use.
- Instrument queue wait separately from execution and dependency time.
- Bound caches, pools, queues, concurrency, retries, and payload size.
- Record before/after tails, throughput, errors, CPU, allocation, and cost; recheck after rollout.

### Interview Challenge

1. A release increases p99 from 180 ms to 900 ms while median and CPU remain stable. Give your investigation plan.
2. A JMH benchmark claims a stream rewrite is 40% faster. What evidence is required before shipping?
3. An in-memory cache cuts database calls by 70% but p99.9 worsens. Explain likely mechanisms and remediation.

### Suggested Answer

1. Confirm SLI validity and segment by version, endpoint, payload, tenant, node, and dependency. Compare traces for wait versus service time, connection/executor queues, locks, GC/safepoints, throttling, and downstream tails. Roll back or disable the feature if the error budget is threatened. Reproduce the skew/burst, fix the identified queue or dependency path, canary, and add a regression guard on tails.
2. Inspect forks, warm-up, result consumption, parameters, allocation, GC, CPU isolation, and confidence; verify equivalent semantics and realistic data/cardinality. A local nanosecond gain may disappear in request I/O or worsen allocation. Profile the actual service path, run representative load, compare p99/cost, and ship only if it advances the stated SLO without readability risk.
3. Suspect lock contention, expiry stampede, loader blocking, oversized live set/GC, key skew, or stale-entry refresh bursts. Correlate cache lock/load/eviction metrics and allocation with tails. Mitigate using stale-while-revalidate, single-flight, jittered expiry, bounded size, asynchronous refresh, and dependency bulkheads; validate hit quality and end-to-end tails, not hit rate alone.

## Common Production Issues

### Explanation

Recurring Java incidents are usually coupled-system failures: slow dependencies fill connection pools, retries multiply demand, executor queues retain expired work, caches synchronize refresh, memory pressure increases GC, and health checks keep an unhealthy instance in rotation. The visible exception is often downstream of the first saturation signal.

Diagnosis should follow chronology and resource flow. Determine what changed, which SLI moved first, where work queued, which resource saturated, and whether retries/timeouts/circuit breakers helped or amplified load. Preserve evidence and distinguish mitigation from root-cause correction.

### Why interviewers ask it

- Tests incident leadership, prioritization, and cross-layer reasoning.
- Reveals ability to interpret incomplete evidence without anchoring on the loudest symptom.
- Shows whether prevention addresses systemic amplification and operability.

### Production examples

- Connection leak manifests as request timeouts; pool active count reaches max while acquisition wait rises.
- Retry policies at HTTP client, service, and gateway multiply traffic during a dependency outage.
- Metric label cardinality consumes heap and destabilizes the telemetry pipeline during the incident.
- A blocking DNS or TLS issue appears as “thread-pool exhaustion.”
- A deployment is healthy at startup but JIT warm-up and cache coldness cause overload after traffic shift.

### Common mistakes

- Restarting every instance simultaneously and destroying evidence/caches.
- Increasing pools and timeouts, allowing more work to accumulate.
- Taking a heap dump or heavy profile on all unhealthy replicas.
- Treating circuit breakers as substitutes for capacity, deadlines, or dependency health.
- Declaring resolution after metrics recover without proving trigger and prevention.

### Follow-up questions

1. What metrics identify connection-pool exhaustion versus a leak?
2. How do retries interact with deadlines and circuit breakers?
3. What is your evidence order during a live incident?
4. How do you diagnose a process that is alive but making no progress?

### Senior-level discussion

Run two tracks: stabilize and investigate. Stabilization may roll back, shed optional work, reduce retries, isolate a tenant, open a circuit, or scale only when the shared dependency can absorb it. Investigation builds a timestamped narrative from deploy/config events, SLI changes, traces, queue/pool metrics, JFR, repeated thread dumps, GC logs, OS/cgroup data, and dependency telemetry.

For leaks, compare checked-out resource age and acquisition stacks, not merely pool utilization. For stalls, take multiple thread dumps and inspect progress counters; one snapshot cannot distinguish slow from stuck. For latency, include coordinated timeout behavior and canceled work that continues consuming resources. Prevention should add leading indicators, bounded queues, deadline propagation, failure drills, and rollback automation.

### Tradeoffs

- Longer timeouts may rescue slow requests but consume capacity and violate upstream deadlines.
- Retries improve transient success but amplify overload and tails.
- Circuit breakers reduce wasted calls but can create synchronized probing and fallback load.
- Scaling improves local headroom but can overwhelm databases, caches, and control planes.
- Detailed observability improves diagnosis but adds cost, cardinality, and data-governance concerns.

### Best practices

- Use one end-to-end deadline and budget retries within it.
- Monitor queue age, pool acquisition wait, in-flight work, cancellation, and retry amplification.
- Keep health/readiness checks tied to ability to serve, without making them dependency cascades.
- Rehearse rollback, evidence capture, degraded modes, and load shedding.
- Write post-incident actions around detection, containment, correction, and recurrence prevention.

### Interview Challenge

1. All request threads wait for database connections, the database reports normal CPU, and restarts help for ten minutes. Diagnose the incident.
2. A dependency slowdown causes traffic to triple although client request rate is unchanged. Explain and contain it.
3. Pods are “healthy,” CPU is low, heap is normal, but throughput approaches zero. What evidence do you gather?

### Suggested Answer

1. Check pool active/idle/waiters, checkout age, transaction duration, abandoned connections, database session/lock state, and traces. Temporary recovery suggests leaked or long-held connections, not necessarily DB CPU. Roll back/disable the suspect path, reduce admission, and selectively restart after evidence. Fix resource/transaction lifecycle, add acquisition and statement deadlines, leak detection, checkout-age alerts, and failure-path tests.
2. Layered or immediate retries are multiplying attempts while slow calls remain in flight. Disable/reduce retries, enforce an end-to-end deadline, shed load, open a scoped circuit, and protect the dependency with a bulkhead. Redesign with capped attempts, exponential backoff and jitter, idempotency, retry budgets, and metrics for original requests versus attempts.
3. Gather repeated thread dumps, JFR wall-clock/lock events, executor and connection queue age, downstream/DNS/TLS latency, socket states, safepoints, cgroup throttling, and progress counters. Low CPU suggests blocking, deadlock, unavailable permits, or external wait. Restore service via rollback/isolation/restart after evidence, then correct the blocked lifecycle and add a readiness/progress signal that detects this failure mode.
