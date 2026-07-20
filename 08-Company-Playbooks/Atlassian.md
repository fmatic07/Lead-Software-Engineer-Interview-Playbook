# Atlassian

> Reverse-engineer how Atlassian evaluates Senior/Lead engineers and Architects for multi-tenant SaaS collaboration platforms — Jira/Confluence-class products where permissions, search, reliability SLOs, and marketplace ecosystems are first-class constraints.

---

## 01 - Company Overview

### Industry

Atlassian builds **enterprise collaboration and software delivery SaaS**: issue tracking, documentation, CI/CD adjacency, ITSM, and team productivity. Primary commercial motion is B2B SaaS (Cloud), with remaining Server/Data Center customers and a large Cloud migration story.

Interview implication: you are evaluated as someone who can design **tenant-safe, permission-correct, searchable collaboration systems** at SaaS scale — not a consumer social app, not a throwaway CRUD service.

### Products

Know the product surface enough to map designs and talk shop:

| Surface | What interviewers hear when you mention it |
|---------|--------------------------------------------|
| **Jira** | Work items, workflows, custom fields, boards, permissions, scale |
| **Confluence** | Hierarchical content, collaboration, search, spaces, attachments |
| **Bitbucket / DevOps adjacency** | Repos, PRs, pipelines, developer workflow integration |
| **Jira Service Management** | ITSM, queues, SLAs, agent workflows |
| **Marketplace / Forge / Connect** | App ecosystem, tenancy isolation, lifecycle, billing adjacency |
| **Atlassian Admin / Identity** | Org, sites, users, groups, SSO/SCIM, product access |

Legal/product nuance that shows maturity: Cloud multi-tenancy, Data Center single-tenant-ish deployments, and Marketplace apps all have different trust boundaries — designs must respect them.

### Engineering Culture

Atlassian publicly emphasizes values around **teamwork, customer focus, open company / no BS, play**, and continuous improvement. Map these to interview signals without fluff:

| Value language | What panels actually probe |
|----------------|----------------------------|
| Teamwork | Cross-team RFCs, unblocking others, shared ownership of incidents |
| Customer focus | Support tickets → root cause → product fix; admin/power-user empathy |
| Open / no BS | Clear tradeoffs in design docs; disagree-and-commit without politics |
| Continuous improvement | Postmortems that change controls; measurable reliability work |
| Play / bias to action | Ship behind flags; iterate; don’t wait for perfect architecture |

Expect emphasis on:

- **Multi-tenant correctness** — one customer’s data never leaks; noisy neighbors contained.
- **Admin and power-user empathy** — permission models, audit logs, bulk ops matter.
- **Platform thinking** — shared services (identity, search, notifications, Forge) vs. product silos.
- **Reliability as product** — SLO language, error budgets, graceful degradation.
- **Ecosystem responsibility** — Marketplace apps can break customers; lifecycle discipline matters.

### Business Model

Subscription SaaS (Cloud seats/products) + Marketplace revenue share + enterprise deals (Data Center / enterprise Cloud). Engineering impact shows up as: Cloud retention, migration success, performance at large tenants, Marketplace trust, support cost reduction, feature adoption for admins and end users.

### Scale

Global SaaS: millions of users, large enterprise tenants with hundreds of thousands of issues/pages, bursty notification and search load, regional Cloud footprints. Hot paths: **issue/page read/write, search, permissions checks, notifications, webhooks to apps**. Cold paths still matter: exports, migrations, audit reports, reindex jobs.

### Global Presence

HQ Australia; engineering/product across AU, US, EU, India, and more. Interviewers care about **Cloud multi-region awareness**, data residency conversations, and async collaboration across time zones more than “build for one country.”

### Technology Direction

Themes to discuss soberly:

- Cloud-first product strategy and Server/Data Center → Cloud migration tooling.
- Platform consolidation (identity, experience, AI features as assistive layers).
- Marketplace modernization (Forge over legacy patterns where applicable).
- Reliability, performance, and large-tenant scale.
- Selective AI for search/summarization/automation — never as a substitute for permission truth.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

1. Treat every request as **tenant-scoped** — authorization before data access, always.
2. Design for **large tenants** (skewed data, hot projects/spaces) not average tenants.
3. Partner with Support/Success early — admin pain is product signal.
4. Ship behind feature flags with measurable SLOs and rollback plans.
5. Write designs that Marketplace/app teams and adjacent product teams can challenge.

### Ownership

- You own correctness in your domain: permissions, indexing lag, notification storms, migration edge cases.
- “Done” means: tenant isolation tests, observability, runbooks, admin-facing failure modes, and Support macros where needed.
- You can explain who can see what data and why — at issue, project, space, org, and app scopes.

### Technical Leadership

- Drive designs that make cross-tenant bugs and silent permission holes difficult.
- Mentor on multi-tenant pitfalls: cache keys, async jobs, search documents, webhook payloads.
- Raise the bar on design reviews for blast radius, backfill strategy, and ecosystem impact.

### Product Mindset

- Admins and end users have different jobs — optimize the right persona for the feature.
- Custom fields, workflows, and apps create combinatorial complexity — constrain wisely.
- Migration and reliability work are product work, not “infra side quests.”

### Collaboration Style

