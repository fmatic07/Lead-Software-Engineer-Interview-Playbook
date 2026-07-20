# Globe Telecom

> Reverse-engineer how Globe evaluates Lead/Senior engineers for telco-scale platforms — availability, traffic spikes, integrations, and operational ownership.

---

## 01 - Company Overview

### Industry

Globe is a major Philippine telecommunications and digital services company. Engineering interviews assume you understand that telco systems sit on top of networks, billing, identity, and partner ecosystems where **downtime is immediately customer-visible** and often commercially expensive.

What panels care about: you design for high availability, burst traffic, and messy real-world integrations — not lab-perfect microservices diagrams.

### Products

Expect your role to touch one or more of:

- Consumer mobile / broadband digital channels (self-care, load, promos)
- Account, subscription, and entitlement services
- Messaging / notification / SMS-related platforms
- Payments and load distribution partnerships
- Enterprise / B2B connectivity and digital products
- Internal platforms enabling product squads (APIs, observability, CI/CD)
- BSS/OSS-adjacent integrations (billing events, provisioning, network/service orchestration touchpoints)

You do not need to memorize product marketing names. You need fluency in subscriber lifecycle, prepaid/postpaid differences, and "event happened in network/channel → must land correctly downstream."

### Engineering Culture

Large telco engineering orgs typically blend:

- Product squads shipping digital experiences quickly
- Platform/SRE groups owning shared runtime and reliability
- Long-lived integration realities with billing and legacy stacks
- Vendor and partner dependencies outside your deploy boundary

Signal that you can deliver iteratively **without** pretending legacy and BSS constraints do not exist.

### Business Model

Connectivity revenue plus digital services/adjacent ecosystems. Engineering impact maps to:

- Reduced churn via reliable self-care and service quality
- Lower cost-to-serve (fewer call-center tickets from digital failures)
- Campaign/promo agility without collapsing platforms
- New digital revenue with controlled operational risk

Frame stories as availability + conversion + cost-to-serve, with metrics.

### Scale

Subscriber-scale traffic: millions of customers, spiky campaign events, payday/promo bursts, outage-driven retry storms. Interviews probe:

- Horizontal scale and backpressure
- Cache and datastore hot keys (MSISDN/subscriber id)
- Multi-AZ / DR thinking appropriate to criticality
- Graceful degradation when a dependency dies

### Global Presence

Primarily Philippines-focused operations with global vendor/cloud/partner linkages. Interview relevance:

- Local latency and data considerations
- Partner API reliability across organizations
- Follow-the-sun vendor support vs local on-call ownership

### Technology Direction

Common themes in telco digital engineering:

- Java/Spring services on cloud + Kubernetes
- API-first channel backends
- Event-driven ingestion for usage/billing/notifications
- Heavy investment in DevOps, CI/CD, and observability
- Selective modernization around BSS/OSS edges
- Growing data/AI use for personalization, ops, and fraud/abuse — behind operational controls

Do not pitch "rip out billing." Pitch durable events, anti-corruption layers, and measurable cutovers.

---

## 02 - Engineering Expectations

### What Makes Engineers Successful

1. **Availability mindset** — failure is normal; customer impact is the metric.
2. **Integration realism** — timeouts, partial success, replay, reconciliation with BSS/partners.
3. **Traffic intuition** — spikes, thundering herds, retry amplification.
4. **Operability** — dashboards, traces, runbooks, capacity signals before incidents.
5. **Leadership in hybrid orgs** — influence across digital, platform, vendors, and ops.

### Ownership

Own services through traffic events and incidents. Ownership includes:

- Clear SLOs for customer-critical journeys (login, load, subscribe, notify)
- Capacity plans for known spikes (campaigns, celebrity events, disasters)
- Documented degradation modes when billing/network dependencies fail

### Technical Leadership

Leads are expected to:

- Set standards for retries, idempotency, and timeout budgets
- Drive Kubernetes/cloud operational maturity on their squads
- Unblock cross-team integrations with explicit contracts
- Mentor engineers on production debugging at scale

### Product Mindset

Translate journeys into platform requirements:

- "Send promo SMS to segment X" implies rate limits, quiet hours, opt-out, and delivery receipts
- "Show load balance" implies consistency expectations vs billing lag
- Feature launches include observability and rollback, not only UI

### Collaboration Style

- Work with network/BSS/ops partners as first-class stakeholders
- Prefer contracts and SLAs over hallway assumptions
- Escalate early when a dependency cannot meet a campaign date safely

### Engineering Principles (interview-usable)

- Prefer **timeouts + bulkheads** over infinite retries
- Prefer **idempotent consumers** for telco event storms
- Prefer **paved-road deploy/observe** over snowflake servers
- Prefer **degrade & communicate** over cascading failure
- Prefer **event + reconcile** at BSS edges over distributed transactions across vendors

---

## 03 - Typical Technology Stack

Explain each as *why Globe-scale interviews care*.

### Java

Core language for high-throughput backend services. Expect depth in concurrency, GC/latency, connection pooling, and defensive I/O around partner APIs.

### Spring Boot

