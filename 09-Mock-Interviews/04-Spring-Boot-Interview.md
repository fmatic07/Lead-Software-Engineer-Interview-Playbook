# 04 — Spring Boot Interview

> Production Spring Boot interviews: transactions, caching, security, JPA, REST, performance, and debugging under load.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–75 minutes |
| Focus | Behavior in production, not annotation trivia |
| Stack assumed | Spring Boot 3.x, Security 6, JPA/Hibernate, Actuator |

---

## Purpose

Prove you can design and operate Spring systems: correct transaction boundaries, safe security filters, sane Hibernate usage, and diagnosable failures.

---

## How Interviewers Evaluate

- Correctness of transactional and persistence semantics
- Security threat modeling (JWT, CSRF, authorization)
- Operational readiness (Actuator, metrics, config)
- Ability to debug proxy/AOP surprises
- Lead+: standards, multi-service patterns, failure isolation

---

## Common Mistakes

- `@Transactional` self-invocation myths ignored.
- "Open Session In View is fine" without tradeoff.
- JWT as the only security story (no authz model).
- Caching without invalidation or stampede control.
- Controllers stuffed with business logic.

---

## Excellent Communication Techniques

Name the proxy boundary, the transaction isolation, the consistency model, and the rollback rules. Tie to an incident when possible.

---

## Confidence Tips

Draw a request path: Filter → Controller → Service → Repo → DB/Broker. Annotate where transactions and security decisions occur.

---

## Ideal Answer Framework

**Request path → Boundary → Failure mode → Observability → Mitigation → Tradeoff**

---

## Topic Scripts

### 1. Transactions

**Interviewer:** When does `@Transactional` not apply as developers expect?

**Candidate:** Self-invocation inside the same class bypasses the Spring proxy, so no transaction/advice. Also: checked exceptions may not rollback unless configured; private methods not proxied (depending on strategy); wrong proxy type; readOnly misunderstandings; transactions spanning remote calls (anti-pattern).

**Follow-up:** How do you structure services to avoid this?

**Expected Senior:** Separate beans for transactional boundaries; keep transactions short; no remote I/O inside TX.  
**Expected Lead:** Codify in ADR/review checklist; archunit rules optional; educate team with a lab.

**Evaluation Notes:** Demand rollback rules and boundary drawing.

---

**Interviewer:** You need "insert order + publish event" reliably. Design it.

**Candidate:** Transactional Outbox: persist aggregate + outbox row in one TX; async publisher relays to Kafka/Rabbit; consumers idempotent. Avoid "DB commit then fire-and-forget message" without outbox.

**Follow-up:** At-least-once vs exactly-once?

**Lead:** Exactly-once end-to-end is rare; aim for idempotent processing + dedupe keys; document delivery guarantees.

---

### 2. Caching

**Interviewer:** Add Redis caching to a product catalog. What can go wrong?

**Candidate:** Stale reads after updates; cache stampede; caching nulls incorrectly; huge keys; caching user-specific data under shared keys; serialization issues; redis as single point without timeout/fallback.

**Follow-up:** Invalidation strategy?

**Senior:** Explicit eviction on write paths; versioned keys; TTL as safety net.  
**Lead:** Define consistency SLA per data class; metrics on hit rate and staleness incidents; chaos test Redis latency.

---

### 3. Security

**Interviewer:** Walk through securing a Spring Boot 3 REST API with JWT.

**Candidate:** `SecurityFilterChain` with stateless session; JWT decoder/validator (issuer, audience, exp, signature); authorize `requestMatchers` by role/scope; method security for fine-grained authz; never only "authenticated()"; protect actuator; rotate keys; clock skew tolerance.

**Follow-up:** How do you authorize "user can only access own resources"?

**Excellent:** Object-level checks / `@PreAuthorize` with service lookups; avoid trusting client-provided userId alone; centralize authorization rules.

**Interviewer:** CSRF for SPA + API?

**Candidate:** For pure bearer-token APIs, CSRF often disabled with care; for cookie-based sessions, CSRF tokens required. Clarify auth mechanism first.

---

### 4. Hibernate / JPA

**Interviewer:** N+1 showing up in production. How do you find and fix?

**Candidate:** Enable statistics/slow query logs; detect chatty SQL; fix with `join fetch` / entity graphs / DTO projections; beware cartesian products; pagination + fetch join caveats. Prefer explicit queries for read models.

**Follow-up:** OSIV?

