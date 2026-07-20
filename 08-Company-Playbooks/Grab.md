# Grab

> Reverse-engineer how Grab evaluates Senior/Lead engineers and Architects for Superapp-scale systems — multi-service reliability across rides, food, deliveries, and payments in Southeast Asian markets where geo, matching, high-QPS mobile APIs, and fraud controls decide winners.

---

## 01 - Company Overview

### Industry

Grab is a **Southeast Asia Superapp** spanning mobility, deliveries, financial services, and merchant ecosystems. It operates in multiple countries with heterogeneous regulations, payment rails, and operational realities (traffic, weather, cash vs. digital mix).

Interview implication: you are evaluated as someone who can ship **high-QPS, geo-aware, multi-country services** with ruthless ownership of reliability and ambiguous marketplace dynamics — not a single-country CRUD backend.

### Products

Know the product surface enough to map designs:

| Surface | What interviewers hear when you mention it |
|---------|--------------------------------------------|
| **Grab rides** | Matching, dispatch, ETAs, pricing, driver/consumer apps |
| **GrabFood / GrabMart** | Merchant catalog, order pipeline, courier assignment, SLAs |
| **GrabExpress** | Logistics, multi-stop, B2B delivery adjacency |
| **GrabPay / financial services** | Wallet, payments, merchant settlement, credit adjacency |
| **Merchant / partner platforms** | Onboarding, portals, settlements, promotions |
| **Platform / shared services** | Identity, localization, notifications, experiment/config, maps adjacency |

Legal/product nuance that shows maturity: each country has distinct regulatory, map data, payment, and ops constraints — designs must be **multi-country by default**, not “add locale later.”

### Engineering Culture

Expect emphasis on:

- **Ownership under ambiguity** — marketplace problems rarely have clean specs; you drive clarity.
- **Superapp platform leverage** — shared identity, payments, incentives, and infra across verticals.
- **Reliability at peak** — rain, lunch rush, payday promos, holiday spikes are design inputs.
- **SEA pragmatism** — network quality variance, device skew, cash/digital hybrid realities.
- **Move with guardrails** — feature flags, kill switches, fraud limits, staged country rollouts.

Culture signals panels probe (without fluff): bias to action, end-to-end ownership, data-informed decisions, respect for driver/merchant/consumer three-sided markets, and calm during SEVs that hit earnings and trust.

### Business Model

Take rates on rides/deliveries + payments/fintech + advertising/promotions + merchant services. Engineering impact shows up as: completion rate, ETA accuracy, payment success, fraud loss, promo ROI, app latency, driver utilization, merchant acceptance time, cross-vertical retention.

### Scale

Regional Superapp scale: high QPS mobile APIs, continuous driver location streams, bursty order creation, Kafka-heavy eventing, multi-country deployments. Hot paths: **auth, geo/location, matching/dispatch, order status, payments, promo evaluation**. Cold paths still matter: settlements, statements, analytics, dispute evidence.

### Global Presence

Core footprint across Southeast Asia (e.g., Singapore, Indonesia, Malaysia, Philippines, Vietnam, Thailand, Cambodia, Myanmar — confirm current footprint for your interview). Interviewers care about **country pack configuration**, localization, regulatory toggles, and independent failure domains per country/vertical — more than US-centric multi-region lore alone.

### Technology Direction

Themes to discuss soberly:

- Superapp consolidation and shared platform services across verticals.
- Real-time matching, logistics, and marketplace efficiency.
- Payments/wallet depth and merchant growth.
- Fraud/risk automation at promo and payment edges.
- Cloud-native reliability, observability, and cost efficiency at high QPS.
- Selective ML for ETA, fraud, recommendations — with policy and kill switches.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

1. Design for **spikes and partial failure** — lunch rush + rain + payment rail blip in the same hour.
2. Treat mobile clients as hostile networks: retries, duplicate taps, version skew.
3. Make **country differences configuration**, not forked codebases.
4. Partner with Ops, Fraud, Finance, and Care early — marketplace is multi-sided.
5. Own metrics end-to-end: not “API 200” but completed trips/orders and correct money movement.

### Ownership

- You own your domain through peak load, fraud attacks, and messy partner failures.
- “Done” means: dashboards, alerts, runbooks, kill switches, Care playbooks, and country rollout plan.
- You can explain user-visible states for rides/orders/payments when downstreams are wrong or slow.

### Technical Leadership

- Drive designs that isolate blast radius by country/vertical/service.
- Mentor on event-driven pitfalls: poison messages, reorder, exactly-once *effects*, backfills.
- Raise the bar on load testing with realistic geo and promo scenarios.

### Product Mindset

- Consumer conversion matters — but not by opening fraud holes or breaking driver fairness.
- Every promo is a distributed systems and abuse problem.
- ETA, fees, and status honesty beat optimistic lies that create Care load.

### Collaboration Style

- Work with Product, Ops, Data/ML, Fraud, Finance, Care, partner managers.
- Write designs with explicit marketplace incentives and failure UX.
- Escalate ambiguity in money, matching fairness, or regulatory toggles immediately.

