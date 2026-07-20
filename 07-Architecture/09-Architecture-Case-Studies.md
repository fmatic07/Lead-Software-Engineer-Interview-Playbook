# Architecture Case Studies

> Design systems under constraints: state the problem, draw the seams, name the tradeoffs, and defend the failure modes.

---

## How to Use Case Studies in Interviews

Architecture interviews are not whiteboard art contests. Panels evaluate whether you can **frame a business problem as an engineering system**, surface constraints early, and make decisions you could operate for years.

**Recommended answer structure (8–12 minutes for a deep dive):**

1. **Clarify** — users, scale, consistency needs, regulatory constraints, team topology, timeline.
2. **Scope** — what is in/out for this round; which NFRs are load-bearing.
3. **Domain model** — core entities, invariants, bounded contexts.
4. **Architecture** — boxes, data stores, sync vs async boundaries; draw while talking.
5. **Critical path** — the one flow that must not lie (booking, payment, auth, clinical write).
6. **Tradeoffs** — what you gave up; what would force a redesign.
7. **Ops** — deploy, observe, recover, roll back.
8. **Close** — evolution path (year 1 vs year 3) and open risks.

**What strong candidates do:**

- Ask about **write/read ratio**, **peak vs average**, **multi-region**, and **compliance** before choosing Kafka or CQRS.
- Separate **system of record** from **derived views**.
- Name **idempotency keys**, **outbox**, **saga compensation**, and **poison messages** without being prompted.
- Tie tech choices to **team ownership** (Conway), not fashion.
- Say “I would not microserve this yet” when the monolith is still the correct answer.

**What weak candidates do:**

- Jump to microservices, event sourcing, and Kubernetes before requirements are clear.
- Draw 12 boxes with no data ownership or failure semantics.
- Treat Redis as a database of record.
- Ignore audit, PII, and operational cost.

Use the case studies below as **rehearsal scripts**. For each, practice a 3-minute elevator design and a 12-minute deep dive. Fill Notes with your own production stories — interviewers hire memory of owned systems, not memorized diagrams.

---

## Why This Matters in Production

Case-study fluency maps directly to lead/architect work:

| Interview skill | Production equivalent |
|-----------------|----------------------|
| Clarifying constraints | Stakeholder workshops, RFCs, ADRs |
| Drawing seams | Bounded contexts, service ownership, API contracts |
| Choosing sync vs async | Latency budgets, coupling, blast radius |
| Naming failure modes | Incident runbooks, SLO error budgets |
| Defending tradeoffs | Budget, headcount, compliance reviews |
| Deployment strategy | Progressive delivery, dual-run, rollback |

Enterprises fail less often from “wrong pattern” than from **unclear ownership**, **hidden coupling**, **underexplained consistency**, and **systems nobody can operate**. Case studies train you to make those risks explicit before code ships.

---

## Engineering Tradeoffs (Cross-Cutting)

These tensions appear in nearly every case below. Name them early in interviews.

| Tradeoff | Prefer A when… | Prefer B when… |
|----------|----------------|----------------|
| **Monolith vs services** | One team, unclear domain, need speed | Clear boundaries, independent scale/deploy, multiple teams |
| **Strong vs eventual consistency** | Money, inventory, clinical invariants | Feeds, recommendations, analytics |
| **Sync request/response vs async events** | User waits for result; simple choreography | Fan-out, load leveling, cross-domain side effects |
| **Shared DB vs DB per service** | Transactional integrity across modules | Independent evolution and blast isolation |
| **Normalized OLTP vs denormalized read models** | Write integrity, ad-hoc queries | Hot read paths, search, dashboards |
| **Managed cloud vs self-hosted** | Ops scarce, elastic demand | Data residency, cost at steady high volume, control |
| **At-least-once + idempotency vs exactly-once illusion** | Almost always in distributed systems | Only where broker + consumer semantics truly guarantee it |
| **Central platform vs product-owned** | Cross-cutting capability (auth, notify, pay) | Domain-specific workflows that change weekly |

**Rule of thumb for leads:** optimize for **change frequency** and **failure blast radius**, not diagram elegance.

---

## Common Anti-Patterns

1. **Distributed monolith** — many services, one shared DB, chatty sync calls, coupled deploys.
2. **Premature microservices** — splitting before domain language stabilizes.
3. **God event bus** — every domain publishes/consumes everything; no ownership of schemas.
4. **Cache as source of truth** — Redis holds balances/inventory without reconciliation.
5. **Saga without compensation design** — happy path only; stuck states in production.
6. **Gateway as business logic dump** — BFF/gateway accumulates domain rules and transforms.
7. **One-size Kafka topic** — huge payloads, mixed concerns, impossible retention policy.
8. **Security bolted on** — authZ in UI only; missing service-to-service identity.
9. **Observability after launch** — no correlation IDs, no SLOs, metrics that do not map to user journeys.
10. **Big-bang cutover** — no dual-run, no feature flags, no data reconciliation plan.
11. **PII everywhere** — logs, topics, search indexes containing secrets/health data.
12. **Infinite horizontal scale myth** — ignoring write contention, hot partitions, and DB limits.

---

## Best Practices

1. Start with **domain invariants** and **system of record**; derive architecture from them.
2. Prefer **modular monolith** until independent scale/deploy is proven necessary.
3. Use **API contracts + consumer-driven tests** at service boundaries.
4. Put **idempotency** on all money, inventory, booking, and notification side effects.
5. Use **transactional outbox** (or equivalent) for reliable event publication.
6. Design **read models** explicitly; do not overload OLTP for search/feeds.
7. Define **SLOs** (availability, latency, freshness) before choosing tech.
8. Separate **control plane** (config, auth policies) from **data plane**.
9. Plan **dual-write / dual-run / shadow traffic** for migrations.
10. Encrypt in transit and at rest; minimize PII in events; tokenize where possible.
11. Make deploy **boring**: blue/green or canary, automated rollback, schema expand/contract.
12. Own **runbooks** for the top five failure modes of each platform.

---

## Architecture Review Checklist

Use before sign-off or as an interview self-check:

- [ ] Business goals and constraints stated (scale, region, compliance, team size)
- [ ] Bounded contexts and data ownership clear
- [ ] Sync vs async boundaries justified
- [ ] Consistency model per critical invariant named
- [ ] Failure modes and compensations designed
- [ ] Idempotency and deduplication strategy defined
- [ ] AuthN/AuthZ model (user, service, admin) specified
- [ ] Data classification (PII/PHI/PCI) and retention addressed
- [ ] Scalability bottlenecks identified (DB, hot keys, partitions)
- [ ] Observability: metrics, logs, traces, alerts, SLOs
- [ ] Deployment and rollback strategy workable
- [ ] Cost and operational complexity acknowledged
- [ ] Evolution path (12–24 months) sketched
- [ ] Alternatives considered and rejected with rationale

---

# Case Studies

---

## 1. Legacy Monolith Modernization

### Business Requirements

A decade-old Java EE / early Spring monolith powers core commerce and back-office. Release cadence is monthly; incidents cascade across unrelated features; hiring is blocked by a codebase only veterans can change. Leadership wants faster delivery, safer releases, and cloud readiness **without** a multi-year rewrite freeze.

### Functional Requirements

- Continue serving existing web/API clients during migration.
- Carve out high-churn domains (catalog, orders, pricing) for independent deploy.
- Preserve reporting and batch jobs that currently share tables.
- Support strangler routes: new implementations behind feature flags / gateway paths.
- Maintain audit trails for financial and admin actions.

### Non-functional Requirements

