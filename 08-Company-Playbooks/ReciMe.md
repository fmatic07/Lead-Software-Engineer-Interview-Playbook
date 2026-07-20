# ReciMe

> Reverse-engineer how ReciMe evaluates Lead/Senior engineers for a consumer cooking product — backend ownership, AI-assisted features, API quality, and product judgment under rapid iteration.

---

## 01 - Company Overview

### Industry

ReciMe is a consumer foodtech / cooking app company. It sits in the crowded space of recipe organizers, meal planners, and grocery helpers — differentiated by import quality (social/web/photo), planning UX, and AI-assisted cooking workflows.

Interviewers assume you understand consumer mobile realities: flaky networks, impulsive usage, subscription funnels, and trust in AI output that touches daily life (food preferences, allergies, household data).

What panels care about: you can own backend systems end-to-end, ship iteratively without melting quality, and treat AI as a product surface with failure modes — not a demo.

### Products

Know the product surface enough to map designs:

| Surface | What interviewers hear when you mention it |
|---------|--------------------------------------------|
| **Recipe library / cookbooks** | CRUD + rich media + organization at user scale |
| **Import from social/web** | Unstructured extraction, scraping ethics, brittle sources |
| **Photo / screenshot / handwritten import** | Multimodal AI pipelines, confidence, human correction |
| **Meal planner** | Calendar semantics, preferences, household sharing |
| **Smart grocery list** | Aggregation, aisle ordering, sync across devices |
| **AI chef / meal generation** | Prompted generation, safety (allergens), personalization |
| **Web + Chrome extension + mobile** | Multi-client API design, auth, sync |
| **Subscriptions / freemium limits** | Entitlements, rate limits, paywall fairness |

You do not need marketing slogans. You need to show which invariants matter: user data ownership, sync correctness, import idempotency, AI confidence UX, and entitlement enforcement.

### Engineering Culture

Expect a small-to-mid product engineering culture (startup / growth-stage signals):

- High ownership per engineer; fewer handoff layers
- Bias to ship, measure, iterate — with pragmatism about debt
- Product and design as daily partners, not ticket vendors
- AI features as core product bets, not side experiments forever
- Quality bars that protect retention (import success, sync trust) over ceremonial process

Signal you can move fast *and* leave the codebase healthier than you found it. "Move fast" without observability or rollback is a fail.

### Business Model

Freemium consumer subscription: free tier with limits (e.g. import caps) plus paid plans for heavier use. Engineering impact maps to:

- Activation: successful first imports
- Retention: weekly meal planning / grocery usage
- Conversion: hitting limits → paid without dark patterns that destroy trust
- Cost control: AI inference, media storage, and egress as COGS
- Reliability: sync and list correctness (users abandon apps that lose data)

Frame stories as product + margin aware: latency, model cost, and storage growth are business metrics.

### Scale

Consumer mobile scale: spiky usage around dinner times, weekends, holidays, and viral social content. Hot paths:

- Auth/session
- Recipe import and AI extraction
- Feed/home and search
- Grocery list sync
- Image upload / recognition

Cold paths still matter: exports, account deletion, subscription reconciliation, recommendation backfills.

### Global Presence

Consumer app with international users (US/English-speaking markets prominent in public footprint; product available on iOS/Android/web). Interviewers care about:

- Multi-region latency for APIs
- Time zones in meal planning
- Localization readiness (even if English-first today)
- Privacy expectations (GDPR-style deletion, app store requirements)
- Content source diversity (Instagram/TikTok/web recipes vary wildly)

### Technology Direction

Themes to discuss soberly:

- Mobile-first backend APIs with strong sync semantics
- AI/multimodal pipelines for import and meal generation (e.g. Gemini-class models in public demos; exact vendors evolve)
- Next.js/React web + Node and/or JVM backends depending on team — prepare for polyglot pragmatism
- AWS (or similar) for compute, storage, queues
- Rapid experimentation with feature flags and analytics
- Cost-aware AI: caching, smaller models for easy cases, escalation for hard cases
- Social/sharing features that create feed and moderation problems

Do not pitch enterprise ceremony for its own sake. Pitch paved paths that keep a small team fast: solid CI, observability, API guidelines, and AI evaluation harnesses.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

1. **End-to-end backend ownership** — API, data model, jobs, metrics, and on-call for your surface.
2. **Product taste** — you notice when an API shape or AI failure creates bad UX.
3. **Pragmatic architecture** — choose boring tech until complexity is forced.
4. **AI systems thinking** — latency, cost, confidence, evals, fallbacks, and user correction loops.
5. **Sync & consistency literacy** — lists and planners that do not corrupt under multi-device use.
6. **Communication density** — write clearly; decide with incomplete information; document just enough.

### Ownership

- You own a product slice including break-glass tools, backfills, and cost dashboards.
- "Done" means: feature flag, metrics, error budgets for critical paths, and a rollback story.
- You can explain where user data lives and how account deletion works.

### Technical Leadership

- Raise API and data modeling standards without drowning a small team in process.
- Mentor on mobile client realities: retries, pagination, idempotency, offline.
- Set the bar for AI feature quality: evaluation sets, prompt/versioning, safe defaults for allergens.
- Drive incident hygiene even when the team is small.

### Product Mindset

- Optimize for activation and retention loops, not only service CPU.
- Every AI feature needs a non-AI fallback or manual correction path.
- Entitlements and rate limits are product features — design them explicitly.
- Instrument funnels: import started → parsed → saved → planned → shopped.

### Collaboration Style

- Pair tightly with Product, Design, Mobile, and (if present) ML/AI specialists.
- Prefer prototypes with measurement over endless design docs — but still write ADRs for irreversible decisions (auth model, sync protocol, data ownership).
- Escalate ambiguity in food-safety-adjacent claims (allergens) immediately.

