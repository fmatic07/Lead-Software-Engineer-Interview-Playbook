# API Architecture

> APIs are long-lived contracts under versioning pressure — design for compatibility, failure, and multi-consumer ownership, not for yesterday’s UI screen.

---

## Purpose

Build Lead/Architect fluency in API style selection and evolution: REST, GraphQL, gRPC, versioning, contract-first delivery, idempotency, pagination, HATEOAS, and OpenAPI — framed for Spring MVC / WebFlux, SpringDoc, API gateways, and enterprise Java consumers.

---

## Topics Covered

- [ ] REST Design
- [ ] GraphQL
- [ ] gRPC
- [ ] API Versioning
- [ ] Contract First Development
- [ ] Idempotency
- [ ] Pagination
- [ ] HATEOAS
- [ ] OpenAPI

---

## REST Design

### Production framing

Enterprise REST is **resource-oriented HTTP** with stable nouns, meaningful status codes, and explicit error contracts — not academic purity. Model consistency boundaries as resources (`Account`, `PaymentIntent`, `Shipment`). Use standard methods for semantics proxies and caches understand; use **controller actions** (`POST .../capture`) when a transition is a privileged workflow, not a field patch.

Spring MVC remains the default for blocking JDBC/JPA estates; WebFlux fits high-concurrency gateways and streaming when the stack is non-blocking end-to-end (do not wrap blocking JPA in WebFlux without isolation).

### Production use cases

- Public partner APIs with strict change control and OpenAPI as the legal-ish contract.
- Internal domain APIs consumed by BFFs (React/Next.js) and other services.
- Webhook callbacks with signed payloads and idempotent receivers.

### Practices that survive review

- Error envelope with stable `code`, human `message`, optional `details`, `correlationId` (RFC 7807 problem+json or house standard).
- Don’t return `200` for business failure.
- Coarse resources over chatty entity CRUD that forces client-side joins.
- Authn at gateway; authz in service with resource-level checks.
- Cache thoughtfully: `ETag`/`If-None-Match` for GETs that matter; avoid caching authenticated personalized data at shared layers without vary rules.

---

## GraphQL

### Production framing

GraphQL gives clients **query-shaped responses** over a typed schema. It shines for mobile/web BFFs with divergent projections. It shifts cost to schema governance, field-level authz, query cost limits, and caching (HTTP cache-unfriendly by default).

### Production use cases

- Mobile app needing nested account + cards + last transactions in one round-trip.
- Federation gateway over existing REST/gRPC microservices.
- Partner portal with rapidly changing UI without new REST endpoints per screen.

### Failure and abuse semantics

- Without depth/complexity limits and persisted queries, GraphQL is a DoS vector.
- N+1 resolvers without DataLoader patterns destroy DB pools.
- Mutations need the same idempotency keys as REST.
- Subscriptions: connection fan-out, auth refresh, backpressure — often overkill vs SSE/polling.

Prefer GraphQL as a **BFF/edge graph**, not as the inter-service mesh language (use gRPC/REST interiors).

---

## gRPC

### Production framing

gRPC is **HTTP/2 + Protobuf** with codegen stubs, streaming, and strong contracts. Excellent service-to-service interior protocol in Kubernetes: low latency, compact payloads, deadline propagation. Awkward for browsers without grpc-web; awkward for ad-hoc partner integration that expects curl-able JSON.

### Production use cases

- Internal Order → Pricing → Inventory calls with strict SLOs.
- Streaming: market data, log ship, large result cursors.
- Polyglot microservices sharing `.proto` contracts.

### Semantics

- Deadlines/timeouts are first-class; always set them.
- Retries: only on idempotent methods; use `Idempotency-Key` metadata or business keys for side-effecting RPCs.
- Load balancing: client-side or service mesh (endpoint list changes); understand retry + hedged request amplification.
- Error model: rich status codes + structured trailers; map carefully at the REST edge.

---

## API Versioning

### Production framing

Versioning is a **compatibility policy**, not a URL fashion choice. Breaking changes need a strategy; additive changes should not.

### Common strategies

