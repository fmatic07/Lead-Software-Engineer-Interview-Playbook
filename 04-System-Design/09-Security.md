# Security

> Threat-aware design for APIs, data, identity, and abuse resistance.

## OWASP

### Explanation

OWASP Top 10 catalogs common web/API risk classes: broken access control, cryptographic failures, injection, insecure design, misconfiguration, vulnerable components, authn failures, integrity failures, logging/monitoring failures, SSRF. For Spring APIs, prioritize authorization bugs (IDOR/BOLA), injection at ORM/native boundaries, mass assignment, exposed actuators, and dependency CVEs.

Scanners find known bad patterns; they do not replace threat modeling. “We use Spring Security” is not proof that every object reference is authorized. Security logging and metrics for authz denials are part of the control—not optional telemetry.

### Why interviewers ask it

- Baseline security literacy for anyone shipping APIs.
- Tests practical mapping to Spring/Java defenses.
- Separates checklist recitation from threat-driven design.

### Production examples

- IDOR: `/orders/{id}` returns another user’s order—authn without object authz.
- MyBatis `${}` interpolation → SQL injection; fixed with `#{}` binds.
- Actuator `/env` exposed → secret leakage.

### Common mistakes

- Equating Spring Security setup with correct access control.
- Ignoring SSRF when fetching user-supplied URLs.
- No dependency scanning in CI.
- No security logging for authz failures.

### Senior Engineer discussion

Threat model each endpoint: actor, asset, abuse case. Enforce authorization on every object reference. Centralize validation; prefer parameterized queries. Disable unused actuators; network-restrict admin. Track CVEs with severity SLAs. Add automated IDOR tests for multi-tenant resources.

### Lead Engineer discussion

Make OWASP-informed reviews part of design/PR gates for Tier-1. Fund AppSec partnership and secure defaults in service templates. Require authz matrix tests in Definition of Done. Report residual risk in business language—not only CVE counts.

### Tradeoffs

- Heavy preventive controls: safer defaults, slower delivery—mitigate with paved-road frameworks.
- Scanner-centric programs: broad coverage, shallow on logic bugs—pair with manual threat modeling.
- Denylist validation: incomplete; prefer allowlists and typed inputs.
- Verbose security logs: better forensics, PII/cost risk.

### Interview Challenge

1. Prevent IDOR in a multi-tenant Spring order API.
2. How do you prioritize a Critical CVE in a transitive dependency?

### Suggested Answer

1. Authenticate, then authorize by tenant/ownership on every read/write. Scope queries by principal; never trust client tenant headers alone. Integration tests for cross-tenant access; metric/log authz denials. Opaque IDs help but are not a control.
2. Assess reachability/exploitability; patch or upgrade; temporary WAF/network mitigate if needed; waive only with documented risk owner and expiry. Track fleet remediation SLA; don’t forever “accept risk” on internet-facing Tier-1.

## JWT

### Explanation

JWTs are compact claims (usually JWS). Access tokens should be short-lived; refresh tokens need stricter storage and rotation. Validate `iss`, `aud`, `exp`, signature, and algorithm allowlist—reject `none` and unexpected algs. Prefer asymmetric keys (RS256/ES256) so resource servers need JWKS, not shared secrets. JWTs are not encrypted by default—use JWE if claim confidentiality matters.

Revocation is the hard part. Short TTL + refresh rotation covers many cases; urgent revoke needs `jti` blocklist, introspection, or versioned session claims. Oversized permission blobs in JWTs couple services and enlarge leak blast radius.

### Why interviewers ask it

- Ubiquitous in Spring API interviews.
- Probes validation pitfalls and session-vs-token tradeoffs.
- Reveals dangerous long-lived localStorage JWT designs.

### Production examples

- Resource server with JWKS rotation via `kid`.
- Fat JWTs cause header bloat; fine-grained authz moved server-side.
- Logout doesn’t revoke JWT; attacker uses token until expiry—mitigated with short TTL + refresh revoke.

### Common mistakes

- Accepting tokens without `aud` (token confusion).
- Sensitive PII in unencrypted claims.
- Long-lived access tokens as the only session.
- Symmetric secret shared across many services forever.

### Senior Engineer discussion

Treat access tokens as bearer credentials: TLS only, minimal claims, short TTL. Bound clock skew. For browser apps, prefer BFF/httpOnly cookie patterns when XSS risk dominates. Propagate identity downstream via sealed tokens or mTLS + explicit principal design—not forgeable headers.

### Lead Engineer discussion

