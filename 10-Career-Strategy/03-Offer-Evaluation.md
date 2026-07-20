# Offer Evaluation

> Accept the role that compounds career capital under constraints you can live with — not the shiniest TC screenshot.

---

## Purpose

Provide a structured way to evaluate offers across compensation, engineering culture, manager quality, architecture maturity, growth, and risk. Includes practical scorecards for Senior → Architect decisions.

---

## Key Takeaways

- TC is necessary but not sufficient; manager and problem quality dominate outcomes.
- Architecture maturity and technical debt predict your weekly pain.
- Startup risk and enterprise politics are different failure modes — price both.
- Hybrid/onsite expectations and on-call load are compensation in disguise.
- Use the same scorecard weights for every offer or comparisons lie.

---

## Topics Covered

- [ ] Evaluating total compensation
- [ ] Engineering culture
- [ ] Manager quality
- [ ] Growth opportunities
- [ ] Architecture maturity & technical debt
- [ ] Team structure
- [ ] Work-life balance & hybrid expectations
- [ ] Promotion opportunities
- [ ] Company stability & startup risk
- [ ] Career trajectory
- [ ] Practical scorecards

---

## Evaluating Total Compensation

Convert everything to **conservative annual TC**:

`Base + (bonus target × historical payout factor) + equity annualized (haircut) + allowances − material benefit gaps`

### Equity haircuts (practical)

| Context | Haircut mindset |
|---------|-----------------|
| Public / liquid RSU | Low haircut; still tax-aware |
| Late-stage private | Medium; liquidity uncertain |
| Early startup options | Heavy; treat as upside, not salary |

---

## Engineering Culture

### Signals to probe

- Code review norms (blocking vs rubber-stamp)
- On-call load and incident retrospectives
- ADR / RFC usage
- Test and CI expectations
- How product and eng negotiate scope

### Real-world examples

- **Banking/fintech:** Strong change management; slower merges; high compliance — excellent capital if you want regulated systems experience.
- **Product scale-up:** Faster ship culture; watch for heroics replacing quality systems.
- **GovTech:** High accountability, accessibility/security bar; procurement timelines affect delivery feel.

---

## Manager Quality

Re-use diligence from Career Planning. An excellent company with a weak manager is often worse than a good manager in a mediocre brand.

**Deal-breaker:** manager cannot describe 90-day success or blames the team for all failures.

---

## Growth Opportunities

Ask:

- What does the next level require here?
- Who was promoted in the last year on this team?
- Will you see customer/regulatory/P&L impact or only tickets?

---

## Architecture Maturity & Technical Debt

### Decision framework

| Question | Healthy answer | Risk answer |
|----------|----------------|-------------|
| Why is the system shaped this way? | Constraints + tradeoffs | “Legacy, don’t touch” |
| How is debt tracked? | Explicit backlog + budget | Only hero rewrites |
| Migration style? | Strangler + metrics | Big-bang fantasy |
| Ownership? | Clear service owners | Everyone/no one |

Technical debt is not automatically bad — **unmanaged** debt is.

---

## Team Structure

Map: squad size, mobile/web/platform split, QA/SRE embedding, architect role (ivory tower vs embedded), dependency on vendor teams.

Prefer clear ownership over matrix chaos unless you thrive in coordination-heavy roles.

---

## Work-Life Balance & Hybrid Expectations

Quantify:

- On-call rotation frequency and incident rate
- Meeting load
- Core hours across timezones
- Expected office days (real vs written)
- Release night / month-end batch culture (common in fintech/telco)

---

## Promotion Opportunities

Prefer evidence over promises: leveling rubric, recent promo stories, whether Lead/Staff exists as IC track.

---

## Company Stability & Startup Risk

| Enterprise / public | Startup |
|---------------------|---------|
| Reorg and politics risk | Runway, pivot, acquihire risk |
| Slower equity upside | Binary equity outcomes |
| Clearer process | Role morphs overnight |

### Startup risk checklist

- [ ] Runway months (ask)
- [ ] Burn vs revenue trajectory
- [ ] Who owns eng quality at leadership level
- [ ] Whether your role survives a 20% headcount cut

---

## Career Trajectory

Project 24 months:

- Skills gained
- Scope gained
- Network gained
- Brand signal
- Health cost

Pick the offer with the best trajectory under your constraints — not the highest week-1 dopamine.

---

## Practical Offer Evaluation Scorecards

