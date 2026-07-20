# Cloud Architecture

> Elastic capacity, packaging, orchestration, and infrastructure as product.

## High Availability

### Explanation

High availability is continued correct service despite component failure—within an explicit RTO/RPO envelope. Multi-AZ is the baseline for stateful and stateless tiers; multi-region is a product/business decision with data consistency and cost consequences. HA requires redundant instances, health-checked failover, dependency HA, and tested recovery—not only `replicas: 3`.

Data-plane HA without control-plane, identity, and DNS HA still fails customers. Single-AZ NAT gateways, shared Kafka clusters, or one Vault become silent correlated failure domains. Spring clients must treat failover as expected: transient SQL errors, brief DNS flips, and connection pool recovery.

### Why interviewers ask it

- Classic system-design pillar for lead roles.
- Distinguishes marketing “99.99%” from failure-domain reasoning.
- Probes DR drills, not just architecture diagrams.

### Production examples

- RDS Multi-AZ failover: brief errors; apps need retry on transient SQLState + Hikari recovery.
- Stateless pods across 3 AZs; single-AZ egress allowlist causes a “regional” outage.
- Redis primary dies; app degrades sessions per product rules instead of hanging requests.

### Common mistakes

- Single AZ “for cost” on Tier-1.
- Ignoring blast radius of shared dependencies.
- Untested failover; DNS TTLs that delay cutover.
- Confusing HA (stay up) with DR (recover from backup).

### Senior Engineer discussion

Map failure domains: instance, AZ, region, provider service, config. For each Tier-1 journey, state RTO/RPO and mechanism (LB, DB failover, queue replay). Design clients for transient errors. Prove with game days: kill AZ, revoke credentials, break DNS. Document degraded modes customers will see.

### Lead Engineer discussion

Set availability targets per criticality tier; refuse one-size SLOs. Budget multi-AZ as default cost of business. Require DR runbooks and last-test dates in service catalogs. Negotiate multi-region consistency tradeoffs with the business explicitly—do not hide them in eng folklore.

### Tradeoffs

- Multi-AZ: strong default HA, higher infra cost.
- Active-active multi-region: lowest RTO, hard data conflicts.
- Active-passive: simpler consistency, longer RTO and idle cost.
- More shared platform services: speed for teams, larger blast radius.

### Interview Challenge

1. Design HA for Spring order API + PostgreSQL + Redis session + Kafka.
2. Multi-AZ DB failover passes; customers still fail for 10 minutes. Why?

### Suggested Answer

1. Stateless API across ≥2 AZs behind ALB/NLB; PDBs. PostgreSQL Multi-AZ/Aurora with connection retry; Redis replication + clear session-loss behavior. Kafka RF/min ISR survive one AZ. No single-AZ admin dependency on the path. Test quarterly.
2. App connection pools pin dead connections; DNS/TTL delay; clients lack retry; downstream caches; or identity/TLS dependencies in one AZ. Fix client resilience, pool failover, and dependency map—not only the DB Multi-AZ checkbox.

## Load Balancer

### Explanation

Load balancers distribute traffic, often terminate TLS, health-check targets, and manage connections. L4 (NLB) vs L7 (ALB/API gateway): choose by protocol, routing needs, and latency. Sticky sessions are usually a smell—prefer externalized session state. Cross-zone balancing, draining, and slow-start matter during deploys.

Health checks are policy: too shallow routes to broken apps; too deep couples the LB to dependency outages and empties the target group. Coordinate LB deregistration with Kubernetes readiness and Spring graceful shutdown or you will ship intermittent 502s every release.

### Why interviewers ask it

- Everyday cloud design with subtle failure modes.
- Tests health checks, draining, and protocol awareness.
- Links directly to zero-downtime deploys.

### Production examples

- ALB path routing to order service; WebSocket idle timeouts need tuning.
- NLB for gRPC/HTTP2 or ultra-low latency; ALB for host/path rules + WAF.
- Health check on `/` depends on down DB → empty target group → total outage.

