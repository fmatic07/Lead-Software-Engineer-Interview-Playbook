# Deltek

> Reverse-engineer how Deltek evaluates Lead/Senior engineers for enterprise SaaS ERP, multi-tenant project platforms, and long-lived B2B product engineering.

---

## 01 - Company Overview

### Industry

Deltek sits in **project-based enterprise software** — ERP, PSA (Professional Services Automation), PPM, and workforce management for firms that bill time, manage projects, and live on utilization and margin. Primary verticals: architecture & engineering (A&E), consulting, government contracting (GovCon), aerospace/defense contractors, and professional services.

Interview implication: panels assume you understand **enterprise B2B constraints** — multi-entity accounting, auditability, configuration over customization, and change that cannot break a customer's month-end close.

### Products

Know the portfolio well enough to map your experience:

| Product | Domain signal |
|---------|---------------|
| **Vantagepoint** | A&E / consulting ERP — CRM, projects, resources, financials |
| **Maconomy** | Global professional services ERP — multi-entity, project accounting |
| **Costpoint** | GovCon ERP — compliance-heavy, contract accounting, earned value adjacency |
| **PPM / Cobra / Open Plan / related** | Project controls, earned value, scheduling |
| **Replicon / WFM adjacencies** | Time, workforce, utilization |
| **Harmony UX + Dela AI** | Unified experience and industry-tuned AI assistants/insights |

Interview implication: you are not joining a greenfield consumer app. You join **product lines with decades of domain rules**, SaaS migration paths, and customers who treat the system as system-of-record.

### Engineering Culture

Expect evaluation against:

- **Product longevity** — designs that survive years of releases, not demo-week architecture.
- **Customer-impact awareness** — a regression can block billing, payroll, or audit packages.
- **Pragmatic modernization** — cloud migration, modularization, UX unification (Harmony), AI features — without rewriting the business.
- **Cross-functional delivery** — PMs, domain SMEs, support, professional services, and customers in the feedback loop.

### Business Model

Subscription SaaS + on-prem/legacy footprints still matter for some products. Revenue depends on **retention, expansions, implementations, and upgrade confidence**. Engineering quality shows up as lower support load, safer upgrades, and features that sell without custom forks.

### Scale

Enterprise tenants: multi-company, multi-currency, large project portfolios, heavy reporting, integration to payroll/GL/HRIS. Throughput is often **burst + batch** (timesheets Monday morning, month-end close, invoice runs) more than TikTok-scale QPS — but correctness and isolation matter more than raw RPS.

### Global Presence

US-rooted product company with global customers and delivery/engineering presence. Interviewers care that you can work across time zones, support regional compliance differences, and design for **multi-entity / multi-currency** reality.

### Technology Direction

Themes to reference without marketing language:

- Cloud SaaS consolidation and infrastructure modernization (including OCI moves for parts of the portfolio — treat as “cloud migration + operational maturity,” not trivia).
- Harmonized UX across products.
- Embedded AI for summaries, insights, and workflow assistance on top of governed enterprise data.
- Integration platform thinking — customers will not rip out their ecosystem.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

Successful senior/lead hires at Deltek-like orgs:

1. Own a domain slice end-to-end (schema → API → UI contract → migration → observability → supportability).
2. Prefer reversible, tenant-safe changes over clever rewrites.
3. Speak fluently about **data model consequences** of feature requests.
4. Partner with product on configurability vs. customization tradeoffs.
5. Raise the bar on testing around billing, time, permissions, and reporting paths.

### Ownership

- You are accountable for production behavior of your area, including upgrade notes and support playbooks.
- “Done” includes migration scripts, feature flags, rollback plan, and metrics that prove the change worked per tenant.
- You do not throw tickets over the wall to support without reproduction steps and blast-radius analysis.

### Technical Leadership

- Drive design reviews that force explicit tenant isolation, permission model, and upgrade strategy.
- Unblock juniors on domain complexity (project → WBS → labor → billing → revenue recognition chains).
- Influence architecture without needing a title: ADRs, spike outcomes, risk registers for releases.

### Product Mindset

- Every feature has a **buyer** (CFO, project controller, resource manager) and a **daily user** (timesheet submitter, PM).
- Configuration knobs create combinatorial testing debt — treat them as first-class design decisions.
- AI features must respect tenant data boundaries and audit requirements.

### Collaboration Style

- Work with domain experts; do not invent accounting semantics.
- Align with professional services / implementation teams — they feel the pain of brittle APIs.
- Document for the next engineer and the support engineer at 2 a.m.

