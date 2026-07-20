# Collections and Streams

> API semantics, resource bounds, and performance judgment for production data paths.

## Collections

### Explanation

Collection selection starts with semantics: ordering, uniqueness, key equality, mutation, concurrency, null policy, range queries, and iteration consistency. Complexity notation is only a starting point; locality, allocation, resizing, collision behavior, comparison cost, and workload distribution determine production behavior.

`ArrayList` is usually preferable for indexed and sequential access; linked structures rarely compensate for poor locality and per-node overhead. `HashMap` requires stable, consistent `equals`/`hashCode`; `TreeMap` provides sorted/range operations at comparison cost. `ConcurrentHashMap` provides thread-safe individual operations and weakly consistent iteration, not transactional multi-key invariants. Java immutable factories reject nulls and prevent mutation, but do not deep-freeze elements.

### Why interviewers ask it

- Tests whether data structures encode domain invariants rather than convenience.
- Reveals understanding of equality, mutability, iteration, and concurrency failure modes.
- Connects algorithm choice to memory footprint, latency distribution, and bounded capacity.

### Production examples

- Mutable fields used in a hash key make entries unreachable and deduplication silently fail.
- An access-ordered `LinkedHashMap` implements a local LRU, but synchronized eviction becomes a hotspot; a policy-aware cache is safer.
- A `CopyOnWriteArrayList` is excellent for tiny, read-mostly listener sets and disastrous for frequent writes.
- A `ConcurrentHashMap.computeIfAbsent` loader blocks a hot key, amplifying downstream latency and retry storms.

### Common mistakes

- Choosing by nominal Big-O while ignoring iteration frequency, cache locality, and object overhead.
- Returning an unmodifiable view while the backing collection remains mutable.
- Assuming concurrent collections make compound business operations atomic.
- Using user-controlled or high-cardinality keys without size/expiry limits.
- Implementing `equals` and ordering inconsistently, causing sorted sets/maps to lose entries.

### Follow-up questions

1. When is `ConcurrentHashMap.computeIfAbsent` unsafe operationally?
2. What is the difference between immutable, unmodifiable, and defensively copied?
3. How do hash flooding and poor key distribution affect latency?
4. How would you maintain a cross-key invariant?

### Senior-level discussion

State access patterns quantitatively: cardinality, read/write ratio, mutation ownership, iteration/range needs, contention, key skew, and memory budget. Then select semantics and validate with representative benchmarks or profiles. For shared state, identify the atomicity boundary; use single-owner state, immutable snapshots, explicit locking, database constraints, or a transactional component when an individual concurrent collection is insufficient.

Operational design includes bounds and observability. Expose size, eviction, hit rate, load duration/failures, key skew, and contention without emitting unbounded key labels. Plan for cache stampedes and stale-data behavior. Migration must preserve equality and iteration assumptions, not just compile.

### Tradeoffs

- Hash structures offer expected constant-time access but no order and can consume substantial spare capacity.
- Sorted structures provide deterministic order/range queries but pay comparison and node overhead.
- Immutable snapshots simplify readers but cost copying and temporary memory.
- Lock-free/concurrent structures improve progress for some workloads but often weaken snapshot semantics.

### Best practices

- Encode stable value-based keys; test equality/hash/ordering contracts.
- Pre-size only from credible cardinality estimates; always enforce a maximum where input can grow.
- Prefer immutable return values and explicit ownership at boundaries.
- Use atomic map methods only for small, bounded, side-effect-controlled computations.
- Benchmark realistic cardinality, skew, mutation, and concurrency; inspect allocation as well as throughput.

### Interview Challenge

1. A tenant cache built with `ConcurrentHashMap.computeIfAbsent` causes latency spikes during an identity-provider slowdown. Diagnose and redesign it.
2. A `TreeSet` occasionally drops distinct customer records. What contract failure do you suspect?
3. You need a frequently read routing table updated every minute as one coherent version. Choose a collection/concurrency design.

### Suggested Answer

1. Measure loader duration, hot-key contention, map growth, downstream saturation, retries, and timeouts. The mapping function can block callers and duplicate loads may still occur after failures/removals; an unbounded map also leaks tenants. Mitigate with timeouts, bulkheads, stale fallback, and load shedding. Use a bounded cache with expiry, single-flight/asynchronous loading, negative-cache policy, and explicit failure metrics; prevent retry synchronization.
2. `TreeSet` uniqueness is defined by `compare(a,b)==0`, not `equals`. A comparator that ignores a distinguishing field, is inconsistent with equals, or depends on mutable fields collapses records. Confirm with contract tests and sampled records, restore a stable total order or use a hash set keyed by domain identity, and add invariant tests.
3. Build a validated immutable map off-thread and publish it through one volatile/atomic reference. Readers get lock-free coherent snapshots; failed refreshes retain the last good version. The cost is copy/build memory and delayed reclamation. Add version, age, refresh latency/failure metrics, size limits, and rollback to the prior snapshot.

