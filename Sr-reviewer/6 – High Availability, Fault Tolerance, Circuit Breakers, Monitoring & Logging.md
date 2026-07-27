# Senior Java System Design Reviewer

# Part 6 – High Availability, Fault Tolerance, Circuit Breakers, Monitoring & Logging

This chapter is what separates a **Senior Software Engineer** from someone who can only build features.

A junior developer asks:

> **"Does the feature work?"**

A senior engineer asks:

* What happens if the database dies?
* What if Redis crashes?
* What if Kafka is down?
* What if another service times out?
* How do we know production is failing?
* How do we recover?

This chapter is about **keeping systems alive.**

---

# 1. High Availability (HA) ⭐⭐⭐⭐⭐

## Definition

High Availability means

> The system remains available even when parts of it fail.

---

## Bad Architecture

```text
           Client
              │
              ▼
       Spring Boot API
              │
              ▼
            MySQL
```

Problem

If Spring Boot crashes

Everything is down.

---

## Better Architecture

```text
                  Client
                     │
              Load Balancer
                     │
      ┌──────────────┼──────────────┐
      ▼              ▼              ▼
 Spring A       Spring B       Spring C
      │              │              │
      └──────────────┼──────────────┘
                     ▼
                MySQL Cluster
```

One server dies?

Users barely notice.

---

Interview Question

How do you achieve High Availability?

Senior Answer

* Multiple application instances
* Load Balancer
* Database replication
* No Single Point of Failure

---

# 2. Single Point of Failure (SPOF)

A favorite interview topic.

Bad

```text
Client

↓

One Server

↓

One Database
```

If

either fails

Everything stops.

---

Better

```text
Client

↓

Load Balancer

↓

Multiple Servers

↓

Database Cluster
```

Remove SPOFs whenever possible.

---

# 3. Fault Tolerance ⭐⭐⭐⭐⭐

Definition

A system continues working

even when one component fails.

Example

Email service crashes.

Should users still

buy products?

Yes.

Email isn't critical.

---

Architecture

```text
Checkout

↓

Save Order

↓

Queue

↓

Email Failed

↓

Retry Later
```

Business continues.

---

Interview Answer

Fault tolerance means designing systems so failures are isolated and do not cause the entire application to fail.

---

# 4. Graceful Degradation

Imagine

Recommendation Service

is down.

Should users

still shop?

Yes.

Just hide recommendations.

---

Example

Normal

```text
Home Page

↓

Products

↓

Recommendations
```

Failure

```text
Home Page

↓

Products

↓

Recommendations Unavailable
```

Application still usable.

---

Interview Tip

Users prefer

limited functionality

over

total downtime.

---

# 5. Timeout ⭐⭐⭐⭐⭐

Imagine

Payment API

never responds.

Without timeout

```text
Client

↓

Wait...

↓

Wait...

↓

Wait...
```

Thread

never returns.

Eventually

all threads

become blocked.

---

Solution

```text
Timeout

↓

3 Seconds

↓

Fail
```

---

Spring Example

```java
WebClient.builder()
    .build()
```

Configure timeout.

Never wait forever.

---

Interview Question

Why are timeouts important?

Answer

Without timeouts, blocked threads accumulate, reducing throughput and eventually making the service unavailable.

---

# 6. Retry ⭐⭐⭐⭐⭐

Temporary failures happen.

Example

```text
Database Busy

↓

Retry
```

Often succeeds.

---

Retry Flow

```text
API Call

↓

Fail

↓

Retry

↓

Success
```

---

Bad Retry

```text
Retry Forever
```

Dangerous.

---

Good Retry

```text
Retry

3 Times

↓

Fail
```

---

Interview Tip

Retry only

temporary failures.

Don't retry

```text
401

Unauthorized
```

---

# 7. Exponential Backoff

Instead of

```text
Retry

1 sec

1 sec

1 sec
```

Use

```text
1 sec

↓

2 sec

↓

4 sec

↓

8 sec
```

Reduces pressure

on failing systems.

---

# 8. Circuit Breaker ⭐⭐⭐⭐⭐

One of the most common senior topics.

Without Circuit Breaker

```text
Client

↓

Service A

↓

Service B (Dead)

↓

Timeout

↓

Timeout

↓

Timeout

↓

Everything Slow
```

---

With Circuit Breaker

```text
Service B Dead

↓

Circuit Opens

↓

Fail Fast

↓

Fallback
```

