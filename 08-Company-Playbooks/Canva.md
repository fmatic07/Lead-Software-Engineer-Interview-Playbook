# Canva

> Reverse-engineer how Canva evaluates Lead/Senior engineers for a design platform at massive scale — real-time collaboration, media pipelines, editor performance, CDN/transforms, and AI features where reliability and craft both raise the bar.

---

## 01 - Company Overview

### Industry

Canva is a **global visual communication / design SaaS** platform: browser-based (and mobile) creation of presentations, social graphics, videos, docs, whiteboards, and related design artifacts. Founded in Sydney; large engineering presence across Sydney and other hubs (e.g., US, Europe, APAC).

Interview implication: you are evaluated as someone who can ship **creative tooling at consumer+pro scale** — document models, collaboration, media, and performance — not CRUD admin apps.

### Products

Know the product surface enough to map designs:

| Surface | What interviewers hear when you mention it |
|---------|--------------------------------------------|
| **Editor** | Document model, layers, selection, undo, rendering performance |
| **Templates & Brand kits** | Search, permissions, enterprise brand controls |
| **Assets / Media library** | Upload, transform, CDN, rights, virus scan |
| **Collaboration** | Multiplayer editing, comments, sharing, permissions |
| **Export / Publish** | PDF/PNG/MP4/print; render farms; job queues |
| **Canva Docs / Presentations / Video / Whiteboard** | Multi-surface document types, shared platform bets |
| **Magic Studio / AI features** | Generative assist, background removal, rewrite, etc. |
| **Enterprise / Education** | Admin, SSO, audit, DRM-ish controls, classrooms |

You do not need product trivia. You need to show which invariants matter: **document consistency under concurrency**, **asset integrity**, **permission correctness**, and **editor frame budget**.

### Engineering Culture

Expect emphasis on:

- **Craft + user empathy** — design tools punish jank; “works on my laptop” fails.
- **Make complex things simple** — complexity belongs in the platform, not the user’s face.
- **High bar for quality** — reviews probe tradeoffs, testing, and extensibility.
- **AI as product leverage** — fluency expected; ownership of AI-assisted output expected in interviews.
- **Global product** — i18n, accessibility, multi-region performance are normal conversation.

Canva values often referenced in hiring: **Make Complex Things Simple**, **Pursue Excellence**, **Set Crazy Big Goals** (confirm current wording on their careers/values pages before looping).

### Business Model

Freemium consumer + Pro/Teams + Enterprise. Engineering impact maps to: editor engagement, export success, collaboration virality, template discovery, AI feature adoption quality, platform reliability (uptime during launches), and enterprise admin trust.

### Scale

Hundreds of millions of monthly active users class traffic (order-of-magnitude; exact numbers move). Spiky creative calendars (back-to-school, holidays, marketing moments). Hot paths: editor load, asset fetch/transform, collab sync, search, AI inference gateways. Cold but critical: billing entitlements, audit exports, legal takedowns.

### Global Presence

Multi-region product and engineering. Interviewers probe: CDN edge strategy, data residency for enterprise, locale/font/script support, latency to editor APIs, and operational follow-the-sun.

### Technology Direction

Themes to discuss soberly:

- Editor performance (WebGL/canvas paths, memory, long sessions).
- Real-time collaboration correctness and presence.
- Media pipeline efficiency and cost.
- Platformization of design primitives across surfaces.
- AI features with safety, cost, and latency budgets.
- Kotlin/Java backend services, TypeScript clients, Python ML — polyglot pragmatism.

Do not pitch rewrites of the editor. Pitch **incremental platform improvements with measurable UX and reliability outcomes**.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

1. Treat the design document as a **versioned, collaborative data structure**, not a JSON blob.
2. Obsess over **performance budgets** (load, interaction latency, memory) for long-lived sessions.
3. Design media and export systems for **async jobs, retries, and cost controls**.
4. Make permissions and sharing **fail closed** — creative content is sensitive.
5. Use AI tools productively while **owning correctness**; never outsource judgment.

### Ownership

- You own user-visible quality in your domain: editor jank, failed exports, broken collab, bad AI outputs.
- “Done” means: metrics, experiments, accessibility, i18n hooks, runbooks, and cost dashboards where relevant.
- You can explain what happens when two users edit the same element offline/online.

### Technical Leadership

- Drive standards for document operations, feature flags, and performance regression gates.
- Mentor on profiling, concurrency in sync protocols, and safe media handling.
- Challenge designs that ignore mobile memory or enterprise permission edge cases.

### Product Mindset

- Simplicity for users may require sophisticated systems underneath — say that explicitly.
- Every AI feature needs evals, abuse controls, and a degradation path when models fail.
- Templates/search quality is product infrastructure, not a side index.

