# Concurrency and Threading

> Correctness, capacity, and failure control under real production load.

## Concurrency

### Explanation

Concurrency is coordination among overlapping tasks; parallelism is simultaneous execution. Correctness requires explicit invariants and Java Memory Model guarantees. Happens-before provides visibility and ordering, while locks can also provide mutual exclusion. Atomic variables safely update one state transition, but do not automatically protect multi-variable invariants.

Progress properties matter: deadlock prevents progress through cyclic waiting; livelock consumes effort without progress; starvation indefinitely denies a participant. Lock-free does not mean contention-free, fair, simple, or faster.

### Why interviewers ask it

- Exposes whether candidates can prove correctness rather than rely on tests or timing.
- Tests design of atomic boundaries, contention control, and failure behavior.
- Distinguishes library familiarity from production incident ownership.

### Production examples

- Inventory is oversold because “check then decrement” spans separate atomic operations.
- Opposite lock ordering deadlocks transfer requests under rare contention.
- Optimistic retries synchronize under load and create livelock-like CPU spikes.
- A read/write lock under a read-heavy workload starves configuration updates.

### Common mistakes

- Confusing thread-safe containers with atomic workflows.
- Locking on publicly reachable or mutable objects.
- Holding locks during network, disk, logging, or callback execution.
- Adding retries without limits, jitter, idempotency, or a contention budget.
- Using sleep-based tests as evidence of race freedom.

### Follow-up questions

1. How do you prove a compound operation is atomic?
2. When would ownership/message passing beat shared-state locking?
3. How do fairness and throughput conflict?
4. What evidence differentiates deadlock, starvation, and slow downstream I/O?

### Senior-level discussion

Define the invariant, synchronization domain, and linearization point. Minimize shared mutable state; partition by key or publish immutable snapshots where possible. If locking is necessary, define ordering, scope, timeout policy, and behavior after interruption. Use thread dumps, JFR lock events, blocked-time metrics, queue depth, and progress counters during incidents.

Mitigation prioritizes recovery: shed load, disable contended work, roll back, or restart only after capturing dumps when safe. Prevention uses deterministic concurrency tests where possible, stress tools such as jcstress for JMM-sensitive components, static lock-order conventions, and load tests with skew.

### Tradeoffs

- Coarse locks simplify proofs but reduce concurrency; fine-grained locks increase state-space and deadlock risk.
- Optimistic concurrency performs well under low contention but wastes work and amplifies tails under high contention.
- Fair locks reduce starvation but generally lower throughput.
- Lock-free structures can improve progress but increase complexity, retries, and reclamation challenges.

### Best practices

- Document invariants and the mechanism that protects each one.
- Keep critical sections bounded and side-effect-free.
- Use immutable values and single-owner state by default.
- Make retries bounded, observable, jittered, and deadline-aware.
- Capture multiple thread dumps during stalls to distinguish persistent blocking from transient contention.

### Interview Challenge

1. A supposedly thread-safe wallet uses two `AtomicLong` balances and occasionally loses money during transfers. Diagnose and redesign.
2. CPU reaches 100%, throughput collapses, and thread dumps show no blocked threads. How do you investigate concurrency causes?
3. How would you safely update a multi-tenant ruleset while requests continue?

### Suggested Answer

1. Individual atomic updates do not make debit-plus-credit one atomic invariant; failure between operations also breaks conservation. Establish a transaction/linearization boundary using database transactions with constraints, single-owner partitioning, or ordered locking with durable recovery. Mitigate by disabling transfers/reconciling the ledger, then add invariant monitoring and fault/concurrency tests.
2. Use JFR and on/off-CPU profiles, progress counters, retry/CAS-failure metrics, queue depth, and repeated dumps. Suspect spin loops, optimistic retry storms, work stealing, serialization contention, or synchronized retries. Shed load or disable the hot path, then cap attempts, add backoff/jitter, reduce shared hotspots, and test with realistic key skew.
3. Build and validate an immutable version off-thread, then atomically publish one reference. Requests retain a coherent version; failed builds leave the current version active. Bound old-version lifetime, track version/age and refresh errors, support rapid rollback, and use stronger coordination only if cross-node activation must be simultaneous.

## Threading

### Explanation