### Engineering Principles (what panels probe)

| Principle | Interview signal |
|-----------|------------------|
| Tenant safety | Isolation model, noisy-neighbor, shared vs. dedicated resources |
| Upgrade safety | Expand/contract migrations, dual-write, backfill strategy |
| Auditability | Who changed what, when, for compliance and customer disputes |
| Integration first | Idempotent APIs, webhooks, mapping layers, versioning |
| Observability | Per-tenant metrics, correlation IDs, support-ready logs |
| Correctness over cleverness | Money, time, and permissions have zero tolerance for “eventual maybe” |

---

## 03 - Typical Technology Stack

Stacks vary by product line. Interview with **principles + likely stack**, not fake certainty.

### Java

Core backend language for many enterprise ERP/SaaS services. Matters because of long-lived codebases, strong typing for complex domain models, and JVM operational maturity.

**Why Deltek:** Domain richness (projects, labor, billing) benefits from explicit models, refactoring safety, and mature concurrency/tooling.

### Spring Boot

Service boundaries, DI, security filters, transactional boundaries, Actuator health, batch jobs.

**Why Deltek:** Fits modularization of monoliths and new microservices around reporting, integrations, notifications, and AI sidecar APIs.

### Cloud

AWS and/or Oracle Cloud Infrastructure depending on product migration path. Expect VPC isolation, managed DB, object storage, secrets managers, IAM.

**Why Deltek:** SaaS delivery, multi-region considerations for global firms, compliance posture for GovCon-adjacent products.

### Microservices

Hybrid reality: modular monoliths + extracted services for integrations, search, reporting, document services, notification, identity.

**Why Deltek:** Full microservice fan-out is rarely the first move for ERP; extraction follows pain (scale of reporting, independent release of integration hub).

### Databases

Relational is the source of truth (SQL Server / Oracle / Postgres-class depending on lineage). Heavy use of normalized project/financial models, reporting replicas, possibly OLAP/warehouse for analytics.

**Why Deltek:** Financial and project data demand ACID boundaries, referential integrity, and carefully designed indexes for portfolio queries.

### Messaging

Kafka / RabbitMQ / cloud queues for async: timesheet aggregation, invoice generation, notification fan-out, integration outbox.

**Why Deltek:** Decouples interactive UX from heavy close/reporting pipelines; enables reliable integration retries.

### CI/CD

Trunk-based or release-train hybrids; automated tests; DB migration gates; canary/tenant-cohort rollouts.

**Why Deltek:** Upgrade confidence is a product feature. Broken migrations destroy trust.

### Kubernetes / Containers

Containerized services, HPA for burst workloads (timesheet peaks), job runners for batch.

**Why Deltek:** Standardizes cloud ops across product lines and supports elastic batch + API tiers.

### Infrastructure

IaC (Terraform/CloudFormation), secrets, observability stack (metrics/logs/traces), feature flags.

**Why Deltek:** Multi-product SaaS needs repeatable environments and auditable infra changes.

### Frontend

Modern SPA stacks (React-class) under Harmony UX direction; older product UIs still exist.

**Why Deltek:** Lead candidates should discuss API contracts, permission-aware UI, and progressive delivery — not only backend.

### AI (Dela / assistants)

LLM-assisted summaries and insights over tenant-governed data; retrieval with strict tenancy filters; human-in-the-loop for financial actions.

**Why Deltek:** Interviewers will ask how you prevent cross-tenant leakage and hallucinated financial numbers.

---

## 04 - Typical Interview Process

Exact loops vary by role and location. Prepare for this shape:

### Stage 1 — Recruiter Screen

**Purpose:** Role fit, level calibration, logistics, motivation for enterprise SaaS.

**Evaluation Criteria:** Clarity of career narrative; relevant domain (ERP, SaaS, B2B, platform); compensation/location alignment; communication.

**Preparation Tips:** 90-second story: enterprise systems ownership → multi-tenant or complex data model work → leadership impact. Name Deltek products and why project-based ERP fits you.

**Common Mistakes:** Generic “I like cloud” answers; no SaaS/upgrade awareness; talking only consumer apps.

### Stage 2 — Technical Interview

**Purpose:** Depth in Java/Spring, APIs, data modeling, debugging production issues.

**Evaluation Criteria:** Correctness under constraints; ability to reason about transactions, permissions, and schema; code clarity; testing mindset.