### Engineering Principles

| Principle | Interview signal |
|-----------|------------------|
| Multi-country first | Config/feature packs per country; no hard-coded single-market assumptions |
| Idempotency | All money and order state transitions safe under mobile retries |
| Event-driven truth | Domain events + consumers; replayable pipelines |
| Real-time with budgets | Location/matching latency budgets; degrade gracefully |
| Fraud-aware defaults | Velocity limits, device risk, promo abuse controls |
| Observability | Trace across app → gateway → services → Kafka → payments |
| Operability | Kill switches per country/vertical; Care tooling with audit |

---

## 03 - Typical Technology Stack

Exact internal stack varies by vertical/team. Interview with **Superapp-credible defaults** and ask which apply.

### Java / Kotlin

Common for high-throughput backend services, payment-adjacent systems, and enterprise integrations.

**Why Grab:** Strong concurrency and typing for complex marketplace domains; mature ops ecosystem; fits large multi-team engineering orgs.

### Go / polyglot services

Often present for high-QPS gateways, geo/location, or latency-sensitive sidecars alongside JVM services.

**Why Grab:** Throughput and deployment flexibility; interview by discussing tradeoffs, not language religion.

### Spring Boot / service frameworks

Payment APIs, order orchestration, merchant services, admin/ops tools — Spring Boot or sibling frameworks depending on team.

**Why Grab:** Rapid delivery with security, validation, metrics, and transactional boundaries where ACID matters.

### Cloud

Multi-account/multi-region cloud footprints; per-country or per-vertical isolation patterns; managed DB, object storage, KMS, WAF, CDN for mobile assets.

**Why Grab:** Elasticity for peaks; regulatory/data considerations; HA for always-on Superapp.

### Microservices

Vertical services (ride, food, pay) + platform services (identity, promo, notification, experiment). Clear contracts; independent scale of hot paths.

**Why Grab:** Blast-radius control and team autonomy — requires disciplined event schemas and distributed tracing.

### Databases

Relational for orders/payments/ledger-adjacent data; Redis for sessions, locks, rate limits, hot geo indexes (with care); Elasticsearch for search/merchant catalogs; warehouses for analytics/fraud features.

**Why Grab:** Match consistency needs to domain — money and order state ≠ tip recommendation cache.

### Messaging

**Kafka-class eventing** as backbone: order events, location-derived signals, payment events, promo redemptions, notifications, fraud async features.

**Why Grab:** Decouples hot mobile path from downstream side effects; enables replay, consumer scale, multi-vertical integration.

### Geo / Location

Streaming locations, geohash/S2 indexing, map/ETA providers, spatial queries for matching and tracking.

**Why Grab:** Mobility and delivery are geo products; stale or wrong location destroys trust and utilization.

### CI/CD

Pipelines with automated tests, security scans, canary/staged rollouts **by country**, feature flags, rapid rollback.

**Why Grab:** Bad deploys become multi-city outages; progressive delivery is survival.

### Kubernetes

API fleets, consumers, stream processors; HPA/KEDA-style autoscaling for peaks; jobs for settlements/recon.

**Why Grab:** Standard Superapp ops model; absorbs lunch-rush elasticity.

### Infrastructure

IaC, service mesh/network policies, secrets, centralized logging, experiment platforms, config service for country packs.

**Why Grab:** Safe multi-country variation; auditability; fast mitigation.

### Mobile

High version skew; offline/poor network; aggressive retry logic on clients — backends must be idempotent.

**Why Grab:** Most revenue UX is mobile; API design is part of product reliability.

### AI / ML

ETA, fraud scoring, courier allocation assists, demand forecasting, recommendations — **advisory** to policy engines with overrides.

**Why Grab:** Material impact on marketplace efficiency; dangerous without monitoring and kill switches.

### Grab-Specific Tech Awareness

- Matching/dispatch loops and cancellation economics.
- Order state machines across merchant → courier → consumer.
- Wallet/payment authorization vs. capture/settlement.
- Promo engine: stacking rules, budgets, abuse.
- Multi-country feature flags and regulatory toggles.
- High-QPS gateway patterns: auth, rate limit, aggregation/BFF.

---

## 04 - Typical Interview Process

Loops vary by vertical (Rides, Food, Pay, Platform) and location; prepare for this Superapp-shaped process:

### Stage 1 — Recruiter Screen

**Purpose:** Motivation for Grab/SEA Superapp, level, logistics, vertical matching, on-call comfort.

**Evaluation Criteria:** Clear senior narrative; scale/marketplace relevance; communication; ownership signals.

**Preparation Tips:** 90-second story emphasizing high-QPS systems, geo/real-time, payments, or multi-service reliability. If SEA experience exists, surface it; else map adjacent peak-load and multi-region work honestly.

**Common Mistakes:** “I want Big Tech” without marketplace understanding; single-country assumptions; pure CRUD narrative for Lead roles.

### Stage 2 — Technical Interview