- Zero prolonged downtime cutovers; RPO ≤ minutes for financial data.
- p99 API latency not regress >10% during strangler phase.
- Independent deploy of extracted services without full monolith release.
- Observability parity: traces across monolith ↔ services.
- Compliance: existing SOX/audit controls preserved.

### Architecture Diagram Description

```
[Clients / SPA] → [API Gateway / Strangler Facade]
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
   [Monolith (legacy)] [Order Service] [Catalog Service]
          │                 │               │
          └────────┬────────┴───────────────┘
                   ▼
            [Shared? → split DBs over time]
                   │
                   ▼
         [Outbox → Kafka] → [Search / Analytics / Notify]
                   │
                   ▼
         [PostgreSQL per domain + Object Storage for docs]
```

Strangler facade routes by path/header. Dual-run: monolith and new service process subset of traffic; reconciliation jobs compare outcomes.

### Technology Choices

- **Spring Boot 3** for extracted services; gradual module boundaries inside monolith first.
- **API Gateway** (Spring Cloud Gateway / AWS ALB + routing rules) as strangler.
- **PostgreSQL** per extracted domain; expand/contract schema migrations (Flyway).
- **Kafka** + transactional outbox for integration events.
- **Redis** for session/feature flags during transition.
- **OpenTelemetry** + Jaeger/X-Ray; structured logging with correlation IDs.
- **Kubernetes (EKS)** for new services; monolith containerized last if needed.
- **Feature flags** (LaunchDarkly / Unleash / custom) for percentage rollout.

### Tradeoffs

| Choice | Gain | Cost |
|--------|------|------|
| Strangler vs rewrite | Continuous value, lower risk | Long dual-stack period, temporary complexity |
| Extract by domain vs by layer | Clear ownership | Harder when tables are entangled |
| Shared DB temporarily | Faster first extraction | Risk of distributed monolith |

### Alternative Designs

- **Modular monolith only** — package-by-domain, single deploy; best if one team and unclear boundaries.
- **Big-bang rewrite** — rarely succeeds; only for greenfield product replacement with hard sunset.
- **Anti-corruption layer + BFF** — keep monolith, wrap with modern APIs for new channels.

### Scalability

Scale extracted hot paths independently. Keep monolith vertical scale / read replicas until write load moves. Watch **chatty sync calls** after split — introduce async where latency allows.

### Reliability

Circuit breakers toward legacy; bulkheads so new services degrade without taking down checkout. Reconciliation for dual-write. Expand/contract DB migrations; never expand-and-break-old-readers in one deploy.

### Security

Preserve existing auth; introduce service identity (mTLS / JWT between services). Audit events for admin paths. Secrets out of monolith config files into vault/KMS.

### Deployment Strategy

1. Modularize in-place → 2. Extract read-only capability → 3. Dual-write → 4. Shadow reads → 5. Cut write ownership → 6. Delete dead monolith paths. Canary per route. Rollback = flag off + route to monolith.

### Operational Considerations

Golden signals on both stacks. Data ownership map living document. Freeze shared-table changes without ADR. Train teams on on-call for new services before cutover.

### Lessons Learned

Modernization is a **product delivery strategy**, not a rewrite project. Extract along **change and ownership seams**. Temporary shared DB is acceptable if time-boxed with a kill plan. Measure lead time and change-fail rate — not number of microservices.

---

## 2. Migration to Microservices

### Business Requirements

Platform grew from one product to multiple lines of business. Three teams block on one release train. Need independent scaling for peak seasons, clearer ownership, and ability to adopt different data stores per domain.

### Functional Requirements

- Split into bounded contexts: Identity, Catalog, Cart/Checkout, Fulfillment, Billing, Notifications.
- Inter-service communication for place-order, cancel, refund.
- Shared customer profile readable by multiple domains without shared mutable tables.
- Developer self-service: scaffolding, CI templates, service catalog.

### Non-functional Requirements

- Independent deploy ≥ daily per service.
- Cross-service tracing mandatory; error budget per SLO.
- Network partitions tolerated; no silent data loss on order placement.
- Platform team provides paved road (auth, logging, Kafka, Postgres operators).

### Architecture Diagram Description

```
[Mobile/Web] → [API Gateway] → [BFF]
                    │
     ┌──────────────┼──────────────┬──────────────┐
     ▼              ▼              ▼              ▼
 [Identity]     [Catalog]      [Checkout]    [Billing]
     │              │              │              │
  Postgres       Postgres      Postgres+     Postgres
                               Redis cart
                    │              │
                    └──────┬───────┘
                           ▼
                    [Kafka platform]
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
        [Fulfillment] [Notify]    [Analytics/CDC]
```

Checkout owns order aggregate; publishes `OrderPlaced`; Billing and Fulfillment react. Identity issues tokens; gateway validates.

### Technology Choices

- Spring Boot + Spring Security OAuth2 Resource Server.
- PostgreSQL per service; Redis for cart/session.
- Kafka for domain events; Schema Registry (Avro/JSON Schema).
- API Gateway + optional BFF per channel.
- Kubernetes, Helm/Kustomize, GitOps (Argo CD).
- Resilience4j; OpenTelemetry.

### Tradeoffs

Microservices buy team autonomy and scale isolation; cost is **ops surface**, **distributed transactions**, and **debugging complexity**. Worth it only with platform investment and clear contexts.

### Alternative Designs

- **Modular monolith + separate deployable workers** for async — middle ground.
- **Service-based architecture on shared DB** — avoid; worst of both worlds.
- **Event sourcing everywhere** — high cost; reserve for audit-heavy aggregates.

### Scalability

Scale Checkout and Catalog independently. Partition Kafka by `orderId`/`customerId`. Watch N+1 sync fan-out; prefer orchestration or events for multi-step flows.

### Reliability

Outbox on producers; idempotent consumers; dead-letter queues; saga/process manager for cancel/refund. Timeouts and retries with jitter; never retry non-idempotent payments without key.

### Security

Zero-trust service mesh or signed service JWTs. Least-privilege DB creds per service. Centralized authZ policy for sensitive ops; domain services enforce invariants.

### Deployment Strategy

Consumer-driven contract tests in CI. Schema compatibility checks on Kafka. Canary + automated rollback. Database migrations expand/contract owned by each service.

### Operational Considerations

Service catalog, on-call ownership matrix, SLO dashboards per domain. Platform paved road reduces snowflake services. Cost attribution per namespace.

### Lessons Learned

Split by **business capability**, not by technical layer. Invest in platform before proliferating services. The migration success metric is **team throughput and incident blast radius**, not service count.

---

## 3. Scaling a Booking Platform

### Business Requirements

Travel/hotel/appointment booking with sharp peaks (holidays, flash sales). Double-booking is unacceptable. Users expect hold-then-pay flows and fast search of availability.

### Functional Requirements

- Search availability by date/location/attributes.
- Hold inventory for TTL (e.g., 10–15 min) during checkout.
- Confirm booking on successful payment; release hold on timeout/cancel.
- Manage rate plans, cancellations, modifications.
- Partner/supplier sync for external inventory.

### Non-functional Requirements

- No double booking under contention (hard invariant).
- Search p95 < 300ms; booking confirm p95 < 1s excluding payment PSP.
- Peak 10× average QPS; graceful degradation (read-only search if write path strained).
- Multi-AZ; RPO near-zero for confirmed bookings.

### Architecture Diagram Description

```
[Clients] → [Gateway/BFF] → [Search API] → [OpenSearch/Elastic] ← indexer
                    │
                    ▼
            [Booking Service]
                    │
         ┌──────────┼──────────┐
         ▼          ▼          ▼
   [Inventory]  [Pricing]  [Payment Adapter]
    Postgres     Redis      PSP (Stripe/etc)
         │
         └── locks/holds table + Redis TTL hold
                    │
                    ▼
              [Kafka] → Notify, Fraud, Analytics, Partner sync
```

