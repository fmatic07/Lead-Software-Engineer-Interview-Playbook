# 03 — Java Interview

> Production-grade Java depth for Senior → Lead → Architect panels in enterprise Spring shops.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–75 minutes |
| Mix | Theory, production scenarios, debugging, performance |
| Levels | Senior (correctness + APIs), Lead (systems + tradeoffs), Architect (platform implications) |

---

## Purpose

Validate that you reason about the JVM, concurrency, memory, and APIs the way you would during an incident — not as trivia.

---

## How Interviewers Evaluate

| Level | Bar |
|-------|-----|
| Senior | Accurate mental models; knows common pitfalls; can debug with evidence |
| Lead | Connects language choices to team standards, performance budgets, failure modes |
| Architect | Language/runtime choices as organizational constraints (GC, threading, serialization, compatibility) |

---

## Common Mistakes

- Memorizing definitions without failure examples.
- Claiming "HashMap is not thread-safe" without discussing `ConcurrentHashMap`, immutability, or confinement.
- Ignoring Java 17/21 features entirely in 2026 loops.
- Performance answers without measurement methodology.

---

## Excellent Communication Techniques

- Start with the contract (happens-before, complexity, memory visibility).
- Give one production anecdote.
- State the tradeoff explicitly.
- Offer how you'd verify (JFR, async-profiler, JMH, heap dump).

---

## Confidence Tips

- Prefer depth on 8 topics over shallow coverage of 40.
- When unsure: reason from first principles aloud.

---

## Question Bank (100+)

Legend: **S** Senior · **L** Lead · **A** Architect · **T** Theory · **P** Production · **Sc** Scenario · **D** Debugging · **Pf** Performance

### Language Fundamentals (1–15)
1. S/T Equals and hashCode contract — breakage symptoms?
2. S/T `record` vs class — when not to use records?
3. S/T Sealed classes — modeling benefit?
4. S/T Checked vs unchecked exceptions — API design stance?
5. S/T `Optional` anti-patterns in domain models?
6. S/T Generics: PECS; wildcards in public APIs?
7. S/T Autoboxing pitfalls in hot loops?
8. S/T `String` immutability and interning — myths vs reality?
9. L/Sc Design a money/amount type in Java — pitfalls?
10. L/T Pattern matching for switch — migration risks?
11. A/T Binary compatibility vs source compatibility?
12. S/T `var` — where you forbid it in reviews?
13. S/T Text blocks — escaping and SQL injection misconceptions?
14. L/P API evolution with sealed hierarchies?
15. S/T `enum` with behavior vs switch — tradeoffs?

### Collections & Streams (16–30)
16. S/T `ArrayList` vs `LinkedList` — when is LinkedList ever right?
17. S/T `HashMap` resize, treeification — implications?
18. S/T `ConcurrentHashMap` sizing and compute methods?
19. S/T `TreeMap` / `NavigableMap` use cases?
20. S/T Fail-fast iterators vs concurrent collections?
21. S/T `IdentityHashMap` / `EnumMap` — when?
22. S/Pf Stream pipeline allocation costs?
23. S/T Parallel streams — when harmful?
24. L/Sc Deduplicate 50M events with bounded memory?
25. S/T `Comparator` contracts and sorting violations?
26. S/D Bug: lost updates with HashMap in request threads?
27. L/P Collection choice standards for a team?
28. S/T Unmodifiable vs immutable collections (Java 9+)?
29. S/Pf Avoiding boxing in frequency maps?
30. A/Sc Cross-service pagination consistency with unstable sort keys?

