# Senior Java System Design Reviewer

# Part 2 – Database Design, Transactions & Concurrency

This chapter is arguably **the most important** for backend interviews, especially at companies like **ING** and **Maya**.

If you demonstrate a strong understanding of transactions, locking, and database design, interviewers will immediately recognize that you've built production systems.

---

# 1. Database Design Fundamentals ⭐⭐⭐⭐⭐

## Goal

A database should be:

- Consistent
- Fast
- Easy to maintain
- Easy to scale
- Prevent duplicate or invalid data

---

## Example

Imagine we're designing a banking system.

### Users

```text
USER
----------------------
id
name
email
password
```

---



### Accounts

```text
ACCOUNT
----------------------
id
user_id
account_number
balance
status
created_at
```

Relationship

```text
User

1

↓

Many

↓

Accounts
```

---



### Transactions

```text
TRANSACTION
----------------------
id
from_account
to_account
amount
status
reference_no
created_at
```

Relationship

```text
Account

1

↓

Many

↓

Transactions
```

---



# Why separate tables?

Imagine

```text
USER

name

balance

transaction1

transaction2

transaction3
```

This becomes impossible to maintain.

Instead

```text
User

↓

Account

↓

Transaction
```

Normalized.

---



# 2. Database Normalization

Interviewers don't usually ask all the normal forms.

Instead understand the goal.

## Bad Design

```text
Orders

id

customer_name

customer_address

customer_phone
```

Every order repeats the same customer information.

---

Better

```text
Customer

↓

Orders
```

Less duplication.

Less inconsistency.

---

Interview Answer

> Normalization reduces duplicate data and improves consistency by storing each piece of information only once.

---



# 3. Primary Key vs Foreign Key

Primary Key

Uniquely identifies a record.

```text
User

id = 10
```

---

Foreign Key

Creates relationships.

```text
Account

user_id = 10
```

Meaning

This account belongs to User 10.

---



# 4. Indexes ⭐⭐⭐⭐⭐

One of the most common interview topics.

Without index

Searching

```sql
SELECT * FROM users
WHERE email='john@gmail.com'
```

Database scans

```text
1

2

3

4

5

...

1000000
```

Slow.

---

With index

```text
Email Index

↓

john@gmail.com

↓

Record Found
```

Much faster.

---

Example

```sql
CREATE INDEX idx_email
ON users(email);
```

---

When should you add indexes?

Usually

- Email
- Username
- Account Number
- Foreign Keys
- Frequently searched columns

---

Don't index everything.

Why?

Indexes

- Consume storage
- Slow INSERT
- Slow UPDATE

---

Interview Question

When would you avoid indexes?

Senior Answer

If the column changes frequently or is rarely searched.

---



# 5. Transactions ⭐⭐⭐⭐⭐

The heart of every banking interview.

Imagine

Frank transfers

₱1000

to John.

Steps

```text
Read Balance

↓

Deduct

↓

Add Balance

↓

Save Transaction

↓

Commit
```

---

Problem

What if

The server crashes

after deducting

but before adding?

Frank loses money.

John never receives it.

---

Solution

Transaction

```java
@Transactional
public void transfer(...)
```

Either

Everything succeeds

OR

Everything rolls back.

---

Interview Answer

Transactions ensure all operations complete successfully as a single unit. If one step fails, every change is rolled back.

---



# 6. ACID ⭐⭐⭐⭐⭐

Every senior Java interview.

---



## A

Atomicity

All or nothing.

```text
Deduct

×

Add

↓

Rollback
```

---



## C

Consistency

Database always remains valid.

Balance cannot become

```text
-5000
```

if business rules forbid it.

---



## I

Isolation

Transactions don't interfere.

Imagine

Two ATMs

withdraw

at the same time.

Isolation prevents corruption.

---



## D

Durability

After Commit

Even power outage

Data remains.

---

Easy Memory Trick


| Letter | Meaning                |
| ------ | ---------------------- |
| A      | All or Nothing         |
| C      | Always Valid           |
| I      | Independent            |
| D      | Permanent After Commit |


---



# 7. Isolation Levels ⭐⭐⭐⭐☆

Interviewers often ask:

"What isolation level would you use?"

---



## READ UNCOMMITTED

Allows dirty reads.

Almost never used.

---



## READ COMMITTED

Default for many databases.

Cannot read uncommitted data.

Good balance.

---



## REPEATABLE READ

Same transaction

Always reads same row.

Default in MySQL (InnoDB).

---



## SERIALIZABLE

Safest.

Also slowest.

Transactions execute almost sequentially.

---

Interview Answer

Most enterprise systems use **READ COMMITTED** or **REPEATABLE READ** because they balance consistency and performance. **SERIALIZABLE** is reserved for cases requiring the strongest guarantees, accepting lower throughput.

---



# 8. Concurrency Problems ⭐⭐⭐⭐⭐

Imagine

Balance

```text
1000
```

