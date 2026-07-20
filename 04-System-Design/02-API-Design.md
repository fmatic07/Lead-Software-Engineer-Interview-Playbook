# API Design

> Contracts that survive versioning, failure, and multi-team ownership.

## REST

### Explanation

REST in enterprise practice means resource-oriented HTTP APIs with stable nouns, standard methods, and explicit representations—not cargo-cult “pure REST.” Resources expose identity (`/accounts/{id}`), collections, and carefully chosen actions when a state transition is not a natural property update (`POST /payments/{id}/capture`). Uniform interface, stateless requests, and cacheability remain the useful constraints; hypermedia is optional and rarely central in internal microservice estates.

Good REST design is contract design: status codes that clients can branch on, error bodies with stable `code` fields, idempotent methods where HTTP semantics promise it, and pagination/filtering that does not explode cardinality. Spring Boot services should treat OpenAPI as the review surface—generated docs without ownership rot into fiction.

Cross-service REST fails when APIs encode one consumer’s UI tree, leak internal tables, or use HTTP as a thin RPC dump (`POST /doSomething` with overloaded payloads). Prefer coarse-grained resources aligned to consistency boundaries, not chatty entity CRUD that forces distributed joins in the client.

### Why interviewers ask it

- Distinguishes resource modeling skill from framework annotation familiarity.
- Tests HTTP semantics literacy (idempotency, caching, status codes) under failure.
- Reveals whether APIs are designed for multiple consumers and longevity.

### Production examples

- Card payments: `PaymentIntent` resource with capture/cancel subresources; never expose acquirer-specific RPC shapes to partners.
- Telecom inventory: SIM/MSISDN as resources with state machines (`available → reserved → active`), not boolean flags updated ad hoc.
- Corporate banking: account collection with field-filtered projections for mobile vs back-office clients via sparse fieldsets or BFF—not N distinct micro-endpoints per screen.

### Common mistakes

- RPC-over-POST everywhere, ignoring method semantics and cache/proxy behavior.
- Returning 200 for business failures, forcing clients to parse prose.
- Exposing DB identifiers and join tables as the public model.
- Breaking changes without versioning policy; “silent” field meaning changes.

### Senior Engineer discussion

Model the domain’s consistency boundary as the resource. Document invariants and allowed transitions. Use problem+json or a house error envelope with machine-readable codes. Make list endpoints bounded; make writes idempotent where retries are expected. Validate the contract with consumer-driven tests, not only controller unit tests.

### Lead Engineer discussion

Publish API design standards: naming, error taxonomy, authz patterns, pagination defaults, and deprecation policy. Require design review for externally exposed and cross-domain APIs. Prefer platform OpenAPI linting and compatibility checks in CI so style is enforced mechanically. Push BFFs or GraphQL gateways when many clients would otherwise warp domain APIs into UI shapes.

### Tradeoffs

- Fine-grained resources increase flexibility and chattiness; coarse resources reduce round-trips and harden coupling inside payloads.
- Strict REST purity aids cache/intermediaries; pragmatic RPC actions speed delivery for complex workflows.
- Public resource models stabilize partners but slow internal refactors; exposing internals ships faster and couples forever.
- HATEOAS can discoverability; most enterprise clients ignore links and want stable documented routes.

### Interview Challenge

1. Design REST resources for “transfer money between accounts” including failure and retry.
2. When is `POST /accounts/{id}/activate` better than `PATCH` with `status=ACTIVE`?
3. How do you keep a REST API from becoming a per-screen backend for React/Next.js?

### Suggested Answer

1. `POST /transfers` with idempotency key creating a `Transfer` resource (`pending|completed|failed`); `GET /transfers/{id}` for status. Debit/credit are internal ledger postings, not separate public calls. On timeout, client reuses key; server returns the same transfer. Errors: `409` insufficient funds (business), `503` with retry for dependency outage.
2. When activation is a privileged transition with side effects (provisioning, audit, multi-step workflow) that is not a generic field edit. Dedicated action clarifies authz, metrics, and idempotency; PATCH remains for true partial updates of mutable attributes.
3. Keep domain APIs coarse and stable; introduce a BFF or aggregation layer for UI-specific shapes. Ban UI field names from core services. Contract-test domain APIs against multiple consumers, not only the web app.