Standard service runtime for APIs and workers. Be ready on:

- WebFlux vs MVC tradeoffs for high-concurrency gateways (only if you have real experience)
- Actuator health that reflects dependency reality
- Config management across envs for campaign-driven toggles
- Resilience patterns (circuit breakers, retries, rate limiters)

### Cloud

Cloud (often multi-account/landing-zone style) for digital workloads. Matters for:

- Elastic capacity for campaigns
- Network paths to on-prem/legacy billing systems
- Managed data services and secret management
- Cost awareness at telco request volumes

### Microservices

Channel and domain services with independent deployability. Telco twist: many "microservices" still orbit **monolithic billing/provisioning** systems — design anti-corruption layers honestly.

### Databases

Relational stores for subscriber/account state; caches for hot reads; sometimes NoSQL for high-volume event/session data. Expect:

- Hot-key mitigation for MSISDN-centric access
- Read replicas for self-care read load
- Careful multi-writer avoidance across services

### Messaging

Kafka/Rabbit/cloud pub-sub for notifications, usage/billing events, async integrations. Critical for absorbing spikes and decoupling channel apps from slow downstreams.

### CI/CD

Mandatory for frequent digital releases. Interviewers look for:

- Pipeline quality gates
- Progressive delivery / feature flags for promos
- Fast rollback
- Environment promotion discipline

### Kubernetes

Central to modern telco digital platforms. Expect conversation on:

- HPA/scaling policies for burst traffic
- Resource requests/limits and noisy neighbor control
- Rolling updates, readiness/liveness correctness
- Multi-cluster or DR patterns at a high level
- Ingress, service mesh, or API gateway integration

### Infrastructure

IaC, networking to hybrid estates, observability backends, artifact registries. Emphasize reproducible environments and least-privilege runtime identity.

### Frontend

Self-care web/mobile often adjacent. Backend leads should design APIs for intermittent mobile networks: idempotent POSTs, clear retry guidance, pagination, and cacheable GETs where safe.

### AI (where applicable)

Churn models, ops anomaly detection, NLU for care bots — discuss data pipelines, feedback loops, and the danger of automated actions without kill switches. Reliability of the serving path still matters more than model novelty in most lead interviews.

### DevOps / Observability (explicit for Globe)

Treat as first-class stack, not accessory:

- Metrics, logs, traces with subscriber/correlation identifiers (privacy-aware)
- Golden signals for critical journeys
- Alert routing to owning squads
- Capacity and saturation dashboards before big events

---

## 04 - Typical Interview Process

### Stage 1 — Recruiter Screen

**Purpose:** Fit, level, availability, motivation for telco/digital scale.

**Evaluation Criteria:** Clear narrative; credible scale/HA experience; communication; constraints.

**Preparation Tips:** 90-second pitch emphasizing availability, integrations, and leadership. Mention spike/event traffic if you have it.

**Common Mistakes:** Generic startup pitch; ignoring enterprise/telco constraints; title inflation without ownership.

### Stage 2 — Technical Interview

**Purpose:** Java/Spring depth, APIs, data, concurrency, cloud/K8s literacy.

**Evaluation Criteria:** Correctness; production debugging skill; realistic dependency thinking.

**Preparation Tips:** Stories for: outage, performance win, bad retry storm you fixed, K8s/deploy incident.

**Common Mistakes:** Cloud buzzwords without failure modes; "just scale pods" as the only answer.

### Stage 3 — Coding Assessment

**Purpose:** Practical implementation quality under time constraints.

**Evaluation Criteria:** Edge cases; concurrency safety; readable structure; tests; API clarity.

**Preparation Tips:** Practice parsers, rate limiting sketches, idempotent handlers, and stream/aggregation problems with clear complexity talk.

**Common Mistakes:** Happy path only; blocking I/O assumptions; ignoring invalid subscriber identifiers/input.

### Stage 4 — System Design

**Purpose:** Design telco-scale systems with HA, spikes, and integration edges.

**Evaluation Criteria:** Requirements discovery; bottleneck analysis; degradation modes; observability; security; evolution.

**Preparation Tips:** Always ask about QPS peaks, fanout (SMS), delivery guarantees, and downstream SLAs (billing). Draw bulkheads.

**Common Mistakes:** Ignoring retry amplification; single-region hand-waving; BSS as a fictional perfectly available DB.

### Stage 5 — Leadership Interview

**Purpose:** Influence, mentoring, cross-team delivery, incident leadership.

**Evaluation Criteria:** Ownership; calm under incident load; standards setting; stakeholder management (including vendors).

**Preparation Tips:** STAR for major incident, campaign launch, and conflict with another team/vendor.

**Common Mistakes:** Hero culture stories without systemic fixes; blaming "legacy" without a strategy.

### Stage 6 — Hiring Manager

**Purpose:** Team mission fit, leveling, working model, 90-day expectations.

**Evaluation Criteria:** Self-awareness; learning agility in telco domain; collaboration style.

**Preparation Tips:** Ask about critical journeys, on-call, K8s maturity, and top reliability risks this quarter.

