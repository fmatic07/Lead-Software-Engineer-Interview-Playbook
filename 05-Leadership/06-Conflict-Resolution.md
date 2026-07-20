# Conflict Resolution

> Technical disagreement as a design tool — resolve on evidence, ownership, and reversible decisions, not personality or politics.

---

## Technical Disagreements

### Explanation

Technical disagreement is disagreement about constraints, failure modes, and cost of change — not about who is “right.” Productive conflict surfaces hidden assumptions: latency budgets, data ownership, compliance, operational load, and who pages at 2 a.m. Unproductive conflict attaches identity to a design and treats critique as personal attack.

Lead engineers convert disagreement into a decision record: options considered, evidence used, what we optimize for, what we explicitly deprioritize, and how we will revisit the choice. The goal is a durable decision, not unanimous enthusiasm.

### Why interviewers ask these questions

- Tests whether you can disagree without fracturing delivery or trust.
- Reveals if you argue from production constraints or from preference and fashion.
- Distinguishes facilitation of decisions from forcing personal taste.

### Real production examples

- Backend wants sync REST for payment capture; platform argues for async with outbox because the payment provider’s P99 timeout exceeds the checkout SLA. Decision: async with explicit “pending” UX and reconciliation job.
- Two seniors split on Kafka vs. RabbitMQ for order events. Consensus failed until they compared consumer lag under peak, poison-message handling, and on-call runbooks — Kafka won on partitioning needs, not “modernity.”
- A rewrite proposal for a billing monolith dies when the disagreement is reframed: migration risk, dual-write window, and tax audit trail vs. clean domain model.

### Engineering tradeoffs

- Speed of decision vs. depth of analysis — shipping with a reversible choice often beats waiting for perfect alignment.
- Local team preference vs. org-standard platform — diverge only with a clear exit cost and ownership.
- Consensus quality vs. calendar pressure — forced agreement creates silent dissent that surfaces as sabotage-by-delay.

### Common mistakes

- Debating tools instead of constraints and failure modes.
- Using seniority or volume as a substitute for evidence.
- Leaving disagreements undocumented so the same fight recurs every quarter.
- “Agreeing” in the room and then implementing a different design.

### Senior Engineer perspective

Bring data: latency histograms, error budgets, migration blast radius, operational toil. Propose a spike with exit criteria rather than endless design debate. Be willing to lose gracefully when evidence favors another option — credibility compounds when you update your position publicly.

### Lead Engineer perspective

Own the decision frame. Time-box disagreement, assign a DRI for spikes, publish an ADR, and protect psychological safety so quieter engineers still challenge risky designs. Escalate only when the disagreement crosses team boundaries, risk appetite, or irreversible cost.

### Interview Challenge

Two staff engineers disagree on whether a new reporting feature should query the OLTP database with read replicas or build a denormalized projection. Deadline is two sprints. How do you resolve it?

### Suggested Answer

Restate shared goals (correctness, freshness SLA, OLTP protection). Quantify load: report query shape, concurrency, lock risk, and replica lag tolerance. Run a short spike measuring replica impact under peak write load vs. projection lag and build cost. Decide with an ADR: if reports can tolerate minutes of lag and OLTP is already near capacity, prefer projection; if freshness is seconds and volume is low, start with constrained replica queries plus query budget and kill switch. Schedule a revisit gate after first production week with metrics.

### Leadership Reflection Questions

1. What was the last technical disagreement you changed your mind on, and what evidence moved you?
2. How do you prevent “loudest voice wins” in design reviews?

### Interview Confidence Checklist

- [ ] Can narrate a disagreement resolved with metrics or a spike, not hierarchy
- [ ] Can write a one-page ADR under time pressure
- [ ] Can explain when consensus is unnecessary

---

## Handling Strong Opinions

### Explanation

Strong opinions are valuable when they compress experience into a clear recommendation with named risks. They become toxic when they refuse falsification. The leadership skill is separating conviction from rigidity: invite the strongest critique of the preferred option, and pre-commit to what evidence would change the call.

### Why interviewers ask these questions