- Work with Product, Design, Support, Security, Marketplace/partner teams, SRE/reliability.
- Write RFCs with explicit non-goals and tenant-scale assumptions.
- Escalate ambiguity in permission semantics immediately — guessing creates CVEs and trust loss.

### Engineering Principles

| Principle | Interview signal |
|-----------|------------------|
| Tenant isolation | Every query/job/cache key carries tenant context; automated tests for leakage |
| Permission before data | AuthZ evaluated on read/write/search/notification paths |
| Eventual consistency with UX honesty | Index lag, webhook delay — show accurate user/admin messaging |
| Large-tenant readiness | Hot partitions, pagination, bulk APIs, rate limits |
| Ecosystem safety | App least privilege, lifecycle, deprecation, blast-radius controls |
| Observability | Trace across product → platform → search/notify; tenant-aware metrics |
| Operability | Reindex, repair, kill switches, support tooling with audit |

---

## 03 - Typical Technology Stack

Exact internal stack varies by product/team. Interview with **SaaS collaboration-credible defaults** and ask which apply.

### Java

Long-standing core language for many Jira/Confluence-class backends and platform services.

**Why Atlassian:** Mature ecosystem for long-lived enterprise products; strong typing for complex domain models (issues, schemas, permissions); large internal/external talent pool.

### Spring Boot / JVM services

Modern services, platform APIs, admin tooling, migration workers — Spring Boot or sibling JVM frameworks depending on team vintage.

**Why Atlassian:** Fast delivery of authenticated APIs with validation, metrics, and transactional boundaries; fits polyglot evolution around legacy cores.

### Cloud

Multi-region SaaS on major cloud providers: compute, managed DB, object storage, CDN, KMS, WAF, private networking between services.

**Why Atlassian:** Elasticity for tenant growth; regional data considerations; HA for always-on collaboration.

### Microservices / modular monoliths

Mix of product monolith heritage and extracted platform services (identity, notifications, search indexing, Forge runtime adjacency).

**Why Atlassian:** Independent scale of search/notify hot paths; controlled blast radius — but requires disciplined contracts and migration dual-writes.

### Databases

Relational primary for issues/pages/permissions/config (Postgres-class patterns). Secondary stores for search indexes, caches, object storage for attachments, warehouses for analytics.

**Why Atlassian:** Strong consistency for permission-critical metadata; search and analytics eventually consistent with repair paths.

### Messaging

Kafka/SQS-class buses for issue events, indexing, notifications, webhooks, audit sinks, migration pipelines.

**Why Atlassian:** Decouples write path from fanout; enables replay, backfill, and consumer scaling per tenant class.

### Search

Elasticsearch/OpenSearch-class indexing for work items and pages; custom analyzers; permission-filtered query pipelines.

**Why Atlassian:** Search is a primary UX; wrong authZ on search is a data leak.

### CI/CD

Mandatory pipelines: tests including tenancy/permission suites, SAST, dependency scanning, staged rollouts, canaries per region/shard.

**Why Atlassian:** Bad deploys become multi-tenant incidents; progressive delivery is table stakes.

### Kubernetes

Container orchestration for API fleets, indexers, notification workers; HPA; job controllers for reindex/migration.

**Why Atlassian:** Standard ops model for SaaS platforms; supports shard/cell styles of isolation.

### Infrastructure

IaC, secrets managers, service identity, centralized logging, feature flags, cell/shard routing, rate limiting at edge.

**Why Atlassian:** Auditability; least privilege; fast mitigation of noisy-neighbor and abuse cases.

### Frontend

React-heavy web clients; design system consistency; aggressive version skew tolerance for long-lived enterprise sessions.

**Why Atlassian:** Collaboration UX is web-first; APIs must tolerate old clients and bulk admin operations.

### AI / ML

Assistive features: summarization, search ranking hints, automation suggestions — **never** bypassing permissions or inventing access.

**Why Atlassian:** Useful for productivity; catastrophic if it surfaces unauthorized content.

### Atlassian-Specific Tech Awareness

- Permission schemes, roles, groups, nested spaces/projects, issue security levels.
- Custom fields / content properties and schema evolution.
- Webhooks, Connect/Forge app auth, installation lifecycle.
- Cloud migration: assess → migrate → validate → cutover → rollback.
- SLO/error budget language for SaaS reliability.

---

## 04 - Typical Interview Process

Loops vary by role and location; prepare for this SaaS-shaped process:

### Stage 1 — Recruiter Screen

**Purpose:** Motivation for Atlassian/collaboration SaaS, level calibration, logistics, team matching (product vs. platform vs. Marketplace).

**Evaluation Criteria:** Clear senior narrative; SaaS/platform relevance; communication; values alignment signals (teamwork, customer focus without slogans).

**Preparation Tips:** 90-second story emphasizing multi-tenant systems, reliability, or large-scale product platforms. Name Jira/Confluence-class problems you’ve solved by analogy (permissions, search, notifications, migrations).

**Common Mistakes:** Generic “I like startups”; no SaaS tenancy awareness; treating Atlassian as “just issue tracker CRUD.”

### Stage 2 — Technical Interview

**Purpose:** Language/framework depth (often Java), API design, data modeling, debugging, concurrency.

