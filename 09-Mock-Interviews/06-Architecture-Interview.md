# 06 — Architecture Interview

> Architecture review discussions — not box drawing contests. Defend boundaries, evolution, and operational cost.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–60 minutes |
| Format | Whiteboard critique + "how would you evolve X?" |
| Levels | Lead / Architect weighted; Senior for modular monolith judgment |

---

## Purpose

Show you choose architecture as a response to constraints (team size, consistency, compliance, change rate) — not fashion.

---

## How Interviewers Evaluate

- Constraint identification before pattern selection
- Clear domain boundaries and ownership
- Evolution path from current → target
- Operability (deploy, observe, fail)
- Honesty about complexity tax

---

## Common Mistakes

- Microservices as default answer.
- DDD vocabulary without bounded contexts.
- CQRS/Event sourcing because "scalable."
- Ignoring team cognitive load.
- No migration plan from legacy.

---

## Excellent Communication Techniques

State drivers: "Change rate here is high; compliance boundary there; team is 8 engineers…" Then pick style. Always name what you are optimizing and sacrificing.

---

## Confidence Tips

Bring one legacy modernization story with incremental steps.

---

## Ideal Answer Framework

**Current state → Pain → Constraints → Options → Decision criteria → Target → Migration → Risks → Metrics**

---

## Discussion 1 — Modular Monolith vs Microservices

**Interviewer:** Our Spring Boot monolith is painful to deploy. Should we split into microservices?

**Candidate:** First diagnose pain: build time, blast radius, ownership conflicts, scaling asymmetry, or just poor modularity? Often a modular monolith with package/module boundaries, separate deployables later, is cheaper. Split when: independent scale, independent release cadence, hard compliance boundary, or multi-team ownership with clear contracts.

**Follow-up:** What would you split first?

**Senior:** Extract a leaf domain with stable API and low chatty coupling (e.g., notifications).  
**Lead:** Define bounded contexts, platform prerequisites (CI, observability, gateway, auth), then extract strangler increments.

**Tradeoffs:** Distributed failure, data ownership, latency vs independent deploy.

**Common mistakes:** Split by technical layers (Controller service, etc.).

---

## Discussion 2 — Domain-Driven Design in Practice

**Interviewer:** How would you apply DDD to a lending platform?

**Candidate:** Identify bounded contexts: Origination, Underwriting, Servicing, Collections, Identity. Ubiquitous language per context; anti-corruption layers toward core banking. Aggregates with invariants (LoanApplication). Prefer modular monolith first if one team; separate services when contexts and teams align.

**Follow-up:** Shared database across contexts?

**Excellent Lead:** Avoid; at minimum separate schemas; eventual integration via events/ACL.

**Evaluation:** Reject "entities and repositories = DDD."

---

## Discussion 3 — Hexagonal / Ports & Adapters

**Interviewer:** Show hexagonal architecture for a payments service.

**Candidate:** Domain core (payment aggregate, policies) → ports (PaymentRepository, FraudChecker, Clock) → adapters (Postgres, HTTP fraud vendor, Kafka). Application services orchestrate use cases. Delivery adapters: REST, message consumers.

**Follow-up:** Isn't this overkill?

**Answer:** Value rises with multiple adapters and long life; skip ceremony for a CRUD report with one DB.

---

## Discussion 4 — CQRS

**Interviewer:** When do you introduce CQRS?

**Candidate:** When read models diverge hard from write invariants (dashboards, search, personalized lists) and write complexity is polluted by read needs. Not for every CRUD. Start with separate read queries/DTO projections; graduate to separate stores when justified.

**Follow-up:** Consistency?

**Lead:** Define lag SLO; show version vectors or "read your writes" tricks for actor-specific paths.

---

## Discussion 5 — Event-Driven Architecture

**Interviewer:** Design async integration between Order and Inventory.

**Candidate:** OrderPlaced via outbox → broker → Inventory reserves; InventoryReserved/Rejected → Order completes/cancels. Idempotent consumers; DLQ; schema evolution (Avro/JSON schema). Avoid choreography spaghetti without tracing and ownership.

**Follow-up:** Saga vs orchestration?

**Senior:** Choreography for simple; orchestrator when many steps/compensations.  
**Architect:** Platform standards for correlation IDs, contracts, poison handling.

---

## Discussion 6 — Legacy Modernization

**Interviewer:** 10-year J2EE monolith, Oracle, nightly batch. Modernize?

**Candidate:** Strangler fig: identify seams; introduce API facade; dual-write or CDC carefully; carve read path first if safer; replace batch with incremental where ROI exists; freeze big-bang rewrite. Measure: deploy frequency, defect rate, lead time.

**Follow-up:** Leadership asks for 6-month full rewrite.

**Excellent:** Present risk-adjusted roadmap; thin vertical slices; parallel run; kill criteria.

---

## Discussion 7 — API Gateway & BFF

**Interviewer:** Mobile and web need different payloads. Architecture?

**Candidate:** Experience-specific BFFs aggregate backend services; gateway for cross-cutting auth/rate limit/TLS. Avoid god gateway with all business logic.

---

## Discussion 8 — Multi-Tenancy Architecture

**Interviewer:** SaaS: shared DB vs DB-per-tenant?

**Candidate:** Pool (shared) for SMB scale/cost; silo for enterprise/compliance; hybrid by tier. Isolation in every layer (authz, cache keys, storage prefixes). Noisy neighbor controls.

---

## Discussion 9 — Data Ownership & Integration

**Interviewer:** Two services need to update the same customer email.

**Candidate:** Single writer ownership; others consume events or query owner. Dual writers create inconsistency. If org forces shared DB table, name it transitional debt with exit plan.

---

## Discussion 10 — Architecture Review Board Simulation

**Interviewer:** Review this proposal: 15 microservices for a 6-person team shipping a MVP lending app.

**Candidate:** Challenge team:cognitive load; propose 1–3 deployables with modules; define extraction triggers (scale, compliance, team growth). Require: SLOs, data ownership map, on-call plan, cost estimate.

**Evaluation Notes:** Architect-level candidates protect the org from premature distribution.

---

## Rapid Prompts

1. Sync vs async for KYC vendor calls?
2. Where do you put authorization policy decision points?
3. How do you version events for 3 years of consumers?
4. Monorepo vs polyrepo for Java services?
5. When is a shared kernel acceptable?
6. How do you prevent distributed monolith (chatty sync mesh)?

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Constraint-driven | Pattern-first | Some constraints | Criteria explicit |
| Boundaries | Fuzzy | Reasonable | Owned contexts |
| Evolution | Big-bang | Partial plan | Incremental + metrics |
| Operability | Ignored | Mentioned | First-class |
| Complexity honesty | Hidden | Acknowledged | Costed |

---

## Confidence Checklist

- [ ] Can argue *against* microservices convincingly
- [ ] One DDD example with real contexts
- [ ] Outbox + idempotent consumer story
- [ ] Legacy strangler steps memorized
- [ ] Ready to facilitate tradeoff discussion, not lecture

---

## Notes

<!-- Map your production systems to patterns above for interview anecdotes -->