### Collaboration Style

- Work with Design, Research, Data, ML, Trust & Safety, Enterprise, Support.
- Critique with kindness and precision; craft challenge reviews reward clear tradeoffs.
- Cross-team platform changes need migration plans for existing designs.

### Engineering Principles

| Principle | Interview signal |
|-----------|------------------|
| User-time sacred | Frame budget, perceived performance |
| Correct collaboration | Conflict resolution you can explain |
| Permission correctness | Share links, team roles, brand locks |
| Async for heavy work | Export/AI/transform never block UI forever |
| Cost as a feature | Media/AI spend observability |
| Progressive enhancement | Degrade AI/collab without bricking edit |
| Measurable craft | Profiles, traces, UX metrics — not vibes |
| Inclusive by default | a11y + i18n in designs |

---

## 03 - Typical Technology Stack

Exact stack varies by team. Credible interview defaults for Canva-scale design platforms:

### Java / Kotlin

Backend services for documents, entitlements, collaboration backends, export orchestration, enterprise admin APIs. Java literacy transfers; Kotlin is commonly discussed in Canva-like backends.

**Why Canva:** Strong concurrency and typing for high-QPS services; mature JVM ops; fits microservice fleets on AWS-class infra.

### Spring / JVM service frameworks

Not always Spring everywhere, but enterprise Java patterns (DI, config, observability, security filters) remain interview-relevant for backend leads.

**Why Canva:** Rapid, consistent service delivery with production-grade metrics/auth across many domains.

### TypeScript / Frontend

Editor and web app: React-scale UI with custom rendering paths (canvas/WebGL), complex client state, strict typing.

**Why Canva:** The product *is* the client for many roles; type safety and modeling of document operations matter daily.

### Cloud (AWS-class)

Compute for APIs and workers, object storage for assets, managed queues, KMS, WAF, multi-AZ/region patterns.

**Why Canva:** Elasticity for viral spikes; global footprint; security controls for enterprise.

### Microservices + platform boundaries

Document service, collab/realtime, asset service, transform/export workers, search, notifications, billing entitlements, AI gateway, admin/audit.

**Why Canva:** Independent scaling of media/AI vs editor APIs; blast-radius control — requires strong contracts and tracing.

### Databases

Relational for metadata/permissions/billing; document/blob stores for design payloads or chunked ops; Redis for sessions/presence/rate limits; search indexes (OpenSearch/Elastic-class) for templates/assets; warehouses for analytics.

**Why Canva:** Different consistency needs per domain — permissions strongly consistent; analytics eventually; search eventually with freshness SLOs.

### Messaging

Kafka/SQS-class for export jobs, virus scans, AI tasks, fan-out notifications, indexing pipelines.

**Why Canva:** Heavy work off the interactive path; replay and backlog management during incidents.

### CI/CD

Mandatory tests, visual/regression where relevant, canaries, feature flags, performance budgets in pipelines for editor-critical paths.

**Why Canva:** Bad deploys break creative sessions mid-flow; rollback and flag discipline are survival skills.

### Kubernetes

Service fleets and job workers (export, transform, AI pre/post-process); HPA/KEDA-style scaling on queue depth.

**Why Canva:** Standard for large SaaS; isolate expensive workers from interactive APIs.

### Infrastructure

CDN for assets, edge caching, IaC, secrets, service mesh/network policies, centralized observability, chaos/load testing for launches.

**Why Canva:** Global asset delivery and operational maturity are product features.

### Frontend / Mobile

Web editor primary; native mobile apps; careful offline/partial sync; font and asset downloading strategies.

**Why Canva:** Session length and input diversity (pen/touch/keyboard) stress state management.

### AI / ML

Python/model services, GPU inference fleets or third-party model APIs behind a gateway; eval harnesses; safety filters; caching of deterministic transforms (e.g., background removal results).

**Why Canva:** Magic features differentiate — but latency, cost, and incorrect edits can destroy trust.

### Design-Platform Tech Awareness

- OT vs CRDT tradeoffs for collaborative editing.
- Presence channels (cursors/selections) vs durable ops log.
- Image pipeline: upload → scan → variants → CDN signed URLs.
- Export render: headless browser vs native renderer vs hybrid.
- Template search: embeddings + lexical + business ranking.
- Font/licensing constraints; DRM for premium assets.

---

## 04 - Typical Interview Process

Loops evolve; prepare for this Canva-shaped process (confirm with recruiter for your role):

### Stage 1 — Recruiter Screen

**Purpose:** Motivation for Canva, level, logistics, craft/AI comfort, values alignment signals.

**Evaluation Criteria:** Clear narrative; product empathy; communication; relevant domain (editor, backend scale, ML, mobile).

