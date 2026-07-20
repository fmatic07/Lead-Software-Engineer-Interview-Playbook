# Technical Decision Making

> Senior panels hire for decision quality under constraints — options, costs, reversibility, and what you refuse to build.

---

## Purpose

Train structured tradeoff reasoning for Lead/Architect interviews: buy vs build, debt, refactoring, risk, maintainability, and the eternal tension between simplicity and scale.

---

## Topics Covered

- [ ] Evaluating Tradeoffs
- [ ] Buy vs Build
- [ ] Technical Debt
- [ ] Refactoring Strategy
- [ ] Risk Assessment
- [ ] Long-term Maintainability
- [ ] Cost vs Performance
- [ ] Simplicity vs Scalability

---

## Evaluating Tradeoffs

### Explanation

Evaluating tradeoffs means making constraints and abandoned alternatives explicit. Every design choice optimizes for some axes (latency, consistency, cost, time-to-market, operability) at the expense of others. Leaders name the axes, weight them for the context, and decide.

### Why interviewers ask these questions

- Core of system design and architecture loops.
- Separates dogma from contextual engineering.
- Predicts whether you will overbuild or underprotect critical paths.

### Real production examples

- Choosing strong consistency for wallet balances and accepting lower write throughput.
- Preferring synchronous REST for a simple approval flow instead of events — clearer UX and debugging.
- Accepting higher infra cost for multi-AZ DB because RPO/RTO targets demanded it.

### Engineering tradeoffs

- Optimizing locally (one service) vs globally (platform cost and cognitive load).
- Perfect information vs deciding under deadline.
- Engineer preference vs operational reality (on-call skill, headcount).

### Common mistakes

- Presenting one option as “best practice” with no alternative.
- Ignoring operability and team skill as first-class axes.
- Tradeoff theater: listing pros/cons without a decision.
- Changing weights mid-project without acknowledging it.

### Senior Engineer perspective

For module decisions, write the constraints, two options, and why. Seek review on irreversible choices. Measure after shipping.

### Lead Engineer perspective

Standardize tradeoff language in ADRs. Facilitate disagreements into weighted criteria. Align product on what is being optimized. Revisit decisions when constraints change — with evidence.

### Interview Challenge

Product wants “real-time everything.” How do you evaluate whether push events or polling is right?

### Suggested Answer

Clarify freshness needs, volume, fan-out, failure UX, cost, and team ops maturity. Compare polling with caching/ETags vs push (SSE/WebSocket/queue). Choose based on actual SLA for freshness and operational burden; often near-real-time polling or async notification is enough. Document kill criteria if load grows.

### Leadership Reflection Questions

1. Which axes do you under-weight by habit (ops? cost? security?)?
2. Can you explain the last major tradeoff to a PM in two minutes?
3. What decision would you reverse with today’s data?

### Interview Confidence Checklist

- [ ] Naturally uses weighted criteria language
- [ ] Always surfaces at least two real options
- [ ] Includes operability in tradeoffs

---

## Buy vs Build

### Explanation

Buy vs build is choosing whether to purchase/integrate a product (SaaS, library, managed service) or implement in-house. The decision hinges on differentiation, compliance, integration cost, lock-in, and total cost of ownership — not only license price.

### Why interviewers ask these questions

- Enterprise orgs constantly face vendor vs custom core.
- Tests TCO thinking and strategic product sense.
- Common at Deltek/Globe/Maya where platforms and vendors coexist.

### Real production examples

- Buying managed Kafka vs running ZooKeeper/KRaft yourselves given SRE capacity.
- Building custom KYC orchestration because regulations and workflows are differentiating; buying OCR as a commodity.
- Using Spring Security + IdP instead of writing custom auth crypto.

### Engineering tradeoffs

- Speed-to-market vs long-term flexibility.
- Vendor lock-in vs maintenance burden of custom code.
- Compliance/data residency constraints that force build or specific vendors.
- Integration tax often exceeds build estimates.

### Common mistakes

- Build because “we are smart” on commodity problems (auth, email, PDF).
- Buy without an exit strategy or data export plan.
- Ignoring internal integration and support costs in vendor TCO.
- Customizing a bought product until it is an unupgradeable fork.

### Senior Engineer perspective

Default buy for commodity; build for differentiating domain logic. Spike integration early. Prototype the hard boundary first.

### Lead Engineer perspective

Run a decision brief: differentiation score, TCO 3-year, compliance, ops model, exit plan. Involve security/legal early. Cap customization; prefer configuration. Revisit when vendor fails SLAs.

### Interview Challenge

