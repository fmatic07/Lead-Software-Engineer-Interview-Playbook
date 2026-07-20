# 02 — Behavioral Interview

> Pressure-test judgment, ownership, and collaboration through realistic enterprise scenarios — not slogans.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–60 minutes |
| Format | Behavioral deep-dives with follow-ups |
| Decision weight | High for Lead+; gate for culture risk |
| Methods | STAR / CAR / SOAR; progressive probing |

---

## Purpose

Prove you have led through conflict, ambiguity, failure, and delivery pressure — with outcomes and lessons, not vibes.

---

## How Interviewers Evaluate

| Axis | Looking for |
|------|-------------|
| Ownership | You absorbed outcomes, not blamed tools |
| Scope | Senior = feature/service; Lead = team/system |
| Learning | Changed behavior after failure |
| Collaboration | Influenced without authority when needed |
| Judgment | Tradeoffs under incomplete information |
| Communication | Structured, specific, interruptible |

---

## Common Mistakes

- Story with no conflict or decision.
- "We" forever — panel cannot find *your* contribution.
- Ending without result or metric.
- Blaming individuals by name.
- Same story recycled for every theme without reframing.

---

## Excellent Communication Techniques

1. Label the theme: "I'll use a conflict-with-stakeholder example."
2. STAR in ~2 minutes; pause for probes.
3. Quantify: latency, incidents, cycle time, headcount, revenue risk.
4. End with principle you reused later.

---

## Confidence Tips

- Maintain a story bank of 8–12 adaptable narratives (see Module 06).
- Map each story to 3 competencies so you can pivot.
- Practice follow-ups harder than openings.

---

## Ideal Answer Framework

**S**ituation (context + stakes) → **T**ask (your responsibility) → **A**ction (decisions you drove) → **R**esult (metric) → **L**esson (systemic change).

---

## Question Bank (75+)

### Leadership (1–12)
1. Tell me about a time you led without formal authority.
2. Describe setting technical direction for a team.
3. How did you align engineers who disagreed on approach?
4. Tell me about mentoring someone who was struggling.
5. Describe raising the quality bar on a team.
6. When did you delegate a critical task — how did you stay accountable?
7. Tell me about leading through an organizational change.
8. Describe a time you had to say no to a senior stakeholder.
9. How have you built trust with a new team quickly?
10. Tell me about sponsoring someone's growth or promotion case.
11. Describe leading a cross-team initiative.
12. When did leadership mean slowing down delivery for safety?

### Conflict (13–22)
13. Conflict with a peer on design — resolution?
14. Conflict with your manager?
15. Handling a toxic or dismissive collaborator?
16. Disagreement between product and engineering — your role?
17. Two seniors arguing in a design review — what did you do?
18. Someone took credit for your work?
19. Pushback on your code review feedback?
20. Team blaming another team for an outage?
21. Cultural or communication style clash?
22. Escalation you initiated — was it worth it?

### Ownership (23–32)
23. Project failing — how did you turn it around?
24. Production bug that was your change?
25. Inherited a messy system — first 30 days?
26. Commitment you made that became unrealistic?
27. Owning a decision everyone disliked?
28. Gap you closed that wasn't on your OKRs?
29. Staying accountable when dependencies slipped?
30. Taking responsibility in a blameless postmortem?
31. Driving a migration others avoided?
32. Keeping a promise to a customer/partner under pressure?

### Mentoring (33–40)
33. Mentored a junior through a hard problem?
34. Coached a mid-level toward senior behaviors?
35. Gave difficult feedback?
36. Mentorship that didn't work — what changed?
37. Built pairing or review practices?
38. Helped someone prepare for promotion?
39. Onboarded engineers effectively?
40. Balanced mentoring time vs delivery?

### Stakeholders (41–50)
41. Managed conflicting stakeholder priorities?
42. Explained a technical risk to non-engineers?
43. Negotiated scope cut mid-project?
44. Partnered with Security / Compliance / Risk?
45. Worked with struggling vendor or partner team?
46. Executive asked for an unrealistic date?
47. Translated architecture options into business choices?
48. Recovered trust after a missed commitment?
49. Influenced roadmap using data?
50. Said "I don't know" to a stakeholder — then what?

### Deadlines & Delivery (51–58)
51. Immovable deadline with incomplete requirements?
52. Cut scope without cutting quality standards?
53. Parallel workstreams you coordinated?
54. Release that had to be stopped?
55. On-call week that collided with a launch?
56. Technical spike that ate the schedule?
57. Delivered under staffing shortage?
58. Prioritized ruthlessly when everything was P0?

### Failures (59–66)
59. Biggest professional failure?
60. Misjudged a technical risk?
61. Communication failure that hurt the team?
62. Hiring or interview miss?
63. Over-engineered a solution?
64. Under-estimated operational cost?
65. Trusted an assumption that was wrong?
66. Failed to escalate early enough?

### Innovation (67–72)
67. Introduced a new technology or practice?
68. Improved developer experience measurably?
69. Automated a painful manual process?
70. Challenged "we've always done it this way"?
71. Prototype that changed roadmap direction?
72. Innovation that failed — kill criteria?

