# Architecture Through Real Production Experience

> Panels hire the architect who owned the decision under constraints — not the one who recited patterns from a textbook.

---

## Purpose

Teach you to explain architecture using **your own production systems**: context, constraints, rejected options, measured outcomes, and lessons. Enterprise Java framing (Spring Boot, JPA/Hibernate, Postgres/MySQL, REST/events, AWS). Fill every skeleton with **your** numbers, ADRs, and incidents — that is the interview advantage.

---

## Why This Matters in Production (and in Interviews)

| Production reality | Interview signal |
|--------------------|------------------|
| You chose under incomplete information | Judgment under ambiguity |
| You paid for a tradeoff later | Honesty about residual risk |
| You measured before/after | Evidence over storytelling theater |
| You aligned stakeholders without jargon | Lead/architect communication |
| You sequenced change (flags, dual-run, strangler) | Delivery craft, not slideware |

Senior loops test recall of **owned systems**. Lead/Architect loops test whether you can **teach the panel how you think** so they trust you with their blast radius. Generic case studies from blogs fail both tests the moment a follow-up asks: “What was *your* p99 before the change?”

**Rule:** If you cannot name the system, the constraint, two options you rejected, a metric, and what you would do differently — do not claim the story.

---

## Universal Narrative Structure

Use this spine for every architecture story (8–12 minutes deep dive; 90 seconds elevator):

1. **Context** — system, users, business goal, your role/ownership.
2. **Constraint** — SLOs, compliance, team size, timeline, legacy coupling, budget.
3. **Options** — ≥2 credible alternatives (including “do nothing / defer”).
4. **Decision** — what you chose and the **decisive** criterion (not a laundry list).
5. **Outcome** — metrics, incidents avoided/caused, rollout method, residual risk.
6. **Lesson** — what you would change; revisit trigger; ADR takeaway.

Map to STAR/CAR when panels expect behavioral format:

| STAR | CAR | Architecture spine |
|------|-----|--------------------|
| Situation | Context | Context + Constraint |
| Task | Action (scope) | Options framing |
| Action | Action (decision) | Decision + sequencing |
| Result | Result | Outcome + Lesson |

---

## Performance Optimization

### How to frame the story

**Context → Constraint → Options → Decision → Outcome → Lesson**

- **Context:** Spring Boot service / batch / API; traffic profile; who felt the pain (users, batch window, cost).
- **Constraint:** Latency SLO (e.g., p99 < 200ms), DB connection pool, cache consistency, release freeze.
- **Options:** Query/index fix vs cache vs async offload vs scale-out vs rewrite hot path.
- **Decision:** Prefer measured bottleneck fix over speculative microservices/Redis.
- **Outcome:** Before/after latency, error rate, CPU, DB load, cost; how you proved it in prod.
- **Lesson:** What profiling taught; what you left on the table; monitoring you added.

### Metrics / evidence interviewers want

- p50/p95/p99 latency, throughput (RPS), saturation (CPU, pool, locks).
- Slow-query samples, EXPLAIN plans, lock wait / deadlocks.
- GC pause contribution, Hibernate N+1 counts, connection wait time.
- Cache hit ratio and stale-read tolerance if caching was involved.
- Rollout: canary %, error budget burn during change.

### Senior vs Lead framing

| Senior | Lead |
|--------|------|
| Owns the hot path fix; shows profiles and PR | Frames capacity vs product priority; sequences work across squads |
| Implements indexes, batching, connection tuning | Sets SLO/error-budget conversation with stakeholders |
| Mentions tools (JFR, async profiler, APM) | Mentions revisit triggers and cost of *not* optimizing |

### Common storytelling mistakes

- Jumping to Redis/Kafka without a profile.
- Reporting average latency only (hides tail).
- Claiming “10× faster” with no baseline method.
- Ignoring correctness/consistency side effects of caching.

### Sample answer skeleton (fill with your experience)

> On **[system]**, **[users]** hit **[symptom]** under **[load]**. Constraint: **[SLO / batch window]**. I profiled with **[tool]** and found **[bottleneck: N+1 / lock / full scan]**. Options: **[A]**, **[B]**, **[C]** — rejected **[X]** because **[reason]**. We shipped **[change]** via **[canary/flag]**. Result: p99 **[before → after]**, DB CPU **[…]**, no SLO breach for **[period]**. Lesson: **[…]**; revisit if **[trigger]**.