- Strong opinions signal senior judgment — panels check whether they are evidence-backed or ego-backed.
- Tests emotional regulation when someone challenges your expertise publicly.
- Reveals coaching ability with opinionated seniors who derail meetings.

### Real production examples

- An architect insists “no microservices ever.” Lead reframes: for this domain’s team size and deploy cadence, modular monolith with clear module boundaries; revisit service extraction when independent scaling or ownership emerges.
- A senior refuses Feature Flags as “complexity theater.” After a failed Friday deploy, the postmortem makes flags non-negotiable for high-risk paths — opinion revised with incident evidence.
- A vocal engineer blocks GraphQL adoption; lead asks them to own the BFF design instead — channeling opposition into accountable design rather than veto by volume.

### Engineering tradeoffs

- Decisive opinions accelerate teams; unchallenged opinions create blind spots.
- Publicly reversing a strong stance costs status short-term and builds trust long-term.
- Containing a dominant voice vs. silencing expertise — contain the meeting behavior, not the insight.

### Common mistakes

- Matching intensity with intensity in meetings.
- Letting strong opinions set architecture without owning operations.
- Labeling dissenters as “not collaborative” when they are naming real risk.

### Senior Engineer perspective

State opinions as bets with confidence levels and kill criteria. Write the counter-argument yourself. Prefer “here is what would make me wrong” over “trust me.”

### Lead Engineer perspective

Set norms: opinions welcome; blocking requires alternative plus ownership. Private coaching for disruptive delivery of strong views; public credit when they prevent incidents. Do not outsource hard calls to the loudest person.

### Interview Challenge

In a design review, a respected principal dismisses your proposal mid-sentence. The room goes quiet. What do you do in the moment and after?

### Suggested Answer

In the moment: acknowledge the concern, restate the constraint you were solving, ask for the specific failure mode they foresee, and park non-blocking details. After: schedule a short 1:1 or async design note with options, metrics, and a decision owner. If their critique is valid, integrate it openly; if not, escalate with a crisp ADR and stakeholder alignment rather than a hallway war.

### Leadership Reflection Questions

1. How do you make your strongest technical opinions falsifiable?
2. When have you redirected a dominant voice into ownership instead of veto?

### Interview Confidence Checklist

- [ ] Can give a strong recommendation with explicit kill criteria
- [ ] Can describe recovering a derailed design review
- [ ] Can coach without public humiliation

---

## Resolving Team Conflict

### Explanation

Team conflict often looks technical but is usually about ownership, credit, interrupted focus, or unclear decision rights. Technical leads diagnose whether the conflict is task (what to build), process (how we decide), or relationship (trust eroded). Misdiagnosing relationship conflict as a tooling debate wastes sprints.

### Why interviewers ask these questions

- Lead roles own delivery health; unresolved conflict destroys throughput.
- Tests whether you intervene early or wait for HR-shaped crises.
- Distinguishes mediation from taking sides to “win.”

### Real production examples

- Frontend and backend deadlock on API contract ownership; conflict resolves when lead introduces consumer-driven contracts and a single DRI for the interface.
- Two engineers rotate blame after repeated merge conflicts; root cause is unclear ownership of a shared module — fix is module ownership and CODEOWNERS, not mediation theater.
- Pairing friction after a harsh code review; lead coaches review language and introduces “intent first, then style” norms.

### Engineering tradeoffs

- Early intervention vs. letting peers self-resolve — intervene when delivery or safety is affected.
- Transparency vs. confidentiality — protect individuals while making process fixes visible.
- Speed of harmony vs. surfacing real structural issues (ownership, staffing, unclear goals).

### Common mistakes

- Treating all conflict as personality; ignoring structural causes.
- Publicly adjudicating blame.
- Solving with process documents nobody reads while incentives stay misaligned.

### Senior Engineer perspective

Keep conflicts about artifacts: PRs, designs, SLAs. Offer to pair on the contested area. Escalate to lead when trust is gone or when you are a party to the conflict and cannot be neutral.

### Lead Engineer perspective

Separate facts from interpretations. Meet parties separately, then jointly with a shared problem statement. Produce a working agreement, decision rights (RACI/DACI), and a short follow-up. Involve manager/HR only for harassment, discrimination, or repeated bad-faith behavior — not for normal engineering friction.

