# Architecture Interview Questions

> One hundred architecture prompts with Senior vs Lead answers — judgment, tradeoffs, and follow-ups under enterprise constraints.

---

## How to Use This Chapter

1. Answer aloud in 2–4 minutes before reading the sample answers.
2. Compare your **Senior** depth (owned subsystem, metrics, failure modes) to **Lead** breadth (org, sequencing, standards).
3. Write one production note per question you miss cold.
4. Drill follow-ups — panels hire on the second and third question.
5. Pair with [09-Architecture-Case-Studies](./09-Architecture-Case-Studies.md) and [11-Architecture-Through-Real-Production-Experience](./11-Architecture-Through-Real-Production-Experience.md).

**Interview rhythm:** constraint → options → decision → tradeoff → operability → lesson.

---

## Why This Matters in Production

Architecture interviews reward the same muscle production requires: choosing under incomplete information, naming what you give up, and owning the operational aftermath. Candidates who only recite styles fail when asked "what broke?" and "what did you measure?"

---

## Engineering Tradeoffs (Answering Architecture Questions)

- Depth vs breadth: Senior answers go deep on one system; Lead answers connect systems and teams — do both deliberately.
- Certainty vs honesty: False precision loses trust; clear assumptions win.
- Tool names vs semantics: Kafka is not a design; delivery guarantees and ownership are.
- Speed vs structure: Unstructured brainstorms burn the clock; a tight decision narrative fits the loop.

---

## Common Anti-Patterns (In Interviews)

- Microservices as default without constraints.
- "It depends" with no decision.
- Perfect diagrams with no failure mode.
- Claiming experience you cannot defend with metrics or incidents.
- Ignoring cost, compliance, or on-call load.

---

## Best Practices (Answering)

- State constraints and assumptions in the first 30 seconds.
- Offer two real alternatives and a recommendation.
- Quantify when possible (p99, RPO/RTO, team size, throughput).
- Separate what you did from what you would do next.
- End with how you would validate and reverse the decision.

---

## Architecture Review Checklist (Self-Assessment Before Interviews)

- [ ] I can defend monolith vs microservices with org and data arguments
- [ ] I can explain saga/outbox/idempotency without slides
- [ ] I can draw bounded contexts for a domain I know
- [ ] I have 3 production architecture stories with metrics
- [ ] I can discuss API evolution and data migration safely
- [ ] I can name resilience patterns with configuration pitfalls
- [ ] I can run a lightweight ADR narrative cold
- [ ] I can challenge a bad design respectfully and concretely

---

## Principles & Foundations

### Q001 — How do you decide between Monolith and Microservices?

**Why interviewers ask it**

Panels want a decision framework, not a fashion answer. This question filters candidates who chase microservices for resume value from those who size organizational and operational cost correctly.

**Competencies being evaluated**

Constraint analysis; org/Conway alignment; operational maturity; sequencing judgment; ability to say "not yet."

**Candidate Thinking Process**

Clarify team count, deploy cadence, data consistency needs, and ops maturity. Separate product boundaries from deployable units. Ask what pain the split would remove today. Propose modular monolith as default unless independent scale, failure isolation, or team autonomy is proven.

**Excellent Senior Engineer Answer**

I default to a modular monolith with clear package/module boundaries and a single deployable until a concrete pain appears: independent scaling of a hot path, separate release trains, or a failure domain that must not take down the core. I have split services only when ownership and data boundaries were already clear and we had observability, CI, and on-call ready. Example: kept order+inventory colocated until inventory write contention forced an extract with an outbox.

**Excellent Lead Engineer Answer**

I frame the decision as reversible investment. I set exit criteria for splitting (SLOs, deploy queue length, blast-radius incidents) and entry criteria for staying monolith (shared transaction needs, small team). I align org structure first—Conway—then draw service seams. I socialize ADRs so product and platform share the cost model: more services means more contracts, more failure modes, more on-call load.

**Architecture Tradeoffs**

Microservices buy independent deploy/scale and team autonomy; cost is distributed data, debugging, and platform overhead. Monoliths buy simplicity and ACID workflows; cost is coupling risk and single deploy blast radius. Wrong split creates a distributed monolith—worst of both.

**Common Mistakes**

- Declaring microservices "best practice" without constraints.
- Ignoring data ownership and pretending sync REST across services is still a modular monolith.
- Splitting by technical layer ("user-service", "db-service") instead of domain.

**Follow-up Questions**

1. What evidence would make you reverse a split?
2. How do you keep a monolith modular enough to split later?
3. How does team size change the answer?


### Q002 — When would you choose Event Driven Architecture?

**Why interviewers ask it**

Tests whether you understand asynchrony as a consistency and coupling choice, not a buzzword for "using Kafka."

**Competencies being evaluated**

Temporal decoupling; consistency models; failure semantics; backpressure; domain event design.

**Candidate Thinking Process**

Identify workflows that can tolerate eventual consistency and benefit from fan-out. Distinguish commands vs events. Define delivery guarantees, idempotency, and ownership of the source of truth. Reject EDA where the user needs an immediate consistent answer in one request.

**Excellent Senior Engineer Answer**

I choose EDA when multiple consumers must react to a fact independently—notifications, search indexing, analytics—or when producers must not block on consumer availability. In Spring systems I pair DB commit with an outbox so events are not lost. I avoid EDA for "place order and show confirmed inventory" unless I design a clear pending state and compensation.

**Excellent Lead Engineer Answer**

I set platform standards: schema registry, idempotency keys, DLQ policy, and ownership of topics by bounded context. I push product to accept UX for eventual consistency. I stop teams from turning every REST call into a topic when a synchronous boundary is clearer and cheaper to operate.

**Architecture Tradeoffs**

EDA improves resilience and fan-out; costs include ordering complexity, delayed feedback, and harder end-to-end tracing. Sync APIs simplify UX and transactions; they couple availability.

**Common Mistakes**

- Using events as a remote procedure call.
- No idempotency or duplicate handling.
- Shared mutable "event bags" with no schema evolution plan.

**Follow-up Questions**

1. How do you guarantee at-least-once without double side effects?
2. When is choreography wrong?
3. How do you debug a multi-consumer lag incident?


### Q003 — How do you reduce technical debt?

**Why interviewers ask it**

Separates engineers who complain about debt from those who manage it as a portfolio with risk and ROI.

**Competencies being evaluated**

Prioritization; risk framing; incremental delivery; influence without pure rewrite authority.

**Candidate Thinking Process**

Classify debt by interest rate: what slows delivery or raises incident risk now. Prefer strangler and seam extraction over big-bang rewrites. Tie paydown to product outcomes and reliability SLOs. Make debt visible in roadmap language, not only eng jargon.

**Excellent Senior Engineer Answer**

I inventory debt with symptoms—flaky tests, 2-day change lead time, hotspots in production—and pay the highest-interest items adjacent to active feature work. Example: extract a payment adapter while adding a new PSP rather than a standalone rewrite quarter.

**Excellent Lead Engineer Answer**

I create a debt budget (e.g., 15–20% capacity) and an ADR/risk register for "accepted debt." I negotiate with product using incident cost and cycle-time metrics. I prevent new high-interest debt via review gates and fitness functions, not heroics.

**Architecture Tradeoffs**

Aggressive paydown slows features short-term; ignoring debt slows everything later. Rewrites risk regression; incremental seams preserve learning but prolong dual-running.

**Common Mistakes**

- "Rewrite everything" as the only plan.
- Treating all debt as equal.
- Secret refactors with no tests or rollback.

**Follow-up Questions**

1. How do you convince a PM to fund debt work?
2. What debt should you accept?
3. How do you measure progress?


### Q004 — How do you evolve a legacy application?

**Why interviewers ask it**

Legacy evolution is the modal enterprise job. Panels test strangler skill and risk control.

**Competencies being evaluated**

Incremental migration; characterization testing; dual-running; rollback; stakeholder communication.

**Candidate Thinking Process**

