# Maya

> Reverse-engineer how Maya evaluates Lead/Senior engineers for Philippine fintech — wallets, payments rails, digital banking, KYC/fraud, and money-moving systems where correctness is non-negotiable.

---

## 01 - Company Overview

### Industry

Maya is a **Philippine fintech / digital financial services** company spanning e-wallet, payments acquiring, and digital banking. It operates in a BSP-regulated environment. Predecessor brand: PayMaya. Parent technology context: Voyager Innovations ecosystem.

Interview implication: you are evaluated as someone who can ship in **regulated money systems** — not generic CRUD microservices.

### Products

Know the product surface enough to map designs:

| Surface | What interviewers hear when you mention it |
|---------|--------------------------------------------|
| **Maya app / wallet** | Cash-in, P2P, QR, bills, cards adjacency |
| **Maya Business / merchant** | Acquiring, QRPh, checkout, settlements |
| **Maya Bank (digital bank)** | Savings, loans, deposits — banking controls |
| **Developer platform / APIs** | Partner integrations, OAuth, signed requests, transfers |
| **Rails adjacency** | InstaPay, PESONet, QRPh, card networks, cash agents |

Legal nuance that shows maturity: wallet/payments entity vs. bank entity are distinct and regulated — designs must respect product and compliance boundaries.

### Engineering Culture

Expect emphasis on:

- **Safety over speed theater** — move fast with kill switches, limits, and reconciliations.
- **Mobile-scale reality** — millions of users, spiky campaigns, uneven network conditions.
- **Security-first defaults** — threat modeling is normal conversation, not a special week.
- **Cross-functional urgency** — Risk, Compliance, Fraud, Ops, Customer Care are first-class stakeholders.
- **Pragmatic microservice architecture** — services around money movement, ledger, KYC, notifications, etc., with strong contracts.

### Business Model

Consumer wallet/banking growth + merchant acquiring + credit/savings products. Engineering impact shows up as: authorization success rate, fraud loss rate, settlement accuracy, uptime during payday/campaigns, KYC conversion funnel, partner API reliability.

### Scale

National consumer scale in the Philippines: high mobile traffic, QR peaks, payday and promo spikes, partner API fan-out. Hot paths are **balance reads, transfers, payments, login/KYC**. Cold paths still matter: statements, AML reports, dispute evidence packs.

### Global Presence

Primarily Philippines-focused with partner/ecosystem integrations. Interviewers care about **local rails literacy** (InstaPay/PESONet/QRPh) and regional operational constraints more than multi-continent active-active — unless the role says otherwise.

### Technology Direction

Themes to discuss soberly:

- Unified money app experience (wallet + bank products).
- Real-time payments and merchant growth.
- Stronger fraud/risk automation.
- Developer/partner platform expansion.
- Cloud-native reliability, observability, and secure SDLC.
- Selective AI for fraud/support — never as a substitute for ledger truth.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

1. Treat money movement as a **state machine with invariants**, not a happy-path API.
2. Design for **idempotency, reconciliation, and replay** from day one.
3. Partner with Risk/Fraud/Compliance early — not after code complete.
4. Obsess over mobile client realities: retries, timeouts, duplicate taps.
5. Communicate clearly during incidents; customer funds anxiety is the clock.

### Ownership

- You own balances/transactions in your domain including break-glass tools and recon jobs.
- “Done” means: limits enforced, audit trail, metrics, runbooks, and Care playbooks.
- You can explain where money is at every step of a transfer.

### Technical Leadership

- Drive designs that make double-spend and lost-update failures difficult.
- Mentor on distributed systems pitfalls specific to payments.
- Set the bar for secure coding reviews (authN/Z, secrets, PII, injection, SSRF to internal ledger).

### Product Mindset

- Conversion and UX matter — but not by weakening KYC or fraud controls blindly.
- Every new rail/partner is an operational product: monitoring, SLAs, fallbacks.
- Feature flags and limit switches are product features.

### Collaboration Style

- Work with Risk, Compliance, AML, Fraud, Treasury/Ops, Care.
- Write designs non-engineers can challenge (funds flow diagrams).
- Escalate ambiguity in money semantics immediately.

### Engineering Principles

| Principle | Interview signal |
|-----------|------------------|
| Ledger integrity | Double-entry or equivalent invariants; no silent balance edits |
| Idempotency | Keys on all money APIs; safe client retries |
| Reconciliation | Daily/continuous recon against rails and partners |
| Least privilege | Service identities, PCI-ish card data boundaries |
| Fail closed on risk | Prefer decline/limit over unbounded accept when uncertain |
| Observability | Trace_id across wallet → ledger → rail → webhook |
| Auditability | Who/what/why for financial and KYC decisions |