Availability search is a **read model**. Booking service is system of record for reservations and holds.

### Technology Choices

- Spring Boot Booking + Inventory services.
- PostgreSQL with careful row locking / optimistic versioning for units.
- Redis for ephemeral holds and rate limiting.
- OpenSearch for availability search.
- Kafka for booking lifecycle events.
- Idempotency keys on confirm/cancel APIs.

### Tradeoffs

| Approach | Pros | Cons |
|----------|------|------|
| Pessimistic lock per unit | Strong correctness | Throughput cliff on hot properties |
| Optimistic + retry | Higher throughput | More conflicts under flash sales |
| Redis hold + DB confirm | Fast UX | Must reconcile Redis/DB on failure |

### Alternative Designs

- **Single-node inventory shard per property** — extreme isolation for mega-hotels.
- **Event-sourced booking ledger** — excellent audit; harder query/availability.
- **Supplier always authoritative** — simpler locally, weaker UX if supplier slow.

### Scalability

Shard inventory by property/region. Cache popular searches with short TTL; invalidate on inventory change events. Separate read replicas / search cluster from write path. Queue confirmations if PSP latency spikes (careful with UX).

### Reliability

Hold TTL workers must be multi-leader safe (fencing tokens). Payment success + booking confirm as saga with compensation (auto-refund / release). Idempotent confirm. Clock skew: store expiry as absolute timestamp in DB.

### Security

PCI via PSP; never store PAN. AuthZ: user can only modify own bookings; partner APIs use mTLS. Fraud signals on velocity of holds.

### Deployment Strategy

Canary booking service; feature-flag new lock algorithm on small property set. Load-test hot-key scenarios before peak season. Game-day: Redis flush, Kafka lag, PSP timeout.

### Operational Considerations

Dashboards: hold conversion rate, double-book attempts blocked, lock wait time, search freshness lag. Runbook for “inventory desync with search.”

### Lessons Learned

**Availability search and booking write path must be separated.** Correctness lives in inventory/booking, not in the search index. Holds are a first-class state machine, not a cache side effect.

---

## 4. Building a Banking Platform

### Business Requirements

Retail banking core for accounts, transfers, cards, statements. Regulated environment (local central bank / BSP-like, AML, audit). Customers demand 24/7 transfers; bank demands absolute ledger integrity.

### Functional Requirements

- Open/close accounts; post deposits/withdrawals/transfers.
- Double-entry ledger; immutable postings; reversible only via compensating entries.
- Card auth requests (ISO8583-like) with tight latency.
- Statements, limits, holds/reservations, standing orders.
- AML screening hooks; audit export for regulators.

### Non-functional Requirements

- Ledger invariant: sum(debits)=sum(credits) always.
- Card auth p99 often < 200–300ms.
- Strong consistency within account/ledger partition.
- Encryption, key management, segregation of duties.
- Durability: no silent loss of committed postings; audited access.

### Architecture Diagram Description

```
[Channels: Mobile/Web/ATM/Card Switch]
                │
                ▼
         [API Gateway + WAF]
                │
     ┌──────────┼───────────┐
     ▼          ▼           ▼
 [Accounts] [Payments/Transfers] [Card Auth]
     │          │                 │
     └──────────┼─────────────────┘
                ▼
         [Ledger Service] ← system of record
                │
           PostgreSQL
        (account-partitioned)
                │
                ▼
    [Kafka: PostingCommitted] → AML, Notify, Data Lake (CDC)
                │
         [Redis] limits/session only — not balances as SoR
```

Optionally CQRS read models for statements; never mutate ledger via read path.

### Technology Choices

- Spring Boot; strict domain model for Account/Posting/Journal.
- PostgreSQL with transactional posting APIs; possible Cockroach/Spanner later for multi-region — only with deep expertise.
- Kafka for downstream; CDC (Debezium) to warehouse.
- HSM/KMS for keys; Vault for secrets.
- Redis for rate limits / session — **balances only in ledger DB**.

### Tradeoffs

Strong consistency and auditability beat microservice fashion. Splitting ledger across services without a clear posting protocol invites money bugs. Multi-region active-active is extremely expensive for ledgers.

### Alternative Designs

- **Mainframe / packaged core** + modern channel layer — common in enterprises.
- **Event-sourced ledger** — natural audit; snapshotting and interest calculation complexity.
- **Separate card auth decisioning service** with reserved balance holds.

### Scalability

Partition by `accountId` / customer segment. Async non-critical paths (notifications, analytics). Card auth path kept thin: local limits cache + ledger reserve. Avoid cross-account chatty workflows without batching.

### Reliability

Idempotency keys on every transfer. Exactly-once **effect** via DB unique constraints + outbox. Reconciliation jobs vs card schemes and correspondent banks. Chaos: kill pod mid-posting; verify no double post.

### Security

Defense in depth: WAF, mTLS, step-up auth for high-value transfers, maker-checker for admin. PII tokenization. Immutable audit logs (WORM storage). Penetration tests and segregation of duties in CI/CD.

### Deployment Strategy

Blue/green with ledger migration expand/contract only. Never hot-fix posting logic without canary on low-risk account cohort. Dual-run against shadow ledger in major upgrades.

### Operational Considerations

SOX-like controls, privileged access monitoring, end-of-day reconciliation, AML case queues. On-call runbooks jointly owned with risk/ops.

### Lessons Learned

**Money is a protocol.** Design posting, idempotency, and compensation first. Caches accelerate decisions; they do not replace the ledger. Regulatory constraints dominate architecture more than scale slogans.

---

## 5. Travel Platform Architecture

### Business Requirements

OTA-style platform: flights, hotels, ancillary (bags, seats), packages. Aggregate many suppliers (GDS, hotel chains). Compete on search speed, price accuracy, and booking reliability.

### Functional Requirements

- Multi-supplier search and fare/room combine.
- Checkout with ancillaries; ticketing/voucher issuance.
- Manage PNR/order lifecycle: change, cancel, refund rules.
- Content (hotel photos/descriptions) and reviews.
- Partner B2B APIs for agencies.

### Non-functional Requirements

- Search fan-out under 2–3s end-to-end with partial results acceptable.
- Booking must not confirm without supplier ack (or clear pending state).
- Cache-heavy; supplier rate limits respected.
- PCI for payments; PII minimization in logs.

### Architecture Diagram Description

```
[Web/App] → [BFF] → [Search Orchestrator]
                         │
            ┌────────────┼────────────┐
            ▼            ▼            ▼
      [Flight Gateway] [Hotel Gateway] [Ancillary]
            │            │
         Supplier APIs (async + circuit breakers)
            │
            ▼
      [Normalization + Offer Cache (Redis)]
            │
            ▼
      [Order/Booking Service] → [Payment] → Supplier confirm
            │
         Postgres orders + Kafka events
            │
      [Content Service] → Object Storage + CDN
```

Offers are ephemeral quotes with TTL; Order is durable.

### Technology Choices

- Spring Boot orchestrators and gateways per supplier family.
- Redis for offer cache; PostgreSQL for orders.
- Kafka for booking status, ticketing, email.
- OpenSearch for hotel content search.
- S3/CloudFront for media; API Gateway for public APIs.
- Resilience4j bulkheads per supplier.

### Tradeoffs

Orchestrated search complexity vs user wait time. Deep caching risks **price/availability drift**; must revalidate before pay. Multi-supplier cancels create messy compensation graphs.