**Preparation Tips:** Refresh Spring transactions, JPA pitfalls, API versioning, idempotency. Prepare one war story: production bug in billing/time/permissions.

**Common Mistakes:** Framework trivia without tradeoffs; ignoring N+1 and tenant filters; no mention of tests/migrations.

### Stage 3 — Coding Assessment

**Purpose:** Practical coding — often service-style problems (parsing timesheets, aggregations, permission checks), not pure puzzle LeetCode.

**Evaluation Criteria:** Clean models, edge cases, complexity awareness, readability, tests.

**Preparation Tips:** Practice Java streams + careful aggregation; write tests for partial failures; name domain concepts clearly.

**Common Mistakes:** Clever one-liners that hide bugs; ignoring invalid states; no error taxonomy.

### Stage 4 — System Design

**Purpose:** Multi-tenant SaaS design, integrations, reporting, document/permissions.

**Evaluation Criteria:** Requirements discipline; tenancy model; data consistency; scaling batch vs. online; operability; security.

**Preparation Tips:** Drill the scenarios in Section 08. Always start with tenant isolation and audit.

**Common Mistakes:** Designing Instagram; forgetting month-end batch; no upgrade/migration plan; weak permission model.

### Stage 5 — Leadership Interview

**Purpose:** Mentoring, conflict, technical debt, cross-team influence.

**Evaluation Criteria:** Ownership language; stakeholder management; how you raise standards without heroics.

**Preparation Tips:** STAR stories with metrics (defect escape, upgrade time, support volume).

**Common Mistakes:** “I just coded faster”; blaming PMs; no examples of disagree-and-commit.

### Stage 6 — Hiring Manager

**Purpose:** Team fit, roadmap ownership, how you operate in product org.

**Evaluation Criteria:** Judgment under ambiguity; prioritization; communication with non-engineers.

**Preparation Tips:** Ask about product line, monolith vs. services, on-call, release cadence. Map your experience to their pain.

**Common Mistakes:** Not asking product questions; over-rotating on resume slogans.

### Stage 7 — Final Interview

**Purpose:** Bar raise / cross-org calibration; culture; sometimes architecture deep dive.

**Evaluation Criteria:** Senior judgment; values alignment; ability to represent engineering externally to customers/PS.

**Preparation Tips:** Prepare questions on tenant architecture, AI governance, and modernization strategy.

**Common Mistakes:** Coasting; inconsistent stories across loops.

### Stage 8 — Offer

**Purpose:** Level, comp, role scope.

**Evaluation Criteria:** Mutual fit; clear ownership area.

**Preparation Tips:** Clarify product assignment, on-call, hybrid expectations, impact scope for Lead vs. Senior.

**Common Mistakes:** Accepting vague “Lead” scope without team/charter clarity.

---

## 05 - Technical Focus Areas

### Multi-Tenant Architecture

Be ready to discuss:

- Isolation models: silo DB per tenant, schema-per-tenant, shared schema with `tenant_id`, hybrid for enterprise whales.
- Noisy neighbor: rate limits, query governors, separate reporting compute.
- Tenant context propagation: gateway → service → DB row filters / RLS.
- Cross-tenant admin tooling vs. customer-facing APIs.

### Enterprise SaaS / ERP Project & Resource Management

- Project hierarchy (project → phases/tasks/WBS), assignments, utilization.
- Labor cost vs. bill rates; overhead; currency.
- Resource management conflicts and forecasting.
- Configuration: charge codes, approval workflows, billing rules.

### Reporting & Analytics

- Operational reports vs. analytical warehouses.
- Consistency: “report numbers match GL” is a trust requirement.
- Async report generation, snapshotting, materialized views.
- Permission-trimmed report rows (project-level security).

### Integrations

- ERP/GL/HRIS/payroll connectors; iPaaS patterns.
- Outbox + idempotent consumers; mapping layers; versioned payloads.
- Customer-specific mappings without forking core product.
- Retry, DLQ, reconciliation reports for failed syncs.

### B2B Enterprise Constraints

- Change windows, UAT tenants, implementation partners.
- Backward-compatible APIs; deprecation policies measured in quarters.
- Feature flags per tenant cohort.
- Audit logs for SOX-ish / contract compliance needs (especially Costpoint adjacency).

### Data Model Complexity

- Normalized financial models; slowly changing dimensions for rates.
- Historical correctness: changing a bill rate must not silently rewrite history without rules.
- Soft deletes, effective dating, period locks (closed accounting periods).

### Migration / Upgrade Safety