---

## 03 - Typical Technology Stack

Exact internal stack varies by team. Interview with **fintech-credible defaults** and ask which apply.

### Java

Common enterprise choice for core transactional services and partner integrations.

**Why Maya:** Strong typing and mature concurrency for ledger-adjacent services; large hiring market; Spring ecosystem for security and transactions.

### Spring Boot

Payment APIs, orchestration services, admin/ops tools, batch statement/AML jobs.

**Why Maya:** Rapid service delivery with battle-tested security filters, validation, metrics, and transactional boundaries.

### Cloud

Cloud-hosted microservices (AWS-class patterns common in PH fintech): VPC, managed DB, KMS, WAF, private networking to sensitive tiers.

**Why Maya:** Elasticity for campaigns; security controls; multi-AZ HA for mobile backends.

### Microservices

Services typically split by capability: identity/KYC, wallet/ledger, payments orchestration, QR, notifications, fraud decisioning, merchant settlement, statements.

**Why Maya:** Independent scaling of hot paths; blast-radius control — but requires disciplined contracts and distributed tracing.

### Databases

Relational primary for accounts/ledger/journal (Postgres/MySQL-class). Redis for sessions, limits, idempotency, hot balances cache (with care). Warehouse for analytics/AML/features.

**Why Maya:** ACID for money; Redis only as acceleration with explicit invalidation/recon rules — never as sole source of truth for balances.

### Messaging

Kafka/Rabbit/SQS-class buses for payment events, notifications, fraud async features, webhook delivery, statement generation.

**Why Maya:** Decouples authorization path from downstream side effects; enables replay and consumer scaling.

### CI/CD

Mandatory pipelines: SAST/DAST hooks, dependency scanning, signed artifacts, staged rollouts, automated regression on payment flows.

**Why Maya:** Secure delivery is part of regulated engineering; bad deploys become fund incidents.

### Kubernetes

Container orchestration for API fleets and workers; HPA for traffic spikes; job controllers for recon/statements.

**Why Maya:** Standard ops model for many fintech platforms; supports rapid scale on promo days.

### Infrastructure

IaC, secrets managers, private service mesh/network policies, centralized logging, SIEM integration, feature flags/kill switches.

**Why Maya:** Auditability of infra changes; least privilege; fast mitigation.

### Frontend / Mobile

Native mobile apps + web; backend-for-frontend patterns; aggressive version skew tolerance.

**Why Maya:** Most money UX is mobile; APIs must tolerate duplicate requests and old clients.

### AI / ML

Fraud scoring, device risk, support copilots, AML typology assists — **advisory** to policy engines.

**Why Maya:** Useful for risk; dangerous if it can unilaterally move money without controls.

### Payments-Specific Tech Awareness

- Idempotency stores, outbox pattern, saga/orchestration for multi-step transfers.
- HSM/KMS, tokenization for cards (PCI-DSS scope reduction).
- JWS/OAuth style partner API signing (public developer platform patterns).
- QRPh payload handling; InstaPay/PESONet integration semantics (async vs. near-real-time).

---

## 04 - Typical Interview Process

Loops vary; prepare for this fintech-shaped process:

### Stage 1 — Recruiter Screen

**Purpose:** Motivation for fintech, level, logistics, regulated-environment comfort.

**Evaluation Criteria:** Clear narrative; payments/platform relevance; communication; stability signals.

**Preparation Tips:** 90-second story emphasizing transactional systems, reliability, security. Mention PH rails literacy if you have it; otherwise map adjacent experience (ledger, idempotency, fraud).

**Common Mistakes:** “I want startups” without safety mindset; no regulated-domain awareness; pure UI focus for backend Lead roles.

### Stage 2 — Technical Interview

**Purpose:** Java/Spring depth, API design, data consistency, debugging.

**Evaluation Criteria:** Correctness under failure; concurrency; security hygiene; testing of money paths.

**Preparation Tips:** Refresh transactions isolation, optimistic locking, idempotent endpoints, exactly-once *effects* (not magic exactly-once delivery).

**Common Mistakes:** Hand-waving distributed transactions; ignoring duplicate mobile submits; logging secrets/PII.

### Stage 3 — Coding Assessment

**Purpose:** Practical coding — balances, transfer validation, rate limits, state machines — sometimes take-home.

**Evaluation Criteria:** Edge cases (insufficient funds, concurrent debit), clarity, tests, error codes.

**Preparation Tips:** Practice wallet transfer kata: validate → lock/condition → journal → emit event. Write concurrency tests.

