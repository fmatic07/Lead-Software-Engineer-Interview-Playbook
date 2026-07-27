# Senior Java System Design Reviewer

# Part 3 – Scalability, Caching, Redis, Load Balancers & Scaling

This chapter answers one of the biggest questions in System Design interviews:

> **"What happens when your application suddenly has 1 million users?"**

A junior developer thinks:

> "The code still works."

A senior engineer thinks:

* Can the server handle it?
* Can the database handle it?
* Where is the bottleneck?
* What should be cached?
* Can we scale without downtime?

This is what scalability is about.

---

# 1. What is Scalability? ⭐⭐⭐⭐⭐

## Definition

Scalability is the ability of a system to handle increasing workloads while maintaining acceptable performance.

Imagine your Spring Boot API serves:

```
100 users
```

Everything is fine.

A month later

```
100,000 users
```

Now users experience

* Slow APIs
* Database timeouts
* High CPU usage
* Memory exhaustion

Scalability means preparing for this growth.

---

## Common Bottlenecks

```
               Client
                  │
                  ▼
           Spring Boot API
                  │
       ┌──────────┴──────────┐
       ▼                     ▼
   CPU Bottleneck      Memory Bottleneck
       │
       ▼
 Database Bottleneck
```

Interview Question

> Before scaling, what do you identify?

Senior Answer

> The bottleneck. Scaling should solve the actual constraint instead of simply adding more servers.

---

# 2. Vertical Scaling ⭐⭐⭐⭐☆

Also called

> **Scale Up**

Instead of adding servers

Upgrade one server.

Example

```
Before

4 CPU
8 GB RAM

↓

After

32 CPU
128 GB RAM
```

---

Advantages

✅ Easy

✅ No application changes

---

Disadvantages

❌ Expensive

❌ Hardware limits

❌ Single point of failure

---

Interview Question

Why can't companies just keep upgrading one server?

Answer

Every machine has physical limits. A single server also becomes a single point of failure.

---

# 3. Horizontal Scaling ⭐⭐⭐⭐⭐

Instead of upgrading one machine

Add more machines.

```
                Load Balancer
                     │
      ┌──────────────┼──────────────┐
      ▼              ▼              ▼
 Spring Boot     Spring Boot    Spring Boot
   Instance 1      Instance 2      Instance 3
```

Now requests are distributed.

---

Advantages

✅ Better availability

✅ Easier maintenance

✅ Unlimited growth

---

Disadvantages

Need

* Load Balancer
* Stateless APIs
* Distributed Cache

---

Interview Answer

Horizontal scaling is preferred in cloud-native applications because it improves availability and allows the system to grow by adding more instances instead of relying on a single powerful machine.

---

# 4. Stateless Applications ⭐⭐⭐⭐⭐

Horizontal scaling only works if servers are stateless.

Bad

```
User logs in

↓

Server A remembers session

↓

Next request goes to Server B

↓

User logged out
```

---

Good

```
Client

↓

JWT Token

↓

Any Server

↓

Authenticated
```

Every request contains authentication.

No server stores user state.

---

Interview Question

Why is JWT useful for scaling?

Answer

JWT keeps authentication information inside the token, allowing any server instance to validate requests without shared session storage.

---

# 5. Load Balancer ⭐⭐⭐⭐⭐

Without Load Balancer

```
Client

↓

Server
```

If the server crashes

Everything is down.

---

With Load Balancer

```
               Load Balancer
                    │
      ┌─────────────┼─────────────┐
      ▼             ▼             ▼
  Server A      Server B      Server C
```

The Load Balancer

* Receives requests
* Chooses a healthy server
* Returns responses

---

Benefits

* High Availability
* Better Performance
* Fault Tolerance

---

Interview Question

What happens if Server B crashes?

Answer

The load balancer stops routing traffic to Server B and continues sending requests to healthy instances.

---

# 6. Load Balancing Algorithms

### Round Robin

