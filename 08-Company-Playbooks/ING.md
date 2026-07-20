# ING

> Reverse-engineer how ING evaluates Lead/Senior engineers for regulated banking platforms — correctness, auditability, and calm ownership under constraint.

---

## 01 - Company Overview

### Industry

ING is a global retail and wholesale bank. Interviewers assume you understand that banking software is not "just CRUD with auth": money movement, customer identity, credit risk, and regulatory reporting create hard constraints that override clever architecture when they conflict.

What panels care about: you treat financial correctness, privacy, and operational resilience as first-class requirements — not post-launch polish.

### Products

Expect interviewers to situate your role near one of these surfaces (names vary by market):

- Retail banking channels (mobile/web, account servicing, payments)
- Wholesale/corporate banking APIs and cash management
- Payment rails and transfer orchestration
- Customer identity, onboarding, KYC/AML touchpoints
- Risk, fraud, and monitoring feeds
- Core banking integration layers (not always "the core" itself)

You do not need product trivia. You need to show you know which domain invariants matter: balances, ledgers, settlement windows, dual control, and non-repudiation.

### Engineering Culture

ING historically invested in Agile@Scale / Squad-Tribe models and strong platform ownership. In interviews, culture shows up as:

- Squad autonomy with clear service boundaries
- Engineering standards that survive audit (not just code review)
- Preference for boring, observable systems over novelty
- Explicit risk language when proposing change

Signal you can move fast *inside* guardrails — not around them.

### Business Model

Interest margins, fee income, and wholesale banking. Engineering impact maps to:

- Lower cost-to-serve via reliable digital channels
- Payment reliability and reduced manual exception handling
- Fraud loss reduction without crushing conversion
- Faster compliant product change without regulatory incidents

Frame your stories as risk-adjusted delivery, not feature velocity alone.

### Scale

Millions of customers across markets; payment and session traffic that is bursty (payroll days, market opens, outages elsewhere). Scale interviews will probe:

- Idempotent APIs under retries
- Hot accounts / hotspot partitions
- Multi-region and failover thinking
- Batch + online coexistence (end-of-day, reporting)

### Global Presence

Multi-country presence means multi-jurisdiction constraints. Interviewers may probe:

- Data residency and cross-border transfer limits
- Market-specific payment schemes
- Shared platforms vs local adaptations
- Time-zone operational handoffs

### Technology Direction

Expect discussion around:

- Java/Spring microservices on cloud/Kubernetes
- Event-driven integration between domains
- API platforms for partner/open banking style exposure
- Zero-trust / strong identity for internal and external APIs
- Platform engineering (paved roads for squads)
- Selective modernization of legacy banking backends

Do not pitch rewrites. Pitch strangler patterns, dual-running, and measurable cutovers.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

Successful candidates demonstrate:

1. **Correctness under failure** — retries, partial commits, poison messages, clock skew.
2. **Audit-ready reasoning** — who changed what, why, with what evidence.
3. **Boundary clarity** — domain ownership, API contracts, data ownership.
4. **Production empathy** — on-call, runbooks, blast radius, rollback.
5. **Stakeholder fluency** — risk, compliance, product, operations without diluting technical judgment.

### Ownership

Own the full lifecycle: design → ship → observe → repair → prevent recurrence. For banking, ownership includes:

- Explicit SLO/SLA thinking for money-moving paths
- Clear escalation when controls are insufficient
- Documented decisions that an auditor could reconstruct later

### Technical Leadership

Leads are expected to:

- Set standards for idempotency, logging, PII handling
- Unblock squads with design spikes and ADRs
- Challenge unsafe shortcuts with alternatives and cost
- Mentor juniors on financial domain pitfalls (not just Spring annotations)

### Product Mindset

Translate customer outcomes into invariants:

- "Transfer completed" means ledger + notification + reconciliation state are coherent
- "Login succeeded" means authn + session + device risk posture are coherent
- Feature flags and dark launches respect regulatory dual-control where required

### Collaboration Style

- Work with risk/compliance as partners, not blockers
- Cross-tribe contracts via APIs and events, not shared databases
- Conflict resolution: data and blast radius first, preference second

### Engineering Principles (interview-usable)

- Prefer **idempotent, auditable** writes over clever in-memory consistency
- Prefer **explicit consistency models** per use case over "eventual everywhere"
- Prefer **paved-road platforms** over bespoke infra per squad
- Prefer **detect + contain** over silent retry forever
- Prefer **reversible change** (feature flags, dual-write with reconciliation) over big-bang cutovers

---

## 03 - Typical Technology Stack

Explain each in interviews as *why ING cares*, not as a resume list.

### Java

Primary enterprise language for transactional services. Interviewers expect fluency in concurrency, memory, GC impact on latency, and defensive coding around money/decimal types (`BigDecimal`, scale rules — never float).

### Spring Boot

Default service framework: DI, config, actuators, security filters, transactional boundaries. Be ready on:

- Transaction demarcation and self-invocation pitfalls
- Outbox patterns with Spring messaging
- Security filter chains and method security for banking APIs

### Cloud

Public cloud and/or regulated cloud landing zones. Matters because:

- Network segmentation and private connectivity to core systems
- Secrets management and key rotation
- Region strategy for resilience and data residency

### Microservices

Domain-aligned services with independent deployability. Banking twist: service boundaries must align with **data ownership and audit scope**, not just team convenience.

### Databases

Relational stores (often PostgreSQL/Oracle/DB2-class in estates) remain the source of truth for ledgers and account state. Expect:

- Strong transactional guarantees for money
- Careful use of eventual consistency for projections/read models
- Migration strategies that preserve audit history

### Messaging

Kafka or enterprise MQ for events between domains. Critical for:

- Payment status propagation
- Fraud/risk feeds
- Decoupling channel apps from core processors

Interviewers will push on **at-least-once + idempotent consumers**, ordering keys, and poison-message handling.

### CI/CD

Gated pipelines with security scanning, policy checks, and controlled promotion. Success looks like: fast feedback *with* change control — not cowboy deploys, not week-long freezes as the only control.

### Kubernetes

Common runtime for stateless APIs and workers. Matters for:

- Horizontal scale of channel/payment APIs
- Resource isolation and blast-radius limits
- Rolling deploys with readiness that actually reflects dependency health

### Infrastructure

IaC, service mesh or API gateways, observability stacks. Banking emphasis: configuration as audited change, least privilege, environment parity without copying prod secrets.

### Frontend

Channel apps (web/mobile) often owned by adjacent teams. As a backend lead, show you design APIs for:

- Clear error taxonomies (retryable vs terminal)
- Idempotency keys from clients
- Minimal PII in logs and analytics

### AI (where applicable)

Fraud scoring, document processing, ops copilots — always behind human/process controls for high-impact decisions. Interview stance: AI augments detection; it does not replace audit trails or deterministic money movement.

---

## 04 - Typical Interview Process

Stages vary by market and role; prepare for this full loop.

### Stage 1 — Recruiter Screen

**Purpose:** Role fit, level calibration, logistics, motivation for banking.

**Evaluation Criteria:** Clarity of career narrative; regulated-domain interest that is credible; compensation/location constraints; communication quality.

**Preparation Tips:** 90-second pitch ending in banking-relevant strengths (reliability, security, leadership). Name 1–2 systems you owned end-to-end.

**Common Mistakes:** Generic "I like challenges"; dismissing compliance as bureaucracy; overselling titles without ownership evidence.

### Stage 2 — Technical Interview

**Purpose:** Depth in Java/Spring, APIs, data, concurrency; how you reason about production failure.

**Evaluation Criteria:** Correctness; tradeoff awareness; ability to go one level deeper on your own claims; secure coding instincts.

**Preparation Tips:** Rehearse one story each for: race condition, production incident, performance win, security fix. Prefer metrics.

**Common Mistakes:** Framework tour without failure modes; floating-point money; "just add a lock" without deadlock/scalability discussion.

### Stage 3 — Coding Assessment

**Purpose:** Implementable clarity under time pressure — often practical (API design, parsing, concurrency-safe structure), not trivia.

**Evaluation Criteria:** Correct edge cases; readability; tests; explicit handling of invalid input; naming that reveals domain thinking.

**Preparation Tips:** Practice idempotent handlers, reconciliation-friendly data models, and clear error types. Talk while coding.

**Common Mistakes:** Happy-path only; mutating shared state casually; no tests; silent catch blocks.

### Stage 4 — System Design

**Purpose:** Design a banking-adjacent system with consistency, audit, and ops in view.

**Evaluation Criteria:** Requirements clarification; explicit consistency model; failure handling; security/PII; operability; evolution path.

**Preparation Tips:** Always ask about money correctness, idempotency, retention/audit, and peak vs steady load. Draw trust boundaries.

**Common Mistakes:** Treating bank transfer as "update two rows"; ignoring reconciliation; no poison-message strategy; security bolted on last.

### Stage 5 — Leadership Interview

**Purpose:** Influence, mentoring, conflict, delivery under constraint.

**Evaluation Criteria:** Ownership without heroics; evidence of raising standards; cross-team negotiation; incident leadership.

**Preparation Tips:** STAR stories with regulatory or risk stakeholders. Show how you said no safely.

**Common Mistakes:** Pure people-management answers with no technical substance; blaming compliance; credit-stealing narratives.

### Stage 6 — Hiring Manager

**Purpose:** Team fit, leveling, working style, expectations for the first 90 days.

**Evaluation Criteria:** Self-awareness; learning speed in new domain; how you handle ambiguity; alignment with squad mission.

**Preparation Tips:** Ask about service ownership, on-call, coupling to core banking, and current top reliability risks.

**Common Mistakes:** Only asking about tech stack shiny objects; no questions about failure modes or org interfaces.

### Stage 7 — Final Interview

**Purpose:** Senior calibration, architecture/culture fit, residual risk on hire.

