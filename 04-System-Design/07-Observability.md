# Observability

> Evidence systems for diagnosis, SLO management, and incident leadership.

## Logging

### Explanation

Logs are discrete event records for debugging and audit. Structure them (JSON) with stable fields: `timestamp`, `level`, `service`, `env`, `traceId`, `spanId`, tenant/customer identifiers (careful with PII), and stable `error.code`. Prefer logs for exceptions and domain state transitions; do not log every happy-path body at high QPS—sample and push volume into metrics.

In Spring: Logback/Log4j2 + MDC for correlation; propagate context across `@Async`, WebClient, and messaging. Never log secrets, tokens, or full PAN/card data. Synchronous blocking appenders on the request thread become latency and outage amplifiers under ingest backpressure.

### Why interviewers ask it

- Tests distributed debugging without SSH folklore.
- Reveals privacy, cost, and signal-to-noise judgment.
- Separates “we have ELK” from actionable incident practice.

### Production examples

- MDC `traceId` joins gateway → order → payment during a payment timeout.
- Unstructured free text hides spikes in `PaymentDeclined` codes from automation.
- Debug left on in prod multiplies ingest cost and elevates GC/CPU on hot paths.

### Common mistakes

- Logging PII/secrets; stringifying bearer tokens.
- Relying on logs as the only golden signal.
- Non-canonical error codes (`null`, “error”, localized messages).
- Blocking logging without async appenders/backpressure policy.

### Senior Engineer discussion

Define a logging standard: levels, required fields, redaction, retention. Use metrics for rates/latencies; logs for narrative. Bound cardinality in indexed fields (no raw URLs with unbounded IDs). Prove correlation IDs survive thread hops (TaskDecorator / Micrometer context propagation) with integration tests.

### Lead Engineer discussion

Own org-wide logging contract and cost budgets with platform. Enforce PII scrubbing via templates and scanners. Standardize “first five minutes” queries by traceId and journey. Treat log pipelines as production systems with their own SLOs and on-call.

### Tradeoffs

- Verbose logs: faster debug, higher cost and privacy risk.
- Heavy sampling: cheaper, may miss rare failures.
- Centralized ingest: unified search, vendor lock-in and blast radius.
- Long retention: audit value, storage cost and legal exposure.

### Interview Challenge

1. p99 rises but error rate is flat; logs show nothing obvious. What next?
2. How do you stop a single service from blowing the org log budget?

### Suggested Answer

1. Do not drown in logs first. Check RED/USE metrics, saturation (pools, CPU, GC), and traces for slow spans. Enable targeted debug on canary/sampled traces. Fix the hot span; add a permanent structured log only if it earns its keep.
2. Quotas per service, sampling policies, drop debug in prod by default, cardinality lint on indexed fields, and chargeback dashboards. Page on ingest runaway like any capacity incident.

## Metrics

### Explanation

Metrics are aggregated numeric time series: counters, gauges, histograms/summaries. Golden signals: latency, traffic, errors, saturation. For Spring: Micrometer → Prometheus/OTLP. Histograms need buckets aligned to SLOs—averages lie while a thin tail burns the budget.

Cardinality is the main production killer. Unbounded labels (`userId`, raw URI, exception message) explode memory and cost. Use URI templates (`/orders/{id}`), bounded status classes, and exemplars to bridge into traces for detail.

### Why interviewers ask it

- Core SRE literacy expected of leads.
- Distinguishes vanity dashboards from SLO-aligned instrumentation.
- Probes histograms vs averages and label discipline.

### Production examples

- `http_server_requests` with templated URIs keeps series bounded.
- Mean latency healthy while histogram shows 1% at 30s—SLO burn invisible to the mean.
- Hikari pending threads / usage predict outages before 5xx spikes.

### Common mistakes

- Alerting on CPU alone without user-facing SLIs.
- High-cardinality labels.
- Only averages/medians; no percentiles or burn-rate views.
- Missing business KPIs (checkout conversion, payment success) for Tier-1 journeys.

### Senior Engineer discussion

Instrument RED at edges and USE for critical resources (JVM, pools, queues). Align histogram buckets to latency SLOs. Separate client vs server metrics. Version names carefully—renames break alerts. Require metrics for new endpoints in PR review.