Map seams and risk hotspots. Establish characterization tests around behavior you must preserve. Choose strangler routes, anti-corruption layers, and data migration strategy. Prefer expand/contract over cutover nights when possible.

**Excellent Senior Engineer Answer**

I start by making the system observable and testable at the edges, then route new capabilities through new modules/services behind a facade. I keep the legacy DB as source of truth until write ownership can move safely. I ship thin vertical slices that prove value early.

**Excellent Lead Engineer Answer**

I sequence migration by business risk and team capacity, publish a migration radar, and protect production with feature flags and dual-write reconciliation. I align org ownership so the strangler is not orphaned. I set explicit kill criteria for the legacy path.

**Architecture Tradeoffs**

Fast migration reduces dual-run cost but raises cutover risk. Slow strangler is safer but expensive. Shared DB during migration simplifies reads and creates coupling.

**Common Mistakes**

- Big-bang rewrite with no parity tests.
- "Temporary" dual writes that become permanent.
- Migrating code without migrating data ownership.

**Follow-up Questions**

1. How do you handle schema that both systems write?
2. When do you stop dual-running?
3. How do you keep features shipping during migration?


### Q005 — How do you structure a large enterprise codebase?

**Why interviewers ask it**

Probes modularity, ownership, and build/deploy topology—not folder aesthetics.

**Competencies being evaluated**

Module boundaries; dependency rules; build tooling; team ownership; API contracts.

**Candidate Thinking Process**

Structure by domain capability first, technical layer second. Enforce dependency direction (domain does not depend on adapters). Decide monolith modules vs multi-repo only after ownership and CI costs are clear.

**Excellent Senior Engineer Answer**

I use a modular monolith or multi-module Maven/Gradle build with bounded packages: `domain`, `application`, `adapters`. I ban cyclic dependencies and push shared code into small libraries with clear APIs. Controllers stay thin; domain rules stay out of JPA entities when invariants are rich.

**Excellent Lead Engineer Answer**

I define module ownership, CODEOWNERS, and architectural fitness checks in CI (ArchUnit). I decide monorepo vs polyrepo based on release coupling and platform support. I invest in inner-source libraries for cross-cutting concerns so domains do not fork auth and observability.

**Architecture Tradeoffs**

Fine modules improve ownership but increase navigation and versioning cost. Coarse modules are simpler until they become mud. Shared libraries reduce duplication and create coupling.

**Common Mistakes**

- Organizing only by layers (`controller`, `service`, `repository`) with no domain seams.
- A `common` module that everything depends on.
- Copy-paste services with divergent standards.

**Follow-up Questions**

1. How do you enforce dependency rules?
2. When is a multi-repo worth it?
3. How do you handle shared kernel?


### Q006 — How do you design for maintainability?

**Why interviewers ask it**

Maintainability is how senior engineers create lasting leverage. Panels listen for change cost, not cleanliness slogans.

**Competencies being evaluated**

Change locality; naming; test strategy; operability; deliberate simplicity.

**Candidate Thinking Process**

Optimize for the next engineer's ability to change behavior safely. Prefer clear boundaries, boring tech where possible, and tests at the right altitude. Design APIs and schemas for evolution. Document decisions that are not obvious from code.

**Excellent Senior Engineer Answer**

I design modules around expected change axes, keep cyclomatic complexity low on money paths, and invest in contract tests and characterization tests. I avoid speculative abstraction. Maintainability shows up as lead time and escaped defects, which I track.

**Excellent Lead Engineer Answer**

I set coding and review standards that protect boundaries, fund platform tooling (lint, ArchUnit, golden paths), and make operability part of "done": dashboards, runbooks, and ownership. I resist framework churn that does not buy maintainability.

**Architecture Tradeoffs**

More abstraction can ease extension and hurt readability. More tests raise confidence and slow change if brittle. Standardization helps onboarding and can block local optimization.

**Common Mistakes**

- Gold-plating abstractions for imaginary futures.
- No docs on non-obvious invariants.
- Tests coupled to implementation details.

**Follow-up Questions**

1. How do you measure maintainability?
2. When is duplication better than the wrong abstraction?
3. How do you keep standards from becoming bureaucracy?


### Q007 — How do you prevent architecture erosion?

**Why interviewers ask it**

Erosion is how good designs die. Lead candidates must show governance without theater.

**Competencies being evaluated**

Fitness functions; review culture; ADRs; dependency management; incentive alignment.

**Candidate Thinking Process**

Define the intended architecture as enforceable rules, not wiki pages. Detect violations in CI. Make the golden path easiest. Review cross-boundary changes. Revisit ADRs when reality diverges.

**Excellent Senior Engineer Answer**

I treat erosion as daily pull requests: wrong-layer imports, shared DB reaches, sync calls that should be events. I use ArchUnit and code review comments that cite the ADR. I fix hotspots when touching adjacent code.

**Excellent Lead Engineer Answer**

I establish lightweight architecture reviews for cross-context changes, publish fitness functions, and measure coupling metrics. I align incentives so shipping through the golden path is faster than bypassing it. I schedule periodic "architecture health" reviews with actionable debt.

**Architecture Tradeoffs**

Heavy governance slows delivery; zero governance yields mud. Automated checks scale; they miss intent. Manual reviews catch intent; they do not scale alone.

**Common Mistakes**

- Architecture slides with no enforcement.
- Exceptions without expiry.
- Platform rules that teams must bypass to ship.

**Follow-up Questions**

1. What fitness functions have you used?
2. How do you handle emergency exceptions?
3. How do you socialize intended architecture to new hires?


### Q008 — How do you handle breaking API changes?

**Why interviewers ask it**

API evolution is a core enterprise skill. Panels test compatibility thinking and consumer empathy.

**Competencies being evaluated**

Versioning strategy; expand/contract; consumer-driven contracts; communication; deprecation.

**Candidate Thinking Process**

Prefer additive changes. Use expand/contract for renames/removals. Version only when necessary. Identify consumers, dual-run, deprecate with dates, monitor usage, then remove. Never break silently.

**Excellent Senior Engineer Answer**

I avoid breaking changes by adding fields and new endpoints. When I must break, I version (`v2` or header), run both versions, emit deprecation headers/metrics, and coordinate with known consumers. Contract tests catch accidental breaks in CI.

**Excellent Lead Engineer Answer**

I set org policy: compatibility windows, ownership of public APIs, and a deprecation calendar. I push producer teams to own migration guides and dashboards of consumer versions. For partner APIs I involve legal/SLA early.

**Architecture Tradeoffs**

Long dual-version support raises cost; aggressive breaks raise partner pain and incident risk. Strict OpenAPI gates reduce breaks and can slow experimentation.

**Common Mistakes**

- Shipping a break because "it's internal."
- Version proliferation without retirement.
- No telemetry on deprecated field usage.

**Follow-up Questions**

1. Header vs URL versioning—when?
2. How do you version events?
3. How do you force migration of a lagging consumer?


### Q009 — How do you scale a backend service?

**Why interviewers ask it**

Classic probe of whether scaling means "add Kubernetes pods" or a full capacity strategy.

**Competencies being evaluated**

Bottleneck analysis; horizontal vs vertical; data tier limits; caching; async offload; load shedding.

**Candidate Thinking Process**

Measure first: CPU, DB, locks, external I/O, GC. Scale the bottleneck. Separate read/write paths if needed. Protect dependencies with pools, timeouts, and backpressure. Only then add replicas.

**Excellent Senior Engineer Answer**

I profile under realistic load. Typical Spring path: DB connections and queries dominate. I fix N+1 and missing indexes, add caching for hot reads, shard or partition when a single primary saturates, and scale stateless app pods horizontally. I load-shed with 429/503 before cascading failure.

**Excellent Lead Engineer Answer**

I plan capacity with SLOs and growth forecasts, define scaling playbooks, and invest in platform autoscaling and DB operations. I decide when to split a service for independent scale. I make cost visible—scaling blindly is not architecture.

**Architecture Tradeoffs**