**Evaluation Criteria:** Judgment under incomplete information; values alignment; communication to non-engineers; long-term ownership signal.

**Preparation Tips:** Bring 2–3 thoughtful questions on platform strategy and risk appetite. Be crisp on your strongest production proof points.

**Common Mistakes:** Overconfidence about rewriting cores; vague "culture fit" answers; inability to discuss a past mistake honestly.

### Stage 8 — Offer

**Purpose:** Level, scope, compensation, start timing.

**Evaluation Criteria:** Mutual clarity on responsibilities and success metrics.

**Preparation Tips:** Confirm on-call expectations, domain ownership, and growth path (Lead → Architect track).

**Common Mistakes:** Negotiating only cash while ignoring scope/level; accepting ambiguous "lead" titles without reporting/decision rights clarity.

---

## 05 - Technical Focus Areas

Company-specific topics most likely to appear for ING-style panels.

### Distributed Transactions

- 2PC vs saga vs outbox + messaging
- When local ACID is mandatory (ledger write) vs when orchestration is enough
- Compensation vs rollback semantics for payments

### Banking Systems Thinking

- Account, balance, posting, value date, booking date
- Double-entry mental model (even if your service is not the ledger)
- Settlement windows and cutoffs
- Exception queues and human repair workflows

### Security

- OAuth2/OIDC, mTLS between services, token audience restrictions
- PII minimization, encryption at rest/in transit, key custody
- Secure logging (no PANs/secrets), break-glass access
- Threat modeling for APIs exposed to channels/partners

### Event-Driven Architecture

- Domain events vs integration events
- Ordering per account/customer key
- Schema evolution and consumer compatibility
- Exactly-once illusion vs idempotent processing reality

### Idempotency

- Idempotency keys on payment APIs
- Dedup stores with TTL and replay windows
- Safe retries from clients, gateways, and brokers
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
- Eventual consistency for analytics/fraud features — with explicit lag SLOs
- Stale read hazards in multi-region setups

### Payment / Ledger Thinking

- Instruct → authorize → post → settle → reconcile
- Reconciliation as a first-class system, not a script
- Partial failures between payment gateway and ledger
- Replayable processing from durable logs

### Practical Drill List

Be ready to whiteboard:

1. Idempotent transfer API with dedup and ledger posting
2. Outbox publisher that cannot double-send business effects
3. Fraud score async path that never blocks posting incorrectly
4. Audit query path for "show all mutations for account X yesterday"

---

## 06 - Leadership Focus

### Ownership

End-to-end accountability for money-adjacent services: correctness, latency, error budgets, and auditability.

### Mentoring

Teach juniors domain invariants (idempotency, PII, decimal money) as hard skills. Pair on incident reviews, not only feature code.

### Decision Making

Use ADRs for consistency model, storage, and integration choices. Record rejected alternatives — auditors and future leads need the "why not."

### Cross-team Collaboration

Contracts with channel, risk, core banking, and platform teams. Prefer versioned APIs/events over tribal knowledge.

### Incident Response

Severity based on customer funds impact and regulatory exposure. Communicate early, contain blast radius, preserve forensic evidence, then remediate.

### Architecture Discussions

Facilitate tradeoff sessions with explicit risk language. Separate "must for compliance" from "preference."

### Technical Debt

Rank debt by risk (silent money bugs, missing audit, unowned topics) over aesthetic debt. Schedule paydown with product risk owners.

### Engineering Culture

Model calm urgency: high standards without blame theater. Celebrate detections and prevented incidents, not only launches.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you owned a money-moving or financially sensitive flow end-to-end.

- **Why asked:** Validates true ownership in high-stakes domains.
- **Competencies:** Accountability, domain rigor, production thinking.
- **Excellent answer framework:**
  - **S:** Payment/billing/ledger-adjacent service with clear correctness risk
  - **T:** Deliver reliability + auditability under deadline
  - **A:** Invariants defined; idempotency; monitoring; dual-run/reconciliation
  - **R:** Error rate/impact metrics; lessons on failure modes
- **Follow-ups:** What was non-negotiable? How did you prove no double-posting?

### Q2. Describe a production incident that could have caused financial or compliance impact.

- **Why asked:** Incident maturity and honesty.
- **Competencies:** Composure, root cause depth, prevention.
- **Excellent answer framework:**
  - **S:** Symptom + customer/regulatory exposure
  - **T:** Contain → communicate → fix → prevent
  - **A:** Blast-radius limits; evidence preserved; corrective + detective controls
  - **R:** Measurable reduction in recurrence risk
- **Follow-ups:** What would you do differently in the first 15 minutes? Who did you notify and why?

### Q3. How have you handled a disagreement with Risk or Compliance?

- **Why asked:** Partnership with control functions.
- **Competencies:** Influence, judgment, stakeholder management.
- **Excellent answer framework:**
  - **S:** Control requirement vs delivery pressure
  - **T:** Reach a safe, shippable design
  - **A:** Options with residual risk; evidence; compromise that preserves control intent
  - **R:** Shipped with audit trail; relationship intact
