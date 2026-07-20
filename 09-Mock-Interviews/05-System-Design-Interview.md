# 05 — System Design Interview

> Full-loop system design drills for enterprise Java engineers — requirements through tradeoffs.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–60 minutes |
| Goal | Structured design under ambiguity |
| Success | Clarifying questions, sensible APIs, failure modes, evolution path |

---

## Purpose

Simulate real panels at banks, telcos, marketplaces, and platforms. Assume Java/Spring-capable implementation without drowning in framework trivia.

---

## How Interviewers Evaluate

| Axis | Strong signal |
|------|---------------|
| Requirements | Functional + non-functional; explicit assumptions |
| Structure | Clear components and trust boundaries |
| Data | Correct storage choices and consistency model |
| Scale | Bottlenecks identified before premature sharding |
| Reliability | Retries, idempotency, backpressure, DR |
| Communication | Checkpointed narrative; diagrams |

---

## Common Mistakes

- Jumping to Kafka/microservices before use cases.
- Ignoring consistency for money/inventory.
- No numbers (QPS, payload, retention).
- Drawing boxes without failure arrows.
- Never mentioning security or multi-tenancy when relevant.

---

## Excellent Communication Techniques

1. Agenda aloud (8–10 min requirements, 20 design, 15 deep dive, 5 wrap).
2. Ask capacity estimates; state assumptions if interviewer declines.
3. Offer depth choices: "API, data model, or scaling — where should we go deep?"

---

## Confidence Tips

Practice the same skeleton every time: clients → gateway → services → data → async → ops.

---

## Ideal Answer Framework

**Clarify → NFRs → High-level → Data model → Deep dive (1–2) → Scale & failure → Security → Tradeoffs → Evolve**

---

## Per-Interview Template (use in all 30)

Each scenario below includes: Requirements · Architecture · Scaling · Database · Caching · Messaging · Security · Tradeoffs · Senior vs Lead notes · Follow-ups · Rubric cues.

---

## Interview 01 — Booking Platform (Generic Appointments)

**Prompt:** Design a multi-tenant appointment booking platform (clinics / salons).

**Requirement Gathering:** Book/cancel/reschedule; provider calendars; double-booking prevention; notifications; multi-tenant isolation; target 5k bookings/min peak.

**Architecture:** API gateway → Booking service → Calendar service → Notification worker; Admin BFF; IdP.

**Scaling:** Partition by tenant/provider; read replicas for search; sticky concurrency controls on slots.

**Database:** Relational for bookings/slots (strong constraints); unique `(provider_id, slot_start)`; outbox for events.

**Caching:** Cache provider profiles and open-slot summaries with short TTL; never cache "available" without version/fence.

**Messaging:** BookingConfirmed / Cancelled → email/SMS/push consumers.

**Security:** Tenant authz; PII encryption; audit trails.

**Tradeoffs:** Strong consistency on slots vs eventual calendar projections.

**Senior:** Correct unique constraints + TX. **Lead:** Multi-tenant blast radius, SLO, abuse (bot bookings).

**Follow-ups:** Recurring appointments? Timezone handling? Waitlists?

---

## Interview 02 — Ride Sharing

**Prompt:** Design matching riders to drivers in a city.

**Requirements:** Request ride, match, track ETA, pricing, payments, surge; 100k concurrent city users.

**Architecture:** Ride service, Matching (geo), Location streaming, Pricing, Payments, Trip history.

**Scaling:** Geo-hash / S2 cells for drivers; separate hot path for location updates (not OLTP DB).

**Database:** Trip state in relational/NewSQL; location in Redis/geo index; analytics to warehouse.

**Caching:** Driver status in memory/Redis; fare rules cached.

**Messaging:** Trip events; payment capture async with idempotency.

**Security:** Location privacy; fraud on GPS spoofing; auth for driver apps.

**Tradeoffs:** Matching freshness vs compute cost; consistency of trip state machine.

**Follow-ups:** Cross-city trips? Offline maps? Dispatch fairness?

---

## Interview 03 — Banking Platform (Core Transfers)

**Prompt:** Design retail bank transfer initiation and ledger posting.

**Requirements:** Intra/inter bank transfers; idempotency; double-entry ledger; audit; regulatory retention; strong consistency on balances.

**Architecture:** API → Transfer orchestration → Ledger service → Limits/Fraud → Notifications; Core banking adapter.