Horizontal app scale is easy until the database is not. Caching improves latency and creates invalidation risk. Async improves throughput and complicates UX consistency.

**Common Mistakes**

- Scaling pods while the DB is the bottleneck.
- No load testing with production-like skew.
- Caching without TTL/invalidation strategy.

**Follow-up Questions**

1. How do you know the DB is the bottleneck?
2. When do you shard vs read-replicas?
3. How do you scale a stateful workflow?


### Q010 — How do you structure engineering teams around software architecture?

**Why interviewers ask it**

Conway's Law question. Lead/Architect loops care deeply about team-service alignment.

**Competencies being evaluated**

Org design; ownership; platform vs product; cognitive load; communication paths.

**Candidate Thinking Process**

Align teams to bounded contexts and value streams. Keep cognitive load bounded. Provide a platform team for golden paths. Avoid orphan services and matrix ownership of critical paths.

**Excellent Senior Engineer Answer**

I prefer stream-aligned teams owning a domain end-to-end (API, data, on-call). I avoid assigning one team ten microservices they cannot understand. Shared libraries and platform reduce duplication without taking product ownership away.

**Excellent Lead Engineer Answer**

I co-design org and architecture: if we need independent deploy of billing, billing needs a team. I use Team Topologies language carefully—platform, enabling, stream-aligned. I revisit ownership after every major split/merge. Architecture reviews include "who pages?"

**Architecture Tradeoffs**

More teams increase parallelism and interface overhead. Strong platform speeds product teams and can become a bottleneck if underfunded. Strict ownership clarifies accountability and can create silos.

**Common Mistakes**

- Microservices with no owning team.
- Frontend/backend split that mirrors every change across two tickets.
- Architecture decided without staffing reality.

**Follow-up Questions**

1. How do you handle a shared kernel team?
2. What is the max services per team?
3. How do you fund platform work?


### Q011 — Explain Separation of Concerns with a production example.

**Why interviewers ask it**

Checks if SoC is lived, not recited.

**Competencies being evaluated**

Boundary judgment; ownership; testability.

**Candidate Thinking Process**

Pick a change axis and show what stayed stable when another concern changed.

**Excellent Senior Engineer Answer**

I separate API DTOs from domain model and persistence entities when they evolve differently. When a partner API changed, only the adapter and ACL changed—ledger rules untouched.

**Excellent Lead Engineer Answer**

I enforce concern boundaries with module rules and review checklists. Cross-cutting concerns live in platform libraries with versioned APIs.

**Architecture Tradeoffs**

Strict separation improves change locality and can add mapping overhead on simple CRUD.

**Common Mistakes**

- God service classes.
- Premature layering on trivial apps.

**Follow-up Questions**

1. When is mapping between layers waste?
2. How do SoC and team ownership interact?


### Q012 — When would you deliberately violate DRY?

**Why interviewers ask it**

Tests judgment against dogma.

**Competencies being evaluated**

Abstraction timing; coupling cost; duplication risk.

**Candidate Thinking Process**

Weigh duplication cost vs wrong-abstraction cost. Prefer duplication until a stable shared concept emerges.

**Excellent Senior Engineer Answer**

I duplicate validation at API edge and domain when they protect different risks. I wait for a third repetition with the same change reason before extracting.

**Excellent Lead Engineer Answer**

I ban "shared utils" dumps. Shared code requires an owning team and compatibility policy. I accept intentional duplication across bounded contexts.

**Architecture Tradeoffs**

DRY reduces copy-paste bugs and creates coupling. Duplication localizes change and risks divergent fixes.

**Common Mistakes**

- Premature shared library.
- DRY across bounded contexts that must diverge.

**Follow-up Questions**

1. How do you spot the wrong abstraction?
2. Example of harmful DRY in microservices?


### Q013 — How do KISS and YAGNI show up in architecture reviews?

**Why interviewers ask it**

Filters over-engineers from pragmatic seniors.

**Competencies being evaluated**

Simplicity bias; speculative design detection.

**Candidate Thinking Process**

Ask which requirement forces complexity. Remove speculative extension points. Prefer boring designs that meet today's NFRs with a clear evolution path.

**Excellent Senior Engineer Answer**

I reject event sourcing for a CRUD admin tool. I reject a service mesh for three services on one cluster without proven need. I keep a design that a new teammate can operate in a week.

**Excellent Lead Engineer Answer**

I coach teams to document rejected alternatives in ADRs. I challenge "future-proof" designs without named futures. I protect time for simplicity in reviews.

**Architecture Tradeoffs**

Too much KISS underbuilds for known scale. Too much speculation burns years of complexity interest.

**Common Mistakes**

- Pattern-driven design.
- Building multi-region before single-region is stable.

**Follow-up Questions**

1. How do you future-proof without YAGNI violations?
2. When is complexity mandatory?


### Q014 — Composition vs inheritance — how do you choose in Java systems?

**Why interviewers ask it**

Classic OO judgment question with Spring reality.

**Competencies being evaluated**

Coupling; testability; framework constraints.

**Candidate Thinking Process**

Prefer composition for behavior variation. Use inheritance sparingly for true is-a stable hierarchies. Watch Spring proxy/self-invocation traps.

**Excellent Senior Engineer Answer**

I compose payment providers behind an interface and inject strategies. I avoid deep abstract service base classes that hide transaction and security behavior.

**Excellent Lead Engineer Answer**

I set guidelines: no inheritance deeper than one meaningful level in domain; prefer interfaces + delegates. I review framework base-class usage for operational surprises.

**Architecture Tradeoffs**

Inheritance reuses quickly and couples hierarchies. Composition is explicit and more verbose.

**Common Mistakes**

- Template Method forests.
- Inheritance for code reuse only.

**Follow-up Questions**

1. How do sealed types change this?
2. Inheritance issues with Spring @Transactional?


### Q015 — What does Dependency Inversion mean in a Spring Boot service?

**Why interviewers ask it**

Checks hexagonal thinking in everyday Spring.

**Competencies being evaluated**

Ports/adapters; test doubles; boundary ownership.

**Candidate Thinking Process**

Domain defines interfaces (ports); infrastructure implements adapters. Spring wires implementations. Domain does not import JDBC/Kafka clients.

**Excellent Senior Engineer Answer**

My domain depends on `PaymentPort`; `StripeAdapter` implements it. Tests use fakes. Swapping PSP does not rewrite order logic.

**Excellent Lead Engineer Answer**

I enforce package dependency rules and review that new libraries do not leak into domain. Platform provides adapter templates.

**Architecture Tradeoffs**

DIP improves substitution and testing; costs indirection and more types.

**Common Mistakes**

- Interfaces with one implementation forever and no test benefit.
- Controllers depending on concrete repositories across modules.

**Follow-up Questions**

1. DIP vs dependency injection—difference?
2. When is an interface unnecessary?


### Q016 — How do you design for change without over-engineering?

**Why interviewers ask it**

Core architecture judgment.

**Competencies being evaluated**

Volatility analysis; optionality; reversible decisions.

**Candidate Thinking Process**

Identify likely change axes from product/org reality. Put stable decisions in concrete code; put volatile ones behind narrow interfaces. Prefer reversible choices (flags, expand/contract).

**Excellent Senior Engineer Answer**

I isolate third-party integrations and pricing rules that change quarterly. I keep stable ledger invariants concrete and well-tested. I do not abstract the database "just in case."

**Excellent Lead Engineer Answer**

I run change-risk workshops with product. ADRs capture what we expect to change. I invest optionality only where cost of being wrong is high.

**Architecture Tradeoffs**

Options have carrying cost. No options make pivots expensive.

**Common Mistakes**

- Abstracting everything.
- Hard-coding known volatiles (PSP, tax engine).

**Follow-up Questions**

1. What change axes surprised you?
2. How do you price an option in engineering time?


### Q017 — High cohesion and low coupling — give a counterexample from production.

**Why interviewers ask it**

Wants concrete failure story.

**Competencies being evaluated**