### Engineering Principles

| Principle | Interview signal |
|-----------|------------------|
| Own the outcome | Metrics + UX, not just merged PR |
| API clarity | Mobile-friendly contracts, versioning, errors |
| Idempotent imports | Retries and duplicate social URLs do not corrupt library |
| Sync honesty | Conflict rules users can understand |
| AI with brakes | Confidence, fallback, cost caps, evals |
| Iterate safely | Flags, gradual rollout, kill switches |
| Cost as a feature | Inference and storage budgets are design inputs |
| Privacy respect | Deletion, export, minimize sensitive household data |

---

## 03 - Typical Technology Stack

Exact stack evolves in a startup; prepare to discuss tradeoffs and *why* each layer matters for ReciMe-like products.

### Java / JVM (if used on your loop)

Some enterprise candidates will map Spring experience to ReciMe services. If the role is JVM:

- Strong for durable domain services (entitlements, sync, billing adjacency)
- Emphasize Spring Boot API design, testability, and operational clarity

If the team is Node-first, translate your Java strengths: modeling, concurrency, observability — do not force Spring into a Node shop rhetorically.

### Spring Boot

**Why it might matter:** structured services for entitlements, recipe canonicalization, or internal admin APIs with mature security and validation.

**Why it might not:** latency of hiring/stack fit — show you can be effective in Node/TypeScript services too if that is the house standard.

### Cloud (AWS)

Common for consumer apps: API compute, S3 media, queues, managed DB, CDN.

**Why it matters here:** image/media cost, burst import traffic, and global edge caching for web assets. Discuss IAM least privilege, private data stores, and cloud cost anomaly detection.

### Microservices vs Modular Monolith

Startups often begin modular monolith and extract under pain.

**Why it matters here:** interviewers listen for judgment — when extraction buys independent scale (AI workers) vs when it only adds ops load. Prefer "modular monolith + async workers" narratives unless scale evidence demands more.

### Databases

PostgreSQL/MySQL for primary user/recipe data; Redis for sessions, rate limits, job locks; object storage for images; search index (OpenSearch/Elastic/Typesense-class) for recipe search.

**Why it matters here:** grocery sync and meal plans need clear consistency rules; search indexing is eventually consistent and must not lie about "saved."

### Messaging

Queues/streams for import pipelines, AI jobs, notifications, and fan-out for social features.

**Why it matters here:** imports and recognition are bursty and CPU/GPU expensive — async is mandatory; users need job status APIs.

### CI/CD

Fast pipelines, preview environments, feature flags, mobile-compatible release trains.

**Why it matters here:** small team velocity dies without safe automation; AI prompt/config changes need the same discipline as code.

### Kubernetes / Containers

Containers likely; full K8s optional depending on stage.

**Why it matters here:** scale AI workers independently from API pods; resource limits prevent a recognition spike from taking down auth.

### Infrastructure

IaC, CDN, WAF for auth endpoints, observability (traces/metrics/logs), error tracking.

**Why it matters here:** consumer apps fail publicly and instantly on Twitter/App Store reviews — detect before users pile on.

### Frontend

React/Next.js web, mobile clients (iOS/Android), Chrome extension.

**Backend interview angle:** design APIs for offline-friendly sync, pagination, partial failure, and clear entitlement errors. Discuss image upload flows and progress UX.

### AI

Core differentiator: multimodal ingredient/recipe recognition, import parsing, meal plan generation, aisle classification.

**Why it matters here:** product quality = model quality + systems around it (evals, caching, human edit, cost). Interview stance: you can productionize AI, not just call an API in a happy-path notebook.

---

## 04 - Typical Interview Process

Stages vary by hiring moment; prepare for this full loop.

### Stage 1 — Recruiter Screen

**Purpose:** Role fit, level calibration, logistics, motivation for consumer/startup product work.

**Evaluation Criteria:** Clarity of narrative; energy for product ownership; compensation/location; communication speed and clarity.

**Preparation Tips:** 90-second pitch ending in ownership + product impact + AI/backend strengths. Name a feature you shipped that moved a metric.

**Common Mistakes:** Enterprise-process flex with no outcome; disdain for consumer apps; "I just want to do architecture" without shipping scars.

### Stage 2 — Technical Interview

**Purpose:** Depth in backend APIs, data modeling, concurrency/async jobs; production debugging.

**Evaluation Criteria:** Correctness; pragmatism; ability to go deeper on claims; awareness of mobile clients and cost.

**Preparation Tips:** Stories for: sync bug, import pipeline incident, performance win, AI/feature flag rollout. Prefer metrics (conversion, error rate, p95, $ / request).

**Common Mistakes:** Over-enterprise designs for a 20–50 person company; ignoring multi-device sync; treating AI as magic.

### Stage 3 — Coding Assessment

**Purpose:** Clean, correct code under time pressure — practical problems (parsing, API handlers, merging lists, rate limiting), not obscure algorithms only.

**Evaluation Criteria:** Edge cases; readability; tests; explicit errors; data-structure taste for real product objects (ingredients, quantities).

**Preparation Tips:** Practice merging grocery items, idempotent import handlers, and pagination. Talk tradeoffs while coding.

**Common Mistakes:** Happy-path only; mutating shared structures; no tests; ignoring unit conversion / duplicate ingredients.

### Stage 4 — System Design

**Purpose:** Design a ReciMe-like system with product constraints, AI pipelines, and sync in view.

**Evaluation Criteria:** Requirements clarification; sensible MVP vs scale path; failure handling; cost; observability; mobile API ergonomics.