### Interview Challenge

Two engineers refuse to review each other’s PRs after a heated Slack thread. A release is blocked. Walk through your 48-hour plan.

### Suggested Answer

Unblock release first: assign alternate reviewers and freeze the Slack thread. Meet each engineer privately to hear facts and impact. Joint session: establish PR review SLA, tone standards, and ownership of the disputed module. Document agreements; coach privately on communication. Track review latency for two weeks; escalate to manager if agreements are violated.

### Leadership Reflection Questions

1. What structural fix (ownership, DRI, interface) resolved a “people” conflict for you?
2. When should a lead stop mediating and involve a manager?

### Interview Confidence Checklist

- [ ] Can diagnose task vs. process vs. relationship conflict
- [ ] Has a concrete story of unblocking delivery during interpersonal friction
- [ ] Knows boundaries between technical lead and people-manager duties

---

## Building Consensus

### Explanation

Consensus is shared understanding of the decision and commitment to execute — not identical preferences. Fake consensus (“any objections?” silence) is dangerous. Real consensus emerges from clarifying the decision type (reversible vs. irreversible), who decides, what input is needed, and how dissent is recorded.

### Why interviewers ask these questions

- Cross-team delivery fails without alignment methods.
- Tests facilitation skill under disagreement and time pressure.
- Distinguishes democratic theater from accountable decision-making.

### Real production examples

- Platform migration consensus reached via RFC with comment period, then a named DRI decides — dissent captured in the RFC appendix.
- Product, security, and eng disagree on SSO deadline; lead builds a phased plan: mandatory for admins first, then all users, with risk accepted in writing by product.
- Architecture board cannot agree on event schema ownership; consensus forms around a schema registry with compatibility checks and a steward rotation.

### Engineering tradeoffs

- Broad consensus increases buy-in and slows start.
- Narrow decision rights speed execution but risk silent non-compliance.
- Written RFCs scale better than meetings; meetings resolve ambiguity faster when stakes are high.

### Common mistakes

- Seeking consensus for reversible decisions that need a DRI.
- Confusing silence with agreement.
- Reopening settled decisions without new evidence (“decision thrash”).

### Senior Engineer perspective

Contribute crisp options and recommendation. Support the decision once made, even if it was not your preference — unless new safety-critical evidence appears.

### Lead Engineer perspective

Choose the decision mode explicitly: consult-and-decide, consent (no blocking objections), or majority only for low-stakes preference. Publish decisions where the team works. Revisit only with new data or expired decision expiry dates.

### Interview Challenge

Three teams must agree on a shared customer ID format before a multi-quarter program. How do you drive consensus without a month of meetings?

### Suggested Answer

Publish a short RFC with constraints (uniqueness, PII, legacy IDs, lookup performance), 2–3 options, and a recommendation. Time-box comments (e.g., 5 business days). Hold one decision meeting with DRIs only. Capture dissent and migration plan. Lock via ADR and schema/compatibility tests. Assign a steward and an expiry/review date.

### Leadership Reflection Questions

1. When did you correctly avoid consensus and just decide?
2. How do you record dissent without reopening every decision?

### Interview Confidence Checklist

- [ ] Can explain consult-and-decide vs. true consensus
- [ ] Can run an RFC-to-ADR flow
- [ ] Can prevent decision thrash with expiry/revisit rules

---

## Escalation Strategy

### Explanation

Escalation is a tool for risk and decision latency — not a punishment channel. Escalate when blast radius, irreversibility, cross-team dependency, or safety exceeds your authority or information. Good escalation packages context, options, recommendation, and asked decision. Bad escalation dumps anxiety upward without a ask.

### Why interviewers ask these questions

- Lead candidates must know when not to “hero” alone.
- Tests judgment about risk appetite and organizational design.
- Reveals whether you escalate early with options or late with incidents.

### Real production examples

- Schema change touching billing is escalated to architecture + finance before merge because rollback is painful and regulatory.
- Vendor SLA dispute escalated when eng cannot accept risk alone — product and legal join.
- Persistent understaffing on a critical service escalated with toil metrics and incident count, not vague “team is tired.”

