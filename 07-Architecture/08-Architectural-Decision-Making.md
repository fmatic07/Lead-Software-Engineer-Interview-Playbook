# Architectural Decision Making

> Lead interviews hire for decision quality under constraints — options weighed, risks named, sequencing honest, and stakeholders aligned without theater.

---

## Purpose

Train judgment and communication for architecture loops: buy vs build, monolith vs microservices, build-for-today vs tomorrow, refactoring strategy, technical debt, architecture reviews, cost optimization, scalability planning, and risk management — with ADRs and stakeholder framing as first-class tools.

---

## Topics Covered

- [ ] Buy vs Build
- [ ] Monolith vs Microservices
- [ ] Build for Today vs Tomorrow
- [ ] Refactoring Strategy
- [ ] Technical Debt
- [ ] Architecture Reviews
- [ ] Cost Optimization
- [ ] Scalability Planning
- [ ] Risk Management

---

## Buy vs Build

### Judgment frame

Buy/build is a **TCO and differentiation** decision: license + integration + lock-in + ops skill vs engineering time + maintenance forever. “We can build it in two sprints” ignores the decade of patches.

### Production use cases

- Identity: Auth0/Cognito/Keycloak vs custom JWT stack.
- Payments: acquirer SDK + PCI scope reduction vs in-house card vault.
- Search: managed OpenSearch vs self-run Elasticsearch on Kubernetes.
- Workflow: Camunda/Temporal vs homegrown state machines.
- Observability: Datadog/New Relic vs self-hosted Prometheus/Grafana/Loki stack.

### Decision axes

| Axis | Prefer buy | Prefer build |
|------|------------|--------------|
| Differentiation | Commodity capability | Core domain IP |
| Compliance | Vendor certifications help | Unique regulatory needs |
| Integration | Clean APIs, escape hatches | Deep embed in domain model |
| Ops maturity | Team thin on that specialty | Strong platform team already runs it |
| Lock-in | Acceptable / portable data | Exit cost unacceptable |
| Time-to-market | Need it this quarter | Can invest for multi-year edge |

Interview signal: quantify integration and exit cost, not only sticker price. Name the **kill criteria** if the vendor fails SLAs.

---

## Monolith vs Microservices

### Judgment frame

This is a **team topology and failure-isolation** decision more than a scalability spell. A well-modular monolith on Spring Boot + Postgres scales further than a poorly bounded microservice mesh. Microservices buy independent deploy and isolation; they cost distributed data, latency, and cognitive overhead.

### When monolith (or modular monolith) wins

- Team < ~2 squads owning the domain.
- Consistency needs span the domain frequently.
- Ops platform immature (no solid CI/CD, observability, on-call).
- Product still discovering boundaries.

### When microservices win

- Clear bounded contexts with separate deploy cadences.
- Independent scale/security/compliance envelopes.
- Multiple teams needing autonomy without merge contention.
- Failure isolation required (payments vs marketing site).

### Hybrid reality

Most enterprises run a **core modular monolith** plus services at the edges (notifications, search indexer, BFF). Strangler fig over rewrite. “Microservices” without independent data ownership is a distributed monolith — the worst of both.

Kubernetes makes running many services easy; it does **not** make designing them correct.

---

## Build for Today vs Tomorrow

### Judgment frame

Over-architecture burns runway; under-architecture creates rewrite pressure. Optimize for **optionality**: make tomorrow’s change cheap without paying today’s full speculative cost. Prefer designs that are easy to split later (module boundaries, stable APIs) over designs that are already split.

### Heuristics

- **YAGNI with teeth:** skip CQRS/event sourcing until pain is measured.
- **Build seams, not skyscrapers:** package by domain, explicit interfaces, single DB OK.
- **Reversibility:** classify decisions as one-way doors (data model public API, multi-tenant isolation model) vs two-way (cache library, log format).
- **Capacity headroom:** design for ~3–5× near-term load with clear scale-up path; don’t design for hypothetical 1000× black Friday in year one unless that is the business plan.

Phrase for panels: “We optimized for the next credible horizon, with seams for the horizon after.”

---

## Refactoring Strategy

### Judgment frame

