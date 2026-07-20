# Team Collaboration

> Delivery is a multiplayer sport: Leads create clarity across Product, QA, DevOps, and peer teams — especially when conversations get hard.

---

## Purpose

Prepare for cross-functional leadership questions common at ING, Globe, Deltek, Maya, and GovTech SG: partnering with PM/QA/DevOps, collaborating across teams, handling conflict, managing expectations, and communicating with stakeholders under uncertainty.

---

## Topics Covered

- [ ] Working with Product Managers
- [ ] Working with QA
- [ ] Working with DevOps
- [ ] Cross-team Collaboration
- [ ] Handling Difficult Conversations
- [ ] Managing Expectations
- [ ] Stakeholder Communication

---

## Working with Product Managers

### Explanation

Working with PMs means co-owning outcomes: engineers bring feasibility, risk, and sequencing; PMs bring customer value and priority. Strong Leads translate constraints into options and refuse false certainty — while staying committed to product success.

### Why interviewers ask these questions

- Lead roles live in the PM interface.
- Tests partnership vs adversarial “no.”
- Reveals product sense and negotiation skill.

### Real production examples

- Offering a thin MVP for regulatory deadline plus a dated hardening phase.
- Explaining why a “simple UI change” needs a data backfill and two sprints.
- Jointly cutting scope when a dependency slipped, then communicating one message to stakeholders.

### Engineering tradeoffs

- Pure product velocity vs engineering risk budget.
- Building what was asked vs solving the underlying user job.
- Early pushback vs implementing then surprising with delays.

### Common mistakes

- Saying no without options.
- Over-promising to please.
- Hiding technical work until it explodes the timeline.
- Treating PM as a ticket vending machine.

### Senior Engineer perspective

Clarify acceptance criteria. Surface technical implications early. Estimate with ranges and risks. Suggest simpler product paths when engineering cost is disproportionate.

### Lead Engineer perspective

Establish a planning rhythm with PM: capacity, risk allocation, dependency map. Co-write release notes risk language. Escalate together. Protect reliability work with visible tradeoffs. Build trust through predictable delivery.

### Interview Challenge

PM insists on a date that engineering considers unsafe for a payments change. What do you do?

### Suggested Answer

Quantify risks and failure scenarios, propose safe scope options (flagged partial release, dual-run, delayed non-critical pieces), present residual risk if date holds, and require an explicit business risk acceptor if overridden. Do not silently accept an unsafe plan; do not stonewall without alternatives.

### Leadership Reflection Questions

1. Do PMs trust your estimates — why or why not?
2. How often do you bring options vs binary no?
3. Is reliability work visible in the roadmap?

### Interview Confidence Checklist

- [ ] Shows PM partnership with options
- [ ] Can negotiate dates via scope/risk
- [ ] Has a hard date conversation story

---

## Working with QA

### Explanation

Working with QA is shared quality ownership: engineers build testability and automate risk-based checks; QA challenges assumptions, explores edge cases, and guards release readiness. Leaders avoid “throw over the wall” and “QA owns quality alone.”

### Why interviewers ask these questions

- Mature orgs still have QA partners; shift-left is expected.
- Probes respect for quality roles without abdication.
- Common friction point in delivery interviews.

### Real production examples

- Involving QA in design review for a wallet transfer to list abuse cases early.
- Agreeing automation ownership: engineers own unit/contract; QA owns exploratory + critical journey suites with eng support.
- Joint release checklist for high-risk launches.

### Engineering tradeoffs

- Engineer-owned automation vs specialized QA depth.
- Speed of shift-left vs losing exploratory insight.
- QA at end vs QA embedded — capacity models differ.

### Common mistakes

- Treating QA as gatekeepers to blame.
- Skipping QA involvement until UAT panic.
- Expecting QA to catch design flaws without specs.
- Automating nothing and dumping everything on manual QA.

### Senior Engineer perspective

Deliver testable slices with clear environments and data. Fix defects with regression tests. Invite QA early on complex changes. Respect bug reports; debate severity with evidence.

### Lead Engineer perspective

Define quality ownership matrix with QA lead. Align DoD and environments. Prioritize test environment stability as delivery infrastructure. Resolve severity disputes with customer impact criteria. Include QA in planning capacity.

### Interview Challenge

QA marks a bug P0; you think it is P2 edge-case. How do you resolve?

### Suggested Answer

Reproduce, assess user impact/frequency/workaround/regulatory exposure, compare against severity rubric, decide with shared criteria, and document. If disagreement remains, escalate to Lead/PM with impact framing — not ego. Add monitoring if uncertainty remains.

### Leadership Reflection Questions

1. When does QA first see your work?
2. Is environment instability your silent sprint killer?
3. Do engineers write regressions for QA-found defects?

### Interview Confidence Checklist

- [ ] Describes shared quality ownership
- [ ] Involves QA early in the narrative
- [ ] Has a severity negotiation example

---

## Working with DevOps

### Explanation

Working with DevOps/Platform/SRE means co-designing operability: CI/CD, environments, observability, secrets, scaling, and incident response. Leaders treat platform constraints as design inputs and contribute to paved roads instead of one-off snowflakes.