### Communication (73–80)
73. Presented to leadership under scrutiny?
74. Wrote an ADR that changed a decision?
75. Facilitated a heated design review?
76. Communicated breaking changes to consumers?
77. Handled ambiguous requirements with constant clarifying?
78. Gave a status update that delivered bad news clearly?
79. Bridged frontend/backend/product language gaps?
80. Documented tribal knowledge into a durable playbook?

---

## Full Interview Scripts

### Script A — Leadership Without Authority

**Interviewer:** Tell me about a time you led without formal authority.

**Candidate:** Situation: Three squads depended on a shared Spring Boot "customer profile" service with rising latency. I wasn't the service owner on paper. Task: Align a fix path before Black Friday traffic. Action: I convened a 45-minute working session with owners, brought p99 dashboards and top SQL, proposed a read-path cache + query fix with a rollback plan, and volunteered to land the first PR and runbook. Result: p99 dropped ~40% in two weeks; we avoided a capacity incident. Lesson: Leadership was evidence + a reversible first step, not a title.

**Follow-up:** What if the owning team refused?

**Expected Senior:** Escalate with risk memo; offer to pair; timebox alternatives.  
**Expected Lead:** Frame business risk to EM/PM; negotiate capacity swap; protect customer outcomes over org chart pride.

**Evaluation Notes:** Look for influence tactics and metrics, not hero narrative.

---

### Script B — Conflict With Peer

**Interviewer:** Describe a design conflict with a peer.

**Candidate:** Peer wanted synchronous microservice chaining for a loan decision; I advocated async with clear SLAs due to timeout amplification. We timeboxed a spike: both drew failure modes. Data showed fan-out p99 breached. We agreed on async + outbox, documented ADR. Relationship stayed intact because we argued criteria, not ego.

**Follow-up:** How do you keep conflict from becoming personal?

**Excellent Lead Answer:** Separate people from positions; write options with costs; decide with an accountable owner by a date.

---

### Script C — Production Failure Ownership

**Interviewer:** Tell me about a production failure you caused.

**Candidate:** I shipped a Hibernate fetch change that looked fine in staging (small datasets) and caused N+1 memory pressure in prod. I led rollback, customer comms with support, then added dataset-scale integration tests and a query budget check in CI. Personal lesson: staging fidelity is part of design.

**Follow-up:** How did you handle blame in the postmortem?

**Strong:** Focus on systemic controls; invite review of my change checklist; no defensiveness.

---

### Script D — Unrealistic Executive Deadline

**Interviewer:** An executive wants it in two weeks. You know it needs six. What do you do?

**Candidate (Lead):** I restate the goal (outcome, not date), present a two-week slice that reduces risk (feature flag, manual ops path, or thin vertical), and a six-week plan for the durable design. I make risks explicit: data integrity, audit, on-call load. I ask which risk they accept if we compress. I document the decision.

**Common mistakes:** Flat "no"; silent heroics; agreeing then missing quietly.

---

### Script E — Mentoring Struggle

**Interviewer:** Mentored someone who was struggling?

**Candidate:** Mid-level stuck on Spring Security for days without escalating. I paired with hypothesis-driven debugging, set a 4-hour escalate norm, and followed with a short SecurityFilterChain clinic for the team. They later owned a JWT migration slice solo.

**Evaluation rubric:** Coaching vs taking keyboard forever; team norm change.

---

### Script F — Innovation Kill

**Interviewer:** Tell me about an innovation that didn't work.

**Candidate:** Proposed rewriting a stable batch to Kafka streams for elegance. Spike showed operational complexity and team skill gap outweighed gains. I killed it, kept incremental JDBC batch improvements, wrote kill criteria into our RFC template.

**Senior vs Lead:** Senior = good technical judgment. Lead = protects team from novelty theater.

---

## Mapping Stories → Questions

| Story theme | Works for questions |
|-------------|---------------------|
| Outage ownership | 24, 30, 55, 59, 64 |
| Design conflict | 3, 13, 17, 47, 75 |
| Scope negotiation | 8, 43, 46, 51, 52 |
| Mentoring | 4, 33–40 |
| Migration/legacy | 25, 31, 67, 70 |
| Cross-team platform | 11, 41, 49, 76 |

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Specificity | Abstract | Some detail | Names systems, numbers, decisions |
| Ownership | Passive "we" | Partial | Clear personal agency |
| Judgment | Binary thinking | Some tradeoffs | Explicit criteria and risk |
| Learning | None | Mild | Durable process change |
| Seniority signal | IC task | Feature lead | Team/system impact |

---

## Confidence Checklist

- [ ] 8+ stories covering leadership, conflict, failure, mentoring, stakeholders
- [ ] Each story has metric or concrete outcome
- [ ] Can answer "What would you do differently?" without collapsing
- [ ] Can reframe one story for three competencies
- [ ] Practice interrupted mid-STAR and resume cleanly

---

## Notes

<!-- Paste live behavioral questions; tag which story you used; score 1–5 -->
