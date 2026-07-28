# Senior Java System Design Reviewer

# Part 4 – Messaging, Kafka, RabbitMQ, Event-Driven Architecture & Asynchronous Processing

This chapter separates many **mid-level developers** from **senior backend engineers**.

A junior developer thinks:

> "After saving the order, I'll send the email."

A senior developer thinks:

> "The customer shouldn't wait for an email to be sent."

That's where **messaging** comes in.

---

# 1. Synchronous vs Asynchronous ⭐⭐⭐⭐⭐

This is the foundation of everything.

---

## Synchronous

One task waits for another.

```text
Client

↓

Save Order

↓

Send Email

↓

Send SMS

↓

Generate PDF

↓

Return Response
```

Problem

If sending email takes

5 seconds

The client waits

5 seconds.

---



## Asynchronous

```text
Client

↓

Save Order

↓

Return Success

↓

Message Queue

↓

Email Worker

↓

SMS Worker

↓

PDF Worker
```

The client gets

```text
200 OK
```

almost immediately.

Background workers do the rest.

---

Interview Question

Why use asynchronous processing?

Senior Answer

> It reduces response time, improves user experience, isolates failures, and allows background work to scale independently.

---



# 2. What is a Message Queue?

Definition

A Message Queue temporarily stores messages until another service processes them.

Think of it as a delivery box.

```text
Producer

↓

Queue

↓

Consumer
```

The producer doesn't care

when

the consumer reads it.

---

Real-world analogy

Restaurant

```text
Customer

↓

Cashier

↓

Kitchen Ticket

↓

Chef
```

Cashier doesn't cook.

Chef doesn't take payments.

Each has one responsibility.

---



# 3. RabbitMQ

RabbitMQ is

A Message Broker.

Designed for

Reliable message delivery.

Architecture

```text
Spring Boot

↓

Exchange

↓

Queue

↓

Consumer
```

Producer sends

↓

Exchange

↓

Exchange decides

which queue receives the message.

---

Good For

- Email
- SMS
- Notifications
- Invoice Generation
- Background Jobs

---

Spring Example

Producer

```java
rabbitTemplate.convertAndSend(
    "email.queue",
    emailRequest
);
```

Consumer

```java
@RabbitListener(queues="email.queue")
public void sendEmail(EmailRequest request){

}
```

Very common in enterprise applications.

---



# 4. Kafka ⭐⭐⭐⭐⭐

Kafka is different.

RabbitMQ

Focuses on

Reliable message delivery.

Kafka

Focuses on

High-throughput event streaming.

---

Architecture

```text
Producer

↓

Kafka Topic

↓

Consumer Group

↓

Consumers
```

Notice

Topic

instead of Queue.

---

Kafka Example

```text
Transfer Completed

↓

Topic

↓

Fraud Service

↓

Analytics Service

↓

Notification Service

↓

Audit Service
```

One event.

Many consumers.

---

Interview Question

Kafka or RabbitMQ?

Senior Answer

RabbitMQ is ideal for task queues where one consumer processes one message reliably. Kafka excels at event streaming, where many independent services need to consume the same event and replay it if necessary.

---



# 5. Queue vs Topic ⭐⭐⭐⭐⭐

Queue

```text
Producer

↓

Queue

↓

Worker
```

Usually

One message

↓

One consumer.

---

Topic

```text
Producer

↓

Topic

↓

Service A

↓

Service B

↓

Service C
```

One event.

Many consumers.

---

Easy Memory Trick

Queue

> "Do this task."

Topic

> "This event happened."

---



# 6. Event-Driven Architecture ⭐⭐⭐⭐⭐

Instead of calling services directly

```text
Checkout Service

↓

Email Service

↓

Inventory Service

↓

Analytics Service

↓

SMS Service
```

Everything becomes tightly coupled.

---

Instead

```text
Checkout Service

↓

OrderPlaced Event

↓

Kafka

↓

Inventory

↓

Email

↓

SMS

↓

Analytics
```

Checkout doesn't know

who receives the event.

Very loosely coupled.

---

Interview Definition

Event-Driven Architecture is a design where components communicate by publishing and subscribing to events rather than directly calling each other.

---



# 7. Event Example

Customer places order.

Publish

```text
OrderPlaced
```

Consumers

```text
Inventory

↓

Reduce Stock
```

```text
Email

↓

Send Confirmation
```

```text
Analytics

↓

Update Dashboard
```

