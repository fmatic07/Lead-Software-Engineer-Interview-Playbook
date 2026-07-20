# Java Interview Scenarios

> Practice framing ambiguous incidents as evidence-led engineering decisions.

## Interview Scenarios

### Explanation

Senior scenarios rarely have one correct API answer. Use a consistent frame:

1. **Clarify impact:** SLO/SLI, correctness risk, affected scope, timeline, and recent change.
2. **Stabilize:** rollback, shed load, isolate, degrade, or cap work without destroying essential evidence.
3. **Localize:** follow request and resource flow; compare healthy and unhealthy cohorts.
4. **Prove:** gather traces, metrics, profiles, dumps, logs, and controlled experiments.
5. **Correct:** address the first causal failure, not only downstream symptoms.
6. **Prevent:** add bounds, observability, tests, rollout guardrails, and ownership.

State assumptions and decision thresholds. Separate immediate mitigation, diagnosis, long-term correction, and verification.

### Why interviewers ask it

- Tests prioritization and communication under incomplete information.
- Combines JVM, concurrency, data structure, and architecture judgment.
- Reveals whether the candidate considers rollback, blast radius, and prevention.

### Production examples

- A Java 21 migration changes GC and virtual-thread behavior simultaneously, obscuring the cause of a p99 regression.
- A fan-out API meets median SLO but violates p99.9 during one dependency's partial outage.
- An unbounded in-memory deduplication map fixes duplicate processing but causes a slow heap leak.
- A batch optimization uses the common pool and degrades unrelated online traffic.

### Common mistakes

- Jumping to a favored root cause before establishing chronology.
- Listing tools without saying what evidence each would confirm or reject.
- Proposing larger heaps, pools, queues, or timeouts as universal fixes.
- Ignoring correctness, partial failure, cancellation, and continued work after timeout.
- Ending with “monitor it” instead of measurable validation and recurrence prevention.

### Follow-up questions

1. What would make you roll back immediately rather than continue diagnosis?
2. Which evidence must be captured before restarting?
3. How would your plan change if data correctness, not latency, were at risk?
4. What leading indicator would have detected the issue earlier?
5. How do you verify the fix under skew, burst, and dependency failure?

### Senior-level discussion

Strong answers identify the constrained resource and amplification loops: retries increase arrival rate, queues preserve obsolete work, concurrency exceeds downstream capacity, cache expiry synchronizes misses, or a leak reduces future headroom. They quantify uncertainty and choose reversible moves. They also assign observability to boundaries: request deadline, executor queue, connection acquisition, dependency attempt, allocation/GC, and container memory.

Communicate in decision order. For example: “Because correctness is intact but the p99 error budget is burning, I would stop the rollout at threshold X, canary a rollback, and preserve JFR plus three thread dumps. The comparison I need is version A versus B at equal workload.” This is stronger than an unordered catalog of JVM concepts.

### Tradeoffs

- Continuing diagnosis preserves live evidence but spends error budget and increases customer impact.
- Restarting restores capacity but destroys transient evidence and can synchronize cold-start load.
- Load shedding protects core workflows but deliberately reduces availability for lower-priority work.
- A tactical resource increase buys time but may hide an unbounded design.
- Deep instrumentation improves certainty but can add overhead or cardinality during an incident.

### Best practices

- Tie every proposed action to a hypothesis, expected signal, risk, and rollback.
- Compare good/bad versions or cohorts under equivalent workload.
- Include tail latency, errors, saturation, and correctness in acceptance criteria.
- Test recovery behavior, not just steady-state success.
- Close with an owner, measurable guardrail, and evidence that prevention works.

### Interview Challenge

1. **Tail-latency regression:** After enabling virtual threads, median latency improves 25%, p99.9 triples, database CPU is 40%, and connection acquisition time rises sharply. Lead the incident and propose the durable design.
2. **Memory ambiguity:** A pod restarts every six hours. Heap after GC grows slowly, RSS grows faster, direct-buffer metrics are incomplete, and traffic has strong tenant skew. Explain the investigation, mitigation, and proof.
3. **Concurrency correctness:** Duplicate payments appear after a timeout/retry change. The service uses `CompletableFuture`, a local deduplication map, and an idempotency key sent to the provider. Frame the likely failure modes and correction.
4. **Performance claim:** A team proposes replacing imperative aggregation with parallel streams after a laptop benchmark shows 2x throughput. The endpoint performs CPU work, cache access, and occasional remote enrichment. Decide whether and how to proceed.
5. **Architecture under outage:** A pricing provider becomes intermittently slow. Retries, circuit breakers, caches, and fallbacks already exist, yet request traffic and stale responses both increase. Describe how you reason about component ordering and system recovery.

### Suggested Answer

1. The removed platform-thread queue was an accidental bulkhead; connection wait indicates concurrency now exceeds database capacity even if DB CPU is not high—locks, I/O, session limits, or slow queries may dominate. Stop/roll back the rollout if the p99.9 budget is burning, cap DB-bound concurrency, propagate deadlines, and shed excess work. Compare query latency/plans, pool wait, active sessions, locks, virtual-thread pinning, retries, and canceled work. Keep virtual threads only with explicit per-resource bulkheads, acquisition timeouts, load tests under DB slowdown, and alerts on wait/in-flight/rejection.
2. Correlate post-GC live-set slope, allocation rate, dominators, tenant cardinality, RSS, threads, metaspace, mapped files, and native-memory diffs. Capture JFR, a safely timed heap dump, NMT, and OS/cgroup evidence before one controlled restart. Bound or disable the suspect tenant/cache path and preserve native headroom. Heap roots prove Java retention; allocation profiles identify churn; NMT/direct-library evidence localizes native growth. Fix ownership/eviction/release, then verify with skewed soak tests longer than six hours and component-level memory alerts.
3. Determine whether retries occur after the provider committed but before the response, whether the key is stable across attempts, whether provider idempotency scope/TTL matches the workflow, and whether local check-then-put is atomic and bounded. `CompletableFuture` timeout may leave the first attempt running, creating overlap. Pause or constrain retries, reconcile against the provider/ledger, and protect correctness. Use one durable idempotency record/state machine with a uniqueness constraint, stable key, explicit in-progress/outcome states, and provider reconciliation; propagate cancellation but never rely on it for exactly-once. Test crash and timeout points.
4. Reject direct rollout from the laptop result. Parallel streams use the common pool, mix blocking and CPU work, lack explicit admission/deadline control, and may interfere with unrelated tasks. Establish representative cardinality/skew, isolate pure CPU stages, and benchmark them rigorously; profile the endpoint and load-test tails. If parallelism is valuable, use owned bounded execution or separate remote orchestration, preserve ordering/error semantics, and canary against p99, CPU, allocation, common-pool saturation, and dependency load.
5. Draw the decorator/order and one request deadline. Determine whether retries occur inside or outside the breaker, whether each attempt consumes the whole timeout, whether fallback reads trigger cache refresh, and whether half-open probes synchronize. Contain by reducing retries, opening a scoped circuit, serving policy-approved stale data, and limiting refresh/probes. Redesign with retry budgets and jitter, single-flight refresh, stale-while-revalidate, idempotency, deadline propagation, and metrics for logical requests, attempts, breaker state, cache age, and fallback outcome. Validate outage and recovery to prevent a retry or cold-cache surge when the provider returns.