Refactoring is **risk-managed change**, not cleanup weekends. Strategy beats motivation: strangler fig, branch by abstraction, parallel run, dark launch, expand/contract.

### Production patterns

| Strategy | Use when |
|----------|----------|
| Strangler fig | Replacing legacy module behind façade |
| Branch by abstraction | Swap implementation under stable interface |
| Parallel run / shadow | Compare old vs new path on production traffic |
| Expand/contract | Schema and API evolution |
| Incremental extract | Carve a service only when team+data ready |

### Sequencing

1. Characterize behavior with tests/contracts/metrics.
2. Introduce seam without behavior change.
3. Move traffic gradually; watch error and latency budgets.
4. Remove old path after soak; delete code (debt interest stops only when deleted).

Never “big bang rewrite” without a coexistence period unless the legacy is literally unrunnable.

---

## Technical Debt

### Judgment frame

Debt is a **deliberate or accidental loan against future delivery**. Good debt: ship with known shortcuts and a repayment date. Bad debt: invisible coupling, missing tests, dual-write landmines. Leaders make debt **visible, priced, and prioritized** — not moralized.

### Classification (use in interviews)

- **Prudent deliberate:** skip abstraction before second use case; tracked.
- **Reckless deliberate:** ship without authz checks to hit a demo.
- **Prudent inadvertent:** learned better design after domain clarified.
- **Reckless inadvertent:** no one understands the payment module.

### Management tactics

- Debt register in backlog with interest (slowed features, SEV risk).
- Budget capacity (e.g., 15–20%) or attach repayment to feature work that touches the area.
- Architecture fitness functions (CI checks) to stop interest from compounding.
- Distinguish **cleanup** (rename) from **risk debt** (data integrity) — prioritize risk.

Stakeholder framing: translate debt to **delivery delay, incident probability, hiring friction** — not “code smells.”

---

## Architecture Reviews

### Judgment frame

Reviews exist to **reduce irreversible risk**, not to enforce personal taste. Lightweight ADRs + focused review boards beat slideware governance that ships nothing.

### ADR (Architecture Decision Record)

Keep ADRs short:

1. Context (forces)
2. Decision
3. Alternatives considered
4. Consequences (positive/negative)
5. Status (proposed/accepted/superseded)

Store next to code. Supersede; don’t silently rewrite history.

### Review checklist culture

- Who owns the decision and the on-call?
- What is the blast radius if wrong?
- What is the rollback?
- What observability proves it works?
- What is explicitly out of scope?

Anti-pattern: architecture review as late veto theater after code is done. Prefer **design review at proposal** and **fitness checks in CI** afterward.

---

## Cost Optimization

### Judgment frame

Cost is an architectural attribute: cloud spend, people time, and opportunity cost. Premature microservice sprawl and idle Kafka clusters are cost bugs. Optimization without SLOs creates fragile systems.

### Levers (enterprise Java / K8s)

- Right-size pods and JVM heaps; avoid one-core defaults × hundreds of services.
- Prefer managed Postgres/Redis when ops headcount is the scarce resource.
- Cache hot reads; don’t scale DB for chatty APIs.
- Retention policies on Kafka/logs/metrics — observability can exceed app cost.
- Spot/preemptible for workers; never for stateful primaries without design.
- Consolidate underused services back into modular monoliths when justified.
- Rate limits and quotas — cost control is also abuse control.

FinOps partnership: tag resources by service/team; show unit cost (per order, per tenant).

---

## Scalability Planning

### Judgment frame

Scalability planning is **evidence-based capacity design**: measure, project, bottleneck-hunt, then scale the constraint. Vertical scale, read replicas, caching, async offload, partition/shard — in that general cost order unless SLOs force otherwise.

### Process

1. Define SLIs/SLOs (latency, error, freshness).
2. Load-test critical journeys with production-like data skew (whale tenants).
3. Find bottleneck (CPU, DB locks, pool exhaustion, Kafka lag, GC).
4. Choose scale action with ADR; include cost.
5. Set alerts at 60–70% of proven capacity, not at death.

### Application to Spring estates