Platform threads are OS-backed and costly enough that pool sizing matters. Java 21 virtual threads are lightweight JVM-scheduled threads designed for high-concurrency blocking-style I/O. They increase feasible task concurrency but do not increase CPU, database connections, memory bandwidth, or downstream capacity.

A virtual thread normally unmounts from its carrier while blocking in supported JDK operations. It can be **pinned**, notably while blocking inside a `synchronized` region or native/foreign call, tying up the carrier and reducing scalability. ThreadLocal values remain possible but become expensive when multiplied across very many virtual threads.

### Why interviewers ask it

- Tests whether Loom is understood as a concurrency model change, not free throughput.
- Reveals awareness of pinning, resource limits, context propagation, and diagnostics.
- Connects thread design to downstream protection and cancellation.

### Production examples

- Migrating request work to virtual threads removes pool queueing but saturates the database connection pool.
- A legacy synchronized client blocks during I/O, pins carriers, and creates burst latency.
- Large ThreadLocal caches multiply memory usage across hundreds of thousands of virtual threads.
- CPU-bound work on virtual threads still saturates cores and needs explicit admission control.

### Common mistakes

- Pooling virtual threads as though they were scarce platform threads.
- Removing all concurrency limits after migration.
- Assuming every blocking library unmounts cleanly.
- Using thread identity or ThreadLocals as durable request state.
- Ignoring cancellation and interrupt handling because tasks are cheap.

### Follow-up questions

1. What causes virtual-thread pinning, and how do you observe it?
2. Where should concurrency limits live in a virtual-thread design?
3. When are platform threads still appropriate?
4. How do virtual threads interact with connection pools and rate limits?

### Senior-level discussion

Adopt virtual threads when the workload is predominantly blocking I/O and the programming model benefits from one task per thread. Inventory synchronized/native blocking, ThreadLocal use, library compatibility, and downstream capacity. Observe JFR virtual-thread pinned events, carrier utilization, task concurrency, dependency queues, connection waits, and end-to-end tails.

Use semaphores, connection pools, rate limits, and admission control to express scarce-resource capacity. Propagate deadlines and interruption. Canary migration per endpoint and retain rollback; compare throughput, latency, CPU, memory, failure rate, and operational debuggability rather than thread count alone.

### Tradeoffs

- Virtual threads simplify blocking code and stack traces but may expose downstream bottlenecks sooner.
- Asynchronous/reactive designs can provide explicit backpressure and fewer stacks but increase cognitive and context-propagation cost.
- `synchronized` is simple and optimized, but blocking inside it can pin virtual threads; `ReentrantLock` may avoid this at added complexity.
- ThreadLocal context is convenient but has lifecycle, memory, and observability costs at large concurrency.

### Best practices

- Use one virtual thread per independent blocking task; do not create a virtual-thread pool.
- Bound access to every scarce downstream resource.
- Keep blocking I/O outside `synchronized` blocks and profile pinning before broad rewrites.
- Make interruption cooperative and test cancellation paths.
- Load-test with real connection limits, dependency latency, and cgroup CPU quotas.

### Interview Challenge

1. After migrating to virtual threads, thread-pool queue latency disappears but database timeouts surge. What happened and what do you change?
2. A virtual-thread service periodically stalls with low CPU. How do you test for pinning?
3. Choose between virtual threads and a reactive pipeline for a new aggregation service.

### Suggested Answer

1. The old platform pool accidentally limited concurrency; virtual threads released that throttle and overwhelmed the fixed database capacity. Restore admission control with a semaphore/bulkhead aligned to connection and DB limits, enforce deadlines, shed excess load, and tune queries/pool only from evidence. Add wait-time, in-flight, saturation, and rejection metrics.
2. Correlate JFR pinned-thread events and thread dumps with carrier utilization and synchronized/native stack frames; reproduce under dependency delay. Mitigate by limiting affected concurrency or rolling back. Move blocking outside synchronized regions, replace the lock only where justified, upgrade incompatible libraries, and retain pinning observability in performance tests.
3. Prefer virtual threads for straightforward blocking clients, imperative control flow, and team operability; prefer reactive when end-to-end nonblocking APIs, streaming, and explicit demand/backpressure dominate. Prototype the riskiest path and compare tails, resource bounds, cancellation, context propagation, and debugging. Either choice still needs downstream bulkheads and SLO evidence.