**Preparation Tips:** 90-second story emphasizing user-facing quality, collaboration systems, media pipelines, or high-scale SaaS. Mention AI tooling fluency without sounding dependent.

**Common Mistakes:** Generic “I like design tools”; no scale stories; hostility to AI-assisted coding when the loop requires it.

### Stage 2 — Technical Interview

**Purpose:** Language depth (TS/Kotlin/Java/Python by role), modeling, debugging, API design.

**Evaluation Criteria:** Clear data models; edge cases; performance awareness; testing discipline.

**Preparation Tips:** Practice modeling hierarchical documents, event streams, job state machines. Be ready to discuss complexity and memory.

**Common Mistakes:** Algorithm trivia without product mapping; ignoring permissions; hand-wavy concurrency.

### Stage 3 — Coding Assessment

**Purpose:** Practical build — historically includes **Craft Challenge** (take-home + review) and/or **AI-assisted live coding** (bring Cursor/Copilot/Claude-class tools; own the output).

**Evaluation Criteria:** Problem decomposition; code quality; tradeoffs; extensibility; how you review AI output; communication in review.

**Preparation Tips:** Prepare a personal starter kit (Vite+React+TS or JVM service template). Practice 60–90 minute slices with AI tools while narrating. For take-homes: polish README, tests, and “what I’d do next.”

**Common Mistakes:** Shipping AI spaghetti you cannot explain; ignoring requirements; no tests; defensive arrogance in craft review feedback.

### Stage 4 — System Design

**Purpose:** Collaboration, CDN/media, search, export, presence, or AI feature backends at Canva scale.

**Evaluation Criteria:** Requirements clarity; consistency model; performance; cost; security; operability; multi-region thinking.

**Preparation Tips:** Drill Section 08. Always clarify document model assumptions and permission boundaries.

**Common Mistakes:** Designing Google Docs clone trivia without media/cost; ignoring abuse; no degradation path.

### Stage 5 — Leadership Interview

**Purpose:** Mentoring, influence, conflict, delivery under ambiguity, craft standards.

**Evaluation Criteria:** Values alignment; ownership; ability to raise quality without heroics; cross-functional empathy.

**Preparation Tips:** STAR stories on performance regressions, cross-team migrations, mentoring juniors on craft.

**Common Mistakes:** Glorifying burnout; dismissing designers/PMs; no examples of simplifying complexity.

### Stage 6 — Hiring Manager

**Purpose:** Team mission fit (editor, growth, enterprise, media, AI), roadmap, ways of working.

**Evaluation Criteria:** Judgment, collaboration, growth trajectory, realism about platform constraints.

**Preparation Tips:** Ask about performance budgets, collab protocol ownership, AI eval process, on-call, craft challenge expectations.

**Common Mistakes:** Not asking how success is measured in the first two quarters.

### Stage 7 — Final Interview

**Purpose:** Bar raise / skills framework loop — technical problem solving mixed with experience; values probe.

**Evaluation Criteria:** Consistency, excellence bar, simplicity bias, integrity under pressure.

**Preparation Tips:** Prepare stories mapped to Canva values; thoughtful questions on platform strategy.

**Common Mistakes:** Inconsistent craft standards across interviews; overselling rewrites.

### Stage 8 — Offer

**Purpose:** Leveling, location/hub, compensation bands, scope.

**Evaluation Criteria:** Mutual clarity on role impact (IC Lead vs manager-adjacent expectations).

**Preparation Tips:** Confirm team charter, on-call, AI feature ownership boundaries, promotion criteria.

**Common Mistakes:** Optimizing title only; unclear on editor vs platform scope.

---

## 05 - Technical Focus Areas

### Design Platform at Massive Scale

- Multi-tenant document storage and caching.
- Entitlements (Free/Pro/Enterprise) on hot paths.
- Launch/event traffic shaping without bricking editors.

### Collaboration

- Real-time multiplayer editing semantics.
- Comments, suggestions, sharing model.
- Conflict resolution explainability for interviews.

### Media Pipelines

- Upload, virus scan, metadata extract, variant generation.
- Cost controls for transforms; idempotent jobs.
- Rights/licensing metadata for premium assets.

### Real-Time Editing & Document Model

- Tree/graph of elements; operations; undo/redo stacks.
- Snapshot + ops log compaction.
- Large-document strategies (pagination, lazy load, virtualization).

### CDN & Asset Delivery

- Signed URLs; cache invalidation; geographic edge.
- Image/video transforms at edge vs origin workers.
- Hotspot assets (viral templates) protection.

### Internationalization & Accessibility

- Fonts/scripts; RTL; locale-aware templates.
- Screen reader semantics for editor chrome; keyboard paths.
- Performance on low-end devices and poor networks.

### Performance