User A

withdraws

800

User B

withdraws

500

Both read

```text
1000
```

Result

```text
-300
```

Impossible.

---

Need locking.

---



# 9. Pessimistic Locking

Assume

Someone else

will modify the row.

Lock immediately.

```sql
SELECT *

FROM account

WHERE id=1

FOR UPDATE;
```

Other transactions wait.

---

Pros

Safe.

---

Cons

Can block other users.

Lower performance.

---

Use for

- Banking
- Wallets
- Payments

---



# 10. Optimistic Locking ⭐⭐⭐⭐⭐

Assume

Conflicts

are rare.

Entity

```java
@Entity
public class Account{

    @Version
    private Long version;

}
```

Flow

Transaction A

reads version

```text
5
```

Transaction B

also reads

```text
5
```

Transaction A

updates

Version

becomes

```text
6
```

Transaction B

tries update

Still has version

5

Spring throws

```text
OptimisticLockException
```

No data corruption.

---

Pros

Fast.

---

Cons

Need retry logic.

---

Use

- Inventory
- User Profile
- Booking Systems

---

Interview Question

Optimistic or Pessimistic?

Senior Answer

If conflicts are rare, Optimistic Locking provides better performance because it avoids holding database locks. If conflicts are frequent or data integrity is critical—such as financial transfers—Pessimistic Locking may be more appropriate.

---



# 11. Deadlocks

Imagine

Transaction A

locks

Account 1

Needs

Account 2

---

Transaction B

locks

Account 2

Needs

Account 1

Both wait forever.

---

Solutions

Always lock resources

in the same order.

Example

Always

Lowest ID first.

---



# 12. Idempotency ⭐⭐⭐⭐⭐

Huge topic in payment systems.

Imagine

Client

clicks Pay

twice.

Without protection

```text
Charge

Charge Again
```

Double payment.

---

Solution

Idempotency Key

```text
POST /payments

Idempotency-Key:

ABC123
```

Server remembers

ABC123

Second request

Returns previous result.

No duplicate payment.

---

Used by

- Stripe
- PayPal
- Maya
- Banking APIs

---



# 13. Soft Delete

Instead of

```sql
DELETE FROM users
```

Use

```text
deleted = true
```

Benefits

- Recovery
- Audit history
- Regulatory compliance

Very common in enterprise applications.

---



# 14. Auditing

Track

Who

changed

What

and

When

Spring Example

```java
@CreatedDate

@LastModifiedDate

@CreatedBy
```

Useful in

- Banking
- Healthcare
- Government
- Enterprise systems

---



# 15. Interview Scenario

Imagine the interviewer asks:

> Design a money transfer feature.

A strong answer might sound like this:

> "I'd model separate User, Account, and Transaction tables to keep the data normalized. The transfer operation would run inside a `@Transactional` service so debiting one account, crediting the other, and recording the transaction either all succeed or all roll back. To avoid race conditions during concurrent transfers, I'd use pessimistic locking for the account rows because financial data requires strong consistency. I'd also add indexes on account numbers and transaction timestamps for fast lookups, and every transfer would generate an idempotency key so duplicate client requests don't result in double charges."

Notice how that answer naturally touches on **database design, transactions, locking, indexing, and idempotency** without going into unnecessary detail.

---



# Senior Interview Cheat Sheet


| Topic            | What to Remember                                                          |
| ---------------- | ------------------------------------------------------------------------- |
| Normalization    | Reduce duplication and improve consistency                                |
| Primary Key      | Unique identifier                                                         |
| Foreign Key      | Table relationships                                                       |
| Index            | Speeds reads, slows writes                                                |
| Transaction      | All-or-nothing operation                                                  |
| ACID             | Atomicity, Consistency, Isolation, Durability                             |
| Isolation Levels | READ COMMITTED and REPEATABLE READ are common defaults                    |
| Optimistic Lock  | Uses `@Version`; best when conflicts are uncommon                         |
| Pessimistic Lock | Uses `SELECT ... FOR UPDATE`; best for high-conflict financial operations |
| Deadlock         | Prevent by acquiring locks in a consistent order                          |
| Idempotency      | Prevent duplicate processing of repeated requests                         |
| Soft Delete      | Mark records deleted instead of removing them                             |
| Auditing         | Record who changed what and when                                          |


---



## Common Senior-Level Questions

These are all realistic questions you may encounter:

1. What happens internally when Spring executes a `@Transactional` method?
2. When would you choose optimistic locking over pessimistic locking?
3. How can two users withdraw from the same account without corrupting the balance?
4. Why can too many indexes actually hurt performance?
5. What database isolation level does MySQL commonly use by default, and why?
6. How would you prevent duplicate payment requests if a client retries the same API call?
7. How would you design your database to support auditing and regulatory requirements?

If you can answer these comfortably and explain the trade-offs—not just the definitions—you'll be operating at the level expected of many Senior Java backend interviews.