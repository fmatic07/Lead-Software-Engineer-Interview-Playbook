# BJAK

> Reverse-engineer how BJAK evaluates Lead/Senior engineers for SEA insurtech — quote aggregation, partner insurer integrations, pricing/rules, claims workflows, and high-availability consumer funnels where conversion and compliance collide.

---

## 01 - Company Overview

### Industry

BJAK is a **Malaysia-headquartered insurtech / digital insurance platform** serving Southeast Asia. Core brand positioning: online insurance comparison and purchase (especially motor), expanding into life, medical, and broader financial services. Markets discussed publicly include Malaysia with regional expansion ambitions (e.g., Thailand and other APAC footprints over time).

Interview implication: you are evaluated as someone who can ship **marketplace + regulated-product orchestration** — not a single-carrier core system, and not generic e-commerce.

### Products

Know the product surface enough to map designs:

| Surface | What interviewers hear when you mention it |
|---------|--------------------------------------------|
| **Quote comparison (motor / motorcycle)** | Fan-out to multiple insurers, latency, partial failure, price freshness |
| **Policy purchase / issuance** | Payment → bind → e-policy → documents; partner SLA dependency |
| **Renewals / marketplace retention** | Reminder journeys, re-quote, lapse risk |
| **Life / medical / ILP-style products** | Longer funnels, underwriting questions, agent/AI assist |
| **Claims intake / status** | FNOL, document upload, insurer handoff, customer status |
| **Partner / insurer integrations** | APIs, webhooks, batch files, sandbox vs prod parity |
| **Ops / agent tools** | Exception queues, manual overrides with audit |

Legal/regulatory nuance that shows maturity: BJAK is often an **aggregator / intermediary / digital distributor**, not always the risk carrier. Designs must respect **who owns the policy contract**, what data can be stored, and what disclosures are required.

### Engineering Culture

Expect emphasis on:

- **Speed with partner reality** — insurer APIs are uneven; your system absorbs that mess.
- **Conversion-obsessed consumer UX** — quote time and checkout drop-off are first-class metrics.
- **Pragmatic full-stack delivery** — own UI → API → integration → ops tooling.
- **Multi-market expansion mindset** — product rules differ by jurisdiction; avoid hardcoding Malaysia forever.
- **AI as assistive layer** — agents/chat for guidance; not a substitute for underwriting truth or insurer responses.

### Business Model

Commission / distribution economics on policies sold + adjacent financial services growth. Engineering impact shows up as: quote success rate, time-to-first-quote, bind rate, payment success, partner uptime handling, claims NPS/time-to-status, and cost-to-serve of exception ops.

### Scale

Consumer-scale traffic with **seasonal and regulatory spikes** (e.g., renewals, road tax / coverage cycles, campaigns). Hot paths: quote fan-out, price display, checkout/payment, document generation. Cold but critical: claims, compliance exports, partner reconciliations, audit trails.

### Global Presence

SEA-first with multi-country ambitions. Interviewers care about **local insurance product literacy**, partner onboarding velocity, and multi-tenant configuration more than multi-continent active-active — unless the role says otherwise.

### Technology Direction

Themes to discuss soberly:

- Deeper insurer API coverage and webhook maturity.
- Pricing/rules engines that can change without full redeploys.
- Claims digitization and status transparency.
- AI agents for customer guidance and ops deflection.
- Cloud-native reliability for consumer peaks.
- Selective expansion into adjacent money products — with stronger financial controls.

Do not pitch “rewrite all partners into one perfect API.” Pitch **adapter patterns, contract tests, and graceful degradation**.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

1. Treat insurer integrations as **unreliable remote systems with contracts**, not happy-path SDKs.
2. Design quote aggregation for **partial success** — one slow insurer must not kill the page.
3. Make pricing/rules **explainable and auditable** — “why this premium” must be reconstructible.
4. Own the consumer funnel end-to-end: latency, errors, retries, payment edge cases.
5. Partner with Compliance/Ops early — insurance disclosures and exception queues are product.

### Ownership

- You own quote → bind → policy document outcomes for your domain, including partner outages.
- “Done” means: metrics, alerts, runbooks, ops playbooks, and audit logs for overrides.
- You can explain where a quote request is at every hop (gateway → adapter → insurer → normalize → UI).

### Technical Leadership

- Drive adapter/facade standards so every new insurer does not invent a snowflake.
- Mentor on timeouts, bulkheads, idempotent payments, and webhook verification.
- Set the bar for PII handling (NRIC, vehicle, medical answers) in logs and support tools.

### Product Mindset

- Conversion matters — but not by showing stale prices as if they were bindable.
- Every partner is an operational product: sandbox, monitoring, on-call ownership, fallback UX.
- Feature flags for insurers/products are release features, not afterthoughts.

### Collaboration Style

- Work with Product, Growth, Ops, Compliance, Partner Management, Customer Support.
- Write sequence diagrams non-engineers can challenge (especially payment and bind).
- Escalate ambiguous insurance semantics immediately (cover vs. add-on vs. excess).

