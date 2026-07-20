# JVM and Memory Model

> Runtime judgment for diagnosing Java systems under production constraints.

## JVM Deep Dive

### Explanation

The JVM is a managed runtime with several independently exhaustible resources: Java heap, per-thread stacks, metaspace, code cache, direct/native buffers, mapped files, GC structures, and native allocations made by the JVM or libraries. JIT compilation converts hot bytecode into optimized machine code using runtime profiles; deoptimization restores correctness when speculative assumptions fail. Class loaders define both loading boundaries and type identity: the same class name loaded by different loaders is a different type.

The Java Memory Model (JMM) defines legal observations between threads. Program order alone does not provide cross-thread visibility. A **happens-before** edge is created by mechanisms such as monitor unlock/lock, volatile write/read, thread start/join, and safe publication through correctly initialized final fields. Atomicity, visibility, and ordering are separate properties.

### Why interviewers ask it

- Tests whether you can distinguish application, JVM, OS, and container failure domains.
- Reveals whether concurrency reasoning is based on JMM guarantees rather than timing intuition.
- Shows whether runtime flags are treated as hypotheses to validate, not folklore.

### Production examples

- A pod is OOM-killed while heap occupancy is stable: Native Memory Tracking and thread counts reveal excessive thread stacks, not a heap leak.
- A plugin deployment leaks class loaders because a global registry retains plugin classes; metaspace grows across reloads.
- A non-volatile shutdown flag works in tests but loops indefinitely after JIT optimization because no visibility edge exists.
- Startup latency increases after a rollout because code-cache pressure causes compilation throttling and deoptimization churn.

### Common mistakes

- Equating process RSS with Java heap or `OutOfMemoryError` with heap exhaustion.
- Assuming `AtomicInteger` makes a compound multi-field invariant atomic.
- Publishing mutable objects before construction completes.
- Treating `volatile` as mutual exclusion or assuming it makes `count++` atomic.
- Increasing stack, metaspace, or code-cache limits without finding the retention or workload cause.

### Follow-up questions

1. Which events establish happens-before, and what do they not guarantee?
2. How would you diagnose high RSS with low heap occupancy?
3. Why can class unloading fail after an application redeploy?
4. What evidence indicates JIT warm-up or deoptimization is affecting latency?

### Senior-level discussion

Begin with the failing boundary: request latency, allocation failure, container kill, or correctness defect. Correlate application telemetry with GC logs, JFR, `jcmd`, NMT, OS RSS/page faults, cgroup limits, thread dumps, and deployment events. Preserve evidence before restart when safe. Explain mitigation separately from root cause: reduce concurrency or roll back first; capture heap/native evidence next; then remove retention, correct publication, or revise capacity limits.

For JMM defects, define the shared state and required invariant, identify every read/write, then prove the synchronization edge. Prefer ownership, immutability, or message passing over scattered fences. A thread-safe component can still participate in a non-atomic business workflow.

### Tradeoffs

- Tiered compilation improves steady-state throughput but creates warm-up variance and more runtime complexity.
- Larger thread stacks tolerate deep call chains but reduce safe thread count and increase committed native memory.
- Class-loader isolation enables plugins and redeploys but creates lifecycle and leak risk.
- Stronger synchronization simplifies correctness but can increase contention; weaker primitives demand a proof and better stress testing.

### Best practices

- Set container-aware heap and native headroom; monitor RSS, committed memory, thread count, class count, and code cache.
- Enable low-overhead JFR continuously with retention appropriate to incident response.
- Use NMT when native growth is plausible; record the performance cost and establish a baseline.
- Encode synchronization policy in one abstraction and document invariants.
- Validate runtime changes through representative load, canaries, and rollback thresholds.

### Interview Challenge

1. A Java 21 service has a flat 3 GiB heap after GC but RSS climbs until an 8 GiB pod is killed. Describe your investigation and immediate response.
2. Two threads update and read a holder containing a `volatile boolean ready` and a non-volatile payload. Under what ordering is publication safe, and how could a refactor break it?
3. Metaspace rises after each hot deployment. How do you establish the retaining mechanism?

### Suggested Answer

1. Confirm cgroup OOM and chart heap, RSS, direct-buffer pools, thread count, mapped memory, and metaspace. Capture `jcmd VM.native_memory summary.diff`, thread dumps, JFR, and `/proc`/container evidence before restart if risk permits. Likely candidates include direct buffers, native library allocations, mmap, or thread stacks. Mitigate by rolling back or bounding the responsible concurrency/buffer use, preserving native headroom; fix lifecycle/retention and add component-level native-memory alerts.
2. Construct and populate the payload, then write `ready=true`; a reader that observes `ready` through a volatile read sees preceding writes. Writing `ready` first, replacing the volatile read with an ordinary read, or mutating the payload after publication removes or exceeds the guarantee. Prefer publishing an immutable holder through one volatile/atomic reference.
3. Compare class-loader counts across deployments, run class histograms and a heap dump, and inspect GC roots retaining old loaders—commonly threads, ThreadLocals, static registries, drivers, executors, or logging callbacks. Mitigate with a clean restart if needed, then close resources on undeploy, remove global references, verify class unloading in a redeploy test, and alert on loader/class growth.

