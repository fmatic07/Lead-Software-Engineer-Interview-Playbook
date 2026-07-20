# General Banking Guide

> Reusable playbook for Lead/Senior interviews at banks, fintechs with bank-grade controls, and payments platforms — correctness, auditability, security, and calm ownership under regulation.

---

## How to Use This Guide

Use this when the target company has **no named playbook**, or when you need regulated-domain depth beyond a thin company page.

| Situation | How to combine |
|-----------|----------------|
| Named banking/fintech playbook exists (e.g. ING, Maya) | Read named playbook first for market/product/stack; use **this guide** for extra behavioral drills and generic payment/ledger/fraud designs you can retarget. |
| Unknown bank / payments firm / neo-bank | Treat this as primary; research licenses, products, and public tech blogs in 2–3 hours. |
| Banking + enterprise SaaS vendor selling to banks | Pair with [General-Enterprise-Guide](./General-Enterprise-Guide.md) for multi-tenant/upgrade themes. |
| Banking + startup neo-bank / fintech | Pair with [General-Startup-Guide](./General-Startup-Guide.md): velocity **inside** controls. |
| Banking + government payments / public rails | Pair with [General-Government-Guide](./General-Government-Guide.md) for public-trust overlays. |

**Example:** **ING + this guide** — ING for company culture and loop specifics; this guide for extra Qs and designs (ledger, open banking, fraud).

**Audience:** Senior SE → Lead SE → Tech Lead → Architect in banking/payments.

---

## 01 - Sector Overview (for interviewers' lens)

### Industry Patterns

Banking software is not “CRUD with auth.” Money movement, identity, credit risk, fraud, and regulatory reporting create hard constraints that override clever architecture when they conflict.

What panels care about: financial correctness, privacy, operational resilience, and audit-ready reasoning — not post-launch polish.

### Product Shapes

| Surface | Interview signal |
|---------|------------------|
| **Retail channels** | Mobile/web servicing, transfers, cards |
| **Payments / rails** | Instruct → authorize → post → settle → reconcile |
| **Wholesale / corporate** | APIs, cash management, bulk payments |
| **Identity / onboarding** | KYC/AML touchpoints, step-up auth |
| **Fraud / risk** | Scoring, case management, fail-open/closed policy |
| **Core integration** | Not always “the core” — adapters, ledgers, posting |
| **Open banking / partners** | Consents, rate limits, partner audit |

You do not need product trivia. You need invariants: balances, ledgers, settlement windows, dual control, non-repudiation, idempotency.

### Engineering Culture Patterns

- Squad/tribe or platform-aligned teams with clear service boundaries
- Standards that survive audit — not only code review
- Preference for boring, observable systems over novelty
- Explicit risk language when proposing change
- Move fast *inside* guardrails — not around them

### Business Model Implications for Engineering

Interest margins, fee income, interchange, float, wholesale fees — or fintech variants (interchange, SaaS to banks, take-rate).

Engineering impact maps to:

- Lower cost-to-serve via reliable digital channels
- Payment reliability and fewer manual exceptions
- Fraud loss reduction without crushing conversion
- Faster *compliant* product change without regulatory incidents

Frame stories as **risk-adjusted delivery**, not feature velocity alone.

### Scale Patterns

Millions of customers; bursty traffic (payroll days, market opens, salary credits, viral payment moments). Probe:

- Idempotent APIs under retries
- Hot accounts / hotspot partitions
- Multi-region and failover thinking
- Batch + online coexistence (EOD, reporting, clearing files)

### Tech Direction

- Java/Spring microservices on cloud/Kubernetes
- Event-driven integration between domains
- API platforms for partners / open banking
- Zero-trust / strong identity for internal and external APIs
- Platform engineering (paved roads)
- Selective modernization of legacy banking backends
- AI for fraud/ops — never as substitute for deterministic money movement or audit trails

Do not pitch rewrites of core banks. Pitch strangler, dual-running, and measurable cutovers.

---

## 02 - Engineering Expectations

### What Success Looks Like

1. **Correctness under failure** — retries, partial commits, poison messages, clock skew.
2. **Audit-ready reasoning** — who changed what, why, with what evidence.
3. **Boundary clarity** — domain ownership, API contracts, data ownership.
4. **Production empathy** — on-call, runbooks, blast radius, rollback.
5. **Stakeholder fluency** — risk, compliance, product, operations without diluting judgment.

### Ownership

Full lifecycle: design → ship → observe → repair → prevent. Includes:

- Explicit SLO/SLA thinking for money-moving paths
- Escalation when controls are insufficient
- Decisions an auditor could reconstruct later

### Technical Leadership

- Standards for idempotency, logging, PII handling
- Unblock with design spikes and ADRs
- Challenge unsafe shortcuts with alternatives and cost
- Mentor on financial domain pitfalls (not only Spring annotations)

### Product Mindset

Translate outcomes into invariants:

- “Transfer completed” means ledger + notification + reconciliation state are coherent
- “Login succeeded” means authn + session + device risk posture are coherent
- Feature flags respect dual-control where required

### Collaboration Style

- Risk/compliance as partners, not blockers
- Cross-team contracts via APIs/events, not shared databases
- Conflict resolution: data and blast radius first, preference second

### Engineering Principles (interview-usable)

- Prefer **idempotent, auditable** writes over clever in-memory consistency
- Prefer **explicit consistency models** per use case over “eventual everywhere”
- Prefer **paved-road platforms** over bespoke infra per squad
- Prefer **detect + contain** over silent retry forever
- Prefer **reversible change** (flags, dual-write + recon) over big-bang cutovers

---

## 03 - Typical Technology Stack

### Java

Primary language for transactional services. Fluency in concurrency, GC/latency, and **never float for money** (`BigDecimal`, scale rules).

### Spring Boot

DI, config, actuators, security filters, transactional boundaries. Be ready on:

- Transaction demarcation and self-invocation pitfalls
- Outbox patterns with messaging
- Security filter chains / method security for banking APIs

### Cloud

Hardened landing zones, private networking, secrets managers, multi-AZ/region patterns, regulated cloud controls.

**Why it matters:** resilience + audit evidence for infrastructure change.

### Microservices

Domain-aligned services with explicit consistency boundaries. Interviewers probe coupling to “core” and failure isolation.

### Databases

Strong relational systems for postings; careful partitioning; read replicas for non-authoritative views.

**Why it matters:** ledger integrity and hotspot accounts.

### Messaging

Kafka (or equivalent) for domain/integration events; strict consumer idempotency.

**Why it matters:** at-least-once is reality; exactly-once *effects* are designed.

### CI/CD

Controlled pipelines: approvals, artifact provenance, staged promotion, dual control for prod.

**Why it matters:** regulated change management.

### Kubernetes

Common runtime with platform paved roads, network policies, resource isolation.

### Frontend / Channels

Web/mobile often adjacent teams. Backend leads design APIs with clear error taxonomies, idempotency keys, minimal PII in logs.

### AI

Fraud scoring, document processing, ops copilots — behind human/process controls for high-impact decisions. AI augments detection; it does not replace audit trails or deterministic money movement.

---

## 04 - Typical Interview Process

### Stage 1 — Recruiter Screen

**Purpose:** Role fit, level, logistics, motivation for banking/regulated work.

**Evaluation Criteria:** Credible regulated-domain interest; narrative clarity; constraints; communication.

**Preparation Tips:** 90-second pitch ending in reliability, security, leadership. Name 1–2 systems owned end-to-end.

**Common Mistakes:** Generic “I like challenges”; dismissing compliance as bureaucracy; title without ownership evidence.

**Banking variation:** Expect motivation probes on why banking (stability vs impact vs domain). Be specific.

### Stage 2 — Technical Interview

**Purpose:** Java/Spring, APIs, data, concurrency; production failure reasoning.

**Evaluation Criteria:** Correctness; tradeoffs; depth; secure coding instincts.

**Preparation Tips:** Stories for race condition, incident, performance, security fix — with metrics.

**Common Mistakes:** Framework tour; floating-point money; “just add a lock” without scalability discussion.

### Stage 3 — Coding Assessment

**Purpose:** Implementable clarity — often practical (API design, parsing, concurrency-safe structure).

**Evaluation Criteria:** Edge cases; readability; tests; invalid input; domain-revealing naming.

**Preparation Tips:** Idempotent handlers, reconciliation-friendly models, clear error types. Talk while coding.

**Common Mistakes:** Happy path; mutating shared state; no tests; silent catches.

### Stage 4 — System Design

**Purpose:** Banking-adjacent system with consistency, audit, and ops in view.

**Evaluation Criteria:** Clarification; explicit consistency; failure handling; security/PII; operability; evolution.

**Preparation Tips:** Ask about money correctness, idempotency, retention/audit, peak vs steady load. Draw trust boundaries.

**Common Mistakes:** “Update two rows” transfers; no reconciliation; no poison strategy; security last.

### Stage 5 — Leadership Interview

**Purpose:** Influence, mentoring, conflict, delivery under constraint.

**Evaluation Criteria:** Ownership without heroics; standards; cross-team negotiation; incident leadership.

**Preparation Tips:** STAR with risk/compliance stakeholders. Show how you said no safely.

**Common Mistakes:** Pure people-management; blaming compliance; credit-stealing.

### Stage 6 — Hiring Manager

**Purpose:** Team fit, leveling, 90-day expectations.

**Evaluation Criteria:** Self-awareness; learning speed in domain; ambiguity; squad mission alignment.

**Preparation Tips:** Ask about service ownership, on-call, coupling to core banking, top reliability risks.

**Common Mistakes:** Only shiny stack questions; no questions on failure modes or org interfaces.