### Engineering tradeoffs

- Early escalation reduces surprise; over-escalation trains leaders to ignore you.
- Skipping escalation to “protect the team” can create organizational blind spots.
- Written escalation creates auditability; verbal-only loses context.

### Common mistakes

- Escalating personalities instead of decisions and risks.
- Escalating without a recommendation.
- Waiting until production is already burning.

### Senior Engineer perspective

Escalate through your lead with a one-pager: impact, urgency, options, recommendation. Keep working the mitigation path while waiting.

### Lead Engineer perspective

Define escalation triggers with the team (SEV definitions, irreversible migrations, security findings). Escalate laterally to peer leads before vertically when possible. Never surprise your manager with a customer-visible outage you saw coming.

### Interview Challenge

You believe a planned “big bang” cutover has unacceptable risk. Product insists on the date. Your manager is on leave. What is your escalation path?

### Suggested Answer

Document risk with blast radius, rollback plan gaps, and a safer phased alternative with dates. Align peer tech leads and on-call owners. Escalate to acting manager / director / risk owner with a clear ask: delay, reduce scope, or accept risk in writing. If forced to proceed, demand feature flags, dry-run, amplified monitoring, and staffing for the window — then record the accepted risk.

### Leadership Reflection Questions

1. What escalation did you delay too long, and what signal will you use next time?
2. How do you escalate without undermining a peer?

### Interview Confidence Checklist

- [ ] Can write a crisp escalation with options and ask
- [ ] Knows triggers for cross-team and executive escalation
- [ ] Can proceed safely when escalation is denied

---

## Giving and Receiving Feedback

### Explanation

Engineering feedback is about observable behavior, system impact, and next experiment — not character labels. Giving feedback well means timely, specific, and tied to shared standards (reliability, readability, operability). Receiving feedback well means separating ego from signal, asking clarifying questions, and closing the loop with changed behavior.

### Why interviewers ask these questions

- Mentorship and code review quality are core lead signals.
- Tests whether you can deliver hard feedback without demoralizing.
- Reveals growth mindset under critique from peers or managers.

### Real production examples

- Feedback on a PR that skipped idempotency keys after a duplicate-charge incident: cite the incident, show the pattern, request a checklist addition — engineer becomes the checklist owner.
- Lead receives feedback that design reviews are too long; switches to async RFC + 30-minute decision slot.
- Senior’s reviews are correct but harsh; coaching shifts comments to “risk / question / suggestion” framing and private voice for teaching moments.

### Engineering tradeoffs

- Immediate feedback maximizes learning; public feedback can shame.
- Written feedback scales; spoken feedback preserves nuance for sensitive topics.
- High standards without coaching create fear; coaching without standards create mediocrity.

### Common mistakes

- Feedback sandwiches that bury the real message.
- Waiting for performance cycles for operational feedback.
- Defending every critique instead of exploring it.

### Senior Engineer perspective

Seek feedback after incidents and designs: “What would you change about how I handled X?” In reviews, prefer questions that teach over edicts when the risk is low; be direct when safety is involved.

### Lead Engineer perspective

Make feedback a continuous operating system: PR norms, retros, 1:1s. Model receiving feedback in public. Separate performance management (manager) from technical coaching (lead), but coordinate when patterns persist.

### Interview Challenge

A teammate’s services repeatedly lack dashboards and runbooks. Previous soft hints failed. How do you give feedback and change the system?

### Suggested Answer

Private, specific feedback citing last two incidents and on-call load. Agree on a Definition of Done: metrics, alerts, runbook, and rollback notes before “done.” Offer to pair on the first service. Make the DoD a team standard enforced in review, not a personal preference. Follow up in two weeks with evidence of change or escalate to manager if production risk continues.

### Leadership Reflection Questions

1. What hard feedback did you give that improved someone’s production impact?
2. What feedback did you resist, and what did you learn when you finally acted on it?

### Interview Confidence Checklist

- [ ] Can deliver hard technical feedback with examples and a path forward
- [ ] Can describe changing behavior after receiving tough feedback
- [ ] Ties feedback to system standards, not personal taste