---

## Legacy Modernization

### How to frame the story

- **Context:** Age, stack (e.g., Java 8 monolith, SOAP, shared DB), business criticality.
- **Constraint:** Cannot stop the business; knowledge silos; compliance audits; shared schema.
- **Options:** Big-bang rewrite vs strangler vs modularize-in-place vs replace module-by-module.
- **Decision:** Usually strangler / expand-contract with coexistence and dual-run.
- **Outcome:** % traffic migrated, defect rate, deploy frequency, time-to-change for key features.
- **Lesson:** Where boundaries were wrong; what stayed coupled; debt still tracked.

### Metrics / evidence interviewers want

- Migration % (traffic, entities, endpoints), dual-run mismatch rate.
- Deploy cadence before/after; lead time for change; incident count on cutover.
- Test coverage / contract tests at the seam; rollback count.
- Cost: parallel-run infra, staff time, vendor licenses.

### Senior vs Lead framing

| Senior | Lead |
|--------|------|
| Owns a strangler seam (API adapter, anti-corruption layer) | Owns sequencing, risk register, stakeholder narrative |
| Delivers expand-contract for one bounded area | Negotiates freeze windows, dual-write policy, kill criteria |
| Shows concrete code boundary (Spring facade, ACL) | Shows Conway alignment and team ownership during transition |

### Common storytelling mistakes

- “We rewrote everything in microservices” with no coexistence story.
- No rollback or reconciliation plan mentioned.
- Blame-only narrative (“legacy was bad”) without business value delivered.
- Claiming greenfield purity while still sharing the old DB.

### Sample answer skeleton

> **[System]** was **[age/stack]** with **[pain: release risk / skill gap / compliance]**. Constraint: **[uptime / audit / shared schema]**. Options: rewrite / strangler / modularize — we chose **[…]** because **[…]**. Seam: **[ACL / BFF / new Spring module]**; coexistence via **[dual-run / feature flag / CDC]**. Migrated **[%]** over **[time]**; mismatch rate **[…]**; rollback tested by **[…]**. Outcome: deploy frequency **[…]**, critical path ownership **[…]**. Lesson: **[…]**.

---

## API Evolution

### How to frame the story

- **Context:** Public/partner/internal API; consumers; breaking-change pressure.
- **Constraint:** Consumer count, SLA, mobile app store lag, backward compatibility policy.
- **Options:** Versioned URLs vs headers vs additive fields vs BFF per client vs deprecate-and-sunset.
- **Decision:** Compatibility strategy + deprecation window + contract tests.
- **Outcome:** Breakage incidents (or lack), adoption of vN, time to deprecate vN-1.
- **Lesson:** What “breaking” meant in practice; documentation/governance gaps.

### Metrics / evidence interviewers want

- Consumer count and version distribution; error rates by version.
- Contract-test failures caught pre-prod; breaking-change incidents in prod.
- Deprecation timeline adherence; % traffic off old version at sunset.
- Payload size / latency impact of additive fields or BFF aggregation.

### Senior vs Lead framing

| Senior | Lead |
|--------|------|
| Designs DTO/versioning; implements expand-contract fields | Sets API governance, deprecation policy, consumer communication |
| Adds consumer-driven contract tests | Arbitrates BFF vs public API when product teams conflict |
| Owns OpenAPI/changelog discipline | Owns sunset decision and residual risk to partners |

### Common storytelling mistakes

- “We just added /v2” with no consumer migration plan.
- Silent field semantics changes (type/nullability) called “non-breaking.”
- Ignoring idempotency and error-model evolution.
- No mention of mobile/offline clients lagging server releases.

### Sample answer skeleton

> **[API]** served **[N consumers]** with **[pain: breaking needs / chatty clients]**. Constraint: **[compat window / app release lag]**. Options: **[versioning styles / BFF / additive]**. Decision: **[…]** with deprecation of **[…]** by **[date]**. Evidence: contract tests in **[CI]**; traffic on v1 fell **[%→%]**; zero P1 breaks during **[period]**. Lesson: **[…]**.