### Lead Engineer discussion

Define a minimal production-eligible metric set. Review cardinality in architecture reviews. Tie metrics to error budgets and capacity planning. Fund business SLIs alongside technical ones; product and eng share one truth.

### Tradeoffs

- Fine-grained metrics: better diagnosis, higher cost/cardinality risk.
- Summaries: cheaper, less accurate quantiles and poor multi-instance merge.
- Push vs pull: different ops models and failure modes.
- More business metrics: better decisions, more interpretation disputes.

### Interview Challenge

1. Prometheus OOM after a release. Diagnose.
2. Design SLIs for a payments authorize API.

### Suggested Answer

1. Suspect cardinality explosion from a new label. Check sample count and TSDB stats; drop/revert the label; keep detail in traces/exemplars. Add CI allowlists and scrape budgets.
2. Success rate (approve+decline vs 5xx/timeout), authorize latency histogram vs SLO, dependency error rate, and saturation (pool/thread). Exclude expected business declines from “error” SLI or split them—do not mix fraud declines with outages.

## Tracing

### Explanation

Distributed tracing records causal request flows as spans across services. Propagate W3C Trace Context (or B3); OpenTelemetry is the current standard. Traces answer “where did time go?” and “which downstream failed?” Sampling is mandatory at scale: head-based, tail-based (keep errors/slow), or adaptive.

Spans need useful names and attributes without PII abuse. Auto-instrumentation is a start; business operations (authorizePayment, reserveInventory) need manual spans. Messaging must carry context in headers or the chain dies at the broker boundary.

### Why interviewers ask it

- Essential for microservice debugging interviews.
- Tests propagation, sampling, and instrumentation quality.
- Separates “we installed Jaeger” from usable traces.

### Production examples

- Trace shows gateway 20ms, order 40ms, inventory 2.8s—index miss confirmed.
- Missing context across `@Async`/Kafka loses the child chain.
- 0.1% head sample never captures a rare payment bug; tail sampling keeps errors.

### Common mistakes

- No propagation through message headers.
- Spans named only `HTTP POST` without route/operation.
- 100% sampling in prod without a cost plan.
- Treating traces as durable audit/compliance storage.

### Senior Engineer discussion

Instrument critical paths with OTel; verify HTTP, gRPC, and Kafka propagation. Use exemplars linking metrics→traces. Keep attributes low-cardinality. Create consumer spans linked to producers. Budget storage/retention separately from logs.

### Lead Engineer discussion

Mandate trace context in platform HTTP clients and messaging starters. Define sampling by tier. Ensure on-call can jump alert → trace → log in one workflow. Reject services that break propagation in readiness reviews.

### Tradeoffs

- High sampling: complete stories, high cost.
- Tail sampling: better signal, more collector complexity.
- Auto-instrumentation: speed, less business context than manual spans.
- Long retention: forensic value, expensive and often unused.

### Interview Challenge

1. Trace stops at order though payment was called. Why?
2. When is 100% tracing justified?

### Suggested Answer

1. Broken propagation: custom WebClient/RestTemplate without OTel/Micrometer instrumentation, executor without context, or headers stripped. Fix shared client config; contract-test outbound `traceparent`.
2. Low-QPS Tier-0 money paths, migration windows, or security forensics—with explicit cost and PII controls. Otherwise prefer tail/adaptive sampling.

## Monitoring

### Explanation

Monitoring continuously collects and visualizes health against expectations: dashboards, synthetics, dependency status, capacity views. Observability aims at novel failures; monitoring watches known modes. Both are required. Synthetics catch DNS/TLS/cert and regional path failures that internal metrics miss.

Dashboards are operator interfaces, not art. Journey views (login → checkout → pay) beat dozens of unowned per-service boards. Annotate deploys, flag changes, and config pushes or you will debug ghosts.

### Why interviewers ask it

- Evaluates operational maturity beyond coding.
- Probes whether dashboards map to user journeys.
- Distinguishes pretty graphs from decision-ready views.

### Production examples

- Journey dashboard with SLO burn overlays for checkout.
- Multi-region synthetics detect bad cert renewal before customers report.
- JVM board correlates GC pauses with latency; deploy markers explain the change.

### Common mistakes