### Engineering Principles

| Principle | Interview signal |
|-----------|------------------|
| Partial failure tolerance | Quotes degrade; UI shows available insurers |
| Freshness over fiction | Expired quotes cannot silently bind |
| Adapter isolation | Partner quirks behind stable internal contracts |
| Idempotent money | Payment + bind safe under retries |
| Auditability | Who changed price rules / manual overrides |
| PII minimization | Need-to-know access; redacted logs |
| Observability | Trace across fan-out adapters |
| Config over code | Market/product rules without redeploy theater |

---

## 03 - Typical Technology Stack

Exact internal stack varies by team and era. Public hiring signals often emphasize **Node.js / TypeScript, React / Next.js, SQL + Mongo, GCP/AWS, Docker/Kubernetes**. For Lead/Architect interviews, also map your **Java/Spring** experience — many panels accept strong backend equivalence if you reason in domain terms.

### Java / Spring (where relevant)

Transactional policy/payment orchestration, batch reconciliation, rules services, and enterprise-style partner gateways are natural Spring Boot fits even if some product surfaces are Node.

**Why BJAK:** Strong typing for money/premiums (`BigDecimal`), mature transaction boundaries, security filters, and batch jobs for partner file drops / recon. Useful signal if the role touches issuance, payments, or compliance extracts.

### Node.js / TypeScript

Common for BFF, quote orchestration APIs, webhook receivers, and fast iteration on consumer products.

**Why BJAK:** High velocity on integration adapters; good fit for I/O-bound fan-out; TypeScript contracts across full stack.

### Cloud (GCP / AWS)

Managed compute, object storage for documents, managed DB, secrets, WAF, CDN for consumer assets.

**Why BJAK:** Elasticity for campaign spikes; private networking to sensitive tiers; multi-AZ HA for quote/checkout.

### Microservices

Typical splits: quote orchestration, insurer adapters, pricing/rules, identity, payments, policy documents, claims, notifications, partner webhook hub, admin/ops.

**Why BJAK:** Independent deploy of flaky partner adapters; blast-radius control — requires disciplined internal contracts and tracing.

### Databases

Relational (MySQL/PostgreSQL-class) for policies, quotes, payments, audit. Document/NoSQL (Mongo-class) for flexible questionnaire payloads / partner raw responses. Redis for rate limits, idempotency, short-lived quote caches.

**Why BJAK:** ACID for bind/payment state; flexible storage for heterogeneous insurer payloads; cache only with explicit TTL tied to quote validity.

### Messaging

Kafka/SQS/PubSub-class buses for quote async enrichment, webhook fan-in processing, document generation, notifications, claims status updates.

**Why BJAK:** Decouples checkout critical path from slow downstream (email, PDF, insurer confirm); enables replay.

### CI/CD

Pipelines with tests, dependency scanning, staged rollouts, contract tests against partner mocks.

**Why BJAK:** Partner regressions are production incidents; bad deploys break bind rates nationally.

### Kubernetes

API fleets, workers for document/claims, jobs for recon and renewal batches; HPA for quote peaks.

**Why BJAK:** Standard ops model; isolate noisy partner workers from checkout APIs.

### Infrastructure

IaC, secrets managers, feature flags per insurer/market, centralized logging, SIEM-ish alerting, object storage for policy PDFs and claim docs.

**Why BJAK:** Auditability; fast disable of a bad partner; least privilege for document access.

### Frontend

React / Next.js consumer funnels; responsive mobile-first; progressive disclosure of quotes; strong empty/error states for partial insurer failure.

**Why BJAK:** Most conversion happens on mobile web; UX must tolerate slow/partial quotes without looking broken.

### AI

Guidance agents, FAQ deflection, form assist, ops copilots, document classification assists — **advisory** to underwriting/insurer truth.

**Why BJAK:** Useful for conversion and support cost; dangerous if AI invents coverage or prices.

### Insurtech-Specific Tech Awareness

- Quote validity windows and bind eligibility checks.
- Partner adapter pattern + contract tests + sandbox promotion gates.
- Webhook signature verification, replay protection, DLQ.
- Payment idempotency + reconciliation against PSP and bind status.
- Document generation (policy schedule PDF) with template versioning.
- Rules engine for loadings, add-ons, eligibility, market-specific disclosures.

---

## 04 - Typical Interview Process

Loops vary by role seniority and location; prepare for this insurtech-shaped process:

### Stage 1 — Recruiter Screen

**Purpose:** Motivation for insurtech/fintech, level, logistics, comfort with partner-integration chaos and consumer metrics.

**Evaluation Criteria:** Clear narrative; marketplace/integration relevance; communication; ownership signals.

**Preparation Tips:** 90-second story emphasizing aggregation systems, unreliable partners, conversion funnels, payments, or regulated workflows. Map adjacent experience (payments, marketplace, B2B integrations).

**Common Mistakes:** Pure LeetCode identity with no product sense; treating insurance as “just forms”; no awareness that insurers are external systems of record.

