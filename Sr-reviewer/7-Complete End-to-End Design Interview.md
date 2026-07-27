This is the chapter that interviewers are really testing.

They don't care if you know what Redis is or what Kafka is.

They want to know:

> **Can you combine everything into one production-ready architecture?**

Let's simulate a **real Senior Java interview** similar to **Maya**, **ING**, or any fintech company.

---

# Senior Java System Design Reviewer

# Part 7 – End-to-End System Design (Digital Wallet / Banking Transfer)

---

# Interview Question

> **Design a Digital Wallet like Maya or GCash that supports money transfers between users.**

---

# Step 1 — Clarify Requirements ⭐⭐⭐⭐⭐

**This is where many candidates fail.**

Don't immediately start drawing boxes.

Instead ask questions.

---

## Functional Requirements

The wallet should support

* User Login
* View Balance
* Transfer Money
* View Transaction History
* Notifications

---

## Non-Functional Requirements

The system should be

* Secure
* Highly Available
* Consistent
* Scalable
* Auditable
* Low Latency

---

Interview Tip

A senior engineer always asks questions first.

---

# Step 2 — Estimate Scale

Example assumptions

```
Users

10 Million

------------

Daily Transfers

5 Million

------------

Peak

5,000 TPS
```

Interviewers don't expect exact numbers.

They want to see

capacity planning.

---

# Step 3 — High-Level Architecture ⭐⭐⭐⭐⭐

```text
                     Mobile App
                          │
                    HTTPS + JWT
                          │
                    API Gateway
                          │
                 Load Balancer
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
 Spring Boot         Spring Boot      Spring Boot
 Instance A          Instance B       Instance C
        │                 │                 │
        └─────────────────┼─────────────────┘
                          │
                  Redis Cache
                          │
                   MySQL Primary
                          │
                  Read Replica
                          │
                        Kafka
      ┌────────────┬────────────┬────────────┐
      ▼            ▼            ▼            ▼
 Notification   Analytics    Fraud      Audit
```

Notice

Everything we've studied

is already here.

---

# Step 4 — API Design ⭐⭐⭐⭐⭐

Login

```
POST /login
```

Balance

```
GET /wallets/{id}
```

Transfer

```
POST /transfers
```

History

```
GET /transactions?page=0&size=20
```

---

Transfer Request

```json
{
   "fromWallet":1,
   "toWallet":2,
   "amount":1000,
   "reference":"ABC123"
}
```

---

Interview Tip

Always make transfer APIs

**idempotent**.

---

# Step 5 — Database Design ⭐⭐⭐⭐⭐

## User

```text
USER

id
username
email
password_hash
status
```

---

## Wallet

```text
WALLET

id

user_id

balance

currency

status
```

---

## Transaction

```text
TRANSACTION

id

reference_no

from_wallet

to_wallet

amount

status

created_at
```

---

## Audit

```text
AUDIT_LOG

id

user_id

action

timestamp
```

---

Indexes

```
wallet_id

reference_no

created_at
```

---

# Step 6 — Authentication

Flow

```text
Login

↓

BCrypt

↓

Generate JWT

↓

Client Stores JWT

↓

Authorization Header

↓

Spring Security Filter
```

HTTPS

required.

---

# Step 7 — Money Transfer Flow ⭐⭐⭐⭐⭐

This is the heart of the interview.

```
Client

↓

POST /transfer

↓

JWT Validation

↓

TransferController

↓

TransferService

↓

@Transactional
```

Business Logic

```
Validate Sender

↓

Validate Receiver

↓

Validate Amount

↓

Check Balance

↓

Lock Accounts

↓

Deduct Balance

↓

Add Balance

↓

Insert Transaction

↓

Commit
```

Only after

successful commit

do we publish

```
TransferCompleted
```

---

# Step 8 — Concurrency ⭐⭐⭐⭐⭐

Imagine

Two devices

transfer simultaneously.

```
Balance

1000
```

Phone A

```
Transfer 900
```

Phone B

```
Transfer 800
```

Without locking

Both succeed.

Impossible.

---