- Per-service boards with no customer journey view.
- No deployment/event annotations.
- Ignoring consumer lag and DLQs.
- Screenshots as “docs” without runbook links.

### Senior Engineer discussion

Build tiered views: overview → service → dependency → instance. Include saturation and queues. Pair every Tier-1 dashboard with a runbook. Validate dashboards in game days—time-to-hypothesis is the metric.

### Lead Engineer discussion

Dashboard standards and ownership: service team owns content; platform owns templates. Monitoring is a release gate for new services. Align views with incident command needs—one glass pane for the journey owner.

### Tradeoffs

- Broad monitoring: coverage, sprawl and noise.
- Journey-centric: clearer UX impact, weaker alone for deep host issues.
- Synthetics: external truth, limited deep dependency visibility.
- Many data sources on one board: power, fragility.

### Interview Challenge

1. Design monitoring for a new payments service before first prod traffic.
2. What makes a dashboard “useless” in an incident?

### Suggested Answer

1. Define SLIs/SLOs; ship RED, resource USE, dependency breakers, lag if async, journey board shared with checkout; synthetics for health and dry-run authorize in lower envs; alerts + runbooks before launch.
2. No SLOs, no deploy markers, no next-click to traces/logs/runbooks, walls of unlabeled graphs, and ownership vacuum. Rebuild around symptoms and actions.

## Alerting

### Explanation

Alerts notify humans when action is required. Good alerts are symptomatic (user-facing SLO burn), not every cause. Prefer multi-window multi-burn-rate alerts; avoid raw threshold spam. Page sparingly; ticket/Slack for warnings. Every page needs owner, runbook, and urgency. Alert fatigue is itself an availability risk.

Cause alerts (CPU, disk) still matter for imminent hard failures, but they should not dominate the pager. Flapping and duplicate pages from gateway + every backend for one outage train people to ignore the pager.

### Why interviewers ask it

- Lead/Staff loops emphasize on-call quality.
- Tests judgment about 3am wake-ups.
- Links error budgets to organizational behavior.

### Production examples

- Page on checkout SLO burn rates; do not page on CPU>80% alone.
- Flapping readiness causes storms; fixed with `for` clauses and dependency isolation.
- “Log contains Exception” replaced by metrics on `payment_failed` by reason.

### Common mistakes

- Cause-only alerts without symptoms.
- No `for` duration; single-sample noise.
- Alerts without owners/runbooks.
- Duplicate pages across layers for one failure.

### Senior Engineer discussion

Prefer SLO burn and symptom alerts; keep a small infrastructure set for hard failures (disk full, cert expiry). Review pages weekly for actionability. Test alerts in staging. Practice silence hygiene during maintenance windows.

### Lead Engineer discussion

Own on-call health: pages/week, % actionable, MTTA/MTTR. Enforce runbook coverage. Drive error-budget policy (feature freeze when exhausted). Treat alert debt like code debt with explicit burn-down.

### Tradeoffs

- Sensitive alerts: early catch, more false pages.
- Symptom-only: better sleep, may miss early saturation—pair with capacity forecasts.
- Auto-remediation: faster recovery, unsafe-action risk.
- Dedup/grouping: less noise, risk of hiding distinct failures.

### Interview Challenge

1. Team gets 200 pages/week; most ignored. How do you lead the fix?
2. Write a burn-rate alert intent for “5% of checkouts >2s over 10m.”

### Suggested Answer

1. Measure actionable rate; run an amnesty sprint; delete/merge non-actionable; convert noise to tickets; move Tier-1 to SLO burn; require runbooks; cap pages/service/week as a quality bar; report fatigue as leadership risk until improved.
2. Latency histogram with 2s bucket; alert when fraction above 2s >0.05 for 10m plus a faster burn window; attach runbook for slow spans and dependency saturation; verify via load test.

## Prometheus

### Explanation

Prometheus is a pull-based TSDB with PromQL, service discovery, and alert rules. Exporters and `/metrics` expose samples; scrapes are periodic. Federation and remote write extend scale. It is not long-term analytics storage by default—use remote storage for retention. Spring Boot 3: Micrometer Prometheus registry + Actuator.

Failure modes matter: missing targets, empty data after label changes, single-replica gaps, and cardinality bombs. Monitor the monitor with `up` and dead-man switches.