### Stage 2 — Technical Interview

**Purpose:** Backend depth (Node and/or Java), API design, data modeling, debugging integrations.

**Evaluation Criteria:** Timeout/bulkhead thinking; state machines; security/PII hygiene; testing strategy for adapters.

**Preparation Tips:** Refresh idempotent payments, quote state machines, webhook at-least-once handling, cache TTLs vs quote expiry.

**Common Mistakes:** Ignoring partial failures; logging NRIC/medical answers; “we’ll just wait for all insurers.”

### Stage 3 — Coding Assessment

**Purpose:** Practical coding — parsing heterogeneous partner responses, ranking quotes, state transitions, concurrency around payment/bind — live or take-home.

**Evaluation Criteria:** Edge cases, clarity, tests, error modeling, readability under time pressure.

**Preparation Tips:** Practice: normalize N partner quote payloads → sort by price/coverage → handle timeouts → idempotent checkout confirm.

**Common Mistakes:** Happy-path only; float money; no tests for malformed partner JSON; unclear API errors for UI.

### Stage 4 — System Design

**Purpose:** Quote aggregation, issuance, claims, webhook hub, or pricing engine at consumer scale.

**Evaluation Criteria:** Requirements clarity; failure modes; consistency of bind; ops/compliance; SEA multi-market awareness.

**Preparation Tips:** Drill Section 08. Always draw partner boundary and customer-visible states.

**Common Mistakes:** Designing a monolith “insurance core”; no quote expiry; no payment recon; ignoring webhook retries.

### Stage 5 — Leadership Interview

**Purpose:** Ownership in partner outages, mentoring, cross-team influence with Ops/Compliance/Growth.

**Evaluation Criteria:** Calm under conversion pressure; accountability; ability to say no to unsafe shortcuts.

**Preparation Tips:** Incident STAR with partner outage → customer messaging → bind protection → postmortem standards.

**Common Mistakes:** Blaming “the insurer” with no mitigation; hero culture; no personal ownership of ops tooling.

### Stage 6 — Hiring Manager

**Purpose:** Team mission fit (quotes vs payments vs claims vs platform), roadmap, on-call, multi-market plans.

**Evaluation Criteria:** Judgment, collaboration with non-eng, growth trajectory, pragmatic architecture.

**Preparation Tips:** Ask about partner ownership model, quote SLOs, payment PSP, rules-engine maturity, AI boundaries.

**Common Mistakes:** Not asking who owns insurer adapter quality or exception queues.

### Stage 7 — Final Interview

**Purpose:** Bar raise, culture, sometimes security/compliance deep dive or founder/leadership values.

**Evaluation Criteria:** Integrity, senior judgment, consistency of stories, appetite for ambiguous partner work.

**Preparation Tips:** Prepare questions on market expansion strategy, reliability goals, and how AI is governed.

**Common Mistakes:** Inconsistent PII posture across interviews; overselling rewrite fantasies.

### Stage 8 — Offer

**Purpose:** Leveling, scope, hybrid/remote expectations, ownership of domain.

**Evaluation Criteria:** Mutual clarity on technical authority and first-6-month outcomes.

**Preparation Tips:** Confirm on-call, partner rotation, success metrics (quote success, bind rate, SEV ownership).

**Common Mistakes:** Accepting Lead title without clear say on architecture standards for adapters/rules.

---

## 05 - Technical Focus Areas

### Insurtech / Fintech SEA (Aggregator Model)

- Distributor vs carrier boundaries; who is system of record for policy.
- Market-specific product rules, disclosures, cooling-off, tax/stamp duty nuances (speak in principles if not Malaysia-expert).
- Partner Management + Engineering co-ownership of go-live.

### Quote Aggregation from Insurers

- Parallel fan-out with deadlines; hedging slow partners.
- Normalization of cover, excess, add-ons, premiums, currency.
- Ranking and presentation fairness (not only cheapest).
- Caching vs freshness; quote ID lifecycle.

### Pricing Engines & Rules

- Rating inputs: vehicle, driver, claims history proxies, add-ons, promo codes.
- Deterministic rules + partner-returned premiums; explainability.
- Versioned rule sets; shadow evaluation before enforce.
- Avoid embedding market law in scattered `if` statements.

### Partner Integrations

- REST/SOAP/file/SFTP heterogeneity behind adapters.
- Contract tests; sandbox promotion checklist.
- Circuit breakers, retries with jitter, error taxonomy.
- Partner scorecards: latency, success %, bind confirm lag.

### Policy Issuance Workflow

- State machine: `QUOTED → PAYMENT_PENDING → PAID → BINDING → ISSUED → DOCUMENTS_READY` (+ failure/cancel).
- Idempotent bind calls; reconciliation when insurer ack is delayed.
- Document generation and delivery; e-policy authenticity.

### Claims Workflows