**Evaluation Criteria:** Correctness under failure; clarity; security/authZ hygiene; testing mindset for tenancy.

**Preparation Tips:** Refresh pagination, optimistic locking, cache invalidation, async job design, permission checks on every path.

**Common Mistakes:** Ignoring tenant context on jobs/caches; hand-waving search consistency; logging sensitive content.

### Stage 3 — Coding Assessment

**Purpose:** Practical coding — data structures, API endpoints, filtering/permissions stubs, sometimes take-home.

**Evaluation Criteria:** Edge cases, readability, tests, complexity awareness, clean interfaces.

**Preparation Tips:** Practice: filtered feed with ACL checks; event → fanout worker; paginated issue search stub; idempotent webhook handler.

**Common Mistakes:** O(n) permission checks in hot loops without design discussion; no tests for unauthorized access; mutable shared state.

### Stage 4 — System Design

**Purpose:** Multi-tenant SaaS designs: issue tracker core, notifications, search, permissions, Marketplace lifecycle.

**Evaluation Criteria:** Requirements clarity; tenancy model; authZ; scale for large tenants; operability; ecosystem impact.

**Preparation Tips:** Drill Section 08. Always state tenant isolation, permission model, and failure/UX for eventual consistency.

**Common Mistakes:** Designing Instagram; forgetting Marketplace blast radius; “just put it in Elasticsearch” without authZ; no reindex/repair story.

### Stage 5 — Leadership Interview

**Purpose:** Values in action — teamwork, customer focus, influence, mentoring, incident ownership.

**Evaluation Criteria:** Collaboration without theater; accountability; ability to disagree constructively; Support/customer empathy.

**Preparation Tips:** STAR stories where you unblocked another team, fixed admin pain from Support tickets, or led a multi-tenant incident calmly.

**Common Mistakes:** Solo-hero narratives; blaming “the platform team”; values buzzwords without concrete behavior.

### Stage 6 — Hiring Manager

**Purpose:** Team mission fit (Jira platform vs. Confluence vs. identity vs. Forge/Marketplace), roadmap, on-call, leveling.

**Evaluation Criteria:** Judgment, cross-functional fit, growth trajectory, ownership appetite.

**Preparation Tips:** Ask about largest tenant pain, SLO ownership, migration backlog, app ecosystem constraints, how RFCs get decided.

**Common Mistakes:** Not clarifying product vs. platform ownership; ignoring on-call/cell ownership realities.

### Stage 7 — Final Interview

**Purpose:** Bar raise, culture/values consistency, sometimes architecture or values deep dive with senior leaders.

**Evaluation Criteria:** Integrity, senior judgment, story consistency across loops, customer-centric tradeoffs.

**Preparation Tips:** Prepare questions on reliability investment, AI + permissions posture, Cloud migration strategy for your domain.

**Common Mistakes:** Inconsistent stance on security vs. speed across interviews; political answers about teamwork.

### Stage 8 — Offer

**Purpose:** Leveling, location/comp bands, scope of ownership.

**Evaluation Criteria:** Mutual clarity on domain and success metrics.

**Preparation Tips:** Confirm team charter, on-call, first-6-month outcomes (e.g., large-tenant performance, migration milestone, Marketplace reliability).

**Common Mistakes:** Accepting Lead title without clear technical authority or multi-team influence path.

---

## 05 - Technical Focus Areas

### Multi-Tenant SaaS (Jira/Confluence-Class)

- Tenant/site/org hierarchy; cell/shard routing; noisy-neighbor isolation.
- Per-tenant configuration: workflows, schemes, apps, feature flags.
- Data residency and regional routing awareness.
- Backup/restore, export, and support tooling that cannot cross tenants.

### Plugin / Marketplace Ecosystems

- App install/upgrade/uninstall lifecycle; scopes/permissions for apps.
- Connect vs. Forge-style trust models (at conceptual level).
- Webhooks and callbacks: authenticity, retries, tenant context.
- Deprecation and breaking-change communication to partners.
- Blast radius: one bad app vs. platform fault isolation.

### Search

- Index pipeline: write → outbox/event → indexer → searchable document.
- Permission-aware query: filter at index time vs. query time vs. hybrid.
- Mapping evolution, analyzers, custom fields in documents.
- Lag SLOs; partial results; repair/reindex for a tenant or project/space.
- Large-tenant indexing backlog and priority queues.

### Permissions

- Role-based + scheme-based + object-level (issue security, page restrictions).
- Group nesting, project/space roles, anonymous/public access edge cases.
- Caching of authZ decisions with safe invalidation.
- Search, notifications, and APIs must share one permission truth.
- Audit: who granted/changed access.

### Collaboration

- Concurrent edits, presence, comments, attachments.
- Watchers/followers; @mentions; activity streams.
- Consistency vs. latency tradeoffs for collaborative UX.
- Abuse: notification storms, mention spam, bulk edit.

### Cloud Migration

- Assessment (apps, customizations, data volume) → test migration → delta sync → cutover.
- Idempotent importers; validation reports; rollback/hold strategies.
- App compatibility gates; permission mapping differences Cloud vs. DC.
- Customer communication and Support enablement as part of engineering delivery.

### Reliability SLOs