Application

remains responsive.

---

Interview Definition

A Circuit Breaker prevents repeated requests to a failing service, allowing it time to recover while protecting the caller.

---

# 9. Circuit Breaker States

## Closed

Everything normal.

```text
Requests

↓

Service
```

---

## Open

Failures exceed threshold.

```text
Requests

↓

Rejected Immediately
```

No network call.

---

## Half Open

Try one request.

```text
Success?

↓

Yes

↓

Close Circuit

No

↓

Open Again
```

---

Easy Memory

| State     | Meaning       |
| --------- | ------------- |
| Closed    | Normal        |
| Open      | Stop Calling  |
| Half Open | Test Recovery |

---

# 10. Fallback

Service unavailable.

Instead of

```text
500 Error
```

Return

```text
Cached Result
```

or

```text
Recommendations unavailable.
```

---

Example

```text
Weather API Down

↓

Return Yesterday's Weather
```

Better

than crashing.

---

# 11. Bulkhead Pattern

Imagine

Notification Service

uses

100 threads.

Email hangs.

All threads blocked.

Everything else waits.

---

Better

Separate thread pools.

```text
Payment Pool

Notification Pool

Analytics Pool
```

One failure

doesn't consume

all resources.

---

# 12. Health Checks ⭐⭐⭐⭐⭐

Load Balancer

needs to know

which servers

are healthy.

Example

```text
GET

/actuator/health
```

Response

```json
{
 "status":"UP"
}
```

If

```text
DOWN
```

Load Balancer

stops routing traffic.

---

Spring Boot

```text
Spring Boot Actuator
```

Very common.

---

# 13. Liveness vs Readiness

Interview favorite.

Liveness

> Is the application alive?

Readiness

> Is it ready to receive traffic?

Example

Server booting.

Alive?

Yes.

Ready?

No.

---

# 14. Monitoring ⭐⭐⭐⭐⭐

If you don't measure

you don't know

what is happening.

---

Metrics

* CPU
* Memory
* Response Time
* Error Rate
* Request Count
* Database Connections
* Kafka Lag

---

Popular Tools

```text
Prometheus

↓

Grafana
```

---

Interview Question

Difference

Logging vs Monitoring?

Answer

Monitoring provides real-time metrics and alerts about system health, while logging records detailed events for troubleshooting and investigation.

---

# 15. Logging ⭐⭐⭐⭐⭐

Bad Logging

```java
System.out.println()
```

Production?

Never.

---

Use

```text
SLF4J

+

Logback
```

---

Example

```java
log.info("Transfer started");

log.error("Transfer failed");
```

---

# 16. Log Levels

| Level | Use                        |
| ----- | -------------------------- |
| TRACE | Detailed debugging         |
| DEBUG | Developer debugging        |
| INFO  | Normal business events     |
| WARN  | Unexpected but recoverable |
| ERROR | Failures                   |

---

Interview Tip

Production

Usually

```text
INFO

WARN

ERROR
```

---

# 17. What Should Be Logged?

Good

```text
User ID

Transaction ID

Endpoint

Duration

Status

Request ID
```

Bad

```text
Password

JWT

OTP

Credit Card

CVV
```

Never log secrets.

---

# 18. Correlation ID ⭐⭐⭐⭐⭐

Imagine

Microservices.

Request

travels

```text
Gateway

↓

Payment

↓

Notification

↓

Fraud

↓

Audit
```

How find

one request?

Assign

```text
X-Correlation-ID

ABC123
```

Every log

contains

ABC123.

Debugging

becomes easy.

---

Interview Tip

Correlation IDs are one of those details that often impress senior interviewers because they show experience with production troubleshooting.

---

# 19. Distributed Tracing

Instead of

isolated logs

```text
Service A

Service B

Service C
```

Tracing

shows

entire journey.

Popular

```text
OpenTelemetry

Jaeger

Zipkin
```

Example

```text
Request

↓

Gateway

↓

Payment

↓

Inventory

↓

Shipping
```

One timeline.

---

# 20. Alerts

Monitoring

without alerts

is useless.

Alert examples

* CPU > 90%
* Error Rate > 5%
* API Latency > 2 sec
* Database Down
* Kafka Consumer Lag High

Notify

* Slack
* Email
* PagerDuty

---

# 21. Complete Production Architecture