| Strategy | When it fits | Cost |
|----------|--------------|------|
| URI `/v1` | Public APIs, clear marketing of major breaks | Forever dual maintenance if not deprecated |
| Header `Accept-Version` / custom | Cleaner URLs | Harder to explore; gateway rules needed |
| Media type `application/vnd.company.order.v2+json` | Strict content negotiation | Client sophistication required |
| Additive evolution (no major bump) | Internal APIs with consumer contracts | Requires discipline and compatibility tests |

### Rules of thumb

- Additive: new optional fields, new endpoints — OK without major version.
- Breaking: rename/remove fields, change meaning, change status code semantics — need version or coordinated migrate.
- Prefer **expand/contract**: add new field → dual-write/read → remove old after consumers move.
- Sunset headers and gateway metrics on old versions; set kill dates.

---

## Contract First Development

### Production framing

**Contract-first** means the OpenAPI / AsyncAPI / Protobuf artifact is designed and reviewed *before* implementation drifts. Codegen produces server stubs and client SDKs; CI fails on incompatible diffs.

Opposite: code-first annotations that generate OpenAPI after the fact — fine for greenfield spikes, risky for multi-team public APIs.

### Production use cases

- Partner APIs with legal review of the YAML.
- Parallel frontend/backend work against mocked contracts.
- Consumer-driven contract tests (Pact) for internal REST.

SpringDoc OpenAPI 3 for Spring MVC/WebFlux documentation; spectral/openapi-diff in CI for compatibility gates. For gRPC, buf lint + breaking change detection.

---

## Idempotency

### Production framing

Idempotency means **retries do not create duplicate side effects**. HTTP: `GET/PUT/DELETE` are idempotent by definition if implemented correctly; `POST` is not — add `Idempotency-Key` (or equivalent) for payment-like POSTs.

### Production use cases

- Payment capture, fund transfer, order submit under mobile retries and gateway timeouts.
- Webhook receivers processing duplicate deliveries.
- Kafka consumers writing to DB (inbox pattern).

### Implementation sketch

1. Client sends `Idempotency-Key: <uuid>` (scoped per API key/tenant).
2. Server stores key → response hash / resource id with TTL (Redis) or unique constraint (Postgres).
3. Replay returns the **original** status and body (or `409` if concurrent in-flight — document it).
4. Keys expire after business-safe window (24h–7d typical).

Do not key only on body hash if legitimate duplicate business requests can share payloads.

---

## Pagination

### Production framing

Unbounded list endpoints are production incidents waiting for a large tenant. Choose pagination that matches access patterns and stability under inserts.

| Style | Pros | Cons |
|-------|------|------|
| Offset/limit | Simple, jump-to-page | Expensive deep pages; drift under inserts |
| Cursor/keyset | Stable, scalable | Opaque cursors; no arbitrary page jump |
| Page tokens (encrypted cursor) | Hides schema | Token versioning needed |

### Production guidance

- Default page size + hard max; reject huge `size`.
- Prefer **keyset** on indexed monotonic keys (`(created_at, id)`).
- Return `nextCursor` / Link headers; never rely on client inventing offsets into hot tables.
- Filter/sort fields must be indexed or rejected — open sort is a DoS.
- GraphQL connections (Relay-style) follow the same cursor rules.

---

## HATEOAS

### Production framing

HATEOAS embeds hypermedia controls (links/actions) in responses so clients discover next steps. Rarely central in enterprise microservice estates — most clients are codegen’d against OpenAPI and ignore links. Still useful for **workflow resources** (onboarding, approvals) and some public APIs.

### When to use

- Long-lived resources with state-dependent allowed actions (`approve`, `cancel` links only when permitted).
- Reducing client hardcoding of state machines (partially).

### When to skip

- Internal service mesh APIs.
- High-performance list endpoints where payload bloat hurts.
- Teams that will not build hypermedia clients — theater.

If used: standardize link relation names; never replace authz with “link absence.”

---

## OpenAPI

### Production framing