**Preparation Tips:** Ask about offline, conflict rules, AI latency budgets, free-tier limits, and abuse. Start from user journey, not from Kafka.

**Common Mistakes:** Designing Twitter-at-scale for a meal planner on day one; no job status model for imports; no entitlement enforcement; security as afterthought on sharing links.

### Stage 5 — Leadership Interview

**Purpose:** Influence in a small team, mentoring, conflict, delivery under ambiguity.

**Evaluation Criteria:** Ownership without heroics; tasteful standards; cross-functional negotiation; hiring bar instincts.

**Preparation Tips:** STAR stories where you unblocked product, cut scope wisely, or raised quality bars without slowing the team to zero.

**Common Mistakes:** Big-company process cosplay; inability to wear multiple hats; blaming designers/PMs.

### Stage 6 — Hiring Manager

**Purpose:** Team fit, leveling, working style, first 90 days.

**Evaluation Criteria:** Self-awareness; bias to action; comfort with ambiguity; alignment with product north star.

**Preparation Tips:** Ask about on-call, AI cost pain points, biggest retention leaks, and how roadmap decisions are made.

**Common Mistakes:** Only asking about titles/levels; no curiosity about users; rigid "I only do backend" in a small team.

### Stage 7 — Final Interview

**Purpose:** Senior calibration, culture fit, residual risk on hire.

**Evaluation Criteria:** Judgment; communication; values around user trust and speed; long-term ownership signal.

**Preparation Tips:** Bring thoughtful product questions (import quality, allergen UX, expansion bets). Be crisp on proof points.

**Common Mistakes:** Overconfidence; hand-wavy AI claims; inability to discuss a shipped mistake.

### Stage 8 — Offer

**Purpose:** Level, scope, compensation/equity, start timing.

**Evaluation Criteria:** Mutual clarity on responsibilities and success metrics.

**Preparation Tips:** Confirm ownership surface, on-call, AI vs core API split, and growth path.

**Common Mistakes:** Optimizing only cash while ignoring equity/scope; accepting vague "lead everything" ownership without support.

---

## 05 - Technical Focus Areas

Company-specific topics most likely to appear for ReciMe-style panels.

### Backend Ownership

- End-to-end feature delivery: schema → API → jobs → metrics → rollback
- Operability in a small team (you will page yourself)
- Technical debt triage tied to product metrics
- Admin/debug tools for support (import replay, entitlement fixes)

### AI / ML-Assisted Features

- Multimodal pipelines: image → ingredients → recipe structure
- Prompt and model versioning; deterministic config in deploys
- Evaluation sets ("golden recipes", handwritten samples, social screenshots)
- Confidence thresholds and human edit loops
- Latency budgets and streaming partial results
- Cost controls: cache, batch, cascade models, deny runaway prompts
- Safety: allergens, medical claims, toxic content in generated text

### API Design

- Mobile-first REST/GraphQL/JSON APIs with pagination and sparse fieldsets
- Idempotency keys for imports, list mutations, purchases
- Clear error taxonomy: retryable vs terminal vs entitlement
- Versioning strategy compatible with app-store release lag
- Attachment upload (presigned URLs) and progress semantics
- Webhook/job status patterns for long AI work

### Product Thinking

- Funnel instrumentation over vanity counters
- Entitlement limits as first-class API behavior
- UX of AI failure (show uncertainty, allow fix, learn)
- Scope cuts that preserve the activation loop
- Experiment design: flags, cohorts, guardrail metrics

### Consumer Mobile Backend

- Auth session refresh; stolen token realities
- Offline edits and sync
- Push notifications without spam
- Bandwidth-aware media (image sizes, lazy load)
- Clock skew and "dinner time" local calendars

### Recipe Import & Canonicalization

- URL import, HTML parsing, social caption parsing
- Dedup by canonical URL / content hash
- Ingredient parsing (quantity, unit, name) and normalization
- Brittleness of third-party sources; ethics/ToS awareness
- Partial success: save what you can, flag what needs review

### Sync & Conflict Resolution

- Shopping list multi-device edits
- CRDT vs last-write-wins vs field-level merge — when each is honest
- Vector clocks / updated_at discipline
- Tombstones for deletes
- User-visible conflict resolution when automatic merge is unsafe

### Recommendations & Feeds

- Cold start; preference learning; exploration vs exploitation
- Ranking features from pantry, history, seasonality, time-to-cook
- Abuse/spam if social graph exists
- Privacy: do not leak another household's data via "for you"

### Reliability & Cost

- Queue backlogs during viral import spikes
- Dead letters and replay
- Media storage lifecycle
- AI budget alarms
- Graceful degradation when model provider is down

### Security & Privacy

- Account takeover of a cooking app still ruins trust
- Signed share links with expiry
- PII minimization; GDPR deletion across DB + object store + logs + model vendor where possible
- Prompt injection via recipe text/images
- Rate limits on AI endpoints to prevent cost attacks

---

## 06 - Leadership Focus

What Lead / Tech Lead panels probe beyond code.

### Ownership Multiplying

Can you make a small team faster by clarifying APIs, paving import/AI worker patterns, and killing ambiguous work?

### Product Partnership

Can you negotiate scope using user metrics and technical risk — not ego?

### AI Delivery Leadership

Can you insist on evals, kill switches, and cost budgets without being labeled a blocker?

### Mentoring in Startup Mode

Can you raise juniors' taste for edge cases (sync, idempotency, AI confidence) while still shipping weekly?

### Quality Under Speed

Do you know which corners are expensive (data loss, billing, auth) vs cheap (pixel polish)?

### Communication

Can you write a one-page RFC that mobile + product can challenge? Can you say "I was wrong" quickly?

### Hiring Bar