### Why interviewers ask it

- De facto metrics stack in cloud-native Java interviews.
- Tests PromQL literacy and cardinality awareness.
- Probes scrape, HA, and monitoring failure modes.

### Production examples

- Kubernetes SD scrapes by annotation; mislabeled pods silently vanish.
- `histogram_quantile` over `le` for SLO dashboards.
- Single Prometheus loss → alert gap; HA pairs + Alertmanager or remote write.

### Common mistakes

- Using Prometheus as an event log (high-cardinality “metrics”).
- Alert rules without `for` or routing labels.
- Scrape intervals too aggressive for large fleets.
- Recording rules that explode series count.

### Senior Engineer discussion

Know scrape vs evaluation intervals, recording rules for expensive queries, and label allowlists. Follow metric naming (`_total`, units). Distinguish target down vs empty series. Keep Micrometer meters consistent across services for reusable PromQL.

### Lead Engineer discussion

Decide topology: per-cluster Prometheus + Thanos/Mimir vs managed. Set scrape budgets and retention. Encode Micrometer naming in engineering standards. Own Alertmanager trees and escalation documentation.

### Tradeoffs

- Pull model: simple target health, awkward short-lived jobs (Pushgateway carefully).
- Long retention in Prometheus: operational pain—remote write instead.
- More recording rules: faster dashboards, hidden coupling.
- Global federation: one pane, query fan-out complexity.

### Interview Challenge

1. Alert on “5% of checkout requests >2s over 10 minutes” in PromQL terms.
2. How do you HA Prometheus without duplicate pages?

### Suggested Answer

1. Instrument histogram including 2s; compute fraction above 2s via `le` buckets / total `rate`; alert `>0.05` for 10m (plus fast burn). Prefer SLO burn-rate formulation; use recording rules; load-test verification.
2. HA scrape pairs with identical rules; Alertmanager dedup/inhibition; or remote write to shared long-term store with one ruler. Always run a dead-man alert.

## Grafana

### Explanation

Grafana visualizes metrics, logs, traces (and profiles) with dashboards, variables, and alerting. Value comes from curated, opinionated boards linked to runbooks—not hundreds of unowned boards. Prefer Git-provisioned dashboards, folders, and permissions. Correlate time ranges across datasources during incidents.

Alerting strategy must not fork the truth: dual pages from Grafana and Prometheus on the same signal train people to ignore both.

### Why interviewers ask it

- Practical on-call tooling literacy.
- Tests whether candidates design for operators.
- Distinguishes sprawl from operational product thinking.

### Production examples

- Provisioned dashboards from Git; PR review for panel changes.
- Variables for `service`/`env`/`region`; shared JVM/Hikari library panels.
- Exemplars: red panel → Explore → Tempo traces.

### Common mistakes

- UI-only edits with no source control.
- Walls of graphs without SLOs/thresholds.
- Misleading Y-axes and mixed units.
- Dual alerting on the same signal from two systems.

### Senior Engineer discussion

Build interfaces: top symptoms, then causes. Consistent error/traffic colors. Deep links to traces, logs, runbooks. Use recording rules for heavy panels. Maintain a Tier-1 war-room journey board.

### Lead Engineer discussion

Treat Grafana as a product: ownership, provisioning, access control, periodic cleanup. Ban unowned boards after N days. Require a service dashboard template at launch. Align Grafana vs Alertmanager so pages have one source.

### Tradeoffs

- UI speed vs GitOps reliability.
- Multi-source boards: powerful, fragile to datasource outages.
- Self-hosted vs Grafana Cloud: control vs ops burden.
- Dense boards: more context, slower cognitive parse under stress.

### Interview Challenge

1. On-call says “Grafana is useless during incidents.” What do you change?
2. Should alerts live in Grafana or Prometheus/Alertmanager?

### Suggested Answer

1. Rebuild one journey board with burn, errors, latency, deps, deploys; add deep links; remove noise from defaults; practice in a game day; measure time-to-hypothesis before/after.
2. Prefer Prometheus/Alertmanager (or unified ruler) as source of truth for paging; Grafana for visualization and optional ticket-level unified alerting—avoid duplicate pages. Pick one paging path and document it.