**Scaling:** Partition accounts carefully; avoid naive distributed TX; use saga with compensating entries.

**Database:** Ledger entries append-only; balance materialized with strict TX; immutable audit log.

**Caching:** Read models for transactions list; never cache authoritative balance without fencing.

**Messaging:** TransferRequested/Posted/Failed; reconcile with payment rails.

**Security:** MFA step-up; device binding; maker-checker for high value; encryption; HSM for keys.

**Tradeoffs:** Availability vs consistency (CP preference for money).

**Lead note:** Speak to SOX/BSP/MAS-style controls without pretending to be a lawyer.

---

## Interview 04 — Food Delivery

**Prompt:** Design order placement through courier delivery.

**Requirements:** Cart, restaurant availability, courier assignment, live tracking, refunds.

**Architecture:** Order, Restaurant, Dispatch, Pricing, Payment, Notification; real-time gateway.

**Scaling:** City-based partitioning; burst lunch peaks.

**Database:** Orders relational; restaurant menus document/cached; courier location stream store.

**Caching:** Menus aggressively; restaurant open/closed flags.

**Messaging:** Order lifecycle events; dispatch offers to couriers.

**Security:** Payment PCI scope minimization; PII; partner auth.

**Tradeoffs:** Merchant inventory accuracy vs UX speed.

---

## Interview 05 — Travel Platform (Flights + Hotels)

**Prompt:** Design search and booking across flights and hotels.

**Requirements:** Search aggregation, fare holds, booking, cancellations, multi-provider.

**Architecture:** Search aggregator, Supplier adapters, Booking orchestrator, Pricing, User trips.

**Scaling:** Cache search results carefully (fare volatility); async supplier fan-out with deadlines.

**Database:** Bookings relational; search ephemeral/cache; supplier raw payloads object storage.

**Caching:** Search results short TTL; airport metadata long TTL.

**Messaging:** Booking confirmation; supplier callback handling.

**Security:** PCI if storing cards (prefer token vault); fraud on promo abuse.

**Tradeoffs:** Stale fares vs supplier QPS limits.

---

## Interview 06 — Notification Service

**Prompt:** Design multi-channel notifications (email, SMS, push, in-app).

**Requirements:** Templates, preferences, delivery receipts, retries, quiet hours, 50k events/sec.

**Architecture:** Ingest API → Validator → Router → Channel workers; Preference service; Template service.

**Scaling:** Partition by userId; per-channel rate limits; backpressure queues.

**Database:** Preferences relational; delivery logs in append store/OLAP; templates versioned.

**Caching:** Preferences and templates in Redis.

**Messaging:** Central event bus; per-channel queues; DLQ + replay.

**Security:** PII minimization; consent; secret management for providers.

**Tradeoffs:** At-least-once delivery vs user annoyance; dedupe windows.

---

## Interview 07 — Messaging Platform (Chat)

**Prompt:** Design 1:1 and group messaging.

**Requirements:** Send/receive, receipts, media, presence, search; millions of DAU.

**Architecture:** Gateway (WS), Chat service, Media service, Presence, Push fanout, Search indexer.

**Scaling:** Shard conversations; fanout-on-write vs read for large groups.

**Database:** Messages in partitioned store/Cassandra-like; conversation metadata relational; media object store + CDN.

**Caching:** Recent conversations; presence in Redis with TTLs.

**Messaging:** Internal event pipeline for push and search.

**Security:** E2E optional vs server-side encryption; auth on WS; abuse/spam.

**Tradeoffs:** Ordering guarantees vs multi-device sync complexity.

---

## Interview 08 — Payment Gateway

**Prompt:** Design a payment gateway accepting card/wallet charges for merchants.

**Requirements:** Authorize/capture/refund; idempotency; webhooks; reconciliation; PCI-aware.

**Architecture:** API → Authz → Risk → Processor adapters → Ledger → Webhook dispatcher.

**Scaling:** Horizontal API; strict idempotency keys; async webhooks.

**Database:** Payments + attempts relational; append-only events; reconcile files in object storage.

**Caching:** Merchant config; risk rules.

**Messaging:** Outbox for webhooks; retry with exponential backoff.

**Security:** Tokenization; vault; least privilege; audit; 3DS flows.