## GraphQL

### Explanation

GraphQL exposes a typed schema where clients specify the shape of the response. It excels when many clients need divergent projections over a shared graph and round-trips are costly (mobile). It is not a free replacement for REST: it shifts complexity to schema design, authorization at field/resolver level, query cost control, and caching.

In microservice backends, a GraphQL gateway often federates or aggregates REST/gRPC services. Without query depth/complexity limits, persistable queries, and dataloader-style batching, GraphQL becomes an elegant denial-of-service and N+1 engine. Mutations still need idempotency and clear error models; subscriptions add connection fan-out and backpressure concerns.

Choose GraphQL when product velocity of client-driven queries outweighs gateway operational cost. Prefer REST/gRPC for service-to-service interiors unless you deliberately invest in a graph platform team.

### Why interviewers ask it

- Tests judgment: right tool vs resume-driven adoption.
- Probes authz, caching, and abuse-control awareness beyond schema syntax.
- Surfaces federation/ownership issues in multi-team graphs.

### Production examples

- Retail banking app: GraphQL BFF over account, card, and transaction services with persisted queries for release stability.
- Telecom care portal: agents query customer–subscription–ticket graphs; complexity limits prevent unbounded nested history fetches.
- Partner integrations still on versioned REST; internal admin UI on GraphQL—different consumer classes, different contracts.

### Common mistakes

- One giant mutable graph with no ownership or deprecation process.
- Resolver-per-field N+1 against SQL/HTTP without batching.
- Treating GraphQL as anonymously public without field-level authz.
- Allowing arbitrary queries in production without cost analysis or allowlists.

### Senior Engineer discussion

Design the schema around client use cases and domain boundaries, not table mirrors. Implement dataloaders, complexity scoring, timeouts, and persisted queries for mobile. Make mutations explicit and idempotent. Measure resolver latency and downstream fan-out like any distributed entry point.

### Lead Engineer discussion

If adopting federation, fund a graph platform: schema review, ownership per subgraph, breaking-change policy, and shared gateway SLOs. Do not let every team publish unconstrained types into a global graph. Decide where GraphQL stops—usually at the edge—so interior services keep simple RPC/REST contracts.

### Tradeoffs

- Flexible client queries vs expensive, hard-to-cache, hard-to-limit server work.
- Single endpoint simplicity vs opaque traffic patterns and weaker CDN cacheability than REST GETs.
- Federation enables team autonomy and increases distributed failure and schema coordination cost.
- Persisted queries improve safety/performance and reduce ad-hoc flexibility.

### Interview Challenge

1. REST vs GraphQL for a public partner payments API and an internal multi-screen admin UI—choose and justify.
2. How do you prevent a GraphQL query from fan-out hammering ten microservices?
3. What breaks if you put GraphQL between every microservice pair?

### Suggested Answer

1. Partner payments: versioned REST—stable, cacheable docs, clear SLAs, easy gateway policies. Internal admin UI: GraphQL BFF—varied screens, rapid iteration, controlled audience. Different consumers warrant different contracts.
2. Dataloaders/batching, complexity/depth limits, timeouts, persisted queries, and carefully designed fields that hide chatty internals behind aggregated resolvers. Monitor per-field cost; reject or rewrite pathological queries.
3. You multiply gateway hops, obscure network boundaries, complicate auth propagation, and lose simple operational semantics. Keep GraphQL at the edge; service-to-service should stay explicit, versioned RPC/REST/events.


## API Versioning

### Explanation