Modular design; incident learning.

**Candidate Thinking Process**

Describe a module that mixed concerns or a chatty coupling that caused cascade failures.

**Excellent Senior Engineer Answer**

A "CustomerService" that handled profile, KYC, notifications, and billing created multi-week change lead times. We split by bounded context; notification became async.

**Excellent Lead Engineer Answer**

I use coupling metrics and service charter reviews. I stop "utility services" that attract unrelated endpoints.

**Architecture Tradeoffs**

Extreme decoupling increases latency and ops cost. Extreme cohesion creates monoliths of mud.

**Common Mistakes**

- Measuring only class count.
- Confusing network boundary with decoupling.

**Follow-up Questions**

1. How do you detect low cohesion early?
2. Can two services be highly coupled?


## Architectural Styles

### Q018 — Layered vs Clean/Hexagonal — when does layering fail?

**Why interviewers ask it**

Style selection under enterprise constraints.

**Competencies being evaluated**

Dependency direction; domain protection.

**Candidate Thinking Process**

Layered fails when domain depends inward on frameworks or when layers become horizontal silos. Hexagonal helps when adapters multiply.

**Excellent Senior Engineer Answer**

Classic three-layer Spring apps rot when services become transaction scripts calling repositories everywhere. I move to ports/adapters when we have multiple entry points (REST, messaging, batch).

**Excellent Lead Engineer Answer**

I pick styles per system criticality. I do not mandate hexagonal ceremony for every CRUD. I standardize adapter patterns for regulated domains.

**Architecture Tradeoffs**

Hexagonal clarity vs ceremony. Layers are familiar and easy to misuse.

**Common Mistakes**

- Renaming packages to "domain" without dependency rules.
- One style dogma for all systems.

**Follow-up Questions**

1. How do you migrate a layered app to hexagonal?
2. Onion vs hexagonal—practical difference?


### Q019 — When is a modular monolith the right end state—not a stepping stone?

**Why interviewers ask it**

Counters microservices inevitability myth.

**Competencies being evaluated**

Simplicity; ACID; team size.

**Candidate Thinking Process**

If one team, strong consistency needs, and modest scale, modular monolith can be the destination.

**Excellent Senior Engineer Answer**

For an internal policy admin with 20 rps and complex workflows, modular monolith with modules and ArchUnit was the right end state—ops cost stayed low.

**Excellent Lead Engineer Answer**

I explicitly ADR "modular monolith as target" so teams do not split prematurely for fashion. I revisit when team count or scale demands change.

**Architecture Tradeoffs**

Monolith simplicity vs independent scale/deploy limits.

**Common Mistakes**

- Calling anything with packages a modular monolith without dependency enforcement.

**Follow-up Questions**

1. How do you test module boundaries?
2. What forces you off a modular monolith?


### Q020 — Serverless — when do you choose it for enterprise Java workloads?

**Why interviewers ask it**

Cloud judgment beyond Spring-on-EC2 habit.

**Competencies being evaluated**

Cost model; cold start; ops ownership; integration limits.

**Candidate Thinking Process**

Prefer for spiky, event-triggered, short-lived work. Avoid for long-lived, latency-critical, heavily connection-pooled Java without careful design.

**Excellent Senior Engineer Answer**

I use Lambda/functions for async image processing and cron-like reconciliation. I keep core request path on always-on Spring services when p99 and connection pooling matter.

**Excellent Lead Engineer Answer**

I set guidance on cold starts, VPC costs, observability, and when to prefer containers. I prevent serverless sprawl without ownership.

**Architecture Tradeoffs**

Serverless reduces idle cost and increases invocation/constraint complexity. Always-on simplifies latency and raises baseline cost.

**Common Mistakes**

- Porting a large Spring Boot fat jar to Lambda without redesign.
- No local/dev parity story.

**Follow-up Questions**

1. How do you handle DB connections in serverless?
2. Java cold start mitigations?


### Q021 — Compare Event-Driven Architecture with request/response for order fulfillment.

**Why interviewers ask it**

Probes whether you can decide and operate around "Compare Event-Driven Architecture with request/response for order fulfillment." under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Order placement needs immediate ack to the user; fulfillment steps (payment capture confirmation, warehouse, notify) can be async. I keep create-order sync with strong inventory reservation, then emit OrderPlaced for downstream. Compensating events handle payment failure after reservation.

**Excellent Lead Engineer Answer**

I define which steps are user-blocking vs eventually consistent, set lag SLOs for fulfillment consumers, and forbid "sync over Kafka" request-reply for the happy path unless latency budgets force it.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q022 — What problems does Hexagonal Architecture actually solve in Spring apps?

**Why interviewers ask it**

Probes whether you can decide and operate around "What problems does Hexagonal Architecture actually solve in Spring apps?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Hexagonal pays off when we have REST + messaging + batch entry points and multiple outbound systems (PSP, KYC). Ports keep domain free of Spring Web and Kafka client types. Mapping cost is acceptable when adapters churn.

**Excellent Lead Engineer Answer**

I mandate ports for regulated money paths and allow simpler layering for internal admin tools. ArchUnit enforces dependency direction.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q023 — When should you avoid microservices even if the org wants them?

**Why interviewers ask it**

Probes whether you can decide and operate around "When should you avoid microservices even if the org wants them?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Avoid when team < ~2 squads, workflows need frequent multi-entity ACID, platform (CI, observability, on-call) is immature, or boundaries are unclear. A distributed monolith is worse than a modular monolith.

**Excellent Lead Engineer Answer**

I push leadership for modular monolith milestones and exit criteria before any split. I cost the platform tax explicitly in the business case.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Domain-Driven Design

### Q024 — What is a Bounded Context and how do you find one?

**Why interviewers ask it**

Probes whether you can decide and operate around "What is a Bounded Context and how do you find one?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

I find contexts by language fractures: "Account" in banking vs CRM means different things. Workshop event storming, look at organizational ownership, and watch where models disagree. Separate contexts with explicit translation.

**Excellent Lead Engineer Answer**

I charter contexts with owners and published language. I stop enterprise data models that force one Account everywhere.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q025 — How do you design Aggregates and transactional boundaries?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you design Aggregates and transactional boundaries?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

An aggregate is a consistency boundary: one transaction, one root. I size aggregates small—Order with lines, not Order+Customer+Payments. Cross-aggregate rules become eventual via domain events or sagas.

**Excellent Lead Engineer Answer**

I review aggregate designs for hotspot contention (single global aggregate) and for chatty multi-aggregate sync transactions that should be sagas.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q026 — Entity vs Value Object — how do you decide?

**Why interviewers ask it**

Probes whether you can decide and operate around "Entity vs Value Object — how do you decide?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Entities have identity that persists (CustomerId). Value objects are defined by attributes (Money, Address) and are immutable. I model Money as VO to avoid float bugs and equality mistakes.

**Excellent Lead Engineer Answer**

I standardize VO libraries for money/quantity in the platform and ban primitive obsession in critical domains via review.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q027 — When do you need a Domain Service vs an Application Service?

**Why interviewers ask it**

Probes whether you can decide and operate around "When do you need a Domain Service vs an Application Service?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Domain services hold domain rules that do not naturally sit on one entity (FX conversion across accounts). Application services orchestrate use cases, transactions, and outbound ports—no business policy.

**Excellent Lead Engineer Answer**

I police anemic domains where all logic sits in application services. Reviews ask: "Is this a business invariant or plumbing?"

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q028 — Explain Anti-Corruption Layer with a partner integration example.

**Why interviewers ask it**

Probes whether you can decide and operate around "Explain Anti-Corruption Layer with a partner integration example." under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

ACL translates partner models into our ubiquitous language. Example: hotel supplier XML rates → our RatePlan VO. Prevents supplier schema from infecting the core.

**Excellent Lead Engineer Answer**

I require ACLs for all external bounded contexts and assign ownership so translation debt does not rot in controllers.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q029 — How does Ubiquitous Language fail in large enterprises?