### Why interviewers ask these questions

- Cloud delivery is cross-functional by default.
- Distinguishes “works on my machine” engineers from production engineers.
- GovTech/bank panels care about controlled release paths.

### Real production examples

- Partnering on canary + auto-rollback for a high-traffic API.
- Agreeing resource quotas and HPA policies before launch.
- Collaborating on golden signals dashboards as part of feature delivery.

### Engineering tradeoffs

- Team-owned pipelines vs centralized platform standards.
- Speed of custom infra vs long-term supportability.
- Self-service platform vs ticket-driven DevOps bottlenecks.

### Common mistakes

- Throwing deploy problems over the wall late.
- Bypassing platform standards for convenience.
- Ignoring cost/observability until production pain.
- Treating DevOps as “the people who click deploy.”

### Senior Engineer perspective

Build with 12-factor discipline: config, health checks, metrics, graceful shutdown. Learn enough CI/CD to diagnose. Ask for paved-road solutions first.

### Lead Engineer perspective

Engage DevOps in design for new services. Negotiate standards and exceptions with expiry. Fund toil reduction that unlocks many teams. Include ops readiness in go-live. Share on-call learnings into platform backlog.

### Interview Challenge

Platform team cannot support your custom deployment for six weeks. How do you deliver?

### Suggested Answer

Challenge whether custom is necessary; prefer paved road even if slightly constrained. If truly blocked, propose a temporary compliant path with explicit debt ticket and sunset date, reduce scope to fit platform capabilities, or negotiate priority with business impact evidence. Avoid shadow infra that becomes permanent.

### Leadership Reflection Questions

1. Are you a platform customer or a platform adversary?
2. What snowflake infra are you still carrying?
3. Do go-lives include ops readiness criteria?

### Interview Confidence Checklist

- [ ] Speaks paved road + exceptions with expiry
- [ ] Includes observability in delivery
- [ ] Has a DevOps partnership story

---

## Cross-team Collaboration

### Explanation

Cross-team collaboration is delivering outcomes that span ownership boundaries: shared contracts, sequenced releases, joint incidents, and clear interface ownership. Leaders manage dependencies explicitly — dates, contracts, and escalation paths.

### Why interviewers ask these questions

- Enterprise value streams cross squads.
- Tests coordination without formal authority.
- Common failure mode: integration week chaos.

### Real production examples

- Aligning account, ledger, and notifications teams on a settlement event schema with a versioning plan.
- Running a shared integration test environment with ownership rules.
- Creating a cross-team RFC and decision deadline for an API break.

### Engineering tradeoffs

- Tight coupling for speed vs explicit contracts for autonomy.
- Central coordination vs decentralized alignment cost.
- Waiting for perfect multi-team plan vs incremental integration.

### Common mistakes

- Assuming other teams share your priorities.
- Late discovery of contract mismatches.
- Meetings without decision owners.
- Integration only at the end of the program.

### Senior Engineer perspective

Define consumer/provider contract needs early. Write clear interface docs. Raise dependency risks in planning. Be a reliable partner on commitments.

### Lead Engineer perspective

Map dependencies, owners, and critical path. Prefer contract tests and staged rollouts. Establish joint milestones. Escalate blocked dependencies with options. Protect interface stability.

### Interview Challenge

Another team will miss an API you depend on by three weeks. What do you do this week?

### Suggested Answer

Confirm the slip and root cause, identify interim mocks/stubs/feature flags, re-sequence your work to non-blocked items, renegotiate scope/dates with PM jointly, document the dependency risk, and agree on a new integration checkpoint. Avoid idle waiting or blame threads.

### Leadership Reflection Questions

1. How early do you integrate across teams?
2. Who owns each interface you depend on?
3. What is your escalation path when priorities conflict?

### Interview Confidence Checklist

- [ ] Shows dependency management mechanics
- [ ] Uses contracts/flags/staged integration
- [ ] Has a cross-team delivery story

---

## Handling Difficult Conversations

### Explanation

Difficult conversations are direct discussions about risk, performance of work, broken commitments, or conflicting designs — held with respect and specificity. Engineering leaders do not outsource all conflict; they address issues early while facts are still cheap.

### Why interviewers ask these questions

- Behavioral + leadership overlap.
- Predicts whether you let rot fester.
- Tests emotional regulation under pressure.

### Real production examples

- Telling a peer their design fails threat modeling before implementation sunk cost.
- Addressing a teammate whose reviews are blocking with sarcasm.
- Informing stakeholders that a committed date will slip — before the date.

### Engineering tradeoffs

- Immediate candor vs waiting for more data.
- Private vs public settings.
- Harmonizing relationships vs protecting production risk.

### Common mistakes

- Avoiding until explosion.
- Ambush in large meetings.
- Personal attacks instead of impact statements.
- False harmony that leaves risk unowned.

### Senior Engineer perspective

Prepare facts and desired outcome. Use situation-behavior-impact. Listen. Agree on next steps. Follow up.

### Lead Engineer perspective

Coach the team to raise issues early. Model calm disagreement. Separate technical conflict (good) from interpersonal disrespect (not ok). Involve EM when it becomes performance management. Keep focus on shared goals.