Can you articulate what "senior" means here: product sense + backend depth + operational ownership?

### Culture

Do you leave blame-light postmortems and high agency behind you?

---

## 07 - Behavioral Questions

### Q1. Tell me about a product feature you owned end-to-end that moved a user metric.

- **Why asked:** Product-minded ownership signal.
- **Competencies:** Ownership, measurement, delivery.
- **Excellent answer framework:**
  - **S:** Feature tied to activation/retention/conversion
  - **T:** Ship and learn quickly
  - **A:** Scoped MVP; instrumented; iterated; handled failures
  - **R:** Metric delta; what you learned next
- **Follow-ups:** What did you cut? What would you build differently now?

### Q2. Describe a production incident that hurt user trust (data loss, sync, wrong charges, bad AI).

- **Why asked:** Incident maturity in consumer context.
- **Competencies:** Composure, root cause, prevention.
- **Excellent answer framework:**
  - **S:** Symptom + user impact
  - **T:** Contain → communicate → fix → prevent
  - **A:** Rollback/flag kill; data repair; customer messaging
  - **R:** Recurrence controls; metric recovery
- **Follow-ups:** First 15 minutes? How did you repair user data?

### Q3. Tell me about shipping an AI or ML-assisted feature to production.

- **Why asked:** ReciMe differentiator depth.
- **Competencies:** AI systems, product judgment, risk control.
- **Excellent answer framework:**
  - **S:** User problem AI might solve
  - **T:** Useful, safe, affordable
  - **A:** Pipeline; evals; fallback; cost caps; UX for confidence
  - **R:** Quality/cost/latency metrics; iteration loop
- **Follow-ups:** How did you handle model provider outage? How do you detect regressions?

### Q4. Give an example of designing an API specifically for mobile clients.

- **Why asked:** Consumer mobile backend literacy.
- **Competencies:** API design, empathy, pragmatism.
- **Excellent answer framework:**
  - **S:** Chatty or brittle API hurting mobile UX
  - **T:** Reduce failures and bytes; clarify errors
  - **A:** Pagination; idempotency; compressed payloads; entitlement errors
  - **R:** Latency/error improvement; fewer client hacks
- **Follow-ups:** How did you version for slow app-store updates?

### Q5. Describe a time you enforced idempotency for imports or payments-like entitlements.

- **Why asked:** Duplicate-tap / retry reality.
- **Competencies:** Distributed systems practicality.
- **Excellent answer framework:**
  - **S:** Duplicates from retries or double submits
  - **T:** Exactly-once *effects*
  - **A:** Idempotency keys; natural keys; dedup store
  - **R:** Duplicate rate before/after
- **Follow-ups:** Retention of keys? Cross-user collisions?

### Q6. Tell me about a sync or conflict-resolution bug you fixed.

- **Why asked:** Shopping list / planner correctness.
- **Competencies:** Data modeling, careful debugging.
- **Excellent answer framework:**
  - **S:** Multi-device divergence
  - **T:** Restore correctness and define rules
  - **A:** Repro; merge rules; tests; backfill
  - **R:** Support tickets down; clear user-visible behavior
- **Follow-ups:** LWW vs merge fields — why? How tested offline?

### Q7. Describe pushing back on a product request that would have created bad UX or debt.

- **Why asked:** Backbone with taste.
- **Competencies:** Influence, product sense.
- **Excellent answer framework:**
  - **S:** Appealing request with hidden cost
  - **T:** Protect users/team while offering path
  - **A:** Evidence; alternative MVP; staged approach
  - **R:** Better outcome; relationship intact
- **Follow-ups:** When have you been wrong in a pushback?

### Q8. Tell me about cutting scope to hit a meaningful launch date.

- **Why asked:** Startup delivery realism.
- **Competencies:** Prioritization, communication.
- **Excellent answer framework:**
  - **S:** Overloaded roadmap
  - **T:** Preserve core user journey
  - **A:** Must/should/later; flag risky bits; align stakeholders
  - **R:** Launched; learned; followed up
- **Follow-ups:** What metric proved the cut was right?

### Q9. Give an example of improving a fragile parsing/import pipeline.

- **Why asked:** ReciMe import core.
- **Competencies:** Resilience, quality engineering.
- **Excellent answer framework:**
  - **S:** High failure rate on certain sources
  - **T:** Raise success without unbounded engineering
  - **A:** Taxonomy of failures; retries; heuristics; human edit; monitoring by source
  - **R:** Success rate; time-to-save; cost
- **Follow-ups:** How do you detect silent quality regressions (parse "succeeds" but wrong)?

### Q10. Describe mentoring a junior on production quality (tests, edge cases, observability).

- **Why asked:** Leadership in small teams.
- **Competencies:** Mentoring, standards.
- **Excellent answer framework:**
  - **S:** Junior shipping fragile code
  - **T:** Raise bar without crushing speed
  - **A:** Pairing; checklists; exemplar PRs; follow-up
  - **R:** Independently solid delivery later
- **Follow-ups:** How do you scale mentoring beyond 1:1?

### Q11. Tell me about a performance optimization that users actually felt.

- **Why asked:** Empathy + measurement.
- **Competencies:** Profiling, prioritization.
- **Excellent answer framework:**
  - **S:** Slow screen/API on critical path
  - **T:** Improve perceived performance
  - **A:** Measure; fix N+1/cache/payload; verify on real devices
  - **R:** p95 and qualitative feedback
- **Follow-ups:** What did you decide *not* to optimize?

### Q12. Describe handling a spike in traffic or job backlog (viral content, campaign).