Solution

Pessimistic Lock

```sql
SELECT *

FROM wallet

WHERE id=1

FOR UPDATE;
```

Financial systems

prioritize

consistency

over

throughput.

---

# Step 9 — Transaction Management

Entire transfer

inside

```java
@Transactional
```

Either

```
Debit

Credit

Save Transaction
```

all succeed

or

everything rolls back.

ACID

protects money.

---

# Step 10 — Caching

Should we cache

wallet balance?

No.

Because

stale balances

are dangerous.

Good Redis candidates

```
User Profile

Exchange Rates

Configuration

Supported Banks

Branch Locations
```

---

# Step 11 — Messaging ⭐⭐⭐⭐⭐

After Commit

Publish

```
TransferCompleted
```

Kafka

```
TransferCompleted

↓

Notification

↓

Fraud Detection

↓

Analytics

↓

Audit
```

Transfer

doesn't wait.

---

# Step 12 — Security ⭐⭐⭐⭐⭐

Transfer

```
HTTPS

↓

JWT

↓

Role Validation

↓

Rate Limit

↓

Transfer
```

Passwords

```
BCrypt
```

Input

validated.

SQL Injection

prevented

using

Spring Data JPA.

---

# Step 13 — High Availability

```
Load Balancer

↓

Spring A

Spring B

Spring C
```

One server dies

Users continue.

---

# Step 14 — Fault Tolerance

Notification Service

fails.

Should

money transfer

fail?

No.

Retry later.

```
Transfer

↓

Success

↓

Kafka

↓

Retry Email
```

---

# Step 15 — Monitoring

Metrics

```
Transfer Rate

↓

Failure Rate

↓

Latency

↓

CPU

↓

Database Connections
```

Prometheus

↓

Grafana

---

Alerts

```
Transfer Failure >5%

↓

Notify DevOps
```

---

# Step 16 — Logging

Every transfer

logs

```
Transaction ID

User ID

Wallet ID

Duration

Status

Correlation ID
```

Never log

```
Password

JWT

OTP

PIN
```

---

# Step 17 — Failure Scenarios ⭐⭐⭐⭐⭐

## Database Down

```
Transfer

↓

Fail
```

Cannot continue.

Money

must remain

consistent.

---

## Redis Down

```
Fallback

↓

Database
```

Application

slower

still works.

---

## Kafka Down

Transfer

still commits.

Notification

delayed.

Retry publishing

or use an Outbox Pattern.

---

## Email Down

Transfer

still succeeds.

Retry later.

---

# Step 18 — Performance Optimizations

Use

```
Connection Pool

↓

HikariCP
```

Pagination

```
GET /transactions

?page=0

&size=20
```

Indexes

```
wallet_id

reference_no
```

Redis

only

for safe data.

---

# Step 19 — Deployment

```
GitHub

↓

CI/CD

↓

Docker

↓

Kubernetes

↓

Spring Boot Pods

↓

Load Balancer
```

Rolling Deployment

No downtime.

---

# Step 20 — Complete Architecture ⭐⭐⭐⭐⭐

```text
                     Mobile App
                           │
                     HTTPS + JWT
                           │
                     API Gateway
                           │
                    Load Balancer
                           │
       ┌───────────────────┼───────────────────┐
       ▼                   ▼                   ▼
 Spring Boot A      Spring Boot B      Spring Boot C
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                  Spring Security
                           │
                    Transfer Service
                           │
                    @Transactional
                           │
               Pessimistic Locking
                           │
                   MySQL Primary
                           │
                 Read Replica (History)
                           │
                         Kafka
      ┌───────────┬───────────┬───────────┬───────────┐
      ▼           ▼           ▼           ▼
 Notification  Analytics   Fraud      Audit
      │
      ▼
    Email/SMS

Redis
 ├── User Profile
 ├── Exchange Rates
 └── Configuration

Monitoring
 ├── Prometheus
 ├── Grafana
 └── Alerts

Logging
 ├── SLF4J
 ├── Logback
 └── Correlation ID
```

---

# ⭐ The Senior-Level Answer