- Expand → migrate → contract.
- Backfills with progress and pause; tenant-by-tenant rollout.
- Dual-read/dual-write during model changes.
- Data repair tooling and support runbooks.

### Documents, Permissions, Search

- Object storage + metadata DB; ACE/ACL models; inheritance from project security.
- Virus scan, retention policies, legal hold.

### AI on Enterprise Data

- Tenant-scoped retrieval; no training leakage across customers.
- Cite sources; block actions that post journals without confirmation.
- Latency and cost controls for assistant features.

---

## 06 - Leadership Focus

### Ownership

Own upgrade risk, customer regressions, and supportability — not just story points.

### Mentoring

Teach domain modeling and “how money/time flows through the system.” Review designs for tenant and period-lock safety.

### Decision Making

Use ADRs for tenancy, storage, and integration choices. Make the cost of configurability explicit.

### Cross-team Collaboration

Coordinate with UX (Harmony), data/reporting, integrations, SRE, and Professional Services. Treat PS feedback as production telemetry.

### Incident Response

Severity by customer business impact (cannot submit timesheets / cannot invoice). Communicate status to Support and CSM. Postmortems with migration/test gaps.

### Architecture Discussions

Defend modular boundaries; resist big-bang rewrites; propose strangler extractions with measurable milestones.

### Technical Debt

Prioritize debt that blocks upgrades, multi-tenancy correctness, or onboarding velocity. Quantify support tickets and cycle time.

### Engineering Culture

Bias to tested, reversible change. Document for global teams. Quality is a sales enablement function in B2B SaaS.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you owned a multi-tenant feature end-to-end.

- **Why asked:** Tenancy is core risk at Deltek.
- **Competencies:** Ownership, security mindset, delivery.
- **Framework:** Context (shared schema) → isolation design → tests for cross-tenant leak → rollout flags → metrics → outcome.
- **Follow-ups:** How did you prove no leakage? What broke in staging? How did support detect issues?

### Q2. Describe a migration that had to be reversible.

- **Why asked:** Upgrade safety is product trust.
- **Competencies:** Technical judgment, risk management.
- **Framework:** Expand/contract → dual-write → backfill controls → rollback drill → customer communication.
- **Follow-ups:** How long was dual-write? How did you verify per-tenant completion?

### Q3. Tell me about a production incident during month-end / peak load.

- **Why asked:** ERP peaks are predictable and brutal.
- **Competencies:** Incident leadership, prioritization.
- **Framework:** Detect → mitigate (feature flag / scale / disable report) → communicate → fix → prevent.
- **Follow-ups:** What was customer impact? What changed in capacity planning?

### Q4. How have you handled conflicting requirements from finance vs. project managers?

- **Why asked:** Multi-persona ERP product tension.
- **Competencies:** Product collaboration, stakeholder management.
- **Framework:** Clarify jobs-to-be-done → find shared data model → propose config → validate with both → ship with docs.
- **Follow-ups:** What did you refuse to build? How did you measure success?

### Q5. Give an example of reducing technical debt without stopping feature delivery.

- **Why asked:** Long-lived products need continuous remediation.
- **Competencies:** Prioritization, influence.
- **Framework:** Quantify pain → carve strangler milestone → attach to feature work → show metric drop.
- **Follow-ups:** Who resisted? How did you get buy-in?

### Q6. Tell me about mentoring someone through a complex domain model.

- **Why asked:** Lead expectation.
- **Competencies:** Mentoring, communication.
- **Framework:** Diagnose gap → pairing on billing/time model → review checklist → independence outcome.
- **Follow-ups:** How did you measure growth? What still needed coaching?

### Q7. Describe a time you said no to a customization request.

- **Why asked:** Config vs. custom is existential in ERP.
- **Competencies:** Product mindset, backbone.
- **Framework:** Impact on upgradeability → propose configuration or integration extension point → align with PM → customer outcome.
- **Follow-ups:** Did the customer churn risk? What alternative shipped?

### Q8. Tell me about designing an integration with an external ERP/GL.

- **Why asked:** Integration hub reality.
- **Competencies:** API design, reliability.
- **Framework:** Contract + idempotency → outbox → mapping → reconciliation → ops dashboard.
- **Follow-ups:** How did you handle partial failures? Schema evolution?

### Q9. Share a time permissions caused a production bug.

- **Why asked:** Project-level security is easy to get wrong.
- **Competencies:** Security, quality.
- **Framework:** Bug → blast radius → hotfix → systematic permission tests → design fix.
- **Follow-ups:** Did any cross-project data leak? How do you test ACLs now?