**Common Mistakes:** Only asking about languages/frameworks; no questions on peak events or BSS dependencies.

### Stage 7 — Final Interview

**Purpose:** Senior calibration, architecture judgment, culture/org fit.

**Evaluation Criteria:** Tradeoff quality; communication to mixed audiences; long-term ownership signal.

**Preparation Tips:** Bring questions on platform roadmap, SRE relationship, and how success is measured for leads.

**Common Mistakes:** Rewrite-everything proposals; inability to discuss a failure honestly.

### Stage 8 — Offer

**Purpose:** Level, scope, compensation, start date.

**Evaluation Criteria:** Mutual clarity on ownership and success metrics.

**Preparation Tips:** Clarify on-call, incident severity model, and whether "lead" includes architectural authority vs people management.

**Common Mistakes:** Accepting ambiguous scope; ignoring operational load in total role evaluation.

---

## 05 - Technical Focus Areas

### Kubernetes

- Workload design (Deployments, Jobs/CronJobs, autoscaling)
- Probes that match real dependency health
- Resource economics and eviction risks
- Rollouts, canaries, and abort criteria
- Cluster failure / node drain mental models

### Cloud

- Hybrid connectivity to on-prem systems
- Elasticity for campaigns
- IAM least privilege for services
- Managed vs self-hosted tradeoffs for Kafka/DB

### Java at Scale

- Thread pools / reactive boundaries
- GC and allocation pressure under load
- Connection pool saturation as a silent outage
- Efficient serialization and payload control

### Scalability

- Horizontal scale + stateful dependency limits
- Caching (what is safe to cache for subscriber data)
- Hot keys (celebrity MSISDNs, popular promo codes)
- Queue-based load leveling

### DevOps

- CI/CD promotion paths
- Feature flags for promos
- Infrastructure as code
- Shift-left security scanning without blocking blindly

### Telco-Scale Traffic

- Spikes, fanout, and retry storms
- Rate limiting at edge and per downstream
- Backpressure and load shedding
- Traffic shaping for SMS/email/push gateways

### BSS/OSS-ish Integrations

- Billing event ingestion and reconciliation
- Provisioning/activation async flows
- Anti-corruption layers around legacy APIs
- Duplicate events and out-of-order arrivals

### High Availability

- Multi-AZ services; dependency HA is not your HA
- Graceful degradation (read-only mode, delayed balance, queued notifications)
- DR objectives matched to journey criticality
- Game days / failover drills

### Observability

- RED/USE or golden signals for critical APIs
- Trace propagation across gateway → service → partner
- Privacy-safe subscriber correlation
- Alert quality (actionable, not noisy)

### Practical Drill List

Be ready to whiteboard:

1. HPA + queue depth scaling for a notification worker
2. Idempotent billing event consumer with recon
3. Cache strategy for subscriber profile reads
4. Edge rate limit + bulkhead to protect billing adapter

---

## 06 - Leadership Focus

### Ownership

Accountable for journey SLOs through spikes and vendor failures — not only code merged.

### Mentoring

Teach production debugging: reading saturation metrics, tracing retries, writing runbooks juniors can execute at 2am.

### Decision Making

ADRs for caching, messaging, and degradation policies. Make "what we shed first" an explicit product decision.

### Cross-team Collaboration

Digital ↔ platform ↔ billing ↔ vendor. Use written contracts, shared dashboards, and joint incident reviews.

### Incident Response

Severity by customer impact and journey criticality. Coordinate war rooms, communicate status, preserve timelines, drive blameless postmortems with owners.

### Architecture Discussions

Facilitate bottleneck-first design reviews. Challenge designs that assume BSS is strongly consistent and always up.

### Technical Debt

Prioritize debt that causes sevs or blocks safe campaigns (missing idempotency, no load shed, weak observability) over cosmetic refactors.

### Engineering Culture

Promote "prepare for the promo" discipline: capacity reviews, failure drills, and celebrating detections.

---

## 07 - Behavioral Questions

### Q1. Tell me about owning a high-traffic service through a major spike or campaign.

- **Why asked:** Telco traffic reality.
- **Competencies:** Capacity planning, calm execution, ownership.
- **Excellent answer framework:**
  - **S:** Known/unknown spike event
  - **T:** Keep critical journey within SLO
  - **A:** Load test; scaling; rate limits; war-room plan; kill switches
  - **R:** Error/latency metrics; lessons for next event
- **Follow-ups:** What failed first? What would you automate next time?

### Q2. Describe a Sev-1/major outage you helped lead.

- **Why asked:** Incident leadership.
- **Competencies:** Composure, communication, root cause depth.
- **Excellent answer framework:**
  - **S:** Customer impact scope
  - **T:** Mitigate → communicate → fix → prevent
  - **A:** Bulkheads; rollback; dependency isolation; status updates
  - **R:** MTTR; postmortem actions completed
- **Follow-ups:** How did you prevent retry storms from making it worse?

### Q3. Tell me about integrating with a slow or unreliable billing/legacy system.

