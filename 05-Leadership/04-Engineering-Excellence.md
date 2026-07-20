# Engineering Excellence

> Excellence is the system of standards, reviews, tests, and feedback loops that make quality the default — not a heroics contest.

---

## Purpose

Show panels you can institutionalize quality: code review culture, standards, docs, testing strategy, CI/CD gates, continuous improvement, Definition of Done, and metrics that drive behavior without becoming vanity dashboards.

---

## Topics Covered

- [ ] Code Reviews
- [ ] Coding Standards
- [ ] Documentation
- [ ] Testing Strategy
- [ ] CI/CD Quality Gates
- [ ] Continuous Improvement
- [ ] Definition of Done
- [ ] Engineering Metrics

---

## Code Reviews

### Explanation

Code review is a quality and knowledge-sharing control: correctness, security, readability, test adequacy, and design fit. Leaders set the tone — reviews are about the change and the standard, not dominance.

### Why interviewers ask these questions

- Universal Senior/Lead expectation.
- Reveals culture: blocking nitpicks vs risk-focused review.
- Ties to mentoring and incident prevention.

### Real production examples

- Catching a missing idempotency key in a refund endpoint during review.
- Review SLAs: first response within one business day to unblock flow.
- Risk-based review: two reviewers on money paths; lighter on docs/tests-only PRs.

### Engineering tradeoffs

- Thoroughness vs cycle time.
- Async review vs pairing for complex changes.
- Enforcing standards in review vs automating them in linters/CI.

### Common mistakes

- Reviews that only discuss formatting (should be automated).
- Rubber-stamping to hit velocity metrics.
- Hostile tone that suppresses questions.
- Huge PRs that cannot be reviewed meaningfully.

### Senior Engineer perspective

Review for defects, design, and teachable moments. Prefer small PRs. Approve when residual risk is acceptable; request changes with clear rationale. Automate nits.

### Lead Engineer perspective

Define review norms and SLA. Route high-risk paths to experienced reviewers. Measure review lag as a delivery constraint. Coach reviewers on tone. Escalate chronic rubber-stamping.

### Interview Challenge

Reviews are taking four days and features stall. What do you change?

### Suggested Answer

Shrink PR size, set first-response SLA, use risk-based reviewer assignment, automate style, protect review time in calendar norms, and escalate blocked PRs daily. If needed, temporary pair-review for critical path items. Optimize for cycle time without dropping risk controls on money/auth.

### Leadership Reflection Questions

1. What did your last review actually prevent?
2. Are nits automated or human-enforced?
3. How do juniors experience your review tone?

### Interview Confidence Checklist

- [ ] Describes risk-based review, not one-size
- [ ] Balances quality and flow
- [ ] Has improved review culture concretely

---

## Coding Standards

### Explanation

Coding standards are agreed constraints that reduce cognitive load: style, structure, error handling, logging, API conventions, and forbidden patterns. Good standards are enforced mostly by tooling and exemplified in canonical modules.

### Why interviewers ask these questions

- Consistency matters in large enterprise codebases.
- Tests whether you distinguish taste from safety standards.
- Shows platform thinking.

### Real production examples

- Standardizing problem+json error responses across APIs.
- Mandating structured logging correlation IDs.
- ArchUnit rules preventing domain → infrastructure leakage in a hexagonal layout.

### Engineering tradeoffs

- Strict uniformity vs pragmatic exceptions.
- Org-wide standards vs team-local conventions.
- Upfront standard investment vs inconsistency tax.

### Common mistakes

- 40-page standards nobody reads.
- Standards without auto-enforcement.
- Bike-shedding naming while ignoring null-safety or transaction rules.
- Weaponizing standards in personal conflicts.

### Senior Engineer perspective

Follow and improve standards with PRs to the shared config. Explain the *why* in reviews. Propose exceptions with expiry when justified.

### Lead Engineer perspective

Own a thin living standard: safety-critical rules first. Enforce via CI. Review exceptions. Align with architecture principles. Keep the doc short; put examples in code.

### Interview Challenge

Two seniors disagree on Optional vs null in the codebase. How do you settle it?

### Suggested Answer

Pick a default aligned with existing codebase and null-safety goals, document with examples, enforce via lint where possible, and ban mixed styles in new modules. Do not prolong debate; decide, write it down, move on. Revisit only with new evidence (bug patterns).