Versioning is how contracts evolve without stranding clients. Strategies include URI versioning (`/v1/...`), header versioning, and media-type versioning. Enterprises often pick URI versioning for visibility in logs and gateways despite purist objections. Compatibility matters more than the bike-shed: additive changes are safe; renames, type changes, and semantic shifts are breaking.

A versioning policy must define what “breaking” means, how long old versions live, and how dual-run works. Microservices that version every internal hop create combinatorial explosion; prefer tolerant readers, additive schemas, and expand/contract migrations. External partner APIs need explicit sunset headers, migration guides, and telemetry of version usage.

Spring Cloud / gateway layers should route versions deliberately—not by accidental path sprawl. Never silently change meaning of a field under the same version.

### Why interviewers ask it

- Reveals real multi-consumer experience vs greenfield-only design.
- Tests expand/contract thinking and deprecation discipline.
- Connects API change to organizational coordination cost.

### Production examples

- Open banking: `/v3/accounts` with 18-month deprecation of `/v2`, usage dashboards per TPP client id.
- Mobile apps: additive fields for 2–3 app releases before removing server support for old clients.
- Event payloads versioned separately from REST (`eventType` + `schemaVersion`) to allow independent evolution.

### Common mistakes

- Shipping breaking changes under the same version because “only one client.”
- Proliferating `/v1` `/v2` `/v3` for trivial renames instead of additive evolution.
- No usage metrics—cannot sunset safely.
- Versioning internal service meshes per deploy, creating diamond dependency hell.

### Senior Engineer discussion

Default to additive evolution. When breaking is unavoidable, ship vNext behind gateway, dual-write/adapt, and migrate clients with observability. Write adapters at the edge rather than forking domain logic forever. Document change logs that name semantic impacts, not only field lists.

### Lead Engineer discussion

Codify versioning and deprecation SLAs for public vs internal APIs. Require compatibility checks in CI (OpenAPI diff). Own the sunset process: partner comms, executive exceptions, and kill-switches. Discourage per-team incompatible dialects of “almost the same” resources.

### Tradeoffs

- URI versions are obvious and cache-friendly per path; they encourage permanent forks if overused.
- Header/media versions are cleaner URLs but harder to debug and proxy.
- Long support windows protect clients and increase maintenance/test matrix cost.
- Strict compatibility slows change; loose compatibility accelerates delivery and externalizes breakage.

### Interview Challenge

1. You must rename `amount` from string to object `{value, currency}`. How do you ship without stranding mobile clients?
2. When would you refuse to create `/v2` and demand additive change instead?
3. How do you sunset an API version used by 2% of traffic but a top enterprise customer?

### Suggested Answer

1. Expand: add `amountDetail` object while keeping `amount` string; deploy clients; then contract: deprecate string, eventually remove in next major with timeline. Or introduce `/v2` only if simultaneous incompatible changes warrant a clean break—still dual-support both.
2. When the change is cosmetic, when a single consumer can update quickly, or when aliases/additive fields solve it. Majors are for incompatible semantics, not preference.
3. Use metrics to identify the customer, engage with a dated migration plan and sandbox, offer an adapter if strategically required, and only force-kill with executive alignment. Sunset is account management plus engineering.


## Idempotency

### Explanation

Idempotency means repeating the same logical request produces the same effect without unintended duplicates. HTTP `GET`/`PUT`/`DELETE` are idempotent by spec; `POST` is not—yet payment captures, provisioning, and submissions are commonly `POST` and retried by clients, gateways, and messengers. Production systems therefore implement idempotency keys: client-supplied tokens that map to a stored request fingerprint and result.

At-least-once delivery is the default in distributed systems. Without idempotency, timeouts create double charges, double SIMs, or duplicate tickets. Keys must be scoped (merchant+key), TTL’d or persisted per retention rules, and bound to request payload hashes to prevent key reuse with different bodies.

Spring services should enforce idempotency in the domain transaction: record key + outcome atomically with the side effect (or via outbox). Returning the original response on replay is part of the contract, including original status codes where feasible.