### Common mistakes

- Health check too shallow or too deep.
- No connection draining / `preStop` coordination.
- Sticky sessions causing uneven load and painful scale-in.
- One LB for hostile public and privileged admin traffic.

### Senior Engineer discussion

Align LB health with readiness. Set deregistration delay ≥ max request duration. Prefer L7 for HTTP auth/WAF routing; L4 when you need raw TCP/gRPC performance. Observe LB-generated 5xx vs target 5xx separately. Load-test rolling deploys, not only steady state.

### Lead Engineer discussion

Standardize LB patterns per exposure class (public, partner, internal). Own WAF/rate-limit policy at the edge. Encode draining and health best practices in Terraform modules so teams do not reinvent footguns.

### Tradeoffs

- L7: rich routing/security, slightly more overhead.
- L4: performance/simplicity, less application awareness.
- Cross-zone: even load, inter-AZ data transfer cost.
- Long drain: cleaner deploys, slower rollouts.

### Interview Challenge

1. Rolling deploy shows intermittent 502s. Explain and fix.
2. When do sticky sessions become acceptable?

### Suggested Answer

1. Targets leave before in-flight finish, or pods take traffic before warm. Coordinate deregistration delay, `preStop`, readiness/startup probes, Spring graceful shutdown. Canary and watch 502/target connection errors.
2. Rare legacy constraints (in-memory conversations you cannot externalize yet) with explicit migration plan. Prefer Redis/DB sessions. Stickiness must not be the architecture forever.

## Auto Scaling

### Explanation

Auto scaling adjusts capacity from signals: CPU, RPS, custom metrics (queue depth, p99, pending jobs), or schedules. Reactive scaling has lag; scheduled scaling handles known peaks. Scale-out is easier than safe scale-in. For Spring apps, warm-up (JIT, caches, pools) means new instances are not instantly full capacity.

Downstream limits cap useful scale-out: more pods can melt `max_connections`, Redis, or partner APIs. Model connection and rate budgets as hard constraints on `maxReplicas`. Flapping policies without stabilization windows create deploy-like instability all day.

### Why interviewers ask it

- Tests capacity thinking beyond “add replicas.”
- Reveals lag, flapping, and bottleneck-shift awareness.
- Connects autoscaling to cost and reliability.

### Production examples

- Scale on ALB requests/target and Kafka lag, not CPU alone for I/O-bound APIs.
- Scale-out multiplies DB connections and melts Postgres.
- Noisy CPU flapping fixed with longer windows and cooldowns.

### Common mistakes

- CPU-only policies for I/O-bound services.
- No connection budget per instance × max replicas.
- Scale-in killing in-flight work without draining.
- Assuming horizontal scale fixes single-threaded bottlenecks.

### Senior Engineer discussion

Identify the true bottleneck and scale on a metric tracking user pain. Cap max replicas by downstream capacity. Tune JVM heap under cgroup limits. Prefer queues when synchronous scale cannot absorb bursts. Prove HPA with production-like skew, not flat synthetic RPS.

### Lead Engineer discussion

Guardrails: max replicas, cost alerts, load-test gates before HPA on Tier-1. Teach connection/rate budget modeling. Review scaling incidents in ops reviews alongside features—elasticity is a product of discipline, not a checkbox.

### Tradeoffs

- Aggressive scale-out: latency headroom, cost and cold-start risk.
- Conservative: cheaper, more SLO burn during spikes.
- Schedule-based: predictable peaks, off-peak waste if over-provisioned.
- Custom metrics: better signal, more pipeline fragility.

### Interview Challenge

1. HPA scales 3→20 pods but p99 worsens. Why?
2. What metric would you use for a Kafka consumer service?

### Suggested Answer

1. Bottleneck shifted to DB/locks/Redis/partner limits. More pods → more connections/queries. Fix the bottleneck, set HPA max to proven safe capacity, add pools/bulkheads/caching/queues.
2. Consumer lag (and maybe processing time), not CPU alone. Scale to drain lag within SLO without exceeding DB/API budgets; include max replicas and rebalance cost awareness.