- Profiling React + custom renderers; memory leaks in long sessions.
- Network waterfalls for editor bootstrap.
- Budgets enforced in CI for critical interactions.

### Platform Reliability

- SLO/SLA thinking for editor APIs and export success rate.
- Feature flags; dark launches; canaries.
- Partial outage modes: read-only docs, disable AI, disable collab.

### AI Design Features

- Gateway: auth, quota, safety, routing, caching.
- Human-editable results; never silent corruption of document.
- Eval sets, red-team prompts, cost/latency dashboards.
- Background removal / generative fill as async jobs with previews.

### Search & Discovery

- Template search relevance; personalization vs brand safety.
- Indexing pipelines from publish events.
- Abuse: scraping, SEO spam templates.

---

## 06 - Leadership Focus

### Ownership

Own user-visible quality and reliability outcomes — not just ticket throughput.

### Mentoring

Teach profiling, document-model thinking, and “simplify the user journey.” Review for accessibility and i18n, not only logic.

### Decision Making

Prefer reversible platform bets. Quantify performance and cost. Kill cleverness that harms craft.

### Cross-team Collaboration

Partner with Design early; negotiate ML constraints honestly; align Enterprise needs without fracturing consumer UX.

### Incident Response

SEV tied to editor/export/AI impact. Communicate clearly; feature-flag mitigate; protect document integrity first.

### Architecture Discussions

Challenge unbounded document growth, sync protocols without compaction, and AI features without eval harnesses.

### Technical Debt

Prioritize debt that causes jank, data loss risk, permission bugs, or runaway media/AI cost. Tie to metrics.

### Engineering Culture

Excellence without blame; craft reviews that teach; celebrate simplicity and reliability wins, not only flashy launches.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you simplified a complex user-facing workflow.

- **Why asked:** “Make Complex Things Simple” signal.
- **Competencies:** Product empathy, design sense.
- **Framework:** User pain → complexity inventory → hide/push down → validate with metrics/research → teach team pattern.
- **Follow-ups:** What complexity moved into the platform? What did you deliberately not build?

### Q2. Describe a performance regression you owned in a client-heavy app.

- **Why asked:** Editor craft bar.
- **Competencies:** Profiling, ownership.
- **Framework:** Detect → profile → root cause → fix/flag → CI budget → prevention.
- **Follow-ups:** How did you catch it before all users? Memory or CPU?

### Q3. Tell me about shipping a collaborative editing or sync feature.

- **Why asked:** Multiplayer is core Canva DNA.
- **Competencies:** Distributed systems, correctness.
- **Framework:** Consistency model → conflict cases → offline → testing strategy → rollout → incident learnings.
- **Follow-ups:** OT vs CRDT — what did you choose and why?

### Q4. Give an example of an export/render pipeline you improved.

- **Why asked:** Media/export reliability.
- **Competencies:** Async systems, UX.
- **Framework:** Failure modes → job state machine → retries → user progress → success rate/cost.
- **Follow-ups:** How do you handle partial failures mid-render?

### Q5. Tell me about using AI tools in your daily engineering — and when you rejected AI output.

- **Why asked:** AI-assisted interview reality + judgment.
- **Competencies:** AI literacy, accountability.
- **Framework:** Workflow → review checklist → caught defect → tests → personal standards.
- **Follow-ups:** How do you prevent subtle security/perf bugs from AI?

### Q6. Describe mentoring someone to raise their craft standard.

- **Why asked:** Leadership via excellence.
- **Competencies:** Mentoring.
- **Framework:** Concrete gap → pairing/review rubric → before/after artifact → independence.
- **Follow-ups:** How do you avoid gatekeeping?

### Q7. Tell me about a disagreement with design/product on interaction complexity.

- **Why asked:** Cross-functional craft negotiation.
- **Competencies:** Collaboration, influence.
- **Framework:** User evidence → technical cost → alternatives → decision → outcome.
- **Follow-ups:** What did you learn about communicating constraints?

### Q8. Share an incident where users could not access or export their work.

- **Why asked:** Trust + reliability ownership.
- **Competencies:** Incident leadership.
- **Framework:** Mitigate → communicate → data integrity check → restore → postmortem controls.
- **Follow-ups:** Any data loss? How verified?

### Q9. Tell me about improving search relevance or template discovery.

- **Why asked:** Growth + platform search.
- **Competencies:** Data thinking, product.
- **Framework:** Query analysis → ranking features → eval set → experiment → abuse checks.
- **Follow-ups:** How did you measure success beyond clickbait CTR?

### Q10. Describe a CDN/asset delivery problem you solved.

- **Why asked:** Global media reality.
- **Competencies:** Networking, caching.
- **Framework:** Symptom (TTFB/404/stale) → cache hierarchy → invalidation → signed URL posture → metrics.
- **Follow-ups:** How do you invalidate transformed variants safely?