### Why interviewers ask it

- Separates theorists from people who have fixed duplicate money/inventory incidents.
- Tests storage design for keys, concurrency, and fingerprinting.
- Links transport retries to business invariants.

### Production examples

- Card capture: `Idempotency-Key` header; Redis/DB unique constraint prevents double capture under client retry storms.
- Disbursement APIs: bank partners require end-to-end idempotency across their timeouts and yours.
- Kafka consumers: dedupe by `eventId` or business natural key before ledger append.

### Common mistakes

- Keys checked in memory only—lost on restart/scale-out.
- Accepting the same key with a different body (security and consistency bug).
- TTL shorter than max client retry window.
- Applying idempotency only at the API edge while async workers still double-apply.

### Senior Engineer discussion

Define key scope, retention, and conflict behavior (`409` on payload mismatch). Make handlers safe under concurrent replays—unique constraints beat check-then-act. Propagate correlation ids separately from idempotency keys. Include replay metrics: hits, conflicts, expired keys.

### Lead Engineer discussion

Mandate idempotency for all money-moving and provisioning POSTs in API standards. Provide a platform library (filter + store) so teams do not invent incompatible schemes. Align gateway retries with service guarantees—retries without keys are organizational defects, not just client bugs.

### Tradeoffs

- Durable idempotency stores add latency and ops cost; in-memory stores are cheap and wrong under scale.
- Long key retention improves safety and grows storage; short retention risks duplicates after delayed retries.
- Strict payload fingerprinting prevents misuse and blocks legitimate “same key, fixed typo” retries—usually desirable.
- Exactly-once end-to-end is aspirational; idempotent effects + dedupe are the practical standard.

### Interview Challenge

1. Client POSTs payment, times out, retries with the same idempotency key. Detail server behavior under concurrency.
2. Should GET be idempotent in your business layer even if HTTP says so?
3. How do idempotency keys interact with Kafka at-least-once consumers?

### Suggested Answer

1. Begin transaction / use unique insert on (scope, key). First writer performs charge and stores response; concurrent writer hits conflict and waits or loads the stored result. Return identical representation. If payload hash differs → `409`. Never charge twice.
2. HTTP GET must be safe/idempotent; still avoid GET handlers that trigger non-idempotent side effects (send email, mutate). Use POST for unsafe operations.
3. Producer should use keys; consumer must also dedupe by business id because dual delivery can occur downstream of the API. Idempotency at HTTP does not replace consumer inbox/dedupe tables.


## Pagination

### Explanation

Pagination bounds list responses so clients and servers remain stable under growth. Offset/limit is simple and breaks under deep pages (slow `OFFSET`, drifting results when inserts occur). Cursor/keyset pagination using a stable sort key (`(created_at, id)`) is the production default for large, append-heavy datasets. Page-size caps are mandatory; “return all” is an incident waiting for a large tenant.

APIs should document sort stability, cursor opacity (encrypt/sign tokens), and whether results are snapshots or live. Filtering + pagination requires the same keyset discipline or you reintroduce scans. For financial ledgers, prefer cursors over offsets to avoid missing/duplicating entries during concurrent posting.

GraphQL connections (edges/nodes/cursors) follow the same underlying keyset principles; fancy relay shapes do not fix bad database access patterns.

### Why interviewers ask it

- Deep pages and large tenants are classic production footguns.
- Tests database index awareness alongside API design.
- Distinguishes toy CRUD from platform-scale list design.

### Production examples

- Transaction history: cursor on `(posted_at, txn_id)`, max page 100, secondary filters indexed.
- Audit logs: time-bounded queries with cursors; deny unbounded “all history” for a corporate tenant.
- Admin search: offset allowed only for small UI tables; export uses async jobs, not giant pages.

### Common mistakes

