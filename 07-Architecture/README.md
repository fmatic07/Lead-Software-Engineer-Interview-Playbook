# Module 07 — Software Architecture & Engineering Design

> A judgment handbook for Senior → Lead → Tech Lead → Architect loops — tradeoffs, boundaries, and long-term evolution over pattern catalogs.

---

## Audience

Senior Software Engineer · Lead Software Engineer · Technical Lead · Software Architect  
(extensive enterprise Java / Spring production experience assumed)

This is **not** a beginner architecture tutorial. It prepares you for architecture discussions that probe engineering judgment, maintainability, scalability, resilience, and software evolution under real constraints.

---

## How This Differs from [04-System-Design](../04-System-Design/)

| | **04 — System Design** | **07 — Architecture** |
|---|------------------------|------------------------|
| **Primary question** | How do I design a system that meets scale, latency, and reliability NFRs? | How do I choose styles, boundaries, and evolution paths I can defend for years? |
| **Unit of thought** | Capacity, data stores, caching, messaging, failure modes under load | Principles, topologies, DDD seams, integration contracts, decision quality |
| **Interview shape** | Whiteboard a service (URL shortener, feed, payments API) with tradeoff analysis | Defend monolith vs microservices, draw bounded contexts, narrate ADRs and production outcomes |
| **Overlap** | Both cover microservices, APIs, data, and cloud | Prefer **04** for scalability drills; prefer **07** for style/DDD/decision/story depth |

Study **04** and **07** together in Phase 3. Use System Design for “design X under load”; use Architecture for “why this shape, what we gave up, how we evolved.”

---

## Chapter Map

| # | Chapter | Focus |
|---|---------|--------|
| 01 | [Architecture Principles](./01-Architecture-Principles.md) | SoC, SOLID, DRY/KISS/YAGNI, cohesion/coupling, composition vs inheritance, DIP, designing for change |
| 02 | [Architectural Styles](./02-Architectural-Styles.md) | Layered, Clean, Hexagonal/Onion, modular monolith, microservices, EDA, serverless — fit and anti-fit |
| 03 | [Domain-Driven Design](./03-Domain-Driven-Design.md) | Ubiquitous language, bounded contexts, aggregates, repositories, ACL — when DDD pays off |
| 04 | [Microservice Architecture](./04-Microservice-Architecture.md) | Boundaries, gateway, discovery, data ownership, sync/async, sagas, choreography vs orchestration |
| 05 | [Enterprise Integration Patterns](./05-Enterprise-Integration-Patterns.md) | Queues, buses, request/reply, pub/sub, outbox, CDC, retry, circuit breaker, bulkhead, rate limiting |
| 06 | [API Architecture](./06-API-Architecture.md) | REST, GraphQL, gRPC, versioning, contract-first, idempotency, pagination, HATEOAS, OpenAPI |
| 07 | [Data Architecture](./07-Data-Architecture.md) | SQL/NoSQL, CQRS, event sourcing, read models, sync, eventual consistency, migrations, multi-tenancy |
| 08 | [Architectural Decision Making](./08-Architectural-Decision-Making.md) | Buy vs build, today vs tomorrow, debt, reviews, cost, scale planning, risk, ADRs |
| 09 | [Architecture Case Studies](./09-Architecture-Case-Studies.md) | Constrained end-to-end designs with seams, tradeoffs, and failure modes |
| 10 | [Architecture Interview Questions](./10-Architecture-Interview-Questions.md) | Dense Q&A bank with Senior vs Lead answers, tradeoffs, and follow-ups |
| 11 | [Architecture Through Real Production Experience](./11-Architecture-Through-Real-Production-Experience.md) | Production storytelling — perf, legacy, APIs, data, cloud, scale, reliability |

Every chapter includes **engineering tradeoffs**, an **architecture review / confidence checklist**, and an **interview challenge** (plus suggested answer framing where applicable). Fill **Notes** with your own production decisions.

---

## Supplementary Ops & Frontend Chapters

Infrastructure and full-stack integration depth that supports Day 5 / Phase 3 coverage:

| # | Chapter | Focus |
|---|---------|--------|
| 12 | [Docker and Containerization](./12-Docker-and-Containerization.md) | Images, multi-stage builds, orchestration basics for enterprise Java |
| 13 | [Cloud-Native AWS](./13-Cloud-Native-AWS.md) | Compute, storage, networking, security, and managed services selection |
| 14 | [CI/CD Pipelines](./14-CI-CD-Pipelines.md) | Build, test, scan, deploy gates before production |
| 15 | [React and Next.js Integration](./15-React-and-NextJS-Integration.md) | Spring Boot ↔ React/Next.js contracts, auth, SSR/SSG tradeoffs |

---

## Study Guidance (Interview Prep)

1. **Start with 01–02** — principles and styles are the vocabulary for every later chapter.
2. **Layer 03–07** in order — DDD → microservices → integration → APIs → data; each chapter assumes the prior boundaries.
3. **Practice 08 aloud** — Lead loops hire for decision quality; rehearse ADRs and stakeholder framing.
4. **Drill 09–10 under time** — one case study + 5–10 questions per session; record yourself.
5. **Fill 11 with your war stories** — map each narrative to metrics, alternatives rejected, and lessons.
6. **Skim 12–15** when the panel is cloud/DevOps/full-stack heavy; do not substitute them for 01–11 judgment work.
7. **Cross-link to 04-System-Design** for capacity and NFR whiteboard drills after you can defend the architecture shape.

**Exit criteria for Module 07:** You can choose a style under constraints, draw bounded contexts, name integration failure modes, and tell one production architecture story with tradeoffs cold.