**Purpose:** Language depth (Java/Go/etc.), API design, concurrency, data stores, debugging production issues.

**Evaluation Criteria:** Correctness under failure; performance instincts; mobile-retry awareness; clarity.

**Preparation Tips:** Refresh idempotency, Kafka consumer patterns, Redis misuse pitfalls, timeouts/deadlines, geo indexing basics.

**Common Mistakes:** Ignoring duplicate requests; “exactly-once Kafka” mythology; unbounded queries; logging PII/tokens.

### Stage 3 — Coding Assessment

**Purpose:** Practical coding — state machines, rate limits, geo/hash helpers, concurrent structures, sometimes take-home.

**Evaluation Criteria:** Edge cases, tests, complexity, readable interfaces, failure modes.

**Preparation Tips:** Practice: order status transition validator; sliding-window rate limiter; geohash neighbor expansion; idempotent payment callback handler.

**Common Mistakes:** Happy-path only; race conditions on status updates; no timezone/country awareness when relevant.

### Stage 4 — System Design

**Purpose:** Superapp designs: matching, order pipelines, location streaming, promo engines, payment wallets.

**Evaluation Criteria:** Requirements clarity; real-time budgets; multi-country; failure UX; fraud/ops awareness; scale math.

**Preparation Tips:** Drill Section 08. Always state QPS assumptions, peak multipliers, and degradation modes.

**Common Mistakes:** Designing Twitter; ignoring drivers/merchants; no Kafka replay story; single DB for everything including location firehose.

### Stage 5 — Leadership Interview

**Purpose:** Ownership under scale/ambiguity, mentoring, cross-team influence with Ops/Fraud, incident leadership.

**Evaluation Criteria:** End-to-end accountability; calm SEV presence; ability to drive clarity when product is fuzzy.

**Preparation Tips:** STAR with peak incident, fraud/promo abuse response, or multi-team delivery across services.

**Common Mistakes:** Blame external partners exclusively; no personal ownership; glorifying heroics without systemic fix.

### Stage 6 — Hiring Manager

**Purpose:** Vertical fit (rides vs. food vs. pay vs. platform), roadmap, on-call, leveling, country scope.

**Evaluation Criteria:** Judgment, collaboration with non-eng, growth trajectory, appetite for marketplace complexity.

**Preparation Tips:** Ask about peak QPS, top SEV classes, fraud SLAs, country rollout process, platform dependencies.

**Common Mistakes:** Not clarifying vertical ownership boundaries; ignoring ops partner model.

### Stage 7 — Final Interview

**Purpose:** Bar raise, culture consistency, sometimes architecture deep dive with senior leaders.

**Evaluation Criteria:** Integrity, senior judgment, story consistency, SEA/product empathy.

**Preparation Tips:** Prepare questions on Superapp platform strategy, reliability investment, cross-vertical leverage.

**Common Mistakes:** Inconsistent risk posture (fraud vs. growth) across interviews; hand-wavy multi-country answers.

### Stage 8 — Offer

**Purpose:** Leveling, location/comp, scope of ownership (countries/verticals).

**Evaluation Criteria:** Mutual clarity on success metrics for first 6 months.

**Preparation Tips:** Confirm on-call, country coverage, platform vs. vertical mandate, peak-season expectations.

**Common Mistakes:** Accepting Lead title without clear authority over technical direction or multi-squad influence.

---

## 05 - Technical Focus Areas

### Superapp Microservices

- Vertical services + shared platform (identity, wallet, promo, notification, config).
- API gateway / BFF for mobile aggregation; per-vertical backends.
- Contract testing; schema evolution for events; sidecars for auth/rate limit.
- Failure isolation: food outage should not take down rides login if avoidable.

### Geo / Location

- High-frequency driver/courier location updates; authenticity and spoofing concerns.
- Spatial indexes (geohash/S2); nearest-neighbor queries; map matching.
- ETA pipelines; traffic/weather inputs; freshness SLAs.
- Privacy: retention, access control, purpose limitation for location data.

### Matching / Dispatch

- Supply/demand balancing; batching vs. immediate assign; re-dispatch on cancel.
- Fairness, utilization, consumer wait time tradeoffs.
- Surge/pricing adjacency; cancellation penalties; deadlocks in assignment.
- Simulation and offline evaluation before online policy changes.

### Payments

- Authorization, capture, refunds, wallet ledger, merchant settlement.
- Idempotent charge APIs; payment rail timeouts → inquiry → terminal state.
- Multi-country rails and methods (cards, bank, wallet, cash-on-delivery adjacency).
- Reconciliation and dispute flows.

### High-QPS Mobile APIs

- Edge rate limits; auth token validation at scale; caching of session/profile.
- Payload minimization; pagination; compression; versioning.
- Timeout budgets and bulkheads; graceful degradation (cached ETA, delayed noncritical widgets).
- Load shedding with user-understandable errors.

### Kafka Eventing