```text
                  Internet
                      │
                Load Balancer
                      │
      ┌───────────────┼───────────────┐
      ▼               ▼               ▼
 Spring A        Spring B        Spring C
      │               │               │
      └───────────────┼───────────────┘
                      ▼
                 Redis Cache
                      │
                      ▼
                MySQL Primary
                      │
                Read Replica
                      │
                      ▼
                    Kafka
               ┌──────┼──────┐
               ▼      ▼      ▼
           Email   Analytics Fraud

Monitoring:
Prometheus → Grafana

Logging:
SLF4J → Logback → ELK

Tracing:
OpenTelemetry
```

---

# 22. Failure Scenario

Interviewer

> Redis goes down.

Bad Answer

> The application crashes.

---

Senior Answer

> Redis should be treated as a performance optimization, not the source of truth. If Redis is unavailable, the application falls back to the database. Response times may increase, but the application should continue functioning.

---

Interviewer

> Kafka is down.

Senior Answer

> Critical synchronous operations such as committing a payment should still complete if possible. Event publication should be retried or handled through a reliable mechanism like the Outbox Pattern. Background processes such as notifications may be delayed but should not cause the core transaction to fail.

---

Interviewer

> Database is down.

Senior Answer

> Writes cannot continue because the database is the system of record. The application should fail gracefully, surface meaningful errors, trigger alerts, and rely on high-availability database configurations (replication, automatic failover, backups) to minimize downtime.

---

# 23. Production Readiness Checklist

Before deployment

Ask yourself

✅ Multiple Application Instances

✅ Load Balancer

✅ Health Checks

✅ Logging

✅ Monitoring

✅ Alerts

✅ Rate Limiting

✅ HTTPS

✅ Timeouts

✅ Retries

✅ Circuit Breakers

✅ Backups

✅ Database Replication

✅ Correlation IDs

✅ Metrics Dashboard

If you can answer "yes" to these,

you're approaching

production-grade architecture.

---

# Senior Interview Cheat Sheet

| Topic                | Key Takeaway                                              |
| -------------------- | --------------------------------------------------------- |
| High Availability    | Keep services online despite failures                     |
| Fault Tolerance      | Isolate failures so the system continues operating        |
| SPOF                 | Eliminate single points of failure                        |
| Graceful Degradation | Offer reduced functionality instead of failing completely |
| Timeout              | Prevent indefinitely blocked requests                     |
| Retry                | Recover from transient failures                           |
| Exponential Backoff  | Space retries progressively to reduce pressure            |
| Circuit Breaker      | Stop calling unhealthy dependencies temporarily           |
| Fallback             | Return an alternative response when dependencies fail     |
| Bulkhead             | Isolate resources to prevent cascading failures           |
| Health Check         | Report service health for orchestration/load balancers    |
| Monitoring           | Collect metrics and trigger alerts                        |
| Logging              | Record events for troubleshooting                         |
| Correlation ID       | Track a request across services                           |
| Distributed Tracing  | Visualize an end-to-end request path                      |

---

# Common Senior-Level Questions

### Q1. What's the difference between High Availability and Fault Tolerance?

* **High Availability** focuses on keeping the service accessible with minimal downtime.
* **Fault Tolerance** focuses on continuing correct operation even when components fail.

They complement each other but are not the same concept.

---

### Q2. Why is a Circuit Breaker better than endless retries?

Endless retries can overload an already failing dependency and consume application resources. A circuit breaker fails fast, allowing the dependency time to recover while keeping the calling service responsive.

---

### Q3. What would you do if an external payment provider takes 30 seconds to respond?

Configure a reasonable timeout, apply a circuit breaker, retry only transient failures with exponential backoff, and provide a meaningful error or fallback to the user if the dependency remains unavailable.

---

### Q4. Why do production systems need both logs and metrics?

Metrics tell you **that** something is wrong (for example, latency increased or error rates spiked). Logs help explain **why** it happened by showing detailed events and context. Traces connect those events across services to show where time was spent or where failures occurred.

---

### Q5. During an interview, how should you answer "What happens if this service fails?"

A strong pattern is:

1. **Detect** the failure (health checks, monitoring).
2. **Contain** it (timeouts, circuit breakers, bulkheads).
3. **Recover** if possible (retries, failover, fallback).
4. **Observe** it (logs, metrics, alerts).
5. **Restore** the service (replication, restart, disaster recovery).

This structured way of thinking demonstrates operational maturity—one of the strongest signals interviewers look for in a Senior Java Engineer.