- **Why asked:** Consumer spike realism.
- **Competencies:** Scalability, calm ops.
- **Excellent answer framework:**
  - **S:** Queue depth / latency explosion
  - **T:** Protect core app; drain backlog
  - **A:** Rate limits; scale workers; degrade non-critical AI; communicate
  - **R:** Recovery time; permanent capacity fixes
- **Follow-ups:** What was sacrificed first and why?

### Q13. Tell me about a disagreement with a designer or PM on technical feasibility.

- **Why asked:** Cross-functional maturity.
- **Competencies:** Collaboration, negotiation.
- **Excellent answer framework:**
  - **S:** Conflict on approach/time
  - **T:** Shared user goal
  - **A:** Prototype/spike; constraints made visible; compromise
  - **R:** Shipped; trust improved
- **Follow-ups:** How do you prevent recurring thrash?

### Q14. Give an example of feature-flagged rollout and a kill switch you used.

- **Why asked:** Safe iteration culture.
- **Competencies:** Release engineering, risk control.
- **Excellent answer framework:**
  - **S:** Risky change (AI, sync, paywall)
  - **T:** Expose gradually
  - **A:** Flag; cohort; metrics guardrails; kill switch exercise
  - **R:** Caught issue early or smooth GA
- **Follow-ups:** What guardrail metric mattered most?

### Q15. Describe enforcing freemium limits without creating support nightmares.

- **Why asked:** Monetization engineering.
- **Competencies:** Product thinking, API design.
- **Excellent answer framework:**
  - **S:** Limits bypassed or confusing
  - **T:** Fair, clear, enforceable server-side
  - **A:** Entitlement service; clear errors; edge cases (timezone, refunds)
  - **R:** Conversion + support ticket metrics
- **Follow-ups:** How do you handle clock skew / client lying?

### Q16. Tell me about deleting or exporting user data across multiple stores.

- **Why asked:** Privacy/compliance readiness.
- **Competencies:** Data architecture, responsibility.
- **Excellent answer framework:**
  - **S:** Deletion/export request spanning DB, media, vendors
  - **T:** Complete within policy window
  - **A:** Inventory; workflow; verification; vendor APIs
  - **R:** Auditable completion; fewer gaps
- **Follow-ups:** Backups? Analytics warehouses? AI vendor retention?

### Q17. Describe a time you reduced AI/inference or cloud cost materially.

- **Why asked:** COGS awareness.
- **Competencies:** Cost engineering, pragmatism.
- **Excellent answer framework:**
  - **S:** Cost rising with usage
  - **T:** Lower unit cost without cratering quality
  - **A:** Cache; model cascade; smaller images; deny abuse; architecture tweak
  - **R:** $ / active user or $ / import down; quality stable
- **Follow-ups:** What quality metric did you watch while saving money?

### Q18. Tell me about building observability for an async pipeline.

- **Why asked:** Import/AI jobs operability.
- **Competencies:** Observability, ownership.
- **Excellent answer framework:**
  - **S:** Blind "stuck importing" user reports
  - **T:** See stage-level health
  - **A:** Job states; metrics by source/model; traces; dead-letter dashboards
  - **R:** MTTR down; proactive alerts
- **Follow-ups:** How do you avoid PII in logs (recipe images, captions)?

### Q19. Give an example of a technical decision you reversed after user evidence.

- **Why asked:** Humility and empiricism.
- **Competencies:** Learning agility.
- **Excellent answer framework:**
  - **S:** Decision that failed in market
  - **T:** Reverse without drama
  - **A:** Metrics/user research; migration; communication
  - **R:** Improved retention/UX; team learned
- **Follow-ups:** Earliest warning signal?

### Q20. Describe leading a design review or RFC in a small team.

- **Why asked:** Lightweight architecture leadership.
- **Competencies:** Communication, facilitation.
- **Excellent answer framework:**
  - **S:** Cross-cutting change (sync protocol, AI worker)
  - **T:** Align fast with written clarity
  - **A:** Short RFC; options; decision; follow-ups
  - **R:** Faster implementation; fewer reworks
- **Follow-ups:** When is an RFC overkill?

### Q21. Tell me about handling ambiguous requirements with a prototype.

- **Why asked:** Startup ambiguity tolerance.
- **Competencies:** Bias to action, learning.
- **Excellent answer framework:**
  - **S:** Unclear product bet
  - **T:** De-risk with smallest experiment
  - **A:** Time-boxed spike; fake door or limited beta; measured
  - **R:** Go/no-go evidence
- **Follow-ups:** How did you avoid prototype code becoming unmaintainable prod?

### Q22. Describe a security issue you found or fixed in a consumer API.

- **Why asked:** Security is not optional at startups either.
- **Competencies:** Secure design, responsibility.
- **Excellent answer framework:**
  - **S:** IDOR, share-link leak, auth gap, prompt injection
  - **T:** Close gap; assess blast radius
  - **A:** Patch; audit sibling endpoints; regression tests
  - **R:** Vulnerability closed; process improved
- **Follow-ups:** How do you prevent recurrence in review?

### Q23. Tell me about collaborating with mobile engineers on offline behavior.

- **Why asked:** Real client constraints.
- **Competencies:** Collaboration, sync design.
- **Excellent answer framework:**
  - **S:** Offline edits causing conflicts/data loss
  - **T:** Define contract both sides trust
  - **A:** Shared sequence diagrams; conflict rules; test plan on devices
  - **R:** Fewer sync bugs; clearer UX copy
- **Follow-ups:** What did backend refuse to pretend it could solve?

### Q24. Give an example of improving search or recommendation relevance.

- **Why asked:** Feed/discovery product depth.
- **Competencies:** Ranking intuition, experimentation.
- **Excellent answer framework:**
  - **S:** Poor discovery / cold start
  - **T:** Improve useful suggestions
  - **A:** Features; baseline; A/B; failure analysis
  - **R:** CTR/save rate/cook rate changes