**Common Mistakes:** `balance -= amt` without constraints; no idempotency; unclear failure modes.

### Stage 4 — System Design

**Purpose:** Payments/wallet/fraud/KYC designs at national scale.

**Evaluation Criteria:** Requirements clarity; consistency model; fraud/security; ops/recon; PH context awareness.

**Preparation Tips:** Drill Section 08. Always draw funds flow and failure states.

**Common Mistakes:** Designing social feed; “just use 2PC”; no reconciliation; no limits; ignoring webhook retries.

### Stage 5 — Leadership Interview

**Purpose:** Ownership in incidents, mentoring, cross-team influence with Risk/Care.

**Evaluation Criteria:** Calm under pressure; accountability; ability to say no to unsafe scope.

**Preparation Tips:** Incident STAR with customer-fund impact and recon outcome.

**Common Mistakes:** Blaming “the rail”; no personal ownership; hero culture glorification.

### Stage 6 — Hiring Manager

**Purpose:** Team mission fit (wallet vs. bank vs. merchant vs. platform), roadmap, on-call.

**Evaluation Criteria:** Judgment, collaboration with non-eng, growth trajectory.

**Preparation Tips:** Ask about ledger ownership, fraud SLAs, release strategy, PCI/BSP audit interactions.

**Common Mistakes:** Not asking about money-movement ownership boundaries.

### Stage 7 — Final Interview

**Purpose:** Bar raise, culture, sometimes security deep dive.

**Evaluation Criteria:** Integrity, senior judgment, consistency of stories.

**Preparation Tips:** Prepare thoughtful questions on risk appetite, platform strategy, reliability goals.

**Common Mistakes:** Inconsistent security posture across interviews.

### Stage 8 — Offer

**Purpose:** Leveling and scope.

**Evaluation Criteria:** Mutual clarity on domain ownership.

**Preparation Tips:** Confirm on-call expectations, team charter (wallet/ledger/fraud), success metrics for first 6 months.

**Common Mistakes:** Accepting Lead title without clear technical authority or domain.

---

## 05 - Technical Focus Areas

### Fintech / Digital Banking (Philippines)

- BSP-regulated mindset: controls, audit, change management.
- Wallet vs. bank product boundaries and data sharing rules.
- Customer Care tooling that is powerful yet tightly audited.

### Payments & Wallets

- Cash-in/out, P2P, merchant pay, bills, QR.
- Authorization vs. clearing/settlement timelines.
- Partner and rail failure modes; customer-visible states (`PENDING`, `SUCCESS`, `FAILED`, `UNKNOWN` → resolve).

### Real-Time Balances & Ledger

- Available vs. ledger vs. hold balances.
- Double-entry journal; immutable postings; corrective entries (not silent edits).
- Concurrent debit protection; deterministic ordering per account.

### Event-Driven Money Movement

- Outbox from ledger commit → async side effects (notify, loyalty, analytics).
- Orchestration/saga for multi-step transfers with compensating actions that are themselves ledgered.
- Webhook delivery to merchants with signature verification and retries.

### KYC Document Pipeline

- Capture → virus scan → OCR/quality checks → manual review queues → decision → tier upgrade.
- PII minimization, encryption, retention, access audit.
- Fraudulent document detection; repeat identity graphs.

### Fraud Rule Engine

- Real-time features (device, velocity, graph, geo/IP, beneficiary risk).
- Rules + ML scores; explainability for disputes.
- Actions: allow, step-up auth, delay, block, limit.
- Shadow mode and canary rules before enforce.

### High-Availability Mobile Backends

- Multi-AZ, graceful degradation (read-only mode carefully designed), cache stampedes.
- Timeout budgets; bulkhead isolation between rails.
- Campaign/payday load tests; edge CDN for static; API shedding with user messaging.

### PCI-ish Security Mindset

- Reduce PCI scope via tokenization/hosted fields.
- No raw PAN in logs; key management; network segmentation.
- Secure SDLC; dependency risk; pen-test findings closure.

### Reconciliation & Disputes

- Recon against InstaPay/PESONet/card/merchant acquirers.
- Breaks classification; auto-resolve vs. ops queue.
- Dispute evidence packs and timelines.

### Idempotency, Limits, Compliance Holds

- Per-user and per-device limits; AML freeze workflows; sanctions screening touchpoints.
- Regulatory reporting extracts without blocking hot path incorrectly.

---

## 06 - Leadership Focus

### Ownership

Own fund-correctness outcomes: incidents, recon breaks, customer remediation.

### Mentoring