```text
Loyalty

↓

Award Points
```

Checkout code

never changes.

Just publish

```java
publish(OrderPlacedEvent);
```

---



# 8. Producer and Consumer

Producer

Creates messages.

Consumer

Processes messages.

Example

```text
Spring Boot API

↓

Producer

↓

Kafka

↓

Consumer

↓

Email Service
```

---

Interview Question

Can producers know who consumes messages?

Answer

No.

That's one of the biggest advantages.

They are completely decoupled.

---



# 9. Retry Mechanism ⭐⭐⭐⭐⭐

Imagine

Email Server

is down.

Without Queue

```text
Order

↓

Email Failed

↓

Everything Failed
```

Bad.

---

With Queue

```text
Order

↓

Success

↓

Queue

↓

Retry

↓

Retry

↓

Retry

↓

Email Sent
```

Users never notice.

---

Interview Question

What happens if the consumer crashes?

Answer

The message remains in the queue and is processed later, depending on the broker's acknowledgment and retry configuration.

---



# 10. Dead Letter Queue (DLQ)

Some messages

will never succeed.

Example

Invalid email address.

Without DLQ

```text
Retry Forever
```

Wasteful.

---

Instead

```text
Queue

↓

Retry

↓

Retry

↓

Retry

↓

Dead Letter Queue
```

Developers inspect later.

Very common interview topic.

---



# 11. Ordering

Imagine

Bank Transfer

```text
Withdraw

↓

Deposit
```

Order matters.

Kafka

Can preserve order

inside

one partition.

RabbitMQ

Can also preserve order

depending on configuration.

---

Interview Tip

Never assume distributed systems process everything in perfect order.

Ordering is a design decision.

---



# 12. At-Least-Once Delivery

Messaging systems often guarantee

```text
At Least Once
```

Meaning

Duplicate messages

are possible.

Example

```text
PaymentCompleted

↓

Network Failure

↓

Sent Again
```

Consumer receives

same message twice.

---

Need

Idempotency.

---



# 13. Idempotent Consumers ⭐⭐⭐⭐⭐

Imagine

Email

already sent.

Duplicate message arrives.

Consumer checks

```text
Already Processed?
```

Yes

↓

Ignore.

No duplicate email.

---

Interview Question

Why are idempotent consumers important?

Answer

Because distributed systems can deliver duplicate messages, consumers must safely handle repeated processing without producing incorrect results.

---



# 14. Saga Pattern (Basic)

Distributed transaction.

Example

```text
Order Service

↓

Payment Service

↓

Inventory Service

↓

Shipping Service
```

Traditional

```text
One Database Transaction
```

Impossible.

Different services.

Instead

Each service

has its own transaction.

If Shipping fails

Compensate.

```text
Cancel Payment

Restore Inventory
```

Very common

Microservices topic.

---



# 15. Choreography vs Orchestration

Choreography

```text
OrderPlaced

↓

Inventory

↓

Payment

↓

Shipping
```

Services react

to events.

No central coordinator.

---

Orchestration

```text
Orchestrator

↓

Inventory

↓

Payment

↓

Shipping
```

Central service

controls everything.

---

Interview Tip

Small systems

↓

Orchestration

Large event-driven systems

↓

Choreography

Both have valid use cases.

---



# 16. Real Banking Example ⭐⭐⭐⭐⭐

User transfers money.

Synchronous

```text
Transfer

↓

Email

↓

SMS

↓

Fraud Check

↓

Analytics

↓

Audit

↓

Response
```

Slow.

---

Better

```text
Transfer

↓

Commit Transaction

↓

Publish

TransferCompleted

↓

Return Success
```

Consumers

```text
Notification

↓

Send SMS
```

```text
Analytics

↓

Update Reports
```

```text
Fraud Detection

↓

Risk Analysis
```

```text
Audit

↓

Store Logs
```

Every service

works independently.

---



# 17. Spring Boot Example

Publishing Event

```java
applicationEventPublisher.publishEvent(
    new OrderPlacedEvent(order)
);
```

Consumer

```java
@EventListener
public void process(OrderPlacedEvent event){

}
```

Important

Spring Events

are useful

inside

one application.

Kafka/RabbitMQ

communicate

between

multiple applications.

---



# 18. Architecture Comparison

Traditional

```text
Client

↓

Spring Boot

↓

Email

↓

SMS

↓

Analytics

↓

Inventory
```

