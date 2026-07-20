# Building Credibility

> Senior credibility is earned in reviews, incidents, and ownership — not in title announcements.

---

## Purpose

Describe how senior engineers earn trust in new and existing organizations: communication, code reviews, architecture discussions, incident response, mentoring, ownership, and continuous improvement.

---

## Key Takeaways

- Trust compounds from kept promises and calm under failure.
- Code review is a leadership surface — teach without humiliating.
- Architecture influence requires written reasoning and respect for constraints.
- Incident behavior is remembered longer than feature work.
- Ownership means outcomes, not heroics.

---

## Topics Covered

- [ ] How senior engineers earn trust
- [ ] Communication
- [ ] Code reviews
- [ ] Architecture discussions
- [ ] Incident response
- [ ] Mentoring
- [ ] Ownership
- [ ] Continuous improvement

---

## How Senior Engineers Earn Trust

### Decision framework — Trust Equation (practical)

`Trust ≈ (Credibility + Reliability + Empathy) / Self-Orientation`

- **Credibility:** technical judgment proven in context  
- **Reliability:** do what you said  
- **Empathy:** understand team constraints  
- **Self-orientation:** if everything is about your brilliance, trust collapses  

### Real-world examples

- New Lead at a bank spends three weeks learning settlement edge cases before proposing batch changes — gains allies in ops.
- “Rockstar” hire rewrites auth in fortnight; breaks audit trail; trust evaporates regardless of code elegance.

---

## Communication

### Patterns that read as senior

| Pattern | Example |
|---------|---------|
| Context → options → recommendation | “Three options; I recommend B because…” |
| Risk-first updates | “Yellow: migration lag risk; need DBA window” |
| Written async for distributed teams | Short RFC before big meeting |
| Precise asks | “Need decision on idempotency key format by Thu” |

### Professional examples

**Status:**

> Blocked on PCI questionnaire from vendor — impact: cannot enable capture API in staging. Next action: escalate via procurement today; workaround: mock in QA only.

**Disagreement:**

> I see the appeal of shared DB tables for speed. The compliance boundary argues for separate schemas. Can we spike both costs for one day and decide with numbers?

---

## Code Reviews

### Senior review priorities (ordered)

1. Correctness & domain invariants  
2. Security & data handling  
3. Failure modes / retries / idempotency  
4. Observability  
5. Performance at relevant scale  
6. Readability & style  

### Communication in reviews

- Prefer questions that teach: “What happens if the webhook is duplicated?”
- Separate blocking vs nit: label nits clearly.
- Praise specifically when someone handles an edge case well.

### Common mistakes

- Style-only reviews while missing transaction bugs.
- Public shaming.
- Approving what you don’t understand to be “nice.”

---

## Architecture Discussions

### How to contribute early without ownership yet

- Restate constraints before solutions.
- Ask about operability and migration.
- Offer to write the ADR draft.

### Decision framework — speak vs wait

| Speak | Wait |
|-------|------|
| Safety/security risk | Pure preference without local context |
| You have production evidence | First week, incomplete map |
| Clarifying question | Status-seeking disagreement |

---

## Incident Response

### Credibility behaviors

- Join bridge calmly; take notes.
- Prefer mitigate → stabilize → root cause.
- No blame during the incident.
- Own follow-up actions to completion.
- Write or improve the postmortem with systems focus.

### Example

During a fintech payment timeout storm: senior disables noncritical notifications, increases consumer concurrency carefully, verifies ledger invariants, then leads a postmortem on client timeout vs server retry amplification.

---

## Mentoring

- Pair on real tasks; don’t only lecture.
- Create growth challenges with safety nets.
- Give feedback private, credit public.
- Mentoring is not doing their work invisibly — that blocks both of you.

---

## Ownership

### Definition for Lead/Architect tracks

| Own | Don’t confuse with |
|-----|--------------------|
| Outcomes & risk calls | Doing every task |
| Quality bar for the domain | Blocking all merges egoistically |
| Cross-team coordination | Political theater |
| Making work reviewable | Knowledge hoarding |

### Checklist — healthy ownership

- [ ] Metrics exist for the area you own
- [ ] On-call knows how to reach escalation path
- [ ] Docs match reality within a sprint of change
- [ ] Bus factor > 1 for critical paths

---

## Continuous Improvement

Small systems upgrades beat manifesto rewrites:

- Template ADR
- Better alert thresholds
- Test data factories
- Deploy checklist
- Error budget conversation with product

---

## Common Mistakes

- Equating seniority with winning arguments.
- Silence in incidents to avoid association with failure.
- Mentoring only people who already think like you.
- Continuous “improvement” that is churn.

---

## Templates

### Personal credibility log (weekly)

```text
Promises kept:
Reviews that taught something:
Incidents / risks surfaced:
Docs improved:
Mentoring moments:
Trust debt created/repaid:
```

### Review comment stems

```text
Blocking – correctness:
Blocking – security/ops:
Question – failure mode:
Suggestion – non-blocking:
Appreciate – specific:
```

---

## Checklists

### First 60 days credibility

- [ ] Zero surprising production changes
- [ ] Reviews valued by peers (ask once)
- [ ] One incident or risk handled calmly
- [ ] One mentee interaction
- [ ] One improvement merged

---

## Reflection Questions

1. Where is your self-orientation leaking into technical debates?
2. What would on-call peers say about your incident presence?
3. Which ownership area still depends too much on you alone?

---

## Action Items

- [ ] Start a weekly credibility log for one month
- [ ] Adopt ordered review priorities explicitly
- [ ] Volunteer for one postmortem action and close it
- [ ] Ask a peer for one piece of candid feedback on your reviews

---

## Progress Checklist

- [ ] Can explain how trust is earned in engineering terms
- [ ] Reviews focus on failure modes
- [ ] Has a clear ownership definition for current role
- [ ] Continuous improvement is incremental and measured

---

## Notes

<!-- Credibility log, feedback, ownership metrics -->