- Domain events for order/ride/payment lifecycle.
- Consumer groups; partitioning keys (orderId vs. userId tradeoffs).
- Exactly-once *effects* via idempotent consumers / outbox.
- Replay, backfill, poison message handling, DLQ.
- Multi-vertical event mesh and ownership of schemas.

### Multi-Country

- Country pack: currency, timezone, tax, feature flags, compliance rules.
- Independent config rollout; blast radius limited to country.
- Localization; address formats; map provider differences.
- Regulatory reporting and data residency constraints where applicable.

### Fraud

- Promo abuse, fake GPS, account takeover, payment fraud, merchant collusion.
- Real-time features + rules + ML scores; step-up challenges.
- Actions: block, limit, delay, force re-auth, manual review.
- Shadow mode and country-specific thresholds.

### Real-Time Systems

- Location streaming, live order tracking, chat/notification adjacency.
- Websocket/SSE/push fanout patterns; backpressure.
- Consistency between tracking map and order status service.
- Degrade to polling when streams collapse.

### Additional High-Yield Topics

- Promo engine budgets, stacking, eligibility.
- Merchant catalog/search and availability.
- Courier allocation for food vs. dedicated fleets.
- Experimentation platform and metric guardianship.
- Cost control: location write amplification, hot Kafka topics, Redis memory.

---

## 06 - Leadership Focus

### Ownership

Own marketplace outcomes in your domain: completion rate, payment success, SEV minutes, fraud loss — including Ops/Care partnership and lasting controls.

### Mentoring

Teach engineers to think in state machines, peak multipliers, and multi-sided incentives — not only service code. Pair on Kafka/idempotency and incident drills.

### Decision Making

Prefer boring reliability for money and assignment correctness. Innovate in matching efficiency and UX with measured experiments.

### Cross-Team Collaboration

Co-design with Fraud, Ops, Data/ML, Finance, Care, and sibling verticals. Explicit event contracts and SLOs beat Slack tribal knowledge.

### Incident Response

SEV definitions tied to completed trips/orders, payment failures, and geo impact. Kill switches by country/vertical; factual comms; verify money and assignments before all-clear.

### Architecture Discussions

Challenge dual-writes without recon; demand idempotency on payment/order APIs; require load tests for lunch-rush and promo scenarios in RFCs.

### Technical Debt

Prioritize debt that causes SEVs, fraud loss, Care volume, or blocked country expansion. Quantify with revenue and reliability metrics.

### Engineering Culture

Blameless postmortems with mandatory guardrails. Celebrate prevented fraud and stable peaks, not only feature launches. Reward ownership that reduces ambiguity for the org.

---

## 07 - Behavioral Questions

### Q1. Tell me about a time you owned a high-QPS mobile API through a traffic spike.

- **Why asked:** Superapp peak reality.
- **Competencies:** Performance, ownership, capacity.
- **Framework:** Forecast → bottlenecks → mitigation (cache/shed/scale) → lasting fix → peak metrics.
- **Follow-ups:** What was your degradation mode? False user messaging?

### Q2. Describe an incident where matching, dispatch, or assignment failed at scale.

- **Why asked:** Marketplace core pain.
- **Competencies:** Incident leadership, systems debugging.
- **Framework:** Detect → stop bleed (pause matching/limits) → customer/driver impact → fix → fairness retro.
- **Follow-ups:** How did you avoid cascading cancels?

### Q3. Tell me about designing idempotent APIs for flaky mobile networks.

- **Why asked:** Duplicate taps are guaranteed.
- **Competencies:** API design, distributed systems.
- **Framework:** Idempotency key scope → storage → replay → expiry → conflict metrics.
- **Follow-ups:** Payment callback vs. client retry interaction?

### Q4. Give an example of partnering with Fraud or Risk on a launch.

- **Why asked:** Growth vs. abuse at Grab.
- **Competencies:** Collaboration, judgment.
- **Framework:** Abuse scenarios → controls → shadow rules → launch gates → loss/completion metrics.
- **Follow-ups:** Which control did you push back on and why?

### Q5. Tell me about a promo or incentive abuse case you helped stop.

- **Why asked:** Promo engine is a battleground.
- **Competencies:** Analytical thinking, urgency.
- **Framework:** Pattern ID → emergency kill → durable detection → ROI recovery → retro.
- **Follow-ups:** False positives on legit users?

### Q6. Describe leading a SEV affecting payments or wallet.

- **Why asked:** Money + trust.
- **Competencies:** Crisis leadership, correctness.
- **Framework:** Roles → freeze/limits → funds impact → recon → customer remediation → controls.
- **Follow-ups:** How did you prevent silent balance patches?

### Q7. Tell me about shipping a feature multi-country with different regulations.

- **Why asked:** SEA multi-market DNA.
- **Competencies:** Configuration design, stakeholder management.
- **Framework:** Country pack → phased rollout → local ops validation → metrics per country → expand.
- **Follow-ups:** What had to remain forked vs. configured?

### Q8. Share an example of Kafka consumer failure that caused business impact.