## Executors

### Explanation

An executor is both a scheduling mechanism and a capacity boundary. Pool size, queue type, rejection policy, task duration, blocking ratio, priority, and lifecycle form one design. An unbounded queue converts overload into latency and memory growth; a large pool converts it into contention and downstream overload.

CPU pools are generally tied to available processors and measured compute behavior. Blocking workloads need capacity derived from arrival rate, service time, downstream limits, and memory—not a memorized formula. Virtual-thread-per-task executors remove platform-thread scarcity but still require resource admission control.

### Why interviewers ask it

- Tests overload behavior and bounded-resource design.
- Reveals whether executors are owned, observable, and shut down correctly.
- Exposes ForkJoin common-pool and hidden-executor hazards.

### Production examples

- An unbounded `LinkedBlockingQueue` grows during a dependency outage and OOMs after requests have already timed out.
- Two task classes share a pool; slow report generation starves latency-critical callbacks.
- Libraries and parallel streams compete in the ForkJoin common pool.
- A per-request executor leaks platform threads and prevents graceful shutdown.

### Common mistakes

- Treating pool size independently from queue and rejection policy.
- Submitting tasks after their request deadline or retaining timed-out queued work.
- Swallowing task exceptions from `submit`.
- Forgetting shutdown, await-termination, and forced-cancellation behavior.
- Using `CallerRunsPolicy` without considering event-loop or request-thread latency.

### Follow-up questions

1. How do you choose queue capacity and rejection behavior?
2. When should workloads use separate executors?
3. What are common-pool hazards?
4. How should graceful shutdown handle queued and running tasks?

### Senior-level discussion

Model overload explicitly. Queue capacity should be derived from acceptable wait and throughput, with deadlines checked before execution. Rejection can fail fast, shed optional work, or apply controlled caller backpressure; it must not silently discard critical tasks. Separate pools/bulkheads where workloads have different SLOs, blocking behavior, or failure domains.

Instrument active threads, queue depth/age, submissions, completions, rejections, task duration, exceptions, cancellations, and shutdown progress. During saturation, identify whether arrival rate, service time, or lost capacity changed. Mitigate through shedding, feature disablement, rollback, or dependency isolation before resizing.

### Tradeoffs

- Bounded queues expose overload but require a product-level rejection policy.
- Dedicated executors isolate failures but consume resources and add configuration.
- Work-stealing balances fine-grained CPU tasks but provides weak isolation and surprises with blocking tasks.
- Caller-runs slows producers but can contaminate latency-sensitive or event-loop threads.

### Best practices

- Give executors explicit owners, names, workload purpose, bounds, metrics, and shutdown hooks.
- Avoid the common pool for blocking or isolation-sensitive production work.
- Propagate deadlines/cancellation and remove obsolete queued work where feasible.
- Size from measurements and downstream capacity; validate under overload and recovery.
- Surface task failures whether using `execute`, `submit`, or framework abstractions.

### Interview Challenge

1. A service queue has 100,000 tasks, request clients have timed out, and workers are still processing stale work. Give an incident and redesign plan.
2. When is `CallerRunsPolicy` dangerous?
3. Design executor isolation for API requests, audit events, and batch exports.

### Suggested Answer

1. Shed new work, cancel/remove expired queued tasks if safe, disable the producer or roll back, and protect the dependency. Capture queue age, arrival/completion rates, task duration, pool utilization, and downstream latency. Replace the unbounded queue with capacity based on the wait budget, reject before deadlines, propagate cancellation, and test overload/recovery. Add queue-age and rejection SLO alerts.
2. It executes on the submitting thread. That may be useful producer throttling, but it can block an event loop, hold a lock, delay a request, create reentrancy, or deadlock if submission occurs inside the same constrained workflow. Use only with known submitter context and bounded task time; otherwise fail fast or route to explicit admission control.
3. API work gets a tightly bounded, latency-observed boundary aligned to dependencies. Audit delivery uses a durable broker/outbox rather than relying on an in-memory executor. Batch exports use a separate low-priority bounded executor with quotas and cancellation. Define shutdown/drain and rejection semantics per class so batch or audit failure cannot consume API capacity.

## CompletableFuture

### Explanation