Should you build an in-house feature-flag system or buy one?

### Suggested Answer

If flags are commodity targeting/percentage rollouts, buy or use a mature open-source/managed option unless compliance forbids it. Build only if you need deep domain-specific experimentation tightly coupled to your transaction model and can staff it. Account for audit logs, UI, SDKs, and on-call — not just a boolean map in Redis.

### Leadership Reflection Questions

1. Where are you maintaining a commodity in-house out of habit?
2. What is your exit plan for your most critical vendor?
3. Did the last buy decision include integration cost honestly?

### Interview Confidence Checklist

- [ ] Uses differentiation + TCO framing
- [ ] Mentions lock-in and exit
- [ ] Has a real buy-or-build story

---

## Technical Debt

### Explanation

Technical debt is deferred engineering work that increases future cost of change — conscious shortcuts, accidental complexity, or outdated platforms. Good leaders make debt visible, classified, and intentionally repaid; they do not moralize every shortcut.

### Why interviewers ask these questions

- Every mature codebase has debt; judgment is how you manage it.
- Distinguishes reckless shipping from strategic debt.
- Links to risk and prioritization stories.

### Real production examples

- Strategic debt: ship monolith module fast for a market deadline, with a dated strangler plan.
- Reckless debt: skip idempotency on payments to “move fast.”
- Platform debt: Java 11 → 21 upgrade blocked by a frozen library; scheduled repayment with risk scoring.

### Engineering tradeoffs

- Feature velocity vs compounding interest on debt.
- Local cleanup vs platform upgrades that unlock many teams.
- Perfect repayment vs containing debt behind interfaces.

### Common mistakes

- Calling every inconvenience “tech debt.”
- Never allocating repayment capacity.
- Big-bang rewrites as the only repayment strategy.
- Hiding debt from product until crisis.

### Senior Engineer perspective

Tag debt with impact (change cost, incident risk). Prefer encapsulating and chipping away. Do not expand debt in money/auth paths.

### Lead Engineer perspective

Maintain a debt register with severity. Negotiate a fixed capacity percentage. Tie repayment to risk and roadmap enablement. Prevent new reckless debt via review standards and Definition of Done.

### Interview Challenge

How do you convince product to fund two sprints of debt work?

### Suggested Answer

Translate debt into customer/business risk: incident probability, delivery slowdown metrics, audit findings, or blocked features. Propose scoped repayment with measurable outcomes and a feature trade. Offer phased approach. Speak cost-of-delay language, not “code is messy.”

### Leadership Reflection Questions

1. Is your debt register real or folklore?
2. What debt is actually a product risk?
3. Where did strategic debt pay off — and where did it not?

### Interview Confidence Checklist

- [ ] Distinguishes strategic vs reckless debt
- [ ] Can sell repayment in business terms
- [ ] Has a repayment success story

---

## Refactoring Strategy

### Explanation

Refactoring strategy is changing structure without changing intended behavior — sequenced, tested, and reversible. Leaders choose strangler, branch-by-abstraction, or incremental modularization over heroic rewrites when systems must keep running.

### Why interviewers ask these questions

- Legacy is the default in enterprise interviews.
- Tests risk management of change.
- Reveals rewrite addiction vs evolutionary design.

### Real production examples

- Strangling a COBOL/legacy billing interface behind an anti-corruption layer.
- Branch-by-abstraction to replace a payment provider with dual-run.
- Incremental extraction of a notifications module only after stable interfaces existed.

### Engineering tradeoffs

- Refactor-in-place vs extract service (ops cost jumps with extraction).
- Test investment before refactor vs refactoring blind.
- Stopping feature work vs interleaved refactoring.

### Common mistakes

- Rewrite from scratch with no parity tests.
- Refactoring without characterization tests on legacy.
- Mixing large behavior changes with structural refactors.
- “Cleanup” PRs that silently change contracts.

### Senior Engineer perspective

Characterize behavior with tests, refactor in small PR slices, keep behavior stable, use feature flags for cutovers. Separate renames/moves from logic changes when possible.

### Lead Engineer perspective

Approve refactor strategy and sequence. Define done criteria and rollback. Protect capacity. Stop unsafe rewrites. Align product on temporary dual-running costs.

### Interview Challenge

A team wants to rewrite a five-year-old service in a new stack in one quarter. How do you respond?

### Suggested Answer

Challenge goals: what pain is unsolved by incremental improvement? Demand parity strategy, risk register, staffing for dual systems, and a strangler plan. Usually propose modularizing and replacing highest-pain seams first. Approve rewrite only with clear boundary, metrics, and executive understanding of risk.