## Memory Management

### Explanation

Memory management is capacity engineering across heap and native memory, not merely choosing `-Xmx`. Heap pressure depends on allocation rate, live-set size, object lifetime distribution, promotion, humongous allocations, and collector headroom. Native consumers include direct buffers, thread stacks, metaspace, code cache, JNI, compression/TLS libraries, and memory mappings. A leak is retained memory with no future business value; high allocation can cause severe pressure without retention.

### Why interviewers ask it

- Tests ability to separate leaks, load growth, cache policy, and allocation churn.
- Exposes whether heap dumps are interpreted using dominators and GC roots rather than largest shallow objects.
- Connects capacity limits to SLOs, deployment density, and failure isolation.

### Production examples

- An unbounded cache retains tenant graphs and grows the live set until mixed collections cannot recover space.
- Large JSON transformations create short-lived arrays, driving allocation and tail latency despite stable post-GC occupancy.
- A missing `ByteBuf.release()` leaks pooled direct memory while heap metrics look healthy.
- Thousands of platform threads consume native stacks; switching blindly to virtual threads moves the bottleneck to downstream connections.

### Common mistakes

- Taking a heap dump only after restart or enabling a dump path without sufficient disk.
- Ranking classes by shallow size and declaring the largest class the leak.
- Setting `Xmx` equal to the container limit.
- Using weak references as a substitute for an explicit cache policy.
- Retaining request objects in ThreadLocals, metrics labels, queues, or lambdas.

### Follow-up questions

1. How do allocation pressure and a memory leak differ in telemetry?
2. What do retained size, dominator tree, and GC root paths tell you?
3. How do you budget memory inside a container?
4. How would you investigate direct-memory exhaustion?

### Senior-level discussion

Establish a timeline and workload correlation. Compare after-GC occupancy and live-set slope, allocation rate, promotion, GC frequency, object age, direct pools, RSS, threads, and class count. Use JFR allocation events or async-profiler allocation mode for churn; use a heap dump and dominator/root analysis for retention. Compare two points only when workload and GC state are understood.

Mitigation must protect the service: reject or shed load, bound queues/caches, reduce batch size, disable a feature, or roll back. Increasing memory can buy evidence-gathering time but is not a root-cause fix. Prevention includes cardinality limits, ownership contracts, resource closure, memory budgets, and load tests that include soak duration and tenant skew.

### Tradeoffs

- Larger heaps reduce collection frequency but increase footprint, recovery time, and potentially pause/concurrent-cycle risk.
- Object pooling can reduce allocation for expensive resources but adds retention, contention, stale-state, and ownership bugs.
- Off-heap storage reduces heap pressure but weakens visibility, accounting, and safety.
- Heap dumps offer strong retention evidence but can pause the process and consume substantial disk/I/O.

### Best practices

- Reserve explicit native headroom and test under the same cgroup limits as production.
- Bound caches, queues, batch sizes, concurrency, and metric cardinality.
- Track allocation rate and after-GC live set, not heap-used alone.
- Make lifecycle ownership explicit for direct buffers, executors, class loaders, and ThreadLocals.
- Preconfigure safe dump/JFR locations and rehearse evidence collection.

### Interview Challenge

1. After a release, p99 latency and GC CPU double, but after-GC heap remains flat. What is your diagnosis path?
2. A heap dump shows `byte[]` as the largest class. How do you determine whether it is the cause?
3. Design a memory budget for a 4 GiB container running a high-concurrency Java service.

### Suggested Answer

1. Suspect allocation churn before retention. Correlate endpoint/feature changes with allocation rate, TLAB events, object types, CPU profiles, and GC logs using JFR or async-profiler. Roll back or disable the allocating path if SLOs are threatened; then remove copies, stream/bound payloads, or reduce temporary object graphs. Add allocation regression tests and representative load gates.
2. Arrays are payload, not ownership. Use dominators and paths to GC roots to identify the retaining structures, group by request/tenant/cache, and compare with expected live data. Validate with a second snapshot or post-eviction behavior. Fix the owner—such as an unbounded cache or queue—not the array type.
3. Start from measured native baselines and peak workload: heap plus GC headroom, metaspace/code cache, direct buffers, platform-thread stacks, JNI/native libraries, and OS margin. Do not allocate the full 4 GiB to `Xmx`; set limits and alerts per component, test peak concurrency and failure recovery in-cgroup, and define load-shedding before memory reaches the kill boundary.