### Stage 7 — Final Interview

**Purpose:** Senior calibration, architecture/culture fit, residual hire risk.

**Evaluation Criteria:** Judgment under incomplete information; values; non-engineer communication; long-term ownership.

**Preparation Tips:** Questions on platform strategy and risk appetite. Crisp production proof points.

**Common Mistakes:** Rewrite-core bravado; vague culture answers; inability to discuss a mistake honestly.

### Stage 8 — Offer

**Purpose:** Level, scope, compensation, start timing.

**Evaluation Criteria:** Mutual clarity on responsibilities and success metrics.

**Preparation Tips:** Confirm on-call, domain ownership, Lead → Architect path.

**Common Mistakes:** Cash-only negotiation; ambiguous “lead” without decision rights.

---

## 05 - Technical Focus Areas

### Distributed Transactions

- 2PC vs saga vs outbox + messaging
- When local ACID is mandatory (ledger write) vs orchestration enough
- Compensation vs rollback semantics for payments

### Banking Systems Thinking

- Account, balance, posting, value date, booking date
- Double-entry mental model (even if you are not the ledger)
- Settlement windows and cutoffs
- Exception queues and human repair workflows

### Security

- OAuth2/OIDC, mTLS, token audience restrictions
- PII minimization; encryption; key custody
- Secure logging (no PANs/secrets); break-glass access
- Threat modeling for channel/partner APIs

### Event-Driven Architecture

- Domain vs integration events
- Ordering per account/customer key
- Schema evolution / consumer compatibility
- Exactly-once illusion vs idempotent processing reality

### Idempotency

- Idempotency keys on payment APIs
- Dedup stores with TTL and replay windows
- Safe retries from clients, gateways, brokers
- At-least-once delivery consequences

### Audit Trails

- Who/what/when for state changes
- Immutable append-only audit vs mutable business tables
- Correlation IDs across services
- Evidence packs for incidents and regulatory inquiries

### Regulatory Constraints

- Change management and dual control
- Data retention/deletion vs legal hold
- Segregation of duties
- Explainable controls for high-risk operations

### Consistency Models

- Strong consistency for balances/postings
- Read-your-writes for channel UX
- Eventual consistency for analytics/fraud — with lag SLOs
- Stale read hazards in multi-region setups

### Payment / Ledger Thinking

- Instruct → authorize → post → settle → reconcile
- Reconciliation as a first-class system
- Partial failures between gateway and ledger
- Replayable processing from durable logs

### Fraud & Abuse

- Inline vs async detection
- Fail-open vs fail-closed by risk tier
- Explainability for challenges
- Velocity features and hot keys

### Resilience

- Timeouts, bulkheads, backpressure
- Degraded modes that never silently corrupt money
- Chaos/duplicate-callback drills

### Practical Drill List

1. Idempotent transfer API with dedup and ledger posting
2. Outbox publisher that cannot double-send business effects
3. Fraud score path that never blocks posting incorrectly
4. Audit query: all mutations for account X yesterday
5. Partner API with consent, rate limit, and kill switch

---

## 06 - Leadership Focus

### Ownership

End-to-end accountability for money-adjacent services: correctness, latency, error budgets, auditability.

### Mentoring

Teach domain invariants (idempotency, PII, decimal money) as hard skills. Pair on incident reviews.

### Decision Making

ADRs for consistency, storage, integration. Record rejected alternatives — auditors and future leads need the “why not.”

### Cross-team Collaboration

Contracts with channel, risk, core banking, platform. Versioned APIs/events over tribal knowledge.

### Incident Response

Severity by customer funds impact and regulatory exposure. Communicate early, contain blast radius, preserve forensic evidence, then remediate.

### Architecture Discussions

Tradeoff sessions with explicit risk language. Separate “must for compliance” from “preference.”

### Technical Debt

Rank by risk (silent money bugs, missing audit, unowned topics) over aesthetic debt.

### Engineering Culture

Calm urgency; high standards without blame theater. Celebrate detections and prevented incidents, not only launches.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you owned a money-moving or financially sensitive flow end-to-end.

- **Why asked:** Validates true ownership in high-stakes domains.
- **Competencies:** Accountability, domain rigor, production thinking.
- **Excellent answer framework:**
  - **S:** Payment/billing/ledger-adjacent service with correctness risk
  - **T:** Reliability + auditability under deadline
  - **A:** Invariants; idempotency; monitoring; dual-run/recon
  - **R:** Error rate/impact metrics; failure-mode lessons
- **Follow-ups:** What was non-negotiable? How did you prove no double-posting?

### Q2. Describe a production incident that could have caused financial or compliance impact.

- **Why asked:** Incident maturity and honesty.
- **Competencies:** Composure, root cause depth, prevention.
- **Excellent answer framework:**
  - **S:** Symptom + customer/regulatory exposure
  - **T:** Contain → communicate → fix → prevent
  - **A:** Blast-radius limits; evidence preserved; corrective + detective controls
  - **R:** Measurable reduction in recurrence risk