### Concurrency (31–50)
31. S/T Happens-before — volatile vs synchronized?
32. S/T `synchronized` vs `ReentrantLock` vs striped locks?
33. S/T Deadlock diagnosis approach?
34. S/T Livelock / starvation examples?
35. S/T Thread pools: `CallerRunsPolicy` meaning?
36. S/T ForkJoinPool common pool risks?
37. S/T CompletableFuture composition pitfalls?
38. S/T Virtual threads (Java 21) — pinning causes?
39. L/Sc Migrate blocking JDBC to virtual threads — checklist?
40. S/T `ThreadLocal` leaks in pools?
41. S/D CPU spin from bad CAS loop — how find?
42. S/T Double-checked locking — correct pattern today?
43. L/P Concurrency bugs that passed tests?
44. S/T `CountDownLatch` vs `CyclicBarrier` vs `Phaser`?
45. S/T Semaphore for rate limiting — limitations?
46. A/Sc Design concurrency model for a high-throughput ingest service?
47. S/T Atomic classes vs LongAdder?
48. S/Pf False sharing — what and how detect?
49. L/T Structured concurrency — why it matters for cancellation?
50. S/Sc Make a cache loader correct under thundering herd?

### JVM & Memory (51–65)
51. S/T Heap regions (generational) — brief model?
52. S/T Stack vs heap allocations (escape analysis)?
53. S/T GC choices: G1 vs ZGC — selection criteria?
54. S/D Diagnose memory leak in Spring app?
55. S/Pf High GC overhead — first metrics?
56. S/T Metaspace OOM causes?
57. S/T Direct buffers / Netty — leak symptoms?
58. L/P Heap dump analysis story?
59. S/T Object header / compressed oops impact?
60. A/Sc Capacity plan JVM for 4xlarge hosts — method?
61. S/D Native memory growing but heap stable?
62. S/T Finalization deprecated — alternatives?
63. S/T ClassLoader leaks in hot reload / containers?
64. L/Pf JIT warm-up strategies for latency-sensitive services?
65. S/T JFR events you'd enable in prod briefly?

### Performance & Debugging (66–78)
66. S/Pf Methodology before optimizing?
67. S/D Latency regression after release — triage order?
68. S/Pf Allocation rate and young GC relationship?
69. S/Sc p99 spikes every 5 minutes — hypotheses?
70. L/P Performance budget for an API — how enforce?
71. S/D Thread dump reading: BLOCKED vs WAITING?
72. S/Pf String concatenation in loops — still relevant?
73. S/T Escape analysis failing — symptoms?
74. A/Sc Define SLOs and error budgets for a Java service?
75. S/D CPU profile shows `hashCode` hot — causes?
76. S/Pf Serialization costs (JSON vs binary)?
77. L/Sc Reduce GC pauses without rewriting domain?
78. S/T JMH pitfalls (dead code elimination)?

### Java 17/21 & Modern Features (79–88)
79. S/T Virtual threads vs platform threads decision tree?
80. S/T Sequenced collections?
81. S/T Pattern matching progress since Java 16?
82. S/T `HttpClient` best practices?
83. L/Sc Adopt Java 21 in a 50-service estate — plan?
84. S/T Foreign Function & Memory API — when over JNI?
85. S/T Record patterns in nested deconstruction?
86. A/T LTS strategy for a regulated enterprise?
87. S/P Migrating away from `SecurityManager` remnants?
88. S/T Switch expressions exhaustiveness with sealed types?

### Production Scenarios (89–100+)
89. Sc Payment API intermittently returns stale balances — Java-level causes?
90. Sc Thread pool exhausted after traffic spike — design fix?
91. Sc Log volume costs explode — structured logging + cardinality?
92. Sc Random `ConcurrentModificationException` — hunt plan?
93. Sc SSL handshake latency in outbound calls?
94. Sc Clock skew affecting token expiry checks?
95. Sc Classpath conflict (`NoSuchMethodError`) in fat jar?
96. Sc Graceful shutdown dropping in-flight requests?
97. L/Sc Standardize error types across services in Java?
98. A/Sc Choose between rewriting module in Go vs optimizing Java?
99. Sc Flaky tests only in CI — concurrency/test isolation?
100. Sc Huge `toString()` on entities causing OOM in logs?
101. D Service "hangs" — distinguish deadlock, pool exhaustion, GC thrash?
102. Pf Bulk data export endpoint timing out — redesign?
103. Sc Multi-tenant noisy neighbor on shared JVM?
104. L/P Code review checklist you enforce for concurrency?
105. A/Sc Platform library for retries — API design to prevent dual retries?