**Tradeoffs:** Sync authorize UX vs processor latency; webhook reliability.

---

## Interview 09 — Hotel Booking

**Prompt:** Design hotel search, room inventory, and booking.

**Requirements:** Search, hold inventory, book, cancel, overbooking policy.

**Architecture:** Search, Inventory, Booking, Pricing, Partner connectivity.

**Scaling:** Cache search by geo/date; inventory hot keys per property.

**Database:** Inventory counts with conditional updates/version; bookings relational.

**Caching:** Property content CDN; prices short TTL.

**Messaging:** Booking events to channel managers.

**Security:** Partner API keys; fraud; PCI outsourced.

**Tradeoffs:** Overbooking revenue vs guest experience; hold TTL length.

---

## Interview 10 — Inventory Management (Retail)

**Prompt:** Design inventory reservation across warehouses for e-commerce.

**Requirements:** Reserve on checkout, commit on payment, release on timeout; multi-warehouse.

**Architecture:** Inventory service, Reservation, Order, Warehouse OMS sync.

**Scaling:** Shard by SKU; avoid global locks; per-SKU serialization.

**Database:** Stock ledger + reservations; strong constraints.

**Caching:** Availability projections eventual; not source of truth.

**Messaging:** StockChanged; reservation expired timers (or wheel).

**Security:** Internal mTLS; audit adjustments.

**Tradeoffs:** Oversell risk vs conversion; sync ERP vs async.

---

## Interview 11 — URL Shortener

**Prompt:** Classic — design a URL shortener at scale.

**Requirements:** Create short URL, redirect, custom aliases, analytics optional, 10k writes/s, 100k reads/s.

**Architecture:** API, Key generator, Redirect service, Analytics pipeline.

**Scaling:** Cache hot redirects; partition by keyspace; CDN optional.

**Database:** key→url mapping (KV/SQL); bloom filter for existence optional.

**Caching:** Redis for hot keys; negative caching careful.

**Messaging:** Click events to analytics.

**Security:** Malicious URL scanning; rate limits; auth for custom domains.

**Tradeoffs:** Hash collision strategy vs counter-based IDs; predictability.

---

## Interview 12 — Rate Limiter Service

**Prompt:** Design a distributed rate limiter for an API platform.

**Requirements:** Per-API-key limits, burst, cluster-accurate enough, low latency.

**Architecture:** Sidecar/SDK + Redis central counters; gateway integration.

**Scaling:** Sharded Redis; local token bucket + global sync hybrid.

**Database:** Config in control plane DB; counters in Redis.

**Caching:** Limit configs cached on gateway.

**Messaging:** Config change notifications.

**Security:** Admin auth for limit changes; prevent bypass.

**Tradeoffs:** Exact global limits vs latency; fail-open vs fail-closed.

---

## Interview 13 — News Feed

**Prompt:** Design a social news feed.

**Requirements:** Post, follow, fanout feed, celebrity problem, media.

**Architecture:** Post service, Graph/follow, Fanout workers, Timeline store, Media, Ranker optional.

**Scaling:** Fanout-on-write for normal; fanout-on-read for celebrities.

**Database:** Timeline caches in Redis/Cassandra; posts durable store.

**Caching:** Home timelines; user profiles.

**Messaging:** PostCreated triggers fanout.

**Security:** Block lists; abuse; private accounts.

**Tradeoffs:** Freshness vs fanout cost; ranking complexity.

---

## Interview 14 — E-Commerce Checkout

**Prompt:** Design checkout from cart to paid order.

**Requirements:** Cart, pricing, tax, inventory reserve, payment, order, receipts.

**Architecture:** Cart, Pricing, Inventory, Order orchestrator, Payment, Tax.

**Scaling:** Sticky sessions optional; mostly stateless with cart store.

**Database:** Orders + payments relational; cart Redis/SQL.

**Caching:** Product price snapshots at checkout start.

**Messaging:** OrderPlaced; fulfillment; emails.

**Security:** PCI; promo fraud; idempotent pay.

**Tradeoffs:** Price freeze window; reservation TTL vs abandoned carts.

---

## Interview 15 — Video Streaming Platform

**Prompt:** Design upload + streaming of videos (VOD).

**Requirements:** Upload, transcode, adaptive bitrate, CDN playback, comments optional.

**Architecture:** Upload service, Object storage, Transcode workers, Metadata, CDN, Playback auth.