### Scorecard A — Primary (use for all offers)

Score 1–5. Keep weights fixed across offers.

| Criterion | Weight | Offer 1 | Offer 2 | Offer 3 |
|-----------|--------|---------|---------|---------|
| Manager quality | 20% | | | |
| Problem difficulty / ownership | 15% | | | |
| Engineering culture | 10% | | | |
| Architecture maturity | 10% | | | |
| Growth / promotion path | 10% | | | |
| Comp (conservative TC vs target) | 15% | | | |
| WLB / on-call / hybrid fit | 10% | | | |
| Stability / risk fit | 10% | | | |
| **Weighted total** | 100% | | | |

**Interpretation:** Prefer highest weighted total if ≥3.5 and no deal-breaker ≤2 on Manager or Ownership.

---

### Scorecard B — Total Compensation Detail

| Component | Offer 1 | Offer 2 | Notes |
|-----------|---------|---------|-------|
| Base | | | |
| Bonus expected | | | hist. payout |
| Equity annualized (haircut) | | | |
| Signing (year-1 only) | | | |
| Allowances | | | |
| Benefits delta | | | |
| **Year-1 cash-ish** | | | |
| **Steady-state Y2+** | | | |

---

### Scorecard C — Engineering Reality Probe

| Probe | Evidence from interviews | Score 1–5 |
|-------|--------------------------|-----------|
| Code review quality | | |
| Incident / on-call maturity | | |
| Testing & CI discipline | | |
| Debt intentionality | | |
| Cross-team API ownership | | |
| Docs / ADR habit | | |
| **Average** | | |

---

### Scorecard D — Manager Diligence

| Question | Answer summary | Confidence (H/M/L) |
|----------|----------------|--------------------|
| 90-day success | | |
| Owned decisions | | |
| Disagreement handling | | |
| Promo evidence | | |
| Current fires | | |
| **Hire this manager?** | Y/N | |

---

## Real-World Comparison Sketch

**Offer 1 — Fintech Lead (hybrid):** Higher base, heavy month-end ops, strong regulated-domain capital, manager excellent.

**Offer 2 — Product startup remote:** Lower base, options lottery, greenfield ownership, manager untested, runway 14 months.

**Offer 3 — GovTech:** Band-limited cash, high mission, strong security engineering, slower delivery cadence.

A Senior optimizing for Architect trajectory might pick Offer 1; someone seeking 0→1 breadth might pick Offer 2 with a cash floor intact; mission-driven security-focused leads might pick Offer 3.

---

## Common Mistakes

- Choosing purely on brand logo.
- Ignoring on-call as unpaid labor.
- Believing verbal promises not in the offer.
- Underestimating commute / office politics cost.
- Failing to speak with future teammates.

---

## Professional Communication Examples

**Asking for peer time:**

> Before deciding, could you connect me with an engineer on the team for a short culture chat? I want to understand on-call and how architecture decisions get made.

**Clarifying hybrid:**

> The offer says hybrid — can we specify expected office days and whether exceptions for deep-work weeks are normal?

---

## Templates

### Offer One-Pager Decision

```text
Company / role / level:
Weighted scorecard total:
Deal-breakers present?
Year-1 vs steady-state TC:
Career capital gain (3 bullets):
Primary risk:
Decision: accept / decline / counter
Reason (2–3 sentences):
```

---

## Checklists

### Before accepting

- [ ] Scorecards A–D completed
- [ ] Spoken to ≥1 future peer
- [ ] On-call and hybrid terms explicit
- [ ] Comp components in writing
- [ ] 24-month trajectory articulated
- [ ] Sleep-on-it rule honored (≥1 night)

---

## Reflection Questions

1. Which offer makes you better at hard problems in two years?
2. What are you pretending not to see (debt, manager, risk)?
3. If both companies disappeared in 18 months, which experience travels better?

---

## Action Items

- [ ] Copy Scorecard A into Notes with your personal weights
- [ ] Monetize benefits and on-call for your top offers
- [ ] Schedule peer conversations before deciding
- [ ] Write a one-pager decision for the leading offer

---

## Progress Checklist

- [ ] Can compute conservative TC
- [ ] Can run architecture maturity probes in HM interviews
- [ ] Have a filled scorecard comparing ≥2 scenarios (real or practice)
- [ ] Defined personal deal-breakers

---

## Notes

<!-- Scorecards, peer conversation notes, decision one-pagers -->