- **Why asked:** Eventing backbone literacy.
- **Competencies:** Operability, debugging.
- **Framework:** Lag/poison → mitigation → replay strategy → idempotent fix → ownership of schema.
- **Follow-ups:** How do you avoid double side effects on replay?

### Q9. Tell me about improving driver/courier location freshness or cost.

- **Why asked:** Geo systems tradeoff.
- **Competencies:** Real-time design, cost awareness.
- **Framework:** Write amplification → sampling/adaptive rates → accuracy metrics → savings.
- **Follow-ups:** Spoofing defenses?

### Q10. Describe mentoring an engineer who underestimated peak load.

- **Why asked:** Lead teaching bar.
- **Competencies:** Mentoring, standards.
- **Framework:** Incident example → load model teaching → pairing → checklist in RFC → independence.
- **Follow-ups:** How encoded in team DoD?

### Q11. Tell me about a time requirements were ambiguous in a marketplace problem.

- **Why asked:** Ownership under ambiguity — Grab signal.
- **Competencies:** Product sense, leadership.
- **Framework:** Clarify goals/metrics → propose experiments → align Ops/Product → ship gated → learn.
- **Follow-ups:** What did you decide without perfect info?

### Q12. Give an example of saying no to a growth feature that was unsafe.

- **Why asked:** Backbone under growth pressure.
- **Competencies:** Integrity, influence.
- **Framework:** Risk articulation → safer alternative → align → partial value delivered.
- **Follow-ups:** Did pressure continue? Documentation?

### Q13. Tell me about order status inconsistencies across services/apps.

- **Why asked:** Real pipeline pain (food/express).
- **Competencies:** State machines, UX honesty.
- **Framework:** Source of truth → transition rules → consumer messaging → repair tooling → Care macros.
- **Follow-ups:** How do merchants and couriers see different states?

### Q14. Describe a conflict with product on launch timing before a campaign.

- **Why asked:** Peak-season judgment.
- **Competencies:** Stakeholder management.
- **Framework:** Evidence (load test/fraud) → phased plan → decision → campaign outcome.
- **Follow-ups:** What was the go/no-go metric?

### Q15. Tell me about working with Ops or Care during a city-scale outage.

- **Why asked:** Multi-sided operations reality.
- **Competencies:** Communication, empathy.
- **Framework:** Accurate macros → tooling → escalation → remediation → follow-up fixes.
- **Follow-ups:** What Care capability did you build after?

### Q16. Share a payment rail timeout / ambiguous authorization story.

- **Why asked:** Real payments engineering.
- **Competencies:** Ambiguity management, correctness.
- **Framework:** Timeout → inquiry → pending policy → finalize/recon → customer UX.
- **Follow-ups:** Double-charge prevention?

### Q17. Tell me about improving observability across a multi-service journey.

- **Why asked:** Superapp debugging needs traces.
- **Competencies:** Operability.
- **Framework:** Blind spot → correlation IDs → RED/USE dashboards → alert quality → MTTD drop.
- **Follow-ups:** How avoid PII in logs?

### Q18. Describe introducing stronger review standards after a SEV.

- **Why asked:** Culture building.
- **Competencies:** Quality leadership.
- **Framework:** Checklist (idempotency, country flags, load) → PR/RFC adoption → escaped defect drop.
- **Follow-ups:** Bureaucracy avoidance?

### Q19. Tell me about a data migration or backfill on a hot domain (orders/payments).

- **Why asked:** Upgrade safety under continuous traffic.
- **Competencies:** Migration discipline.
- **Framework:** Expand/contract → dual-write/read → validator → cohort → rollback.
- **Follow-ups:** How verified historical receipts?

### Q20. Give an example of cross-vertical delivery (e.g., pay + food + promo).

- **Why asked:** Superapp integration complexity.
- **Competencies:** Coordination, contracts.
- **Framework:** Sequence diagram → SLAs → failure contracts → joint test → staged country launch.
- **Follow-ups:** What contract broke in staging?

### Q21. Tell me about using kill switches during production chaos.

- **Why asked:** Mitigation speed.
- **Competencies:** Release engineering, calm.
- **Framework:** Switch design → per-country scope → drill → incident use → cleanup/audit.
- **Follow-ups:** Who can flip money/matching switches?

### Q22. Describe a time ML/ETA predictions harmed user trust and you responded.

- **Why asked:** Real-time ML accountability.
- **Competencies:** Judgment, monitoring.
- **Framework:** Detect drift → fallback heuristic → root cause → guardrails → restore.
- **Follow-ups:** How do you gate model rollout by city?

### Q23. Tell me about rate limiting or abuse controls on public APIs.

- **Why asked:** High-QPS edge reality.
- **Competencies:** Security, product balance.
- **Framework:** Abuse pattern → limit design → user messaging → break-glass → effectiveness.
- **Follow-ups:** Legit bulk merchant traffic handling?

### Q24. Share how you prioritized reliability debt against growth OKRs.

- **Why asked:** Lead judgment.
- **Competencies:** Prioritization.
- **Framework:** Risk-ranked debt → tie to SEV/fraud/Care → negotiate capacity → before/after.
- **Follow-ups:** What did you defer?