### Leadership Reflection Questions

1. When did a rewrite fail or succeed for you — why?
2. Do you have characterization tests on your scariest legacy?
3. How do you keep refactors reviewable?

### Interview Confidence Checklist

- [ ] Prefers evolutionary strategies with reasons
- [ ] Mentions tests/dual-run/rollback
- [ ] Can push back on rewrite bravado

---

## Risk Assessment

### Explanation

Risk assessment identifies what can go wrong, how likely, how severe, and what controls reduce it. Technical leaders assess delivery risk, operational risk, security/compliance risk, and organizational risk — then choose mitigations proportional to blast radius.

### Why interviewers ask these questions

- ING/Maya/GovTech-style orgs are risk-sensitive by nature.
- Leads must speak risk without fear-mongering.
- Ties architecture choices to controls.

### Real production examples

- Pre-release risk review for a ledger change: data loss, double spend, audit gaps.
- Assessing third-party SMS vendor downtime risk and adding fallback.
- Ranking migration risks: irreversible schema changes first-class.

### Engineering tradeoffs

- Mitigation cost vs residual risk acceptance.
- Speed of delivery vs additional controls (canaries, dual-write).
- Over-process on low-risk changes vs under-process on high-risk.

### Common mistakes

- Binary thinking: safe vs unsafe with no residual risk.
- Ignoring human/process risk (bus factor, unclear runbooks).
- Listing risks without owners or mitigations.
- Discovering compliance risk after build.

### Senior Engineer perspective

For your changes: failure modes, blast radius, observability, rollback. Call out risks in design docs early.

### Lead Engineer perspective

Run lightweight risk reviews for high-blast-radius work. Maintain different change tiers. Ensure residual risk is accepted by the right stakeholder. Track risk themes across incidents.

### Interview Challenge

How do you assess risk for a zero-downtime schema migration on a high-traffic table?

### Suggested Answer

Analyze lock behavior, expansion/contraction pattern, dual-write needs, backfill strategy, monitoring (lag, error rate), canary on read path, rollback feasibility, and peak-traffic windows. Prefer expand-migrate-contract. Rehearse on staging with production-like volume. Define abort criteria.

### Leadership Reflection Questions

1. Which risks are you accepting without a named accepter?
2. Do change tiers match real blast radius?
3. What risk did an incident teach you to price higher?

### Interview Confidence Checklist

- [ ] Speaks likelihood × impact × mitigation
- [ ] Has a migration/risk story
- [ ] Involves stakeholders in residual risk

---

## Long-term Maintainability

### Explanation

Maintainability is the expected cost of future change: clarity of modules, stability of contracts, testability, observability, and alignment with team cognitive load. Leaders optimize for the next two years of change, not only launch day.

### Why interviewers ask these questions

- Architects are hired to reduce future cost of change.
- Separates clever code from operable systems.
- Enterprise systems live longer than project teams.

### Real production examples

- Preferring explicit domain modules over a clever generic framework nobody can extend.
- Stabilizing API contracts with versioning to allow independent team release.
- Investing in contract tests between services to make refactors safer.

### Engineering tradeoffs

- Upfront modularity vs YAGNI.
- Consistency across teams vs local speed.
- Abstraction layers vs indirection cost.

### Common mistakes

- Premature frameworks for one use case.
- No ownership or docs — unmaintainable regardless of code beauty.
- Ignoring onboarding time as a maintainability metric.
- Cleverness that only the author understands.

### Senior Engineer perspective

Write boring, explicit code at boundaries. Keep modules cohesive. Leave the codebase easier than you found it in your area.

### Lead Engineer perspective

Set maintainability standards: module boundaries, lint/arch unit tests, API compatibility rules. Measure lead time and onboarding pain. Reject designs that only the strongest engineer can operate.

### Interview Challenge

What signals tell you a system is becoming unmaintainable?

### Suggested Answer

Rising change failure rate, long lead time for small changes, fear of deploy, knowledge concentrated in one person, PRs that touch everywhere, missing tests at boundaries, and onboarding taking months. Treat these as leading indicators and intervene with boundaries, tests, and ownership — not slogans.

### Leadership Reflection Questions

1. What small change recently took too long — why?
2. Would a new senior be productive in two weeks?
3. Which abstraction is costing more than it saves?

### Interview Confidence Checklist

- [ ] Defines maintainability with operational signals
- [ ] Balances YAGNI and modularity
- [ ] Has improved maintainability measurably

---

## Cost vs Performance

### Explanation