Teach juniors to think in state machines, failure modes, and threat models — not only happy API paths.

### Decision Making

Prefer boring, auditable designs for money. Innovate at edges (UX, fraud features), not at ledger integrity.

### Cross-team Collaboration

Co-design with Fraud/Risk/Compliance; align Care macros with real system states; partner with merchants/platform teams on API contracts.

### Incident Response

SEV definitions tied to money impact and blast radius. Freeze deployments; enable limits; communicate factually; recon before declaring victory.

### Architecture Discussions

Challenge unsafe dual-writes to “balance tables.” Demand idempotency and tracing in every RFC that moves money.

### Technical Debt

Prioritize debt that causes recon breaks, unclear transaction states, or security findings. Quantify loss/ops hours.

### Engineering Culture

Blameless postmortems with mandatory control improvements. Celebrate prevented fraud and clean recons, not only feature launches.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you owned a money-moving feature in production.

- **Why asked:** Core Maya ownership signal.
- **Competencies:** Accountability, transactional design.
- **Framework:** Funds flow → invariants → idempotency → rollout limits → recon → outcome metrics.
- **Follow-ups:** What failure modes did you test? How did Care know what to tell users?

### Q2. Describe an incident where balances or transfers were wrong.

- **Why asked:** Integrity under pressure.
- **Competencies:** Incident leadership, debugging.
- **Framework:** Detect → stop the bleed → customer impact → fix forward/compensate → recon → postmortem controls.
- **Follow-ups:** How many accounts affected? How did you prevent silent “balance patching”?

### Q3. Tell me about designing for idempotent APIs used by mobile clients.

- **Why asked:** Duplicate taps/retries are guaranteed.
- **Competencies:** API design, distributed systems.
- **Framework:** Idempotency key scope → storage → response replay → expiry → metrics on conflicts.
- **Follow-ups:** What happens if two different payloads share a key? Multi-device same user?

### Q4. Give an example of partnering with Fraud or Risk on a launch.

- **Why asked:** Cross-functional fintech reality.
- **Competencies:** Collaboration, judgment.
- **Framework:** Threat scenarios → controls → step-up auth → shadow rules → launch gates → loss metrics.
- **Follow-ups:** What control did engineering push back on and why?

### Q5. Tell me about a KYC funnel improvement you influenced.

- **Why asked:** Growth vs. compliance tension.
- **Competencies:** Product mindset, privacy.
- **Framework:** Drop-off analysis → document quality issues → UX/tech fixes → integrity checks → conversion + fraud rate.
- **Follow-ups:** How did you avoid weakening identity assurance?

### Q6. Describe leading a high-severity production incident affecting payments.

- **Why asked:** Lead calm and structure.
- **Competencies:** Crisis leadership, communication.
- **Framework:** Roles → mitigation (kill switch/limit) → external/internal comms → recovery → customer remediation.
- **Follow-ups:** What did you tell executives? What changed in runbooks?

### Q7. Tell me about a time you said no to a feature because it was unsafe.

- **Why asked:** Backbone in regulated env.
- **Competencies:** Integrity, influence.
- **Framework:** Risk articulation → safer alternative → stakeholder alignment → delivered control + partial value.
- **Follow-ups:** Did business pressure continue? How did you document the decision?

### Q8. Share an example of reconciliation saving you from a larger incident.

- **Why asked:** Recon is a first-class control.
- **Competencies:** Operability, financial controls.
- **Framework:** Break detection → classification → root cause → customer fix → automation improvement.
- **Follow-ups:** False positive rate? Who owns unresolved breaks?

### Q9. Tell me about mentoring an engineer who underestimated consistency issues.

- **Why asked:** Technical leadership.
- **Competencies:** Mentoring, systems teaching.
- **Framework:** Concrete bug → teaching model (holds/ledger) → pairing → checklist in reviews → independence.
- **Follow-ups:** How do you encode that learning in team standards?

### Q10. Describe a performance incident on a hot balance/read path.

- **Why asked:** Mobile HA expectations.
- **Competencies:** Performance, capacity.
- **Framework:** Symptoms → cache/DB diagnosis → safe mitigation → lasting fix → load test.
- **Follow-ups:** Did caching ever show stale available balance? How handled?

### Q11. Tell me about implementing rate limits or velocity controls.

- **Why asked:** Fraud + abuse reality.
- **Competencies:** Security, product balance.
- **Framework:** Abuse pattern → limit design → user messaging → override/break-glass → effectiveness metrics.
- **Follow-ups:** How did limits affect legit payroll senders?

### Q12. Give an example of improving observability for payment traces.

