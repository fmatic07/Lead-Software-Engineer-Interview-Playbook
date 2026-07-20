# 09 — Live Coding Interview

> Communicate while coding — enterprise panels care about process, edge cases, and testability as much as the final snippet.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–70 minutes |
| Languages | Java preferred; sometimes language-agnostic |
| Styles | Algorithms lite, API implementation, debugging existing code, pair refactor |

---

## Purpose

Show how you think with a collaborator: clarify, decompose, implement, test, optimize — under timeboxes.

---

## How Interviewers Evaluate

| Axis | Strong signal |
|------|---------------|
| Clarification | Asks about inputs, scale, constraints |
| Structure | Decomposes before typing |
| Correctness | Handles edges; tests mentally/on board |
| Communication | Narrates intent, not every keystroke |
| Adaptation | Responds to hints without ego |
| Quality | Readable names; small functions |

---

## Common Mistakes

- Silent coding for 10 minutes.
- Optimizing before a correct brute force (when asked for working solution first).
- Ignoring null/empty/overflow.
- Arguing with the interviewer.
- Giving up verbally ("I'm bad at algorithms") — panels hear that.

---

## Excellent Communication Techniques

1. Restate problem + examples.
2. Confirm allowed libraries / Java version.
3. Outline approach + complexity.
4. Implement in slices; verify each.
5. Call out tradeoffs if time for optimize.

**Thinking out loud template:** "I'll track X with a HashMap because… Edge case: empty input returns… I'll write a helper for…"

---

## Confidence Tips

- Practice Java collections fluency cold.
- Prefer clear O(n log n) you can finish over unfinished clever O(n).
- If stuck 3 minutes: simplify problem or ask for a hint explicitly.

---

## How to Communicate (Playbook)

| Phase | What to say |
|-------|-------------|
| Start | "Here are assumptions… Is N up to 10^5?" |
| Design | "Two approaches: A simpler, B faster. I'll start with A." |
| Code | "Implementing the happy path first." |
| Test | "Cases: empty, single, duplicate, max int." |
| Optimize | "Hotspot is … I can change to …" |
| Time up | "Remaining would be … Here's the incomplete part." |

---

## Edge Cases Checklist (Universal)

- Empty / null inputs
- Single element
- Duplicates
- Already sorted / reverse sorted
- Integer overflow
- Unicode/string normalization (if strings)
- Concurrent modification (if threading mentioned)
- Idempotent replay (if API)

---

## Testing During Interview

- Dry-run with 2–3 examples on the side.
- Write assert-like comments or small main.
- If IDE allowed: junit mini-tests.
- State what property-based tests you'd add later.

---

## Optimization & Time Management

| Time left | Action |
|-----------|--------|
| 0–10 min | Clarify + examples + approach |
| 10–35 min | Implement + dry run |
| 35–45 min | Edges + complexity + optional optimize |
| Last 5 | Summarize correctness and next steps |

If behind: cut scope ("I'll hardcode parser; focus on core matcher").

---

## Coding Scenarios

### Scenario A — Rate Limiter (Token Bucket)

**Interviewer:** Implement `boolean allow(String key)` with N requests / window per key.

**Candidate process:** Clarify window type (fixed vs sliding); single node vs distributed (start single); use map key → timestamps or tokens; discuss cleanup; concurrency (`ConcurrentHashMap` + atomic state); then code.

**Follow-up:** Multi-node?

**Expected:** Redis sliding window / GCRA; clock issues; fail-open/closed.

**Senior answer:** Correct single-node + tests. **Lead:** Production failure modes + API for config.

---

### Scenario B — Merge Interval Schedules

**Interviewer:** Merge overlapping meeting intervals.

**Approach:** Sort by start; sweep merge. Discuss comparator contracts; empty list.

---

### Scenario C — Idempotent Payment Apply

**Interviewer:** Given in-memory store, implement `applyPayment(idempotencyKey, amount)` safely under retries.

**Approach:** Map key → result; putIfAbsent semantics; define success record; concurrent callers.

---

### Scenario D — Debug Broken Code

**Interviewer:** Here's a Spring service with intermittent wrong totals — find the bug.

*(Typical bugs: shared mutable state, wrong equals/hashCode, TZ, integer division, stream re-use, TX not flushing.)*

**Process:** Reproduce with test; form hypotheses; add logging/asserts; fix minimally; add regression test.

---

### Scenario E — LRU Cache

**Classic:** `get`/`put` O(1) — HashMap + doubly linked list; discuss concurrency if asked.

---

### Scenario F — Parse & Validate CSV of Transactions

**Focus:** Robust parsing, error aggregation, streaming large files, not loading all memory.

---

### Scenario G — Top-K Frequent

**Heap vs bucket sort; discuss for interview N size.**

---

### Scenario H — Design Small Interface + Impl

**Interviewer:** Design `FeatureToggleClient` with caching and refresh.

**Evaluate:** Interface clarity, thread safety, stale reads, test doubles.

---

### Scenario I — Concurrent Job Runner with Limits

**Semaphore + Executor; cancellation; error propagation; virtual threads optional discussion.**

---

### Scenario J — Refactor God Method

**Interviewer:** Refactor this 80-line method without changing behavior.

**Process:** Characterize with tests; extract pure functions; name intermediates; keep behavior parity.

---

## Full Script Excerpt

**Interviewer:** Implement first non-repeating character in a stream of characters (online).

**Candidate:** Clarify: ASCII vs Unicode? Entire stream or window? Return when known?  
I'll maintain frequency counts and a queue of candidates. On each char, increment count; enqueue if first seen; dequeue while front has count > 1.  
Complexity O(n) time, O(k) space.

**Follow-up:** Parallelize?

**Answer:** Ordering constraint makes parallel hard; shard by key only for offline batch.

**Evaluation Notes:** Clarifying questions score heavily.

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Clarification | None | Some | Precise constraints |
| Correctness | Broken | Mostly | Solid + edges |
| Communication | Silent/chaotic | Adequate | Collaborative |
| Code quality | Spaghetti | Readable | Clean structure |
| Testing instinct | None | Manual cases | Systematic |

---

## Confidence Checklist

- [ ] Practiced narrating while coding 5+ problems
- [ ] Java Map/List/Queue/Heap fluency
- [ ] Comfortable saying "I'll take a hint"
- [ ] Always runs mental tests before declaring done
- [ ] Can abandon approach cleanly and restart

---

## Notes

<!-- Log timed practices; note panic triggers -->