Imagine the interviewer asks:

> **"Walk me through a money transfer."**

This is the answer I'd give.

---

> "The client first authenticates through Spring Security. During login, the password is verified using BCrypt, and if authentication succeeds, the server issues a short-lived JWT. Every subsequent request includes that JWT in the Authorization header over HTTPS.

> When the user submits a transfer request, the API Gateway forwards it to one of the Spring Boot instances behind the load balancer. Spring Security validates the JWT before the request reaches the controller.

> The `TransferService` executes the transfer inside a `@Transactional` method. It validates the sender, receiver, amount, and available balance. Since this is a financial operation, I would use pessimistic locking on the wallet records to prevent concurrent transfers from causing inconsistent balances.

> Within the same transaction, the sender's balance is debited, the receiver's balance is credited, and a transaction record is inserted. If any of these operations fail, the entire transaction is rolled back to maintain ACID guarantees.

> After the database transaction commits successfully, the service publishes a `TransferCompleted` event to Kafka. This ensures that non-critical work is asynchronous and does not delay the customer's response.

> Independent consumers subscribe to that event. The Notification service sends SMS and email confirmations, the Fraud Detection service performs risk analysis, the Analytics service updates reporting dashboards, and the Audit service records compliance information. If one of these downstream services fails, the transfer itself is not rolled back. Instead, retries or a dead-letter queue handle the failure independently.

> The application runs as multiple stateless Spring Boot instances behind a load balancer, enabling horizontal scaling. Redis is used only for cacheable data such as user profiles and configuration, not wallet balances. Monitoring through Prometheus and Grafana tracks latency, error rates, and throughput, while structured logs with correlation IDs allow requests to be traced across services."

---

# 🎯 What the interviewer is actually grading

Notice that one answer demonstrates almost every concept you've studied.

| Topic                     | Included? |
| ------------------------- | --------- |
| Layered Architecture      | ✅         |
| SOLID Principles          | ✅         |
| REST API Design           | ✅         |
| Authentication (JWT)      | ✅         |
| Authorization             | ✅         |
| HTTPS                     | ✅         |
| BCrypt                    | ✅         |
| Transactions              | ✅         |
| ACID                      | ✅         |
| Pessimistic Locking       | ✅         |
| Database Design           | ✅         |
| Indexing                  | ✅         |
| Redis                     | ✅         |
| Kafka                     | ✅         |
| Event-Driven Architecture | ✅         |
| Async Processing          | ✅         |
| High Availability         | ✅         |
| Fault Tolerance           | ✅         |
| Monitoring                | ✅         |
| Logging                   | ✅         |
| Scalability               | ✅         |
| Security                  | ✅         |

---

# ⭐ Final Interview Advice (Very Important)

After reviewing all seven parts, you'll notice something interesting:

**Senior Java System Design is not about memorizing technologies.**

Interviewers are evaluating whether you consistently think through this sequence:

```text
Requirements
      ↓
Architecture
      ↓
API Design
      ↓
Database Design
      ↓
Business Logic
      ↓
Security
      ↓
Transactions
      ↓
Concurrency
      ↓
Scalability
      ↓
Reliability
      ↓
Observability
      ↓
Deployment
```

If you follow that order during a system design interview, your explanation will sound organized, practical, and senior-level instead of jumping randomly between Redis, Kafka, JWT, and databases.

---

## I have one more chapter I'd strongly recommend.

Although we completed the system design reviewer, there's one area that often determines whether a candidate performs well in a live interview:

> **Part 8 – How to Answer Senior Java System Design Interviews**

Instead of new technical concepts, it would cover:

* How to structure your answer from start to finish.
* Common follow-up questions and how to respond.
* Trade-offs (Why Kafka over RabbitMQ? Why Redis? Why not cache balances?).
* Whiteboard drawing strategy.
* How to think out loud like a Staff/Senior Engineer.
* Real interview simulations from companies like ING, Maya, and other enterprise Java teams.

In my experience, that chapter often provides the biggest improvement because it teaches **how to communicate your knowledge**, not just how to acquire it.