- **Follow-ups:** When have you refused to ship? How did you document the decision?

### Q4. Give an example of enforcing idempotency or exactly-once *effects* in a system.

- **Why asked:** Banking correctness signal.
- **Competencies:** Distributed systems depth, practical design.
- **Excellent answer framework:**
  - **S:** At-least-once bus or client retries
  - **T:** Prevent duplicate business effects
  - **A:** Idempotency keys; dedup store; natural keys; outbox
  - **R:** Duplicate rate before/after; replay drills
- **Follow-ups:** How long do you retain dedup keys? What happens on key collision across customers?

### Q5. Tell me about mentoring someone who made a dangerous assumption (e.g., floats for money, swallowed exceptions).

- **Why asked:** Leadership through standards.
- **Competencies:** Mentoring, quality culture.
- **Excellent answer framework:**
  - **S:** Risky pattern spotted in review or prod near-miss
  - **T:** Fix and teach without humiliation
  - **A:** Concrete example; guideline; checklist; follow-up review
  - **R:** Behavior change; fewer similar defects
- **Follow-ups:** How do you scale that teaching beyond one person?

### Q6. Describe a time you said no to a feature because it violated an invariant.

- **Why asked:** Backbone under product pressure.
- **Competencies:** Technical integrity, communication.
- **Excellent answer framework:**
  - **S:** Request that broke ledger/security/audit invariant
  - **T:** Protect customers and bank while offering alternatives
  - **A:** Explained blast radius; proposed safer design; aligned stakeholders
  - **R:** Better outcome shipped; trust increased
- **Follow-ups:** What was the product's reaction? Did you escalate?

### Q7. Walk through a design decision where you chose strong consistency over availability (or vice versa).

- **Why asked:** Explicit CAP/consistency judgment for banking.
- **Competencies:** Architecture tradeoffs.
- **Excellent answer framework:**
  - **S:** Use case (balance check vs marketing feed)
  - **T:** Pick model matching business risk
  - **A:** Alternatives; failure modes; UX implications
  - **R:** Outcome and measured lag/error impact
- **Follow-ups:** How did clients detect staleness? What was the SLO for lag?

### Q8. Tell me about introducing or improving an audit trail.

- **Why asked:** Regulatory readiness.
- **Competencies:** Observability for compliance, design thoroughness.
- **Excellent answer framework:**
  - **S:** Missing who/what/when for critical mutations
  - **T:** Make actions reconstructable
  - **A:** Event/audit schema; immutability; access controls; correlation IDs
  - **R:** Used in incident/audit successfully
- **Follow-ups:** How do you prevent PII sprawl in audit logs?

### Q9. Describe leading a cross-team API or event contract change.

- **Why asked:** Platform collaboration in a tribe/squad model.
- **Competencies:** Influence without authority, versioning discipline.
- **Excellent answer framework:**
  - **S:** Breaking change risk across consumers
  - **T:** Evolve safely
  - **A:** Compatibility strategy; dual publish; consumer checklist; rollout order
  - **R:** Zero/low incident migration
- **Follow-ups:** How did you handle a lagging consumer team?

### Q10. Give an example of reducing fraud loss or abuse without killing conversion.

- **Why asked:** Product + risk balance.
- **Competencies:** Experimentation, metrics, security pragmatism.
- **Excellent answer framework:**
  - **S:** Fraud vector with false-positive pain
  - **T:** Reduce loss with acceptable friction
  - **A:** Signals; stepped-up auth; monitoring; kill switches
  - **R:** Loss ↓, conversion impact quantified
- **Follow-ups:** How did you validate model/rule changes safely?

### Q11. Tell me about a legacy modernization you executed without a big-bang rewrite.

- **Why asked:** Realistic banking change management.
- **Competencies:** Strangler patterns, risk control.
- **Excellent answer framework:**
  - **S:** Legacy constraint blocking delivery
  - **T:** Incremental extraction
  - **A:** Anti-corruption layer; dual-running; reconciliation; traffic shifting
  - **R:** Migration metrics; rollback story
- **Follow-ups:** What was the hardest data migration issue?

### Q12. Describe how you handle secrets, keys, or certificates in a service you owned.

- **Why asked:** Security hygiene.
- **Competencies:** Secure operations.
- **Excellent answer framework:**
  - **S:** Secret sprawl or expiry incident risk
  - **T:** Least privilege + rotation
  - **A:** Vault/KMS; short-lived creds; ownership; alert on expiry
  - **R:** Rotation drill success; reduced access footprint
- **Follow-ups:** How do you rotate without downtime?

### Q13. Tell me about a time you improved reconciliation or detected silent data drift.

- **Why asked:** Ledger/payment maturity.
- **Competencies:** Detective controls, data quality.
- **Excellent answer framework:**
  - **S:** Two systems diverged silently
  - **T:** Detect and repair systematically
  - **A:** Checksums/recon jobs; alerting; repair playbooks; root cause
  - **R:** Time-to-detect reduced; unmatched items trending down