### Q11. Tell me about implementing or hardening permissions/sharing.

- **Why asked:** Enterprise + consumer trust.
- **Competencies:** Security, correctness.
- **Framework:** Threat cases → model (ACL/RBAC/link) → tests → audit → migration of legacy shares.
- **Follow-ups:** Public link edge cases? Team vs folder inheritance?

### Q12. Give an example of an AI feature you helped ship safely.

- **Why asked:** Magic Studio-era expectations.
- **Competencies:** ML productization, judgment.
- **Framework:** UX → gateway quotas → safety → latency budget → evals → degrade path → cost.
- **Follow-ups:** What failure mode scared you most?

### Q13. Tell me about a large document / memory issue in a long-lived session.

- **Why asked:** Real editor pain.
- **Competencies:** Client performance.
- **Framework:** Reproduction → heap snapshots → retainers → structural fix → guardrails.
- **Follow-ups:** Virtualization? Compaction? Asset eviction?

### Q14. Describe leading a cross-team platform migration without breaking existing designs.

- **Why asked:** Platform leadership.
- **Competencies:** Migration discipline.
- **Framework:** Dual-run → converters → cohort → correctness validators → rollback → completion metrics.
- **Follow-ups:** How did you handle undecodable legacy docs?

### Q15. Tell me about setting a quality bar (lint, review checklist, perf budgets).

- **Why asked:** Pursue Excellence culturally.
- **Competencies:** Engineering culture.
- **Framework:** Pain incident → lightweight standard → adopt → measure escaped defects/jank.
- **Follow-ups:** How did you prevent bureaucracy?

### Q16. Share a time you said no to a flashy feature that would harm reliability.

- **Why asked:** Judgment under growth pressure.
- **Competencies:** Integrity, influence.
- **Framework:** Risk articulation → smaller slice → enablement plan → result.
- **Follow-ups:** Did leadership agree? What evidence worked?

### Q17. Tell me about internationalization or accessibility work you drove.

- **Why asked:** Global inclusive product.
- **Competencies:** a11y/i18n literacy.
- **Framework:** User impact → technical gaps → incremental fixes → tests → partnering with design.
- **Follow-ups:** Fonts/RTL/screen readers — which was hardest?

### Q18. Describe coaching a team through noisy alerts or burnout.

- **Why asked:** Sustainable excellence.
- **Competencies:** People leadership, SRE basics.
- **Framework:** Alert audit → SLO pages → runbooks → rotation health → outcome.
- **Follow-ups:** What % of alerts became actionable?

### Q19. Tell me about a craft challenge / take-home style project you are proud of.

- **Why asked:** Predicts Canva process performance.
- **Competencies:** Communication, tradeoffs.
- **Framework:** Ambiguous brief → decisions → polish → what you’d do with more time → feedback handling.
- **Follow-ups:** What tradeoff would you reverse?

### Q20. Give an example of cost optimization on media or AI spend.

- **Why asked:** Scale economics.
- **Competencies:** FinOps sense, systems.
- **Framework:** Cost driver → cache/batch/resize → quality impact → savings metric.
- **Follow-ups:** Did UX regress? How measured?

### Q21. Tell me about handling abusive uploads or malware in a media pipeline.

- **Why asked:** Trust & safety adjacency.
- **Competencies:** Security.
- **Framework:** Threat → scan architecture → quarantine → UX → metrics on catch rate.
- **Follow-ups:** Zero-day / AV lag strategy?

### Q22. Describe a time you influenced standards across multiple squads.

- **Why asked:** Staff/Lead multi-team impact.
- **Competencies:** Influence without authority.
- **Framework:** Shared incident → RFC → reference implementation → adoption.
- **Follow-ups:** Who resisted and why?

### Q23. Tell me about delivering under a crazy-big goal without sacrificing users.

- **Why asked:** Ambition + craft.
- **Competencies:** Execution, prioritization.
- **Framework:** Goal → cut scope wisely → risk bets → quality gates → outcome.
- **Follow-ups:** What did you cut that hurt later?

### Q24. Share an architectural decision you later reversed.

- **Why asked:** Humility.
- **Competencies:** Reflection.
- **Framework:** Bet → disconfirming signal → migration off → lesson encoded.
- **Follow-ups:** Early indicator missed?

### Q25. Tell me about working with ML engineers as a platform/product engineer.

- **Why asked:** AI feature collaboration.
- **Competencies:** Cross-discipline communication.
- **Framework:** Latency/quality contract → fallback UX → online eval → iteration loop.
- **Follow-ups:** How do you version models safely?

### Q26. Describe improving observability for a realtime or editor system.