```
Request 1 → Server A

Request 2 → Server B

Request 3 → Server C

Repeat
```

Simple and common.

---

### Least Connections

```
Server A

5 users

Server B

100 users

↓

Next request

↓

Server A
```

Useful when request durations vary.

---

### Weighted

Powerful servers receive more traffic.

```
Server A

Weight = 4

Server B

Weight = 1
```

Server A receives roughly four times as many requests.

---

Interview Tip

You don't need to memorize every algorithm.

Knowing

* Round Robin
* Least Connections

is usually enough.

---

# 7. Caching ⭐⭐⭐⭐⭐

Imagine

```
Client

↓

API

↓

Database
```

Every request hits MySQL.

Expensive.

---

Instead

```
Client

↓

API

↓

Redis

↓

MySQL
```

Now

Frequently requested data

Never reaches the database.

---

Interview Definition

Caching stores frequently accessed data in faster storage to reduce database load and improve response times.

---

# 8. What is Redis?

Redis

(Remote Dictionary Server)

An

In-Memory Key-Value Store

Meaning

Everything lives in RAM.

```
User:5

↓

JSON Object
```

Retrieval

Almost instant.

---

Spring Boot Example

```java
@Cacheable("users")
public User findUser(Long id){
    return repository.findById(id).orElseThrow();
}
```

First request

```
Database
```

Second request

```
Redis
```

Much faster.

---

# 9. Cache-Aside Pattern ⭐⭐⭐⭐⭐

Most common caching strategy.

```
Client

↓

Spring Boot

↓

Redis?

↓

Yes

↓

Return

↓

No

↓

Database

↓

Store in Redis

↓

Return
```

This is what many Spring Boot applications implement.

---

Interview Question

What happens when data is not in Redis?

Answer

The application retrieves it from the database, stores it in Redis, and returns the result. Future requests use the cached value.

---

# 10. What Should Be Cached?

Good candidates

✅ User profiles

✅ Product catalogs

✅ Configuration

✅ Frequently viewed locations

✅ Search suggestions

For Lakbay

```
Municipalities

Travel Nodes

Popular Destinations

Terminal Information
```

Excellent Redis candidates.

---

Don't cache

❌ Bank balance

❌ Wallet balance

❌ Payment status

❌ Active transactions

Because stale data can cause incorrect business decisions.

---

# 11. Cache Invalidation

One of the hardest problems.

Imagine

```
Redis

User

Age

30
```

Database

```
Age

31
```

Users receive outdated data.

---

Solutions

### Time To Live (TTL)

```
5 Minutes

↓

Automatically removed
```

---

### Manual Eviction

```java
@CacheEvict("users")
```

Update database

↓

Remove cache

↓

Next request reloads data.

---

Interview Question

What is the biggest challenge in caching?

Senior Answer

Keeping cached data consistent with the source of truth while maximizing performance.

---

# 12. CDN vs Redis

Many candidates confuse them.

Redis

```
Application Data

↓

User

Orders

Profiles
```

CDN

```
Static Files

↓

Images

CSS

JavaScript

Videos
```

Different purposes.

---

# 13. Database Replication

One database eventually becomes overloaded.

Solution

```
             Application

                  │

          ┌───────┴────────┐

          ▼                ▼

Primary DB          Replica DB

(Read & Write)      (Read Only)
```

Writes

Go to Primary.

Reads

Can go to Replicas.

---

Benefits

* Better read performance
* Reduced database load

---

# 14. Read/Write Separation

```
GET /products

↓

Replica
```

```
POST /orders

↓

Primary
```

Very common architecture.

---

# 15. Auto Scaling

Cloud platforms

AWS

Azure

GCP

can automatically create more servers.

```
100 Users

↓

2 Servers

------------

10,000 Users

↓

12 Servers
```

No manual intervention.

---

# 16. Sticky Sessions

Some systems require

```
User

↓

Always Server A
```

Called

Sticky Session.

Problem

Harder to scale.

