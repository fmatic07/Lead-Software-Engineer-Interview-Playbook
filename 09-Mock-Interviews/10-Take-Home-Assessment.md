# 10 — Take-Home Assessment

> How companies score take-homes — and how strong Senior/Lead candidates package delivery.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 3–8 hours expected (sometimes take-home + review) |
| Formats | Small service, bugfix + extend, design doc + prototype |
| Follow-on | Often a walkthrough with interviewers |

---

## Purpose

Take-homes evaluate how you ship when nobody is watching: judgment, clarity, testing, and communication artifacts.

---

## How Companies Evaluate

| Dimension | What they read in your repo |
|-----------|-----------------------------|
| Problem understanding | README restates goals/assumptions |
| Architecture | Sensible modules; not over-engineered |
| Code quality | Naming, boundaries, error handling |
| Correctness | Tests that fail for real bugs |
| Operability | How to run; config; logs |
| Engineering taste | Tradeoffs documented |
| Git craft | Atomic commits with why |
| Seniority | What you chose *not* to build |

---

## Code Quality

**Strong:** Consistent style; small focused classes; explicit errors; no dead code; Java records/DTOs where apt; constructor injection if Spring.

**Weak:** Copy-paste; god classes; swallowed exceptions; commented-out experiments left behind; framework fireworks for a CRUD toy.

---

## Architecture

- Draw a simple diagram in README.
- Separate domain from delivery adapters if non-trivial.
- State consistency and idempotency if payments/orders involved.
- Avoid Kafka + K8s + service mesh for a 4-hour assignment unless required.

**Lead signal:** Extension points noted ("If this grew, I'd extract X because…").

---

## Documentation

README must answer:

1. What it does / doesn't do  
2. Assumptions  
3. How to run (Java version, commands)  
4. How to test  
5. Design decisions & tradeoffs  
6. Known limitations & next steps  
7. Time spent (if asked)

Optional: ADR folder for 1–2 key decisions.

---

## Git Commits

| Weak | Strong |
|------|--------|
| `WIP` `stuff` `fix` | `Add idempotency key unique constraint` |
| One giant commit | Incremental: model → API → tests → docs |
| Secrets committed | `.env.example` only |

Commit messages explain **why**. Reviewers often read `git log` before code.

---

## Testing

- Unit tests for core logic.
- Integration/API tests for happy + failure paths.
- Edge cases listed in README if not automated.
- Deterministic tests (fixed clocks, no sleep flakiness).

**Senior:** Solid coverage of invariants.  
**Lead:** Also risk-based tests (idempotency, auth failure, validation).

---

## Presentation / Walkthrough

Expect:

1. 3-minute overview of goals and architecture  
2. Demo happy path  
3. Show a test proving a tricky case  
4. Discuss what you'd do with 2 more days  
5. Honest limitations  

Do not apologize endlessly — own tradeoffs.

---

## Ideal Take-Home Workflow

1. Restate requirements; email clarifying questions early if allowed.  
2. Timebox architecture (20–40 min).  
3. Vertical slice first (running skeleton).  
4. Harden core invariants.  
5. Tests + README last 20–30% of time (non-negotiable).  
6. Re-clone fresh and follow your own README before submit.

---

## Common Mistakes

- Building a platform instead of the asked feature.
- No README.
- Cannot run without your laptop's tribal setup.
- Ignoring stated non-functionals (auth, pagination).
- Submitting with failing tests.
- Overusing AI-generated boilerplate you cannot explain.

---

## Excellent Communication Techniques (Walkthrough)

- "I optimized for X because the prompt emphasized Y."
- "I skipped Z deliberately; here's the risk."
- "This test locks the idempotency behavior."

---

## Confidence Tips

- Treat take-home as a professional sample, not a hackathon.
- If time-capped by company, respect the cap and document scope cuts.

---

## Evaluation Rubric (Candidate Self-Score)

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Runability | Broken | Works with tribal knowledge | Clean README path |
| Architecture | Mess | Adequate | Clear boundaries |
| Tests | None/flaky | Basic | Risk-based |
| Docs | Absent | Minimal | Decision-quality |
| Git | Noise | OK | Reviewable story |
| Judgment | Overbuilt | Balanced | Explicitly scoped |

---

## Confidence Checklist

- [ ] Fresh clone run succeeds
- [ ] Tests green in CI or local script
- [ ] Tradeoffs written
- [ ] Can defend every dependency
- [ ] Walkthrough rehearsed in 10 minutes

---

## Notes

<!-- Store anonymized feedback from real take-homes -->