### Alternative Designs

- **Supplier-specific mini-apps** (vertical slices) vs shared order model.
- **GraphQL BFF** for mobile flexibility vs REST discipline.
- **Precomputed package catalog** for simple tours vs dynamic packaging.

### Scalability

Horizontal orchestrators; cache offers aggressively; collapse identical supplier queries. Queue ticketing workers. CDN for content. Shard orders by `orderId`.

### Reliability

Revalidate offer at pay; pending states if supplier slow; timeout + user messaging. Compensating cancel across suppliers. Poison offer detection when supplier schemas drift.

### Security

Tokenize traveler PII where possible; vault for supplier credentials; partner API keys + mTLS. Fraud on velocity of bookings/refunds.

### Deployment Strategy

Supplier adapters versioned independently. Feature-flag new GDS. Contract tests against recorded supplier fixtures; sandbox certifications.

### Operational Considerations

Supplier SLA dashboards, cache hit rates, offer-to-book conversion, ticketing lag. War room playbooks for major airline outages.

### Lessons Learned

Travel is an **integration and state-machine problem**. Treat offers as quotes, bookings as durable workflows, and supplier adapters as anti-corruption layers with ruthless isolation.

---

## 6. Notification Platform

### Business Requirements

Centralized multi-channel notifications (email, SMS, push, in-app, WhatsApp) for many product teams. Replace ad-hoc Sendmail/Twilio calls scattered across services. Need templates, preferences, delivery receipts, and cost control.

### Functional Requirements

- Accept notify requests via API/events with template + data.
- Resolve user preferences and quiet hours.
- Render templates; route to channel providers.
- Track delivery/bounce/complaint; expose status API.
- Admin: templates, provider failover, rate budgets per tenant.

### Non-functional Requirements

- High throughput bursts (campaigns) without blocking transactional OTP.
- At-least-once delivery with deduplication by `notificationId`.
- p99 for OTP path low; campaigns can be lagged.
- PII-safe logging; consent/opt-out enforcement.

### Architecture Diagram Description

```
[Producers] → [Notify API / Kafka topic notify.commands]
                        │
                        ▼
              [Ingestion + Validation]
                        │
                        ▼
              [Priority queues: OTP | Transactional | Marketing]
                        │
                        ▼
              [Orchestrator: prefs → render → route]
                        │
         ┌──────────────┼──────────────┐
         ▼              ▼              ▼
   [Email Worker]  [SMS Worker]  [Push Worker] → Providers
         │              │              │
         └──────────────┼──────────────┘
                        ▼
              [Status callbacks → Kafka] → Postgres status store
                        │
                   [Redis] dedupe + rate limit
```

### Technology Choices

- Spring Boot services; Kafka for commands and status.
- PostgreSQL for templates, prefs, delivery records.
- Redis for idempotency and provider rate limits.
- Providers: SES/SendGrid, Twilio, FCM/APNs.
- Object storage for large campaign recipient lists.
- Kubernetes HPA on workers by lag/priority.

### Tradeoffs

Central platform reduces duplication but becomes a shared bottleneck if poorly isolated. Strict templates improve compliance; reduce product team flexibility.

### Alternative Designs

- **Library in each service** — simple start; becomes chaos.
- **Vendor all-in-one (Braze/Customer.io)** — faster, less control/cost predictability.
- **Per-channel microservices vs single orchestrator** — split when scale/teams demand.

### Scalability

Partition by `userId`/`tenantId`. Separate consumer groups for OTP vs marketing. Pre-render where possible; batch provider APIs. Autoscale workers on Kafka lag.

### Reliability

Idempotent provider calls; store provider message IDs; retry with backoff; DLQ for poison templates. Fail over SMS providers. Never lose OTP: dedicated capacity and alerts.

### Security

Consent as hard gate. Encrypt PII at rest; redact templates in logs. Admin RBAC. Prevent template injection (SSTI). Tenant isolation on data and rate budgets.

### Deployment Strategy

Canary workers; template versioning with immutable publishes. Provider credential rotation via secrets manager. Load-test campaign blasts vs OTP SLO.

### Operational Considerations

Cost per channel dashboards; bounce/complaint rates; quiet-hour violations; lag alarms per priority lane. Runbook for provider outage.

### Lessons Learned

**Priority isolation is the architecture.** One queue for OTP and marketing will fail you in production. Preferences and consent are product features, not afterthoughts.

---

## 7. Authentication Platform

### Business Requirements

Enterprise identity: workforce + customer IAM. SSO for internal apps, social/passwordless for customers, MFA, session management, and delegated auth for partners. Security org demands centralized policy; product teams demand low-friction login.

### Functional Requirements

- OIDC/OAuth2 authorization server; SSO (SAML optional).
- User registration, credential storage, MFA (TOTP/WebAuthn), recovery.
- Token issuance (access/refresh), introspection, revocation.
- Admin: apps, clients, scopes, risk policies.
- Directory sync (HR/LDAP) for workforce.

### Non-functional Requirements

- Login p99 tight; availability critical (auth outage = company outage).
- Hardening against credential stuffing, brute force, token theft.
- Audit every auth decision for privileged apps.
- Horizontal scale; multi-AZ; careful key rotation.

### Architecture Diagram Description

```
[Apps/SPAs/Mobile] → [Auth Gateway / OIDC endpoints]
                              │
                              ▼
                    [AuthN Service]
                     /     |     \
                    ▼      ▼      ▼
             [Cred Store] [MFA] [Risk/Fraud]
                 Postgres  Redis   rules
                              │
                              ▼
                    [Token Service] → signed JWT / opaque ref
                              │
                    [Session Store Redis]
                              │
              [Admin & Policy API] [Directory Sync Worker]
                              │
                         Kafka audit events
```

Resource servers validate JWT (or introspect opaque tokens).

### Technology Choices

- Spring Authorization Server or enterprise IdP (Keycloak/Okta/Cognito) with custom extensions.
- PostgreSQL for users/clients; Redis for sessions/rate limits.
- KMS for signing keys; hardware-backed where required.
- WAF + bot management; Argon2/bcrypt for passwords.
- Kafka for security audit stream.

### Tradeoffs

Opaque tokens + introspection = revocable but chatty. JWT = scalable validation, harder instant revoke (short TTL + blocklist). Build vs buy: build only with security staffing.

### Alternative Designs

- **Fully managed IdP** — preferred default for most orgs.
- **Split CIAM vs Workforce IAM** — different UX and risk profiles.
- **mTLS service identity separate from user IAM** — recommended.

### Scalability

Stateless JWT validation at edge; sticky only if sessions require. Shard user DB by realm/tenant. Cache JWKS. Isolate MFA challenge path.

### Reliability

Multi-AZ auth; degrade gracefully (e.g., skip optional risk checks under load with alert). Key rotation with overlapping JWKS. Session store failover tested.

### Security

Threat model first: stuffing, phishing, token replay, open redirects, SSRF on connectors. Mandatory MFA for admins. Secure recovery flows. Continuous monitoring of anomalous auth.

### Deployment Strategy

Blue/green auth is delicate — dual-run keys and clients. Canary by application client_id. Chaos: Redis down, DB failover during login peak.

### Operational Considerations

Dashboards: login success rate, MFA challenge rate, token errors, stuffing blocks. Joint ownership with SecOps. Break-glass admin procedures.

### Lessons Learned

Auth is a **product and a control plane**. Prefer buy/extend over bespoke crypto. Separate user sessions from service identity. Short-lived tokens + refresh rotation beat long-lived JWTs.

---

## 8. Payment Platform

### Business Requirements