### Leadership Reflection Questions

1. Which standards prevent real defects vs encode taste?
2. What is enforced automatically today?
3. Where are exceptions rotting into the new normal?

### Interview Confidence Checklist

- [ ] Prefers thin, enforced standards
- [ ] Separates safety rules from style
- [ ] Can decide and document disputes

---

## Documentation

### Explanation

Documentation is operational knowledge: how to run, test, deploy, decide, and recover. Leaders prioritize docs that reduce incident time and onboarding cost — ADRs, runbooks, API contracts, and threat models — over novel-length wikis.

### Why interviewers ask these questions

- Auditability and continuity matter in regulated orgs.
- Distinguishes useful docs from coverage metrics.
- Links to ownership and bus factor.

### Real production examples

- ADR for choosing outbox pattern, linked from the module README.
- Runbook with exact queries/dashboards for payment lag alerts.
- OpenAPI as the contract source of truth with consumer checks.

### Engineering tradeoffs

- Doc investment vs delivery speed.
- Central wiki vs docs-as-code next to the repo.
- Completeness vs accuracy (stale perfection is harmful).

### Common mistakes

- Documenting only after audits demand it.
- Docs that duplicate code instead of explaining intent/constraints.
- No owner for doc freshness.
- Screenshots of UIs that change weekly as the only guide.

### Senior Engineer perspective

Update docs in the same PR as behavior changes for APIs and runbooks. Write ADRs for non-obvious decisions. Delete stale docs.

### Lead Engineer perspective

Make critical docs part of Definition of Done. Link runbooks from alerts. Review doc quality in design reviews. Fund doc debt when onboarding/incident pain shows up.

### Interview Challenge

What documentation would you require before a service can take production traffic?

### Suggested Answer

Ownership/on-call, SLO/alerts/runbook, architecture diagram, API contract, deployment/rollback steps, data stores and migration notes, security considerations, and dependency list. Prefer minimal accurate over exhaustive stale.

### Leadership Reflection Questions

1. Which doc would save you at 2 a.m.?
2. What doc is lying today?
3. Are ADRs actually findable?

### Interview Confidence Checklist

- [ ] Prioritizes runbooks/ADRs/contracts
- [ ] Treats stale docs as defects
- [ ] Ties docs to DoD for risky work

---

## Testing Strategy

### Explanation

Testing strategy allocates scarce effort across unit, integration, contract, e2e, and exploratory tests based on risk. Leaders design a pyramid (or trophy) that catches defects cheaply while protecting critical journeys.

### Why interviewers ask these questions

- Quality without endless e2e suites is a Lead skill.
- Fintech/telco paths need stronger guarantees.
- Reveals understanding of flaky-test economics.

### Real production examples

- Contract tests between account and ledger services to unlock independent deploys.
- Heavy unit tests for pricing rules; integration tests for persistence and transactions.
- Few critical-path e2e smoke tests in CI; deeper e2e nightly.

### Engineering tradeoffs

- Coverage % vs risk-based testing.
- Deterministic unit speed vs realistic integration confidence.
- Flaky e2e “confidence” that trains people to ignore CI.

### Common mistakes

- 100% coverage goals that test getters and ignore concurrency.
- Only manual QA as the quality strategy.
- E2E for everything — slow, flaky, expensive.
- No tests around idempotency and failure injection.

### Senior Engineer perspective

Write tests that document behavior at boundaries. Prefer fast feedback locally. Add regression tests for every production bug you touch.

### Lead Engineer perspective

Define risk-based testing standards per domain. Track flaky tests as P0 engineering work. Align with QA on ownership of layers. Require tests for money/auth/PII paths as release gates.

### Interview Challenge

How do you test a distributed payment flow without a huge brittle e2e suite?

### Suggested Answer

Unit-test domain rules, integration-test each service’s DB/messaging adapters, contract-test interfaces, use testcontainers where valuable, and keep a small smoke e2e for the happy path plus one failure path. Add idempotency and duplicate-message tests. Use staging canaries for residual risk.

### Leadership Reflection Questions

1. Where is your pyramid inverted?
2. What flake rate trains distrust of CI?
3. Which critical path lacks automated protection?

### Interview Confidence Checklist