- **Why asked:** BSS/OSS realism.
- **Competencies:** Integration design, patience with constraints.
- **Excellent answer framework:**
  - **S:** Legacy SLA insufficient for digital UX
  - **T:** Protect customers and core systems
  - **A:** Async; cache where safe; circuit breaker; reconciliation
  - **R:** Ticket volume ↓; timeout rates ↓
- **Follow-ups:** How did you detect silent mismatch with billing?

### Q4. Give an example of fixing a retry storm or thundering herd.

- **Why asked:** Scale failure mode literacy.
- **Competencies:** Resilience engineering.
- **Excellent answer framework:**
  - **S:** Cascading retries amplified outage
  - **T:** Stabilize dependency and clients
  - **A:** Jittered backoff; caps; shed load; cache stampedes fixed
  - **R:** Recovery time; permanent client policy change
- **Follow-ups:** Where do retries belong — client, gateway, or worker?

### Q5. Describe mentoring someone through their first production incident.

- **Why asked:** Lead multiplier effect.
- **Competencies:** Mentoring, psychological safety.
- **Excellent answer framework:**
  - **S:** Junior on-call / first sev
  - **T:** Resolve while teaching
  - **A:** Pair; narrate decisions; later rewrite runbook together
  - **R:** Engineer now handles similar class independently
- **Follow-ups:** How do you avoid taking over completely?

### Q6. Tell me about saying no to a promo launch because platforms were not ready.

- **Why asked:** Backbone under commercial pressure.
- **Competencies:** Risk communication, integrity.
- **Excellent answer framework:**
  - **S:** Marketing date vs capacity/reliability gap
  - **T:** Protect customers and brand
  - **A:** Data-backed risk; options (throttle, phased, delay); exec alignment
  - **R:** Safer launch; trust maintained
- **Follow-ups:** What minimum bar would have made you say yes?

### Q7. Walk through a Kubernetes production issue you diagnosed.

- **Why asked:** Globe-relevant runtime depth.
- **Competencies:** K8s operations, debugging.
- **Excellent answer framework:**
  - **S:** CrashLoop/OOM/bad probes/rolling outage
  - **T:** Restore service
  - **A:** Events, metrics, config diff, rollback, root fix
  - **R:** Guardrails added (resource policies, probe fixes)
- **Follow-ups:** How did bad readiness probes contribute?

### Q8. Describe improving observability for a critical customer journey.

- **Why asked:** Operability as leadership.
- **Competencies:** Metrics design, prioritization.
- **Excellent answer framework:**
  - **S:** Blind spots during incidents
  - **T:** Make journey health obvious
  - **A:** Golden signals; traces; exemplar logs; alert rewiring
  - **R:** Faster detect/diagnose; fewer noisy pages
- **Follow-ups:** Which alert did you delete?

### Q9. Tell me about a cross-team delivery with a vendor or external partner API.

- **Why asked:** Telco partner ecosystem.
- **Competencies:** Stakeholder management, contract discipline.
- **Excellent answer framework:**
  - **S:** External dependency on critical path
  - **T:** Ship with clear failure semantics
  - **A:** SLA negotiation; sandbox tests; fallback; joint runbook
  - **R:** Launch metrics; incident-free or well-handled failure
- **Follow-ups:** How did you handle a partner missing a deadline?

### Q10. Give an example of capacity planning you owned.

- **Why asked:** Proactive scale leadership.
- **Competencies:** Forecasting, performance.
- **Excellent answer framework:**
  - **S:** Upcoming growth/campaign
  - **T:** Right-size before impact
  - **A:** Load model; bottleneck find; scale changes; verification test
  - **R:** Headroom metrics; no sev during event
- **Follow-ups:** What assumption in your model was wrong?

### Q11. Describe a legacy modernization at the edge of a monolith/BSS.

- **Why asked:** Incremental change skill.
- **Competencies:** Strangler pattern, risk control.
- **Excellent answer framework:**
  - **S:** Painful coupling blocking digital features
  - **T:** Extract safely
  - **A:** ACL; dual-write/event; compare; traffic shift
  - **R:** Migration progress metrics; rollback story
- **Follow-ups:** What data inconsistency did you find mid-migration?

### Q12. Tell me about enforcing idempotency in a notification or provisioning flow.

- **Why asked:** Duplicate events are normal at telco scale.
- **Competencies:** Exactness of effects, messaging depth.
- **Excellent answer framework:**
  - **S:** Duplicate SMS/activations from at-least-once delivery
  - **T:** Exactly-once effects
  - **A:** Idempotency keys; dedup store; natural keys; replay tests
  - **R:** Duplicate rate ↓; cost ↓
- **Follow-ups:** How long do you retain dedup state?

### Q13. Describe a performance optimization that materially improved a subscriber-facing API.

- **Why asked:** Scale with user impact.
- **Competencies:** Measurement, optimization judgment.
- **Excellent answer framework:**
  - **S:** High p99 / timeouts
  - **T:** Meet SLO
  - **A:** Profile; query/index; pool; cache safely; reduce payloads
  - **R:** Latency and error budget recovery
- **Follow-ups:** What did you refuse to cache?

### Q14. Tell me about leading standards for timeouts, retries, and circuit breakers across services.