### Q10. Describe leading a design review that changed the approach.

- **Why asked:** Technical leadership without authority theater.
- **Competencies:** Influence, architecture.
- **Framework:** Risk you spotted (tenant/reporting) → evidence → alternative → decision recorded.
- **Follow-ups:** What would you still revisit? How did the team react?

### Q11. Tell me about improving performance of a heavy report or portfolio query.

- **Why asked:** Reporting is a daily trust surface.
- **Competencies:** Performance, data modeling.
- **Framework:** Measure → find hotspot → index/materialize/async → validate numbers still match → monitor.
- **Follow-ups:** Did you sacrifice freshness? How did finance react?

### Q12. Give an example of working with Professional Services / implementation teams.

- **Why asked:** B2B delivery ecosystem.
- **Competencies:** Collaboration, API usability.
- **Framework:** Pain from PS → API/docs fix → fewer tickets → faster implementations.
- **Follow-ups:** What documentation changed? What telemetry did you add?

### Q13. Tell me about a disagreement with an architect or principal engineer.

- **Why asked:** Senior conflict handling.
- **Competencies:** Humility, rigor.
- **Framework:** Steelman their view → data/spike → disagree-and-commit or escalate with options.
- **Follow-ups:** What did you learn? Would you decide differently now?

### Q14. Describe owning on-call for a SaaS product.

- **Why asked:** Operational maturity.
- **Competencies:** Ownership, communication.
- **Framework:** Alert quality → runbooks → noisy alert reduction → customer comms standards.
- **Follow-ups:** MTTF/MTTR improvements? What alert did you delete?

### Q15. Tell me about introducing testing standards to a legacy area.

- **Why asked:** Legacy ERP codebases.
- **Competencies:** Quality leadership.
- **Framework:** Characterize risk hotspots → characterization tests → CI gate → cultural adoption.
- **Follow-ups:** How did you avoid boiling the ocean?

### Q16. Share a time you had to explain a complex technical constraint to executives or customers.

- **Why asked:** Lead communication.
- **Competencies:** Clarity, trust-building.
- **Framework:** Business impact first → options with risk → recommendation → ask for decision.
- **Follow-ups:** What analogy worked? What did they decide?

### Q17. Describe a feature flag / cohort rollout strategy you ran.

- **Why asked:** Tenant-safe delivery.
- **Competencies:** Release engineering.
- **Framework:** Cohorts → success metrics → kill switch → full rollout → cleanup flag debt.
- **Follow-ups:** How long did flags live? Any tenant stuck on old path?

### Q18. Tell me about handling PII or sensitive employee/project data.

- **Why asked:** Enterprise trust and compliance.
- **Competencies:** Security, privacy.
- **Framework:** Classification → access control → encryption → audit → retention.
- **Follow-ups:** How do support engineers access data? Any redaction tooling?

### Q19. Give an example of diagnosing a Heisenbug in concurrent timesheet or booking logic.

- **Why asked:** Concurrency in resource/time domains.
- **Competencies:** Debugging depth.
- **Framework:** Repro under load → race hypothesis → locking/versioning fix → stress test.
- **Follow-ups:** Optimistic vs. pessimistic locking choice?

### Q20. Tell me about balancing AI/innovation work with core platform reliability.

- **Why asked:** Dela/Harmony era priorities.
- **Competencies:** Judgment, prioritization.
- **Framework:** Guardrails for AI → reliability SLO budget → staged delivery → measure adoption without risking close.
- **Follow-ups:** What AI idea did you reject and why?

### Q21. Describe a time you improved developer experience or build times.

- **Why asked:** Productivity in large monorepos/legacy.
- **Competencies:** Platform thinking.
- **Framework:** Baseline → bottleneck → incremental fix → adoption metrics.
- **Follow-ups:** How did you fund the work?

### Q22. Tell me about delivering under a hard external deadline (compliance, contract, release train).

- **Why asked:** Enterprise release discipline.
- **Competencies:** Delivery leadership.
- **Framework:** Cut scope ruthlessly → risk list → parallel workstreams → war room → retro.
- **Follow-ups:** What quality did you refuse to cut?

### Q23. Share how you onboarded onto a complex legacy codebase.

- **Why asked:** Realistic Deltek day-one.
- **Competencies:** Learning agility.
- **Framework:** Map domain flows → find runtime paths → small safe fix → expand ownership.
- **Follow-ups:** How long to first production change?