**Why interviewers ask it**

Probes whether you can decide and operate around "How does Ubiquitous Language fail in large enterprises?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Language fails when committees invent enterprise glossaries nobody uses, or when the same word spans contexts. Fix by context-scoped glossaries and renaming APIs/tables when language drifts.

**Excellent Lead Engineer Answer**

I fund glossary + example-driven specs per context and include language checks in design reviews.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q030 — Repository pattern — what belongs in a repository and what does not?

**Why interviewers ask it**

Probes whether you can decide and operate around "Repository pattern — what belongs in a repository and what does not?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Repositories load/save aggregates and express domain-oriented queries. They do not send emails, publish Kafka messages, or contain workflow orchestration. Complex reporting gets a read model/query service.

**Excellent Lead Engineer Answer**

I ban "generic repository" CRUD that leaks across aggregates and encourage intentional query APIs.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q031 — How do you integrate two bounded contexts without a shared database?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you integrate two bounded contexts without a shared database?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Integrate via published open-host APIs or domain events, plus ACL on the consumer. Duplicate data deliberately as read models. No shared tables.

**Excellent Lead Engineer Answer**

I set integration standards (async first for non-user-blocking) and a data product catalog so teams do not invent private DB links.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q032 — What is a shared kernel and when is it dangerous?

**Why interviewers ask it**

Probes whether you can decide and operate around "What is a shared kernel and when is it dangerous?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Shared kernel is a small, highly stable shared model between contexts. Dangerous when it becomes a dumping ground or forces lockstep releases. Prefer duplicate VOs or published events.

**Excellent Lead Engineer Answer**

I require an owning team, semver, and change review for any shared kernel. Default answer is "no shared kernel."

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Microservices

### Q033 — How do you define service boundaries?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you define service boundaries?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Boundaries follow business capabilities and change cadence, not nouns or tables. A service should own its data and be deployable by one team. If every feature touches five services, boundaries are wrong.

**Excellent Lead Engineer Answer**

I run boundary reviews with org design. I merge chatty services and split only on proven axes (scale, compliance, team).

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q034 — API Gateway responsibilities — what should NOT live there?

**Why interviewers ask it**

Probes whether you can decide and operate around "API Gateway responsibilities — what should NOT live there?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Gateway: authn, TLS termination, routing, rate limits, request logging. Not: core business workflows, multi-step orchestration that owns business state, or embedding domain rules. BFF aggregation is OK if thin.

**Excellent Lead Engineer Answer**

I prevent gateway from becoming an ESB. Heavy orchestration belongs in application services or workflow engines with clear ownership.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q035 — Service discovery in Kubernetes vs older approaches — what still matters?

**Why interviewers ask it**

Probes whether you can decide and operate around "Service discovery in Kubernetes vs older approaches — what still matters?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

On Kubernetes, DNS/Service objects replace much of Netflix Eureka-style discovery, but you still need timeouts, retries, circuit breaking, and locality. Discovery does not solve load or failure semantics.

**Excellent Lead Engineer Answer**

I standardize service mesh or library resilience policies so discovery is not mistaken for reliability.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q036 — Database per service — how do you implement reporting?

**Why interviewers ask it**

Probes whether you can decide and operate around "Database per service — how do you implement reporting?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Reporting via analytics pipeline: events/CDC → warehouse/lake → read models. Avoid cross-service joins in OLTP. For operational UI, use composed APIs or a dedicated read service fed asynchronously.

**Excellent Lead Engineer Answer**

I fund a data platform path early when adopting DB-per-service so product teams do not recreate shared DB for reports.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q037 — When is a shared database an acceptable temporary stage?

**Why interviewers ask it**

Probes whether you can decide and operate around "When is a shared database an acceptable temporary stage?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Acceptable during strangler with clear schema ownership, expand/contract migrations, and a dated exit. Not acceptable as a permanent multi-service integration bus.

**Excellent Lead Engineer Answer**

I track shared-DB exceptions with owners and end dates. No new tables in foreign schemas without review.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q038 — Synchronous vs asynchronous communication — decision criteria?

**Why interviewers ask it**

Probes whether you can decide and operate around "Synchronous vs asynchronous communication — decision criteria?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Sync when the caller needs the result to continue UX or maintain a local invariant. Async when work can complete later, fan-out exists, or you must absorb spikes. Never async-wash a required sync user journey without pending states.

**Excellent Lead Engineer Answer**

I publish decision criteria and default timeouts. I review sync chains longer than two hops as reliability risks.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q039 — How do you handle distributed transactions without 2PC?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you handle distributed transactions without 2PC?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Avoid 2PC across services. Use sagas with compensations, outbox for reliable messaging, idempotent receivers, and reconciliation. For money, prefer a single ledger service with strong consistency and async projections.

**Excellent Lead Engineer Answer**

I ban distributed XA as a standard. I require saga design reviews for cross-service money movement.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q040 — Saga pattern — choreography vs orchestration?

**Why interviewers ask it**

Probes whether you can decide and operate around "Saga pattern — choreography vs orchestration?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Choreography: services react to events—simple, decentralized, harder to see global state. Orchestration: a coordinator drives steps—clearer visibility, central ownership risk. Complex/compensating flows often need orchestration.

**Excellent Lead Engineer Answer**

I choose orchestration for high-risk multi-step money flows with SLAs; choreography for simple fan-out. I require correlation IDs and a workflow view for ops.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q041 — How do you ensure idempotency across service calls?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you ensure idempotency across service calls?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Clients send Idempotency-Key; services persist key+response. Consumers dedupe by eventId. Retries must be safe. Exactly-once effects are built from at-least-once + idempotency.

**Excellent Lead Engineer Answer**

I mandate idempotency on all money and provisioning APIs and provide a platform idempotency store pattern.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q042 — What is a distributed monolith and how do you detect it?

**Why interviewers ask it**

Probes whether you can decide and operate around "What is a distributed monolith and how do you detect it?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Symptoms: lockstep releases, chatty sync meshes, shared DB, inability to deploy one service alone. Fix by consolidating or by repairing data ownership and async boundaries—not by adding more services.

**Excellent Lead Engineer Answer**

I measure release coupling and cross-service change frequency. I fund merges when distribution has no benefit.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q043 — How do you version and evolve events between services?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you version and evolve events between services?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Prefer additive fields, schema registry compatibility checks, and consumer tolerance. Use explicit event versioning or upcasters for breaking changes. Dual-publish during migrations.

**Excellent Lead Engineer Answer**

I enforce compatibility in CI and own deprecation windows for event schemas like public APIs.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q044 — BFF pattern — when is it justified?

**Why interviewers ask it**

Probes whether you can decide and operate around "BFF pattern — when is it justified?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

BFF justified when multiple clients need different aggregations/auth shapes and the gateway would otherwise accumulate client-specific logic. Not justified as a second business layer.

**Excellent Lead Engineer Answer**

I allow BFFs per client channel with thin aggregation and clear ownership; I prevent domain logic leakage into BFF.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Integration Patterns

### Q045 — Outbox pattern — why not publish to Kafka in the same request thread after commit?

**Why interviewers ask it**

Probes whether you can decide and operate around "Outbox pattern — why not publish to Kafka in the same request thread after commit?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Publishing after commit in another connection can lose events on crash. Outbox writes the event in the same DB transaction as state change; a relay publishes to Kafka. Dual-write without outbox is a production bug farm.

**Excellent Lead Engineer Answer**

I make outbox the standard for reliable domain events and provide a shared relay library/metrics.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q046 — CDC vs application events — how do you choose?

**Why interviewers ask it**

Probes whether you can decide and operate around "CDC vs application events — how do you choose?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Application events express business meaning. CDC captures data change and is great for replication/search/cache but can leak schema and miss intent. Prefer domain events for workflows; CDC for integration/read models when app changes are hard.

**Excellent Lead Engineer Answer**

I discourage CDC as a substitute for product domain events on core workflows. I allow CDC for brownfield unlock.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q047 — Retry strategies that do not amplify outages