- [ ] Speaks risk-based layers, not coverage theater
- [ ] Addresses flaky tests as first-class
- [ ] Has a payments/critical-path testing story

---

## CI/CD Quality Gates

### Explanation

Quality gates are automated checks that block promotion when standards fail: compile, tests, lint, security scans, coverage floors on critical modules, contract checks, and deployment policies (approvals, canaries). Leaders design gates that enforce risk controls without becoming bureaucracy.

### Why interviewers ask these questions

- Modern Lead expectation in Docker/AWS enterprise shops.
- Distinguishes gatekeeping from enabling fast safe delivery.
- Security/compliance panels care about SDLC controls.

### Real production examples

- Blocking merge on failed contract tests.
- Requiring SAST/dependency scan with severities gated.
- Canary gate: auto-rollback on error-rate spike before full promote.

### Engineering tradeoffs

- Strict gates vs developer friction.
- Blocking on all CVEs vs risk-accepted vulnerabilities with expiry.
- More environments vs promotion complexity.

### Common mistakes

- Gates so slow people route around them.
- Non-blocking “gates” that everyone ignores.
- No path to break-glass with audit in true emergencies.
- Identical gates for a README typo and a ledger change.

### Senior Engineer perspective

Keep builds green. Fix flakes. Add checks that catch real failure modes you have seen. Do not bypass casually.

### Lead Engineer perspective

Tier gates by risk. Keep pipeline fast (parallelize, cache). Own break-glass policy with audit. Partner with DevOps/Sec on scanner noise. Measure lead time impact of each gate.

### Interview Challenge

Developers bypass CI with force merges. How do you respond?

### Suggested Answer

Remove the ability where policy requires; if process allowed it, tighten branch protection. Understand why (flakes, slow CI, emergency). Fix root causes, introduce audited break-glass, and address cultural incentives that reward bypass. Pair policy with enabling faster reliable pipelines.

### Leadership Reflection Questions

1. Which gate catches real defects?
2. Which gate is pure friction?
3. Is break-glass audited?

### Interview Confidence Checklist

- [ ] Describes risk-tiered gates
- [ ] Balances speed and control
- [ ] Has a pipeline improvement story

---

## Continuous Improvement

### Explanation

Continuous improvement is a deliberate loop: observe delivery/operations pain, change a practice or tool, measure, keep or discard. Leaders institutionalize learning from incidents, retros, and metrics — small changes compounding over quarters.

### Why interviewers ask these questions

- Separates static process from learning organizations.
- Shows whether retros produce change.
- Enterprise transformation language — panels want substance.

### Real production examples

- After three similar null bugs, adding a static analysis rule and a review checklist item.
- Reducing deploy time from 40 to 12 minutes by parallelizing tests — then raising release frequency.
- Turning incident themes into a quarterly reliability roadmap.

### Engineering tradeoffs

- Improvement work vs feature pressure.
- Local team experiments vs org standardization.
- Tool changes vs habit/practice changes.

### Common mistakes

- Retros with no action owners.
- Changing everything after every incident (whiplash).
- Copying Spotify/Netflix rituals without problems they solve.
- Improvement theater: posters, no metrics.

### Senior Engineer perspective

Bring concrete improvement proposals from friction you feel. Implement small tools/checklists. Follow through on retro actions you own.

### Lead Engineer perspective

Maintain an improvement backlog with capacity. Track outcomes. Limit WIP of process changes. Spread successful experiments. Connect improvements to incident and delivery data.

### Interview Challenge

Your retros keep repeating “tests are flaky.” What do you do differently?

### Suggested Answer

Stop treating it as a lament. Create a owned flaky-test quarantine policy, SLO for flake rate, dedicated capacity each sprint, and CI visibility. Report trend to stakeholders. Make ignoring flakes harder than fixing them.

### Leadership Reflection Questions

1. What improved measurably last quarter because of a retro?
2. How many process changes are in flight?
3. Do incidents produce systemic fixes?

### Interview Confidence Checklist

- [ ] Shows closed-loop improvement with metrics
- [ ] Avoids cargo-cult process
- [ ] Ties reliability work to capacity

---

## Definition of Done

### Explanation