OpenAPI is the **reviewable surface** for REST: paths, schemas, security, examples, error models. SpringDoc generates from code or serves a hand-maintained spec. Treat the artifact as owned product surface: lint, diff, publish to a portal, generate clients.

### Production use cases

- Developer portal for partners.
- Gateway validation (request schema at edge — carefully; can be brittle).
- Contract tests and mock servers in CI.

### Governance

- Required fields: `operationId`, error schema ref, security scheme, pagination parameters.
- Examples for happy path + one business failure.
- Ban anonymous inline schemas for shared domain objects — `$ref` models.
- Version the spec with the API; publish deprecations in description + `deprecated: true`.

---

## Style comparison (decision table)

| Concern | REST | GraphQL | gRPC |
|---------|------|---------|------|
| Public partners | Strong default | Sometimes (careful) | Rare |
| Browser/mobile flexibility | BFF or many endpoints | Strong | Via gateway |
| Service-to-service | Common | Weak fit | Strong |
| Caching | HTTP natural | Hard | App-level |
| Streaming | SSE/chunked | Subscriptions | First-class |
| Tooling in Java | Spring MVC/WebFlux + SpringDoc | Spring GraphQL | grpc-java |
| Evolution | URI/header versions | Schema + deprecation | Protobuf compatibility rules |

---

## Why this matters in production

APIs outlive the teams that wrote them. A casually broken field rename can strand a mobile app release for weeks. Missing idempotency turns payment retries into chargebacks. Unbounded pagination turns one whale tenant into a production SEV. Lead engineers are evaluated on whether they **design for evolution and failure**, not whether they can annotate a controller.

Interview panels listen for: compatibility policy, consumer communication, gateway vs service responsibilities, and honest tradeoffs among REST/GraphQL/gRPC — without religion.

---

## Engineering tradeoffs

| Decision | Gain | Pay |
|----------|------|-----|
| REST everywhere | Simple ops, HTTP ecology | Chatty mobile; awkward streaming |
| GraphQL at edge | Client velocity | Gateway complexity, abuse controls |
| gRPC interior | Perf, contracts | Poor browser story; mesh LB nuance |
| URI versioning | Clarity | N versions alive |
| Additive-only discipline | Less dual-run | Requires strong review culture |
| Contract-first | Parallel work, fewer surprises | Upfront design cost; codegen friction |
| Code-first OpenAPI | Speed | Spec drift; weak partner trust |
| Strict HATEOAS | Discoverable workflows | Client complexity few will adopt |
| Idempotency keys on all POSTs | Safe retries | Storage and semantics overhead |
| Offset pagination | Familiar UX | Deep-page pain at scale |

---

## Common anti-patterns

1. **RPC-over-POST** (`/doAction`) for everything — loses HTTP semantics and cache/gateway behavior.
2. **UI-shaped domain APIs** — every React screen gets a dedicated backend path in the core service.
3. **Breaking JSON silently** — field type/meaning change without version or coordination.
4. **OpenAPI fiction** — generated once, never updated; portal lies.
5. **`200 OK` with `{ success: false }`** — breaks monitoring and client branching.
6. **No idempotency on payment POSTs** — duplicate charges under retry.
7. **`SELECT` + offset into million-row tables** — p99 collapse.
8. **GraphQL without complexity limits** — expensive queries as DoS.
9. **gRPC across the public internet without a façade** — client and firewall pain.
10. **Version proliferation** without sunset — permanent maintenance tax.
11. **Authz only at gateway** — direct service calls bypass checks.
12. **HATEOAS cargo cult** on internal CRUD — noise, no benefit.

---

## Best practices

1. Write an **API design standard** (naming, errors, pagination, idempotency, deprecation).
2. Prefer **coarse resources** aligned to consistency boundaries; use BFF/GraphQL for UI aggregation.
3. Adopt **contract-first** for external and cross-domain APIs; enforce compatibility in CI.
4. Require **Idempotency-Key** on side-effecting POSTs that clients may retry.
5. Default to **cursor pagination** with capped page size.
6. Use **SpringDoc** + lint; publish specs to a portal; fail builds on breaking diffs.
7. Propagate **deadlines/timeouts** and correlation ids across REST and gRPC.
8. Put **rate limits and authn** at the API gateway; keep **authz** domain-aware in services.
9. Choose gRPC for interior hot paths; REST/JSON for human-debuggable edges.
10. Document **error codes** as a stable taxonomy — treat them as API surface.
11. Deprecate with metrics: no traffic → remove; never “hope.”
12. Test contracts with **consumer-driven tests** for critical pairs.