- Availability and latency SLOs per critical journey (issue view, search, comment).
- Error budgets driving release freeze vs. feature work.
- Graceful degradation: search delayed, notifications deferred, reads from replica.
- Multi-region failover thinking; dependency failure modes (identity, mail, search cluster).

### Additional High-Yield Topics

- Rate limiting and bulk API design for admins/integrations.
- Webhook delivery at scale; DLQ; replay tooling.
- Attachment storage, virus scan, CDN, link permissions.
- Schema/custom field evolution without downtime.
- Observability: tenant-dimensional metrics without cardinality explosion.

---

## 06 - Leadership Focus

### Ownership

Own outcomes for your surface: permission bugs, index lag incidents, migration failures, Marketplace regressions — including Support partnership and lasting controls.

### Mentoring

Teach engineers to ask “who can see this?” and “what happens for a 1M-issue tenant?” in every design. Pair on tenancy tests and RFC writing.

### Decision Making

Prefer boring, isolatable designs for authZ and data boundaries. Innovate in UX and developer experience, not in permission bypasses or cross-tenant cleverness.

### Cross-Team Collaboration

Co-design with platform (identity, search, notifications), product siblings, Marketplace, Security, and Support. Explicit contracts beat tribal knowledge.

### Incident Response

SEV tied to customer blast radius (tenant count, data exposure risk, core journey down). Freeze risky deploys; communicate factually; verify tenant isolation before all-clear.

### Architecture Discussions

Challenge dual-writes without reconciliation; demand authZ on async paths; require backfill/reindex plans in RFCs that change searchable or permissioned data.

### Technical Debt

Prioritize debt that causes Support load, large-tenant outages, permission ambiguity, or migration blockers. Quantify with ticket volume, SLO burn, and churn risk.

### Engineering Culture

Blameless postmortems with mandatory test/guardrail improvements. Celebrate Support ticket extinction and reliability wins, not only feature launches. Embody teamwork by making other teams faster.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you fixed a cross-tenant or authorization boundary bug.

- **Why asked:** Multi-tenant correctness is sacred; values map to customer trust.
- **Competencies:** Security mindset, ownership, rigor.
- **Framework:** Detection → blast radius → stop bleed → fix + regression tests → customer/Support comms → process change.
- **Follow-ups:** How did it escape review? What automated guardrail did you add?

### Q2. Describe leading an incident that burned an SLO error budget.

- **Why asked:** Reliability is product language at SaaS companies.
- **Competencies:** Incident leadership, prioritization.
- **Framework:** Detect → mitigate → customer impact → root cause → error budget consequences → lasting fix.
- **Follow-ups:** What feature work did you defer? How did stakeholders react?

### Q3. Tell me about partnering with Support or Success on a recurring admin pain.

- **Why asked:** Customer focus without fluff — Support is a sensor.
- **Competencies:** Empathy, product thinking, delivery.
- **Framework:** Ticket pattern → root cause → fix or tooling → macro updates → ticket volume drop.
- **Follow-ups:** Did you change the product or only ops tooling? Why?

### Q4. Give an example of a design review where you challenged a cross-team RFC.

- **Why asked:** Open company / no BS — constructive disagreement.
- **Competencies:** Influence, architecture judgment, teamwork.
- **Framework:** Risk articulated with data → alternative → disagree-and-commit or redesign → relationship intact.
- **Follow-ups:** What happened after launch? Were you right?

### Q5. Tell me about shipping a feature behind flags for a large enterprise tenant cohort.

- **Why asked:** Large-tenant reality and safe rollout.
- **Competencies:** Release engineering, risk management.
- **Framework:** Cohort strategy → metrics → soak → expand → kill switch drill.
- **Follow-ups:** What tenant class failed first and why?

### Q6. Describe a time you improved search relevance or indexing lag.

- **Why asked:** Search is core Jira/Confluence UX.
- **Competencies:** Systems thinking, operability.
- **Framework:** Symptom (lag/wrong results) → pipeline diagnosis → fix → reindex/repair → SLO/metrics.
- **Follow-ups:** How did you handle permission filtering correctness?

### Q7. Tell me about designing or changing a permission model.

- **Why asked:** Permissions are Atlassian-hard.
- **Competencies:** Domain modeling, security, backward compatibility.
- **Framework:** Actors/resources/actions → edge cases → migration of existing grants → audit → Support education.
- **Follow-ups:** What broke for Marketplace apps or integrations?

### Q8. Share an example of working with a Marketplace app / plugin partner issue.

- **Why asked:** Ecosystem thinking.
- **Competencies:** External empathy, API stability, communication.
- **Framework:** Partner impact → root cause (app vs. platform) → mitigation → deprecation/comms → prevention.
- **Follow-ups:** How do you balance platform progress vs. partner breakage?

### Q9. Tell me about a Cloud migration or large data import you influenced.

- **Why asked:** Strategic company motion.
- **Competencies:** Migration discipline, customer empathy.
- **Framework:** Assess → dry run → validation report → delta → cutover → rollback criteria → outcome.
- **Follow-ups:** What customization or app blocked customers?

### Q10. Describe mentoring someone who didn’t think about tenancy or scale.