- FNOL intake; photo/document upload; virus scan; insurer handoff.
- Customer-visible status vs internal ops status.
- Fraud indicators (duplicate claims, inconsistent photos) as signals, not silent denies without process.

### Compliance & Audit

- Consent, purpose limitation, retention for identity/vehicle/medical data.
- Immutable audit for price overrides and manual policy fixes.
- Change management for production rule changes.

### High-Availability Consumer Apps

- Multi-AZ APIs; CDN for static; graceful degradation when partners fail.
- Timeout budgets end-to-end; bulkheads per insurer.
- Campaign load tests on quote + checkout paths.

### Payment Flows

- PSP integration; 3DS/step-up; webhook confirmation; refunds/voids.
- Never mark ISSUED on client assertion alone.
- Recon: PSP settlements vs bind vs finance.

### Idempotency, Webhooks, Exception Ops

- At-least-once partner callbacks; dedupe keys; DLQ + replay tooling.
- Human-in-the-loop queues for stuck binds with dual control where needed.

---

## 06 - Leadership Focus

### Ownership

Own conversion and correctness outcomes: quote failures, stuck payments, wrong documents, partner SEVs.

### Mentoring

Teach juniors adapter discipline, timeout thinking, and “never trust partner happy path.” Review PRs for PII leaks.

### Decision Making

Prefer boring, observable integration patterns. Innovate on UX and rules velocity, not on inventing coverage.

### Cross-team Collaboration

Co-design with Growth on funnel experiments; with Compliance on disclosures; with Ops on exception SLAs; with Partner team on API readiness.

### Incident Response

SEV tied to quote/checkout/bind impact. Disable bad insurer quickly; communicate accurately; protect customers from double charge; recon before “all clear.”

### Architecture Discussions

Challenge shared-DB coupling between adapters. Demand internal stable DTOs, tracing, and contract tests in RFCs.

### Technical Debt

Prioritize debt that causes silent wrong prices, stuck ISSUED states, or unmaintainable partner snowflakes. Quantify ops hours and lost binds.

### Engineering Culture

Blameless postmortems with partner scorecard improvements. Celebrate prevented bad binds and clean recons, not only new insurer logos on the homepage.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you owned an integration with an unreliable external partner.

- **Why asked:** BJAK’s core engineering reality.
- **Competencies:** Ownership, resilience design.
- **Framework:** Partner failure modes → timeouts/bulkheads → customer UX → metrics → durable adapter standards.
- **Follow-ups:** How did you decide degrade vs hard-fail? Who owned the relationship escalation?

### Q2. Describe designing a fan-out system where partial success is acceptable.

- **Why asked:** Quote aggregation signal.
- **Competencies:** Distributed systems, product sense.
- **Framework:** Deadline budget → parallel calls → normalize → rank → show partial → retry strategy for missing.
- **Follow-ups:** How did you prevent UI flicker/reordering that destroys trust?

### Q3. Tell me about a payment + fulfillment workflow you made idempotent.

- **Why asked:** Bind after pay is money-critical.
- **Competencies:** Correctness, API design.
- **Framework:** Idempotency keys → PSP webhooks → state machine → recon → customer messaging for UNKNOWN.
- **Follow-ups:** Did you ever double-charge or double-issue? How prevented?

### Q4. Give an example of a pricing or rules change that needed auditability.

- **Why asked:** Explainable premiums / compliance.
- **Competencies:** Governance, design.
- **Framework:** Versioned rules → shadow → canary → evidence of who/what/why → rollback.
- **Follow-ups:** How do support agents explain a premium to a customer?

### Q5. Tell me about improving conversion without weakening controls.

- **Why asked:** Growth vs compliance tension.
- **Competencies:** Product mindset, judgment.
- **Framework:** Funnel analytics → friction hypothesis → experiment → integrity checks → bind rate + complaint rate.
- **Follow-ups:** What “clever” growth idea did you reject?

### Q6. Describe an incident where customers saw wrong prices or stale quotes.

- **Why asked:** Freshness integrity.
- **Competencies:** Incident leadership, caching judgment.
- **Framework:** Detect → stop bind on stale → invalidate cache → customer remediation → TTL/version fix.
- **Follow-ups:** How do you define quote expiry across partners with different validity?

### Q7. Tell me about leading through a major partner outage during peak traffic.

- **Why asked:** Calm ownership under conversion loss.
- **Competencies:** Crisis leadership, communication.
- **Framework:** Disable partner → preserve checkout for others → status page/macros → exec updates → postmortem with Partner Mgmt.
- **Follow-ups:** What automated kill switch existed vs manual?

### Q8. Share mentoring an engineer who treated partner responses as trusted input.

- **Why asked:** Technical leadership.
- **Competencies:** Mentoring, security.
- **Framework:** Concrete bug (injection/overflow/wrong map) → validation schema → contract tests → review checklist.
- **Follow-ups:** How did you encode that into onboarding?

### Q9. Tell me about building or hardening a webhook receiver.