Standardize token formats, TTLs, and JWKS management. Own key rotation runbooks. Decide revocation strategy for compliance (banks may require introspection/reference tokens). Teach that JWT is a format, not an architecture.

### Tradeoffs

- Stateless JWT: scalable validation, weak instant revoke.
- Opaque/reference tokens: easy revoke, introspection latency and IdP dependency.
- Fat claims: fewer lookups, larger leak blast radius and coupling.
- JWE: confidentiality, more crypto ops and key distribution.

### Interview Challenge

1. Fired user must lose access within 1 minute; access JWT TTL is 15 minutes. Options?
2. How do you rotate signing keys without downtime?

### Suggested Answer

1. Shorten access TTL; revoke refresh at IdP; for hard SLA use `jti` revocation checked at gateway/resource servers, introspection on sensitive APIs, or bump session version claim on HR event. Measure introspection latency/cache carefully.
2. Publish new JWKS key; sign new tokens with new `kid` while still accepting old until expiry window ends; then retire. Overlap is mandatory; monitor `kid` acceptance rates.

## OAuth2

### Explanation

OAuth2 delegates authorization; OIDC adds identity. Prefer Authorization Code + PKCE for public clients; client credentials for service-to-service; avoid implicit and password grants in new designs. Spring Authorization Server or enterprise IdPs (Okta, Cognito, Keycloak) issue tokens; resource servers validate. Scopes are coarse capabilities—fine-grained authz still lives in the API.

Audience restriction prevents token reuse across APIs. Service meshes and zero-trust do not remove the need for least-privilege tokens; they complement them. Redirect URI allowlists and client authentication method choice (secret vs private_key_jwt vs mTLS) define partner threat models.

### Why interviewers ask it

- Standard enterprise identity integration topic.
- Distinguishes flows and threat models per client type.
- Tests least-privilege scope design.

### Production examples

- Mobile: auth code + PKCE; rotating refresh; sender-constrained tokens where supported.
- Batch job: client credentials with narrow `payments:settle`.
- Confused deputy avoided by `aud` checks and mTLS for high-risk clients.

### Common mistakes

- Password grant in new systems.
- One “god” scope for all APIs.
- Resource servers accepting tokens meant for another API.
- Gateway-only authn with forgeable internal headers.

### Senior Engineer discussion

Map each client to a flow and threat model. Scope minimally; encode tenant in claims carefully. Strict redirect URIs. For service calls prefer mTLS/SPIFFE plus narrow tokens. Document BFF token handoff. Test token rejection paths, not only happy login.

### Lead Engineer discussion

Govern one enterprise IdP; ban per-team Keycloak snowflakes without platform ownership. Define scope taxonomy and approval. Integrate identity incidents into on-call. Align OAuth with zero-trust network plans and partner onboarding.

### Tradeoffs

- Central IdP: consistent security, outage/product coupling.
- Many scopes: least privilege, consent/UX complexity.
- Gateway-only authn: simpler services, dangerous if east-west is fully trusted.
- PKCE public clients: better mobile/SPA security, more implementation detail to get right.

### Interview Challenge

1. Partner needs API access—flow and controls?
2. User click triggers an internal multi-service call chain. How does identity propagate?

### Suggested Answer

1. Confidential client credentials (or auth code if user-delegated). Tokens with `aud`=partner API, narrow scopes, mTLS or private_key_jwt, rate limits, audit logs, rotatable credentials. No password grant.
2. Edge validates user token; downstream uses sealed internal token or token exchange including user + service principals; mTLS for service identity. Never trust raw `X-User-Id`. Both user and calling service matter for audit.

## Secrets Management

### Explanation

Secrets are credentials, API keys, private keys, and sensitive connection material. Store in a secrets manager (AWS Secrets Manager, Vault, External Secrets on K8s with KMS)—not git, images, or chat. Inject at runtime; rotate with overlap windows. Workload identity (IRSA/app roles) beats long-lived static cloud keys. Audit reads; alert on anomalous access.

Kubernetes Secrets are base64-encoded, not encrypted at rest unless you configure encryption providers/KMS. Pre-commit secret scanning and CI blockers catch the common leak path; assume leaked secrets are already compromised.

### Why interviewers ask it

- Common breach root cause and interview war story fuel.
- Tests rotation operations, not just “we use Vault.”
- Links IAM, least privilege, and incident response.

### Production examples

- IRSA for Spring on EKS: AWS APIs without static keys.
- DB password rotation with Secrets Manager + Hikari credential refresh.
- Pre-commit scanning blocks AWS keys in PRs.