- **Why asked:** Debuggability across services/rails.
- **Competencies:** Operability.
- **Framework:** Blind spot → correlation IDs → structured events → dashboards/alerts → MTTD drop.
- **Follow-ups:** How do you avoid logging PII/PAN?

### Q13. Tell me about a disagreement with product on launch timing.

- **Why asked:** Risk appetite negotiation.
- **Competencies:** Stakeholder management.
- **Framework:** Evidence of risk → phased rollout proposal → decision → result.
- **Follow-ups:** What metric unlocked full launch?

### Q14. Describe working with Customer Care during a widespread issue.

- **Why asked:** Customer trust operations.
- **Competencies:** Empathy, communication.
- **Framework:** Accurate macros → tooling for lookup → escalation path → follow-up when resolved.
- **Follow-ups:** What Care capability did you build afterward?

### Q15. Tell me about a security finding you drove to closure.

- **Why asked:** Secure SDLC maturity.
- **Competencies:** Security ownership.
- **Framework:** Severity → exploit scenario → patch/WAF/config → verification → regression tests.
- **Follow-ups:** How did it enter the codebase? Process change?

### Q16. Share a time you designed a webhook/event delivery system for merchants.

- **Why asked:** Merchant platform reliability.
- **Competencies:** Integration design.
- **Framework:** At-least-once → signatures → retries/backoff → DLQ → merchant replay tooling.
- **Follow-ups:** How do merchants reconcile missed events?

### Q17. Tell me about handling an ambiguous transaction state from an external rail.

- **Why asked:** Real payments pain.
- **Competencies:** Ambiguity management, correctness.
- **Framework:** Timeout → inquiry API → pending policy → customer messaging → finalization/recon.
- **Follow-ups:** Did you ever double-credit? How prevented?

### Q18. Describe introducing stronger code review standards for financial services.

- **Why asked:** Lead culture building.
- **Competencies:** Quality leadership.
- **Framework:** Incident-inspired checklist → adopt in PRs → measure escaped defects.
- **Follow-ups:** How did you avoid review bureaucracy?

### Q19. Tell me about a migration involving account or ledger schema changes.

- **Why asked:** Upgrade safety in money systems.
- **Competencies:** Migration discipline.
- **Framework:** Expand/contract → dual-read → recon validators → cohort rollout → rollback.
- **Follow-ups:** How did you verify historical statements still correct?

### Q20. Give an example of cross-team delivery (wallet + bank + notifications).

- **Why asked:** Ecosystem complexity at Maya.
- **Competencies:** Coordination, contracts.
- **Framework:** Shared sequence diagram → SLAs → failure contracts → joint test plan → launch.
- **Follow-ups:** What contract broke first in staging?

### Q21. Tell me about using feature flags / kill switches in production.

- **Why asked:** Mitigation speed.
- **Competencies:** Release engineering.
- **Framework:** Flag design → owner → default safe → drill → incident use → cleanup.
- **Follow-ups:** Who can flip money-related flags? Audit?

### Q22. Describe a time fraud losses spiked and engineering responded.

- **Why asked:** Fraud loop ownership.
- **Competencies:** Analytical thinking, urgency.
- **Framework:** Pattern ID → emergency rules → customer protection → durable detection → retro with Risk.
- **Follow-ups:** False positive impact? How tuned?

### Q23. Tell me about improving a partner onboarding / API developer experience.

- **Why asked:** Platform thinking (Maya Developers).
- **Competencies:** External empathy, API design.
- **Framework:** Partner pain → auth/signing clarity → sandbox fidelity → support deflection metrics.
- **Follow-ups:** How do you keep sandbox behavior honest vs. prod?

### Q24. Share how you prioritized tech debt against growth OKRs.

- **Why asked:** Lead judgment.
- **Competencies:** Prioritization.
- **Framework:** Risk-ranked debt → tie to loss/uptime/Care cost → negotiate capacity → show before/after.
- **Follow-ups:** What debt did you consciously defer?

### Q25. Tell me about an architectural decision you later reversed.

- **Why asked:** Humility and learning.
- **Competencies:** Reflection, adaptability.
- **Framework:** Original bet → signal it was wrong → migration off → lesson encoded.
- **Follow-ups:** What early indicator did you miss?

### Q26. Describe coaching a team through on-call burnout or noisy alerts.

- **Why asked:** Sustainable operations.
- **Competencies:** People leadership, SRE basics.
- **Framework:** Alert audit → SLO-based alerts → runbook quality → rotation health.
- **Follow-ups:** What % of alerts became actionable?