- **Follow-ups:** How did you detect popularity bias?

### Q25. Describe dealing with a flaky third-party dependency (social APIs, model vendor, payments).

- **Why asked:** External dependency realism.
- **Competencies:** Resilience, pragmatism.
- **Excellent answer framework:**
  - **S:** Vendor failures or ToS/rate changes
  - **T:** Protect UX
  - **A:** Timeouts; retries; circuit breakers; fallbacks; caches
  - **R:** Error budget impact reduced
- **Follow-ups:** When do you build a fallback extractor vs wait on vendor?

### Q26. Tell me about a time you said no to premature microservices.

- **Why asked:** Architecture taste for stage.
- **Competencies:** Judgment, persuasion.
- **Excellent answer framework:**
  - **S:** Split proposed without scale pain
  - **T:** Keep team fast
  - **A:** Modular boundaries; extract criteria; document triggers
  - **R:** Delivered faster; extracted later when metrics justified
- **Follow-ups:** What would make you split tomorrow?

### Q27. Describe improving code review culture without slowing shipping.

- **Why asked:** Quality leadership at speed.
- **Competencies:** Process design, empathy.
- **Excellent answer framework:**
  - **S:** Reviews noisy or rubber-stamped
  - **T:** Catch high-risk issues; keep flow
  - **A:** Risk-based review focus; templates; SLAs; pair on hard PRs
  - **R:** Fewer prod bugs; similar cycle time
- **Follow-ups:** What belongs in CI vs human review?

### Q28. Tell me about supporting a growth/marketing experiment that stressed engineering.

- **Why asked:** Cross-functional startup reality.
- **Competencies:** Partnership, resilience.
- **Excellent answer framework:**
  - **S:** Campaign/viral loop needs
  - **T:** Enable experiment without melting prod
  - **A:** Capacity; flags; rate limits; success metrics; rollback
  - **R:** Experiment ran; uptime held; learnings captured
- **Follow-ups:** What engineering constraints did you negotiate into the campaign?

### Q29. Give an example of documenting just enough for future you / a new hire.

- **Why asked:** Small-team bus-factor reduction.
- **Competencies:** Communication, sustainability.
- **Excellent answer framework:**
  - **S:** Knowledge in one person's head
  - **T:** Make system operable by others
  - **A:** Runbook; diagram; onboarding path; delete stale docs
  - **R:** Someone else shipped/fixed without you
- **Follow-ups:** How do you keep docs from rotting?

### Q30. Describe a hard tradeoff between consistency and UX responsiveness.

- **Why asked:** Explicit distributed judgment.
- **Competencies:** Architecture tradeoffs.
- **Excellent answer framework:**
  - **S:** Optimistic UI vs server truth tension
  - **T:** Choose honest UX
  - **A:** Alternatives; failure modes; user messaging
  - **R:** Measured confusion/error impact
- **Follow-ups:** How do clients reconcile when server rejects optimistic edit?

### Q31. Tell me about handling allergen or safety-sensitive content carefully.

- **Why asked:** Food domain ethics.
- **Competencies:** Judgment, product responsibility.
- **Excellent answer framework:**
  - **S:** AI/user content risk around allergies/diet
  - **T:** Reduce harm without fake certainty
  - **A:** Disclaimers; user-confirmed allergens; conservative filtering; human edit
  - **R:** Clearer UX; reduced risky claims
- **Follow-ups:** How do you avoid over-blocking legitimate recipes?

### Q32. Give an example of hiring loop or interview bar contribution (if applicable).

- **Why asked:** Lead-level multiplication.
- **Competencies:** Hiring judgment, communication.
- **Excellent answer framework:**
  - **S:** Need to raise/define bar
  - **T:** Predict on-the-job success
  - **A:** Rubric; practical exercise; calibrated feedback
  - **R:** Strong hire or confident no-hire; process reused
- **Follow-ups:** What false positive/negative did you learn from?

---

## 08 - System Design Questions

### Design 1 — Recipe Recommendation Feed

**Requirements**

- Personalized home/feed of recipes for a signed-in user
- Cold start for new users
- Incorporate preferences, history, pantry signals, time-to-cook, dietary flags
- Freshness and diversity; avoid repetitive suggestions
- p95 latency suitable for mobile scroll
- Analytics for impressions/saves/cooks

**Architecture Discussion**

- Event ingestion: views, saves, cooks, imports, pantry updates
- Feature store / offline compute for heavy signals; online store for recent actions
- Candidate generation (collaborative + content + contextual) → ranker → filters (allergens, blocked)
- API: cursor pagination; request-time filters
- Fallback: popular/trending when personalization unavailable
- Experiment framework for ranker versions

**Tradeoffs**

- Real-time personalization vs cost/complexity
- Popularity bias vs niche discovery
- On-device vs server ranking
- Strict allergen filtering false positives vs safety

**Scaling**

- Cache feed fragments per user with short TTL
- Precompute candidates offline; light re-rank online
- Protect ranker with timeouts and degrade to simpler model

**Reliability**

- Partial failure: show cached/popular rather than empty error
- Poison events isolated in feature pipeline
- Replay for backfills after bugs

**Security**

- Authz on user features; no cross-user leakage in debug tools
- Rate limit scrape of feed APIs
- Privacy of household dietary data

**Production Considerations**

- Eval dashboards (save rate, dwell, cook completion)
- Shadow traffic for new rankers
- Cost per 1000 feeds
- Clear UX when dietary filters empty the feed

### Design 2 — Image / Ingredient Recognition Pipeline

**Requirements**