`CompletableFuture` represents completion and composition, but does not inherently provide structured ownership, deadlines, cancellation propagation, or backpressure. Non-`Async` continuations may run on the thread completing the prior stage; `Async` methods without an executor use the ForkJoin common pool. Exceptions are wrapped and can be transformed or accidentally hidden depending on `handle`, `exceptionally`, and `whenComplete`.

`cancel(true)` marks the future cancelled but does not reliably interrupt arbitrary underlying work. `orTimeout` completes the stage exceptionally; it does not guarantee that remote calls or supplier work stop. Composition must therefore integrate cancellable clients and explicit resource bounds.

### Why interviewers ask it

- Tests asynchronous composition, failure semantics, and execution-context control.
- Reveals understanding of common-pool hazards and orphaned work.
- Shows whether timeout, cancellation, and partial-result policy are designed end to end.

### Production examples

- A timed-out fan-out request leaves dozens of HTTP calls running and exhausts connections.
- `thenApplyAsync` defaults to the common pool, where blocking callbacks starve unrelated tasks.
- `allOf` waits for every branch although one mandatory dependency has already failed.
- An `exceptionally` fallback converts serious errors into plausible empty data and masks an outage.

### Common mistakes

- Confusing `thenApply` with `thenCompose` and creating nested futures.
- Omitting an explicit executor for blocking async work.
- Calling `join()` on a constrained pool task and causing starvation.
- Treating stage timeout as cancellation of underlying work.
- Losing correlation/context or recording errors only at the final stage.

### Follow-up questions

1. Which thread executes each continuation?
2. How do you implement fail-fast fan-out with optional dependencies?
3. Why does cancellation often fail to stop work?
4. When would structured concurrency or virtual threads be clearer?

### Senior-level discussion

Define a request-wide deadline, branch budgets, mandatory versus optional results, concurrency cap, and cancellation behavior before composing stages. Use explicit executors by workload and instrument branch latency, timeout, cancellation, executor saturation, and orphaned work. Ensure downstream clients accept deadlines and can abort I/O.

Java 21 virtual threads can make fan-out ownership and stack traces simpler; structured concurrency is preview in Java 21 and requires a deliberate preview-feature policy. `CompletableFuture` remains useful at asynchronous API boundaries and for nonblocking completion graphs, but complexity rises quickly when lifecycle and partial failure are implicit.

### Tradeoffs

- Async graphs enable overlap but make ownership, context, and debugging less obvious.
- Fail-fast saves capacity but may discard usable partial results.
- Fallbacks improve availability but can violate freshness or correctness.
- Dedicated executors isolate work but require capacity and lifecycle management.

### Best practices

- Pass explicit executors for blocking or isolation-sensitive stages.
- Propagate one deadline to clients; distinguish stage completion from actual work cancellation.
- Keep exception policy typed and visible; never silently convert all failures to empty values.
- Bound fan-out and define mandatory/optional branch behavior.
- Prefer simpler synchronous virtual-thread code when it provides equivalent concurrency and clearer ownership.

### Interview Challenge

1. Design a 20-way aggregation with a 300 ms SLO, three mandatory dependencies, and optional enrichment.
2. A future times out at 300 ms, but dependency traffic continues for seconds. Explain and fix it.
3. Production shows intermittent common-pool starvation. How do you find and remove the cause?

### Suggested Answer

1. Derive a request deadline and branch budgets, cap in-flight calls, use explicit isolation, and fail fast when a mandatory branch makes success impossible. Optional branches may return clearly marked stale/absent data only within product policy. Cancel unfinished work through clients that support abort, preserve partial diagnostics, and observe each branch plus aggregate p99 and saturation. Load-test dependency slowness and retry interaction.
2. `orTimeout`/future cancellation changes completion state but may not interrupt the supplier or socket. Pass deadlines into the HTTP/database client, retain cancellable handles, cancel sibling work, and ensure interruption closes or aborts I/O. Mitigate current impact with bulkheads and connection limits; track post-timeout in-flight work to prove the fix.
3. Capture JFR, common-pool metrics, thread dumps, and async stack context; search for parallel streams, default `*Async` stages, blocking joins, and blocking I/O. Move blocking/isolation-sensitive tasks to owned bounded executors or virtual threads, eliminate nested blocking, then load-test saturation. Add checks and metrics so new default-common-pool use is visible.