- **Why asked:** Partner async reality.
- **Competencies:** Integration design, security.
- **Framework:** Signature verify → dedupe → async process → DLQ → replay tooling → partner retry behavior.
- **Follow-ups:** How do you handle out-of-order events?

### Q10. Describe a claims or document-upload pipeline you influenced.

- **Why asked:** Claims digitization maturity.
- **Competencies:** Privacy, reliability.
- **Framework:** Upload → scan → store encrypted → status machine → insurer handoff → customer tracking.
- **Follow-ups:** Retention policy? Insider access controls?

### Q11. Tell me about a time you said no to launching an insurer integration.

- **Why asked:** Quality bar under commercial pressure.
- **Competencies:** Integrity, influence.
- **Framework:** Readiness gaps (sandbox parity, error codes, runbooks) → risk → phased alternative → decision record.
- **Follow-ups:** What minimum bar did you institutionalize afterward?

### Q12. Give an example of cross-team delivery (Growth + Ops + Engineering).

- **Why asked:** BJAK collaboration style.
- **Competencies:** Coordination.
- **Framework:** Shared funnel metrics → experiment design → ops capacity → launch gates → results.
- **Follow-ups:** What metric disagreement did you resolve?

### Q13. Tell me about handling PII (identity/vehicle/medical) in logs and support tools.

- **Why asked:** Compliance maturity.
- **Competencies:** Security, privacy.
- **Framework:** Data classification → redaction → JIT access → audit → training.
- **Follow-ups:** How do you debug production without raw NRIC in logs?

### Q14. Describe a performance incident on a quote path.

- **Why asked:** Consumer HA expectations.
- **Competencies:** Performance, capacity.
- **Framework:** Symptoms → slow partner vs self → isolation → caching/pooling → load test → SLO.
- **Follow-ups:** Did head-of-line blocking affect healthy insurers?

### Q15. Tell me about reconciling payments with policy issuance states.

- **Why asked:** Money/ops correctness.
- **Competencies:** Financial controls.
- **Framework:** Break detection → classification → auto-resolve vs queue → customer fix → prevention.
- **Follow-ups:** Who owns unresolved breaks after 24h?

### Q16. Share designing multi-market configuration (not hardcoded country logic).

- **Why asked:** SEA expansion signal.
- **Competencies:** Architecture, foresight.
- **Framework:** Tenant/market config → product catalog → localization → compliance hooks → rollout.
- **Follow-ups:** What still leaked as code branches?

### Q17. Tell me about improving observability across many partner adapters.

- **Why asked:** Debuggability at fan-out.
- **Competencies:** Operability.
- **Framework:** Trace IDs → partner latency histograms → error taxonomy → alert routing → MTTD drop.
- **Follow-ups:** How do you alert without paging on every partner blip?

### Q18. Describe a migration of quote or policy schema under live traffic.

- **Why asked:** Upgrade safety.
- **Competencies:** Migration discipline.
- **Framework:** Expand/contract → dual-read → validators → cohort → rollback.
- **Follow-ups:** How did historical policies remain readable?

### Q19. Tell me about using feature flags for products/insurers.

- **Why asked:** Safe rollout / kill switches.
- **Competencies:** Release engineering.
- **Framework:** Flag design → defaults safe → ownership → incident use → cleanup.
- **Follow-ups:** Audit trail on who enabled a risky insurer?

### Q20. Give an example of reducing ops exception volume with engineering.

- **Why asked:** Cost-to-serve leadership.
- **Competencies:** Product ops empathy.
- **Framework:** Top exception reasons → automate/self-serve → guardrails → volume/NPS impact.
- **Follow-ups:** Which exceptions must remain human and why?

### Q21. Tell me about a disagreement with product on showing incomplete quotes.

- **Why asked:** Trust vs speed.
- **Competencies:** Stakeholder management.
- **Framework:** Evidence → options (wait vs partial vs skeleton) → experiment → decision → outcome.
- **Follow-ups:** How did you measure “trust damage”?

### Q22. Describe introducing contract tests for partner APIs.

- **Why asked:** Platform quality bar.
- **Competencies:** Quality leadership.
- **Framework:** Broken prod incident → Pact/mocks → CI gate → partner change detection.
- **Follow-ups:** How do you handle partners who change silently?

### Q23. Tell me about coaching a team through noisy on-call.

- **Why asked:** Sustainable operations.
- **Competencies:** People leadership, SRE basics.
- **Framework:** Alert audit → SLO-based pages → runbooks → rotation health.
- **Follow-ups:** What % of pages became actionable?

### Q24. Share an architectural decision you later reversed (e.g., shared partner DB).

- **Why asked:** Humility and learning.
- **Competencies:** Reflection.
- **Framework:** Original bet → failure signal → migration → lesson in standards.
- **Follow-ups:** What early indicator did you miss?

### Q25. Tell me about delivering bad news on a go-live delay for compliance reasons.

- **Why asked:** Integrity under commercial pressure.
- **Competencies:** Communication, ethics.
- **Framework:** Early warning → options → recommend → protect customer/legal → rebuild trust.
- **Follow-ups:** How did sales/partners react?