**Scaling:** Queue transcode by priority; CDN for egress.

**Database:** Metadata SQL; manifests in object storage.

**Caching:** Metadata; signed URL short TTL.

**Messaging:** UploadComplete → transcoder; TranscodeDone → publish.

**Security:** Signed URLs; DRM optional; copyright takedown.

**Tradeoffs:** Cost of bitrate ladders vs quality; processing latency.

---

## Interview 16 — Search Autocomplete

**Prompt:** Design typeahead for product/search terms.

**Requirements:** Low latency prefix suggestions, personalization light, typo tolerance optional.

**Architecture:** Suggest API, Trie/inverted index service, Ranking, Offline analytics.

**Scaling:** Shard by prefix; edge cache popular prefixes.

**Database:** Offline-built index artifacts; click logs OLAP.

**Caching:** Edge/CDN for top queries; in-memory tries.

**Messaging:** Query logs stream for training.

**Security:** Filter unsafe terms; privacy on personalization.

**Tradeoffs:** Freshness of new products vs index rebuild cost.

---

## Interview 17 — Distributed Job Scheduler

**Prompt:** Design cron-at-scale for millions of jobs.

**Requirements:** Exact-once *attempt* semantics carefully; retries; calendars; multi-tenant.

**Architecture:** API, Scheduler shards, Worker fleets, Lease manager.

**Scaling:** Shard by jobId; consistent hashing; calendar partitioning.

**Database:** Job definitions SQL; run history append; leases in DB/etcd.

**Caching:** Next-fire cache per shard.

**Messaging:** Job triggers to workers; DLQ.

**Security:** Tenant isolation; secret injection for job payloads.

**Tradeoffs:** At-least-once triggers vs duplicate side effects (require idempotent workers).

---

## Interview 18 — Feature Flag Service

**Prompt:** Design feature flags / experiments platform.

**Requirements:** Boolean/percentage flags, targeting, low latency evaluation, audit.

**Architecture:** Control plane UI/API, Config store, Edge evaluators/SDKs, Streaming updates.

**Scaling:** Push configs to SDK caches; avoid sync call per request.

**Database:** Flag defs + audit SQL; assignment logs optional sampling.

**Caching:** Local SDK memory; mid-tier Redis optional.

**Messaging:** Config change pub/sub.

**Security:** Who can change prod flags; approval workflows; kill switches.

**Tradeoffs:** Consistency of assignment vs performance; sticky bucketing.

---

## Interview 19 — Metrics / Observability Backend

**Prompt:** Design ingest and query for metrics at scale.

**Requirements:** High cardinality controls, rollups, query API, retention tiers.

**Architecture:** Agents → ingest gateway → write path → TSDB → query; downsample jobs.

**Scaling:** Shard by metric key; reject explosive cardinality.

**Database:** TSDB; metadata index; object storage cold.

**Caching:** Query results short; metadata.

**Messaging:** Optional Kafka buffer for bursts.

**Security:** Multi-tenant isolation; PII in labels banned.

**Tradeoffs:** Cardinality freedom vs cost; precision vs downsample.

---

## Interview 20 — Document Collaboration (Lite Google Docs)

**Prompt:** Design collaborative document editing.

**Requirements:** Concurrent edits, presence, history, sharing ACLs.

**Architecture:** WS gateway, OT/CRDT service, Doc store, ACL, History.

**Scaling:** Doc affinity (sticky sessions) or CRDT merge; shard by docId.

**Database:** Snapshots + op log; S3 for blobs.

**Caching:** Active docs in memory on owner node.

**Messaging:** Fanout ops to subscribers.

**Security:** ACLs; link sharing risks; encryption.

**Tradeoffs:** OT complexity vs CRDT size; strong ordering vs availability.

---

## Interview 21 — IoT Telemetry Ingest

**Prompt:** Design ingest for millions of devices sending telemetry.

**Requirements:** Auth devices, ingest, validate, alert rules, dashboards.

**Architecture:** MQTT/HTTP edge, Ingest, Stream processing, Rules, Storage hot/cold.

**Scaling:** Partition by deviceId; backpressure; edge aggregation.

**Database:** Hot TSDB; cold object storage; device registry SQL.

**Caching:** Device credentials/session; rule defs.

**Messaging:** Kafka/Pulsar core bus.