- **Why asked:** Lead/Staff teaching bar.
- **Competencies:** Mentoring, standards.
- **Framework:** Concrete bug → mental model → pairing → checklist in PR/RFC → independence.
- **Follow-ups:** How is that encoded in team Definition of Done?

### Q11. Tell me about a notification or email storm incident.

- **Why asked:** Collaboration systems create fanout disasters.
- **Competencies:** Debugging, rate control, UX judgment.
- **Framework:** Detect volume → circuit break → root cause (loop/bulk edit) → durable limits → customer messaging.
- **Follow-ups:** How do watchers/@mentions interact with bulk ops?

### Q12. Give an example of saying no to a feature that would weaken security or tenancy.

- **Why asked:** Backbone; customer trust over vanity scope.
- **Competencies:** Integrity, influence.
- **Framework:** Risk scenario → safer alternative → stakeholder alignment → delivered partial value safely.
- **Follow-ups:** Did pressure continue? How documented?

### Q13. Tell me about improving observability with tenant-aware metrics.

- **Why asked:** Debuggability without cardinality meltdown.
- **Competencies:** Operability, judgment.
- **Framework:** Blind spot → low-cardinality dimensions → exemplars/traces → alert quality → MTTD drop.
- **Follow-ups:** How do you find one bad tenant without a metric explosion?

### Q14. Describe a conflict with product on performance vs. deadline.

- **Why asked:** Values under schedule pressure.
- **Competencies:** Stakeholder management, tradeoffs.
- **Framework:** Evidence (load test/large tenant) → phased delivery → decision → result.
- **Follow-ups:** What was the launch gate metric?

### Q15. Tell me about a time you unblocked another team at your own short-term cost.

- **Why asked:** Teamwork signal that is behavioral, not slogan.
- **Competencies:** Collaboration, prioritization.
- **Framework:** Their blocker → your tradeoff → help delivered → shared outcome → reset priorities.
- **Follow-ups:** How did you prevent being a permanent bottleneck?

### Q16. Share a webhook / integration reliability improvement.

- **Why asked:** Automation and Marketplace depend on it.
- **Competencies:** Integration design, operability.
- **Framework:** At-least-once → signing → retries/backoff → DLQ → replay tooling → partner metrics.
- **Follow-ups:** How do admins diagnose “my automation didn’t fire”?

### Q17. Tell me about a performance incident on issue/page view for a huge tenant.

- **Why asked:** Skewed tenant scale is the real scale.
- **Competencies:** Performance, capacity, data modeling.
- **Framework:** Symptoms → hot keys/queries → mitigation → lasting fix → load test with realistic skew.
- **Follow-ups:** Did caching create permission stale reads? How handled?

### Q18. Describe introducing stronger code review or RFC standards.

- **Why asked:** Culture building for Lead roles.
- **Competencies:** Quality leadership.
- **Framework:** Incident-inspired checklist (tenancy, authZ, backfill) → adopt → measure escaped defects.
- **Follow-ups:** How did you avoid review theater?

### Q19. Tell me about a schema or custom-field migration in production.

- **Why asked:** Enterprise config is forever.
- **Competencies:** Migration safety, compatibility.
- **Framework:** Expand/contract → dual-read → backfill → validate → contract → rollback.
- **Follow-ups:** How did search documents stay consistent?

### Q20. Give an example of cross-team delivery (product + identity + notifications).

- **Why asked:** Platform/product reality at Atlassian.
- **Competencies:** Coordination, contracts.
- **Framework:** Sequence diagram → failure contracts → joint test plan → staged launch → shared dashboards.
- **Follow-ups:** What contract broke first in staging?

### Q21. Tell me about using kill switches during a production issue.

- **Why asked:** Mitigation speed for SaaS.
- **Competencies:** Release engineering, calm under pressure.
- **Framework:** Switch design → ownership → default safe → drill → incident use → cleanup.
- **Follow-ups:** Who can flip tenant-wide flags? Audit trail?

### Q22. Describe handling ambiguous consistency (UI showed X, search showed Y).

- **Why asked:** Eventual consistency honesty.
- **Competencies:** UX + systems judgment.
- **Framework:** Explain lag → user messaging → repair path → SLO for sync → monitoring.
- **Follow-ups:** When do you block the write vs. accept lag?

### Q23. Tell me about a security finding (IDOR, XSS, SSRF) you drove closed.

- **Why asked:** Secure SDLC maturity.
- **Competencies:** Security ownership.
- **Framework:** Severity → exploit path → patch → verification → regression → root process fix.
- **Follow-ups:** Did Marketplace apps amplify the issue?

### Q24. Share how you prioritized reliability debt against roadmap OKRs.

- **Why asked:** Lead judgment.
- **Competencies:** Prioritization, communication.
- **Framework:** Risk-ranked debt → tie to SLO/Support/churn → negotiate capacity → before/after.
- **Follow-ups:** What did you consciously defer?

### Q25. Tell me about an architectural decision you later reversed.

- **Why asked:** Humility; learning culture.
- **Competencies:** Reflection, adaptability.
- **Framework:** Original bet → disconfirming signal → migration off → lesson in standards.
- **Follow-ups:** What early indicator did you miss?

### Q26. Describe coaching a team through noisy on-call.