### Q26. Describe working with Customer Support during a widespread checkout issue.

- **Why asked:** Customer trust operations.
- **Competencies:** Empathy, communication.
- **Framework:** Accurate macros → lookup tooling → escalation → fix → proactive outreach.
- **Follow-ups:** What Support capability did you ship afterward?

### Q27. Tell me about applying AI in a customer or ops workflow safely.

- **Why asked:** BJAK AI direction without hallucination risk.
- **Competencies:** Judgment, AI literacy.
- **Framework:** Use case → grounding/sources → human handoff → eval metrics → blocked behaviors (inventing coverage).
- **Follow-ups:** How do you measure harmful answers?

### Q28. Give an example of influencing standards across multiple squads.

- **Why asked:** Lead/Staff multi-team impact.
- **Competencies:** Influence without authority.
- **Framework:** Shared incident theme → RFC → reference adapter → adoption metrics.
- **Follow-ups:** Who resisted and how did you address it?

### Q29. Tell me about hiring for an integration-heavy team — what signals matter?

- **Why asked:** Lead hiring bar.
- **Competencies:** Talent selection.
- **Framework:** Probe failure-mode thinking → past ownership → skepticism of external data → teachability.
- **Follow-ups:** Automatic no-hire signal?

### Q30. Describe prioritizing tech debt against growth OKRs.

- **Why asked:** Lead judgment.
- **Competencies:** Prioritization.
- **Framework:** Risk-rank debt → tie to bind loss/ops cost → negotiate capacity → show before/after.
- **Follow-ups:** What debt did you consciously defer?

### Q31. Tell me about a time retry logic made things worse.

- **Why asked:** Mature distributed-systems scars.
- **Competencies:** Debugging, humility.
- **Framework:** Amplification symptom → backoff/jitter/idempotency → partner rate limits → fix → chaos test.
- **Follow-ups:** How do you test retry storms?

### Q32. Share improving document generation / e-policy delivery reliability.

- **Why asked:** Post-bind customer experience.
- **Competencies:** Reliability, UX.
- **Framework:** Async pipeline → template versioning → failure replay → customer retry → metrics on time-to-PDF.
- **Follow-ups:** How do you handle template legal changes mid-flight?

### Q33. Tell me about securing admin overrides (manual discount, force-bind, data fix).

- **Why asked:** Insider risk + audit.
- **Competencies:** Security, compliance.
- **Framework:** RBAC → dual control → reason codes → immutable audit → periodic access review.
- **Follow-ups:** Can overrides bypass payment? Should they?

---

## 08 - System Design Questions

### Scenario A — Quote Aggregation from Insurers

**Requirements:**  
Customer submits vehicle/driver details; system requests quotes from N insurers within a tight UX budget (e.g., 3–8s first paint of results). Show partial results if some insurers are slow. Quotes expire. Must normalize heterogeneous responses. High concurrency during campaigns. Audit who saw what price.

**Architecture Discussion:**  
API Gateway/BFF → Quote Orchestrator creates `quote_session` → parallel adapter calls with per-partner timeout and bulkhead thread/semaphore pools → normalize to internal `Offer` DTO → rank → store offers with TTL → push/poll to UI. Optional async “late offer” channel. Cache rating inputs carefully; never cache bindable offers beyond validity without revalidation. Circuit breaker per insurer. Partner raw payload stored encrypted for dispute/debug with strict access.

**Tradeoffs:**  
Sync wait-for-all (simpler, worse UX) vs deadline partial (complex ordering). Edge-cache quotes (fast, stale risk) vs always-live partner calls (fresh, expensive). Node fan-out vs reactive Java — either fine if isolation is real.

**Scaling:**  
Horizontal orchestrators; isolate adapters as separate services if noisy; rate-limit per partner; queue overflow for non-interactive requotes; autoscale on concurrent sessions.

**Reliability:**  
Hedged requests for critical partners; fallback messaging; degrade ranking when add-on data missing; synthetic canaries per insurer.

**Security:**  
AuthN for customer session; PII minimization in logs; encrypt raw partner payloads; WAF on public quote APIs; abuse rate limits (quote scraping).

**Production Considerations:**  
Metrics: time-to-first-offer, offers-per-session, partner p95/p99, error taxonomy, bindable%. Runbooks to disable insurer. Care tools to inspect session without leaking secrets.

---

### Scenario B — Policy Issuance Workflow

**Requirements:**  
Customer selects offer, pays, policy binds with insurer, e-policy issued. Must be idempotent under double-submit and webhook retries. Handle delayed insurer acknowledgment. Support refunds if bind fails after payment. Clear customer-visible states.

**Architecture Discussion:**  
Checkout service creates `order` + idempotency key → PSP payment intent → on `PAYMENT_SUCCEEDED` event, Issuance Orchestrator transitions `PAID → BINDING` → insurer bind adapter → on success `ISSUED` → document service generates PDF → notify. Outbox pattern from state transitions. Recon job: PSP vs order vs insurer policy number. Manual ops queue for `BINDING` stuck beyond SLA with audited actions.