## Docker

### Explanation

Containers package app + runtime into immutable images. Distroless/minimal JRE bases reduce CVE surface; multi-stage builds keep Maven/JDK out of runtime. PID 1, signals, and graceful shutdown matter for Spring Boot—`SIGTERM` must reach the JVM for orderly drain. Never bake secrets into images; inject at runtime. Tag by digest/semver, not `latest`.

Memory is a joint contract between cgroup limits and JVM heap/metaspace/direct/native. “Heap is half the limit” folklore fails when Netty direct buffers and thread stacks dominate. Image provenance (SBOM, signing, scan gates) is part of release quality.

### Why interviewers ask it

- Baseline packaging skill for cloud-native Java.
- Probes image hygiene, security, and ops correctness.
- Distinguishes laptop Docker from production images.

### Production examples

- Multi-stage Maven → JRE runtime; CVE scan gates deploy.
- Fat jar with proper signal handling so shutdown hooks run.
- Dependency layer before source for faster CI cache hits.

### Common mistakes

- Running as root.
- Secrets in image ENV/`application.yml`.
- Shipping JDK + build tools in prod images.
- Ignoring CA/timezone trust until TLS fails in prod.

### Senior Engineer discussion

Golden base image with patched JVM, non-root user, optional agents. Explicit memory flags under cgroup constraints. Emit SBOM and scan in CI. Prefer reproducible builds. Verify graceful shutdown with connection draining tests.

### Lead Engineer discussion

Own base-image pipeline and patch cadence as a platform product. Ban `latest` in prod manifests. Provide Dockerfile templates for Spring Boot. Track MTTR for critical CVE remediation across the fleet.

### Tradeoffs

- Slim images: security/speed, harder ad-hoc debug (use ephemeral debug containers).
- Fat images: easier debug, larger attack surface.
- JVM in containers: density, noisier neighbors and memory tuning complexity.
- Strict scan gates: safer, can block urgent hotfixes without break-glass.

### Interview Challenge

1. Container OOMs though heap is 512m and limit is 1Gi. Diagnose.
2. How do you rotate a base image CVE across 80 services?

### Suggested Answer

1. Non-heap: metaspace, direct buffers, threads, native, agents. Measure RSS vs heap; adjust heap ratio/limits; reduce threads; check Netty direct usage; confirm cgroup visibility; NMT under load.
2. Rebuild golden base; automated PRs/version bumps via dependabot-like pipeline; staged rollout by tier; break-glass rebuild for critical CVEs; measure % fleet patched within SLA.

## Kubernetes

### Explanation

Kubernetes schedules containers: pods, Deployments, Services, Ingress, ConfigMaps/Secrets, HPA, PDBs, NetworkPolicies. Probes gate traffic and restarts: startup for slow boots, readiness for dependency fitness, liveness for true deadlocks—not for DB blips. Resource requests/limits drive scheduling and eviction; missing requests create noisy-neighbor chaos.

Operators extend the API for data planes, but managed cloud services often beat self-operated databases/messengers. Policy-as-code (OPA/Kyverno) encodes non-root, resource, and probe standards so YAML reviews are not the only control.

### Why interviewers ask it

- Default runtime for microservices in enterprise interviews.
- Tests probes, resources, rollouts, and failure semantics.
- Separates YAML familiarity from production judgment.

### Production examples

- RollingUpdate with careful surge/unavailable + PDB for Tier-1.
- Startup probe for slow Spring Boot; liveness kept cheap.
- Default-deny NetworkPolicy + allow DB/Kafka egress reduces lateral movement.

### Common mistakes

- Liveness including downstream calls → restart loops under dependency outage.
- No resource requests → unpredictable scheduling/evictions.
- Deployments for work that should be Jobs/CronJobs.
- Config only in images; no ConfigMap/Secret discipline.