## Garbage Collection

### Explanation

Collector choice follows workload objectives. G1 is a balanced default with regional, generational collection and pause targets that are goals, not guarantees. ZGC targets very low pauses using concurrent relocation and colored pointers, trading CPU/headroom and operational familiarity; Java 21 includes generational ZGC. Shenandoah also performs concurrent compaction and is distribution-dependent. Throughput collectors can be appropriate for batch workloads where pauses are acceptable.

Relevant signals are allocation rate, live-set size, object lifetime, concurrent-cycle duration, GC CPU, pause distribution, promotion/evacuation failures, humongous allocations, and headroom. Average pause time is insufficient; assess p99/p99.9 request latency and coordinated effects across replicas.

### Why interviewers ask it

- Tests workload-based decision-making rather than collector brand preference.
- Reveals understanding of GC as a CPU, memory, and latency tradeoff.
- Shows incident discipline around logs, profiles, experiments, and rollback.

### Production examples

- G1 starts concurrent marking too late under bursts, exhausting evacuation headroom and causing full GC.
- Humongous buffers fragment regions; bounding payloads and removing contiguous copies outperform flag tuning.
- ZGC reduces pauses but higher concurrent CPU competes with request processing on CPU-tight pods.
- A synchronized cache expiry causes simultaneous reclamation, allocation spikes, and cross-replica tail-latency events.

### Common mistakes

- Selecting a collector from benchmark headlines without representative workload tests.
- Tuning dozens of flags before checking allocation profiles and live-set growth.
- Treating `MaxGCPauseMillis` as an SLA.
- Ignoring warm-up, cgroup CPU throttling, NUMA, and replica-level synchronization.
- Calling every long JVM pause “GC” without checking safepoint and OS evidence.

### Follow-up questions

1. When would you choose G1 versus generational ZGC?
2. What causes evacuation failure or full GC?
3. How do humongous allocations affect regional collectors?
4. How do you prove GC is causing application tail latency?

### Senior-level discussion

Define the optimization target and constraints: throughput, pause budget, memory cost, CPU quota, startup, and operational support. Establish baseline GC logs and JFR under representative steady state, bursts, failover, and soak. Correlate pauses and concurrent CPU with application latency using timestamps; distinguish GC pauses from safepoint delays, scheduler starvation, and downstream stalls.

Prefer changing allocation and retention behavior before advanced collector tuning. Change one major variable at a time, canary it, and retain an immediate rollback. Evaluate total service economics: a low-pause collector needing more CPU or memory may still be cheaper than SLO breaches—or may reduce density unacceptably.

### Tradeoffs

- Concurrent collectors minimize pauses but consume concurrent CPU and require heap headroom.
- Throughput collectors maximize application work over time but permit long stop-the-world pauses.
- Aggressive collection initiation reduces exhaustion risk but spends more CPU.
- Larger regions reduce region metadata but change humongous thresholds and evacuation granularity.

### Best practices

- Keep unified GC logs with rotation and parse them into pause, CPU, allocation, and live-set trends.
- Use supported defaults first; document every non-default flag with hypothesis, evidence, owner, and rollback.
- Test collector changes under cgroup quotas and production-like data/lifetime distributions.
- Alert on SLO impact, full GC, allocation failure, GC CPU, and headroom—not every routine pause.
- Revalidate collector assumptions after JDK upgrades.

### Interview Challenge

1. G1 shows rare 8-second pauses during traffic bursts. Explain the evidence you need and the remediation sequence.
2. Leadership asks to move every service to ZGC because one service improved. How do you respond?
3. A collector change improves p99 pauses but worsens request p99.9. Explain plausible causes and next steps.

### Suggested Answer

1. Correlate unified GC logs, JFR, request latency, CPU throttling, allocation rate, live set, humongous allocations, and safepoints. Determine whether the event is evacuation failure, full GC, reference processing, or an unrelated safepoint. Protect SLOs via rollback/load shedding, then remove burst allocations or retention, restore headroom, and only then test focused G1 changes. Reproduce with burst and soak tests and alert on leading indicators.
2. Require per-workload objectives and evidence. Candidate low-pause services should be canaried with production-like heap, CPU quotas, allocation patterns, and failover; compare end-to-end tails, GC/application CPU, memory density, and operability. Keep G1 or throughput collectors where their economics fit. Standardize evaluation and observability, not one collector.
3. Concurrent GC may consume CPU, trigger cgroup throttling, require more barriers, or alter locality; pause metrics can improve while application scheduling worsens. Compare on-CPU/off-CPU profiles, throttling, allocation, service time, downstream latency, and coordinated omission in the load test. Roll back if the SLO regressed, then retest with adequate CPU/headroom or reject the collector for that workload.