## Streams

### Explanation

Streams express lazy data transformations over a source; they are not collections and generally support one traversal. Their value is compositional clarity when operations are stateless and side-effect-free. Stateful operations such as sorting and distinctness may buffer data. Encounter order and collector characteristics affect correctness and parallelism.

Parallel streams use the `ForkJoinPool.commonPool()` by default. They are suitable only when work is sufficiently large, CPU-bound, splittable, independent, and benchmarked. Blocking I/O, shared mutation, nested parallelism, request-context assumptions, or common-pool competition make them operationally risky.

### Why interviewers ask it

- Tests whether declarative code preserves correctness and debuggability.
- Reveals awareness of hidden buffering, allocation, pool usage, and ordering.
- Shows judgment about when a loop or dedicated concurrency design is clearer.

### Production examples

- A parallel stream performs HTTP calls and starves unrelated common-pool tasks.
- `groupingBy` over unbounded cardinality creates a large map and triggers GC pressure.
- Side effects in `map` produce duplicate writes when pipelines are refactored or retried.
- A “clean” pipeline materializes several intermediate collections and worsens p99 latency.

### Common mistakes

- Using `parallel()` as a performance switch without measuring end-to-end behavior.
- Mutating shared collections from stream operations.
- Assuming `peek` is a reliable audit or business side-effect mechanism.
- Reusing consumed streams or returning streams backed by already-closed resources.
- Hiding checked failures, cancellation, or partial progress inside lambdas.

### Follow-up questions

1. What determines whether a spliterator parallelizes effectively?
2. Why is blocking I/O in a parallel stream hazardous?
3. When do `groupingByConcurrent` and `unordered()` help?
4. How do you make partial failure explicit in a pipeline?

### Senior-level discussion

Start with semantics: ordering, duplicate handling, failure behavior, maximum input, cancellation, and ownership of side effects. For expensive pipelines, profile allocation and CPU, and compare against an imperative baseline using JMH only for isolated computation and load tests for service effects. Keep I/O orchestration explicit so deadlines, concurrency limits, retries, and observability are visible.

If parallelism is required, own the executor or use structured concurrency where appropriate rather than depending on a global pool. Bound in-flight work against downstream capacity. Include request deadline propagation and define whether partial results are acceptable.

### Tradeoffs

- Streams improve local composition but can obscure control flow, exceptions, and allocation.
- Ordered parallel operations preserve semantics at synchronization/buffering cost.
- Custom collectors can avoid intermediates but are easy to make non-associative or unsafe.
- Materialization simplifies lifecycle and repeated access but consumes memory.

### Best practices

- Keep transformations pure; isolate side effects in explicit terminal orchestration.
- Avoid parallel streams in request-serving code unless the common-pool impact is understood and measured.
- Bound source cardinality before sorting, grouping, or collecting.
- Use primitive streams where boxing is proven material, not reflexively.
- Test collector associativity and sequential/parallel equivalence.

### Interview Challenge

1. A developer changes a sequential stream of remote calls to `parallelStream()` and median latency improves while p99 and error rate regress. Explain and redesign.
2. A collector works sequentially but loses records in parallel. What properties must you inspect?
3. Review a pipeline that sorts, groups, and retains millions of events. What production questions come first?

### Suggested Answer

1. Median improvement hides uncontrolled common-pool concurrency, downstream saturation, queueing, synchronized retries, and lack of deadline/cancellation control. Roll back to protect the SLO. Replace it with bounded, observable asynchronous or virtual-thread orchestration sized to the downstream limit, propagate timeouts/cancellation, define partial failure, and load-test tails and dependency health.
2. Verify that supplier creates independent containers, accumulator is safe for its container, combiner merges without loss, and operations are associative with a correct identity. Check `CONCURRENT`, `UNORDERED`, and `IDENTITY_FINISH` claims plus source encounter order. Add randomized sequential-versus-parallel equivalence tests; prefer a standard collector if possible.
3. Establish maximum cardinality, windowing, required order, group skew, retention lifetime, memory budget, and whether exact results are necessary. Sorting and grouping are stateful and can retain the full input. Mitigate with upstream limits, chunking/windowing, external sort or database aggregation, spill-to-disk, and backpressure; observe input size, group distribution, allocation, GC, and completion latency.