**Security:** Device certs; rotate keys; anomaly detection.

**Tradeoffs:** Edge filtering vs cloud cost; exactly-once analytics vs latency.

---

## Interview 22 — Fraud Detection Pipeline

**Prompt:** Design near-real-time fraud scoring for payments.

**Requirements:** Score txns <100ms path or async review; rules + ML; case management.

**Architecture:** Event ingest, Feature service, Rules engine, Model service, Case workflow, Feedback loop.

**Scaling:** Feature cache; model replicas; async enrichment for non-critical.

**Database:** Features online store; cases SQL; training data lake.

**Caching:** Hot features; denylists.

**Messaging:** Payment events; decision events; analyst actions.

**Security:** Model/PII access; adversarial fraud; audit decisions.

**Tradeoffs:** False positive customer pain vs fraud loss; sync vs async block.

---

## Interview 23 — Multi-Tenant SaaS Analytics

**Prompt:** Design product analytics for B2B SaaS (events → dashboards).

**Requirements:** Event ingest SDKs, tenant isolation, query latency, retention.

**Architecture:** Ingest, Enrichment, Warehouse/OLAP, Query API, Dashboard BFF.

**Scaling:** Per-tenant quotas; burst buffers; materialized views.

**Database:** Events in OLAP (ClickHouse/BigQuery-style); tenant config SQL.

**Caching:** Dashboard query cache; schema registry.

**Messaging:** Ingest bus; delayed batch loads acceptable.

**Security:** Tenant isolation critical; GDPR deletion pipelines.

**Tradeoffs:** Real-time dashboards vs cost; schema-on-read vs write.

---

## Interview 24 — KYC / Identity Verification

**Prompt:** Design identity verification workflow for fintech onboarding.

**Requirements:** Document upload, vendor checks, manual review, status machine, audit.

**Architecture:** Onboarding orchestration, Doc store, Vendor adapters, Review queue, Case store.

**Scaling:** Async vendor calls; queue spikes; OCR workers.

**Database:** Case state SQL; documents encrypted object store; immutable audit.

**Caching:** Vendor tokens; country rule configs.

**Messaging:** StatusChanged; reviewer tasks; webhooks to clients.

**Security:** Extremely sensitive PII; encryption; access break-glass; retention policies.

**Tradeoffs:** Auto-approve conversion vs compliance risk; vendor lock-in.

---

## Interview 25 — Logistics Tracking

**Prompt:** Design package tracking across carriers.

**Requirements:** Ingest carrier events, normalize, customer tracking page, ETAs, exceptions.

**Architecture:** Carrier webhooks/EDI ingest, Normalizer, Shipment aggregate, ETA, Notification, Public tracking API.

**Scaling:** Partition by trackingId; webhook bursts.

**Database:** Shipment timeline SQL/event store; geo points TS.

**Caching:** Public tracking pages CDN with short TTL.

**Messaging:** Internal shipment events; customer notifications.

**Security:** Unlisted tracking tokens; PII redaction on public pages.

**Tradeoffs:** Carrier data quality vs ETA accuracy; polling vs webhooks.

---

## Interview 26 — Ad Click Aggregator

**Prompt:** Design click/impression ingestion and billing aggregates.

**Requirements:** Extremely high write QPS, dedupe, near-real-time counters, anti-fraud.

**Architecture:** Edge collect → Kafka → stream agg → OLAP; billing export.

**Scaling:** Partition by campaignId; probabilistic structures optional.

**Database:** Raw cold storage; aggregates OLAP; billing relational.

**Caching:** Campaign config; bloom filters for dedupe windows.

**Messaging:** Central log bus; exactly-once not assumed — idempotent agg keys.

**Security:** Bot filtration; partner auth; audit for money.

**Tradeoffs:** Exact counts vs latency/cost; late events handling.

---

## Interview 27 — Healthcare Appointment + Records Lite

**Prompt:** Design appointment scheduling with access to visit summaries (HIPAA-like mindset).

**Requirements:** Booking, clinician calendars, summary docs, consent, audit every access.

**Architecture:** Scheduling, EHR adapter, Doc service, Consent, Audit, Notification.

**Scaling:** Clinic partitions; quieter than social but stricter correctness.

**Database:** Appointments SQL; documents encrypted; audit append-only.

**Caching:** Limited; prefer authz correctness over aggressive cache.

