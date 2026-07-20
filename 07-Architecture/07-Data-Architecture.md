# Data Architecture

> Data design is consistency, operability, and migration cost — storage engines are an implementation detail of those constraints.

---

## Purpose

Prepare Lead/Architect candidates to reason about SQL vs NoSQL, CQRS, event sourcing, read models, synchronization, eventual consistency, migrations, and multi-tenancy — with emphasis on **when not to adopt** fashionable patterns, under PostgreSQL/MySQL, Redis, Kafka, and Elasticsearch-class projections.

---

## Topics Covered

- [ ] SQL
- [ ] NoSQL
- [ ] CQRS
- [ ] Event Sourcing
- [ ] Read Models
- [ ] Data Synchronization
- [ ] Eventual Consistency
- [ ] Data Migration
- [ ] Multi Tenancy

---

## SQL

### Production framing

Relational databases remain the **default system of record** for enterprise Java: ACID transactions, constraints, mature backup/PITR, declarative integrity. PostgreSQL (JSON/JSONB, rich indexing) and MySQL/InnoDB dominate Spring/JPA estates. The decision to leave SQL should be forced by access patterns and scale evidence — not résumé pressure.

### Production use cases

- Ledgers, balances, inventory reservations, entitlements.
- Workflow state machines needing multi-row invariants.
- Multi-table reporting inside a modest cardinality domain.
- Tenant metadata with relational integrity to billing.

### Engineering notes

- Model **aggregates/consistency boundaries** explicitly; avoid god-schemas shared by all services.
- Use constraints (`UNIQUE`, `FK` where appropriate, `CHECK`) as last-line defense.
- Optimistic locking (`@Version`) for contended entities; pessimistic locks sparingly for scarce resources.
- Connection pools (Hikari) sized to DB capacity × pod count — a classic Kubernetes footgun.
- Read replicas for read scale; session/read-your-writes sticky when required.

---

## NoSQL

### Production framing

Document, key-value, wide-column, and search engines optimize specific access patterns: key lookup at massive scale, flexible documents, write-heavy time series, or full-text. They trade multi-entity ACID and ad-hoc join flexibility for partition-friendly scale and operational models that differ sharply from Postgres.

### Production use cases

- **Redis:** cache, sessions, rate limits, ephemeral locks (not system of record for money).
- **MongoDB/Document:** highly variable catalog attributes with careful uniqueness strategy.
- **Cassandra/Dynamo-style:** high write throughput keyed by partition; feed/timeline patterns.
- **Elasticsearch/OpenSearch:** search/analytics projections — rebuildable, not authoritative stock counts.

### When NoSQL hurts

- You still need cross-key transactions (money movement across accounts).
- You invent application joins across five collections.
- You lack ops skill for the engine’s backup/restore/compaction failure modes.

Polyglot persistence is a **product decision with a sync plan**, not a trophy shelf.

---

## CQRS

### Production framing

**CQRS** (Command Query Responsibility Segregation) splits write models from read models so each can optimize independently. Writes enforce invariants on a transactional model; reads serve denormalized queries, search, or dashboards. Synchronization is asynchronous (events/CDC) unless you accept the cost of synchronous dual update.

### Production use cases

- Complex write rules (lending underwriting) + simple high-QPS read dashboards.
- Different scaling: writes small QPS, reads massive fan-out.
- Multiple read shapes (mobile summary vs back-office grid) without polluting the write schema.

### When NOT to use CQRS

- CRUD apps with modest query needs — you buy distributed complexity for nothing.
- Team cannot operate eventual read lag or rebuild pipelines.
- “CQRS” as synonym for “two tables in the same DB updated in one transaction” — that’s just a read table; fine, but don’t claim the pattern’s benefits/costs.

Start with a **modular monolith** single model; extract read models when query pain is measured.

---

## Event Sourcing

### Production framing

**Event sourcing** stores state as an append-only sequence of domain events; current state is a fold/projection. Excellent auditability and temporal queries; expensive cognitively and operationally (versioning, snapshots, PII, rebuild times, debugging).

### Production use cases