### Interview Challenge

A senior peer publicly dismissed your risk concerns. The release is in two days. What do you do?

### Suggested Answer

De-escalate publicly; move to a private/fact-based discussion with the risk register and blast radius. Invite a neutral tech lead/architect if needed. If residual risk is high, escalate with options and a recommendation. Document the decision and acceptance. Do not turn it into a status fight.

### Leadership Reflection Questions

1. Which conversation are you postponing?
2. Do you argue to win or to reduce risk?
3. How do you repair trust after conflict?

### Interview Confidence Checklist

- [ ] Has a difficult conversation STAR story
- [ ] Shows early, specific, respectful challenge
- [ ] Knows when to escalate

---

## Managing Expectations

### Explanation

Managing expectations is aligning stakeholders on what will be delivered, by when, at what quality, and with what uncertainty. Leaders communicate ranges, risks, and changes early — so trust compounds instead of collapsing at release time.

### Why interviewers ask these questions

- Delivery credibility is a Lead currency.
- Tests honesty under pressure.
- Enterprise programs punish silent slips.

### Real production examples

- Giving estimate ranges with confidence levels and top risks.
- Mid-sprint communication that a security finding forces a cut.
- Setting “preview” vs “GA” expectations for a partial rollout.

### Engineering tradeoffs

- Optimistic commitments that please now vs realistic ones that retain trust.
- Buffer padding vs transparency about uncertainty.
- Frequent updates vs noise.

### Common mistakes

- Sandbagging so extremely you lose credibility another way.
- Precision estimates for high-uncertainty work.
- Updating only when asked.
- Promising best-case as the plan.

### Senior Engineer perspective

Estimate with assumptions listed. Update when assumptions break. Do not hide bad news. Offer revised plans, not only problems.

### Lead Engineer perspective

Create a communication cadence. Use risk burndown language. Align one narrative across eng/PM. Separate commitment dates from target dates. Teach the team shared expectation hygiene.

### Interview Challenge

You discover a critical defect five days before launch. How do you manage expectations?

### Suggested Answer

Assess severity and fix/rollback options immediately, present impact and choices (delay, partial launch, workaround) to PM/stakeholders the same day, recommend a path with risk, and communicate a single coordinated message. Preserve trust through speed and clarity, not heroic silence.

### Leadership Reflection Questions

1. Do stakeholders hear bad news from you first?
2. Are your estimates ranges or false precision?
3. What commitment style damaged trust before — and what changed?

### Interview Confidence Checklist

- [ ] Communicates uncertainty explicitly
- [ ] Updates early with options
- [ ] Distinguishes targets from commitments

---

## Stakeholder Communication

### Explanation

Stakeholder communication is translating engineering reality for audiences that do not share your jargon: executives, risk/compliance, customer support, partner teams. Leaders match altitude — outcomes, risk, decisions needed — and leave implementation detail for the right forum.

### Why interviewers ask these questions

- Tech Leads and Architects are communication roles.
- Tests audience adaptation.
- Critical in regulated environments with audit/risk partners.

### Real production examples

- Explaining eventual consistency to support leaders with customer-visible wording and workaround scripts.
- Executive update: green/amber/red on outcomes, risks, decisions needed — not task lists.
- Compliance walkthrough of controls for a new data flow (encryption, access, retention).

### Engineering tradeoffs

- Transparency vs overwhelming non-technical audiences.
- Optimistic narrative vs risk realism.
- Centralized comms vs every engineer speaking differently.

### Common mistakes

- Jargon walls (“we need a saga with idempotent consumers…”).
- Hiding uncertainty behind green status.
- Inconsistent messages from eng and PM.
- Only communicating when things are on fire.

### Senior Engineer perspective

Practice plain-language explanations of your system’s user impact. Prepare diagrams. Support your Lead with accurate status.

### Lead Engineer perspective

Own the engineering narrative. Align with PM before stakeholder forums. Provide decision memos with options. Tailor depth to audience. Establish periodic trusted updates so urgent messages are believed.

### Interview Challenge

Explain a production incident to a non-technical director in 90 seconds.

### Suggested Answer

Structure: what customers felt → what failed in plain terms → how we mitigated → residual risk → what we are changing to prevent recurrence → whether they need to decide anything. No framework soup. Offer a deeper tech follow-up if asked.

### Leadership Reflection Questions

1. Can you explain your system’s failure modes in business language?
2. Who are your stakeholders beyond PM?
3. Do your status updates request decisions clearly?

### Interview Confidence Checklist

- [ ] Adapts altitude to audience
- [ ] Aligns message with PM
- [ ] Can do a 90-second incident brief

---

## Progress Checklist

- [ ] Partners with PM using options and visible risk tradeoffs
- [ ] Shares quality ownership with QA and ops readiness with DevOps
- [ ] Manages cross-team dependencies with contracts and escalation
- [ ] Handles difficult conversations early and specifically
- [ ] Manages expectations and stakeholder comms with trust-preserving clarity

---

## Notes

<!-- Fill with PM/QA/DevOps stories, conflict examples, and stakeholder briefs -->