### Senior Engineer discussion

Design probes correctly; set CPU/memory from load tests; use topology spread across AZs. Sidecars/mesh have a complexity tax—adopt deliberately. Debug with events, describe, logs, traces—not only `exec`. Prove PDBs and drains during disruptive node upgrades.

### Lead Engineer discussion

Golden Helm/Kustomize charts: probes, PDB, resources, securityContext, NetworkPolicy. Gate prod with policy-as-code. Decide on-cluster vs managed services. Measure cluster cost and reliability as a platform product with SLOs.

### Tradeoffs

- More replicas/PDBs: resilience, cost.
- Strict network policies: security, ops friction.
- Operators for data: convenience, heavy ownership vs managed SaaS.
- Mesh: uniform policy, opaque debugging and resource overhead.

### Interview Challenge

1. Pods restart every few minutes under dependency latency. What’s wrong?
2. How do you run a zero-downtime Spring Boot rollout on EKS?

### Suggested Answer

1. Liveness includes downstream calls or timeouts too aggressive; kubelet kills slow-but-alive pods, reducing capacity. Move deps to readiness; keep liveness cheap; fail fast in handlers via timeouts/breakers.
2. Readiness/startup probes, graceful shutdown, PDB, surge settings, LB deregistration delay, `preStop`, canary/wave by percentage, watch error budget during rollout, automatic rollback on SLO burn.

## Infrastructure as Code

### Explanation

IaC defines infrastructure in versioned, reviewable code (Terraform, CloudFormation, Pulumi, Crossplane). Desired state is applied continuously; drift is detected. Remote state, locking, modules, and policy checks are mandatory at scale. App CI/CD and infra CI/CD must coordinate contracts (IAM roles, DNS, queues) without ticket-driven snowflakes.

ClickOps creates invisible truth that the next apply destroys—or worse, never reproduces after an incident. Break-glass exists for emergencies; permanent console drift is a process failure. State blast radius matters: one mega-state for the org is an outage waiting to happen.

### Why interviewers ask it

- Lead engineers treat infra as software.
- Tests safety: plan/apply, blast radius, secrets, state.
- Distinguishes ClickOps heroes from sustainable platforms.

### Production examples

- Terraform module for Spring service: IAM/IRSA, ECR, ALB rules, alarms.
- PR plan in CI; apply on merge with prod approvals.
- Drift detection finds a security group opened to `0.0.0.0/0`.

### Common mistakes

- Local state files; no locking.
- Giant monolithic state for the whole org.
- Secrets committed to VCS.
- Apply without plan review; unprotected destroys of stateful resources.

### Senior Engineer discussion

Split state by blast radius (network, data, app). Least-privilege CI roles. Versioned modules. Preventative policies (deny public buckets). Protect destroys on databases. Document import/brownfield. Make `terraform plan` a readable artifact for reviewers, not noise.

### Lead Engineer discussion

Build an IDP paved road: modules, environments, promotion. Change management for prod applies. Measure lead time for infra changes and incident causation from manual changes. Train teams; revoke standing console write where IaC is source of truth—with documented break-glass.

### Tradeoffs

- Strict GitOps: auditability, slower emergencies—need break-glass.
- Fine-grained states: safer applies, more wiring complexity.
- High module abstraction: team speed, leaky edges.
- Policy-as-code: consistent safety, false positives slowing delivery.

### Interview Challenge

1. Prod outage needs an emergency security group change; IaC will overwrite it. Process?
2. How do you structure Terraform state for 50 microservices?

### Suggested Answer

1. Break-glass with logged approval; fix immediately; mirror into IaC in the same window; plan/apply to clear drift; postmortem with module defaults and a faster paved-road change path. Never leave console drift as permanent truth.
2. Shared modules; per-service or per-domain state for app resources; separate network/data states; env separation; remote state + locking; CI plan on PR. Avoid one state file for everything; avoid one state per tiny resource that cannot be reasoned about.
