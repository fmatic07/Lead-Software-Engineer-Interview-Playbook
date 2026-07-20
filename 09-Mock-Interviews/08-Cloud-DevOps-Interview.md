# 08 — Cloud & DevOps Interview

> AWS-focused cloud, containers, IaC, CI/CD, reliability, and security for enterprise Java deliveries.

---

## Interview Overview

| Attribute | Detail |
|-----------|--------|
| Duration | 45–60 minutes |
| Stack lens | AWS + Docker + K8s + Terraform + CI/CD |
| Bar | Production operations, not certification dumps |

---

## Purpose

Prove you can ship and run Spring services safely: immutable infra, progressive delivery, observability, and recovery.

---

## How Interviewers Evaluate

- Architecture fit on cloud primitives
- Security baseline (IAM least privilege, secrets, network)
- Reliability (SLOs, DR, multi-AZ)
- Automation maturity
- Cost and operability awareness

---

## Common Mistakes

- "We'll put it on Kubernetes" without why.
- Admin IAM for apps.
- No health/readiness distinction.
- DR as "we have backups" without restore tests.
- CI without supply-chain basics.

---

## Excellent Communication Techniques

Describe traffic path, failure domain, blast radius, and rollback in every design answer.

---

## Confidence Tips

One war story each: deploy failure, IAM incident, noisy neighbor, restore drill.

---

## Ideal Answer Framework

**Workload → Placement → Network/IAM → Data → Delivery → Observe → Failover → Cost**

---

## Topic Scripts

### AWS

**Interviewer:** Deploy a Spring Boot API on AWS for a bank-like workload. Sketch.

**Candidate:** Multi-AZ VPC; public ALB → private ECS/EKS; RDS Multi-AZ; ElastiCache if needed; Secrets Manager; CloudWatch/X-Ray or OTel; WAF optional; no SSH bastion without controls. Immutable AMIs/images; IaC everything.

**Follow-up:** EC2 vs ECS vs EKS?

**Senior:** ECS/Fargate for simpler ops; EKS when platform needs portability/complex scheduling.  
**Lead:** Choose based on team skills, compliance tooling, and platform strategy — not résumé.

---

### Docker

**Interviewer:** Hardening a Java container image — checklist?

**Candidate:** Distroless/JRE-slim; non-root; minimal layers; pin digests; multi-stage build; JVM flags for containers; health endpoints; no secrets in image; SBOM/scan in CI.

---

### Kubernetes

**Interviewer:** Pods crash looping after deploy. Triage?

**Candidate:** `kubectl describe/logs`; readiness vs liveness misconfig; config/secret missing; OOMKilled; dependency DNS; probe too aggressive; migrations. Roll back via rollout undo; fix pipeline.

**Follow-up:** HPA on CPU only — what's wrong?

**Answer:** For IO-bound Spring apps, CPU may stay low while latency burns; use custom metrics (RPS, queue depth, Hikari pending).

---

### Terraform

**Interviewer:** How do you manage Terraform state and blast radius?

**Candidate:** Remote state + locking; workspaces or separate state per env/account; plan in CI; least-privilege apply roles; module versioning; never manual prod clicks; policy-as-code optional.

---

### CI/CD

**Interviewer:** Design pipeline for Spring Boot to prod.

**Candidate:** PR checks (compile, unit, spotbugs/lint, tests); main → build image → scan → deploy to staging → integration/Testcontainers → manual/auto promote → prod canary/blue-green → smoke → rollback hooks. Separate credentials per env.

**Follow-up:** Database migrations in CD?

**Lead:** Backward-compatible expand/contract; migrate before app if needed; avoid lock-heavy DDL in peak; ownership clear.

---

### Monitoring

**Interviewer:** What do you alert on for a payments API?

**Candidate:** SLO burn (latency/availability); saturation (pool threads, DB CPU, queue lag); business (payment success rate); not raw CPU alone. Pages for customer-impacting; tickets for trends.

---

### Security

**Interviewer:** Secrets in Kubernetes for Spring?

**Candidate:** External secrets operator / Secrets Manager CSI; short-lived creds if possible; encrypt etcd; RBAC tight; no secrets in git; rotate; audit access.

---

### Reliability & DR

**Interviewer:** RPO/RTO for account service?

**Candidate:** Define with business (e.g., RPO 5m, RTO 1h). Multi-AZ baseline; cross-region replicas/backups for higher tiers; practice restore; runbooks; game days. Backups ≠ DR until restored.

---

## Rapid-Fire Bank (Cloud/DevOps)

1. Security groups vs NACLs?
2. ALB vs NLB for gRPC/WebSocket?
3. How does blue/green on ECS/K8s work?
4. Cluster Autoscaler vs Karpenter tradeoffs?
5. JVM memory vs container memory limits?
6. Cross-account IAM patterns?
7. Canary analysis signals you'd automate?
8. Chaos testing — what do you break first?
9. Cost spike after scale event — investigation?
10. Supply chain: signing images (cosign) worth it when?
11. Centralized logging PII redaction?
12. Spot instances for stateless workers — risks?
13. Terraform drift detection?
14. GitOps (Argo CD) vs pipeline apply?
15. Multi-region active-active pitfalls for stateful apps?

---

## Full Script — Production Incident

**Interviewer:** After Terraform apply, API 5xxs surge. You suspect SG change. Your moves?

**Candidate:** Rollback traffic (previous task set / undo); confirm error type; check recent changes (TF state, CW, deploy timeline); revert SG via TF; postmortem: require plan review, restricted apply, change windows, synthetic canaries before full shift.

---

## Evaluation Rubric

| Dimension | 1 | 3 | 5 |
|-----------|---|---|---|
| Cloud design | Buzzwords | Working sketch | Secure multi-AZ design |
| Delivery | Manual | Basic CI | Progressive + rollback |
| Reliability | Hope | Backups | Tested DR + SLOs |
| Security | Afterthought | Some IAM | Least privilege + secrets |
| Debug | Panic | Plausible | Systematic |

---

## Confidence Checklist

- [ ] Can whiteboard VPC + compute + data + CI
- [ ] Explain readiness vs liveness for Spring
- [ ] Migration strategy in CD
- [ ] One restore-test story
- [ ] IAM least privilege example

---

## Notes

<!-- Tie to Module 07 Architecture cloud docs -->