---

## Full Scripts

### Script 1 — ConcurrentHashMap & Correctness

**Interviewer:** Is `ConcurrentHashMap` enough to make any composite operation thread-safe?

**Candidate:** No. Individual operations are thread-safe; check-then-act composites (`if (!map.containsKey) put`) race. Use `putIfAbsent`, `compute`, `merge`, or external locking for invariants spanning multiple keys.

**Follow-up:** How would you implement a correct single-flight cache load?

**Expected Senior:** `computeIfAbsent` with careful loader exception handling; or future-based single-flight map.  
**Expected Lead:** Also discuss stampede at scale, timeouts, metrics on load time, and avoiding lock convoys; document team standard.

**Evaluation Notes:** Fail if "CHM is always safe for everything."

---

### Script 2 — Virtual Threads Pinning

**Interviewer:** We moved to virtual threads and latency got worse under load. What do you investigate?

**Candidate:** Pinning from synchronized blocks / native frames; carrier thread starvation; huge number of concurrent blocking ops overwhelming DB pool; connection pool sized for old thread model; unintentional thread-locals bloating. I'd JFR for virtual thread pinned events, check pool sizes, and review synchronized usage on hot paths.

**Follow-up:** Do you replace all synchronized with ReentrantLock?

**Lead Answer:** Not blindly — measure pinning; prefer reducing critical section; locks help some cases; architecture may need bounded concurrency (semaphores) in front of DB.

---

### Script 3 — Memory Leak Diagnosis

**Interviewer:** Heap grows over days until OOM. Walk me through diagnosis.

**Candidate:** Confirm heap vs native. Capture heap dump (or allocation profiles). Look for retained Dominators: caches without bounds, static maps, listener registries, `ThreadLocal`, Hibernate session misuse, unbounded queues. Correlate with traffic and deploy. Fix with bounds + eviction + tests; add saturation metrics.

**Architect angle:** Platform-level cache guidelines, dump access controls in prod, playbooks for on-call.

---

### Script 4 — Equals/HashCode Production Bug

**Interviewer:** Intermittent "duplicate" entries in a `HashSet` of entities. Cause?

**Candidate:** Mutable fields used in `hashCode`/`equals` changed after insertion; or Lombok/`@Data` on JPA entities using generated IDs that flip from null→value. Prefer business keys carefully or identity for managed entities; never put managed entities in sets across flush cycles carelessly.

---

### Script 5 — Performance Methodology

**Interviewer:** This endpoint is "slow." What do you do?

**Candidate:** Define slow (p50/p99, which dependency). Reproduce with prod-like data. Trace (latency breakdown). Profile if CPU-bound; check pool waits if saturation. Optimize the proven hot spot. Add regression test/budget. Avoid guessing "add cache" first.

---

## Ideal Answer Framework (Technical)

**Define contract → Failure mode → Detection → Mitigation → Tradeoff → Verification**

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Correctness | Misleading | Mostly right | Precise contracts |
| Production sense | Textbook only | Some ops awareness | Incident-grade reasoning |
| Tradeoffs | None | Mentioned | Quantified alternatives |
| Level fit | Junior depth | Senior solid | Lead/Architect systems view |
| Communication | Rambling | Structured | Teachable clarity |

---

## Confidence Checklist

- [ ] Concurrency: HB, pools, CF, virtual threads
- [ ] Collections + stream costs
- [ ] GC + leak diagnosis path
- [ ] One live performance war story with numbers
- [ ] Java 21 features you can defend using/not using

---

## Notes

<!-- Track questions missed; link to Module 02 deep-dives -->