- **Why asked:** Platform hygiene leadership.
- **Competencies:** Influence, reliability culture.
- **Excellent answer framework:**
  - **S:** Inconsistent client policies causing sevs
  - **T:** Shared standard
  - **A:** RFC; default library; review checklist; adoption tracking
  - **R:** Fewer cascade incidents
- **Follow-ups:** How did you handle a team that refused?

### Q15. Give an example of designing graceful degradation.

- **Why asked:** HA maturity.
- **Competencies:** Product-aware architecture.
- **Excellent answer framework:**
  - **S:** Dependency outage
  - **T:** Partial service > total hard down
  - **A:** Defined shed order; cached reads; queue writes; UX messaging
  - **R:** Contained impact; faster recovery
- **Follow-ups:** Who decided which features shed first?

### Q16. Describe a security or abuse issue on a public digital channel (credential stuffing, promo abuse, API scraping).

- **Why asked:** Telco digital surfaces are attacked.
- **Competencies:** Security pragmatism, product balance.
- **Excellent answer framework:**
  - **S:** Abuse pattern with customer impact
  - **T:** Reduce abuse without crushing legit users
  - **A:** Rate limits; bot defenses; stepped controls; monitoring
  - **R:** Abuse metrics ↓; support tickets trend
- **Follow-ups:** How did you avoid locking out real subscribers?

### Q17. Tell me about coordinating a release that spanned multiple squads.

- **Why asked:** Org-scale delivery.
- **Competencies:** Program thinking, communication.
- **Excellent answer framework:**
  - **S:** Multi-service feature
  - **T:** Ordered safe rollout
  - **A:** Contract first; feature flags; sequenced deploys; joint verification
  - **R:** Clean launch or controlled rollback
- **Follow-ups:** What was the rollback plan if squad B failed?

### Q18. Describe handling ambiguous requirements for a subscriber-facing journey.

- **Why asked:** Ambiguity → incidents if ignored.
- **Competencies:** Discovery, precision.
- **Excellent answer framework:**
  - **S:** Vague "real-time balance/status"
  - **T:** Define semantics and lag expectations
  - **A:** State model; SLOs; edge cases; acceptance tests
  - **R:** Shared understanding; fewer production disputes
- **Follow-ups:** Which edge case was most expensive?

### Q19. Tell me about reducing on-call toil for your team.

- **Why asked:** Sustainable ownership.
- **Competencies:** Prioritization, automation.
- **Excellent answer framework:**
  - **S:** Noisy pages / repetitive manual fixes
  - **T:** Make on-call humane and effective
  - **A:** Fix classes of failures; better alerts; automations
  - **R:** Page volume ↓; eng satisfaction ↑
- **Follow-ups:** What toil did you intentionally keep manual and why?

### Q20. Give an example of communicating platform risk to commercial/product stakeholders.

- **Why asked:** Lead communication in telco business context.
- **Competencies:** Translation, influence.
- **Excellent answer framework:**
  - **S:** Reliability/capacity risk to revenue journey
  - **T:** Get prioritization or scope change
  - **A:** Impact framing; likelihood; options; clear ask
  - **R:** Decision recorded; risk funded or accepted
- **Follow-ups:** How did you avoid sounding like a blocker?

### Q21. Describe a conflict about architecture (sync vs async, push vs pull, monolith edge vs new service).

- **Why asked:** Facilitation skill.
- **Competencies:** Decision quality, empathy.
- **Excellent answer framework:**
  - **S:** Two credible approaches
  - **T:** Decide with evidence
  - **A:** Spike; criteria (HA, cost, team skill); ADR; revisit triggers
  - **R:** Alignment; later validation
- **Follow-ups:** What would reopen the decision?

### Q22. Tell me about a time you improved CI/CD so the team could ship safer and faster.

- **Why asked:** DevOps leadership signal.
- **Competencies:** Delivery systems thinking.
- **Excellent answer framework:**
  - **S:** Slow/fragile releases
  - **T:** Reduce lead time without raising sev rate
  - **A:** Pipeline fixes; tests in right stage; progressive delivery; rollback drills
  - **R:** Lead time ↓; change fail rate ↓
- **Follow-ups:** Which gate did you remove or add?

### Q23. Describe owning a data pipeline or event ingestion path that other systems depended on.

- **Why asked:** Platform ownership at scale.
- **Competencies:** Contracts, reliability of feeds.
- **Excellent answer framework:**
  - **S:** Billing/usage/notification events as shared backbone
  - **T:** Durable, ordered-enough, observable ingestion
  - **A:** Schema discipline; DLQ; lag monitors; consumer support
  - **R:** Lag/error SLOs met; fewer downstream fire drills
- **Follow-ups:** How do you version events without breaking consumers?

### Q24. Tell me about a mistake you made in production and how you fixed the system, not only the bug.

- **Why asked:** Accountability + learning.
- **Competencies:** Humility, systemic thinking.
- **Excellent answer framework:**
  - **S:** Your change caused impact
  - **T:** Mitigate and prevent class of failure
  - **A:** Rollback/fix; communicate; add test/guardrail/alert
  - **R:** Recurrence prevented; trust repaired