- Domains where the **history is the product** (trading, claims adjudication, collaborative editors).
- Regulatory need to reconstruct why a decision was made.
- Complex temporal business rules.

### When NOT to use event sourcing

- Standard CRUD enterprise forms.
- Teams without strong domain modeling discipline.
- Need for ad-hoc SQL reporting on current state without investing in projections.
- High churn schema with unclear event versioning strategy.
- Soft-delete/GDPR erasure without a designed redaction story.

ES ≠ using Kafka. Kafka may transport events; the **source of truth event store** is a deliberate choice (EventStoreDB, Postgres event table, etc.) with retention and uniqueness semantics.

Often **audit log + current state** is enough without full ES.

---

## Read Models

### Production framing

Read models are **projections optimized for queries**: SQL views/materialized views, Redis documents, Elasticsearch indices, GraphQL precomputed fields. They must be rebuildable from the source of truth (DB, event stream, or snapshot + CDC).

### Production use cases

- Account summary card for mobile (denormalized balances + flags).
- Search index of products.
- Admin “customer 360” assembled from multiple services into a serving store.

### Operational rules

- Define **lag SLO** (e.g., 95% of projections < 5s).
- Define **rebuild runbook** (from snapshot, from events, from CDC replay).
- Never let a read model silently become write authority.
- Version projection schemas; blue/green index builds for Elasticsearch.

---

## Data Synchronization

### Production framing

Sync strategies move data across stores without lying about consistency:

| Pattern | Use | Risk |
|---------|-----|------|
| Transactional outbox + events | Domain sync between services | Consumer lag |
| CDC | DB → warehouse/search/cache | Schema coupling |
| Batch ETL | Analytics, non-urgent | Stale windows |
| Dual write | Almost never | Divergence |
| Shared DB | Legacy integration | Coupling, outage blast radius |

### Production use cases

- Order service → Search via Kafka events.
- OLTP Postgres → Snowflake via CDC.
- Redis cache aside with TTL + event invalidation.

Idempotent consumers, ordering per key, and reconciliation jobs (nightly checksums) separate hobby sync from production sync.

---

## Eventual Consistency

### Production framing

**Eventual consistency** means replicas/projections converge given no new writes — with **lag** and **temporary disagreement**. User experience and financial controls must account for it; pretending it is “same as ACID” causes support nightmares.

### Production manifestations

- Read replica lag → user doesn’t see their update.
- Search index lag → “order not found” in search after checkout.
- Cross-service saga in flight → inventory reserved but order UI still pending.

### Design levers

- **Read-your-writes:** sticky sessions, primary reads after write, or version tokens (`If-Match` / `updatedAt`).
- **UX honesty:** “Processing” states instead of fake finality.
- **Business rules:** money movement stays strongly consistent inside one aggregate/transaction; cross-domain is eventual with compensation.
- **SLOs:** measure lag; alert on projection delay, not only error rate.

CAP talk in interviews is cheap; **name the inconsistency window and the user impact** — that is senior.

---

## Data Migration

### Production framing

Migrations are production software: expand/contract, backfills, observability, and rollback. Liquibase/Flyway version DDL; application code must tolerate dual schemas during rollout.

### Patterns

1. **Expand:** add nullable column/table; deploy writers that fill both old and new.
2. **Backfill:** batched jobs with throttling; monitor locks and replication lag.
3. **Contract:** switch readers; remove old after verification.
4. **Online DDL:** respect engine specifics (Postgres `CONCURRENTLY`, gh-ost/pt-osc for MySQL large tables).

### Multi-store migrations

Moving from monolith DB to service DBs: dual write is dangerous — prefer CDC or outbox shadow reads, compare, then cut traffic. Keep a reconciliation report.

### Failure modes

- Long locks taking down checkout.
- Migration applied in app pods concurrently without leadership.
- Irreversible data transform without backup snapshot.
- Kubernetes rolling deploy mid-migration assuming instant DDL compatibility.

---

## Multi Tenancy

### Production framing

Multi-tenancy isolates customer data and noisy neighbors while sharing infrastructure cost. Isolation strength vs cost is the core tradeoff.