- Pool math: pods × Hikari max ≤ DB max_connections.
- Idempotent async for spikes; don’t size HTTP tier for peak batch.
- Partition Kafka by growth plan; changing keys later is painful.
- Multi-AZ and PDB for availability ≠ unlimited horizontal scale.

Plan for **scale-down** too — HPA flapping and Monday-morning cold starts are scalability problems.

---

## Risk Management

### Judgment frame

Architectural risk = probability × impact on availability, integrity, security, compliance, delivery. Leaders maintain a living risk list with mitigations and owners — not a once-yearly slide.

### Categories to surface in interviews

| Risk | Example mitigation |
|------|--------------------|
| Data integrity | Outbox, constraints, reconciliation |
| Availability | Multi-AZ, bulkheads, degrade modes |
| Security | Threat model, least privilege, secrets rotation |
| Change risk | Canaries, feature flags, expand/contract |
| Key-person | Docs, pairing, ADRs |
| Vendor | Exit plan, data export drills |
| Compliance | Data residency, audit trails |

### Stakeholder communication

- Executives: residual risk in business language (“duplicate charge window”).
- Product: trade between speed and risk mitigation capacity.
- Engineers: concrete runbooks and guardrails.

Never claim zero risk; claim **managed risk with detection and response**.

---

## Decision communication patterns

### For hiring panels

Use a repeatable spine:

1. **Goal & constraints** (SLO, time, team skill, compliance)
2. **Options** (≥2 real ones)
3. **Criteria & weights**
4. **Decision**
5. **Consequences & risks**
6. **Validation & revisit triggers**

### Phrases that sound Lead-level

- “The one-way door here is…”
- “We’d accept X debt because Y repayment is scheduled with feature Z.”
- “Microservices fail the team-topology test today.”
- “Buy — it’s not differentiating; our scarce resource is senior bandwidth.”
- “We’ll revisit when lag SLO breaches or tenant count hits N.”

---

## Why this matters in production

Architecture fails more often from **poor decisions and poor sequencing** than from ignorance of patterns. Enterprises drown in unfinished migrations, microservice sprawl without platform maturity, and debt that was never priced. Lead/Architect interviews probe whether you can **decide, explain, and sequence** — especially saying no.

Your job in the room is to show you can protect the business from both reckless speed and ivory-tower delay.

---

## Engineering tradeoffs

| Tension | Lean left | Lean right |
|---------|-----------|------------|
| Buy vs build | Speed, certifications | Control, differentiation |
| Monolith vs services | Simplicity, strong consistency | Team autonomy, isolation |
| Today vs tomorrow | Ship value, learn domain | Optionality, avoid rewrite |
| Refactor vs feature | Risk reduction | Revenue/learning |
| Central review vs team autonomy | Coherence | Velocity |
| Cost cut vs resilience | Lower bill | Higher MTTR if naive |
| Scale now vs measure first | Headroom | Avoid waste |

There is no universal winner — only **context-weighted** choices with explicit losers.

---

## Common anti-patterns

1. **Resume-driven development** — microservices/CQRS/Kubernetes because interviews like them.
2. **Big-bang rewrite** without strangler coexistence.
3. **Architecture by veto** — late reviews that only say no.
4. **Invisible debt** — no register, no budget, surprise SEVs.
5. **Buy without exit plan** — data hostage.
6. **Build every commodity** — identity, email, observability invented in-house.
7. **Scale theater** — sharding before indexes and query fix.
8. **Cost cutting that deletes redundancy** — single-AZ “savings.”
9. **ADR archaeology** — decisions only in Slack.
10. **Optimistic roadmap** — migration with no capacity allocated.
11. **Distributed monolith** — many services, one DB, one release train.
12. **Analysis paralysis** — seeking perfect information on two-way door decisions.

---

## Best practices

1. Write **ADRs** for one-way doors and cross-team decisions.
2. Always present **≥2 options** with consequences.
3. Classify decisions by **reversibility**; spend ceremony accordingly.
4. Sequence change with **strangler / expand-contract / flags**.
5. Keep a **debt register** tied to risk and delivery impact.
6. Match architecture to **team topology** (Conway).
7. Prefer **modular monolith** until boundaries and platform hurt.
8. Buy non-differentiating capabilities; build the domain.
9. Measure before scaling; load-test with skewed data.
10. Pair cost optimization with **SLO guards**.
11. Make risks and residual risks explicit to stakeholders.
12. Define **revisit triggers** (load, team size, lag, incident themes).