Central payments capability: authorize, capture, refund, payouts; multiple PSPs and local methods (cards, wallets, bank transfer). Finance needs reconciliation; products need a simple Payment API.

### Functional Requirements

- Create payment intent; confirm; capture/cancel; refund partial/full.
- PSP routing by method, country, cost, success rate.
- Webhooks from PSPs mapped to internal state machine.
- Merchant/tenant configuration; settlement reports.
- Idempotent APIs; human-readable payment timeline.

### Non-functional Requirements

- Exactly-once **business effect** on money movement (via idempotency).
- PCI SAQ scope minimization — prefer PSP-hosted / tokenized.
- High availability; webhook processing durable.
- Strong audit; immutable payment events.

### Architecture Diagram Description

```
[Checkout / Services] → [Payment API]
                            │
                            ▼
                   [Payment Orchestrator]
                    state machine in Postgres
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
        [PSP Adapter A] [PSP Adapter B] [Wallet Adapter]
              │             │             │
              └─────────────┼─────────────┘
                            ▼
                     PSP networks
                            │
                      webhooks in
                            ▼
                   [Webhook Ingest] → verify → Kafka → Orchestrator
                            │
                   [Ledger/Finance export] [Fraud] [Notify]
```

### Technology Choices

- Spring Boot orchestrator; adapters per PSP.
- PostgreSQL as payment SoR; Redis idempotency keys.
- Kafka for webhooks and downstream accounting.
- KMS; vault for PSP secrets; never store PAN.
- Kubernetes; strict network policies.

### Tradeoffs

Single payment platform standardizes finance ops but concentrates risk. Multi-PSP routing improves acceptance; complicates reconciliation. Sync confirm UX vs async bank methods.

### Alternative Designs

- **Direct PSP per product** — faster start; painful finance consolidation.
- **Full payment institution / acquirer stack** — only if licensed and staffed.
- **Event-sourced payment log** — strong audit; more engineering cost.

### Scalability

Partition by `paymentId`/`merchantId`. Horizontal webhook consumers with idempotent handlers. Separate authorize-hot path from report generation.

### Reliability

Idempotency keys required. State machine with explicit transitions; ignore duplicate webhooks. Outbox to finance. Reconciliation jobs daily vs PSP reports. Timeout handling: inquire PSP status, do not double-charge.

### Security

PCI DSS scope reduction; tokenize; least privilege. Webhook signature verification mandatory. Fraud scoring hooks before capture. Admin maker-checker on payouts.

### Deployment Strategy

Adapter canaries per PSP. Contract tests with recorded webhooks. Feature-flag new routing rules. Dual-post to finance in shadow mode when changing ledger export.

### Operational Considerations

Auth rate, capture lag, refund SLA, reconciliation breaks, PSP latency. On-call with finance for settlement incidents.

### Lessons Learned

Payments are a **state machine + reconciliation problem**. Design for duplicate webhooks and ambiguous timeouts first. Idempotency is non-negotiable.

---

## 9. File Storage Platform

### Business Requirements

Company-wide file platform for user uploads, documents, media, and internal artifacts. Replace NFS shares and ad-hoc S3 buckets with consistent security, virus scan, lifecycle, and access audit.

### Functional Requirements

- Presigned upload/download; multipart for large files.
- Metadata catalog: owner, tags, retention, classification.
- Async virus scan and content-type validation.
- Quotas per tenant; lifecycle to cold storage; legal hold.
- Image/video derivatives (thumbnails) optional pipeline.

### Non-functional Requirements

- Durability 11 nines via object storage; availability multi-AZ.
- Upload resilience (resume); download via CDN for public assets.
- Strong AuthZ on every object; signed URLs short-lived.
- Scan before making file readable by others.

### Architecture Diagram Description

```
[Clients] → [Metadata API] → Postgres catalog
                │
                ▼
        issue presigned PUT → [Object Storage S3]
                │
         ObjectCreated event
                │
                ▼
        [Scan Worker] → ClamAV/Cloud AV
                │
         clean? update status → allow GET presign
                │
         [Derivative Worker] → thumbnails → S3
                │
         [CDN] for public/published objects
                │
         Kafka audit: access, share, delete
```

### Technology Choices

- Spring Boot metadata service.
- PostgreSQL for metadata; S3/MinIO for bytes.
- Kafka/SQS for scan and derivatives.
- Redis for rate limits/quotas counters.
- CloudFront/CDN; KMS-SSE; AWS Macie optional for classification.

### Tradeoffs

Presigned direct-to-S3 reduces API bandwidth but complicates policy (what if scan fails after upload). Central catalog adds consistency; must not become a bottleneck for listing.

### Alternative Designs

- **Per-team raw buckets** — simple, weak governance.
- **CMS (DAM) product** — rich features, less custom control.
- **Block storage / NFS** — legacy fit; poor web scale.

### Scalability

Metadata queries indexed by owner/tenant; avoid huge directory listings — paginate/search. Parallel multipart uploads. CDN for hot reads. Lifecycle rules for cost.

### Reliability

Upload sessions tracked; incomplete multipart GC. Scan failures quarantine. Versioning + soft delete; legal hold blocks purge. Cross-region replication if required.

### Security

Default private objects; explicit share ACLs; short presign TTL; malware gate; DLP on sensitive classifications. Encrypt with KMS CMKs per tenant if needed. Audit every access grant.

### Deployment Strategy

Immutable metadata schema migrations. Canary scan workers. Chaos: AV down → fail closed for sharing. Blue/green API only; storage persists.

### Operational Considerations

Cost by storage class; scan queue lag; quarantine rates; orphaned multipart cleanup. Quotas alerting before hard deny.

### Lessons Learned

**Bytes in object storage, truth in metadata service, trust after scan.** Never serve unscanned uploads to other users. Presigned URLs are capabilities — treat them like secrets.

---

## 10. Inventory Platform

### Business Requirements

Real-time stock for omnichannel retail/warehouse: stores, DCs, online. Prevent oversell; support reservations, replenishment, and partner marketplaces.

### Functional Requirements

- Adjust stock (receipts, shipments, cycle count, returns).
- Reserve / commit / release for orders.
- Query ATP (available to promise) by SKU/location.
- Publish inventory changes to channels and search.
- Support bundles/kits and safety stock policies.

### Non-functional Requirements

- No negative sellable stock under concurrency (per policy).
- Hot SKU updates at high QPS during campaigns.
- Near-real-time channel sync (seconds).
- Auditable adjustments with reason codes.

### Architecture Diagram Description

```
[OMS / POS / WMS] → [Inventory API]
                         │
                         ▼
              [Inventory Service + Postgres]
              tables: on_hand, reserved, attrs
                         │
              Redis: hot ATP cache (optional)
                         │
                         ▼
                   Outbox → Kafka inventory.changed
                         │
              ┌──────────┼──────────┐
              ▼          ▼          ▼
          [Channels]  [Search]   [Planning]
```

Reservation is explicit state, not “decrement and hope.”

### Technology Choices

- Spring Boot; PostgreSQL with row-level versioning per SKU-location.
- Redis for ATP read acceleration with invalidation on change.
- Kafka for fan-out; Schema Registry.
- Idempotency on adjust/reserve APIs.

### Tradeoffs

Fine-grained locking protects correctness but limits throughput on celebrity SKUs. Eventual channel sync risks brief oversell on external marketplaces unless buffered.

### Alternative Designs

- **Per-warehouse inventory services** — scale isolation; harder global ATP.
- **Event-sourced stock ledger** — strong audit; complex ATP snapshots.
- **Soft oversell with compensation** — business choice for some categories.

### Scalability