- **Follow-ups:** How do you avoid recon jobs that mark everything as OK incorrectly?

### Q14. Describe a performance optimization on a hot banking path.

- **Why asked:** Scale under correctness constraints.
- **Competencies:** Measurement, careful optimization.
- **Excellent answer framework:**
  - **S:** p99 latency or timeout on critical API
  - **T:** Improve without weakening consistency
  - **A:** Profile; cache only safe data; query plans; pool tuning
  - **R:** Latency/error budget recovery with proof of correctness preserved
- **Follow-ups:** What did you refuse to cache and why?

### Q15. Tell me about influencing engineering standards across squads.

- **Why asked:** Lead-level multiplier effect.
- **Competencies:** Culture building, persuasion.
- **Excellent answer framework:**
  - **S:** Inconsistent practices causing incidents
  - **T:** Raise the floor
  - **A:** RFC/ADR; reference implementation; review guild; metrics
  - **R:** Adoption rate; incident class reduction
- **Follow-ups:** How did you handle a team that opted out?

### Q16. Give an example of designing for GDPR/privacy or data minimization.

- **Why asked:** Privacy as banking baseline.
- **Competencies:** Privacy engineering.
- **Excellent answer framework:**
  - **S:** Over-collected or over-retained PII
  - **T:** Minimize and protect
  - **A:** Field-level review; tokenization; retention jobs; access logging
  - **R:** Reduced exposure; passed review/audit
- **Follow-ups:** How do deletion requests interact with legal retention?

### Q17. Describe a time you managed delivery across time zones or multiple countries.

- **Why asked:** Global bank operating model.
- **Competencies:** Coordination, documentation quality.
- **Excellent answer framework:**
  - **S:** Distributed stakeholders/on-call
  - **T:** Continuity without heroics
  - **A:** Clear handoffs; runbooks; overlap meetings; written decisions
  - **R:** Fewer blocked days; smoother incidents
- **Follow-ups:** How do you prevent decision ping-pong?

### Q18. Tell me about a poorly defined requirement you turned into a safe design.

- **Why asked:** Ambiguity handling in regulated contexts.
- **Competencies:** Requirements discovery, risk framing.
- **Excellent answer framework:**
  - **S:** Vague "real-time payment status"
  - **T:** Make semantics precise
  - **A:** Clarified states; SLAs; failure modes; acceptance tests
  - **R:** Shared state machine; fewer production disputes
- **Follow-ups:** Which ambiguity was most expensive if left unresolved?

### Q19. Describe your approach to on-call and reducing toil.

- **Why asked:** Operational ownership.
- **Competencies:** SRE mindset, prioritization.
- **Excellent answer framework:**
  - **S:** Noisy pages / recurring wakeups
  - **T:** Actionable alerting + automation
  - **A:** SLO-based alerts; runbook fixes; eliminate class of pages
  - **R:** Page volume ↓; MTTR ↓
- **Follow-ups:** Give an example of an alert you deleted and why.

### Q20. Tell me about a time you had to dual-control or enforce segregation of duties in engineering process.

- **Why asked:** Banking control awareness.
- **Competencies:** Process design, integrity.
- **Excellent answer framework:**
  - **S:** Risky single-person production change path
  - **T:** Enforce review without freezing delivery
  - **A:** PR rules; break-glass; audited approvals; emergency path
  - **R:** Safer changes; emergency path still workable
- **Follow-ups:** How do emergencies stay audited?

### Q21. Give an example of communicating a technical risk to non-engineers.

- **Why asked:** Executive/stakeholder clarity.
- **Competencies:** Communication, risk translation.
- **Excellent answer framework:**
  - **S:** Hidden reliability/security risk
  - **T:** Get prioritization
  - **A:** Business impact framing; likelihood; mitigation options; ask
  - **R:** Decision made; risk accepted or funded
- **Follow-ups:** How did you avoid fearmongering?

### Q22. Describe a conflict within your team about architecture direction.

- **Why asked:** Facilitation skill.
- **Competencies:** Conflict resolution, technical leadership.
- **Excellent answer framework:**
  - **S:** Two credible designs
  - **T:** Decide with evidence
  - **A:** Spike; criteria matrix; ADR; revisit triggers
  - **R:** Team alignment; later validation
- **Follow-ups:** What would make you reopen the decision?

### Q23. Tell me about delivering under a hard regulatory or audit deadline.

- **Why asked:** Delivery under non-negotiable dates.
- **Competencies:** Prioritization, scope control.
- **Excellent answer framework:**
  - **S:** Audit finding / regulatory date
  - **T:** Meet bar with minimal viable control
  - **A:** Cut scope; sequential risk; evidence pack; parallel workstreams
  - **R:** Met deadline; followed with hardening
- **Follow-ups:** What technical debt did you intentionally accept?

### Q24. Describe how you measure success for a platform or shared service.