### Q24. Tell me about a time customer support escalations revealed an engineering gap.

- **Why asked:** Closed-loop quality.
- **Competencies:** Empathy, systems thinking.
- **Framework:** Pattern in tickets → root cause → product fix → knowledge base → ticket volume drop.
- **Follow-ups:** Did you change logging/metrics?

### Q25. Describe influencing a cross-team roadmap.

- **Why asked:** Lead-level impact.
- **Competencies:** Influence without authority.
- **Framework:** Shared pain → joint RFC → sequenced milestones → shared KPIs.
- **Follow-ups:** What stalled and how did you unblock?

### Q26. Tell me about a security review you initiated or led.

- **Why asked:** Enterprise SaaS security bar.
- **Competencies:** Security leadership.
- **Framework:** Threat model → findings → remediations → verification → prevented class of bugs.
- **Follow-ups:** AuthZ vs. AuthN issues?

### Q27. Give an example of designing for auditability.

- **Why asked:** GovCon/professional services audit trails.
- **Competencies:** Compliance-aware design.
- **Framework:** Event capture → immutable audit store → query UX for auditors → performance constraints.
- **Follow-ups:** How do you prevent audit gaps on bulk updates?

### Q28. Tell me about managing a struggling project or slipping milestone.

- **Why asked:** Delivery ownership.
- **Competencies:** Honesty, replanning.
- **Framework:** Early signal → re-estimate → stakeholder reset → recovery plan → outcome.
- **Follow-ups:** What leading indicators do you watch now?

### Q29. Describe a time you advocated for observability.

- **Why asked:** Supportability of SaaS.
- **Competencies:** Operability.
- **Framework:** Blind incident → golden signals per tenant → dashboards/alerts → faster diagnosis.
- **Follow-ups:** Cardinality issues with tenant labels?

### Q30. Tell me about hiring or interviewing engineers — what do you select for?

- **Why asked:** Lead bar for team building.
- **Competencies:** Talent judgment.
- **Framework:** Signals you use (ownership stories, tradeoff talk) → bias checks → outcome of hire.
- **Follow-ups:** Who did you pass on and why?

### Q31. Share a time you had to deprecate an API used by customers.

- **Why asked:** B2B platform maturity.
- **Competencies:** Platform empathy, communication.
- **Framework:** Usage analytics → deprecation policy → dual-run → partner outreach → sunset.
- **Follow-ups:** How long was the deprecation window?

### Q32. Tell me about making a build-vs-buy decision for a platform capability.

- **Why asked:** Architectural judgment.
- **Competencies:** Economic reasoning.
- **Framework:** Requirements → TCO → risk → decision → revisit criteria.
- **Follow-ups:** When would you reverse it?

---

## 08 - System Design Questions

### Scenario A — Multi-Tenant Project Portfolio Service

**Requirements:**  
Tenants manage thousands of projects with hierarchies, assignments, statuses, and portfolio dashboards. Strict tenant isolation. Project-level permissions. Soft real-time updates for PM views; heavier analytics can be async. Audit of structural changes.

**Architecture Discussion:**  
API gateway with tenant auth → Project service (Spring Boot) → relational primary store with `tenant_id` (+ optional RLS) → cache for hot project summaries → async projector to read models for portfolio boards → audit event stream. Consider schema-per-tenant for strategic accounts.

**Tradeoffs:**  
Shared schema (ops simple, isolation risk) vs. silo DB (strong isolation, ops cost). Sync portfolio queries vs. eventual read models.

**Scaling:**  
Partition hot tenants; read replicas for portfolio lists; pagination and cursor APIs; limit deep hierarchy fan-out.

**Reliability:**  
Idempotent project create; optimistic locking on project aggregates; backup/restore per isolation model.

**Security:**  
Tenant context mandatory; deny-by-default project ACL; admin impersonation fully audited.

**Production Considerations:**  
Migration of hierarchy model; backfill of permissions; feature flags; per-tenant SLO dashboards; support tooling to inspect one tenant safely.

---

### Scenario B — Timesheet Ingestion at Peak

**Requirements:**  
Workers submit timesheets (web/mobile/import). Monday peaks. Approvals workflow. Feeds billing and utilization. Must not lose entries; duplicates dangerous; period locks block edits.

**Architecture Discussion:**  
Ingest API → validate worker/project/charge code → write timesheet entries in transaction → outbox event → aggregation service for utilization → notification for approvals. Bulk CSV ingest via object storage + worker fleet. Idempotency keys per entry/import batch.