- Default page size huge or unspecified.
- Offset pagination on hot, frequently inserted tables.
- Total-count on every request (`COUNT(*)`) destroying DB CPU.
- Cursors that embed raw SQL offsets or leak internal ids insecurely without need.

### Senior Engineer discussion

Pick keyset keys that match the query’s ORDER BY and supporting indexes. Make cursors opaque and expireable if they carry filters. Separate “UI page” from “export”: exports go async. Avoid mandatory global totals; use `hasMore` or approximate counts when exact totals are expensive.

### Lead Engineer discussion

Standardize pagination envelopes and max page sizes in API guidelines. Lint OpenAPI for unbounded arrays. Educate teams that search and list are different products—search may use search engines with their own cursors. Ban silent removal of page caps for “just this partner.”

### Tradeoffs

- Offset is easy for jump-to-page UX; expensive and unstable at scale.
- Keyset is efficient and stable for next/prev; poor at random page numbers.
- Exact totals delight UI and tax the database; approximate or deferred totals scale better.
- Larger pages reduce chatter and increase memory/latency tails.

### Interview Challenge

1. Design pagination for an account ledger that receives hundreds of posts per second.
2. Product wants “page 9000 of results.” How do you respond?
3. How do you paginate while filtering by status without missing rows?

### Suggested Answer

1. Keyset on `(created_at, id)` with composite index; opaque cursor; max limit; no deep OFFSET. Optional `asOf` timestamp for stable snapshots during disputes.
2. Explain cost/instability; offer better filters, search, or async export. If jump-to-page is mandatory, constrain to smaller result sets or accept a dedicated expensive path with strict authz and rate limits—not the default list API.
3. Include filter in the query and cursor payload; index `(status, created_at, id)` or equivalent. Document that concurrent status changes can move rows between views—define semantics explicitly.


## Rate Limiting

### Explanation

Rate limiting protects shared capacity and enforces fair use. Algorithms include token bucket (bursts), leaky bucket (smooth egress), fixed/sliding windows (simple quotas), and concurrency limits (outstanding requests). Limits belong at multiple layers: edge gateway per API key/IP, service bulkheads per dependency, and tenant quotas for noisy-neighbor control.

Effective limiting returns clear signals (`429`, `Retry-After`) and is observable by key. Blind limiting without product tiers becomes a support nightmare; unlimited trusted clients become your outage. In fintech, rate limits also bound fraud and credential-stuffing blast radius when combined with auth anomaly detection.

Implementation choices: Redis counters for distributed limits, local limiters for coarse protection, mesh/gateway policies for consistency. Limits must survive retries—clients should back off; servers should not amplify.

### Why interviewers ask it

- Shows capacity and abuse thinking beyond feature APIs.
- Tests multi-tenant fairness and layered defense design.
- Connects client UX (backoff) to server protection.

### Production examples

- Open banking: per-TPP and per-customer quotas aligned to regulatory/performance commitments.
- Login/OTP endpoints: stricter limits than read APIs; progressive delays on suspicion.
- Internal batch clients: separate higher quotas with mutual TLS identity—not the public anon bucket.

### Common mistakes

- Single global limit that punishes all tenants for one abuser.
- No distinction between cheap GETs and expensive search/export.
- Returning opaque 503s without retry guidance.
- Rate limits only in-app while gateway still floods the service.

### Senior Engineer discussion

Define limit dimensions (key, route, tenant, user) and cost weights for expensive endpoints. Emit metrics and headers (`X-RateLimit-*`) where clients are sophisticated. Load-test the limiter itself; a hot Redis key can become the bottleneck. Fail policy under limiter outage must be explicit: fail open (availability) vs fail closed (safety)—choose per endpoint class.

### Lead Engineer discussion

Publish org-wide default quotas and tier catalogs. Enforce edge limiting as a platform capability; services add local bulkheads for dependencies. Align commercial tiers with technical limits so sales does not sell “unlimited.” Include rate-limit reviews in partner onboarding for high-volume integrations.

### Tradeoffs