---

## Architecture review checklist

- [ ] Problem statement and non-goals clear
- [ ] Constraints listed (time, skill, compliance, SLOs, budget)
- [ ] Alternatives considered with rejected reasons
- [ ] One-way vs two-way door classified
- [ ] Data ownership and consistency impact assessed
- [ ] Operability: on-call, runbooks, observability
- [ ] Security/privacy implications noted
- [ ] Cost projection (cloud + people) order-of-magnitude
- [ ] Migration/sequencing plan with rollback
- [ ] Debt introduced explicitly tracked
- [ ] Revisit/kill criteria defined
- [ ] ADR drafted and owner named
- [ ] Stakeholder summary in non-jargon form ready

---

## Interview Challenge

You inherit a Spring Boot modular monolith (Postgres) used by three squads. Product wants “microservices on Kubernetes” before a major market launch in five months. Peak load today is comfortable on a primary + replica; launch forecasts 5×. The payments module is entangled with notifications and reporting. Leadership asks you for a recommendation in a 30-minute architecture review.

Structure your decision: buy/build (where relevant), monolith vs services, what to build now vs later, debt, cost, scale, and risk. Include what you will tell the VP who wants microservices for hiring marketing.

---

## Suggested Answer

**Recommendation:** Stay on a **modular monolith** for launch; extract **one** service only if a clear isolation/scale need appears (e.g., notifications async worker already queue-based). Move to Kubernetes if delivery/ops benefits are real — but K8s ≠ microservices.

**Today vs tomorrow:** Introduce package/API seams around Payments; outbox for domain events; harden observability and load tests to 8–10×. Do **not** split databases mid-launch crunch.

**Scale plan:** Prove 5× with load tests; scale vertically/replica/HPA first; cache hot GETs; async reporting. Sharding/services deferred until bottleneck identified.

**Debt:** Entanglement is tracked; repayment = extract notification publisher behind interface this quarter; reporting via read replica/CDC next.

**Buy vs build:** Prefer managed Postgres/Redis and existing Kafka (if present) over new platforms mid-flight; don’t build a new workflow engine for launch.

**Risk:** Highest risk is rewrite distraction → missed launch / payment defects. Residual risk of monolith deploy coupling accepted for five months with stronger test gates and feature flags.

**VP message:** “Microservices are a team-scaling tool. With three squads and a five-month launch, splitting payments now maximizes coordination risk. We’ll ship on modular monolith with seams, publish an ADR, and revisit extraction when we have two teams inside payments or an independent scale envelope — measured, not aspirational.”

**ADR + revisit triggers:** team count in payments domain, deploy contention SEVs, load test ceiling < forecast, or compliance boundary requiring isolation.

---

## Architecture Reflection Questions

1. What is the last one-way door decision you made, and what alternatives did you reject?
2. How do you currently surface technical debt to non-engineers?
3. When did you choose a monolith (or consolidation) against fashion — what happened?
4. What buy vs build decision would you reverse with today’s knowledge?
5. How do you run architecture reviews without becoming a bottleneck?
6. What revisit triggers do you attach to ADRs in practice?
7. How do you discuss residual risk with a VP after a SEV?

---

## Interview Confidence Checklist

- [ ] Naturally structures answers as constraints → options → decision → consequences
- [ ] Can run a buy vs build discussion with TCO and exit criteria
- [ ] Can defend modular monolith without anti-microservice dogma
- [ ] Can explain strangler/expand-contract sequencing
- [ ] Can classify and prioritize technical debt by risk
- [ ] Can draft an ADR outline live on a whiteboard
- [ ] Can connect cost optimization to SLOs (not just spend cuts)
- [ ] Can outline a scale plan that starts with measurement
- [ ] Can name residual risks without claiming certainty
- [ ] Can push back on résumé-driven architecture diplomatically

---

## Notes

<!-- Your ADRs, buy/build calls, monolith/service decisions, stakeholder pushbacks -->