- **Why asked:** Debuggability at scale.
- **Competencies:** Operability.
- **Framework:** Blind spot → client+server traces → session identifiers → dashboards → MTTD.
- **Follow-ups:** Privacy constraints on session replay?

### Q27. Tell me about a conflict on your team about technical approach.

- **Why asked:** Collaboration maturity.
- **Competencies:** Conflict resolution.
- **Framework:** Steelman both sides → criteria → experiment/spike → decide → commit.
- **Follow-ups:** How did relationships fare after?

### Q28. Give an example of enterprise requirements you integrated without wrecking consumer UX.

- **Why asked:** Dual-motion product.
- **Competencies:** Architecture judgment.
- **Framework:** Constraint (SSO/audit/DLP) → isolation strategy → progressive disclosure → validation with both segments.
- **Follow-ups:** What leaked as special-case complexity?

### Q29. Tell me about hiring or interviewing — what signals matter for Canva-like craft?

- **Why asked:** Lead hiring bar.
- **Competencies:** Talent selection.
- **Framework:** Probe user empathy → debugging under ambiguity → ownership of quality → teachability → AI judgment.
- **Follow-ups:** Automatic no-hire signal?

### Q30. Describe prioritizing tech debt against growth OKRs.

- **Why asked:** Lead judgment.
- **Competencies:** Prioritization.
- **Framework:** Risk-rank debt → tie to jank/loss/cost → negotiate capacity → show before/after.
- **Follow-ups:** What debt did you defer consciously?

### Q31. Tell me about a time retries or realtime reconnect storms made things worse.

- **Why asked:** Mature distributed systems scars.
- **Competencies:** Debugging.
- **Framework:** Amplification → backoff/jitter → server load shed → client strategy → chaos test.
- **Follow-ups:** How tested reconnect stampedes?

### Q32. Share making an offline or flaky-network editing experience tolerable.

- **Why asked:** Real-world client conditions.
- **Competencies:** UX + sync.
- **Framework:** Local queue → conflict UX → reconnect → durable save indicators → metrics.
- **Follow-ups:** How do you prevent silent data loss?

### Q33. Tell me about giving/receiving hard feedback in a design/code review.

- **Why asked:** Craft culture fit.
- **Competencies:** Communication.
- **Framework:** Specific examples → impact → alternatives → outcome → relationship.
- **Follow-ups:** How do you keep excellence from becoming harshness?

---

## 08 - System Design Questions

### Scenario A — Collaborative Document Editing

**Requirements:**  
Multiple users edit a design in near real time. Support presence (cursors/selections), comments, undo, permissions. Handle flaky networks. Scale to many concurrent editors on popular templates (usually fewer co-editors per doc, but many docs). Prevent silent data loss. Enterprise: audit of access.

**Architecture Discussion:**  
Client applies local ops optimistically → send ops to Collab service over WebSocket → server orders/validates against permission version → broadcast to peers → persist ops log + periodic snapshots in Document store. Presence on ephemeral channel (Redis/pubsub) separate from durable ops. Compaction job snapshots and truncates logs. Conflict strategy: OT or CRDT — pick one and defend (CRDT simpler merge, larger metadata; OT smaller wire, central sequencing). AuthZ check on every op. Secondary indexes for “shared with me.”

**Tradeoffs:**  
Central sequencer (simpler reasoning, hotspot) vs pure P2P CRDT (complex, harder auth). Fat snapshots vs long op tails. Include media blobs in doc vs references only (always references).

**Scaling:**  
Shard docs by document_id; sticky sessions optional; horizontal WS gateways; separate presence cluster; cold docs on cheaper storage.

**Reliability:**  
Ack/save indicators; retry with idempotent op IDs; snapshot checksums; read-only mode on corruption suspicion; backup/restore tooling.

**Security:**  
Share-link tokens; team RBAC; disable public access enterprise policies; rate-limit op floods; PII in comments retention.

**Production Considerations:**  
Metrics: sync latency, conflict rates, WS disconnects, doc load time. Runbooks for poison docs. Feature flag new op types.

---

### Scenario B — Asset CDN & Transform

**Requirements:**  
Users upload images/videos; system generates variants (resize, format, quality); deliver globally via CDN with low TTFB; secure access for private designs; virus scan; cost control; invalidate when asset replaced.

**Architecture Discussion:**  
Upload API → pre-signed PUT to object storage → event → virus scan worker → metadata DB row → transform workers produce variants (or lazy transform on first request) → store variants → CDN in front with signed cookies/URLs. Cache keys include transform params + asset version. Async queue for video. Allowlist transform params to prevent CPU DoS. Origin shield to protect storage.