- Tight limits protect the system and increase false positives for legitimate bursts.
- Distributed accurate counters add latency/Redis dependency; local limits are fast and approximate.
- Fail-open under limiter failure preserves availability and risks overload; fail-closed does the opposite.
- Per-route weights improve fairness and complicate configuration.

### Interview Challenge

1. Design rate limiting for a payments API used by both a mobile app and a large ERP batch.
2. What changes when the expensive endpoint is “search transactions”?
3. How should clients react to `429`, and how do you stop retry stampedes?

### Suggested Answer

1. Separate identities/quotas: user/app tokens with bursty token buckets; ERP client credentials with higher sustained limits and scheduled windows. Edge + service limits; never share one bucket across both.
2. Cost-based limiting (higher weight), stricter concurrency caps, mandatory pagination, and possibly async search for heavy queries. Protect DB with its own bulkhead.
3. Honor `Retry-After`, exponential backoff with jitter, idempotency on mutating retries. Server-side: jittered admission, shed load, and avoid synchronized client retry intervals documented as fixed sleeps.


## Authentication

### Explanation

Authentication establishes identity of caller (user, service, partner). Common patterns: OAuth 2.0 / OIDC for user-delegated access, mTLS for service identity, API keys for simple partner ingress (preferably as a pointer to richer credentials). JWTs enable stateless validation at the edge but require key rotation, short TTL, audience/issuer checks, and a revocation story for compromised tokens.

Never confuse “gateway validated TLS” with application authentication. Terminate and verify at the edge, still propagate a signed identity context to services—services must not trust spoofable headers alone in zero-trust models. For Spring Security, prefer standard resource-server validation over hand-rolled JWT parsing.

Threats to design for: token replay, redirect URI abuse, key leakage in logs, confused deputy at gateways, and long-lived PATs. In regulated environments, strong customer authentication (SCA) and step-up auth attach to high-risk operations, not only to login.

### Why interviewers ask it

- Security design is mandatory for lead-level API ownership.
- Tests OAuth/OIDC literacy and service-to-service identity patterns.
- Reveals unsafe header-trust and DIY crypto habits.

### Production examples

- Mobile banking: OIDC auth code + PKCE; short-lived access tokens; refresh rotation; step-up for transfers.
- Service mesh: mTLS between services; JWT for user identity on the request path (`Authorization` + hardened internal principal).
- Partners: OAuth client credentials or mutually authenticated API gateway apps—not shared static keys in email.

### Common mistakes

- Accepting unsigned/alg=none JWTs or skipping `aud`/`iss` validation.
- Putting long-lived tokens in localStorage without threat modeling; logging Authorization headers.
- API keys without rotation, scoped permissions, or per-environment isolation.
- Homegrown encryption of credentials instead of standard protocols.

### Senior Engineer discussion

Choose protocol per client type; implement validation with vetted libraries. Bind tokens to audience and minimal scopes. Plan key rotation and revocation (blocklist, short TTL, introspection for sensitive paths). Ensure auth failures are uniform enough to avoid user enumeration on public endpoints where required.

### Lead Engineer discussion

Centralize identity with a platform IdP and gateway policies; forbid per-service login inventions. Define service identity standards (mTLS/SPIFFE). Run security reviews for new external auth flows. Coordinate with compliance on session lifetime, MFA, and audit of auth events.

### Tradeoffs

- Stateless JWT scales validation and complicates instant revocation.
- Introspection centralizes control and adds latency/availability coupling to the IdP.
- mTLS strongly identifies services and increases cert-ops burden.
- Long sessions improve UX and enlarge stolen-token windows; short sessions reverse that.

### Interview Challenge

1. Design auth for a public partner API and an internal microservice call chain initiating from a user click.
2. How do you rotate JWT signing keys without downtime?
3. Why is “trust `X-User-Id` from the gateway” dangerous, and how do you harden it?

### Suggested Answer