Modern APIs usually avoid this by using stateless authentication.

---

# 17. Interview Scenario

Suppose the interviewer asks:

> "Your Spring Boot application suddenly receives ten times more traffic. What would you do?"

A strong senior-level answer could be:

> "First, I'd identify the bottleneck using monitoring. If the database is overloaded due to repeated reads, I'd introduce Redis with a cache-aside strategy for frequently accessed data. I'd ensure the API remains stateless using JWT so additional application instances can be added behind a load balancer. If read traffic continues to grow, I'd introduce read replicas to offload queries while keeping writes on the primary database. Finally, I'd enable horizontal auto-scaling so the platform can add more application instances automatically as traffic increases."

Notice that answer demonstrates **analysis**, not just technology choices.

---

# 18. How These Concepts Fit Together

```
                    Internet
                        │
                        ▼
                 Load Balancer
                        │
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
   Spring Boot     Spring Boot     Spring Boot
    Instance A      Instance B      Instance C
        │               │               │
        └───────────────┼───────────────┘
                        ▼
                     Redis Cache
                        │
                        ▼
                Primary Database
                        │
                 Read Replicas
```

Flow

1. Load balancer distributes requests.
2. Any application instance can process them because the API is stateless.
3. Frequently requested data is served from Redis.
4. Cache misses go to the database.
5. Reads may use replicas, while writes always go to the primary.

---

# Senior Interview Cheat Sheet

| Topic              | Key Takeaway                                              |
| ------------------ | --------------------------------------------------------- |
| Scalability        | Handle increased workload without degrading performance   |
| Vertical Scaling   | Add more CPU/RAM to one server; simple but limited        |
| Horizontal Scaling | Add more servers; preferred for cloud systems             |
| Stateless API      | Required for efficient horizontal scaling                 |
| JWT                | Enables stateless authentication                          |
| Load Balancer      | Distributes traffic and improves availability             |
| Redis              | In-memory key-value store for fast application data       |
| Cache-Aside        | Check cache → DB on miss → update cache                   |
| TTL                | Automatically expires cached data                         |
| Cache Eviction     | Remove stale cache after updates                          |
| Database Replica   | Handles read-heavy workloads                              |
| Primary Database   | Processes writes                                          |
| Auto Scaling       | Automatically adjusts the number of application instances |

---

# Common Senior-Level Questions

These are very common interview questions:

### Q1. Why can't we cache everything?

Because not all data can tolerate staleness. Financial balances, payment status, and inventory often require the latest committed value, while user profiles or product catalogs are usually safe to cache.

---

### Q2. When would you choose vertical scaling instead of horizontal scaling?

For small or legacy systems where simplicity is more important than elasticity, or when an application cannot yet be distributed. However, horizontal scaling is generally preferred for modern cloud applications.

---

### Q3. Why is Redis faster than MySQL?

Redis stores data in memory (RAM), avoiding disk I/O and complex relational queries. This makes lookups extremely fast, though memory is more expensive than disk storage.

---

### Q4. What happens if Redis goes down?

The application should fall back to the database. Users may experience slower responses, but the system should continue functioning. In production, Redis is often deployed with replication and failover to reduce downtime.

---

### Q5. How do you know **what** to optimize first?

Don't guess. Measure. Use metrics (CPU, memory, database latency, request latency, cache hit ratio) to identify the real bottleneck before making architectural changes.

---

## Interview Advice

A common mistake is to answer every scaling question with "add Redis" or "add more servers."

A senior engineer thinks in this order:

1. **Measure** the bottleneck.
2. **Optimize** the existing system.
3. **Cache** repetitive reads when appropriate.
4. **Scale horizontally** if a single instance is insufficient.
5. **Distribute** traffic with a load balancer.
6. **Scale the database** (replicas, partitioning, etc.) only when it becomes the limiting factor.

That structured reasoning is often more impressive than listing technologies because it shows you make architectural decisions based on evidence rather than assumptions.