**Why interviewers ask it**

Probes whether you can decide and operate around "Retry strategies that do not amplify outages" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Retries need jitter, caps, idempotency, and respect for deadlines. Do not retry 4xx (except 408/429 carefully). Propagate time budgets. Retry storms cause outages.

**Excellent Lead Engineer Answer**

I standardize retry policies in gateway/clients and load-test cascading failure scenarios.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q048 — Circuit breaker — how do you configure failure thresholds responsibly?

**Why interviewers ask it**

Probes whether you can decide and operate around "Circuit breaker — how do you configure failure thresholds responsibly?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Open on error rate/latency to fail fast and shed load. Configure with realistic windows; pair with fallbacks that do not lie about money state. Half-open probes carefully.

**Excellent Lead Engineer Answer**

I require dashboards for open state and forbid silent fallbacks on financial confirmations.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q049 — Bulkhead pattern in thread pools and connection pools

**Why interviewers ask it**

Probes whether you can decide and operate around "Bulkhead pattern in thread pools and connection pools" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Isolate thread pools and connection pools per dependency so one slow collaborator cannot exhaust the whole app. In Java, separate pools for payment vs notification clients.

**Excellent Lead Engineer Answer**

I review pool sizing vs DB and downstream limits during capacity planning.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q050 — Rate limiting at gateway vs service — where and why?

**Why interviewers ask it**

Probes whether you can decide and operate around "Rate limiting at gateway vs service — where and why?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Gateway limits protect the estate and enforce partner contracts; service limits protect critical resources and multi-tenant noisy neighbors. Use both: edge for abuse, service for fairness.

**Excellent Lead Engineer Answer**

I define tenant quotas as product features with observability and appeal paths.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q051 — Request-reply over messaging — pitfalls

**Why interviewers ask it**

Probes whether you can decide and operate around "Request-reply over messaging — pitfalls" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Possible but easy to misuse: timeout correlation, lost replies, and operational complexity. Prefer sync HTTP for true request/response unless you need broker-mediated decoupling with clear correlation.

**Excellent Lead Engineer Answer**

I allow request-reply messaging only with platform patterns for correlation and deadlines.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q052 — Publish/subscribe fan-out — ordering and lag

**Why interviewers ask it**

Probes whether you can decide and operate around "Publish/subscribe fan-out — ordering and lag" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Fan-out scales consumers independently. Per-key ordering needs partition keys; global ordering does not scale. Lag is an SLO—alert and scale consumers; design producers for backpressure.

**Excellent Lead Engineer Answer**

I set lag budgets per consumer and capacity playbooks. I avoid single-partition topics for high throughput.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q053 — Dead letter queues — operational design

**Why interviewers ask it**

Probes whether you can decide and operate around "Dead letter queues — operational design" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

DLQ is not ignore-forever. Define classification, replay tooling, alert ownership, and poison-message handling. Include payload redaction for PII.

**Excellent Lead Engineer Answer**

I require runbooks and SLAs for DLQ depth. DLQ without owners is an incident debt account.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q054 — Exactly-once illusions in messaging systems

**Why interviewers ask it**

Probes whether you can decide and operate around "Exactly-once illusions in messaging systems" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Brokers may offer idempotent producers/EOS in limited scopes; end-to-end exactly-once effects still need idempotent business handling. Design for at-least-once.

**Excellent Lead Engineer Answer**

I educate teams to stop treating broker settings as business correctness.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## API Architecture

### Q055 — REST design mistakes you see in enterprise APIs

**Why interviewers ask it**

Probes whether you can decide and operate around "REST design mistakes you see in enterprise APIs" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Verbs in URLs, inconsistent error models, breaking field renames, unbounded lists without pagination, chatty N+1 APIs, using POST for everything, leaking internal IDs/PII.

**Excellent Lead Engineer Answer**

I publish API style guides and automated linting on OpenAPI in CI.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q056 — When would you choose GraphQL over REST?

**Why interviewers ask it**

Probes whether you can decide and operate around "When would you choose GraphQL over REST?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Choose GraphQL for multi-client flexible read models with strong governance on complexity/cost. Avoid as a substitute for poor REST design or for simple CRUD with few clients.

**Excellent Lead Engineer Answer**

I require query cost analysis, persisted queries for production, and N+1 defenses (DataLoader).

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q057 — When is gRPC the right internal protocol?

**Why interviewers ask it**

Probes whether you can decide and operate around "When is gRPC the right internal protocol?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Strong for internal service-to-service with low latency, typed contracts, streaming. Weaker for public browser APIs without gateway translation. Pair with careful versioning of protobuf.

**Excellent Lead Engineer Answer**

I standardize gRPC for intra-cluster sync paths and REST/JSON at the edge.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q058 — Contract-first development — how do you practice it with OpenAPI?

**Why interviewers ask it**

Probes whether you can decide and operate around "Contract-first development — how do you practice it with OpenAPI?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Write OpenAPI first, generate server stubs/clients, review contracts in PRs, consumer-driven tests where critical. Prevents UI/backend drift.

**Excellent Lead Engineer Answer**

I make contract PR review a gate for public/partner APIs and fund style checkers.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q059 — Idempotency keys for payment APIs

**Why interviewers ask it**

Probes whether you can decide and operate around "Idempotency keys for payment APIs" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Client-generated key scoped to payer/merchant stored with response. Retries return the first result. Combine with exactly-once money ledger invariants and reconciliation.

**Excellent Lead Engineer Answer**

I treat missing idempotency on payment APIs as a Sev-level design defect.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q060 — Pagination strategies: offset vs cursor vs keyset

**Why interviewers ask it**

Probes whether you can decide and operate around "Pagination strategies: offset vs cursor vs keyset" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Offset breaks under churn and is expensive on deep pages. Cursor/keyset is stable for feeds. Return opaque cursors; never expose raw offsets for large datasets.

**Excellent Lead Engineer Answer**

I ban offset pagination on large tenant data APIs without explicit exception.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q061 — Is HATEOAS worth it in enterprise APIs?

**Why interviewers ask it**

Probes whether you can decide and operate around "Is HATEOAS worth it in enterprise APIs?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Rarely worth full hypermedia in enterprise APIs; clients seldom drive from links. Useful sparingly for workflow state transitions. Prefer clear resource models and docs.

**Excellent Lead Engineer Answer**

I do not mandate HATEOAS; I do mandate explicit state machines where workflows matter.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q062 — OpenAPI governance across many teams

**Why interviewers ask it**

Probes whether you can decide and operate around "OpenAPI governance across many teams" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Write OpenAPI first, generate server stubs/clients, review contracts in PRs, consumer-driven tests where critical. Prevents UI/backend drift.

**Excellent Lead Engineer Answer**

I make contract PR review a gate for public/partner APIs and fund style checkers.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q063 — How do you secure APIs beyond JWT-on-every-request?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you secure APIs beyond JWT-on-every-request?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

mTLS service identity, fine-grained authz, audience-scoped tokens, secret rotation, abuse rate limits, data minimization, audit logs. JWT alone is not a security architecture.

**Excellent Lead Engineer Answer**

I set zero-trust defaults for service mesh identity and break-glass procedures.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q064 — Public vs partner vs private API design differences

**Why interviewers ask it**

Probes whether you can decide and operate around "Public vs partner vs private API design differences" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Public: stability, versioning, abuse controls. Partner: contractual SLAs, signed webhooks, stricter auth. Private: can evolve faster but still needs compatibility discipline inside the company.

**Excellent Lead Engineer Answer**

I classify APIs in a catalog with different compatibility SLAs and review depth.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Data Architecture

### Q065 — SQL vs NoSQL decision framework

**Why interviewers ask it**

Probes whether you can decide and operate around "SQL vs NoSQL decision framework" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

SQL for relational invariants, joins, transactions. NoSQL for flexible docs, extreme partition scale, simple key access. Multi-model often wins: SQL system of record + search/analytics stores.