---

## Architecture review checklist

- [ ] API style justified (REST / GraphQL / gRPC) with consumers named
- [ ] Consistency boundary matches resource/aggregate design
- [ ] Error model standardized; business vs technical errors distinct
- [ ] Versioning/compatibility policy written; breaking changes gated in CI
- [ ] Idempotency strategy for all unsafe retries
- [ ] Pagination bounded; indexes support sort/filter
- [ ] OpenAPI/Protobuf owned, linted, published
- [ ] Authn/authz split clear (gateway vs service)
- [ ] Timeouts, size limits, rate limits defined
- [ ] PII/logging redaction for request/response logs
- [ ] Deprecation and sunset plan for prior versions
- [ ] BFF vs domain API boundary clear for frontend
- [ ] Observability: RED metrics per route, golden error codes, gateway 429s

---

## Interview Challenge

Design the API architecture for a retail banking mobile app:

- Mobile needs flexible dashboards (accounts, cards, recent txns).
- Core ledger services are Java/Spring with PostgreSQL.
- Partners need a stable payments API.
- Internal service calls are high-QPS (balances, fraud checks).
- React admin console needs operational screens.

Choose styles per edge, versioning, idempotency, and pagination. Defend against a panelist who says “just GraphQL everything.”

---

## Suggested Answer

**Public payments (partners):** REST `/v1`, contract-first OpenAPI, SpringDoc portal, `Idempotency-Key` on `POST /payments`, problem+json errors, cursor pagination on lists. URI versioning with 12-month deprecation. Gateway: mTLS or OAuth client-credentials, rate limits per `client_id`.

**Mobile:** GraphQL **BFF** (or tailored BFF REST) over domain services — persisted queries, complexity limits, field authz. Does not own ledger truth.

**Interior:** gRPC between Ledger, Fraud, Limits — Protobuf, deadlines, retries only on idempotent read RPCs; writes use business idempotency keys.

**Admin React:** BFF REST aggregated for ops screens; never expose raw gRPC to browser; RBAC stricter than mobile.

**Domain REST:** still exists for non-GraphQL consumers and webhook-style integrations; resources = `Account`, `Card`, `Transaction`, `PaymentIntent`.

**Reject “GraphQL everything”:** GraphQL as mesh multiplies N+1 and authz complexity across teams; partners and auditors want versioned REST; gRPC wins interior SLOs. GraphQL is an edge composition tool with a platform owner — not a substitute for domain boundaries.

---

## Architecture Reflection Questions

1. What was your last breaking API change, and how did consumers migrate?
2. Where do you place BFFs vs domain APIs in your current estate?
3. When has missing idempotency caused a production defect?
4. How do you enforce OpenAPI compatibility in CI today (or what would you add)?
5. What pagination style do you default to and why?
6. When would you still choose offset pagination?
7. How do you explain gRPC vs REST to a product owner who only knows JSON?

---

## Interview Confidence Checklist

- [ ] Can choose REST vs GraphQL vs gRPC with consumer and ops arguments
- [ ] Can describe a concrete versioning and deprecation policy
- [ ] Can implement/explain Idempotency-Key behavior under concurrent retries
- [ ] Can defend cursor pagination and its failure modes
- [ ] Can discuss contract-first vs code-first honestly
- [ ] Can place HATEOAS in the “sometimes useful” category without dogma
- [ ] Can describe Spring MVC vs WebFlux selection criteria
- [ ] Can outline gateway vs service responsibilities
- [ ] Can whiteboard a BFF + domain + interior gRPC layout
- [ ] Can list OpenAPI governance checks worth putting in CI

---

## Notes

<!-- Your API versioning, idempotency, and BFF decisions -->