**Tradeoffs:**  
Bind-before-capture vs capture-before-bind (commercial + risk). Sync bind in request (simple, brittle) vs async worker (resilient, harder UX). Store policy of record locally vs always fetch insurer (latency vs consistency).

**Scaling:**  
Partition workers by insurer; priority queues for paid-not-issued; horizontal document workers; DB indexes on state+updated_at for sweepers.

**Reliability:**  
Exactly-once *effects* via idempotent bind keys; compensating refund saga; poison-message DLQ; clock-skew tolerant webhook verify.

**Security:**  
PCI scope reduction via hosted fields/PSP; signed webhooks; least privilege on issuance admin; audit every force-issue.

**Production Considerations:**  
Customer messaging for pending bind; Support macros mapped to states; finance recon dashboard; kill switch stop new binds per insurer while draining queue.

---

### Scenario C — Claims Intake

**Requirements:**  
Customer files FNOL: incident details, photos, documents. Validate completeness, store securely, notify insurer/ops, show status timeline. Handle large uploads on mobile networks. Detect obvious abuse signals. Retention and access controls required.

**Architecture Discussion:**  
Claims API → create claim draft → pre-signed uploads to object storage → async virus scan + image validation workers → completeness rules → submit → adapter to insurer claims API or ops case → status projection for customer. Event log for timeline. Optional ML assist for damage categorization (advisory). Deduplicate claim submissions with idempotency.

**Tradeoffs:**  
Direct insurer API vs human ops first (speed vs partner maturity). Sync vs async virus scan before accept. Store originals forever vs retention windows.

**Scaling:**  
Separate upload bandwidth from API CPU; CDN not for private docs; queue scan workers; shard object prefixes; throttle abusive upload storms.

**Reliability:**  
Resume uploads; retry insurer notify; DLQ; customer can continue when insurer API down (park in `SUBMITTED_INTERNAL`).

**Security:**  
Encrypted bucket; short-lived URLs; malware scan; RBAC for adjusters; watermark/download audit; no public ACLs.

**Production Considerations:**  
SLA dashboards time-to-first-response; Support tools; GDPR/PDPA deletion workflows; fraud review queue hooks.

---

### Scenario D — Partner Webhook Hub

**Requirements:**  
Many insurers send async callbacks (bind confirm, policy cancel, claims status) with different auth schemes and payloads. Must verify, dedupe, normalize, route to internal consumers, allow replay, and survive partner retry storms.

**Architecture Discussion:**  
Edge webhook receivers (per partner or generic with plugin verifiers) → verify signature/IP allowlist → write durable inbox event (unique event_id) → ack quickly → async router maps to domain topics → consumers update issuance/claims. Admin UI for replay. Schema registry for normalized events. Quarantine unknown payloads.

**Tradeoffs:**  
Per-partner endpoints (clear isolation) vs single endpoint (ops simplicity). Sync processing (partner timeouts) vs inbox-first (correct). Shared Kafka vs DB inbox.

**Scaling:**  
Autoscale receivers; partition by partner_id; backpressure when consumers lag; rate-limit abusive partners without dropping forever (buffer).

**Reliability:**  
At-least-once to consumers with idempotent handlers; poison quarantine; lag alerts; dead-letter replay drills.

**Security:**  
Rotate secrets; reject unsigned; prevent SSRF if partner URLs used outbound; separate ingress from internal network.

**Production Considerations:**  
Partner scorecards on duplicate rates; runbooks for secret rotation; contract tests simulating retries/out-of-order.

---

### Scenario E — Pricing / Rules Engine

**Requirements:**  
Compute eligibility, loadings, promo discounts, and display premiums using versioned rules. Support shadow mode. Explain decisions. Allow market/product config without redeploying all services. High read QPS during quotes; strong audit on publishes.

**Architecture Discussion:**  
Rules Authoring Service (draft/approve/publish) → versioned rule bundles in object store/DB → Pricing Service loads bundles to memory with hot reload → evaluation API returns premium breakdown + rule trace IDs. Quote orchestrator calls pricing for internal adjustments and merges with insurer-returned premiums. Approval workflow with dual control for production publish. Experiment flags for cohorts.

**Tradeoffs:**  
DMN/rules engine vs coded strategies vs partner-only premiums. Real-time evaluate vs precompute tables. Central pricing service vs library embedded in orchestrator (consistency vs latency).

**Scaling:**  
Read replicas of bundles; local caches; avoid remote call per tiny rule if latency tight — embed + checksum verify. Separate authoring cluster from serving.

**Reliability:**  
Bundle checksum; canary publish; instant rollback to previous version; evaluation timeouts; default fail-closed on eligibility when rules missing.

**Security:**  
RBAC on publish; immutable audit; prevent unverified bundles; protect promo abuse with velocity checks.