- **Follow-ups:** First 15 minutes? Who notified and why?

### Q3. How have you handled a disagreement with Risk or Compliance?

- **Why asked:** Partnership with control functions.
- **Competencies:** Influence, judgment, stakeholder management.
- **Excellent answer framework:**
  - **S:** Control requirement vs delivery pressure
  - **T:** Safe, shippable design
  - **A:** Options with residual risk; evidence; compromise preserving control intent
  - **R:** Shipped with audit trail; relationship intact
- **Follow-ups:** When have you refused to ship? How documented?

### Q4. Give an example of enforcing idempotency or exactly-once *effects*.

- **Why asked:** Banking correctness signal.
- **Competencies:** Distributed systems depth, practical design.
- **Excellent answer framework:**
  - **S:** Duplicate risk from retries/callbacks
  - **T:** Single business effect
  - **A:** Keys; dedup; transactional outbox; tests
  - **R:** Duplicate rate / incident avoided
- **Follow-ups:** Replay window and TTL choices?

### Q5. Tell me about mentoring someone who made a dangerous assumption (floats for money, swallowed exceptions).

- **Why asked:** Lead-level domain teaching.
- **Competencies:** Mentoring, standards, safety culture.
- **Excellent answer framework:**
  - **S:** Hazardous code/assumption
  - **T:** Correct and prevent class of bug
  - **A:** Pairing; standards; tests/linters; blameless review
  - **R:** Behavior change; fewer defects
- **Follow-ups:** How do you detect these in review systematically?

### Q6. Describe a time you said no to a feature because it violated an invariant.

- **Why asked:** Integrity under product pressure.
- **Competencies:** Judgment, courage, alternatives.
- **Excellent answer framework:**
  - **S:** Feature vs hard money/identity invariant
  - **T:** Protect correctness
  - **A:** Explain; propose safer design; escalate if needed
  - **R:** Safer outcome; trust maintained
- **Follow-ups:** Documented decision trail?

### Q7. Walk through a design decision where you chose strong consistency over availability (or vice versa).

- **Why asked:** Explicit consistency literacy.
- **Competencies:** Tradeoff reasoning, communication.
- **Excellent answer framework:**
  - **S:** Competing requirements
  - **T:** Choose model for the use case
  - **A:** CAP/latency/risk analysis; ADR
  - **R:** Outcome and remaining risks
- **Follow-ups:** What would reverse the decision?

### Q8. Tell me about introducing or improving an audit trail.

- **Why asked:** Auditability bar.
- **Competencies:** Compliance pragmatism, design.
- **Excellent answer framework:**
  - **S:** Insufficient forensic evidence
  - **T:** Reconstructable history
  - **A:** Immutable events; actor/context; retention; query path
  - **R:** Incident/audit win
- **Follow-ups:** PII in audit logs — how handled?

### Q9. Describe leading a cross-team API or event contract change.

- **Why asked:** Integration leadership.
- **Competencies:** Influence, versioning, delivery.
- **Excellent answer framework:**
  - **S:** Breaking or risky contract evolution
  - **T:** Safe migration
  - **A:** Versioning; dual consumers; rollout; metrics
  - **R:** Completion without incident
- **Follow-ups:** How did you handle a lagging consumer team?

### Q10. Give an example of reducing fraud loss or abuse without killing conversion.

- **Why asked:** Risk vs growth judgment.
- **Competencies:** Product risk, experimentation, metrics.
- **Excellent answer framework:**
  - **S:** Fraud/abuse pressure
  - **T:** Reduce loss with acceptable friction
  - **A:** Signals; step-up; shadow rules; measure FP/FN proxies
  - **R:** Loss and conversion metrics
- **Follow-ups:** Fail-open vs fail-closed policy?

### Q11. Tell me about a legacy modernization executed without a big-bang rewrite.

- **Why asked:** Banking-compatible change strategy.
- **Competencies:** Architecture, incremental delivery.
- **Excellent answer framework:**
  - **S:** Legacy constraint
  - **T:** Incremental replacement
  - **A:** Strangler; dual-run; recon; sliced cutover
  - **R:** Risk reduced; capability delivered
- **Follow-ups:** What stayed on legacy deliberately?

### Q12. Describe how you handle secrets, keys, or certificates in a service you owned.

- **Why asked:** Security operations maturity.
- **Competencies:** Secure ops, rotation discipline.
- **Excellent answer framework:**
  - **S:** Key/secret lifecycle need
  - **T:** Least privilege + rotation without downtime
  - **A:** KMS/HSM patterns; dual keys; runbooks; access audit
  - **R:** Successful rotation / audit pass
- **Follow-ups:** Break-glass and emergency revoke?

### Q13. Tell me about improving reconciliation or detecting silent data drift.