| Model | Isolation | Cost | Fit |
|-------|-----------|------|-----|
| Shared schema + `tenant_id` | Weakest (bug = leak) | Lowest | Early SaaS, strong review culture |
| Schema-per-tenant | Medium | Medium | Moderate counts, Postgres |
| Database-per-tenant | Strong | High | Enterprise/regulated, few large tenants |
| Hybrid | Tiered | Complex | Free tier shared; enterprise dedicated |

### Cross-cutting concerns

- **Authz:** every query must be tenant-scoped; row-level security (Postgres RLS) as defense in depth.
- **Noisy neighbor:** rate limits, pool partitions, per-tenant quotas, separate consumer groups.
- **Migrations:** thousands of schemas need automation and blast-radius control.
- **Backups/restores:** per-tenant restore is hard on shared tables — plan ahead.
- **Encryption/keys:** per-tenant keys for high-security tiers.

Spring: tenant context in `ThreadLocal`/Reactor context; Hibernate multi-tenant connection providers — test for context leaks across threads aggressively.

---

## Consistency model cheat sheet

| Data | Typical model | Store |
|------|---------------|-------|
| Wallet balance | Strong, single aggregate TX | SQL |
| Shopping cart | Strong per user; eventual across devices | SQL + cache |
| Search results | Eventual | Elasticsearch |
| Session | Eventual / TTL | Redis |
| Audit trail | Append-only, immutable | SQL/ES/object |
| Analytics | Highly eventual | Warehouse |

---

## Why this matters in production

Most “microservices disasters” are **data disasters**: distributed transactions without boundaries, CQRS adopted as fashion, event sourcing without rebuild tooling, multi-tenant leaks, migrations that lock tables during peak. Lead interviews reward candidates who protect **invariants and operability** over those who name the most patterns.

Cost is not only cloud spend — it is on-call cognitive load, migration risk, and time-to-diagnose when projections diverge.

---

## Engineering tradeoffs

| Choice | Benefit | Cost |
|--------|---------|------|
| Single SQL system of record | Simplicity, integrity | Scale ceiling, coupling risk if shared carelessly |
| Polyglot | Fit-for-purpose queries | Sync, skills, failure modes × N |
| CQRS | Independent scale/shape | Lag, dual pipelines, mental model |
| Event sourcing | Perfect audit, temporal | Complexity, PII, versioning, rebuild |
| Cache-aside Redis | Latency/cost wins | Invalidation bugs, stampede |
| Shared-table tenancy | Efficiency | Leak risk, noisy neighbor |
| DB-per-tenant | Isolation | Ops explosion |
| Sync dual-write | “Instant” consistency illusion | Divergence under partial failure |
| Eventual projections | Decoupling | UX and reconciliation burden |

---

## Common anti-patterns

1. **Shared database across “microservices”** with unrestricted joins — distributed monolith with extra failure modes.
2. **CQRS/ES by default** on CRUD domains.
3. **Elasticsearch as system of record** for inventory/money.
4. **Dual write** without reconciliation.
5. **Cache without TTL or invalidation story**.
6. **tenant_id filter forgotten** on one query path — data leak SEV.
7. **Big-bang migrations** on hot tables in peak hours.
8. **Read replica without read-your-writes strategy** for post-update screens.
9. **Event store without snapshots** — multi-second folds on hot aggregates.
10. **Unbounded JSON blobs** in Postgres as substitute for modeling — then indexing pain.
11. **Cross-tenant unique indexes missing** (`email` global vs per tenant).
12. **Assuming Kafka retention = forever audit** without policy.

---

## Best practices

1. Start with **one transactional store per bounded context**; add stores with an explicit sync design.
2. Keep **strong consistency inside money/inventory aggregates**; eventual across contexts with visible UX states.
3. Adopt CQRS only with **lag SLOs, rebuild runbooks, and owners**.
4. Prefer **audit tables + state** before full event sourcing.
5. Make all projections **rebuildable**; practice rebuild in staging.
6. Use **outbox/CDC**, never hopeful dual-write.
7. Migrations: **expand/contract**, batched backfills, lock monitoring.
8. Multi-tenancy: defense in depth (context + forced predicate + RLS where warranted).
9. Size pools and replicas for **pod × pool ≤ DB max_connections**.
10. Measure **replication/projection lag** as a product metric.
11. Classify data (PII, payment) per store; encryption and retention accordingly.
12. Document **source of truth** per entity in an ADR.

