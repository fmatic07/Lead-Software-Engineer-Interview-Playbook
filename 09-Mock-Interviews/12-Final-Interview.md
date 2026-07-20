# 12 — Final Interview

> Realistic conversations with Director of Engineering, VP Engineering, and CTO.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 30–60 minutes |
| Nature | Strategic, cultural, trust |
| Goal | Confirm senior judgment and long-term fit |

---

## Purpose

Final rounds test whether you can operate at altitude: strategy, risk, org design, and executive communication — while remaining technically credible.

---

## How Interviewers Evaluate

| Level | Emphasis |
|-------|----------|
| Director | Delivery systems, cross-team influence, talent |
| VP | Org leverage, portfolio tradeoffs, culture at scale |
| CTO | Technical strategy, bets, architecture fitness for business |

They ask fewer trivia questions. They probe how you think about *companies*, not only services.

---

## Common Mistakes

- Diving into low-level implementation unprompted.
- Agreeing with everything (no spine).
- Speaking only from IC hero stories without leverage.
- Criticizing prior employers unprofessionally.
- No curiosity about their strategy.

---

## Excellent Communication Techniques

- Bottom line first; then 2 supporting points.
- Use "option A/B with costs" language.
- Tie tech to customer, risk, cost, speed.
- Leave room for dialogue — ask them questions.

---

## Confidence Tips

Read recent company news, engineering blog, and product launches. Prepare one informed point of view.

---

## Ideal Answer Framework

**Executive summary → Context → Options → Recommendation → Risks → Ask**

---

## Conversation Set A — Director of Engineering

### A1. Delivery & Org

**Interviewer (Director):** Our teams keep missing commitments. What would you look at in your first 60 days?

**Candidate:** I'd inspect: how commitments are made (uncertainty buffers?), dependency mapping, WIP limits, quality escapes causing rework, on-call load, and whether "done" includes operability. I'd sample recent missed launches for root patterns, not individual blame. Then propose 1–2 process changes with metrics (forecast accuracy, change fail rate).

**Follow-up:** People say process is the problem; engineers say product churns.

**Lead/Architect answer:** Both can be true — install a lightweight change-control for mid-sprint interrupts with visible cost; improve estimation only after interrupt rate is honest.

---

### A2. Cross-Team Conflict

**Interviewer:** Platform and product squads are at war over priorities. Your approach?

**Candidate:** Shared OKR or explicit capacity contract; written intake with SLAs; rotate embeds; escalate chronic mismatch to Director/VP with data. Culture fix needs incentives aligned — not pizza parties.

---

### A3. Talent

**Interviewer:** How do you raise the hiring bar without slowing hiring to zero?

**Candidate:** Structured scorecards; calibrated interviewers; better sourcing; internship/junior pipelines; track false rejects. Speed comes from preparedness, not lowered standards.

---

## Conversation Set B — VP Engineering

### B1. Portfolio Tradeoffs

**Interviewer (VP):** We can fund only one: observability platform rewrite, or payments modernization. How would you advise?

**Candidate:** Criteria: regulatory/risk exposure, revenue dependency, current incident cost, option value for other teams, time-to-risk-reduction. Payments usually wins if money integrity is fragile; observability wins if MTTR is existential and payments is stable. I'd ask for incident/cost data and propose a sequenced thin slice if both are critical.

**Follow-up:** CEO wants both yesterday.

**Excellent:** Present sequenced milestones; risk of parallelizing with same senior talent; recommend explicit kill/defer list.

---

### B2. Culture at Scale

**Interviewer:** What culture have you seen destroy engineering orgs?

**Candidate:** Fear-based blame after incidents; hero worship; promotion by visibility not impact; endless rewrites; ignoring compliance until crisis. Healthy cultures pair high standards with psychological safety and clear ownership.

---

### B3. Manager vs IC Leadership

**Interviewer:** Do you want to manage managers someday?

**Candidate:** Honest preference + openness. Emphasize leverage preference (org technical strategy vs people org). VPs respect self-knowledge.

---

## Conversation Set C — CTO

### C1. Technical Strategy

**Interviewer (CTO):** Where should we standardize vs allow team freedom?

**Candidate:** Standardize: identity, observability, CI security baselines, language LTS policy, secrets, networking patterns. Allow freedom: internal module design, datastores when justified by access patterns, experimentation behind clear exit criteria. Standardization without enabling platforms becomes bureaucracy.

---

### C2. Build vs Buy vs OSS

**Interviewer:** Build internal workflow engine or buy?

**Candidate:** Evaluate core differentiator vs commodity; total cost (build + operate + staff); vendor lock/compliance; time-to-value. For regulated industries, exit strategy and data residency matter. Default buy commodity; build differentiation.

---

### C3. Architecture Bet

**Interviewer:** Convince me we should (or should not) go multi-region active-active next year.

**Candidate:** Demand drivers (latency, DR regulation); data conflict model; eng maturity; cost. Many orgs need active-passive well-tested before active-active. I'd propose DR game days first; active-active only with clear customer requirement and conflict story.

---

### C4. AI / Productivity

**Interviewer:** How should we adopt AI coding tools safely?

**Candidate:** Enable with guardrails: secret scanning, license policy, human review for sensitive domains, measure cycle time/defect rates, training on review skills. Ban neither blindly nor allow unreviewed codegen into payments/auth.

---

## Mixed Rapid Prompts (Director / VP / CTO)

1. What is your personal operating cadence (week/month)?
2. Tell me about a strategy you got wrong.
3. How do you communicate risk to non-technical executives?
4. What's your view on outsourcing critical systems?
5. How do you evaluate engineering org health?
6. Describe a principled disagreement with leadership.
7. What technical investment has the best ROI you've seen?
8. How should platform teams be funded?
9. When is a monolith the right CTO-level call?
10. What questions do you have for me?

---

## Strong Questions To Ask Executives

1. What is the biggest technical risk that keeps you up at night?
2. How do you want this role to change the trajectory of the org?
3. What does excellent look like at the 12-month mark?
4. Where is the company under-invested in engineering leverage?
5. How do product and engineering resolve priority conflicts today?

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Altitude | Too low-level | Mixed | Executive-ready |
| Judgment | Binary | Nuanced | Criteria + sequencing |
| Courage | Pure agreeableness | Some pushback | Principled + respectful |
| Business link | Weak | Present | Fluent |
| Curiosity | None | Polite Qs | Strategic Qs |

---

## Confidence Checklist

- [ ] Bottom-line-first answers practiced
- [ ] One portfolio tradeoff story
- [ ] One culture / org health POV
- [ ] Informed question about *their* strategy
- [ ] Comfortable disagreeing without arrogance

---

## Notes

<!-- Capture exact exec questions; refine altitude of answers -->