- **Why asked:** Detective controls mindset.
- **Competencies:** Data integrity, observability.
- **Excellent answer framework:**
  - **S:** Divergence risk between systems
  - **T:** Detect and repair systematically
  - **A:** Recon jobs; tolerances; alerting; repair tools
  - **R:** Drift found/fixed; prevention
- **Follow-ups:** How do you avoid alert fatigue on recon?

### Q14. Describe a performance optimization on a hot banking path.

- **Why asked:** Latency under correctness constraints.
- **Competencies:** Profiling, careful caching.
- **Excellent answer framework:**
  - **S:** Hot path SLO miss
  - **T:** Improve p99 without corrupting money
  - **A:** Measure; bottleneck; safe cache/partition; verify
  - **R:** Latency metrics; correctness held
- **Follow-ups:** What could you not cache and why?

### Q15. Tell me about influencing engineering standards across squads.

- **Why asked:** Lead/architect impact.
- **Competencies:** Influence without authority.
- **Excellent answer framework:**
  - **S:** Inconsistent practices causing risk
  - **T:** Raise bar
  - **A:** RFC; paved road; examples; review norms
  - **R:** Adoption; fewer incidents
- **Follow-ups:** How avoid process theater?

### Q16. Give an example of designing for GDPR/privacy or data minimization.

- **Why asked:** Privacy as first-class.
- **Competencies:** Privacy engineering, pragmatism.
- **Excellent answer framework:**
  - **S:** Over-collection or retention risk
  - **T:** Minimize while preserving function
  - **A:** Field reduction; tokenization; retention; access controls
  - **R:** Compliance posture / reduced blast radius
- **Follow-ups:** Legal hold vs deletion conflicts?

### Q17. Describe managing delivery across time zones or multiple countries.

- **Why asked:** Global bank reality.
- **Competencies:** Coordination, clarity, handoffs.
- **Excellent answer framework:**
  - **S:** Distributed stakeholders/teams
  - **T:** Predictable delivery
  - **A:** Written contracts; overlap rituals; ownership clarity
  - **R:** Fewer dropped balls; on-time outcomes
- **Follow-ups:** Data residency constraints you navigated?

### Q18. Tell me about a poorly defined requirement you turned into a safe design.

- **Why asked:** Ambiguity in regulated contexts.
- **Competencies:** Clarification, risk framing.
- **Excellent answer framework:**
  - **S:** Vague ask with money/risk implications
  - **T:** Explicit invariants before code
  - **A:** Questions; options; ADR; SME validation
  - **R:** Safe delivery; fewer late surprises
- **Follow-ups:** What assumption did you document?

### Q19. Describe your approach to on-call and reducing toil.

- **Why asked:** Sustainable production ownership.
- **Competencies:** SRE instincts, prioritization.
- **Excellent answer framework:**
  - **S:** Noisy/painful on-call
  - **T:** Healthier reliability
  - **A:** Alert quality; runbooks; fix classes; error budgets
  - **R:** Page volume / MTTR
- **Follow-ups:** How negotiate reliability work with product?

### Q20. Tell me about dual-control or segregation of duties in engineering process.

- **Why asked:** Control literacy.
- **Competencies:** Governance without paralysis.
- **Excellent answer framework:**
  - **S:** High-risk change path
  - **T:** Prevent unilateral dangerous action
  - **A:** Approvals; break-glass audit; tooling
  - **R:** Control effective; delivery viable
- **Follow-ups:** Friction you redesigned later?

### Q21. Give an example of communicating a technical risk to non-engineers.

- **Why asked:** Risk communication bar.
- **Competencies:** Clarity, influence.
- **Excellent answer framework:**
  - **S:** Invisible technical risk
  - **T:** Informed decision
  - **A:** Business impact; options; residual risk
  - **R:** Tradeoff made consciously
- **Follow-ups:** When leadership accepted residual risk — how mitigate?

### Q22. Describe a conflict within your team about architecture direction.

- **Why asked:** Facilitation under disagreement.
- **Competencies:** Conflict resolution, judgment.
- **Excellent answer framework:**
  - **S:** Split opinions
  - **T:** Decide and commit
  - **A:** Criteria; spike; ADR; revisit triggers
  - **R:** Alignment; delivery unblocked
- **Follow-ups:** How keep dissenters engaged?

### Q23. Tell me about delivering under a hard regulatory or audit deadline.

- **Why asked:** Regulated delivery pressure.
- **Competencies:** Scope control, calm execution, evidence.
- **Excellent answer framework:**
  - **S:** External deadline
  - **T:** Credible compliant ship
  - **A:** Scope; controls first; evidence pack; progressive delivery
  - **R:** Met deadline; audit artifacts ready
- **Follow-ups:** What did you defer explicitly?

### Q24. Describe how you measure success for a platform or shared service.