**Excellent Lead Engineer Answer**

I reject NoSQL for ledgers without a hard scale reason. I fund polyglot only with ownership for each store.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q066 — When is CQRS justified?

**Why interviewers ask it**

Probes whether you can decide and operate around "When is CQRS justified?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Justified when read/write models differ sharply, scale diverges, or workflows need optimized projections. Not justified for simple CRUD with one UI.

**Excellent Lead Engineer Answer**

I require a problem statement (scale/complexity) before CQRS. I watch for dual-write bugs without outbox.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q067 — When should you avoid Event Sourcing?

**Why interviewers ask it**

Probes whether you can decide and operate around "When should you avoid Event Sourcing?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Avoid when audit-via-logs suffices, team lacks expertise, queries are ad-hoc relational, or debugging timelines would overwhelm. ES shines for complex temporal domains with clear event language.

**Excellent Lead Engineer Answer**

I treat ES as high-cost; default to state + audit log unless domain demands ES.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q068 — Designing read models for query scale

**Why interviewers ask it**

Probes whether you can decide and operate around "Designing read models for query scale" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Build projections from events/CDC optimized for queries. Version projections, make rebuildable, monitor lag. Do not treat read DB as source of truth for writes.

**Excellent Lead Engineer Answer**

I require rebuild playbooks for projections and lag SLOs.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q069 — Eventual consistency — how do you make it acceptable to users?

**Why interviewers ask it**

Probes whether you can decide and operate around "Eventual consistency — how do you make it acceptable to users?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Use pending states, optimistic UI with reconciliation, clear "processing" labels, and SLAs on visibility delay. Never pretend strong consistency in the UI when the backend is eventual.

**Excellent Lead Engineer Answer**

I negotiate UX copy with product as part of architecture acceptance.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q070 — Data migration strategies with zero/near-zero downtime

**Why interviewers ask it**

Probes whether you can decide and operate around "Data migration strategies with zero/near-zero downtime" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Expand/contract schema, dual-write or backfill, shadow reads, batch migrators with checkpoints, instant rollback of reads via flags. Avoid long exclusive locks.

**Excellent Lead Engineer Answer**

I require migration runbooks, rehearsal on prod-sized data, and success metrics before cutover.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q071 — Multi-tenancy: silo vs pool vs hybrid

**Why interviewers ask it**

Probes whether you can decide and operate around "Multi-tenancy: silo vs pool vs hybrid" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Silo for strong isolation/compliance; pool for efficiency with rigorous tenant guards; hybrid for enterprise tiers. Missing tenant predicates is a Sev-1 class bug.

**Excellent Lead Engineer Answer**

I classify tenants by tier and enforce tenant tests in CI for pooled models.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q072 — How do you synchronize data across services?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you synchronize data across services?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Prefer events/CDC to async read models. Avoid two-way sync. If sync is required, define system of record, conflict rules, and reconcile jobs.

**Excellent Lead Engineer Answer**

I ban ad-hoc dual writes without reconciliation. I fund data contracts.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q073 — Handling schema evolution in event-sourced systems

**Why interviewers ask it**

Probes whether you can decide and operate around "Handling schema evolution in event-sourced systems" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Upcasters, weak schema, versioned events, rebuilding projections. Never mutate historical events in place without a governed rewrite tool.

**Excellent Lead Engineer Answer**

I require replay tests in CI for upcasters.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q074 — Strong consistency requirements in banking-style ledgers

**Why interviewers ask it**

Probes whether you can decide and operate around "Strong consistency requirements in banking-style ledgers" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Ledger writes are strongly consistent within account/partition with immutable entries and constraints. Async is for projections/notifications, not for balance authority.

**Excellent Lead Engineer Answer**

I keep money movement in narrowly owned services with hard invariants and audit.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Decision Making

### Q075 — Buy vs build — your decision checklist

**Why interviewers ask it**

Probes whether you can decide and operate around "Buy vs build — your decision checklist" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Buy undifferentiated commodity (auth providers, email delivery) when integration cost < build+ops. Build core differentiators and regulated invariants. Include exit cost in the buy decision.

**Excellent Lead Engineer Answer**

I run buy/build with security, cost, and roadmap fit; I reject buy that blocks core domain control.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q076 — Build for today vs tomorrow — how do you draw the line?

**Why interviewers ask it**

Probes whether you can decide and operate around "Build for today vs tomorrow — how do you draw the line?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Design for today's requirements plus reversible paths for known near-term scale. Do not build multi-region fantasy. Document one-way doors carefully.

**Excellent Lead Engineer Answer**

I challenge speculative platforms in reviews and fund optionality only where irreversibility is high.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q077 — How do you run an architecture review that helps rather than blocks?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you run an architecture review that helps rather than blocks?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Time-boxed, decision-focused, with context docs beforehand. Output: decisions, risks, follow-ups—not slide theater. Invite builders, not only architects.

**Excellent Lead Engineer Answer**

I measure review lead time and whether reviews catch production risks. I kill rubber-stamp councils.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q078 — How do you quantify and communicate architectural risk?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you quantify and communicate architectural risk?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Express risk as likelihood × impact on SLOs, compliance, and delivery. Use ADRs and risk registers. Tie to incidents and dependency concentration.

**Excellent Lead Engineer Answer**

I report top architecture risks alongside product risks to leadership with mitigations.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q079 — Cost optimization without harming reliability

**Why interviewers ask it**

Probes whether you can decide and operate around "Cost optimization without harming reliability" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Optimize after understanding drivers: overprovisioned DB, chatty calls, retention, idle environments. Never cut redundancy that protects money paths without risk acceptance.

**Excellent Lead Engineer Answer**

I pair FinOps with error budgets so cost cuts cannot silently burn reliability.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q080 — Scalability planning before you have traffic

**Why interviewers ask it**

Probes whether you can decide and operate around "Scalability planning before you have traffic" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Use scenarios, synthetic load, and architecture that scales horizontally at the app tier without premature sharding. Measure early; avoid distributed complexity before product-market fit.

**Excellent Lead Engineer Answer**

I define scale milestones that unlock complexity (e.g., shard only past X TPS).

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q081 — Refactoring strategy for a brownfield system under feature pressure

**Why interviewers ask it**

Probes whether you can decide and operate around "Refactoring strategy for a brownfield system under feature pressure" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Strangle adjacent to features, keep the main branch shippable, characterization tests first, small PRs, feature flags. Negotiate a debt budget explicitly.

**Excellent Lead Engineer Answer**

I protect a fixed capacity slice for refactor and stop "drive-by rewrites" without tests.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q082 — How do you write an ADR that teams actually use?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you write an ADR that teams actually use?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

One to two pages: context, decision, consequences, status. Link from repo. Revisit when violated. Store next to code, not in a lost Confluence space.

**Excellent Lead Engineer Answer**

I require ADRs for cross-team decisions and check them in reviews.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q083 — How do you say no to a bad architecture proposal?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you say no to a bad architecture proposal?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Reframe as risk and cost with alternatives. Offer a smaller experiment. Escalate with written tradeoffs if needed. Never only "I don't like it."

**Excellent Lead Engineer Answer**

I create a safe path to dissent and back engineers who raise risk with data.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q084 — How do you prioritize non-functional work on a product roadmap?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you prioritize non-functional work on a product roadmap?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Translate NFRs into user/business outcomes: fewer outages, faster checkout, compliance gates. Bundle with features when possible. Use error budgets.

**Excellent Lead Engineer Answer**

I negotiate NFR epics with measurable acceptance and executive visibility.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Resilience & Ops

### Q085 — How do you design for partial failure?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you design for partial failure?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Timeouts, bulkheads, fallbacks that are honest, degrade non-critical features, queue work, and preserve core invariants. Design UX for degraded mode.

**Excellent Lead Engineer Answer**

I require dependency failure mode matrices in design docs for critical systems.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q086 — Timeouts, retries, and deadlines — how do they compose?

**Why interviewers ask it**