- **Why asked:** Sustainable operations.
- **Competencies:** People leadership, SRE basics.
- **Framework:** Alert audit → SLO-based paging → runbooks → rotation health.
- **Follow-ups:** What % of pages became actionable?

### Q27. Tell me about designing bulk admin APIs safely.

- **Why asked:** Admin power tools are load and abuse vectors.
- **Competencies:** API design, rate limits, authZ.
- **Framework:** AuthZ → async jobs → progress UX → limits → audit → Support tooling.
- **Follow-ups:** How do you prevent one admin from melting a cell?

### Q28. Give an example of improving developer experience for internal or external APIs.

- **Why asked:** Platform/Marketplace adjacency.
- **Competencies:** Empathy, API design.
- **Framework:** Pain → clearer contracts/errors → versioning → adoption/support deflection.
- **Follow-ups:** How do you version without stranding apps?

### Q29. Tell me about influencing standards across multiple squads.

- **Why asked:** Staff/Lead multi-team impact.
- **Competencies:** Influence without authority.
- **Framework:** Shared incident theme → guild/RFC → reference implementation → adoption metrics.
- **Follow-ups:** Who resisted and how did you address it?

### Q30. Describe delivering bad news about a delay tied to security or migration readiness.

- **Why asked:** Integrity under business pressure; customer focus.
- **Competencies:** Communication, ethics.
- **Framework:** Early warning → options → recommend → align → protect customers.
- **Follow-ups:** How did you maintain trust afterward?

### Q31. Tell me about a time AI/automation features interacted with permissions.

- **Why asked:** Modern Atlassian direction; sharp edges.
- **Competencies:** Security, product judgment.
- **Framework:** Threat model → permission-bound retrieval → eval set including negative authZ cases → launch gates.
- **Follow-ups:** How do you test that summaries never include unauthorized pages/issues?

### Q32. Share a story where “play” or experimentation paid off — with guardrails.

- **Why asked:** Values balance: bias to action without recklessness.
- **Competencies:** Innovation, risk control.
- **Framework:** Hypothesis → flag → limited cohort → learn → scale or kill.
- **Follow-ups:** What did you kill, and how did you celebrate learning?

### Q33. Tell me about resolving a disagreement between engineering teams on ownership boundaries.

- **Why asked:** Teamwork and platform clarity.
- **Competencies:** Conflict resolution, architecture.
- **Framework:** Customer journey map → ownership RACI → interface contract → shared SLO → follow-through.
- **Follow-ups:** What still falls through the cracks?

### Q34. Describe a time you reduced Support escalations with better admin diagnostics.

- **Why asked:** Customer focus operationalized.
- **Competencies:** Operability, empathy.
- **Framework:** Top escalations → missing signals → admin/Support UI or logs → training → volume drop.
- **Follow-ups:** What privacy/audit constraints applied to diagnostic tools?

### Q35. Tell me about preparing a design for “cell” or shard isolation.

- **Why asked:** Modern SaaS isolation patterns.
- **Competencies:** Distributed systems, tenancy.
- **Framework:** Routing → data placement → failure domains → migration between cells → operational playbooks.
- **Follow-ups:** How do cross-cell features (org-level) work?

---

## 08 - System Design

### Scenario 1 — Issue Tracker Core (Jira-Class Work Items)

**Prompt:** Design the core create/update/read path for work items in a multi-tenant Cloud issue tracker.

**Clarify:** Tenant/site model; custom fields; workflows; permissions; scale (issues per tenant); consistency needs; API + UI clients; audit requirements.

**Requirements:**
- Functional: CRUD work items, transitions, comments, assignees, custom fields, projects.
- Non-functional: p99 latency targets, strong authZ, tenant isolation, high write bursts (imports), auditability.

**High-level design:**
- Edge: authN (session/OAuth), rate limits, tenant routing to cell.
- Issue service: validates project scheme, field config, workflow transition rules.
- Primary store: relational tables partitioned/keyed by tenant + project; immutable change log / history events.
- Caches: project config and permission snapshots with versioning.
- Async: emit domain events for search, webhooks, notifications.

**Data model sketch:** `Tenant`, `Project`, `Issue`, `IssueFields`, `WorkflowState`, `Comment`, `ChangeHistory`, `PermissionGrant`.

**Permission evaluation:** On write: can create/edit/transition in project; field-level security if applicable. On read: project role + issue security level.

**Large-tenant tactics:** Pagination everywhere; avoid unbounded watchers queries; bulk import via async job with progress; hotspot projects get careful indexing.

**Failure modes:** Partial comment save vs. issue update — transactional boundary; workflow validator failures; config cache stale → version checks.

**Observability:** Latency by operation; transition error rates; tenant saturation; import job lag.

**Tradeoffs:** Monolith module vs. extracted issue service; sync vs. async history; JSON custom fields vs. typed tables.

**What interviewers push:** “How do you prevent tenant A from reading tenant B?” “What happens during a 10M issue import?” “How do custom fields affect schema?”

---

### Scenario 2 — Notification Fanout

**Prompt:** Design notifications for issue events (email, in-app, webhooks) without melting the platform during bulk edits.

**Clarify:** Channels; recipient rules (watchers, assignees, @mentions, project roles); delivery SLOs; digests; user prefs; localization.