- **Follow-ups:** What process failed upstream of your mistake?

### Q25. Give an example of multi-region or DR thinking you applied (even if active-passive).

- **Why asked:** HA beyond single cluster myths.
- **Competencies:** Continuity planning.
- **Excellent answer framework:**
  - **S:** Critical service continuity requirement
  - **T:** Meet RTO/RPO appropriate to journey
  - **A:** Failover design; data strategy; drill results
  - **R:** Proven failover or clear gaps escalated
- **Follow-ups:** What was the hardest part — DNS, data, or dependencies?

### Q26. Describe working with SRE/platform teams to raise your service's reliability.

- **Why asked:** Collaboration with platform orgs.
- **Competencies:** Partnership, shared ownership.
- **Excellent answer framework:**
  - **S:** Reliability gap
  - **T:** Joint improvement
  - **A:** SLO definition; error budget policy; platform capabilities adopted
  - **R:** SLO attainment improved
- **Follow-ups:** Where did you disagree with SRE and how was it resolved?

### Q27. Tell me about prioritizing technical debt before a major commercial event.

- **Why asked:** Risk-based debt leadership.
- **Competencies:** Prioritization, persuasion.
- **Excellent answer framework:**
  - **S:** Debt that would fail under spike
  - **T:** Buy down risk in time
  - **A:** Rank by sev likelihood; cut scope; timebox; verify with load test
  - **R:** Event survived; remaining debt tracked
- **Follow-ups:** What debt did you consciously defer?

### Q28. Describe hiring or interviewing engineers for a high-ownership team.

- **Why asked:** Bar raising.
- **Competencies:** Talent evaluation.
- **Excellent answer framework:**
  - **S:** Hiring need
  - **T:** Consistent senior bar
  - **A:** Rubric emphasizing production sense; calibrated feedback
  - **R:** Hires who reduced incident load / raised quality
- **Follow-ups:** What signal matters most for telco-scale seniors?

### Q29. Tell me about designing rate limits or quotas for an API used by many clients.

- **Why asked:** Platform protection.
- **Competencies:** Fairness, abuse control, product sense.
- **Excellent answer framework:**
  - **S:** Noisy clients risking shared platform
  - **T:** Protect system while preserving legit traffic
  - **A:** Tenant keys; burst vs sustained; messaging; override process
  - **R:** Stability metrics; fewer neighbor incidents
- **Follow-ups:** How do you communicate limits to partner teams?

### Q30. Why Globe, and why this level (Lead/Senior/Architect)?

- **Why asked:** Motivation + level calibration.
- **Competencies:** Judgment, career intent.
- **Excellent answer framework:**
  - **S:** Your path in large-scale systems
  - **T:** Apply HA/integration/leadership skills to telco digital impact
  - **A:** Map proof points to Globe realities (spikes, K8s, BSS edges)
  - **R:** Clear 90-day contribution thesis
- **Follow-ups:** What do you need from your manager to succeed?

### Q31. Tell me about preparing a runbook and game day for a critical service.

- **Why asked:** Operational readiness culture.
- **Competencies:** Preparedness, teaching.
- **Excellent answer framework:**
  - **S:** Service too tribal-knowledge dependent
  - **T:** Make failure recoverable by any on-call
  - **A:** Runbook; failure injection/game day; gaps fixed
  - **R:** Faster mitigation in real incident later
- **Follow-ups:** What surprise did the game day reveal?

### Q32. Describe balancing new feature velocity with platform reliability work in the same quarter.

- **Why asked:** Lead prioritization under dual mandates.
- **Competencies:** Portfolio thinking, negotiation.
- **Excellent answer framework:**
  - **S:** Feature pressure + rising error budget burn
  - **T:** Protect reliability while shipping value
  - **A:** Error budget policy; percentage capacity to reliability; phased features
  - **R:** Measurable reliability + delivered outcomes
- **Follow-ups:** How transparent was the tradeoff to stakeholders?

---

## 08 - System Design Questions

### Design 1 — Notification / SMS Gateway at Scale

**Requirements**

- Accept notification requests from many internal products
- Fan out to SMS/push/email providers
- Respect opt-out, quiet hours, and per-subscriber rate limits
- High spike tolerance (campaigns, outage blasts)
- Delivery receipts and retry without duplicates

**Architecture Discussion**

- Ingest API with authN/Z and idempotency keys
- Validation + preference service
- Partitioned queues by channel/priority
- Worker pools with provider adapters
- Receipt ingestion → status store
- DLQ + replay tooling
- Edge rate limits per calling service

**Tradeoffs**

- Multi-provider complexity vs resilience
- Priority lanes vs fairness
- Sync API acceptance vs async-only ingest

**Scaling**

- Queue depth-based autoscaling
- Provider rate limit aware schedulers
- Shard by destination hash; protect hot destinations carefully

**Reliability**

- Bulkheads per provider
- Deferred retry with jitter
- Degrade: drop low-priority, preserve OTP/transactional

**Security**