### Common mistakes

- Secrets in committed `application.yml`.
- One shared prod password for all services.
- No rotation because “it would break us.”
- Treating K8s Secrets base64 as encryption.

### Senior Engineer discussion

Prefer workload identity. If secrets are required: short TTL, automated rotation, per-service/env scope. Redact configs in logs/actuators. Plan break-glass with logging. Test credential refresh under failure—rotation that pages every night is not “done.”

### Lead Engineer discussion

Platform secret injection as paved road. Ban static cloud keys in service catalogs. Set rotation SLAs and ownership. Audit secret sprawl including SaaS tokens. Rehearse leaked-secret playbooks (rotate, invalidate, forensics).

### Tradeoffs

- Dynamic secrets: better security, more moving parts.
- Long-lived shared secrets: simple, catastrophic blast radius.
- Env-var injection: easy, visible in dumps—prefer restricted files or SDK fetch.
- Central Vault: control, availability coupling to Vault HA.

### Interview Challenge

1. GitHub push leaked a prod DB password. Lead the response.
2. How should Spring Boot load DB credentials in EKS?

### Suggested Answer

1. Rotate immediately; kill sessions; audit access from unexpected hosts; purge git history/caches; treat clones as compromised; scan lateral movement; enable secret scanning; postmortem with automation to remove static creds.
2. Prefer IRSA + Secrets Manager/External Secrets syncing to a mounted file or CSI driver; refresh on rotation; never bake into image; least-privilege IAM; separate creds per service/env.

## Encryption

### Explanation

Encryption protects confidentiality: TLS in transit; AES-GCM (or equivalent) at rest; field-level encryption for sensitive columns when threats include honest-but-curious DBAs or snapshot theft beyond volume encryption. Prefer managed KMS with envelope encryption; separate keys by domain and env. Hash passwords with Argon2/Bcrypt—never reversible-encrypt passwords.

Homemade protocols, ECB, and reused nonces are career-limiting moves. Understand what encryption does *not* solve: abuse with valid app credentials, XSS stealing bearer tokens, or insider access through the application layer.

### Why interviewers ask it

- Distinguishes “HTTPS on” from cryptographic design literacy.
- Probes key management and threat models.
- Catches dangerous DIY crypto.

### Production examples

- RDS/S3 CMKs; app-level PAN tokenization via envelope encryption.
- mTLS between services for defense in depth.
- BCrypt password hashes; legacy SHA migrated on login.

### Common mistakes

- Rolling your own crypto protocols.
- ECB or reused nonces.
- Encrypting without integrity (no GCM/HMAC).
- Same key across all tenants/envs.

### Senior Engineer discussion

TLS 1.2+ everywhere; cipher policy at the edge. At rest: volume encryption plus app-level for regulated fields. Use JCA/Tink—not raw Cipher DIY. Plan rotation and re-encryption jobs. Document decrypt paths and audit who can call them.

### Lead Engineer discussion

Data classification drives required controls. Centralize KMS policy and key ownership. Automate cert lifecycle (ACM/cert-manager). Engage compliance early for custody/HSM needs. Make “who can decrypt” a governance question.

### Tradeoffs

- Field-level encryption: stronger confidentiality, breaks naive DB search unless tokenized.
- Customer-managed keys: control, availability coupling to KMS.
- mTLS everywhere: stronger identity, cert ops complexity.
- Shorter cert TTLs: better security, more automation dependency.

### Interview Challenge

1. RDS storage encryption is on—do we still need application-level encryption?
2. How do you rotate a data encryption key for millions of rows?

### Suggested Answer

1. Volume encryption protects media/snapshots; it does not stop `SELECT` with stolen app creds. For regulated fields, add app-level/tokenization with restricted decrypt and audit—decide from threat model/compliance.
2. Envelope encryption: re-wrap data keys with new KEK (fast) or re-encrypt data keys in batches; dual-read old/new during window; track progress; never require downtime full-table rewrite without a plan.

## Rate Limiting

### Explanation

Rate limiting bounds request rates per key (IP, user, client_id, API key) to protect availability and control cost/abuse. Algorithms: token bucket, leaky bucket, sliding/fixed window. Enforce at edge and on expensive/auth endpoints. Return `429` with `Retry-After`. Distributed limiters need shared state (Redis); per-instance limits are softer behind LBs.

Key choice defines fairness: IP-only fails for CGNAT and shared egress; user/client limits matter after auth. Fail-open vs fail-closed when the limiter store is down is a product decision per endpoint criticality.

### Why interviewers ask it