- **Why asked:** Platform leadership.
- **Competencies:** Metrics, consumer empathy.
- **Excellent answer framework:**
  - **S:** Shared service with unclear success
  - **T:** Define SLOs + adoption
  - **A:** Latency/error; consumer satisfaction; toil
  - **R:** Clearer prioritization
- **Follow-ups:** Consumers bypassing the platform?

### Q25. Tell me about improving test strategy for a critical path.

- **Why asked:** Quality on money paths.
- **Competencies:** Testing strategy, risk-based QA.
- **Excellent answer framework:**
  - **S:** Escaped defects on critical flow
  - **T:** Higher confidence without freezing delivery
  - **A:** Risk-based tests; contract tests; recon tests; chaos duplicates
  - **R:** Defect escape rate
- **Follow-ups:** What remains untested and why acceptable?

### Q26. Give an example of handling a vendor/third-party payment dependency failure.

- **Why asked:** External rail reality.
- **Competencies:** Resilience, ops communication.
- **Excellent answer framework:**
  - **S:** Scheme/gateway outage or partial failure
  - **T:** Protect customers; avoid double effects
  - **A:** Timeouts; status model; retries; recon; customer messaging
  - **R:** Recovery; permanent hardening
- **Follow-ups:** How detect “unknown” payment states?

### Q27. Describe balancing speed of delivery with change-management controls.

- **Why asked:** Velocity inside guardrails.
- **Competencies:** Pragmatism, process design.
- **Excellent answer framework:**
  - **S:** Control overhead vs delivery need
  - **T:** Faster safe path
  - **A:** Risk-tiered controls; automation; paved roads
  - **R:** Lead time down; incidents not up
- **Follow-ups:** What control did you refuse to weaken?

### Q28. Tell me about hiring, interviewing, or leveling engineers.

- **Why asked:** Bar raising.
- **Competencies:** Talent, fairness, calibration.
- **Excellent answer framework:**
  - **S:** Hiring need
  - **T:** High-signal evaluation
  - **A:** Rubric including correctness/security instincts; structured debrief
  - **R:** Hire quality
- **Follow-ups:** How assess regulated-domain aptitude without unfair trivia?

### Q29. Describe a mistake you made in a design and how you corrected it.

- **Why asked:** Honesty and recovery.
- **Competencies:** Humility, remediation.
- **Excellent answer framework:**
  - **S:** Flawed assumption
  - **T:** Correct with minimal harm
  - **A:** Detect; contain; fix; prevent
  - **R:** Lesson in standards/tests
- **Follow-ups:** Early warning missed?

### Q30. Why banking / this institution, and why this level?

- **Why asked:** Motivation authenticity.
- **Competencies:** Self-awareness, alignment.
- **Excellent answer framework:**
  - **S:** Trajectory and strengths
  - **T:** Match to regulated impact problems
  - **A:** Specific domain reasons + level evidence
  - **R:** 90-day value thesis
- **Follow-ups:** Why not a less regulated product company?

### Q31. Tell me about building or improving a kill switch / feature flag strategy for risky financial features.

- **Why asked:** Safe release of money features.
- **Competencies:** Release engineering, risk.
- **Excellent answer framework:**
  - **S:** High-risk launch
  - **T:** Progressive exposure + instant disable
  - **A:** Flags; metrics; kill criteria; dual control for enable
  - **R:** Contained incident or clean ramp
- **Follow-ups:** Who can disable in production at 2 a.m.?

### Q32. Describe collaborating with security on a threat model for a new API.

- **Why asked:** Secure-by-design partnership.
- **Competencies:** Threat modeling, collaboration.
- **Excellent answer framework:**
  - **S:** New exposure
  - **T:** Acceptable residual risk
  - **A:** Assets; threats; controls; tests
  - **R:** Issues found pre-prod
- **Follow-ups:** Deferred control and rationale?

### Q33. Tell me about designing customer-facing statuses for payments that can be ambiguous mid-flight.

- **Why asked:** UX honesty under async rails.
- **Competencies:** Product thinking, state machines.
- **Excellent answer framework:**
  - **S:** Users confused by pending/unknown states
  - **T:** Clear, truthful statuses + ops repair
  - **A:** Explicit state machine; timeouts; customer copy; agent tools
  - **R:** Support drop; fewer duplicate submissions
- **Follow-ups:** How prevent users from double-submitting?

### Q34. Give an example of preserving forensic evidence during an incident.

- **Why asked:** Regulated incident maturity.
- **Competencies:** Forensics, composure.
- **Excellent answer framework:**
  - **S:** Incident with investigation needs
  - **T:** Fix without destroying evidence
  - **A:** Snapshot logs; retain payloads carefully; chain of custody mindset
  - **R:** Root cause proven; regulators/stakeholders satisfied
- **Follow-ups:** Privacy vs evidence tension?

---

## 08 - System Design Questions

### Design 1 — Payment Processing Pipeline

**Requirements**