---

## Database Redesign

### How to frame the story

- **Context:** Schema pain (god table, missing constraints, wrong isolation of bounded contexts).
- **Constraint:** Downtime budget, dual-write cost, reporting dependencies, ORM mapping (JPA).
- **Options:** In-place migration vs expand-contract columns vs new schema + backfill vs CQRS read model vs split DB.
- **Decision:** Prefer expand-contract and online migrations; split DB only with clear ownership.
- **Outcome:** Migration duration, lock time, data reconciliation diffs, query latency, integrity incidents.
- **Lesson:** What Hibernate/Flyway/Liquibase made hard; invariants you encoded in DB vs app.

### Metrics / evidence interviewers want

- Migration window, lock duration, replication lag during backfill.
- Row counts reconciled; checksum/diff rate; orphan/inconsistency count.
- Query p99 before/after; index size; vacuum/bloat if relevant.
- Dual-write lag and conflict resolution rules.
- Rollback: forward-only vs expandable schema strategy.

### Senior vs Lead framing

| Senior | Lead |
|--------|------|
| Authors Flyway/Liquibase scripts; dual-write code paths | Decides system-of-record, split timing, reporting cutover |
| Proves zero-downtime technique (expand-contract) | Aligns analytics/finance consumers on schema ownership |
| Handles JPA mapping evolution carefully | Sets data migration war-room and success criteria |

### Common storytelling mistakes

- Big-bang ALTER on hot tables with no lock analysis.
- “We moved to microservices DB-per-service” while still joining across DBs in app code.
- No reconciliation story for dual-write.
- Treating ORM model rename as a free schema change.

### Sample answer skeleton

> In **[DB/service]**, **[schema problem]** caused **[incidents/slow features]**. Constraint: **[RPO/RTO / downtime ≤ X]**. Options: **[…]**. We used **[expand-contract / backfill / dual-write]** via **[Flyway/Liquibase]**. Reconciliation: **[method]**, mismatch **[<N]**. Result: migration **[duration]**, p99 **[…]**, integrity bugs **[…]**. Lesson: **[…]**; revisit if **[…]**.

---

## Cloud Migration

### How to frame the story

- **Context:** On-prem/VM → AWS (or similar); what moved (compute, DB, files, jobs).
- **Constraint:** Data residency, network, skills, cost envelope, cutover window, compliance.
- **Options:** Rehost (lift-shift) vs replatform (managed DB/K8s) vs refactor (cloud-native) vs hybrid.
- **Decision:** Usually phased: lift critical path with exit criteria, then managed services where TCO wins.
- **Outcome:** Availability, MTTR, cost/month, deploy frequency, incident themes post-cutover.
- **Lesson:** What lift-shift deferred; FinOps surprises; identity/network gotchas.

### Metrics / evidence interviewers want

- Cutover duration, abort criteria used (or not), RTO/RPO proven in drill.
- Availability/SLO before vs after; MTTR; Sev counts in first 90 days.
- Cost: on-prem vs cloud TCO (compute, DB, egress, observability).
- % workloads migrated; remaining hybrid dependencies.
- Security: IAM boundaries, secrets, network segmentation post-move.

### Senior vs Lead framing

| Senior | Lead |
|--------|------|
| Migrates a service/runtime; IaC (Terraform/CDK), CI/CD to cloud | Owns wave plan, risk register, business cutover narrative |
| Proves health checks, autoscaling, backups restore | Buy vs build for managed services; FinOps with finance |
| Fixes app assumptions (local disk, sticky sessions) | Sets kill criteria for lift-shift debt repayment |

### Common storytelling mistakes

- Equating “on Kubernetes” with “cloud-native architecture.”
- No cost or egress discussion.
- Ignoring data migration and DNS/TLS cutover details.
- Claiming multi-AZ without proving failure drills.

### Sample answer skeleton

> We moved **[workload]** from **[on-prem]** to **[AWS service set]** for **[driver: agility / DR / cost]**. Constraint: **[compliance / window / skills]**. Options: rehost / replatform / refactor — chose **[…]**. Sequencing: **[waves]**; cutover: **[blue-green/DNS]**. Drill proved RTO **[…]**. Outcome: availability **[…]**, cost **[±%]**, MTTR **[…]**. Residual: **[…]**. Lesson: **[…]**.