- Practical API protection expected of seniors.
- Tests key choice, fairness, and distributed correctness.
- Links product quotas vs security controls.

### Production examples

- Login limited per IP and per account against credential stuffing.
- Partner quotas by client_id with burst + sustained limits.
- Expensive report export limited per tenant to protect the warehouse.

### Common mistakes

- Only IP limits (NAT/CGNAT and IP rotation).
- In-memory limits per instance behind a load balancer.
- No differentiation of expensive vs cheap endpoints.
- Silent drops without clear client signaling.

### Senior Engineer discussion

Layer limits: edge coarse, service fine for business quotas. Choose keys matching abuse. Make limiter failure policy explicit. Observe 429 rates and false positives. Combine with risk scores/CAPTCHA on auth. Load-test limiters themselves—Redis latency can become the outage.

### Lead Engineer discussion

Productize quotas in API contracts. Align limits with capacity tests and SLOs. Provide shared gateway/Redis modules for consistency. Review partner tiers and abuse complaints as operational data feeding policy.

### Tradeoffs

- Strict limits: stronger protection, more false positives on shared IPs.
- Distributed Redis limits: accurate global caps, extra dependency.
- Fail closed on limiter outage: safer capacity, availability hit.
- Per-route limits: precision, more config to govern.

### Interview Challenge

1. Legitimate mobile users behind CGNAT get 429s. Fix?
2. Design limits for mobile app + ERP batch on the same payments API.

### Suggested Answer

1. After auth, limit by user/device/client_id—not IP alone. Softer anonymous thresholds; risk-based controls; clear backoff; monitor false positives by ASN; careful allowlists only when justified.
2. Separate identities/quotas: user tokens with bursty buckets; ERP client credentials with higher sustained limits and scheduled windows. Edge + service enforcement; never one shared bucket for both.

## DDOS Mitigation

### Explanation

DoS/DDoS exhausts network, connections, compute, or dependencies. Volumetric attacks are largely absorbed by CDN/WAF/cloud scrubbing; application-layer attacks look legitimate and need rate limits, bot management, caching, and cheap-by-default endpoints. Defense in depth: DNS/CDN, WAF, edge limits, capped autoscaling, graceful degradation, dependency protection.

Unbounded autoscaling turns availability attacks into bill attacks. Protect auth and expensive search/report paths first. Have a runbook that preserves money-movement journeys while shedding nice-to-haves.

### Why interviewers ask it

- Availability under abuse is a lead-level concern.
- Distinguishes “buy Cloudflare” from application hardening.
- Tests whether candidates protect dependencies, not only the edge.

### Production examples

- CloudFront/WAF absorb Layer-7 floods; origin sees cleaned traffic.
- Search cached and rate-limited; bot challenges on anomalies.
- Attack pivots to login; account/IP limits + MFA contain without site-wide lockout.

### Common mistakes

- Infinite autoscaling (bill DoS).
- No cache for public read-heavy content.
- Unindexed expensive queries turning bots into DB killers.
- Assuming private APIs are immune to credential stuffing.

### Senior Engineer discussion

Make expensive operations costly to attackers and cheap to you (cache, pagination caps, async jobs). Cap scale; set budget alerts. Protect auth/token endpoints. Use synthetics during attacks to track real-user success. Runbook: tighten WAF, shed non-critical traffic, preserve checkout/pay, communicate status.

### Lead Engineer discussion

Establish provider retainer/runbooks and tabletop exercises. Define business priority for traffic shedding. Track cost controls as resilience. Coordinate status-page comms. Fund permanent fixes after the war room ends—do not only “survive the weekend.”

### Tradeoffs

- Aggressive WAF/bot challenges: protection vs real-user friction.
- Over-scaling: survival vs unbounded cost.
- Heavy caching: resilience vs freshness complexity.
- Global anycast scrubbing: capacity, vendor dependency and privacy routing concerns.

### Interview Challenge

1. Traffic is 20× normal; 90% looks like bots hitting search. Plan?
2. How do you stop a DDoS from taking down payments while search is under attack?

### Suggested Answer

1. CDN cache anonymous search GETs; tighten WAF/bot rules and rate limits; shed faceted/expensive queries; preserve auth/checkout; cap HPA/DB pools; breakers degrade search first; status comms; afterward add query cost budgets and anomaly alerts on QPS vs conversion.
2. Bulkheads and separate pools/clusters for payments vs search; edge priority/routing; independent rate limits; shared dependency budgets; fail search closed before payment pools exhaust. Priority is a design, not a hope during the incident.