Shard by SKU hash or location. Batch adjustments. Separate read replicas for reporting. Queue marketplace sync.

### Reliability

Idempotent reserves keyed by `orderLineId`. Timeout sweeper releases expired reservations. Reconciliation vs WMS counts. Exactly-once effect via unique constraints.

### Security

Service auth for WMS/OMS; privileged adjust roles audited. Prevent arbitrary negative adjust without reason + dual control for large deltas.

### Deployment Strategy

Expand/contract schema for new location attributes. Canary reservation algorithm on subset of SKUs. Load-test hot SKU.

### Operational Considerations

Metrics: reserve conflict rate, negative-block count, Kafka lag to channels, sweep latency. Cycle-count variance reports.

### Lessons Learned

Model **on_hand vs reserved vs ATP** explicitly. Hot SKUs need special partitioning strategy. Channel feed lag is a business risk — measure it.

---

## 11. Search Platform

### Business Requirements

Unified product/content search with relevance, facets, typo tolerance, and personalization hooks. Replace slow `LIKE %` SQL. Support indexing from multiple source systems.

### Functional Requirements

- Full-text search, filters, sort, facets, autocomplete.
- Near-real-time indexing from domain events/CDC.
- Synonyms, boosts, business rules (merchandising).
- Multi-tenant indexes or filters; language analyzers.
- Admin relevance tooling and query logs.

### Non-functional Requirements

- Query p95 < 200–300ms at peak.
- Index lag SLO (e.g., < 30s for critical catalog).
- High read QPS; bursty reindex jobs isolated.
- Relevance quality monitored (NDCG/click metrics).

### Architecture Diagram Description

```
[Sources: Catalog, Content, User] → events/CDC
                    │
                    ▼
            [Indexer Workers] → transform docs
                    │
                    ▼
         [OpenSearch / Elasticsearch cluster]
                    │
                    ▼
            [Search API / BFF] ← query understanding
                    │
         Redis: autocomplete / popular queries
                    │
         Kafka: query logs → ranking feedback
```

OLTP DBs are sources; search cluster is a **derived read model**.

### Technology Choices

- OpenSearch/Elasticsearch; Spring Boot search API.
- Kafka Connect / custom consumers for indexing.
- Redis for suggestions; PostgreSQL for synonyms/rules config.
- Blue/green index aliases for reindex.

### Tradeoffs

Relevance vs freshness vs cost. Heavy scripting in queries hurts p99. Dual-write to search from services is fragile — prefer events/CDC.

### Alternative Designs

- **Typesense/Meilisearch** — simpler ops, less enterprise features.
- **SQL full-text** — OK for small catalogs.
- **Vector search sidecar** — semantic recall; hybrid with keyword.

### Scalability

Shard indexes by tenant or category; replica for read. Isolate heavy aggregations. Autocomplete on separate small index. Autoscale search API; storage-bound cluster sized for heap discipline.

### Reliability

Alias swap for zero-downtime reindex. Dead-letter for poison docs. Snapshot/restore tested. Degrade: serve cached popular results if cluster sick.

### Security

Document-level security / tenant filters mandatory. Strip PII from indexed docs unless required. Audit admin relevance changes.

### Deployment Strategy

Index versioning + alias; canary new analyzers on shadow queries. Replay Kafka from checkpoint to rebuild.

### Operational Considerations

Heap, circuit breakers, refresh interval, lag, slow query logs, relevance dashboards. Capacity before big catalog imports.

### Lessons Learned

Search is a **product**: indexing pipeline + relevance + ops. Never treat the search cluster as system of record. Alias-based reindex is table stakes.

---

## 12. Recommendation Platform

### Business Requirements

Personalized recommendations (home feed, “similar,” “bought together”) to lift conversion. Must respect freshness, cold-start, and privacy. Online inference latency tight for PDP/home.

### Functional Requirements

- Batch + nearline model training from events (views, purchases).
- Online feature retrieval + ranking service.
- Business rules overlay (stock, brand pins, suppressions).
- A/B experiments; explanation optional (“because you viewed X”).
- Fallback to popularity when personalization unavailable.

### Non-functional Requirements

- Recommend p95 < 100–150ms.
- Eventual consistency OK for features; stale features bounded.
- Privacy: opt-out; no leakage of sensitive affinities where banned.
- Train/serve skew monitored.

### Architecture Diagram Description

```
[Clickstream] → Kafka → [Feature Pipeline]
                              │
                     ┌────────┴────────┐
                     ▼                 ▼
              [Offline train]   [Nearline features]
                     │                 │
                     ▼                 ▼
              [Model Registry]   [Feature Store / Redis]
                     │                 │
                     └────────┬────────┘
                              ▼
                    [Ranker Service] ← context (user, item)
                              │
                              ▼
                    [Business Rules Filter] → response
                              │
                         Postgres experiment config
```

Candidate generation (ANN/co-visitation) then rank then filter.

### Technology Choices

- Kafka event bus; Flink/Spark for pipelines.
- Redis/Feature store for online features; S3 for training data.
- Spring Boot ranker API; optional Python model service behind it.
- OpenSearch k-NN or dedicated ANN (Faiss/ScaNN) for candidates.
- Experimentation service / feature flags.

### Tradeoffs

Complex ML stack vs rule-based heuristics. Real-time features improve quality; raise cost and failure modes. Global model vs per-tenant models.

### Alternative Designs

- **Heuristics + merchandising only** — strong baseline.
- **Vendor RecSys** — faster, less differentiation.
- **Fully online learning** — powerful, harder to govern.

### Scalability

Cache recommendations briefly per user segment; precompute for heavy hitters. Shard feature keys by userId. Async enrichment; tight sync path only for rank.

### Reliability

Hard fallbacks: popular / category bestsellers. Timeouts on model service with default ranking. Feature store failure should not 500 the PDP — degrade.

### Security

PII minimization in events; purpose limitation; access controls on training data. Prevent recommendation of restricted items (compliance filters last).

### Deployment Strategy

Model version canary by user cohort; shadow ranking compare. Replay pipelines in staging. Feature flag new candidate sources.

### Operational Considerations

CTR/CVR, fallback rate, feature freshness, train/serve skew, latency histograms. Joint ownership ML + platform engineering.

### Lessons Learned

**Always ship a fallback.** Rules filters after ML prevent embarrassing recommendations. Start with simple co-visitation before deep models — often enough for v1.

---

## 13. Messaging Platform

### Business Requirements

Real-time messaging for customers/agents or user-to-user chat: 1:1, groups, presence, media, history search. Mobile-first; must work on flaky networks.

### Functional Requirements

- Send/receive messages; delivery/read receipts.
- Conversations, participants, typing indicators, presence.
- Media attachments via file platform.
- Moderation hooks; block/report; retention policies.
- Server-side history sync and catch-up after reconnect.

### Non-functional Requirements

- Low latency fan-out; at-least-once to devices with dedupe.
- Horizontal scale to millions of connections (as product grows).
- Durable history; ordered messages per conversation (practical monotonicity).
- Encryption in transit; optional E2E for sensitive products.

### Architecture Diagram Description

```
[Clients] ⇄ WebSocket Gateway farm
                │
                ▼
        [Connection / Presence Service] → Redis presence
                │
                ▼
        [Chat API / Message Service]
                │
         Postgres (conversation metadata)
         Cassandra/Dynamo/Scylla (message timelines)  OR  Postgres partitioned
                │
                ▼
         Kafka message.fanout → [Push Notify] [Moderation] [Search indexer]
                │
         [Object Storage] for media
```

Gateway holds sockets; core services remain stateless where possible.

### Technology Choices