- **Why asked:** Lead/architect product thinking.
- **Competencies:** Metrics, customer-of-engineer empathy.
- **Excellent answer framework:**
  - **S:** Shared auth/payments/platform library
  - **T:** Define adoption + reliability metrics
  - **A:** SLOs; time-to-integrate; escape defects; NPS of squads
  - **R:** Decisions driven by those metrics
- **Follow-ups:** How do you handle a squad bypassing the platform?

### Q25. Tell me about a time you improved test strategy for a critical path.

- **Why asked:** Quality systems, not hero testing.
- **Competencies:** Test architecture.
- **Excellent answer framework:**
  - **S:** Escaped defect on critical flow
  - **T:** Prevent class of bugs
  - **A:** Contract tests; property tests for money; recon fixtures; chaos for retries
  - **R:** Escape rate ↓
- **Follow-ups:** Where are unit tests insufficient for banking logic?

### Q26. Give an example of handling a vendor/third-party payment dependency failure.

- **Why asked:** Integration realism.
- **Competencies:** Resilience, customer communication design.
- **Excellent answer framework:**
  - **S:** Downstream timeout/incorrect callbacks
  - **T:** Protect customers and ledger integrity
  - **A:** Timeouts; idempotent callbacks; status reconciliation; degraded mode
  - **R:** Contained impact; clear customer states
- **Follow-ups:** How do you detect "success" callbacks that never arrive?

### Q27. Describe a time you balanced speed of delivery with change-management controls.

- **Why asked:** Mature DevOps in a bank.
- **Competencies:** Process pragmatism.
- **Excellent answer framework:**
  - **S:** Slow release train vs risk
  - **T:** Faster safe path
  - **A:** Risk-tiered releases; automation of controls; smaller batches
  - **R:** Lead time ↓ without audit findings
- **Follow-ups:** What control did you refuse to weaken?

### Q28. Tell me about hiring, interviewing, or leveling engineers.

- **Why asked:** Lead contribution to org quality.
- **Competencies:** Talent bar, fairness.
- **Excellent answer framework:**
  - **S:** Hiring need / bar ambiguity
  - **T:** Consistent evaluation
  - **A:** Rubric; work-sample signals; bias checks; calibrated feedback
  - **R:** Successful hires; clearer levels
- **Follow-ups:** What signal do you weigh most for senior candidates?

### Q29. Describe a mistake you made in a design and how you corrected it.

- **Why asked:** Humility and learning speed.
- **Competencies:** Accountability, reflection.
- **Excellent answer framework:**
  - **S:** Flawed assumption (ordering, consistency, security)
  - **T:** Admit and remediate
  - **A:** Detection; fix; communication; systemic prevention
  - **R:** Stronger design review checklist
- **Follow-ups:** How did you rebuild trust?

### Q30. Why ING, and why this level (Lead/Senior/Architect)?

- **Why asked:** Motivation authenticity + level self-awareness.
- **Competencies:** Judgment, career intent.
- **Excellent answer framework:**
  - **S:** Your trajectory in enterprise/fintech systems
  - **T:** Seek regulated impact + technical leadership scope
  - **A:** Map your proof points to ING's constraints (audit, scale, platforms)
  - **R:** Clear 90-day contribution thesis
- **Follow-ups:** What would make you leave in a year? What do you need from your manager?

### Q31. Tell me about building or improving a kill switch / feature flag strategy for risky financial features.

- **Why asked:** Safe rollout culture.
- **Competencies:** Release engineering, risk control.
- **Excellent answer framework:**
  - **S:** High-risk feature near money/identity
  - **T:** Progressive exposure with instant disable
  - **A:** Flag taxonomy; owner; default-safe; monitoring hooks; disable drill
  - **R:** Incident avoided or mitigated via flag
- **Follow-ups:** How do flags interact with audit of who enabled production behavior?

### Q32. Describe collaborating with security on a threat model for a new API.

- **Why asked:** Secure-by-design leadership.
- **Competencies:** Threat modeling, prioritization.
- **Excellent answer framework:**
  - **S:** New external/partner API
  - **T:** Identify abuse cases early
  - **A:** STRIDE-like pass; authZ matrix; rate limits; logging gaps closed
  - **R:** Issues fixed pre-prod; clearer acceptance criteria
- **Follow-ups:** Which threat did you accept and why?

---

## 08 - System Design Questions

### Design 1 — Payment Processing Pipeline

**Requirements**

- Accept payment instructions from channels
- Validate, authorize, post, and notify
- Exactly-once *business effect* despite retries
- Observable statuses for customers and ops
- Peak load on payroll / promo days

**Architecture Discussion**

- API layer with idempotency keys
- Validation service + risk hooks (sync cheap checks, async deep checks)
- Durable orchestration (workflow engine or explicit state machine + outbox)
- Ledger posting service as strong-consistency boundary
- Status projection via events for read APIs
- Dead-letter + repair console for exceptions

**Tradeoffs**