**Requirements:**
- At-least-once delivery with dedupe; user preference respect; storm control; tenant fairness.

**Design:**
- Event bus from issue service (`IssueUpdated`).
- Fanout planner: expands recipients, applies prefs and permission checks (“can this user still see the issue?”).
- Per-channel queues; worker pools with per-tenant rate limits.
- Aggregation window for bulk edits (digest).
- Template rendering service; attachment/link rewriting.
- Webhook dispatcher with signed payloads, retries, DLQ.

**Permission critical path:** Never notify a user who lost access; re-check at send time for sensitive events.

**Storm controls:** Cap recipients per event; detect bulk change storms; circuit-break email; prioritize assignee-critical events.

**Storage:** Notification outbox, delivery attempts, user notification feed, webhook subscription config.

**Tradeoffs:** Push every event vs. digest; sync fanout vs. staged expansion; email reliability vs. cost.

**What interviewers push:** “Bulk edit 50k issues — what happens?” “How do @mentions interact with restricted issues?”

---

### Scenario 3 — Search Indexing for Work Items

**Prompt:** Design search so users only find issues they can view, with acceptable index lag.

**Clarify:** Query types (JQL-like), ranking needs, custom fields, attachments/comments searchable?, lag SLO, multi-region.

**Design:**
- Write path: issue commit → outbox → indexer consumers → search documents in Elasticsearch/OpenSearch-class cluster.
- Document contents: searchable fields + **permission tokens** (project + security level + restrictions) for filter-efficient queries.
- Query path: parse query → authZ context (user groups/roles) → filter clause intersection → fetch → optional hydrate from primary store.
- Repair: per-tenant reindex; checksum jobs comparing primary vs. index; dead-letter for poison documents.

**Consistency:** Show “updated recently, search may lag” for near-real-time UX; strong read after write optional via primary fallback for known keys.

**Scale:** Separate hot tenant index shards; priority lanes; backpressure when lag exceeds SLO; reject pathological queries.

**Security:** Index-time filters insufficient alone if tokens wrong — test suites for negative authZ; never use search as sole authZ for deep links without revalidation.

**Tradeoffs:** Denormalize permissions into index vs. query-time join; one index per tenant vs. shared with routing.

**What interviewers push:** “User removed from group — how fast do results disappear?” “Custom field added — mapping evolution?”

---

### Scenario 4 — Permission Evaluation at Scale

**Prompt:** Design a permission evaluation service used by issue view, search, notifications, and APIs.

**Clarify:** Grant types (product access, project roles, issue security, space permissions analogy); group nesting depth; caching; consistency when membership changes.

**Design:**
- Central Policy Decision Point (PDP) API: `allowed(user, action, resource)`.
- Policy sources: product entitlements, project role membership, issue security, hierarchical restrictions.
- Group expansion service with cycle detection; bounded depth.
- Cache: decision cache keyed by `(userVersion, resourceVersion, action)`; membership version clock.
- Invalidation: SCIM/group change → bump userVersion; project role change → bump resourceVersion.
- Batch API for search/notification planners.

**Performance:** Avoid N+1; batch evaluate; precompute project-level permissions; push issue-level checks only when security levels exist.

**Correctness:** Fail closed on cache uncertainty for sensitive actions; fail soft only for non-sensitive with explicit product decision.

**Audit:** Log grant changes; break-glass admin access with reason codes.

**Tradeoffs:** Central PDP vs. library embedded in services; cache TTL vs. immediate revoke.

**What interviewers push:** “SSO group removed — revoke latency?” “How do anonymous project browse perms work?”

---

### Scenario 5 — Marketplace App Lifecycle

**Prompt:** Design install, upgrade, uninstall, and runtime invocation for Cloud apps that extend Jira/Confluence.

**Clarify:** Trust model (cloud app vs. forge-like runtime); scopes; billing adjacency optional; data storage by app; webhooks.

**Design:**
- App descriptor registry: scopes, endpoints, lifecycle callbacks.
- Install flow: admin consent → grant scopes → store installation credentials → enable → callback to app.
- Runtime: authenticated app calls into product APIs with installation identity; product calls out via webhooks/functions with signing.
- Upgrade: new scopes require re-consent; migrate secrets; compatibility checks.
- Uninstall: revoke credentials; disable webhooks; schedule data deletion per policy; notify app.
- Isolation: rate limits per installation; abuse detection; circuit break outbound calls.

**Security:** Least privilege scopes; rotate shared secrets; prevent confused-deputy; tenant binding on every call.

**Operability:** Admin UI for app health; delivery logs; force-disable; Support tooling.

**Tradeoffs:** Sync lifecycle vs. async; platform-hosted runtime vs. vendor-hosted; shared DB tables vs. app-owned storage.

**What interviewers push:** “App compromised — how do you contain?” “Breaking API change — partner communication?”

---

### Scenario 6 — (Bonus) Attachment Storage & Permissioned Links

**Prompt:** Design attachment upload/download for issues/pages with virus scanning and permissioned URLs.

**Sketch:** Direct-to-object-storage upload with tenant-prefixed keys; async malware scan gate; download via short-lived signed URLs issued only after authZ; CDN optional with signed cookies; quarantine bucket; retention/legal hold.