**Senior:** Prefer disabled OSIV; fetch what you need in service TX.  
**Lead:** Mandate OSIV off for new services; performance + transactional clarity.

---

**Interviewer:** `LazyInitializationException` in a REST layer — root causes?

**Candidate:** Session closed; serialization triggering lazy loads; mapping entities directly to JSON. Fix: map to DTOs inside TX; careful fetch plans.

---

### 5. REST & API Design

**Interviewer:** Design versioning and error contracts for public APIs.

**Candidate:** URI or header versioning with compatibility policy; RFC 7807 problem+json; idempotency keys for payments; pagination cursors; rate limit headers; correlation IDs.

**Follow-up:** Breaking change process?

**Lead:** Consumer tally, deprecation window, dual-run, contract tests.

---

### 6. Performance

**Interviewer:** Spring Boot service CPU high after traffic increase. Triage?

**Candidate:** Thread dump + CPU profile; check Jackson serialization, regex, logging; DB pool waits mistaken for CPU; actuator metrics for Tomcat/Hikari; GC; hot endpoints. Bound work (pagination), cache carefully, optimize queries first.

---

### 7. Debugging Scenarios

| Scenario | Strong investigation path |
|----------|---------------------------|
| Intermittent 401 after deploy | JWKS cache, clock, issuer mismatch, filter order |
| Slow first request | Cold start, Hibernate metadata, connection pool, JIT |
| Random rollback | Unchecked vs checked; TX propagation `REQUIRES_NEW` surprises |
| Bean not found in test | Slice test context vs full `@SpringBootTest` |
| Memory grows | Caches, entity persistence context bloat, meter registries |

**Interviewer:** `@Async` methods not running async. Why?

**Candidate:** Same self-invocation/proxy issue; missing `@EnableAsync`; calling from same class; returning non-Future incorrectly; executor saturated.

---

## Additional Rapid-Fire Questions (Production Mix)

1. Difference between `REQUIRED` and `REQUIRES_NEW` — when is REQUIRES_NEW dangerous?
2. How does Spring Boot auto-configuration decide what to load?
3. Custom `HealthIndicator` — what belongs in liveness vs readiness?
4. Config order: `application.yml` vs env vs secrets manager?
5. `RestClient`/`WebClient` timeouts and retries — double retry risk?
6. HikariCP sizing heuristic for your workload?
7. Flyway/Liquibase in blue-green deployments?
8. Testing: unit vs `@DataJpaTest` vs Testcontainers strategy?
9. Multi-tenant Spring apps — discriminator vs separate schemas?
10. Observability: Micrometer + tracing propagation across Thread/virtual thread?
11. Graceful shutdown with in-flight HTTP and messaging consumers?
12. Securing Actuator in cloud deployments?
13. Handling large file uploads through Spring MVC?
14. Domain events vs application events vs broker events?
15. Circuit breaker placement relative to DB transactions?

---

## Full Mock Loop (30–40 min excerpt)

**Interviewer:** Design a Spring Boot payments command API: create payment, idempotent retries, notify wallet service.

**Candidate structure:**
1. Requirements: exactly-once *effects*, at-least-once delivery, authn/z, audit.
2. API: `POST /payments` with `Idempotency-Key`.
3. Service TX: persist payment + outbox; unique constraint on idempotency key.
4. Publisher to Kafka; wallet consumer idempotent.
5. Security: scopes `payments:write`; audit log.
6. Observability: metrics on duplicate keys, publish lag, failures.
7. Failure modes: DB up / broker down; poison messages; timeout to client with safe retry.

**Follow-up:** Client times out but payment committed — what does client see?

**Expected:** Safe replay with same idempotency key returns same payment resource; never create duplicate.

**Evaluation Notes:** Strong candidates invent idempotency without prompting.

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| TX/persistence | Confused | Basic annotations | Boundary mastery |
| Security | Authn only | Roles | Authz + threat model |
| Ops | None | Actuator basics | SLOs, failure playbooks |
| Debugging | Guesses | Plausible | Systematic, proxy-aware |
| Lead signal | Self only | Mentions standards | Sets team contracts |

---

## Confidence Checklist

- [ ] Draw TX + proxy boundaries correctly
- [ ] Outbox / idempotency story ready
- [ ] JWT + method security + object authz
- [ ] N+1 and LIE diagnosis
- [ ] Cache invalidation + stampede
- [ ] One Spring production incident STAR

---

## Notes

<!-- Link war stories to Module 03 Spring Boot docs -->