Definition of Done (DoD) is the agreed checklist that makes work releasable: code, tests, docs/runbooks, observability, security considerations, and product acceptance. Leaders keep DoD explicit and risk-sensitive so “done” means production-ready, not “dev complete.”

### Why interviewers ask these questions

- Prevents hidden work and production surprises.
- Tests cross-functional clarity with QA/PM.
- Mature teams argue about DoD content — good sign.

### Real production examples

- DoD includes metrics/alerts for new endpoints.
- Feature-flag off plan and rollback noted before merge.
- Accessibility/security checks for customer-facing flows in GovTech-style delivery.

### Engineering tradeoffs

- Strict DoD vs speed for low-risk changes.
- Team DoD vs org release policy.
- Perfect checklists vs ignored walls of text.

### Common mistakes

- DoD that is only “code merged.”
- Identical DoD for spikes and production features.
- DoD owned only by QA at the end.
- Never updating DoD after incidents.

### Senior Engineer perspective

Refuse to call work done without tests and operability for your risk level. Push back on pressure to skip DoD on critical paths.

### Lead Engineer perspective

Co-author DoD with QA/PM/DevOps. Tier it. Audit compliance without blame; fix systemic gaps. Use DoD breaches as improvement fuel.

### Interview Challenge

Product marks a story done because the UI works in a demo, but there are no alerts. What do you do?

### Suggested Answer

Clarify that demo-complete ≠ production-done. Explain operational risk, require minimum telemetry/alerts before release, offer to help add them quickly, and update DoD communication so expectations match. Escalate if release pressure overrides safety on high-impact flows — with options.

### Leadership Reflection Questions

1. Is your DoD written and actually used?
2. What item was added after the last incident?
3. Do spikes have a different done state?

### Interview Confidence Checklist

- [ ] Distinguishes demo-done from prod-done
- [ ] Mentions tiered DoD
- [ ] Cross-functional ownership of DoD

---

## Engineering Metrics

### Explanation

Engineering metrics are signals used to improve system and team performance: DORA-like measures (lead time, deploy frequency, change fail rate, MTTR), reliability SLOs, and quality indicators. Leaders choose metrics that drive healthy behavior and reject those that incentivize gaming.

### Why interviewers ask these questions

- Data-informed leadership is expected at Lead level.
- Distinguishes vanity metrics from decision metrics.
- Common in mature orgs (ING/GovTech transformation contexts).

### Real production examples

- Using change fail rate + incident themes to justify more contract tests.
- Tracking review latency as a flow metric.
- SLO error budgets governing feature vs reliability work.

### Engineering tradeoffs

- Individual metrics vs team metrics (prefer team/system).
- Quantitative clarity vs qualitative judgment.
- Metric overhead vs insight.

### Common mistakes

- Lines of code / commit counts as performance.
- Story points as productivity truth.
- 100% uptime targets without error budgets.
- Dashboards nobody reviews.

### Senior Engineer perspective

Instrument your services with useful SLIs. Use metrics in design arguments. Avoid personal vanity stats.

### Lead Engineer perspective

Pick a small metric set tied to outcomes. Review in team rituals. Guard against misuse in performance punishment. Combine metrics with narrative (incidents, customer impact). Adjust when metrics stop correlating with health.

### Interview Challenge

Management wants individual velocity rankings from Jira points. How do you respond?

### Suggested Answer

Explain perverse incentives (inflate estimates, avoid hard work, avoid helping others). Propose team-level flow and quality metrics plus qualitative outcomes. Offer better questions for performance: scope of ownership, reliability impact, mentoring, decision quality. Redirect the conversation to system bottlenecks.

### Leadership Reflection Questions

1. Which metric changed a decision last month?
2. What metric are people gaming?
3. Do you have error budgets with teeth?

### Interview Confidence Checklist

- [ ] Names DORA/SLO style metrics correctly
- [ ] Rejects harmful individual productivity metrics
- [ ] Uses metrics in a real improvement story

---

## Progress Checklist

- [ ] Can describe risk-based code review and standards enforcement
- [ ] Designs testing strategy and CI gates for critical paths
- [ ] Treats documentation and DoD as operational controls
- [ ] Runs continuous improvement with owned actions
- [ ] Chooses engineering metrics that drive healthy behavior

---

## Notes

<!-- Fill with review norms, gate designs, DoD, and metric-driven improvements -->