- Accept payment instructions from channels
- Validate, authorize, post, notify
- Exactly-once *business effect* despite retries
- Observable statuses for customers and ops
- Peak load on payroll / promo days

**Architecture Discussion**

- API with idempotency keys
- Validation + risk hooks (sync cheap, async deep)
- Durable orchestration (workflow or state machine + outbox)
- Ledger posting as strong-consistency boundary
- Status projection via events
- Dead-letter + repair console

**Tradeoffs**

- Orchestration complexity vs distributed spaghetti
- Sync fraud checks vs latency
- Choreography vs orchestration for state clarity

**Scaling**

- Partition by payment/account; protect hot accounts
- Horizontal scale of stateless validators
- Backpressure when ledger/scheme is slow

**Reliability**

- Timeouts, retries with jitter, compensations where applicable
- Reconciliation against scheme/gateway reports
- Runbooks for stuck states

**Security**

- mTLS; scoped tokens; field encryption
- Maker-checker for manual repairs
- Full audit of status transitions

**Production Considerations**

- SLOs per stage; synthetic payments; PII banned from logs; chaos for duplicate callbacks

### Design 2 — Fraud Detection Feed

**Requirements**

- Stream payment/login events to scorers
- Near-real-time signals without incorrectly blocking all payments
- Model/rule updates without downtime
- Explainability for challenges

**Architecture Discussion**

- Event bus from channels/payment services
- Feature pipeline; online store for velocity features
- Versioned rule/model service
- Decisioning API + async enrichment
- Case management for analysts

**Tradeoffs**

- False positives vs fraud loss
- Inline vs side-door detection
- Central model platform vs embedded rules

**Scaling**

- Consumer groups; hot-key customer features
- Sampling/aggregation for ultra-high volume

**Reliability**

- Degrade: fail-open vs fail-closed by product risk tier
- Poison event isolation; shadow mode for new rules

**Security**

- Strict access to PII features; audited rule changes; model/data exfil controls

**Production Considerations**

- Latency budgets; feature staleness SLOs; replay for forensics; KPI dashboards

### Design 3 — Account Ledger Consistency

**Requirements**

- Post debits/credits with double-entry integrity
- No lost updates under concurrency
- Query balance and statement history
- Corrections/reversals with audit

**Architecture Discussion**

- Ledger DB as system of record; append-only postings
- Account concurrency (row version / per-account queue)
- Balance materialization strategies
- Read models for statements
- Reversal as compensating postings (never silent edits)

**Tradeoffs**

- Single-account serialization vs throughput
- Synchronous vs projected balance reads
- Shard by account vs ops complexity

**Scaling**

- Partition by account id; separate hot post vs cold history
- Never cache authoritative balance without versioning

**Reliability**

- Deterministic posting IDs; integrity checks; PITR drills

**Security**

- Strict service identity to post; immutable audit; break-glass

**Production Considerations**

- Dual posting + recon for legacy migration; manual adjustment procedures; statement seasonality capacity

### Design 4 — Customer Identity & Auth for Banking

**Requirements**

- Authenticate across web/mobile
- Step-up for high-risk actions
- Session management, device binding, revocation
- Integrate fraud signals

**Architecture Discussion**

- IdP/OIDC; token service; session store
- Device/risk hooks; step-up (OTP/push/passkey)
- Central authZ for sensitive ops; login anomaly events

**Tradeoffs**

- UX friction vs ATO prevention
- Centralized IdP vs embedded auth
- Token lifetime vs revocation complexity

**Scaling**

- Stateless access tokens + scalable session/revocation store
- Regional latency/residency

**Reliability**

- Carefully limited IdP degraded modes; OTP rate limits; multi-AZ session store

**Security**

- Credential stuffing defenses; MFA; secure recovery; anti-enumeration; signing key rotation

**Production Considerations**

- Key rotation runbooks; privacy of auth logs; SCA expectations where applicable

### Design 5 — Open Banking / Partner API Platform

**Requirements**

- Expose account/payment APIs to partners
- Per-partner rate limits, consents, audit
- Versioning and break-glass disable per client

**Architecture Discussion**

- API gateway; OAuth client credentials / consent flows
- Consent store; fine-grained scopes
- Per-client quotas; usage metering
- Full request/response audit with redaction
- Kill switch per partner

**Tradeoffs**

- Gateway centralization vs service mesh complexity
- Sync payment initiation vs async status
- Granular scopes vs partner UX

**Scaling**

- Edge caching for non-sensitive reads only
- Isolate noisy partners

**Reliability**

- Idempotency mandated in partner docs
- Status webhooks with retries
- Synthetic partner journeys

**Security**

- mTLS; JWS; certificate pinning guidance
- Consent revocation propagation
- Anomaly detection on partner traffic

**Production Considerations**

- Partner onboarding checklist; sandbox parity; incident comms to partners

### Design 6 — End-of-Day / Clearing File Reconciliation

**Requirements**