- User uploads fridge/pantry photo or recipe screenshot
- Detect ingredients (and optionally quantities)
- Return structured results with confidence
- Allow user corrections that improve future quality (product loop)
- Bound latency and cost; support free-tier limits
- Safe handling of sensitive/unexpected image content

**Architecture Discussion**

- Client → presigned upload to object storage
- API creates recognition job; returns job id
- Worker: virus/size checks → image normalize → model inference (cascade: cheap classifier → multimodal LLM/vision)
- Post-process: canonicalize ingredient names; dedupe; confidence thresholds
- Persist results + model/prompt version; notify client via poll/push
- Human correction API feeds eval dataset (with consent/privacy rules)

**Tradeoffs**

- Sync inference UX vs async jobs
- Single large model vs cascaded specialists
- Higher recall vs precision for ingredients
- Storing raw images vs derived features only

**Scaling**

- Autoscale workers on queue depth
- Cap concurrent jobs per user; global AI budget governor
- Thumbnail vs full-res paths

**Reliability**

- Retries with idempotent job ids
- Dead-letter for poison images
- Provider failover / degraded "manual tag" mode

**Security**

- Authz on image access; short-lived URLs
- Prompt-injection and NSFW handling policies
- Rate limits against cost attacks
- Encryption at rest for user media

**Production Considerations**

- Golden-set evals in CI for prompt/model changes
- Cost and latency SLOs per tier
- Retention policy for images
- Support tooling to replay a job

### Design 3 — Social Sharing Feed

**Requirements**

- Users share recipes/cook results to followers or via links
- Feed of friends/following activity
- Share links for non-users (growth)
- Moderation for abuse/spam/copyright complaints
- Fan-out that does not melt write path
- Privacy controls (private cookbooks vs public)

**Architecture Discussion**

- Graph service: follow relationships
- Activity events → fan-out on write for small graphs; fan-out on read for celebrities / large graphs (hybrid)
- Feed store (per-user timeline) + ranking light sort
- Public share links: signed tokens, expiry, revoke
- Moderation queue + automated signals
- Notification hooks for new followers/comments if present

**Tradeoffs**

- Fan-out on write vs read
- Public SEO pages vs privacy defaults
- Comments/social complexity vs MVP share links only
- Copyright risk of imported content reshared

**Scaling**

- Partition timelines; cache hot users
- Async fan-out workers; backpressure
- CDN for public media

**Reliability**

- Eventual consistency OK for social; never corrupt private recipes into public accidentally
- Replayable activity log

**Security**

- Strict authz on private content
- Unforgeable share links; rate limit guessing
- Abuse detection for spam follows/shares
- Report/takedown workflows

**Production Considerations**

- Growth metrics vs moderation staffing
- Tombstone deleted content across feeds
- Legal request process for content removal

### Design 4 — Shopping List Sync

**Requirements**

- Multi-device, near-real-time sync of grocery list items
- Merge items by ingredient identity where possible
- Support check-off, quantity edits, aisle grouping
- Offline edits with conflict resolution
- Household sharing (optional multi-user list)
- Low latency; zero silent data loss

**Architecture Discussion**

- List aggregate with version / revision token
- Item entities with stable IDs; tombstones for deletes
- Sync API: pull changes since cursor; push mutations with idempotency keys
- Merge rules: checked state, quantity add vs replace policies documented
- Optional: CRDT for checkmarks; server authority for aisle metadata from catalog
- Pub/sub or push notify devices on remote change
- Aisle classifier async job when items added

**Tradeoffs**

- CRDT complexity vs LWW simplicity
- Server-authoritative vs peer sync
- Aggregate-per-list vs item event log
- Automatic ingredient merge mistakes vs user annoyance

**Scaling**

- Hot lists during dinner prep — keep payloads small (diff sync)
- Shard by list id / household id
- Rate limit pathological sync loops

**Reliability**

- Integration tests for offline interleaving
- Snapshot + compaction of event logs
- Repair endpoint for support
- Backups and point-in-time recovery for user data

**Security**

- Membership checks for household lists
- Audit of member add/remove
- Prevent list enumeration by ID

**Production Considerations**

- UX copy for conflicts users must resolve
- Metrics: sync failure rate, conflict rate, time-to-consistency
- Migration plan when merge rules change

### Design 5 — AI Meal Planner

**Requirements**

- Generate a multi-day meal plan from preferences, pantry, time budgets, dietary constraints
- Allow user edits; regenerate partial days
- Push planned recipes into grocery list aggregation
- Bound cost/latency; show progress
- Respect free/paid entitlements
- Avoid unsafe dietary advice tone; honor allergens strictly

**Architecture Discussion**

- Planning API creates plan job with input snapshot (prefs, pantry, constraints)
- Planner worker: retrieve candidate recipes → constrained generation (rules + LLM) → validate allergens/nutrition heuristics → persist plan
- Plan as first-class entity with version history for undo
- "Add missing ingredients to list" transactional workflow with idempotency
- Cache similar plans cautiously (privacy!)
- Experiment on prompt/model versions with eval harness (constraint violation rate)

**Tradeoffs**

- Fully generative menus vs rank-and-arrange existing library recipes
- Daily regenerate vs weekly stable plan
- Strict constraint solver vs LLM creativity
- Household vs personal plans

**Scaling**

- Async jobs; stream partial day results if UX needs
- Cap tokens; template-first for common cases
- Reuse embeddings/candidate sets across users carefully (no data leak)

**Reliability**

- Validation layer must catch allergen violations even if model fails
- Fallback: template meal plans from popular sets
- Kill switch per model version

**Security / Safety**

- Entitlement checks server-side
- Prompt injection via custom user notes
- Clear disclaimers; do not claim medical authority
- Privacy of health-related preferences