- PII minimization in payloads/logs
- Template controls to prevent spam abuse
- Audit of who can send what class of traffic

**Production Considerations**

- Cost controls (SMS is expensive at scale)
- Campaign throttle dashboards
- Provider outage playbooks
- Privacy retention for message content

### Design 2 — Subscriber Data Service

**Requirements**

- Low-latency reads for self-care and channels
- Consistent-enough profile/entitlement data
- Safe updates from digital and BSS-origin events
- High QPS with hotspot subscribers

**Architecture Discussion**

- API layer (read-optimized)
- System of record boundaries clarified (what you own vs project)
- Cache (Redis) with explicit TTLs and invalidation events
- Event consumers applying BSS updates idempotently
- CDC or periodic recon for drift detection

**Tradeoffs**

- Cache freshness vs load on source
- Owned DB vs pure projection
- Sync write path vs event-only updates

**Scaling**

- Read replicas; cache hit ratio targets
- Hot-key strategies (request coalescing, local cache, segmented keys)
- Pagination and field filtering for mobile clients

**Reliability**

- Stale-on-failure read policy if product allows
- Consumer lag alerts
- Recon jobs with auto/manual repair

**Security**

- Fine-grained access to subscriber PII
- Token audience restrictions
- Masking in non-prod

**Production Considerations**

- Schema evolution for client apps
- Clear SLOs for freshness lag after BSS change
- Load tests with realistic key skew

### Design 3 — CDN / Edge Content for Digital Channels

**Requirements**

- Serve static and semi-dynamic content near users
- Fast invalidation for promo/content updates
- Origin protection during spikes
- Secure access for private assets where needed

**Architecture Discussion**

- CDN in front of object storage / origin services
- Cache-control strategy by content type
- Signed URLs for private media
- Origin shield / WAF
- Invalidation API + versioned URLs (prefer immutable versioning)

**Tradeoffs**

- Short TTL vs purge complexity
- Edge compute vs origin simplicity
- Multi-CDN vs operational overhead

**Scaling**

- Offload origin for campaign assets
- Compress and image-format strategies for mobile

**Reliability**

- Origin failover
- Stale-while-error if acceptable
- Monitoring of cache hit ratio and origin 5xx

**Security**

- WAF rules; bot controls on origin
- Tokenized access for non-public content
- Secure CI publishing to origin buckets

**Production Considerations**

- Purge mistakes as an incident class — practice safe invalidation
- Cost monitoring for egress
- Regional performance views (PH-focused)

### Design 4 — Billing Event Ingestion Pipeline

**Requirements**

- Ingest high-volume usage/billing-related events
- Durable, replayable, ordered-enough per subscriber
- Downstream consumers: rating/billing adapters, analytics, fraud
- Exactly-once effects into each consumer's domain

**Architecture Discussion**

- Producers → Kafka (or equivalent) with keys by subscriber
- Schema registry and compatibility rules
- Consumer groups per domain
- Idempotent writes to consumer stores
- Lag monitoring, DLQ, replay tools
- Periodic reconciliation against source totals

**Tradeoffs**

- Throughput vs per-key ordering
- Many topics vs few topics with headers
- Stream processing vs batch micro-batches

**Scaling**

- Partition count planning; rebalance awareness
- Backfill strategies without starving live traffic
- Compacted topics for certain state projections

**Reliability**

- Producer acks and disk durability settings matched to loss tolerance
- Poison message isolation
- Consumer autoscaling on lag

**Security**

- Encryption; access control per topic
- PII field policies; tokenization where possible
- Audited replay operations

**Production Considerations**

- Quotas for noisy producers
- Clear ownership of schemas
- Runbooks for "lag explosion" during campaigns

### Design 5 — Self-Care Backend for Load / Promo Redemption

**Requirements**

- Authenticated subscriber actions (load, redeem, subscribe)
- Strong anti-abuse controls
- Idempotent purchases under double-taps and network retries
- Integration with payment/load partners and entitlement systems

**Architecture Discussion**

- BFF/API gateway
- AuthN/Z + device risk hooks
- Orchestration with timeouts/bulkheads per dependency
- Idempotency store for purchase intents
- Event outbox for downstream entitlement updates
- Status APIs for in-flight transactions

**Tradeoffs**

- Orchestrated saga vs choreography
- Strong consistency with entitlements vs speed
- Partner-first vs wallet-first flows

**Scaling**

- Horizontal API scale; protect partner rate limits
- Queue overflow strategies during payday spikes

**Reliability**

- Clear state machine: initiated → paid → entitled → failed/compensated
- Reconciliation for paid-but-not-entitled
- Feature flags for specific promo skus

**Security**

- Fraud/velocity checks; OTP step-up for risky actions
- Audit trail of redemptions
- Secrets for partner credentials with rotation

**Production Considerations**

- Customer support tooling for stuck transactions
- Cost of compensations
- Real-time dashboards during promo hours

### Design 6 — Unified API Gateway for Digital Channels & Partners

**Requirements**

- Single entry for external/mobile traffic
- Auth, rate limit, routing, request logging
- Per-client quotas and emergency kill switches
- Observability across all downstreams