- Ingest scheme/gateway settlement files
- Match to internal postings; surface breaks
- Support repair workflows with dual control
- Meet cutoff windows

**Architecture Discussion**

- File intake with checksums and duplicate detection
- Parser → normalize → match engine
- Break queue + maker-checker repair UI
- Reports for ops/finance; immutable audit of repairs

**Tradeoffs**

- Real-time micro-recon vs batch windows
- Auto-repair vs human-only for money breaks
- Tight coupling to ledger vs derived match keys

**Scaling**

- Parallelize by file partition / account ranges
- Prioritize high-value breaks

**Reliability**

- Late file handling; partial file quarantine
- Replay safe; clock/cutoff edge cases

**Security**

- Secure file transfer; least privilege ops roles
- Dual control on manual posts

**Production Considerations**

- Runbooks for cutoff misses; capacity for month-end; evidence packs for audits

---

## 09 - Preparation Checklist

- [ ] Research target bank/fintech: products, licenses, public tech themes
- [ ] Map last 3 stories to: money correctness, security, audit, incident leadership
- [ ] Prepare one ADR-style consistency decision
- [ ] Whiteboard: idempotency + outbox + reconciliation (20 min timed)
- [ ] Draft threat model bullets for an API you owned
- [ ] Prepare metrics: latency, error budget, duplicate rate, MTTR
- [ ] Write 90-day plan: learn domain, stabilize, deliver one risk-reducing improvement
- [ ] List 8 questions (ownership, core coupling, on-call, paved roads, risk appetite)
- [ ] Refresh Spring transactions, consumer semantics, authN/Z
- [ ] Practice double-entry / posting invariants without pretending you built a core bank
- [ ] Prepare “said no to product” + cross-team contract migration stories
- [ ] Align resume to banking language (recon, controls, blast radius) without fabrication
- [ ] Mock: payments pipeline + fraud feed; behavioral incident + compliance disagreement
- [ ] If combining with named playbook (e.g. ING): overlay company specifics onto these drills
- [ ] Sleep/logistics for multi-stage loop days

---

## 10 - How My Experience Maps

### Enterprise Experience

Map large-org delivery to squad/tribe reality: governance navigated, standards raised, shipping inside controls.

### Performance Optimization

Hot paths where correctness constrained caching; measurement discipline and p99 outcomes.

### Legacy Modernization

Strangler, dual-run, reconciliation, rollback — banking-compatible narrative.

### Leadership

Standards (idempotency, logging, reviews), mentoring on domain hazards, incident command.

### Cloud

Landing zones, secrets, observability, multi-AZ — not “we moved VMs.”

### Architecture

ADRs, explicit consistency, outbox/sagas with failure semantics.

### Scalability

Partitioning, backpressure, hotspot accounts — controlled concurrency.

### Mentoring

Multiplied safe practices across a team.

### Product Ownership

Risk-adjusted customer value: fewer failed payments, faster safe release, clearer statuses.

### Combining with Named Playbooks

**ING + Banking guide:** company culture/loop from ING; extra Qs/designs here. **Maya + Banking guide:** fintech product specifics from Maya; deepen ledger/fraud/idempotency with this guide.

---

## Interview Confidence Checklist

- [ ] I can explain idempotency vs exactly-once delivery without hand-waving
- [ ] I can design a posting flow with audit and reconciliation
- [ ] I can discuss fail-open vs fail-closed for fraud/risk
- [ ] I have 5 STAR stories mapped to banking themes
- [ ] I can critique a naive “update balance” design in under 3 minutes
- [ ] I know my leveling pitch (Senior vs Lead vs Architect)
- [ ] I can describe a production incident including prevention
- [ ] I have intelligent questions about platform and risk appetite

---

## Mock Interview Preparation Checklist

- [ ] 45-min system design: payment pipeline
- [ ] 45-min system design: ledger consistency
- [ ] 30-min deep dive: messaging + outbox on a past system
- [ ] 45-min behavioral: Q2, Q3, Q6, Q11, Q21
- [ ] 60-min coding: idempotent API handler + tests
- [ ] Feedback captured; weak stories rewritten with metrics
- [ ] Second pass mocks after gap closure

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | Sector overview + stack “why” + resume language mapping (+ named playbook if any) |
| 2 | Distributed transactions, idempotency, outbox drills |
| 3 | Security, audit, privacy behavioral + technical Qs |
| 4 | System design: payments + ledger |
| 5 | System design: fraud + identity / open banking |
| 6 | Leadership/behavioral battery (selective deep practice) |
| 7 | Full mock loop + gap fixes |
| 8 | Light review, interviewer questions, rest |

---

## Estimated Preparation Time

**5–8 days** of focused prep (2–4 hours/day) if Java/Spring/system design/leadership modules are already complete. Stretch to **10 days** if you lack prior fintech/payments stories and need to carefully translate adjacent experience (billing, inventory reservations, identity platforms) into banking-relevant narratives without overclaiming.