---

## Scalability Improvements

### How to frame the story

- **Context:** Growth trigger (campaign, new market, batch volume); architecture before.
- **Constraint:** Single DB writer, sticky sessions, sync fan-out, team topology.
- **Options:** Vertical scale vs read replicas vs cache vs queue/async vs shard vs service extract.
- **Decision:** Measure bottleneck; scale the constraint; avoid distributed complexity early.
- **Outcome:** Load-test ceiling, prod peak handled, saturation metrics, cost per request.
- **Lesson:** What broke first next; headroom policy; when extraction became justified.

### Metrics / evidence interviewers want

- Peak RPS/jobs; load-test max before error budget burn.
- DB connections, lock waits, replica lag, queue depth/age.
- Horizontal pod/instance count vs benefit curve; cache hit rate.
- Hot partition / skewed key evidence if sharding involved.
- Cost per unit work before/after.

### Senior vs Lead framing

| Senior | Lead |
|--------|------|
| Removes bottleneck (pool, index, async boundary) | Capacity plan vs roadmap; says no to premature sharding |
| Load-tests realistically (skewed data) | Aligns product launch forecasts with engineering headroom |
| Implements idempotent consumers / backpressure | Defines scale revisit triggers in ADR |

### Common storytelling mistakes

- Jumping to Kafka/sharding before indexes and query plans.
- Load tests with uniform data that hide hot keys.
- Scaling app pods while DB is the bottleneck.
- No backpressure or degradation story.

### Sample answer skeleton

> **[System]** needed to handle **[Nx]** for **[event]**. Constraint: **[DB writer / sync calls / …]**. Profile showed **[bottleneck]**. Options: **[…]** — rejected **[sharding/services]** because **[…]**. Shipped **[replicas/cache/queue/HPA]**. Load test: **[ceiling]**; prod peak: **[…]** with error rate **[…]**. Cost/request **[…]**. Lesson: next limit is **[…]**; revisit at **[trigger]**.

---

## Reliability Improvements

### How to frame the story

- **Context:** Incident class (timeouts, poison messages, partial failure, bad deploy).
- **Constraint:** SLO/error budget, on-call load, blast radius, compliance reporting.
- **Options:** Retries/timeouts vs circuit breaker vs idempotency vs isolation vs progressive delivery vs redesign.
- **Decision:** Fix the failure mode you actually have; make recovery boring.
- **Outcome:** MTTR, Sev frequency, error-budget burn, toil hours, customer-impact minutes.
- **Lesson:** Runbooks, game days, what still wakes humans.

### Metrics / evidence interviewers want

- SLI/SLO definitions; error-budget burn before/after.
- MTTR, MTTD; Sev-1/2 counts; customer-impact minutes.
- Retry amplification, timeout budgets, queue DLQ rates.
- Deploy rollback rate; canary catch rate.
- On-call pages per week; toil hours reclaimed.

### Senior vs Lead framing

| Senior | Lead |
|--------|------|
| Implements timeouts, idempotency keys, outbox, DLQ | Sets SLO with product; error-budget policy for releases |
| Improves dashboards/alerts that map to user journeys | Reduces blast radius across ownership boundaries |
| Writes runbooks; participates in game days | Drives incident review quality and systemic fixes |

### Common storytelling mistakes

- “We added retries” without idempotency or deadline budgets.
- Alerting on CPU instead of user-journey SLIs.
- Claiming five-nines without measurement method.
- No mention of partial failure or multi-dependency timeouts.

### Sample answer skeleton

> After **[incident class]** on **[system]**, SLO **[…]** was burning. Constraint: **[on-call / blast radius]**. Failure mode: **[timeouts / duplicate side effects / bad deploy]**. Options: **[…]**. Decision: **[timeouts+idempotency / circuit / canary / isolation]**. Result: MTTR **[…]**, Sev rate **[…]**, pages/week **[…]**. Game day proved **[…]**. Lesson: **[…]**.

---