### Q25. Tell me about an architectural decision you later reversed.

- **Why asked:** Humility and learning.
- **Competencies:** Reflection, adaptability.
- **Framework:** Bet → disconfirming signal → migration → standard updated.
- **Follow-ups:** Early indicator missed?

### Q26. Describe coaching a team through noisy on-call during peaks.

- **Why asked:** Sustainable ops.
- **Competencies:** People leadership, SRE basics.
- **Framework:** Alert audit → SLO paging → runbooks → rotation health → peak staffing.
- **Follow-ups:** Actionable page rate?

### Q27. Tell me about handling GPS spoofing or fake supply.

- **Why asked:** Mobility fraud specificity.
- **Competencies:** Security, marketplace integrity.
- **Framework:** Signal features → detection → enforcement → driver appeals → iterate.
- **Follow-ups:** Collusion patterns?

### Q28. Give an example of load testing before a major campaign or holiday.

- **Why asked:** Peak readiness culture.
- **Competencies:** Capacity planning.
- **Framework:** Traffic model → critical journeys → bottlenecks → fixes → go/no-go.
- **Follow-ups:** What still surprised in prod?

### Q29. Tell me about influencing standards across multiple squads/verticals.

- **Why asked:** Staff/Lead multi-team impact.
- **Competencies:** Influence without authority.
- **Framework:** Shared SEV theme → RFC/guild → reference impl → adoption metrics.
- **Follow-ups:** Who resisted?

### Q30. Describe delivering bad news about slipping a campaign-tied deadline.

- **Why asked:** Integrity under commercial pressure.
- **Competencies:** Communication, ethics.
- **Framework:** Early warning → options → recommend → protect users/funds → rebuild trust.
- **Follow-ups:** Relationship afterward?

### Q31. Tell me about optimizing Kafka partition keys or hot partitions.

- **Why asked:** Eventing scale depth.
- **Competencies:** Distributed systems.
- **Framework:** Hot key diagnosis → re-key/shard strategy → migration → lag recovery.
- **Follow-ups:** Ordering requirements preserved?

### Q32. Share a story of driver/merchant fairness vs. consumer ETA tradeoffs.

- **Why asked:** Multi-sided marketplace judgment.
- **Competencies:** Product thinking, ethics.
- **Framework:** Metric conflict → experiment → ops input → decision → monitored outcomes.
- **Follow-ups:** Who lost and how mitigated?

### Q33. Tell me about a wallet top-up or payout pipeline you designed/improved.

- **Why asked:** GrabPay adjacency.
- **Competencies:** Payments, recon.
- **Framework:** State machine → rails → recon → Care tools → success metrics.
- **Follow-ups:** Cross-border or multi-rail complexity?

### Q34. Describe reducing Care volume with better status honesty or tooling.

- **Why asked:** Operational product sense.
- **Competencies:** Empathy, operability.
- **Framework:** Top contacts → root UX/system cause → fix → macro update → volume drop.
- **Follow-ups:** Which statuses were lies before?

### Q35. Tell me about owning a problem end-to-end when no single team owned it.

- **Why asked:** Ambiguity + ownership — core Grab Lead signal.
- **Competencies:** Leadership without authority.
- **Framework:** Map journey → convene owners → propose RACI → ship interface/SLO → lasting ownership.
- **Follow-ups:** What still orphaned?

---

## 08 - System Design

### Scenario 1 — Ride Matching / Dispatch

**Prompt:** Design matching that assigns nearby drivers to ride requests with strong peak behavior.

**Clarify:** Cities in scope; QPS; matching radius; batching window; cancellation; pooling or not; pricing/surge adjacency; fairness.

**Requirements:**
- Functional: request → match → accept/reject → re-match → trip start.
- Non-functional: match latency budget, high cancel resilience, multi-city isolation, fraud/spoof awareness.

**High-level design:**
- Ride request service writes request; emits `RideRequested`.
- Supply index: drivers by geohash/S2 cells with status (available), refreshed from location service.
- Matcher: query candidate cells → rank (ETA, rating, fairness) → offer → wait accept → commit assignment.
- Assignment store: strong consistency on rideId state machine; optimistic locks.
- Re-match worker on timeout/reject/cancel.
- Kafka events for notifications, pricing, analytics.

**Scale tactics:** Per-city matching workers; sharded supply indexes; avoid global locks; circuit-break matching in catastrophic map failure with Ops mode.

**Failure modes:** Double assignment → fencing tokens; stale location → max age; thundering re-match → jitter/backoff.

**Observability:** Match rate, time-to-assign, cancel rate, supply shortage by cell, offer accept rate.

**Tradeoffs:** Immediate greedy assign vs. batch optimization; Redis geo vs. specialized index; ML ranking vs. heuristic.

**What interviewers push:** “Rain doubles demand — what breaks?” “How prevent two riders getting same driver?”

---

### Scenario 2 — Food Order Status Pipeline

**Prompt:** Design the order lifecycle from cart checkout to delivered, with merchant and courier legs.