### Q27. Tell me about handling PII/KYC document access requests internally.

- **Why asked:** Privacy & insider threat awareness.
- **Competencies:** Compliance, security.
- **Framework:** Need-to-know → just-in-time access → audit → redaction → policy alignment.
- **Follow-ups:** How long are documents retained? Who approves access?

### Q28. Give an example of load testing before a major campaign.

- **Why asked:** Payday/promo readiness.
- **Competencies:** Capacity planning.
- **Framework:** Traffic model → critical journeys → bottlenecks → fixes → go/no-go criteria.
- **Follow-ups:** What production surprise still happened?

### Q29. Tell me about influencing standards across multiple squads.

- **Why asked:** Staff/Lead multi-team impact.
- **Competencies:** Influence without authority.
- **Framework:** Shared incident theme → guild/RFC → reference implementation → adoption metrics.
- **Follow-ups:** Who resisted and how did you address it?

### Q30. Describe delivering bad news about a delay tied to compliance/security work.

- **Why asked:** Integrity under business pressure.
- **Competencies:** Communication, ethics.
- **Framework:** Early warning → options → recommend → align → protect customer/funds.
- **Follow-ups:** How did you maintain trust afterward?

### Q31. Tell me about designing step-up authentication or SCA-like flows.

- **Why asked:** Fraud + UX balance.
- **Competencies:** Security UX.
- **Framework:** Risk triggers → challenge methods → fallback → completion rates → fraud delta.
- **Follow-ups:** How do you prevent challenge fatigue?

### Q32. Share a time you improved settlement or merchant payout accuracy.

- **Why asked:** Merchant side money correctness.
- **Competencies:** Domain modeling, ops.
- **Framework:** Settlement rules → cutoffs → recon → dispute handling → merchant trust metrics.
- **Follow-ups:** How are fees and taxes represented in ledger?

### Q33. Tell me about hiring for a fintech team — what signals matter?

- **Why asked:** Lead hiring bar.
- **Competencies:** Talent selection.
- **Framework:** Probe for failure-mode thinking → past ownership of correctness → security instincts → teachability.
- **Follow-ups:** What is an automatic no-hire signal for you?

---

## 08 - System Design Questions

### Scenario A — Wallet Transfer System (P2P)

**Requirements:**  
User A sends money to User B by mobile/username. Strong consistency on balances. Idempotent. Limits and fraud checks. Clear states for sender/receiver. Notifications async. High concurrency on popular accounts (e.g., merchants receiving).

**Architecture Discussion:**  
API → authN/Z → limit/fraud service (fail-closed policies) → Transfer orchestrator → Ledger service posts double-entry journal (debit A, credit B) in one transactional boundary per account sharding strategy → outbox → notify. Idempotency key required. Account-level locking or compare-and-swap on account version. Hot accounts may use queue-per-account to serialize postings.

**Tradeoffs:**  
Single DB transaction across both accounts (simpler, scaling limits) vs. saga with holds (scales, harder). Sync fraud vs. async with delay.

**Scaling:**  
Shard accounts; serialize per account; horizontal API tier; cache profile data not balances (or cache carefully with version).

**Reliability:**  
Idempotent postings; pending on upstream uncertainty; recon job; compensating entries only as ledgered corrections.

**Security:**  
Device binding/step-up for risky transfers; beneficiary cool-off; audit trail; PII-safe logs.

**Production Considerations:**  
Duplicate push notifications; Care lookup by transfer id; kill switch by corridor; metrics: success rate, insufficient funds, fraud blocks, p99 latency.

---

### Scenario B — QR Payment Flow (QRPh / Merchant QR)

**Requirements:**  
Customer scans merchant QR, confirms amount, pays from wallet. Merchant must get reliable confirmation. Handle offline merchant connectivity via polling/webhooks. Prevent double pay. Peak lunchtime traffic.

**Architecture Discussion:**  
QR decode → merchant resolution → quote/intent creation → user confirm → authorize ledger hold/capture → rail/in-house settle path → merchant webhook + app push. Payment intent as state machine: `CREATED → AUTHORIZED → CAPTURED/FAILED/EXPIRED`. Idempotency on confirm. Merchant QR static vs. dynamic amount validation.

**Tradeoffs:**  
Hold-then-capture vs. immediate post; webhook-first vs. merchant poll; in-house wallet ledger vs. external rail.

**Scaling:**  
Stateless payment APIs; partition by merchant; async webhook workers; cache merchant profiles.

**Reliability:**  
Intent expiry; inquiry endpoints; exactly-once capture effects; DLQ for webhooks; recon merchant settlement vs. ledger.