- Netty/Spring WebFlux or dedicated gateway (or MQTT for IoT-like).
- Redis for presence and pub/sub routing to gateway nodes.
- Wide-column or carefully partitioned Postgres for message history.
- Kafka for async side effects; OpenSearch for history search.
- Push via notification platform.

### Tradeoffs

Strict global ordering is expensive; per-conversation ordering is usually enough. E2E encryption limits server-side search/moderation. WS gateway state complicates deploy.

### Alternative Designs

- **Managed chat (Twilio/Stream)** — faster MVP.
- **XMPP cluster** — legacy fit.
- **Email-style async only** — not real-time.

### Scalability

Shard conversations; sticky or route-by-conversation to gateway via Redis pub/sub. Separate media path. Archive cold history to object storage.

### Reliability

Client idempotency keys; durable write before ack; replay from cursor. Gateway drain on deploy. Moderation async with undo.

### Security

Auth on connect; authZ every send; rate limit; E2E optional; encrypt attachments; retain per policy; audit admin access to history.

### Deployment Strategy

Rolling gateway with connection draining; message service canary. Schema for message envelopes versioned. Load-test reconnect storms.

### Operational Considerations

Connected clients, fan-out latency, Redis CPU, write latency, unread queues, moderation backlog. Runbook for Redis failover (presence blip acceptable; history not).

### Lessons Learned

Split **connection plane** from **message durability**. Ack only after durable write. Presence may be lossy; message history must not be.

---

## 14. Healthcare Platform

### Business Requirements

Clinical and patient-facing platform: appointments, EHR-lite records, e-prescriptions, telehealth. Subject to health data regulations (HIPAA-like / local privacy acts). Clinicians need reliable workflows; patients need usable apps.

### Functional Requirements

- Patient demographics, encounters, notes, allergies, meds.
- Appointment scheduling with provider calendars.
- eRx with drug interaction checks (integration).
- Telehealth session signaling; consent capture.
- Audit of every PHI access; break-glass access.

### Non-functional Requirements

- Confidentiality and integrity of PHI paramount.
- Availability for clinical hours; defined RTO/RPO.
- Strong AuthZ (role + relationship + purpose).
- Traceable disclosures; retention and right-to-access workflows.

### Architecture Diagram Description

```
[Patient App] [Clinician EHR UI] [Partner HL7/FHIR]
                    │
                    ▼
              [API Gateway + WAF]
                    │
         ┌──────────┼──────────┐
         ▼          ▼          ▼
   [Patient]  [Clinical]  [Scheduling]
         │          │          │
         └──────────┼──────────┘
                    ▼
            [PHI-store Postgres]
            field-level encryption / vault
                    │
            [Consent & Audit Service] → immutable audit log (WORM)
                    │
            Kafka (minimal PHI) → Notify / Analytics (de-identified)
                    │
            [Telehealth] → SFU/media vendor
            [eRx Adapter] → pharmacy networks
```

Prefer FHIR-aligned resources at boundaries.

### Technology Choices

- Spring Boot; PostgreSQL with encryption; key in KMS/HSM.
- FHIR API layer for interoperability.
- Kafka with strict payload hygiene (IDs, not clinical text) where possible.
- Redis short-lived sessions; never PHI cache long-term without controls.
- Object storage for imaging with separate auth.

### Tradeoffs

Interoperability (FHIR) vs speed of proprietary models. Telehealth build vs vendor. Strict access control vs clinician UX under emergency (break-glass).

### Alternative Designs

- **Buy EHR + build channels** — common and often wiser.
- **Event-sourced clinical ledger** — strong audit; complex UX queries.
- **Regional data residency silos** — required in some jurisdictions.

### Scalability

Partition by clinic/tenant; separate read models for scheduling search. Telehealth media scaled via vendor. Analytics on de-identified warehouse only.

### Reliability

Appointment double-book prevention (see booking patterns). Clinical write path strongly consistent. Backup/restore tested with encryption keys. Degrade non-clinical features first.

### Security

Threat model PHI. MFA for clinicians; short sessions; audit every read. Minimize PHI in tickets/logs. BAAs with vendors. Static analysis + pen tests. Data classification automated.

### Deployment Strategy

Change control aligned with clinical risk. Canary on non-production-like tenants first. Dual-run FHIR mappings. Documented rollback for clinical workflows.

### Operational Considerations

Access anomaly detection, backup restore drills, consent revocation latency, telehealth join failure rates. Compliance evidence continuous, not annual scramble.

### Lessons Learned

**Security and audit are the architecture.** Prefer buy for core clinical systems when possible. De-identify early for analytics. Break-glass must be loud and reviewed.

---

## 15. IoT Platform

### Business Requirements

Ingest telemetry from large device fleets (sensors, gateways, vehicles). Command/control devices; alert on anomalies; expose APIs/dashboards for operations. Devices are unreliable, offline often, and insecure by default.

### Functional Requirements

- Device registry/provisioning; credentials rotation.
- Ingest telemetry (high volume); validate; store hot/cold.
- Rules engine for alerts; downlink commands with ack.
- OTA firmware updates with staged rollout.
- Tenant isolation for B2B fleets.

### Non-functional Requirements

- Millions of messages/hour capability path; burst tolerant.
- Ordered processing per device where required.
- Backpressure when consumers slow; no unbounded memory.
- Secure device identity; compromise containment.

### Architecture Diagram Description

```
[Devices] → MQTT/HTTPS → [IoT Gateway / Broker (MQTT)]
                              │
                              ▼
                    [Ingest Validators]
                              │
                              ▼
                         Kafka topics
                         (deviceId keyed)
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        [Stream Rules]  [Time-series DB]  [Cold lake S3]
              │           (Timestream/
              │            Timescale)
              ▼
        [Alerting] → Notify
              │
        [Command Service] → downlink queue → device ack
              │
        [Device Registry] Postgres + certs in HSM/KMS
```

### Technology Choices

- MQTT broker (EMQX/HiveMQ/AWS IoT Core); Kafka for durable fan-out.
- Spring Boot control plane (registry, commands, OTA).
- TimescaleDB/Timestream for hot telemetry; S3 + Parquet cold.
- Redis for device session/rate limits.
- Kubernetes for consumers; autoscale on lag.

### Tradeoffs

Cloud IoT suite vs self-hosted broker. Exact per-device ordering vs max throughput. Edge aggregation reduces cost; increases firmware complexity.

### Alternative Designs

- **HTTPS-only polling devices** — simpler, worse battery/latency.
- **Fully edge-processed** — low cloud cost; hard fleet governance.
- **Vendor IoT platforms** — faster compliance path.

### Scalability

Partition Kafka by `deviceId`; aggregate at edge; downsample stored metrics. Tiered storage. Separate command path capacity from ingest firehose.

### Reliability

At-least-once ingest + idempotent upserts by `(deviceId, ts, metric)`. Command retries with sequence numbers; OTA resume. Dead-letter poison payloads. Clock skew handling on devices.

### Security

Per-device certs/keys; mutual TLS; least privilege IoT policies; signed firmware; secure boot where possible; anomaly detection on device behavior; rapid revoke.

### Deployment Strategy

Consumer canaries; schema evolution for telemetry envelopes. OTA rings (canary devices → cohorts → fleet). Never OTA 100% without health abort gates.

### Operational Considerations

Connect churn, ingest lag, rule false-positive rate, OTA success, credential expiry. Capacity planning for reconnect storms after outages.

### Lessons Learned

Assume devices are **hostile and flaky**. Durable ingest bus + per-device keys solve half the pain. OTA is a product with kill switches, not a file copy.

---

## 16. Multi-Tenant SaaS Platform (Bonus)