## Engineering Tradeoffs (Storytelling vs Substance)

| Temptation | Risk | Discipline |
|------------|------|------------|
| Polished narrative | Hollow if numbers missing | Bring one dashboard screenshot memory: baselines |
| Heroic lone-wolf story | Signals poor collaboration | Name partners, reviewers, on-call shared ownership |
| Perfect hindsight | Sounds dishonest | State what you did *not* know at decision time |
| Pattern name-dropping | Triggers “prove it” traps | Lead with constraint and metric; pattern last |
| Blame the legacy / vendor | Avoids accountability | Own your decision surface and residual risk |
| Inflating scope (“I architected the platform”) | Reference checks fail | Precise ownership: module, ADR, migration wave |

**Substance beats theater:** a messy dual-write with reconciliation beats a clean microservices fairy tale.

---

## Common Anti-Patterns

1. **Buzzword salad** — “event-driven cloud-native mesh” with no diagram of data ownership.
2. **Stolen case studies** — Netflix/Amazon stories presented as personal ownership.
3. **No numbers** — “much faster / more scalable” without method or magnitude.
4. **Options of one** — decision with no rejected alternative.
5. **Happy-path only** — no failure, rollback, or dual-run.
6. **Title inflation** — claiming architect scope for a single PR.
7. **Tool worship** — Kafka/K8s as the decision instead of the problem.
8. **Metric theater** — vanity averages; no SLI tied to users.
9. **Lesson-free ending** — no revisit trigger or what you’d change.
10. **Contradicting your resume** — dates, stack, or scale that don’t match.
11. **Ignoring enterprise constraints** — compliance, change windows, shared DBA ownership.
12. **Unowned Notes** — playbook topics never filled with your war stories.

---

## Best Practices

1. Maintain **3–5 deep stories** covering performance, reliability, data/API, and a migration — not twenty shallow ones.
2. Use **ADR one-pagers** as memory aids: context, options, decision, consequences, revisit triggers.
3. Tie each story to a **playbook Notes** entry (link system name, metric, ADR id).
4. Practice **90s / 5min / 12min** versions of the same story.
5. Always state **role**: drove, co-designed, implemented, reviewed — be precise.
6. Prefer **enterprise Java artifacts**: Flyway, outbox, Spring timeouts, connection pools, OpenAPI — concreteness builds trust.
7. Quantify **residual risk**; never claim zero risk.
8. Prepare **one failure story** where your decision was wrong and you corrected course — high trust signal.
9. Map stories to **Senior vs Lead** emphasis depending on the loop.
10. Rehearse follow-ups: “What did you reject?”, “How did you measure?”, “What broke next?”

---

## Architecture Review Checklist (Preparing Your Own Stories)

For each candidate story, verify:

- [ ] System name, users, and business goal stated in one sentence
- [ ] Your ownership boundary is precise and defensible
- [ ] Constraints listed (SLO, time, compliance, team, money)
- [ ] ≥2 real options with rejection reasons
- [ ] Decisive criterion for the choice is clear
- [ ] Sequencing/rollout named (flag, canary, dual-run, expand-contract)
- [ ] ≥2 quantitative outcomes with before/after or drill proof
- [ ] Failure/rollback/reconciliation addressed
- [ ] Residual risk and revisit trigger named
- [ ] Lesson is non-generic (specific to this system)
- [ ] ADR or design doc exists (or reconstructed honestly)
- [ ] Story does not contradict resume or other answers
- [ ] 90-second and 12-minute variants practiced aloud
- [ ] Notes section in this playbook updated with the story

---

## Interview Challenge

**Prompt:** Pick one production system you owned (or co-owned). Walk the panel through an architecture change you led or heavily influenced — performance, modernization, API, database, cloud, scale, or reliability.

In ~10 minutes cover: context, constraints, options considered, decision, rollout, measurable outcome, residual risk, and what you would do differently. Expect deep follow-ups on metrics and failure modes.

Prepare as if the panel can ask for: the ER/API sketch, the migration sequence, and the on-call impact.

---

## Suggested Answer

*(Skeleton — replace brackets with your facts. Do not memorize a fake system.)*