**Clarify:** Countries; COD vs. prepaid; merchant SLA; courier allocation model; substitutions; cancellations.

**Requirements:**
- Clear state machine; idempotent transitions; multi-party visibility; event-driven side effects.

**States (example):** `CREATED` → `PAID`/`PAYMENT_PENDING` → `MERCHANT_ACCEPTED` → `PREPARING` → `COURIER_ASSIGNED` → `PICKED_UP` → `DELIVERED` / `CANCELLED` + reason codes.

**Design:**
- Order service as state authority; transitions validated with who/why.
- Outbox → Kafka topics (`order.events`) consumed by: merchant notify, courier dispatch, tracking, promo redemption finalize, Care index, analytics.
- Payment service interaction: auth on place; capture on milestones per country policy; refunds on cancel rules.
- Courier assignment service similar to matching but with restaurant prep-time constraints.
- Read models/BFF for consumer, merchant, courier apps.

**Consistency:** UI may lag; never invent `DELIVERED`; repair jobs for stuck states; Care tools with audit.

**Peaks:** Lunch rush — queue merchants, prioritize dispatch, degrade noncritical recommendations.

**What interviewers push:** “Merchant never accepts — what happens?” “Payment success but order create fails?”

---

### Scenario 3 — Driver / Courier Location Streaming

**Prompt:** Design ingestion and query of high-frequency location updates for tracking and matching.

**Clarify:** Update Hz; number of active drivers; authenticity; retention; query patterns (nearest, path, live track).

**Design:**
- Mobile SDK → edge gateway (auth, rate limit, validate) → location ingestion service.
- Hot path write: Redis/memory index by cell for matching; async Kafka for tracking history and analytics.
- Adaptive sampling: reduce frequency when idle/far from trips; increase when on-trip.
- Tracking API: latest location + recent path; permissioned to trip parties.
- Anti-spoof: device signals, jump detection, impossible speed, attestation where available.

**Scale math:** Speak QPS = drivers × updates/sec; show sharding by city/cell; backpressure when Kafka lags.

**Privacy:** Encrypt; short retention for raw points; access audited; purpose-limited.

**Degradation:** If ingestion lags, matching uses last-known with age penalty; tracking shows stale banner.

**Tradeoffs:** Push vs. pull; store every point vs. downsample; separate clusters per country.

**What interviewers push:** “10× drivers for a festival — cost controls?” “Spoofed GPS creating fake supply?”

---

### Scenario 4 — Promo Engine

**Prompt:** Design a promotion eligibility and redemption service used across rides/food/pay.

**Clarify:** Promo types (percent, fixed, cashback, free delivery); stacking; budgets; targeting; abuse; multi-country.

**Design:**
- Promo catalog service: rules, windows, country, vertical, merchant constraints.
- Eligibility API at checkout: user attributes, device, history features, cart/ride context → decision + discount quote.
- Redemption: reserve budget atomically; finalize on order/trip success; release on cancel.
- Fraud hooks: velocity, multi-account graphs, device farms; shadow rules.
- Experimentation: holdouts; metric guards (margin, completion).
- Kafka: `PromoReserved`, `PromoFinalized`, `PromoReleased` for finance and analytics.

**Consistency:** Budget must not go heavily negative; accept tiny overspend only with explicit policy; idempotent reserve keys.

**Performance:** Cache catalogs; precompute segments; avoid heavy ML on synchronous path without timeout fallback.

**What interviewers push:** “Stacking two promos?” “Budget exhausted mid-rush?” “Cross-vertical coupon abuse?”

---

### Scenario 5 — Payment Wallet for Marketplace

**Prompt:** Design a consumer wallet that pays for rides/food and supports top-up, cash-in, and merchant settlement adjacency.

**Clarify:** Countries/rails; ledger model; P2P or not; limits; KYC tiers; offline merchants.

**Design:**
- Ledger service: double-entry postings; available vs. hold balances; immutable journal.
- Wallet API: top-up, pay-order, refund, cash-out — all idempotent.
- Holds: authorize hold on order place; capture/release on terminal order states.
- Rails adapters: bank/card/wallet partners with inquiry for ambiguous states.
- Recon workers vs. rails and vs. vertical order services.
- Risk: limits, device binding, step-up auth; freeze workflows.
- Kafka outbox for notifications and accounting exporters.

**Invariants:** Never mint money in app DB without journal; corrective entries not silent edits.

**Failure modes:** Vertical says paid, ledger doesn’t → recon + Care; double client submit → idempotency key.

**What interviewers push:** “Exactly-once payment?” (effects, not magic) “Cross-border?” “COD hybrid?”

---

### Scenario 6 — (Bonus) Real-Time Order Tracking Fanout

**Prompt:** Fan out location + status to millions of consumer app sessions efficiently.

**Sketch:** Trip-scoped pubsub channels; gateway fanout shards; subscribe authZ; coalesce updates; fallback polling; protect ingestion from slow clients with buffer drops and latest-wins.