**Messaging:** Appointment reminders; audit async ship to SIEM.

**Security:** Least privilege; break-glass; encryption; BAAs with vendors.

**Tradeoffs:** Convenience features vs compliance burden.

---

## Interview 28 — Stock Trading Lite (Order Matching Intro)

**Prompt:** Design placing and matching equity orders for a retail broker (simplified).

**Requirements:** Place/cancel orders, matching, positions, market data, audit.

**Architecture:** Order gateway, Risk checks, Matching engine (or exchange adapter), Positions/ledger, Market data.

**Scaling:** Symbol-based sharding for matching; gateway horizontally scaled.

**Database:** Orders/trades durable log + DB; positions strict TX.

**Caching:** Market data; buying power projections with care.

**Messaging:** Trade prints; user fills notifications.

**Security:** Auth hard; kill switches; market manipulation detection basics.

**Tradeoffs:** Build matcher vs route to exchange; latency vs fairness.

**Note:** Emphasize state machines and durability over flashy design.

---

## Interview 29 — CI Artifact + Build Cache Platform

**Prompt:** Design internal platform for build artifacts and remote cache.

**Requirements:** Upload/download artifacts, authn for CI jobs, retention, dedupe by hash, high throughput.

**Architecture:** API, Content-addressable storage, Metadata index, GC/retention worker, CDN/internal edge.

**Scaling:** Hash-partitioned storage; parallel chunk upload.

**Database:** Metadata SQL; blobs object store.

**Caching:** Hot artifacts on edge nodes.

**Messaging:** GC events; vulnerability scan triggers.

**Security:** Signed URLs; tenant/repo isolation; SBOM optional.

**Tradeoffs:** Dedup savings vs GC complexity; immutability vs delete GDPR.

---

## Interview 30 — Government Citizen Services Portal

**Prompt:** Design a citizen portal for applications (licenses/permits) with casework.

**Requirements:** Apply, upload docs, pay fees, caseworker review, status, audit, accessibility, multi-language.

**Architecture:** Portal BFF, Application service, Workflow/case engine, Payments adapter, Doc store, Notify, Audit.

**Scaling:** Predictable peaks (deadlines); queue casework; autoscale web.

**Database:** Cases SQL; documents encrypted; workflow history event-sourced optional.

**Caching:** Reference data (locations, form schemas); not case decisions.

**Messaging:** Status updates; payment settlement; SLA timers for case aging.

**Security:** Strong identity (national ID/IdP); step-up auth; corruption-resistant audit; least privilege for officers.

**Tradeoffs:** UX speed vs verification rigor; monolith modular vs microservices for agencies.

**Lead/Architect:** Emphasize procurement constraints, accessibility, long retention, zero-trust.

---

## Cross-Cutting Deep-Dive Prompts (use after any design)

1. Draw the sequence for a retry after client timeout.
2. What is your consistency model for the hottest write?
3. How do you rotate secrets with zero downtime?
4. What dashboards page the on-call?
5. How does multi-region active-passive failover work?
6. Estimate cost drivers for year 1.

---

## Senior vs Lead Expectations

| | Senior | Lead / Architect |
|--|--------|------------------|
| Scope | Solid single-region design | Org constraints, evolution, standards |
| Data | Correct primary store | Lifecycle, compliance, ownership |
| Failure | Mentions retries | Chaos, budgets, operable runbooks |
| Comms | Clear diagram | Facilitates interviewer tradeoff choices |

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Requirements | Skipped | Partial NFRs | Explicit assumptions + NFRs |
| Architecture | Soup of buzzwords | Coherent | Clean boundaries |
| Data & consistency | Vague | Mostly right | Precise for domain |
| Scale & reliability | Handwavy | Plausible | Bottlenecks + mitigations |
| Security | Omitted | Basic auth | Threat-aware |
| Communication | Monologue | Structured | Collaborative |

---

## Confidence Checklist

- [ ] Timed 45-min design completed for ≥10 scenarios above
- [ ] Numbers practiced (QPS, storage, bandwidth)
- [ ] Money/inventory scenarios use strong consistency language
- [ ] Always mention idempotency + observability
- [ ] Can cut scope gracefully when time runs out

---

## Notes

<!-- Record which scenarios you drilled; weak deep-dives (caching, geo, etc.) -->
