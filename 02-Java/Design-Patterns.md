# Design Patterns

> Patterns as context-dependent design vocabulary, with their operational costs made explicit.

## Design Patterns

### Explanation

A pattern is a recurring tradeoff, not a target architecture. Use one when it makes variation, lifecycle, or a boundary clearer than direct code. In modern Java/Spring systems, records, sealed types, lambdas, dependency injection, and composition often replace ceremonial class hierarchies.

Commonly useful forms include Strategy for policy variation, Adapter for external boundaries, Decorator for orthogonal behavior, Factory for construction policy, and Chain of Responsibility for ordered processing. Proxy/AOP is powerful for cross-cutting behavior but has invocation-boundary traps. Observer decouples publication from reaction but introduces delivery, ordering, and failure semantics. Singleton scope is a lifecycle choice, not proof of statelessness or thread safety.

### Why interviewers ask it

- Tests whether abstractions follow real change axes and team constraints.
- Reveals understanding of framework proxies, lifecycle, testing, and distributed failure semantics.
- Distinguishes pattern recognition from pattern-driven over-engineering.

### Production examples

- An Adapter isolates a payment provider's unstable model and enables controlled dual-running during migration.
- Strategy selects pricing policy, but a registry of Spring beans needs deterministic conflict and rollout rules.
- A Decorator adds metrics/retries around a client; incorrect ordering retries non-idempotent calls and records misleading latency.
- Spring transactional proxies do not intercept self-invocation, causing code to run outside the expected transaction.
- An in-process Observer loses events on crash; an outbox and broker are required when delivery is a business guarantee.

### Common mistakes

- Adding interfaces, factories, and builders with only one stable implementation and no boundary value.
- Using inheritance/Template Method where composition would localize change.
- Hiding network I/O, retries, or transactions behind innocent-looking methods.
- Assuming Spring singleton beans are safe under concurrent requests.
- Naming classes after patterns while leaving failure and lifecycle semantics undefined.

### Follow-up questions

1. When is Strategy better than a switch, and when is it not?
2. How do proxy-based transactions and retries fail at self-invocation boundaries?
3. When must Observer become durable messaging?
4. How do decorator order and idempotency interact?

### Senior-level discussion

Start from volatility and ownership. Identify which rule, provider, protocol, or lifecycle is expected to change and put the abstraction at that boundary. Include operational contracts: timeout, retry, idempotency, transaction scope, observability, thread safety, rollout, and compatibility. An elegant pattern that obscures remote cost or partial failure is architecturally harmful.

Evaluate removal cost as well as extension cost. A small switch over a sealed hierarchy may be clearer and safer than reflection-driven plugin discovery. Refactor toward a pattern after repeated variation provides evidence; do not prepay complexity for speculative reuse. Track abstraction quality through change blast radius, defect rate, testability, and onboarding—not class count.

### Tradeoffs

- Strategy enables independent policy evolution but increases discovery, wiring, and configuration risk.
- Adapter protects the domain but adds mapping code and potential semantic loss.
- Decorator composes behavior but ordering changes semantics and stack traces.
- Factory centralizes construction but can become a service locator.
- Events decouple time and ownership but introduce eventual consistency, duplication, ordering, and observability costs.
- AOP reduces repetition but makes behavior less visible and depends on proxy mechanics.

### Best practices

- Name abstractions after domain roles, not `*Pattern` terminology.
- Keep provider models behind adapters and test contracts at the boundary.
- Make decorator order explicit; define idempotency before adding retries.
- Prefer constructor injection, immutable dependencies, and stateless singleton services.
- Record why an abstraction exists and simplify it when the expected variation disappears.

### Interview Challenge

1. A team proposes Strategy, Abstract Factory, and a plugin registry for two stable tax rules. How do you decide?
2. A `@Transactional` method called from another method in the same Spring bean does not open a transaction. Diagnose and redesign.
3. An in-process domain-event Observer sends customer notifications. Events are occasionally lost or duplicated. Redesign the boundary.
4. A retry decorator surrounds a metrics decorator and reported latency looks healthy during an outage. Explain the ordering problem.

### Suggested Answer

1. Identify credible variation rate, independent deployment/configuration needs, regulatory ownership, and test boundaries. Two explicit branches or a sealed-type switch may be the lowest-cost design today. Introduce Strategy when policies vary independently; add factories/plugins only when runtime discovery or separate ownership justifies wiring and failure complexity. Keep a refactoring path rather than speculative infrastructure.
2. Spring's proxy intercepts external calls through the proxy; self-invocation calls `this` and bypasses advice. Verify actual transaction state and proxy type. Move the transactional operation to a separate focused bean, or restructure the public transaction boundary; avoid self-injection hacks. Add integration tests for rollback and document transaction ownership.
3. In-memory callbacks provide neither durable handoff nor exactly-once effects. Persist the aggregate change and outbox event atomically, publish asynchronously, and make consumers idempotent with deduplication keys. Define ordering, retry/dead-letter, schema evolution, tracing, and reconciliation. During incident mitigation, replay from durable state rather than trusting memory.
4. If metrics wrap each attempt or sit inside retry, they may report short attempt durations and successes while hiding total user latency and attempt amplification. Instrument both logical operation and individual attempts, with retry count and final outcome. Order timeout/retry/circuit/metrics intentionally, retry only idempotent operations within one deadline, and test outage behavior.