**Tradeoffs:**  
Sync validation richness vs. ingest latency; immediate aggregation vs. nightly jobs; row-level lock vs. conflict errors to client.

**Scaling:**  
Queue bursts; autoscale ingest workers; shard by tenant; backpressure when approval DB saturates.

**Reliability:**  
Exactly-once effects via idempotent keys; DLQ for poison imports; replay tools; reconcile totals vs. source files.

**Security:**  
Workers only see assignable projects; managers approval scope; PII minimization in logs.

**Production Considerations:**  
Period close calendar; clock-skew; offline mobile sync conflicts; observability on submit error rates by tenant; runbooks for “stuck approval.”

---

### Scenario C — Document Storage with Project Permissions

**Requirements:**  
Store contracts, drawings, deliverables. Permission inherits from project + explicit shares. Versioning, retention, virus scan, search metadata. Large files; global customers.

**Architecture Discussion:**  
Metadata service (SQL) + object storage (S3/OCI). Upload via pre-signed URLs. Async malware scan. ACL evaluation service cached. Search indexer consumes metadata events. Encryption at rest with tenant-aware keys if required.

**Tradeoffs:**  
Pre-signed upload (scale) vs. streamed through API (inspection control). Inheritance ACL complexity vs. flat ACL simplicity.

**Scaling:**  
CDN for downloads where allowed; multipart upload; separate hot metadata DB from blob store.

**Reliability:**  
Orphan blob GC; scan failure quarantine; version immutability for audit.

**Security:**  
Check AuthZ on every download token mint; short-lived URLs; DLP considerations; legal hold blocks delete.

**Production Considerations:**  
Cross-region replication; customer data residency; support “restore deleted file”; cost controls on version churn.

---

### Scenario D — Integration Hub with External ERP Systems

**Requirements:**  
Bi-directional sync of customers, projects, invoices, payments with external GL/ERP. Customer-specific field mappings. Retries, monitoring, reconciliation. No silent drift.

**Architecture Discussion:**  
Integration service + connector SDK. Outbox on domain changes → mapper → destination adapter. Inbound webhooks → canonical model → domain APIs. Mapping DSL/config per tenant. Reconciliation job compares balances/counts and opens tickets.

**Tradeoffs:**  
iPaaS buy vs. build; point-to-point vs. hub; strict schemas vs. flexible maps.

**Scaling:**  
Partition by tenant; rate-limit per destination; bulk vs. incremental sync windows.

**Reliability:**  
Idempotent upserts; DLQ; poison message quarantine; compensating transactions where needed; human approval for financial posts optional.

**Security:**  
Secrets per connector; least-privilege OAuth; payload encryption; audit of mapping changes.

**Production Considerations:**  
Versioned canonical model; contract tests per connector; customer-visible sync health dashboard; break-glass disable sync per tenant.

---

### Scenario E — Reporting & Analytics Pipeline for Project Margins

**Requirements:**  
Near-real-time operational reports + deep analytics for margin, utilization, forecast. Numbers must reconcile to transactional system within defined lag. Permission-trimmed.

**Architecture Discussion:**  
CDC/outbox from OLTP → streaming pipeline → warehouse/lakehouse → semantic layer → report API. Operational “fast path” materialized views for common PM reports. Snapshot for month-end locked reports.

**Tradeoffs:**  
Warehouse freshness vs. cost; precomputed cubes vs. flexible query; sync SQL reports vs. BI tool.

**Scaling:**  
Separate analytical compute; tenant workload isolation; result caching with ACL keys.

**Reliability:**  
Pipeline lag alerts; reconciliation checksums; replay from CDC checkpoints.

**Security:**  
Row-level security in semantic layer; forbid raw warehouse access for app users.

**Production Considerations:**  
Month-end freeze semantics; explainability when AI summarizes margins; backfill after schema change.

---

### Scenario F — Safe Major Version Upgrade Across Thousands of Tenants

**Requirements:**  
Ship breaking domain model change (e.g., new WBS structure). Zero data loss. Cohort rollout. Instant kill switch. Supportability.

**Architecture Discussion:**  
Expand schema → dual-write dual-read → tenant backfill workers with checkpointing → correctness validators → switch read path → contract old columns later. Control plane tracks tenant state machine.

**Tradeoffs:**  
Long dual-write complexity vs. big-bang risk; per-tenant silo upgrades vs. shared fleet.

**Scaling:**  
Backfill rate limited; priority for enterprise tenants; pause on error budget burn.

