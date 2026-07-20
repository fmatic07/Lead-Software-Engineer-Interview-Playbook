# 07 — Leadership Interview

> Executive-level leadership discussions for Lead / Tech Lead / Architect tracks.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–60 minutes |
| Focus | Judgment under org constraints |
| Interviewers | Eng managers, Directors, Staff+ |

---

## Purpose

Demonstrate you multiply team outcomes: debt strategy, hiring signal, mentoring systems, incident leadership, and culture — not heroics alone.

---

## How Interviewers Evaluate

| Signal | Strong |
|--------|--------|
| Technical debt | Portfolio thinking, risk-based sequencing |
| People | Feedback, growth, fair standards |
| Ownership | Absorbs outcomes; escalates early |
| Incidents | Calm command; learning systems |
| Culture | Psychological safety + high bar |

---

## Common Mistakes

- Leadership = "I worked longer hours."
- Debt discussions without user/risk impact.
- Mentoring stories that are rescue-only.
- Blameless language with blame energy.
- No examples of disagreeing and committing.

---

## Excellent Communication Techniques

Use situation → options → decision criteria → people impact → result → system change.

---

## Confidence Tips

Prepare metrics: MTTR, change fail rate, cycle time, promotion outcomes, incident counts.

---

## Ideal Answer Framework

**Stakeholder map → Risk → Decision → Communication → Follow-through → Measurement**

---

## Topic Scripts

### Technical Debt

**Interviewer:** How do you decide what debt to pay this quarter?

**Candidate:** Inventory debt by risk (security, data loss, page-level outages), cost to change, and strategic enablement. Allocate a fixed capacity (e.g., 20%) plus emergency buffer. Pair each debt item with a user/risk narrative for product. Kill low-risk aesthetic refactors when delivery is threatened — unless they block hiring/onboarding severely.

**Follow-up:** Product wants zero debt work.

**Lead:** Translate to probability × impact; offer smaller slices; escalate with written risk acceptance if overridden.

---

### Hiring

**Interviewer:** How do you interview for a Senior Java engineer?

**Candidate:** Scorecard: Java/Spring production depth, debugging, design clarity, collaboration, ownership. Structured questions; shared rubric; avoid vibe hiring. Debrief with evidence quotes. Watch for false negatives on nervousness vs true gaps.

**Follow-up:** Disagreement in debrief?

**Excellent:** Revisit scorecard evidence; no "I'll coach them later" for core gaps; hire slow for culture-risk.

---

### Mentoring

**Interviewer:** How do you raise a mid-level to senior?

**Candidate:** Explicit senior behaviors (ownership scope, design writing, incident leadership, mentoring juniors). Pair on design docs; shadow customer conversations; rotate on-call with coaching; feedback monthly against scorecard — not vague "be more proactive."

---

### Ownership

**Interviewer:** A dependency team will miss your launch date. What do you do?

**Candidate:** Detect early via integration milestones; re-scope MVP; parallel stub/contract tests; escalate with options (not surprises); protect customer promise with transparent status. Ownership is outcome management, not blame.

---

### Performance Reviews

**Interviewer:** Engineer is solid coder but blocks the team in reviews with nitpicks.

**Candidate:** Private feedback with examples; redefine review standards (correctness/security/readability vs style prefs); introduce style automation; set expectations for turnaround SLA. If unchanged, formal PIP path with manager — fairness and documentation.

---

### Incident Response

**Interviewer:** Sev-1 at 2am. You are incident commander. Walk through.

**Candidate:** Declare IC; mitigate first (rollback/feature flag); single comms channel; timeboxed hypotheses; customer status cadence; handoff; blameless postmortem with owners/dates; follow-through review in 2 weeks.

**Follow-up:** Engineer who caused it is panicking.

**Lead:** Separate person from process; assign clear task; protect from pile-on; coach later.

---

### Engineering Culture

**Interviewer:** How have you shaped engineering culture?

**Candidate:** Concrete mechanisms: ADR habit, on-call runbooks, "escalate in 4 hours" norm, celebration of tests/catching bugs, intolerance for hero culture that hides risk. Culture is what you reward and refuse — repeatedly.

---

## Additional Executive Prompts

1. How do you handle two seniors in a deadlocked design fight?
2. When do you take the keyboard vs coach?
3. How do you say no to a VP?
4. Build vs buy decision you owned?
5. How do you measure mentoring ROI?
6. Diversity/inclusion in hiring loop — your practices?
7. Remote team alignment rituals that worked?
8. How do you prevent burnout on a team during a death march ask?
9. Strategy when leadership wants microservices and the team is not ready?
10. How do you create accountability without fear?

---

## Full Script — Debt vs Feature Pressure

**Interviewer:** CEO demo in 3 weeks. You know authz checks are incomplete on admin APIs. Product says ship UI polish.

**Candidate (Lead):** I frame incomplete authz as launch-blocking risk with exploit scenario. Propose: minimal authz hardening + feature flag polish behind flag; defer non-critical UI. If overridden, write risk acceptance with EM/PM signatures and monitoring. I do not silently ship known broken authz.

**Evaluation Notes:** Integrity + stakeholder management under pressure.

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Judgment | Reactive | Sound | Risk-based, principled |
| People leadership | Avoidant | Supportive | Grows systems + humans |
| Communication | Fuzzy | Clear | Executive-ready brevity |
| Ownership | Excuses | Accountable | Outcome + escalation craft |
| Culture impact | None | Local | Durable norms |

---

## Confidence Checklist

- [ ] Debt prioritization story with outcome
- [ ] Hiring scorecard you can recite
- [ ] Sev-1 IC narrative
- [ ] Difficult feedback example
- [ ] Example of principled pushback

---

## Notes

<!-- Align stories with Module 05 Leadership scenarios -->