**Opening (45s):** “I’ll walk through **[system]** — a Spring Boot **[monolith/service]** on **[Postgres/MySQL]** serving **[users/use case]**. I was **[role]**. We needed to **[goal]** under **[constraint: SLO/timeline/compliance]**.”

**Options (2min):** “We considered (1) **[…]**, (2) **[…]**, (3) defer/do nothing. (1) failed because **[…]**. (2) cost **[…]**. We chose **[…]** because the decisive factor was **[…]**.”

**Decision + sequencing (3min):** “Design: **[seam/data ownership/sync vs async]**. Rollout: **[expand-contract / dual-run / canary % / feature flag]**. Operability: **[metrics, alerts, runbook, rollback]**. Enterprise specifics: **[Flyway, outbox, idempotency keys, IAM, etc.].**”

**Outcome (2min):** “Before: **[metric]**. After: **[metric]** over **[window]**. Incidents: **[…]**. Cost/toil: **[…]**. Residual risk: **[…]**, revisit if **[trigger]**.”

**Lesson (1min):** “I’d change **[…]**. The ADR takeaway for the next team is **[…]**.”

**Lead-level close (optional):** “Stakeholder message was: **[non-jargon summary]**. We protected **[error budget / launch date]** by explicitly not doing **[fashionable alternative]**.”

Weak answer to avoid: pattern laundry list, no ownership, no numbers, no rejected options, no rollback.

---

## Architecture Reflection Questions

1. Which production decision would you defend hardest in a lead loop — and which metric proves it?
2. Where did you choose *not* to introduce microservices/events/cache, and what happened?
3. What dual-run or expand-contract migration have you actually operated?
4. Which of your stories still lacks hard numbers, and how will you recover them (APM, tickets, ADR)?
5. When did a follow-up question expose a hole in your narrative — what did you add?
6. How do your Senior-scoped stories differ from your Lead-scoped ones?
7. What is your best “I was wrong” architecture story, and what systemic fix followed?
8. Which playbook Notes entries are still empty for systems you claim on your resume?

---

## Interview Confidence Checklist

- [ ] Can deliver one owned architecture story in 90s and in 12min
- [ ] Naturally uses context → constraint → options → decision → outcome → lesson
- [ ] Brings real metrics (latency, SLO burn, migration %, MTTR, cost) without prompting
- [ ] Names ≥2 rejected options with reasons
- [ ] Explains rollout and rollback for every migration story
- [ ] Separates Senior implementation depth from Lead stakeholder framing
- [ ] Can sketch data ownership / API seam on a whiteboard from memory
- [ ] Has at least one cloud, one data/API, and one reliability/performance story
- [ ] Can discuss residual risk without defensiveness
- [ ] ADRs or Notes capture the stories — not only verbal memory
- [ ] Resume claims match story ownership and scale
- [ ] Avoids buzzwords unless tied to a concrete mechanism in *your* system

---

## Optional: Template Worksheets

### Worksheet A — Story card (one per system change)

| Field | Your notes |
|-------|------------|
| Story title | |
| System / stack | |
| Your role / ownership | |
| Business context | |
| Hard constraints | |
| Options (incl. reject reasons) | |
| Decision + decisive criterion | |
| Rollout / sequencing | |
| Metrics before → after | |
| Failure / rollback / reconciliation | |
| Residual risk + revisit trigger | |
| Lesson | |
| ADR / ticket / dashboard refs | |
| Senior angle (1 line) | |
| Lead angle (1 line) | |

### Worksheet B — Evidence pack

| Evidence type | Link / location | Number to cite |
|---------------|-----------------|----------------|
| APM / dashboard | | |
| Load test report | | |
| Incident / PIR | | |
| ADR / RFC | | |
| Migration checklist | | |
| Cost / FinOps | | |
| Contract test / CI | | |

### Worksheet C — Follow-up drill

Prepare one sentence each:

- What did you personally write/review?
- What would break if we reversed the decision tomorrow?
- What did ops inherit (alerts, runbooks, toil)?
- What would you not repeat?
- How does this map to *our* (interviewer’s) scale/constraints?

---

## Notes

<!-- Fill with your production stories: system names, metrics, ADR IDs, incident links, Senior vs Lead variants -->