**Security:**  
Signed QR payloads where applicable; merchant category limits; fraud velocity on device/merchant pair; replay protection.

**Production Considerations:**  
Partial outages: degrade to delayed confirmation messaging; Care tools; dashboards for authorize success and webhook failure rates.

---

### Scenario C — Statement Generation

**Requirements:**  
Generate monthly statements for millions of accounts. Accurate running balances. PDF/app views. Regenerate after corrections. Cost-efficient. Must not overload OLTP.

**Architecture Discussion:**  
CDC/ledger events → statement projection store → monthly batch/stream aggregator → immutable statement snapshot → object storage for PDFs → app reads snapshots. On-demand generation for sparse users. Correction entries create addendum or regenerated snapshot with version.

**Tradeoffs:**  
Batch once a month vs. continuous projection; PDF pregeneration vs. on-demand; storing every transaction vs. tiered archival.

**Scaling:**  
Partition by account hash; spot/preemptible workers; separate analytical store; rate-limit regenerations.

**Reliability:**  
Checksum statements vs. ledger; regeneration audit; quarantine failed accounts; replay from event offsets.

**Security:**  
Encrypted storage; access only by owner/authorized Care with audit; signed download URLs.

**Production Considerations:**  
Backpressure against OLTP; cost alarms; customer disputes when regenerated numbers change — versioning policy must be explicit.

---

### Scenario D — Fraud Rule Engine

**Requirements:**  
Evaluate transfers/payments in near real time (< budgeted ms). Rules + ML score. Actions: allow, step-up, block. Shadow mode. Explain decisions. Rapid rule deploy without full app release if possible.

**Architecture Discussion:**  
Feature service (velocity, device, graph, beneficiary history) → decision engine (rules DSL + model score) → action enforcer in payment orchestrator. Features from streaming aggregates + online KV. Rules config in versioned store with canary %. Decision log immutable for disputes/AML.

**Tradeoffs:**  
Sync hard block vs. async review; richness of features vs. latency budget; rules centralization vs. embedded checks.

**Scaling:**  
Cache features; precompute velocity windows; isolate fraud infra from ledger outages (define fail-open/closed carefully — usually fail-closed for high-risk types).

**Reliability:**  
Fallback policies if fraud service times out; circuit breakers; deterministic decision ids; replay for model evaluation offline.

**Security:**  
Protect rules from tampering; restrict who can publish enforce-mode rules; audit all changes.

**Production Considerations:**  
False positive monitoring with product; fraud loss dashboards; emergency rule push process; model drift reviews.

---

### Scenario E — KYC Document Pipeline

**Requirements:**  
Users upload IDs/selfies. Automated checks + manual review. Upgrade KYC tier. SLA for review. Fraudulent docs. Strict PII controls. Burst during growth campaigns.

**Architecture Discussion:**  
Upload via pre-signed URL → object storage (encrypted) → metadata DB → virus scan → quality/OCR/face match workers → risk signals → auto-approve/reject or case queue → reviewer tooling → decision service updates customer tier → notify. Event-driven stages with retries. Deduplicate identities via document hash/graph.

**Tradeoffs:**  
Auto-approval aggressiveness vs. fraud; in-house review vs. vendor; sync UX waits vs. async “we’ll notify you.”

**Scaling:**  
Queue workers; priority lanes (VIP/business); autoscale OCR; separate storage accounts for KYC.

**Reliability:**  
Poison message handling; reprocess tools; SLA aging alerts; idempotent decision application.

**Security:**  
KMS CMKs; strict IAM; viewer watermarking; short-lived access; retention/legal deletion; no documents in tickets/logs.

**Production Considerations:**  
Vendor outage fallbacks; reviewer accuracy QA sampling; regulator audit export; conversion funnel metrics.

---

### Scenario F — Cash-In via Partner + Rail with Reconciliation

**Requirements:**  
User cashes in through partners/banks/rails. Funds should appear reliably. Partner may retry. Rail may be delayed. Ops needs recon and break resolution. Customer messaging must not promise funds early.

**Architecture Discussion:**  
Partner API (signed) → cash-in intent → await partner/rail confirmation → ledger credit → notify. Treat partner callbacks as at-least-once. Inquiry jobs for stuck intents. Daily recon file vs. ledger; breaks workflow. Customer sees `PROCESSING` until final.

**Tradeoffs:**  
Credit on partner callback vs. only after rail settlement (risk vs. UX). Auto-credit with threshold vs. always hold for recon.

**Scaling:**  
Partition intents; horizontal callback ingress with verification; batch recon workers.