Many dependencies.

---

Event-Driven

```text
Client

↓

Spring Boot

↓

Kafka

↓

Email

↓

SMS

↓

Analytics

↓

Inventory

↓

Fraud
```

Spring Boot

doesn't know

who listens.

---



# 19. When Should You Use Messaging?

Use it for

✅ Email

✅ SMS

✅ Push Notifications

✅ Audit Logs

✅ Report Generation

✅ Analytics

✅ Video Processing

✅ Image Processing

✅ Fraud Detection

✅ Background Jobs

Avoid it for

❌ Login authentication

❌ Checking account balance

❌ Payment authorization that requires an immediate result

Those usually require synchronous responses because the caller is waiting for the answer.

---



# 20. RabbitMQ vs Kafka


| Feature            | RabbitMQ                                             | Kafka                                       |
| ------------------ | ---------------------------------------------------- | ------------------------------------------- |
| Primary Purpose    | Task Queue                                           | Event Streaming                             |
| Throughput         | High                                                 | Extremely High                              |
| Message Retention  | Removed after acknowledgment (typical configuration) | Retained for a configurable period          |
| Replay Messages    | Difficult                                            | Easy                                        |
| Multiple Consumers | Supported                                            | Excellent support with consumer groups      |
| Typical Use        | Email, background jobs                               | Analytics, event sourcing, activity streams |
| Ordering           | Queue-based                                          | Per partition                               |
| Best For           | Reliable task execution                              | High-volume event pipelines                 |


---



# Senior Interview Scenario

Interviewer

> "After a customer successfully transfers money, what happens?"

Poor Answer

> "I send the email."

Senior Answer

> "The transfer service commits the database transaction first. After a successful commit, it publishes a `TransferCompleted` event. Independent consumers subscribe to that event: the notification service sends an email and SMS, the fraud service performs risk analysis, the analytics service updates reporting, and the audit service records compliance information. Because these are asynchronous, the customer receives a response immediately while background processing continues independently."

This answer demonstrates

- Event-Driven Architecture
- Loose Coupling
- Asynchronous Processing
- Scalability
- Reliability

all in one explanation.

---



# Senior Interview Cheat Sheet


| Topic                     | Key Takeaway                                         |
| ------------------------- | ---------------------------------------------------- |
| Synchronous               | Caller waits for completion                          |
| Asynchronous              | Work continues in the background                     |
| Message Queue             | Buffers work between producers and consumers         |
| RabbitMQ                  | Reliable task processing                             |
| Kafka                     | High-throughput event streaming                      |
| Queue                     | One task → one worker                                |
| Topic                     | One event → many subscribers                         |
| Producer                  | Publishes messages                                   |
| Consumer                  | Processes messages                                   |
| Event-Driven Architecture | Components communicate through events                |
| Retry                     | Automatically reprocess transient failures           |
| Dead Letter Queue         | Stores permanently failed messages                   |
| Idempotency               | Safely handle duplicate deliveries                   |
| Saga                      | Coordinates distributed transactions across services |
| Spring Events             | In-process events within one application             |
| Kafka/RabbitMQ            | Communication across services                        |


---



# Common Senior-Level Questions



### Q1. Why not send emails directly after saving an order?

Because email delivery is relatively slow and can fail independently. Offloading it to a message queue keeps the user-facing request fast and isolates failures.

---



### Q2. What if Kafka or RabbitMQ is temporarily unavailable?

A production system should handle this gracefully—for example by retrying publication, using an outbox pattern, or surfacing an operational alert. The exact approach depends on how critical the event is.

---



### Q3. Why can consumers receive duplicate messages?

Most messaging systems favor reliable delivery over exactly-once delivery, so duplicates are possible during retries or network failures. Consumers should therefore be idempotent.

---



### Q4. When should you choose Kafka over RabbitMQ?

Kafka is generally a better fit when multiple independent services need to consume the same stream of events, when very high throughput is required, or when replaying historical events is valuable. RabbitMQ is often a better choice for distributing background work items to workers.

---



### Q5. If `TransferCompleted` triggers five downstream services and one fails, should the transfer roll back?

Usually, **no**. The financial transaction has already been committed. The failed downstream service should retry independently or move the message to a dead-letter queue if retries are exhausted. Rolling back a completed bank transfer because an email failed would create a much worse user experience and violate the principle of separating critical business transactions from auxiliary processing.