**Push topics:** Link sharing vs. issue perms; scan latency UX; ransomware-scale upload abuse.

---

## 09 - Company Preparation Checklist

- [ ] Read Atlassian careers page for your target team; note product vs. platform vs. Marketplace
- [ ] Skim Atlassian engineering blog posts on scale, reliability, Cloud, or Marketplace
- [ ] Refresh Jira/Confluence admin concepts: schemes, roles, workflows, spaces, apps
- [ ] Prepare 2 multi-tenant / authZ stories with metrics
- [ ] Prepare 1 reliability/SLO or incident story
- [ ] Prepare 1 Support/customer-driven improvement story
- [ ] Prepare 1 cross-team RFC/influence story (teamwork signal)
- [ ] Drill system designs: issue core, notification fanout, search+authZ, permissions PDP, app lifecycle
- [ ] Practice explaining eventual consistency to a PM (search lag, webhook delay)
- [ ] List 8 sharp questions for HM (SLOs, large tenants, migration, on-call, AI+perms)
- [ ] Align leveling language: Senior vs. Lead vs. Principal expectations for scope
- [ ] Review resume bullets for tenancy, permissions, search, migrations, SaaS reliability keywords

---

## 10 - How My Experience Maps

Fill before the loop. Prefer production evidence over aspirations.

| Atlassian signal | My evidence (system, decision, metric) | Gap / how I’ll speak to it |
|------------------|----------------------------------------|----------------------------|
| Multi-tenant isolation | | |
| Permission / authZ design | | |
| Search or indexing pipeline | | |
| Notification / event fanout | | |
| Large-tenant performance | | |
| Cloud migration / data import | | |
| Marketplace / plugin / partner API | | |
| SLO / error budget / incident leadership | | |
| Support-driven product fix | | |
| Cross-team RFC / platform collaboration | | |
| Mentoring on security or scale | | |
| Feature flags / progressive delivery | | |

**Narrative bridge examples:**
- Banking/fintech authZ → issue/page permission evaluation (fail closed, audit).
- E-commerce search → permission-filtered work item search (lag SLOs, reindex).
- Telecom notification platforms → watcher fanout with storm controls.
- Enterprise SaaS admin tools → bulk APIs, audit logs, Support diagnostics.

---

## Interview Confidence Checklist

- [ ] I can explain tenant isolation on sync and async paths in under 2 minutes
- [ ] I can whiteboard permission evaluation with cache invalidation
- [ ] I can design search that cannot leak unauthorized issues/pages
- [ ] I can describe notification storm mitigation
- [ ] I have 5 STAR stories mapped to Atlassian values-as-behaviors
- [ ] I can discuss Cloud migration stages without hand-waving
- [ ] I can articulate Marketplace blast-radius controls
- [ ] I know my first-6-month value prop for the target team
- [ ] I have intelligent questions that prove SaaS literacy
- [ ] I can stay calm when pushed on authZ edge cases

---

## Mock Interview Preparation Checklist

- [ ] Mock recruiter: 90-second Atlassian-shaped pitch
- [ ] Mock coding: ACL-filtered list + tests for unauthorized access
- [ ] Mock system design #1: issue tracker core (45–60 min)
- [ ] Mock system design #2: search indexing + permissions (45–60 min)
- [ ] Mock leadership/values: teamwork + customer focus stories under follow-ups
- [ ] Mock HM: questions on SLO ownership and large-tenant pain
- [ ] Record one design session; fix clarity of tenancy diagram
- [ ] Peer review STAR stories for “heroics” vs. teamwork balance
- [ ] Red-team your search design for IDOR-style leaks
- [ ] Timeboxed revision of Java concurrency + API pagination (if coding loop)

---

## Suggested Revision Plan

| Day | Focus | Exit criteria |
|-----|-------|---------------|
| 1 | Company + values→signals mapping; story selection | 8 STAR stories tagged to Atlassian signals |
| 2 | Permissions + tenancy deep dive | Whiteboard PDP + invalidation cold |
| 3 | Search + notifications designs | Two full designs with failure modes |
| 4 | Marketplace lifecycle + migration | Can teach install/upgrade/uninstall + cutover |
| 5 | Reliability/SLO + incident story polish | Error budget narrative crisp |
| 6 | Coding kata + authZ tests | Clean solution + negative tests |
| 7 | Full mock loop + gap fill | Weak areas documented and restudied |

Compress to 3 days if needed: Day A stories+tenancy, Day B search+notify+permissions designs, Day C mock+gaps.

---

## Estimated Preparation Time

| Track | Hours |
|-------|-------|
| Company research + narrative mapping | 3–4 |
| Behavioral/values story polishing (30+ Q familiarity) | 6–8 |
| Technical refresh (Java/API/tenancy patterns) | 4–6 |
| System design drills (5 scenarios × 1–1.5 hr) | 8–10 |
| Mocks + feedback incorporation | 4–6 |
| **Total** | **~25–34 hours** |

For Lead/Architect loops, add 4–6 hours on multi-team RFCs, cell architecture, and ecosystem strategy narratives.

---

*Use this playbook to reverse-engineer the panel — then fill Section 10 with your production evidence until every claim is defensible under follow-up.*