**Reliability:**  
Idempotent credit by partner reference id; duplicate callback safe; manual credit tool fully audited; reverse only via ledgered debit rules.

**Security:**  
mTLS/JWS; IP allowlists; amount tamper checks; partner secret rotation.

**Production Considerations:**  
Partner scorecards; SLA reports; Care scripts aligned to states; incident playbooks for partner outage.

---

## 09 - Company Preparation Checklist

- [ ] Explain Maya (ex-PayMaya) + Maya Bank distinction in plain language
- [ ] Name core journeys: cash-in, P2P, QR pay, KYC upgrade, merchant accept
- [ ] Prepared 2 stories on idempotency / duplicate requests
- [ ] Prepared 1 ledger/balance integrity incident or design
- [ ] Prepared 1 fraud/risk collaboration story
- [ ] Prepared 1 KYC/PII security story
- [ ] Prepared 1 reconciliation / ambiguous rail state story
- [ ] Prepared leadership stories: SEV incident, mentoring, saying no to unsafe scope
- [ ] Reviewed Java concurrency + transactional boundaries
- [ ] Drilled 3 designs from Section 08 with funds-flow diagrams
- [ ] Listed 8 questions for HM (ledger ownership, fraud SLA, on-call, rails)
- [ ] Mapped resume to fintech language (auth success, loss rate, recon, uptime)
- [ ] Refreshed PH rails vocabulary: InstaPay, PESONet, QRPh (conceptual)
- [ ] Reviewed PCI-scope reduction concepts (tokenization, no PAN in logs)

---

## 10 - How My Experience Maps

| Maya expectation | My evidence (system, metric, decision) | Gap / plan |
|------------------|----------------------------------------|------------|
| Money movement / ledger thinking | | |
| Idempotency & retries | | |
| Fraud/risk collaboration | | |
| KYC / PII handling | | |
| High-availability mobile APIs | | |
| Reconciliation & disputes | | |
| Security / PCI-ish mindset | | |
| Incident leadership | | |
| Cross-team delivery | | |
| Mentoring & standards | | |

Narrative template:  
“In [payments/wallet/transactional system], I owned [path]. The invariant was [no double-spend / exact balances]. Failure mode [rail timeout] forced [pending + inquiry + recon]. Result: [metric]. At Maya I’d apply this to [wallet/QR/KYC/fraud].”

---

## Interview Confidence Checklist

- [ ] I can draw a P2P transfer with ledger postings and failure states
- [ ] I can explain idempotency keys and response replay
- [ ] I can design QR payment intent state machines
- [ ] I can discuss fraud fail-open vs. fail-closed tradeoffs
- [ ] I can design KYC document flow with PII controls
- [ ] I can explain reconciliation breaks and remediation
- [ ] I have 6 STAR stories with fund-impact metrics
- [ ] I can challenge unsafe “just update balance” designs
- [ ] I sound regulated-industry ready without fake compliance claims
- [ ] My questions show rails/fraud/ops literacy

---

## Mock Interview Preparation Checklist

- [ ] 45-min design: wallet transfer OR QR payment
- [ ] 45-min design: fraud engine OR KYC pipeline
- [ ] 45-min design: statements OR cash-in recon
- [ ] 60-min behavioral: incident + fraud + mentoring set
- [ ] 45-min coding: transfer validation + concurrent debit tests
- [ ] HM mock: prioritize reliability vs. growth feature
- [ ] Record one funds-flow explanation; remove jargon fog
- [ ] Peer challenge: “Where can money be duplicated or lost?”

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | Company/products + experience mapping table |
| 2 | Ledger, balances, idempotency; coding kata |
| 3 | Scenario A & B designs aloud |
| 4 | Fraud + KYC designs; security stories |
| 5 | Statements + cash-in recon; ops/Care angle |
| 6 | Behavioral Q1–Q17 |
| 7 | Behavioral Q18–Q33 |
| 8 | Full mock (design + behavioral + coding) |
| 9 | Close gaps; refine PH rails vocabulary |
| 10 | Light review; rest; logistics |

---

## Estimated Preparation Time

| Profile | Hours |
|---------|------:|
| Strong backend, little payments experience | 30–40 |
| Payments experience, weak storytelling/design structure | 20–28 |
| Strong fintech + Java, need Maya-specific polish | 12–18 |
| Lead bar (fraud/KYC/incident depth + multi-team) | 32–45 |

Minimum viable: **3–4 focused days** if core modules are done — expect harder follow-ups on recon and ambiguous rail states.

**Target for Lead SE:** ~**32 hours** across 10 days with two full mocks and one coding kata under time pressure.