Probes whether you can decide and operate around "Timeouts, retries, and deadlines — how do they compose?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Set deadlines at the edge and subtract hop budgets. Retries only within remaining deadline. Align gateway, client, and server timeouts or you get retry amplification.

**Excellent Lead Engineer Answer**

I standardize budget propagation (e.g., deadlines/trace context) across services.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q087 — Backpressure strategies in Spring and messaging

**Why interviewers ask it**

Probes whether you can decide and operate around "Backpressure strategies in Spring and messaging" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Bounded queues, load shedding, consumer lag control, HTTP 429, reactive stream demand where applicable. Unlimited queues turn outages into memory disasters.

**Excellent Lead Engineer Answer**

I review queue bounds and shed policies in capacity tests.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q088 — Chaos testing — what do you actually inject?

**Why interviewers ask it**

Probes whether you can decide and operate around "Chaos testing — what do you actually inject?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Inject latency, kill pods, partition networks, exhaust connection pools, fail dependencies. Start in staging; graduate carefully. Always with blast radius control.

**Excellent Lead Engineer Answer**

I fund chaos on critical paths tied to SLO learning, not theater.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q089 — SLO/error budget driven architecture changes

**Why interviewers ask it**

Probes whether you can decide and operate around "SLO/error budget driven architecture changes" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

When budgets burn, freeze features or prioritize reliability work. Architecture changes should map to SLO defense (caching, isolation, failover).

**Excellent Lead Engineer Answer**

I run error-budget policies with product leadership so reliability is not optional.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q090 — Multi-region active-active vs active-passive

**Why interviewers ask it**

Probes whether you can decide and operate around "Multi-region active-active vs active-passive" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Active-passive is simpler for strong consistency. Active-active needs conflict rules and data topology—hard for ledgers. Choose based on RTO and consistency class.

**Excellent Lead Engineer Answer**

I resist active-active for money cores without a clear conflict story.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Security & Compliance

### Q091 — How does architecture change under PCI/regulated data?

**Why interviewers ask it**

Probes whether you can decide and operate around "How does architecture change under PCI/regulated data?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Reduce scope: tokenize, isolate PCI zone, minimize data retention, strict access, audit. Architecture follows compliance boundary first.

**Excellent Lead Engineer Answer**

I involve security/compliance early and treat scope reduction as a first-class design goal.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q092 — Zero trust for service-to-service calls

**Why interviewers ask it**

Probes whether you can decide and operate around "Zero trust for service-to-service calls" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Expand/contract schema, dual-write or backfill, shadow reads, batch migrators with checkpoints, instant rollback of reads via flags. Avoid long exclusive locks.

**Excellent Lead Engineer Answer**

I require migration runbooks, rehearsal on prod-sized data, and success metrics before cutover.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q093 — Auditability as an architectural requirement

**Why interviewers ask it**

Probes whether you can decide and operate around "Auditability as an architectural requirement" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Immutable audit events for sensitive actions, who/what/when, tamper evidence, retention. Not the same as debug logs.

**Excellent Lead Engineer Answer**

I require audit trails on money, access, and consent changes as acceptance criteria.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q094 — Secrets and key management in distributed systems

**Why interviewers ask it**

Probes whether you can decide and operate around "Secrets and key management in distributed systems" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Central secret store, short-lived credentials, rotation, no secrets in images/repos, envelope encryption for sensitive fields.

**Excellent Lead Engineer Answer**

I ban long-lived static credentials in services via platform policy.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

## Leadership & Evolution

### Q095 — How do you onboard teams onto an existing architecture?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you onboard teams onto an existing architecture?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Golden path docs, paved road templates, pairing, architecture katas, and a glossary. Reduce tribal knowledge.

**Excellent Lead Engineer Answer**

I fund enabling teams and measure time-to-first-production-change.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q096 — How do you resolve architectural disagreements between senior engineers?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you resolve architectural disagreements between senior engineers?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Time-box debate, write options, spike if needed, decide with accountable owner, record ADR. Disagree and commit.

**Excellent Lead Engineer Answer**

I facilitate decisions and prevent endless architecture ping-pong.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q097 — Measuring architecture quality beyond "feels clean"

**Why interviewers ask it**

Probes whether you can decide and operate around "Measuring architecture quality beyond "feels clean"" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Change lead time, escaped defects, coupling metrics, incident MTTR, fitness function pass rates, onboarding time—not vibes.

**Excellent Lead Engineer Answer**

I publish a small architecture health dashboard to leadership.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q098 — Platform engineering vs product architecture ownership

**Why interviewers ask it**

Probes whether you can decide and operate around "Platform engineering vs product architecture ownership" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Platform provides golden paths; product owns domain architecture. Platform should not own business rules. Product should not each reinvent auth/obs.

**Excellent Lead Engineer Answer**

I clarify charters and SLAs between platform and product teams.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q099 — How do you retire a technology from the estate?

**Why interviewers ask it**

Probes whether you can decide and operate around "How do you retire a technology from the estate?" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Inventory usage, provide replacement golden path, migrate incrementally, set dates, remove from paved road, then turn off. Celebrate deletion.

**Excellent Lead Engineer Answer**

I run technology radar with explicit hold/retire and funding for migration.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?

### Q100 — Communicating architecture to non-engineers

**Why interviewers ask it**

Probes whether you can decide and operate around "Communicating architecture to non-engineers" under enterprise constraints—not define it.

**Competencies being evaluated**

Judgment under constraints; failure-mode thinking; production evidence; Senior depth vs Lead system/org impact.

**Candidate Thinking Process**

Clarify goals, constraints, and NFRs. List options with failure modes and operability cost. Recommend one approach for the stated context, name tradeoffs, and describe validation/rollback.

**Excellent Senior Engineer Answer**

Use risk, cost, time-to-market, and user impact. Avoid pattern soup. One diagram, one decision ask.

**Excellent Lead Engineer Answer**

I coach engineers to present options with business consequences.

**Architecture Tradeoffs**

Optimizing for one property (isolation, consistency, speed, cost, autonomy) degrades another. State what you are selling and how you will detect if the price is too high.

**Common Mistakes**

- Textbook definition without a decision.
- Ignoring data ownership, idempotency, or rollback.
- Tool-name answers without semantics.
- Assuming greenfield freedom in a brownfield estate.

**Follow-up Questions**

1. What incident or metric informed your stance?
2. What would reverse this decision?
3. How do you roll this out safely under feature pressure?
4. Who owns the operational burden after the decision?


---

## Interview Challenge

**Prompt:** In 10 minutes, answer: "We need to extract billing from our modular monolith. How would you approach it?" Then answer two follow-ups: data ownership and rollback.

### Suggested Answer

Clarify why extract (team, scale, compliance). Propose strangler: billing module already isolated → anti-corruption facade → dual-write/outbox for invoices → move writes → move reads → retire old paths. Database-per-service with replicated read model for the monolith. Saga for refunds across order+billing. Success metrics: error budget, reconcile job zero-diff, deploy independence. Rollback: feature flag back to in-process billing, keep dual-running until reconcile is clean. Lead add-on: owning team, on-call, API compatibility window, ADR.

---

## Architecture Reflection Questions

1. Which ten questions above expose gaps in your production stories?
2. Where do your Senior and Lead answers currently sound identical — and what Lead dimension is missing?
3. Which tradeoffs do you avoid saying aloud that panels need to hear?
4. What decision would you reverse from your last two years — and how would you narrate it?
5. How will you practice follow-ups without memorizing scripts?

---

## Interview Confidence Checklist

- [ ] 100 questions skimmed; weak topics flagged
- [ ] 20 questions answered aloud with timer
- [ ] Senior vs Lead distinction practiced on 10 prompts
- [ ] Follow-ups drilled for microservices, data, and evolution
- [ ] Three war stories mapped to at least 15 questions
- [ ] Anti-pattern answers ("always microservices") eliminated
- [ ] Ready to whiteboard constraints before boxes

---

## Notes

<!-- Map question IDs to your production examples and metrics -->