**Tradeoffs:**  
Eager vs lazy transforms (cost vs first-byte latency). Edge compute transforms vs precompute. Long-lived public URLs vs short-lived signed.

**Scaling:**  
Queue depth autoscaling; separate image vs video fleets; CDN cache hit ratio as primary lever; regional buckets if residency requires.

**Reliability:**  
Idempotent transform jobs; poison quarantine; fallback to original; retry with backoff; checksum verify.

**Security:**  
Malware scan; content-type sniffing; authZ before sign; prevent open transforms as free image CDN for attackers; watermark premium assets if needed.

**Production Considerations:**  
Cost dashboards per transform type; hot key protection; invalidation tooling; Support: “why is my image blurry” → variant params explainability.

---

### Scenario C — Design Template Search

**Requirements:**  
Users search millions of templates with low latency. Support filters (type, theme, language), personalization light-touch, spam/abuse controls, freshness when new templates publish, A/B ranking experiments.

**Architecture Discussion:**  
Publish pipeline: template metadata + thumbnails + embeddings → indexing workers → search cluster (inverted index + vector). Query service: rewrite/locale → hybrid retrieval (BM25 + kNN) → business ranker (quality, conversion, brand safety) → blistering cache for head queries. Admin tools for demote/remove. Eventual consistency OK with freshness SLO (e.g., searchable within minutes).

**Tradeoffs:**  
One index vs per-locale indexes. Online learning rankers vs simpler heuristics. Exact filters in search vs post-filter (recall issues).

**Scaling:**  
Sharded indexes; replica reads; CDN for thumbnails; query cache; isolate indexing from serving.

**Reliability:**  
Index lag alerts; canary ranking; freeze bad model; fallback lexical-only.

**Security:**  
Hide private/enterprise templates; legal takedown path; scrape rate limits; poisoned embedding detection.

**Production Considerations:**  
Eval harness (nDCG on labeled sets); offline vs online metrics; dark launch rankers; human review queue for reported templates.

---

### Scenario D — Export / Render Pipeline

**Requirements:**  
Export design to PDF/PNG/MP4/print package. Show progress; handle complex docs with many assets/fonts; retries; priority for paid users; cost and queue fairness; deterministic-enough output for print.

**Architecture Discussion:**  
Export API creates job → enqueue with priority → worker pulls doc snapshot + assets → render (headless Chromium or native renderer) → upload artifact → notify client via WS/poll. Cache warmed fonts/assets. Split stages: resolve → render → postprocess. Dead-letter with partial logs. Idempotent job keys for retries. Virus scan outbound artifacts if accepting user fonts/scripts carefully (prefer allowlisted engines).

**Tradeoffs:**  
Browser engine fidelity vs native speed/cost. Sync export for tiny docs vs always async. Per-page fan-out for huge PDFs vs single process.

**Scaling:**  
Worker pools per format; autoscale on queue age; spot capacity with interruption handling; regional workers near assets.

**Reliability:**  
Checkpoint multi-page; retry only failed pages; timeout budgets; circuit-break when asset service down; quality synthetic exports in CI.

**Security:**  
SSRF protections in renderers; sandbox workers; entitlements check; signed download URLs; audit enterprise exports.

**Production Considerations:**  
Success rate SLO; p95 time-to-export; cost per export; war-room for viral campaign spikes; customer messaging on failures.

---

### Scenario E — Real-Time Presence

**Requirements:**  
Show who is in a design, their cursor/selection, maybe follow mode. High fan-out on popular education/enterprise docs. Ephemeral — loss on disconnect OK. Must not overload durable collab path. Privacy: don’t leak presence across permissions.

**Architecture Discussion:**  
Presence service separate from ops log. Clients heartbeat + publish cursor updates at throttled rates. Pub/sub channels per document (Redis/Redis Cluster, NATS, etc.). Server filters recipients by authZ snapshot. Optionally aggregate updates (interest management). Scale: shard by doc_id; cap visible avatars; sample cursors under extreme load.

**Tradeoffs:**  
Sticky WS to presence nodes vs pure pub/sub. Exact cursors vs approximate. Include presence in CRDT doc (usually no).

**Scaling:**  
Horizontal gateways; channel sharding; degrade to avatar list without cursors; rate-limit noisy clients.

**Reliability:**  
Soft state; quick rejoins; avoid page storms on reconnect (jitter).

**Security:**  
Revalidate permissions on join; hide anonymous where required; prevent doc_id enumeration.

**Production Considerations:**  
Metrics on channel cardinality, msg/s, degrade triggers; feature flag cursor sharing.

---

### Scenario F — AI Background Removal Service

**Requirements:**  
User selects image → remove background → preview → apply to design. Latency target interactive or near-interactive. GPU/model cost controls. Fail gracefully. Cache results for identical assets. Safety: inappropriate content handling. Must not corrupt original asset.