1. Partner: OAuth2 client credentials (or mTLS+gateway app) with scoped tokens. User click: user OIDC access token at edge; propagate user identity via signed internal token/JWT with mTLS service identity between hops—both user principal and service principal matter.
2. Support multiple `kid`s: publish new key, sign new tokens with new key, accept old and new during overlap, then retire old after max token TTL. Automate JWKS distribution.
3. Any compromised internal hop can spoof headers. Use signatures, mesh identity, and gateway-issued sealed context; services validate signature/issuer, not plain headers.


## Authorization

### Explanation

Authorization decides what an authenticated principal may do. Models include RBAC (roles), ABAC (attributes/context), ReBAC/relationship-based (Google Zanzibar-style), and policy-as-code (OPA/Cedar). Object-level and field-level checks matter: “authenticated” is not “may read this account.” In multi-tenant systems, tenant isolation is an authorization invariant, not a filter suggestion.

Central PDP (policy decision point) vs embedded checks trade consistency against latency. Regardless, enforce on the server—never only in UI. For APIs, map scopes/permissions to operations and resources; audit denials and privileged allows. Spring Security method security and custom voters help, but domain rules (account ownership, dual control) often live in application services with explicit policy tests.

Broken access control remains a top vulnerability class: IDOR on `/accounts/{id}`, missing tenant checks, and over-broad service accounts. Lead engineers treat authz as a product-wide architecture concern.

### Why interviewers ask it

- IDOR and tenant leaks are interview and production classics.
- Tests ability to choose RBAC vs richer models for the domain.
- Distinguishes UI hiding from real enforcement.

### Production examples

- Banking: user may see own accounts; advisor roles with break-glass and full audit; maker-checker on limit changes.
- Telecom CSR: attribute-based access by region/skill; mask PANs/MSISDNs by clearance.
- SaaS: org-admin vs member; resource ACLs on workspaces; service accounts scoped to single tenant.

### Common mistakes

- Checking authn only; trusting client-supplied tenant ids.
- Coarse roles (`ADMIN`) that accumulate god powers.
- Authorization only at gateway path level without object checks.
- Inconsistent rules across REST, GraphQL resolvers, and consumers.

### Senior Engineer discussion

Enumerate resources and actions; enforce on every read/write path including batch and admin tools. Prefer deny-by-default. Add tests for cross-tenant IDOR. For GraphQL, field/resolver authz must match REST policies—single policy source helps. Log decision-relevant attributes for forensics without leaking secrets.

### Lead Engineer discussion

Select an org authz strategy and shared libraries/services so each team does not invent incompatible role matrices. Require threat modeling for new resource types. Align SoD (segregation of duties) with compliance for fintech. Invest in continuous detection of over-privileged service identities.

### Tradeoffs

- Central policy engines improve consistency and add latency/dependency risk.
- Fine-grained policies reduce blast radius and increase policy complexity/ops load.
- Fat roles ship faster and become toxic privilege piles.
- Caching authz decisions speeds reads and risks stale permits after revocation—TTL carefully.

### Interview Challenge

1. API is `GET /accounts/{accountId}`. User is authenticated. What authz checks are mandatory?
2. Compare RBAC vs ABAC for a multi-region support desk.
3. How do you authorize a service account that must process webhooks for all tenants without becoming god-mode?

### Suggested Answer

1. Authenticated identity must be permitted on that `accountId` (ownership, delegation, or staff role with audit). Enforce tenant binding; never authorize solely by knowledge of the UUID. Return `404` vs `403` per enumeration policy.
2. RBAC alone (`CSR`) fails regional/data-class constraints. ABAC/ReBAC: role + region + customer segment + sensitivity. Use RBAC for coarse entry, attributes/relationships for row-level control.
3. Narrow by capability: per-event signature verification, queue isolation, or impersonation tokens minted per tenant/event. Avoid a single credential with `*/*` on production data; split workers and use just-in-time scoped credentials where possible.