- Orchestration complexity vs distributed spaghetti
- Sync fraud checks vs latency
- Choreography vs orchestration for clarity of state

**Scaling**

- Partition by payment id / account; protect hot accounts
- Horizontal scale of stateless validators
- Backpressure when ledger or scheme is slow

**Reliability**

- Timeouts, retries with jitter, compensations where applicable
- Reconciliation against scheme/gateway reports
- Runbooks for stuck states

**Security**

- mTLS; scoped tokens; field encryption for sensitive payloads
- Maker-checker for manual repairs
- Full audit of status transitions

**Production Considerations**

- SLOs per stage; synthetic payments in prod-like env
- Data retention; PII in logs banned
- Chaos drills for duplicate callbacks

### Design 2 — Fraud Detection Feed

**Requirements**

- Stream payment/login events to scorers
- Near-real-time signals without blocking all payments incorrectly
- Model/rule updates without downtime
- Explainability for challenges

**Architecture Discussion**

- Event bus from channels/payment services
- Feature pipeline; online store for device/customer velocity
- Rule/model service with versioned configs
- Decisioning API + async enrichment
- Case management integration for analysts

**Tradeoffs**

- False positives vs fraud loss
- Inline vs side-door detection
- Central model platform vs embedded rules

**Scaling**

- Consumer groups; hot-key customer features
- Sampling/aggregation for ultra-high volume events

**Reliability**

- Degrade: fail-open vs fail-closed policy by product risk tier
- Poison event isolation
- Shadow mode for new rules

**Security**

- Strict access to features containing PII
- Model theft / data exfil controls
- Audited rule changes

**Production Considerations**

- Latency budgets; feature staleness SLOs
- Replay for incident forensics
- KPI dashboards (precision/recall proxies, challenge rate)

### Design 3 — Account Ledger Consistency

**Requirements**

- Post debits/credits with double-entry integrity
- No lost updates under concurrency
- Query current balance and statement history
- Support corrections/reversals with audit

**Architecture Discussion**

- Ledger DB as system of record; append-only postings
- Account aggregate concurrency (row version / per-account queue)
- Balance materialization strategies
- Read models for statements
- Reversal as compensating postings (never silent edits)

**Tradeoffs**

- Single-account serialization vs throughput
- Synchronous balance read vs projected balance
- Shard by account vs operational complexity

**Scaling**

- Shard/partition by account id
- Separate hot-path post vs cold historical storage
- Careful caching: never cache authoritative balance without versioning

**Reliability**

- Deterministic posting IDs
- Periodic integrity checks (sum of postings == balance)
- Backup/restore with point-in-time recovery drills

**Security**

- Strict service identity to post
- Immutable audit; privileged break-glass
- Encryption and access logging

**Production Considerations**

- Migration from legacy ledger via dual posting + recon
- Clear operational procedures for manual adjustments
- Capacity planning for statement seasonality

### Design 4 — Customer Identity & Auth for Banking

**Requirements**

- Authenticate retail customers across web/mobile
- Step-up auth for high-risk actions
- Session management, device binding, revocation
- Integrate with fraud signals

**Architecture Discussion**

- IdP / OIDC; token service; session store
- Device fingerprint/risk engine hooks
- Step-up flows (OTP/push/passkey)
- Central authZ for sensitive operations
- Event trail for login anomalies

**Tradeoffs**

- UX friction vs account takeover prevention
- Centralized IdP vs embedded auth
- Token lifetime vs revocation complexity

**Scaling**

- Stateless access tokens + scalable session/revocation store
- Regional considerations for latency and residency

**Reliability**

- IdP outage degraded modes (carefully limited)
- Replay protection; OTP rate limits
- Multi-AZ session store

**Security**

- Credential stuffing defenses; MFA; secure recovery
- Anti-enumeration on login/forgot flows
- Secrets for signing keys with rotation

**Production Considerations**

- Key rotation runbooks
- Privacy of auth logs
- Regulatory expectations for strong customer authentication where applicable

### Design 5 — Open Banking / Partner API Platform

**Requirements**

- Expose account/payment APIs to partners
- Per-partner rate limits, consents, audit
- Versioning and break-glass disable per client

**Architecture Discussion**

- API gateway; consent store; partner identity (mTLS/OAuth)
- BFF/orchestration to internal domain APIs
- Idempotency enforced at edge
- Full request audit with correlation to internal posts

**Tradeoffs**

- Gateway complexity vs per-service duplication
- Coarse vs fine-grained consent scopes
- Sync vs async payment initiation APIs

**Scaling**

- Tenant-aware rate limiting; burst quotas
- Caching of consent decisions with invalidation

**Reliability**

- Partner-specific circuit breakers
- Contract testing; sandbox parity

**Security**

- Strict scopes; intent signing where required
- Continuous monitoring for anomalous partner traffic
- Data minimization in responses

**Production Considerations**

- Partner onboarding checklist; certification tests
- Status pages and clear error catalogs
- Legal retention of access logs

### Design 6 — Regulatory Reporting Extract Pipeline