**Architecture Discussion:**  
Editor calls AI Gateway (auth, quota, entitlement) → create job with asset_id + model version → worker fetches asset → model inference → write result variant to Asset service → return URLs + mask metadata → client applies as non-destructive layer edit (pointer to new asset). Cache key: hash(asset_bytes, model_version, params). Async path with progress for slow/large images. Human report flow. Eval set for quality regressions before model promote.

**Tradeoffs:**  
On-device vs server inference. Sync wait vs async job. Third-party model API vs in-house GPUs. Store mask separately vs flattened PNG.

**Scaling:**  
GPU autoscaling; queue priorities; batch small images carefully (latency); CDN for results; regional inference for latency/residency.

**Reliability:**  
Timeouts + fallback “manual brush” UX; retry idempotent; poison image quarantine; model canary.

**Security:**  
Quota abuse prevention; content safety classifier; signed result URLs; audit enterprise usage; prompt/injection less relevant but parameter tampering is.

**Production Considerations:**  
Cost per successful removal; p95 latency; quality complaints; kill switch to disable feature globally; version pin per experiment cohort.

---

## 09 - Company Preparation Checklist

- [ ] Can explain Canva’s product surfaces and why editor performance matters
- [ ] Can speak to collaboration consistency (OT/CRDT) without dogma
- [ ] Can whiteboard asset CDN + transforms with cost/security
- [ ] Can whiteboard export job pipeline with priorities and failure modes
- [ ] Can discuss AI gateway: quota, eval, degrade, non-destructive edits
- [ ] Can map stories to Canva values (simplicity, excellence, ambition)
- [ ] Prepared AI-assisted coding workflow you can narrate
- [ ] Craft challenge practice: polished README, tests, tradeoffs list
- [ ] Reviewed recent Canva engineering/AI news soberly
- [ ] Questions ready on performance budgets and team mission
- [ ] Stack translation ready: your Java/Spring ↔ Kotlin services + TS client realities

---

## 10 - How My Experience Maps

Fill before interviews (replace bullets with your metrics):

| Canva need | My evidence | Metric / artifact |
|------------|-------------|-------------------|
| Client performance / profiling | | |
| Realtime collaboration / sync | | |
| Media pipeline / CDN | | |
| Async job systems (export/AI) | | |
| Search / ranking | | |
| Permissions / sharing | | |
| Large-scale SaaS reliability | | |
| AI feature productization | | |
| Mentoring + craft standards | | |
| Cross-functional with Design/ML | | |

**Gap plan:** Empty rows → borrow adjacent STAR stories (docs collab, image pipelines, high-QPS APIs) or drill Section 08 until fluent; practice one AI-assisted build end-to-end.

---

## Interview Confidence Checklist

- [ ] 90-second Canva-specific pitch ready
- [ ] 8+ STAR stories mapped to Q1–Q33 themes
- [ ] Can draw Scenarios A–F without notes
- [ ] Can defend collab + media tradeoffs under pushback
- [ ] AI-assisted coding kit ready; can critique AI output live
- [ ] Values stories prepared (simplicity / excellence / big goals)
- [ ] Questions ready for recruiter, HM, and final
- [ ] Leveling narrative honest (Senior vs Lead vs Architect)

---

## Mock Interview Preparation Checklist

- [ ] Mock recruiter + values (30 min)
- [ ] AI-assisted coding dry run (60 min, screen share practice)
- [ ] Craft-challenge style mini project (4–8 hours) + review rehearsal
- [ ] Mock system design: pick 3 of Scenarios A–F (60 min each)
- [ ] Mock leadership: performance incident + mentoring (45 min)
- [ ] Record one design explanation; critique clarity of document model
- [ ] Feedback logged; weak areas scheduled

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | Company + values; write pitch; AI tooling dry run |
| 2 | Collaboration + presence; Scenarios A & E |
| 3 | Assets CDN/transform + export; Scenarios B & D |
| 4 | Search + AI background removal; Scenarios C & F |
| 5 | Performance, a11y/i18n, permissions deep dive |
| 6 | Behavioral battery (Q1–Q33); fill Section 10 |
| 7 | Full mock loop (craft review + design + values); revise |

---

## Estimated Preparation Time

| Track | Hours |
|-------|------:|
| Company + values + AI interview setup | 4–5 |
| Technical focus + designs (A–F) | 12–14 |
| Behavioral story binding | 6–8 |
| Craft challenge + AI coding practice | 8–12 |
| Mocks + revision | 6–8 |
| **Total** | **~36–47 hours** |

For a compressed timeline (5 days): prioritize Scenarios A/B/D/F, one polished craft mini-project, values stories, and AI-assisted coding fluency.