Cost vs performance is balancing latency/throughput goals against infrastructure spend and engineering complexity. Leaders set performance budgets tied to user/business value and avoid optimizing vanity metrics.

### Why interviewers ask these questions

- Cloud bills and SLOs are both real constraints.
- Tests whether you optimize with measurement.
- Common in fintech/telco where scale spikes are expensive.

### Real production examples

- Caching product catalog aggressively; not caching authorization decisions incorrectly.
- Choosing vertical scale temporarily over premature sharding.
- Accepting slightly higher p99 for a back-office report to avoid huge replica cost.

### Engineering tradeoffs

- More hardware vs better algorithms/data model.
- Complex caching vs simpler DB with headroom.
- Optimizing average vs tail latency.

### Common mistakes

- Optimizing without profiling.
- Caching as first response to design problems.
- Ignoring cost of complexity (eng hours) in “performance wins.”
- Meeting latency SLO by sacrificing correctness.

### Senior Engineer perspective

Measure first. Optimize the bottleneck. Keep correctness and idempotency sacred. Document performance budgets for critical endpoints.

### Lead Engineer perspective

Define SLOs and error budgets with product. Review cost anomalies. Decide when performance work beats feature work. Prevent performance cults that ignore TCO.

### Interview Challenge

p95 is fine but p99 is terrible on checkout. Cost pressure is high. What do you do?

### Suggested Answer

Profile tail contributors (GC, lock, remote calls, cold cache, noisy neighbors). Fix the dominant cause. Consider targeted capacity for the checkout path, timeouts/circuit breakers, and async non-critical work. Avoid global over-provisioning. Set a performance budget and regression tests for the checkout journey.

### Leadership Reflection Questions

1. Do you have performance budgets written down?
2. What was your last optimization’s cost delta?
3. Where is caching hiding a design flaw?

### Interview Confidence Checklist

- [ ] Insists on measure-before-optimize
- [ ] Ties performance to business journeys
- [ ] Includes cost and complexity in the answer

---

## Simplicity vs Scalability

### Explanation

Simplicity vs scalability is choosing the least complex architecture that meets foreseeable load and change — not today’s fashion. Scale is a requirement with numbers; simplicity is a strategy for correctness and speed of change until those numbers demand more.

### Why interviewers ask these questions

- Classic Lead/Architect discriminator.
- Candidates often over-rotate to microservices.
- Enterprise panels want justified complexity.

### Real production examples

- Keeping a modular monolith for a team of eight until domain boundaries and scale demanded split.
- Introducing read replicas before introducing a new data store.
- Using a queue for spike absorption without jumping to event sourcing.

### Engineering tradeoffs

- Operational complexity of distributed systems vs single-deploy simplicity.
- Scaling a simple system vertically/horizontally vs redesign.
- Designing for 10x vs designing for infinity (usually wasteful).

### Common mistakes

- Microservices for org-chart reasons alone.
- “We might need to scale” without load projections.
- Simplicity as excuse for ignoring known growth cliffs.
- Distributed transaction sprawl without idempotency design.

### Senior Engineer perspective

Prefer simple designs with clear module seams so you *can* split later. Avoid distributed complexity until forced by team scale, deployment independence, or hard load limits.

### Lead Engineer perspective

Demand numbers: QPS, growth, failure domains, team ownership. Approve complexity only with operating model. Keep an evolutionary path. Educate stakeholders that “scalable” without operability is fragility.

### Interview Challenge

When would you refuse a microservices split proposed by a senior engineer?

### Suggested Answer

Refuse when team is small, domains are unclear, ops maturity is low, or the pain is internal module mess solvable by modularization. Approve when independent deploy, separate scaling, or team ownership boundaries are real and supported by platform (CI, observability, on-call). Propose modular monolith seams as a stepping stone.

### Leadership Reflection Questions

1. What complexity did you add that you would remove today?
2. What scale number would force your next architectural step?
3. Are your service boundaries aligned to team boundaries?

### Interview Confidence Checklist

- [ ] Challenges microservices fashion with criteria
- [ ] Uses numbers and team topology
- [ ] Shows evolutionary architecture thinking

---

## Progress Checklist

- [ ] Can run a tradeoff analysis with weighted criteria
- [ ] Can defend buy vs build with TCO and differentiation
- [ ] Manages debt and refactoring as risk work, not aesthetics
- [ ] Assesses risk with mitigations and residual acceptance
- [ ] Balances cost, performance, simplicity, and scale with evidence

---

## Notes

<!-- Fill with ADRs, buy/build decisions, debt repayments, and migration risks -->