**Push topics:** Thundering reconnect after outage; battery/network cost; consistency with order state.

---

## 09 - Company Preparation Checklist

- [ ] Identify target vertical (Rides, Food, Pay, Platform) and country scope
- [ ] Read Grab engineering blog / career posts for stack and culture cues
- [ ] Prepare 2 peak-load / high-QPS stories with numbers
- [ ] Prepare 1 Kafka/event-driven production story
- [ ] Prepare 1 payments or ledger/idempotency story
- [ ] Prepare 1 fraud/promo abuse or risk partnership story
- [ ] Prepare 1 ambiguity/ownership story (no clear owner)
- [ ] Prepare 1 multi-country or multi-region config rollout story
- [ ] Drill designs: matching, order pipeline, location streaming, promo, wallet
- [ ] Practice degradation modes for lunch rush + payment blip
- [ ] List 8 HM questions (QPS, SEV classes, fraud SLA, country packs, on-call)
- [ ] Map resume bullets to geo, matching, payments, Kafka, mobile APIs

---

## 10 - How My Experience Maps

Fill before the loop. Prefer production evidence over aspirations.

| Grab signal | My evidence (system, decision, metric) | Gap / how I’ll speak to it |
|-------------|----------------------------------------|----------------------------|
| High-QPS mobile APIs | | |
| Geo / location systems | | |
| Matching / dispatch / allocation | | |
| Order / booking state machines | | |
| Kafka / event-driven pipelines | | |
| Payments / wallet / ledger | | |
| Promo / fraud / abuse controls | | |
| Multi-country configuration | | |
| Peak incident leadership | | |
| Ownership under ambiguity | | |
| Cross-vertical / multi-team delivery | | |
| Kill switches / progressive delivery | | |

**Narrative bridge examples:**
- Telecom/network peaks → Grab lunch-rush API shedding and HPA.
- Fintech ledger → GrabPay wallet holds/captures.
- Logistics/routing → courier assignment and ETA honesty.
- E-commerce checkout promos → stacking rules and budget reservation.
- Notification platforms → tracking fanout and Care macros.

---

## Interview Confidence Checklist

- [ ] I can estimate QPS and peak multipliers for a mobile Superapp path
- [ ] I can whiteboard ride matching with double-assign prevention
- [ ] I can design an order state machine with payment holds
- [ ] I can explain Kafka idempotent consumers and replay safely
- [ ] I can discuss location streaming cost vs. freshness
- [ ] I can design promo budget reservation under abuse
- [ ] I have 5 STAR stories for ownership, SEV, fraud, ambiguity, multi-country
- [ ] I can describe kill switches scoped by country/vertical
- [ ] I know my first-6-month value prop for the target vertical
- [ ] I can stay calm when pushed on money + marketplace fairness edge cases

---

## Mock Interview Preparation Checklist

- [ ] Mock recruiter: 90-second Grab/SEA Superapp pitch
- [ ] Mock coding: idempotent state transition + rate limiter tests
- [ ] Mock system design #1: ride matching (45–60 min)
- [ ] Mock system design #2: food order pipeline or wallet (45–60 min)
- [ ] Mock leadership: ambiguity ownership + SEV story under follow-ups
- [ ] Mock HM: questions on peaks, fraud, country rollout
- [ ] Record one design; fix clarity of state machine + event flow
- [ ] Red-team promo design for multi-account abuse
- [ ] Red-team wallet design for double-capture
- [ ] Timeboxed refresh: Kafka, Redis, concurrency, geo indexing basics

---

## Suggested Revision Plan

| Day | Focus | Exit criteria |
|-----|-------|---------------|
| 1 | Company + vertical research; story selection | 8 STAR stories tagged to Grab signals |
| 2 | Mobile APIs + Kafka + idempotency refresh | Explain exactly-once effects cold |
| 3 | Matching + location streaming designs | Two full designs with peak math |
| 4 | Order pipeline + promo engine | State machine + budget reservation crisp |
| 5 | Wallet/payments + fraud stories | Ledger invariants + SEV narrative ready |
| 6 | Coding kata + multi-country config talking points | Clean solution + country-pack examples |
| 7 | Full mock loop + gap fill | Weak areas restudied |

Compress to 3 days if needed: Day A stories+Kafka/idempotency, Day B matching+orders+wallet designs, Day C mock+gaps.

---

## Estimated Preparation Time

| Track | Hours |
|-------|-------|
| Company/vertical research + narrative mapping | 3–4 |
| Behavioral story polishing (30+ Q familiarity) | 6–8 |
| Technical refresh (APIs, Kafka, geo, concurrency) | 5–7 |
| System design drills (5–6 scenarios × 1–1.5 hr) | 8–12 |
| Mocks + feedback incorporation | 4–6 |
| **Total** | **~26–37 hours** |

For Lead/Architect loops, add 4–6 hours on Superapp platform strategy, cross-vertical contracts, and multi-country failure domains.

---

*Use this playbook to reverse-engineer the panel — then fill Section 10 with your production evidence until every claim is defensible under follow-up.*