### Business Requirements

B2B SaaS serving many customers (tenants) on shared infrastructure with strong isolation, per-tenant config, and enterprise features (SSO, audit export, data residency options).

### Functional Requirements

- Tenant provisioning; plans/quotas; feature flags per tenant.
- Logical isolation of data; optional dedicated DB for enterprise tier.
- Custom domains; tenant admin IAM; audit logs exportable.
- Usage metering and billing integration.

### Non-functional Requirements

- No cross-tenant data leakage (hard invariant).
- Noisy-neighbor controls; fair scheduling.
- Per-tenant encryption keys for premium tier.
- Scalable onboarding (minutes, not weeks).

### Architecture Diagram Description

```
[Tenant Users] → [Gateway] → tenant resolver (domain/header/JWT claim)
                        │
                        ▼
              [App Services] enforce tenant_id on every query
                        │
         ┌──────────────┼──────────────┐
         ▼              ▼              ▼
   Shared Postgres   Dedicated Postgres  Object Storage prefix/CMK
   (RLS / tenant_id) (enterprise)
                        │
                   [Metering] → Kafka → Billing
                   [Admin Control Plane]
```

### Technology Choices

- Spring Boot; Hibernate filters / Postgres RLS as defense in depth.
- Redis rate limits per tenant; Kafka metering events.
- Stripe/billing adapter; LaunchDarkly-style flags.
- K8s namespaces + network policies; optional dedicated node pools.

### Tradeoffs

Shared tenancy cost efficiency vs isolation. RLS helps but app bugs still leak without rigorous `tenant_id` discipline. Cell-based architecture for large scale.

### Alternative Designs

- **Silo per tenant** — max isolation, max cost.
- **Cell/AMDA architecture** — groups of tenants per cell.
- **Fully pooled with strict RLS only** — lean startup mode.

### Scalability

Shard tenants across cells; hot tenants upgraded to silo. Cache tenant config. Partition Kafka by tenant for metering.

### Reliability

Backup/restore per cell; avoid noisy neighbor DB vacuum storms; quota enforcement before hard failures.

### Security

Defense in depth: claim → middleware → RLS → storage prefix. Pen-test cross-tenant. SSO per tenant; SCIM provisioning.

### Deployment Strategy

Control plane separate from data plane. Migrate tenant between cells with dual-run. Canary new app version on low-risk cell.

### Operational Considerations

Per-tenant SLO views; abuse detection; key rotation; residency compliance maps.

### Lessons Learned

**Tenant identity on every layer.** Cells beat infinite shared monoliths at scale. Enterprise isolation is a product tier, not an afterthought.

---

## 17. Real-Time Analytics / Observability Pipeline (Bonus)

### Business Requirements

Company needs product analytics and operational metrics: event ingestion, stream processing, dashboards, anomaly alerts. Must not destabilize OLTP.

### Functional Requirements

- SDKs/APIs for track events; schema registry.
- Stream transforms; funnels; sessionization.
- OLAP serving for dashboards; alert rules.
- Data quality checks; late event handling.

### Non-functional Requirements

- High ingest; query freshness SLO (seconds to minutes).
- Backpressure; isolation from production DBs (CDC ok, dual-write carefully).
- Cost controls on retention and scan.

### Architecture Diagram Description

```
[Apps] → Kafka (raw events)
            │
            ▼
     [Stream Processor Flink]
            │
     ┌──────┼──────┐
     ▼      ▼      ▼
  Hot OLAP  Lake   Alerts
 (ClickHouse/ Druid / Pinot)   S3
            │
         [Query API / BI]
```

### Technology Choices

- Kafka; Flink/Spark; ClickHouse or Druid; Spring Boot query API.
- Schema Registry; Great Expectations-style DQ.
- Grafana/Superset; PagerDuty for alerts.

### Tradeoffs

Freshness vs cost. Exact-once analytics rarely needed — approximate is fine if disclosed. CDC vs app events ownership.

### Alternative Designs

- **Warehouse-only ELT (Snowflake/BigQuery)** — simpler, higher latency.
- **Vendor analytics (Amplitude/Mixpanel)** — faster product analytics.

### Scalability / Reliability / Security / Deployment / Ops

Scale consumers independently; tier storage; quarantine bad schemas; PII scrubbing at ingest; canary new jobs on shadow topics; monitor lag and query scan bytes.

### Lessons Learned

Treat analytics as a **product with SLOs**, not a dump of logs. Schema discipline prevents lake rot.

---

# Interview Challenge + Suggested Answer

## Challenge

> Design a **booking + payment** flow for a multi-supplier travel platform that must not double-book, must handle PSP timeouts, and must keep search results fast during a 10× traffic spike. You have three teams and six months to production. Whiteboard the architecture and call out the top three risks.

## Suggested Answer (condensed)

**Clarify:** inventory ownership (us vs supplier), hold TTL, payment methods, multi-region, compliance.

**Architecture sketch:**

1. **Search read path** — OpenSearch + Redis offer cache; supplier fan-out behind bulkheads; partial results OK.
2. **Booking write path** — Booking service owns order state machine; Inventory reservations with TTL; revalidate offer before pay.
3. **Payment** — Payment platform intent + idempotency key; PSP adapter; webhook truth with inquire-on-timeout.
4. **Integration** — Kafka outbox for `OrderHeld`, `OrderPaid`, `OrderTicketed`; Notify and partner sync async.
5. **Strangler timeline** — modular monolith for order+inventory if teams are not ready; extract payment and search first.

**Top risks:**

1. **Availability drift** between search cache and true inventory → mandatory revalidation + measured lag SLO.
2. **Payment ambiguity** (timeout with unknown capture) → inquire API + idempotent state machine, never double confirm.
3. **Distributed saga complexity** across suppliers → explicit pending states, compensation playbooks, game-days before peak.

**What I would not do in six months:** full event sourcing, multi-region active-active ledger, or 15 microservices. Prefer a **modular booking monolith**, separate **search**, and a thin **payment orchestrator**.

---

# Architecture Reflection Questions

1. Where is the **system of record** for the critical invariant, and what is allowed to be eventually consistent?
2. What happens if the message broker is down for 15 minutes? For 15 hours?
3. How do you **idempotently** retry the most dangerous write in this design?
4. Which team owns the **failure** when service A’s event is misinterpreted by service B?
5. What is your **rollback** if a schema or event change ships broken?
6. Where does **PII/PHI/PCI** appear in logs, topics, and read models — and how is it minimized?
7. What is the **hot key / hot partition** in this design under 10× load?
8. If you must cut scope by 50%, which box do you delete first without lying to the customer?
9. How would you dual-run an old and new implementation safely?
10. What SLO would page you at 3 a.m., and what is the first graph you open?

---

# Interview Confidence Checklist

- [ ] I can clarify constraints before drawing boxes
- [ ] I can explain strangler modernization vs rewrite with a real example
- [ ] I can defend monolith vs microservices for a stated team size
- [ ] I can design a booking/inventory hold without double-booking
- [ ] I can walk through payment idempotency and webhook duplicates
- [ ] I can place Kafka/outbox correctly (and say when not to use Kafka)
- [ ] I can separate search/read models from OLTP systems of record
- [ ] I can discuss auth (OIDC, MFA, service identity) at platform level
- [ ] I can address PHI/PCI/PII in architecture answers without hand-waving
- [ ] I can name failure modes, compensations, and observability for my design
- [ ] I can propose a 6–12 month evolution path, not only the end state
- [ ] I have at least three personal production stories mapped to case studies above

---

## Notes

<!-- Map each case study to a system you owned: decisions, incidents, metrics, what you would redo -->