**Reliability:**  
Automated validation sampling; shadow reads comparing old/new; rollback to old read path.

**Security:**  
Backfill jobs use service identity with tight scope; audit upgrade actions.

**Production Considerations:**  
Customer communication; PS checklist; feature compatibility matrix; metrics: % tenants migrated, error rate, support ticket delta.

---

## 09 - Company Preparation Checklist

- [ ] Can explain Deltek’s project-based ERP positioning in 60 seconds
- [ ] Named products: Vantagepoint, Maconomy, Costpoint, PPM adjacencies, Harmony/Dela
- [ ] Prepared 2 multi-tenant isolation stories
- [ ] Prepared 1 upgrade/migration safety story
- [ ] Prepared 1 integration + reconciliation story
- [ ] Prepared 1 reporting performance / correctness story
- [ ] Prepared 1 permissions / AuthZ bug or design story
- [ ] Prepared leadership stories: mentoring, conflict, debt, incident
- [ ] Reviewed Java/Spring transaction + JPA pitfalls
- [ ] Drilled 3 system designs from Section 08 aloud
- [ ] Listed 8 intelligent questions for HM/architects
- [ ] Mapped resume bullets to SaaS ERP language (not consumer fluff)
- [ ] Understood B2B release/UAT/partner constraints
- [ ] Skimmed recent Deltek product/AI direction (Dela, Harmony) at high level

---

## 10 - How My Experience Maps

Fill before interviews:

| Deltek expectation | My evidence (project, metric, decision) | Gap / plan |
|--------------------|-----------------------------------------|------------|
| Multi-tenant safety | | |
| Complex relational domain | | |
| Upgrade/migration ownership | | |
| Integrations & reconciliation | | |
| Reporting at scale | | |
| Permissions/audit | | |
| Mentoring & design leadership | | |
| Incident / on-call ownership | | |
| Stakeholder management (PM/Finance/PS) | | |
| Cloud + CI/CD maturity | | |

Narrative template:  
“In [system], I owned [domain]. The hard constraint was [tenant/period lock/audit]. I decided [X] over [Y] because [risk]. Result: [metric]. At Deltek I’d apply this to [product concern].”

---

## Interview Confidence Checklist

- [ ] I can whiteboard tenant isolation models and pick one with rationale
- [ ] I can design timesheet ingest with idempotency and period locks
- [ ] I can explain expand/contract migrations with dual-write
- [ ] I can discuss ACL inheritance for project documents
- [ ] I can design an integration hub with reconciliation
- [ ] I have 6 STAR stories memorized with metrics
- [ ] I can challenge a bad microservice proposal diplomatically
- [ ] I can discuss AI features without ignoring tenancy/audit
- [ ] I sound like a product engineer, not a ticket-taker
- [ ] My questions to them show enterprise SaaS literacy

---

## Mock Interview Preparation Checklist

- [ ] 45-min system design: portfolio service or timesheet ingest
- [ ] 45-min system design: integration hub or upgrade control plane
- [ ] 60-min behavioral: Q1–Q10 recorded, self-critiqued
- [ ] 45-min coding: aggregation + validation + tests in Java
- [ ] Peer review: “Would this answer work at an ERP company?”
- [ ] HM mock: roadmap prioritization with upgrade risk
- [ ] Feedback loop: rewrite weak STAR stories the same day

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | Company overview + map experience table |
| 2 | Multi-tenant + permissions deep dive; stories |
| 3 | Migrations/upgrades; practice Scenario F |
| 4 | Integrations + reconciliation; Scenario D |
| 5 | Timesheets/reporting; Scenarios B & E |
| 6 | Leadership/behavioral Q1–Q16 |
| 7 | Behavioral Q17–Q32 + polish |
| 8 | Full mock loop (design + behavioral) |
| 9 | Gap fix from mock; HM questions |
| 10 | Light review; sleep; logistics |

---

## Estimated Preparation Time

| Profile | Hours |
|---------|------:|
| Strong SaaS + Java, weak ERP domain | 25–35 |
| Strong ERP/domain, weak system design storytelling | 20–30 |
| Strong overall, need Deltek-specific polish | 12–18 |
| Lead/Architect bar (architecture + leadership depth) | 30–40 |

Minimum viable: **3 focused days** if you already completed core playbook modules — but expect thinner margins on upgrade and tenancy follow-ups.

**Target for Lead SE:** ~**28 hours** across 10 days with two full mocks.