---

## Architecture review checklist

- [ ] System of record named per entity; projections marked non-authoritative
- [ ] Consistency requirements mapped to user journeys
- [ ] CQRS/ES justified with workload evidence — or explicitly rejected
- [ ] Sync pattern chosen (outbox/CDC/batch); reconciliation defined
- [ ] Lag SLOs and alerts for replicas/projections
- [ ] Migration plan expand/contract with rollback
- [ ] Multi-tenant isolation model and leak-test strategy
- [ ] Connection pool math under Kubernetes scale-out
- [ ] Backup/PITR/restore tested for primary stores
- [ ] PII flows across Kafka/CDC/search reviewed
- [ ] Hot keys / whale tenants considered
- [ ] Read-your-writes behavior specified where UX needs it
- [ ] Cost model: storage, secondaries, search clusters, ops time

---

## Interview Challenge

You are designing data architecture for a multi-tenant B2B SaaS invoicing product:

- Strong correctness for invoice finalize and payments.
- Customers demand full-text search across invoice line items.
- Exec dashboards need aggregates across millions of rows.
- Enterprise tier wants data isolation guarantees.
- Team is six backend engineers, strong Spring/Postgres skills, weak ES/CQRS ops experience.

Propose the data architecture for the next 18 months. Explicitly state what you will **not** build yet.

---

## Suggested Answer

**System of record:** Postgres, shared schema with `tenant_id` + Hibernate filters + integration tests for leak paths; Postgres RLS as defense in depth for critical tables. Enterprise tier path: dedicated schema/DB option in roadmap, not day one for all.

**Writes:** Invoice aggregate transactional finalize; payment intents with idempotency keys; no distributed transactions across exotic stores.

**Search:** CDC or outbox → Elasticsearch **projection**; ES never authoritative; rebuild script from Postgres. Lag SLO e.g. 30s; UI may fall back to SQL search for immediate post-create if needed.

**Dashboards:** start with SQL materialized views / nightly warehouse ETL; defer real-time OLAP. If needed later, CDC → ClickHouse/BigQuery.

**Cache:** Redis for session and hot GET invoice PDF metadata — TTL + invalidate on update events.

**Explicitly not yet:** event sourcing, full CQRS write/read service split, Kafka as ledger, Mongo for invoices, database-per-tenant for every customer.

**Why:** correctness and team skill beat pattern completeness; buy isolation upgrades when enterprise deals pay for ops cost. Revisit CQRS when dashboard/search load proves Postgres insufficient **and** staffing can own pipelines.

---

## Architecture Reflection Questions

1. Where did a projection diverge in a system you know, and how was it detected?
2. What consistency explanation do you give product managers for search lag?
3. When have you successfully argued **against** CQRS or event sourcing?
4. How do you test for multi-tenant data leaks in CI?
5. What is your expand/contract example from a real migration?
6. How do you calculate Hikari pool sizing for an HPA’d Spring service?
7. What makes a read model “rebuildable” in your definition?

---

## Interview Confidence Checklist

- [ ] Can select SQL vs NoSQL from access patterns and consistency needs
- [ ] Can explain CQRS benefits and give clear non-goals / when-not
- [ ] Can explain event sourcing and why audit logs often suffice
- [ ] Can design outbox/CDC sync with reconciliation
- [ ] Can discuss eventual consistency with UX remedies
- [ ] Can walk through expand/contract migration steps
- [ ] Can compare multi-tenant isolation models with leak risks
- [ ] Can identify system of truth vs projection in a diagram
- [ ] Can discuss operational cost (people + cloud) of polyglot data
- [ ] Has a coherent 18-month sequencing story (build now vs later)

---

## Notes

<!-- Your CQRS/ES rejections, tenant isolation, and migration war stories -->