**Requirements**

- Periodic extracts from operational systems
- Exactness and reproducibility for a reporting date
- Late data handling and restatements

**Architecture Discussion**

- Immutable snapshots / as-of queries
- Batch jobs with checksums
- Data quality gates before submission
- Restatement versioning

**Tradeoffs**

- Push from OLTP vs pull from warehouse
- Real-time streaming vs batch certainty
- Rebuild cost vs storage cost

**Scaling**

- Partition by book/date; parallel workers
- Incremental + periodic full reconciliations

**Reliability**

- Job orchestration with restartability
- Quarantine bad records without silent drops

**Security**

- Segregation of reporting data access
- Encryption of extracts in transit to authorities/vendors

**Production Considerations**

- Calendar/cutoff clarity across time zones
- Evidence packs for each submission
- Drill restatement process before you need it

---

## 09 - Company Preparation Checklist

- [ ] Read ING engineering/public tech material for your target market; note platform themes
- [ ] Map your last 3 production stories to: money correctness, security, audit, incident leadership
- [ ] Prepare one ADR-style explanation of a consistency decision you made
- [ ] Rehearse idempotency + outbox + reconciliation on a whiteboard (20 minutes timed)
- [ ] Draft threat model bullets for an API you owned
- [ ] Prepare metrics: latency, error budget, duplicate rate, MTTR — real numbers
- [ ] Write a 90-day plan: learn domain, stabilize, deliver one risk-reducing improvement
- [ ] List 8 questions for HM/architects (ownership boundaries, core coupling, on-call, paved roads)
- [ ] Refresh Spring transactions, Kafka consumer semantics, and authN/Z deep dive
- [ ] Practice explaining double-entry / posting invariants without pretending you built a core bank
- [ ] Prepare a "said no to product" story with a safer alternative
- [ ] Prepare a cross-team contract migration story
- [ ] Align resume bullets to banking language (reconciliation, controls, blast radius) without fabrication
- [ ] Mock system design: payments pipeline + fraud feed
- [ ] Mock behavioral: incident + compliance disagreement
- [ ] Sleep and logistics plan for multi-stage loop days

---

## 10 - How My Experience Maps

### Enterprise Experience

Map large-org delivery to ING's squad/tribe reality: governance you navigated, standards you raised, and how you shipped inside controls.

### Performance Optimization

Emphasize hot paths where correctness constrained caching; show measurement discipline and p99 outcomes.

### Legacy Modernization

Lead with strangler, dual-run, reconciliation, and rollback — the banking-compatible narrative.

### Leadership

Show standards (idempotency, logging, reviews), mentoring on domain hazards, and incident command.

### Cloud

Tie cloud work to landing zones, secrets, observability, and resilient multi-AZ design — not "we moved VMs."

### Architecture

Present ADRs, explicit consistency, and integration patterns (outbox, sagas) with failure semantics.

### Scalability

Discuss partitioning, backpressure, and hotspot accounts — scale as controlled concurrency, not infinite pods.

### Mentoring

Evidence of multiplying safe engineering practices across a team.

### Product Ownership

Frame outcomes as risk-adjusted customer value: fewer failed payments, faster safe release, clearer statuses.

---

## Interview Confidence Checklist

- [ ] I can explain idempotency vs exactly-once delivery without hand-waving
- [ ] I can design a posting flow with audit and reconciliation
- [ ] I can discuss fail-open vs fail-closed for fraud/risk
- [ ] I have 5 STAR stories mapped to ING themes
- [ ] I can critique a naive "update balance" design in under 3 minutes
- [ ] I know my leveling pitch (Senior vs Lead vs Architect)
- [ ] I can describe a production incident including prevention
- [ ] I have intelligent questions about platform and risk appetite

---

## Mock Interview Preparation Checklist

- [ ] 45-min system design: payment pipeline (peer or self-record)
- [ ] 45-min system design: ledger consistency
- [ ] 30-min deep dive: Kafka consumer + outbox on your past system
- [ ] 45-min behavioral set: Q2, Q3, Q6, Q11, Q21
- [ ] 60-min coding: idempotent API handler + tests
- [ ] Feedback captured; weak stories rewritten with metrics
- [ ] Second pass mocks after gap closure

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | ING overview + stack "why" + resume language mapping |
| 2 | Distributed transactions, idempotency, outbox drills |
| 3 | Security, audit, privacy behavioral + technical Qs |
| 4 | System design: payments + ledger |
| 5 | System design: fraud + identity |
| 6 | Leadership/behavioral battery (Q1–Q32 selective deep practice) |
| 7 | Full mock loop + gap fixes |
| 8 | Light review, questions for interviewers, rest |

---

## Estimated Preparation Time

**5–8 days** of focused prep (2–4 hours/day) if Java/Spring/system design/leadership modules are already complete. Stretch to **10 days** if you lack prior fintech/payments stories and need to carefully translate adjacent experience (billing, inventory reservations, identity platforms) into banking-relevant narratives without overclaiming.