**Production Considerations:**  
Diff UI between versions; “why this price” Support tool; metrics on shadow disagreements before enforce.

---

### Scenario F — High-Availability Checkout During Campaign

**Requirements:**  
Flash campaign drives 10× quote/checkout. Maintain availability; protect databases and partners; fair UX; no thundering herd retries; accurate sell-out/eligibility if limited promos.

**Architecture Discussion:**  
Edge rate limits + bot detection → cache catalog/static → quote orchestrator with partner budgets → checkout queue or lease for limited promos → payment → issuance async. Load shed noncritical features (recs, heavy AI). Read-only degrade modes carefully (cannot claim bindable if not). Autoscaling + pre-warmed pools. Chaos: kill one insurer during test.

**Tradeoffs:**  
Hard queue (fair, slower) vs reject overload (harsh, protects core). Aggressive caching vs wrong promo eligibility.

**Scaling:**  
HPA; connection pools; partner concurrency caps; separate clusters for quote vs checkout; CDN; DB proxy.

**Reliability:**  
Budgeted retries; idempotent checkout; circuit breakers; synthetic journey monitoring.

**Security:**  
Campaign fraud (card testing); promo code farming; WAF; step-up on risky pays.

**Production Considerations:**  
War-room dashboards; feature freeze window; clear customer messaging; post-campaign recon surge staffing.

---

## 09 - Company Preparation Checklist

- [ ] Can explain BJAK as aggregator/distributor vs carrier in one minute
- [ ] Can name primary products: motor quote, issuance, renewals, claims, expanding lines
- [ ] Can discuss SEA multi-market implications (config, compliance, partners)
- [ ] Can whiteboard quote fan-out with partial failure and deadlines
- [ ] Can whiteboard pay → bind → document with idempotency and recon
- [ ] Can explain webhook inbox, dedupe, and replay
- [ ] Can discuss pricing/rules versioning and audit
- [ ] Can articulate PII handling for NRIC/vehicle/medical answers
- [ ] Can map personal stories to partner outages, conversion, payments, mentoring
- [ ] Can ask sharp HM questions on adapter ownership and quote SLOs
- [ ] Reviewed recent BJAK news (expansion, product launches) without marketing fluff
- [ ] Prepared stack translation: your Java/Spring ↔ their Node/TS realities

---

## 10 - How My Experience Maps

Fill before interviews (replace bullets with your metrics):

| BJAK need | My evidence | Metric / artifact |
|-----------|-------------|-------------------|
| Unreliable partner integrations | | |
| Fan-out / aggregation systems | | |
| Payment + fulfillment state machines | | |
| Rules/pricing or config-driven product | | |
| High-traffic consumer APIs | | |
| Webhook / event hubs | | |
| PII / compliance discipline | | |
| Ops exception reduction | | |
| Mentoring + engineering standards | | |
| Incident leadership | | |

**Gap plan:** For any empty row, either build a concrete STAR story from adjacent domains (marketplace, payments, telecom partners) or practice a design drill in Section 08 until fluent.

---

## Interview Confidence Checklist

- [ ] 90-second BJAK-specific pitch ready
- [ ] 8+ STAR stories mapped to Q1–Q33 themes
- [ ] Can draw Scenario A–F without notes
- [ ] Can defend timeout/bulkhead choices under pushback
- [ ] Can explain money safety on checkout without hand-waving
- [ ] Can discuss AI assist boundaries calmly
- [ ] Questions ready for recruiter, HM, and final
- [ ] Leveling narrative: Senior vs Lead vs Architect scope claimed honestly

---

## Mock Interview Preparation Checklist

- [ ] Mock recruiter screen (20 min)
- [ ] Mock technical deep dive: adapters + idempotency (45–60 min)
- [ ] Timed coding: normalize quotes + checkout state machine (60–90 min)
- [ ] Mock system design: pick 2 of Scenarios A–F (60 min each)
- [ ] Mock leadership: partner outage + mentoring (45 min)
- [ ] Record one design; critique clarity of partner boundary
- [ ] Feedback logged; weak areas scheduled

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | Company overview + engineering principles; write pitch |
| 2 | Quote aggregation + HA consumer paths; draw Scenario A & F |
| 3 | Issuance + payments + recon; Scenario B |
| 4 | Webhooks + claims + PII; Scenarios C & D |
| 5 | Pricing/rules + leadership stories; Scenario E |
| 6 | Behavioral battery (Q1–Q33) aloud; fill Section 10 |
| 7 | Full mock loop; revise gaps; checklist pass |

---

## Estimated Preparation Time

| Track | Hours |
|-------|------:|
| Company + stack translation | 3–4 |
| Technical focus + designs (A–F) | 10–12 |
| Behavioral story binding | 6–8 |
| Coding katas (quote/payment) | 4–6 |
| Mocks + revision | 6–8 |
| **Total** | **~30–38 hours** |

For a compressed timeline (5 days): prioritize Scenarios A/B/D, payment idempotency, partner outage STAR stories, and Section 10 mapping.