**Production Considerations**

- Cost per plan; abandonment during long generation
- Quality eval: constraint pass rate, edit distance by users
- Grocery aggregation correctness after edits

### Design 6 — Recipe Import Orchestrator (stretch)

**Requirements**

- Import from URL, social link, screenshot, or handwritten photo
- Normalize into canonical recipe schema
- Deduplicate; attach source attribution
- Entitlement-limited weekly imports
- High success rate with partial save + user repair UI

**Architecture Discussion**

- Ingress API → classify source → route to HTML parser / social fetcher / vision pipeline
- Canonicalization service (ingredients, steps, timing)
- Dedup index by URL hash + content fingerprint
- Outbox to search indexer and recommendation feature updates
- Status machine visible to clients

**Tradeoffs / Scaling / Reliability / Security / Production**

- Same discipline as recognition pipeline: async jobs, evals by source type, ToS-aware fetching, cost governors, PII-minimized logging, support replay tools

---

## 09 - Company Preparation Checklist

- [ ] Use the ReciMe app (free tier): import, meal plan, grocery list — note friction and delight
- [ ] Map your last 3 stories to: product metric impact, async pipelines, sync/API design, AI productionization
- [ ] Prepare one RFC-style explanation of a sync or job-orchestration decision
- [ ] Rehearse AI feature design aloud: evals, fallback, cost, confidence UX (20 minutes)
- [ ] Draft mobile API error taxonomy you have used or would use
- [ ] Prepare metrics: p95, import success, cost per request, retention/conversion if you have them
- [ ] Write a 90-day plan: learn codebase, fix one trust leak, ship one measured improvement
- [ ] List 8 questions for HM (AI COGS, sync pain, on-call, roadmap bets, ownership surfaces)
- [ ] Refresh idempotency, job state machines, pagination, and conflict merge strategies
- [ ] Practice ingredient/list merge coding problem with tests
- [ ] Prepare a "cut scope to save the activation loop" story
- [ ] Prepare a production incident story with data repair
- [ ] Align resume bullets to product language (activation, retention, import quality) without fabrication
- [ ] Mock system design: recognition pipeline + shopping list sync + meal planner
- [ ] Mock behavioral: AI ship + product pushback + sync bug
- [ ] Sleep and logistics plan for dense interview days

---

## 10 - How My Experience Maps

### Enterprise Experience

Translate large-org strengths into startup value: standards that are lightweight, incident discipline, and mentoring — drop ceremony that does not buy risk reduction.

### Performance Optimization

Emphasize mobile-perceived latency and hot paths (home, import status, list sync). Show measurement on real devices/networks when possible.

### Legacy Modernization

If you modernized monoliths, map to modularization and extracting AI workers — not multi-year rewrite theater.

### Leadership

Show how you multiplied a small team: API guidelines, paved job pipelines, review focus on data loss risks, and calm incident leadership.

### Cloud

Tie cloud work to cost, media lifecycle, queue autoscaling, and security basics — startups notice $ graphs.

### Architecture

Present pragmatic ADRs: sync protocol, idempotent imports, AI job orchestration, entitlement service. Explicitly state stage-appropriate complexity.

### Scalability

Discuss spikes (viral recipes, dinner-hour traffic) with backpressure and degradation — not infinite Kafka topologies.

### Mentoring

Evidence of raising juniors' product taste and edge-case instincts quickly.

### Product Ownership

Frame outcomes as user and business metrics: import success, weekly active planners, conversion, AI cost per active user, crash-free sync.

---

## Interview Confidence Checklist

- [ ] I can design an async AI pipeline with job status, evals, and cost governors
- [ ] I can explain shopping-list sync conflict rules clearly to a PM
- [ ] I can defend modular monolith vs microservices for this stage
- [ ] I have 5 STAR stories mapped to ReciMe themes
- [ ] I can critique a naive "just call the LLM in the request thread" design in under 3 minutes
- [ ] I know my leveling pitch (Senior vs Lead) for a small team
- [ ] I can describe a production incident including user data repair
- [ ] I have intelligent questions about import quality, AI COGS, and roadmap

---

## Mock Interview Preparation Checklist

- [ ] 45-min system design: image/ingredient recognition pipeline
- [ ] 45-min system design: shopping list sync
- [ ] 45-min system design: AI meal planner or recommendation feed
- [ ] 30-min deep dive: idempotent import + entitlements on a past system
- [ ] 45-min behavioral set: Q1, Q3, Q6, Q7, Q12, Q17
- [ ] 60-min coding: merge grocery items / parse ingredients + tests
- [ ] Feedback captured; weak stories rewritten with metrics
- [ ] Second pass mocks after gap closure

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | ReciMe product walkthrough + stack "why" + resume language mapping |
| 2 | API design, idempotency, entitlements, mobile constraints |
| 3 | AI pipelines, evals, cost, safety behavioral + technical drills |
| 4 | System design: recognition + import orchestrator |
| 5 | System design: shopping list sync + meal planner + feed |
| 6 | Leadership/behavioral battery (Q1–Q32 selective deep practice) |
| 7 | Full mock loop + gap fixes |
| 8 | Light review, questions for interviewers, rest |

---

## Estimated Preparation Time

| Profile | Focused hours |
|---------|----------------|
| Strong product backend + some AI exposure | **18–28 hours** over 1–2 weeks |
| Strong enterprise backend, light consumer/AI | **30–40 hours** over 2–3 weeks |
| Need system design + behavioral reps | **45–55 hours** over 3–4 weeks |

Allocate roughly: 25% product/domain immersion, 35% system design (AI + sync), 20% behavioral, 20% coding/API drills. Prefer shipped stories with metrics over memorizing ReciMe marketing copy.