**Architecture Discussion**

- Gateway tier (Kong/Apigee/cloud GW/custom) + service discovery
- JWT validation / mTLS for partners
- Routing rules; canary by header/percentage
- Centralized rate limiting (token buckets per client)
- Correlation ID injection
- WAF integration

**Tradeoffs**

- Central gateway bottleneck vs per-service duplication
- Plugin sprawl vs standardization benefits
- Sync gateway filters vs sidecar mesh policies

**Scaling**

- Horizontally scale gateway; careful with shared rate-limit stores
- Regional edge considerations

**Reliability**

- Timeouts to all backends; fail fast
- Maintenance mode / path disables
- Dependency on config control plane HA

**Security**

- Strict authZ; schema validation at edge for abusive payloads
- DDoS/WAF posture
- Audit of config changes (who enabled a route)

**Production Considerations**

- Config-as-code with review
- Shadow traffic for new routes
- Clear error catalog for mobile clients

---

## 09 - Company Preparation Checklist

- [ ] Research Globe digital products and recent platform/tech themes relevant to your target team
- [ ] Map 3 production stories to: spike traffic, HA incident, messy integration
- [ ] Prepare one Kubernetes diagnosis story (probes, OOM, rollout, HPA)
- [ ] Whiteboard SMS/notification gateway and billing event ingestion (timed)
- [ ] Draft degradation policy example for a service you owned
- [ ] Gather real metrics: QPS, p99, error budget, MTTR, cache hit ratio
- [ ] Write 90-day plan: learn critical journeys, stabilize, deliver reliability win
- [ ] List 8 questions (on-call, BSS edges, K8s maturity, campaign process, SRE model)
- [ ] Refresh Spring resilience patterns, Kafka lag semantics, cloud IAM basics
- [ ] Practice explaining retry storms and load shedding in plain language
- [ ] Prepare "delayed a launch" story with commercial stakeholders
- [ ] Prepare cross-team/vendor contract story
- [ ] Align resume language to telco-scale outcomes without fabrication
- [ ] Mock system design: notifications + subscriber data service
- [ ] Mock behavioral: major incident + capacity planning
- [ ] Logistics/sleep plan for multi-interview loop

---

## 10 - How My Experience Maps

### Enterprise Experience

Map large-org delivery, vendor constraints, and governance to Globe's digital-plus-legacy reality.

### Performance Optimization

Emphasize p99 under skewed keys and dependency saturation — not microbenchmarks.

### Legacy Modernization

Lead with strangler at BSS edges, dual-running, reconciliation, and rollback.

### Leadership

Show campaign readiness, incident command, standards for retries/timeouts, and mentoring in production.

### Cloud

Tie cloud work to elasticity, hybrid connectivity, cost at volume, and managed service failure modes.

### Architecture

Present ADRs on sync/async boundaries, caching freshness, and gateway policies.

### Scalability

Discuss HPA, queues, rate limits, and hotspot mitigation with numbers.

### Mentoring

Evidence of teaching debugging and runbook execution, not only code style.

### Product Ownership

Frame outcomes as journey success: redemption completion rate, notification delivery, self-care availability during spikes.

---

## Interview Confidence Checklist

- [ ] I can design for spikes without hand-waving "autoscaling"
- [ ] I can explain idempotent consumers and DLQ/replay
- [ ] I can diagnose a bad Kubernetes rollout path
- [ ] I have 5 STAR stories mapped to Globe themes
- [ ] I can critique a design that retries forever into billing
- [ ] I know my leveling pitch (Senior vs Lead vs Architect)
- [ ] I can describe degradation modes for a critical journey
- [ ] I have sharp questions about platform maturity and BSS coupling

---

## Mock Interview Preparation Checklist

- [ ] 45-min system design: SMS/notification gateway
- [ ] 45-min system design: billing event ingestion
- [ ] 30-min deep dive: K8s + observability on a past service
- [ ] 45-min behavioral set: Q1, Q2, Q6, Q10, Q20
- [ ] 60-min coding: rate limiter / idempotent handler + tests
- [ ] Feedback captured; weak stories rewritten with metrics
- [ ] Second-pass mocks after closing gaps

---

## Suggested Revision Plan

| Day | Focus |
|-----|--------|
| 1 | Globe overview + stack "why" + resume mapping to telco language |
| 2 | Kubernetes + cloud operational deep dive + story prep |
| 3 | Scalability, retries, load shedding, observability drills |
| 4 | System design: notifications + subscriber data |
| 5 | System design: billing ingestion + API gateway |
| 6 | Leadership/behavioral battery (selective Q1–Q32 deep practice) |
| 7 | Full mock loop + gap fixes |
| 8 | Light review, interviewer questions, rest |

---

## Estimated Preparation Time

**5–8 days** of focused prep (2–4 hours/day) if Java, Spring Boot, system design, and leadership modules are already complete. Stretch to **10 days** if you need to build stronger Kubernetes/observability narratives or translate non-telco scale experience (e-commerce spikes, fintech notifications, large SaaS multi-tenant APIs) into Globe-relevant stories without overclaiming domain expertise.
