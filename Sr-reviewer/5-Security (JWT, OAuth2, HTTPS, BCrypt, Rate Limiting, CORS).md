# Senior Java System Design Reviewer

# Part 5 – Security (JWT, OAuth2, HTTPS, BCrypt, Rate Limiting & CORS)

> **Senior Engineer Mindset**
>
> Security is **not just login**.
>
> It's about protecting:
>
> - Users
> - Data
> - APIs
> - Servers
> - Infrastructure
> - Business

This is one of the favorite topics for **ING, Maya, GCash, banks, fintech, and enterprise companies.**

---

# 1. Security Layers ⭐⭐⭐⭐⭐

Think of security as multiple layers.

```text
                 Client

                    │

           HTTPS (Encrypted)

                    │

              Spring Security

                    │

          Authentication (JWT)

                    │

         Authorization (Roles)

                    │

          Input Validation

                    │

            Business Logic

                    │

          Database Security
```

If one layer fails,

another still protects the application.

---



# 2. Authentication vs Authorization ⭐⭐⭐⭐⭐

Interviewers LOVE this question.

## Authentication

> Who are you?

Example

```text
Username

Password
```

Server verifies identity.

---



## Authorization

> What are you allowed to do?

Example

```text
ADMIN

↓

Can Delete Users
```

```text
USER

↓

Cannot Delete Users
```

---

Easy Memory

Authentication

↓

Identity

Authorization

↓

Permission

---

Interview Question

Difference between Authentication and Authorization?

Senior Answer

Authentication verifies the identity of the user, while authorization determines what resources or actions that authenticated user is permitted to access.

---



# 3. HTTPS ⭐⭐⭐⭐⭐

Never send credentials over HTTP.

Bad

```text
Browser

↓

username

password

↓

Internet
```

Anyone intercepting traffic can read it.

---

HTTPS

```text
Browser

↓

Encrypted

↓

Server
```

Safe.

---

HTTPS Uses

- TLS
- SSL (legacy)
- Certificates

---

Interview Question

Why HTTPS?

Answer

It encrypts communication between client and server, protecting credentials, tokens, and sensitive data from interception or tampering.

---



# 4. Password Storage ⭐⭐⭐⭐⭐

Never

Ever

Store

```text
password123
```

inside the database.

Bad

```text
USER

password

password123
```

---

Instead

Store

```text
$2a$10$sdhsjfhsdfjksdf...
```

A BCrypt hash.

---



# 5. BCrypt ⭐⭐⭐⭐⭐

Spring Example

```java
PasswordEncoder encoder =
new BCryptPasswordEncoder();

String hash =
encoder.encode(password);
```

Login

```java
encoder.matches(rawPassword, hash);
```

Notice

You never decrypt the password.

You compare hashes.

---

Why BCrypt?

- Salt included automatically
- Slow by design
- Resistant to brute-force attacks

---

Interview Question

Why not SHA-256?

Senior Answer

SHA-256 is designed to be fast, which makes brute-force attacks more practical. BCrypt is intentionally slow and includes salting, making password cracking significantly more difficult.

---



# 6. JWT (JSON Web Token) ⭐⭐⭐⭐⭐

Probably the most asked Spring Security topic.

Flow

```text
Login

↓

Server

↓

JWT

↓

Client Stores Token

↓

Future Requests

↓

Authorization Header

↓

Server Validates
```

---

JWT Example

```text
Authorization:

Bearer eyJhbGciOi...
```

---

Inside JWT

```text
User ID

Username

Roles

Expiration
```

---

JWT is

NOT encrypted.

It is

Signed.

Meaning

Clients can read it

but cannot modify it.

---

Interview Question

Should passwords be inside JWT?

Absolutely not.

JWT should contain

identity

not secrets.

---



# 7. JWT Structure

```text
Header

.

Payload

.

Signature
```

Example

```text
xxxxx

.

yyyyy

.

zzzzz
```

---

Header

Algorithm

Payload

Claims

Signature

Verification

---

Interview Question

What happens if someone changes the payload?

Answer

The signature no longer matches, so the server rejects the token.

---



# 8. JWT Expiration

Never issue tokens forever.

Example

```text
Access Token

15 Minutes
```

```text
Refresh Token

7 Days
```

Flow

```text
Access Token Expired

↓

Use Refresh Token

↓

New Access Token
```

Much safer.

---



# 9. OAuth2 ⭐⭐⭐⭐⭐

JWT

≠

OAuth2

Many candidates confuse them.

---

OAuth2

Authorization Framework

JWT

Token Format

OAuth2

may use

JWT.

---

Example

```text
User

↓

Google Login

↓

Google

↓

Access Token

↓

Your Application
```

Your application

never sees

the password.

---

Interview Question

Difference between JWT and OAuth2?

Senior Answer

JWT is a token format used to carry authenticated user information. OAuth2 is an authorization framework that defines how applications obtain and use access tokens, which may or may not be JWTs.

---



# 10. Spring Security Flow

```text
Client

↓

Filter

↓

JWT Validation

↓

Authentication

↓

Controller

↓

Service

↓

Repository
```

Notice

Controller

never checks

JWT.

Spring Security

already did.

---



# 11. Role-Based Access Control (RBAC)

Example

```text
ADMIN

↓

Create User

Delete User

View Reports
```

---

```text
USER

↓

View Profile

Transfer Money
```

Spring

```java
@PreAuthorize("hasRole('ADMIN')")
```

Simple.

Powerful.

---



# 12. SQL Injection ⭐⭐⭐⭐⭐

Bad

```java
String sql =
"SELECT * FROM users WHERE username='"
+ username + "'";
```

Attacker enters

```sql
' OR 1=1 --
```

Everything returned.

---

Good

Spring Data JPA

Prepared Statements

```java
findByUsername(username)
```

Never concatenate SQL.

---

Interview Question

How does PreparedStatement prevent SQL Injection?

Answer

It sends SQL and parameter values separately, preventing user input from being interpreted as executable SQL.

---



# 13. XSS (Cross-Site Scripting)

Attacker stores

```html
<script>

stealCookie()

</script>
```

Browser executes it.

---

Protection

- Escape HTML
- Validate input
- Content Security Policy

---

Usually handled

by frontend frameworks

plus backend validation.

---



# 14. CSRF (Cross-Site Request Forgery)

User

already logged in.

Attacker tricks browser

into making

```text
POST /transfer
```

Protection

- CSRF Tokens
- SameSite Cookies

---

Interview Tip

If using

JWT

Stateless APIs

CSRF risk is typically reduced because credentials are not automatically attached by the browser the way cookies are.

---



# 15. CORS ⭐⭐⭐⭐⭐

Very common Spring question.

Browser

```text
Frontend

localhost:3000
```

Backend

```text
localhost:8080
```

Browser blocks.

Need

CORS.

---

Spring Example

```java
@CrossOrigin(
origins = "https://lakbay.com.ph"
)
```

or

Global Configuration.

---

Interview Question

Does CORS secure APIs?

Answer

No.

CORS is a browser security policy controlling which web origins can make cross-origin requests. It is not an authentication or authorization mechanism.

---



# 16. Rate Limiting ⭐⭐⭐⭐⭐

Suppose

One attacker sends

```text
100000 Requests
```

Server crashes.

---

Solution

Rate Limiting.

Example

```text
100 Requests

Per Minute

Per User
```

Extra requests

```text
429

Too Many Requests
```

---

Implementation

- Cloudflare
- API Gateway
- Nginx
- Spring Filters

---

Interview Tip

Since you've configured Cloudflare for your own APIs, mentioning **edge rate limiting** is a strong real-world answer because it blocks abusive traffic before it reaches the application.

---



# 17. API Keys

Sometimes

Instead of JWT

Systems use

```text
X-API-Key
```

Good for

- Internal Services
- Public APIs

Not

End-user authentication.

---



# 18. Secrets Management

Bad

```java
password = admin123
```

inside GitHub.

---

Better

```text
Environment Variables
```

or

```text
AWS Secrets Manager

Azure Key Vault

Vault
```

---

Interview Question

Should secrets be committed to Git?

Never.

---



# 19. Logging Security

Never log

```text
Password

JWT

Credit Card

CVV

OTP
```

Instead

Log

```text
User ID

Endpoint

Timestamp

Status

Duration
```

Enough

for debugging.

---



# 20. Complete Authentication Flow ⭐⭐⭐⭐⭐

```text
User

↓

POST /login

↓

Spring Security

↓

Verify Password

↓

BCrypt

↓

Generate JWT

↓

Client Stores JWT

↓

Future Requests

↓

Authorization Header

↓

JWT Validation Filter

↓

Controller

↓

Service

↓

Database
```

Notice

Password

only used

once.

JWT

used afterward.

---



# 21. Banking Example

Money Transfer

```text
Client

↓

HTTPS

↓

JWT Validation

↓

Role Validation

↓

Rate Limit

↓

Transfer Service

↓

@Transactional

↓

Database
```

If successful

```text
Kafka Event

↓

Notification

↓

Audit

↓

Fraud Detection
```

Security

exists

before

business logic.

---



# 22. Spring Security Best Practices

✅ Use Constructor Injection

✅ Stateless JWT

✅ BCrypt

✅ HTTPS

✅ Validate Input

✅ Global Exception Handler

✅ Least Privilege

✅ Rotate Secrets

✅ Access Token Expiration

✅ Refresh Tokens

✅ Rate Limiting

---



# 23. Common Security Mistakes

❌ Storing plain-text passwords

❌ Logging JWT tokens

❌ Hardcoding API keys

❌ Using HTTP in production

❌ Trusting client-side validation

❌ Allowing `*` for CORS in production

❌ Long-lived access tokens

❌ Returning stack traces to users

---



# 24. Security Comparison


| Technology          | Purpose                          |
| ------------------- | -------------------------------- |
| HTTPS               | Encrypt communication            |
| BCrypt              | Secure password hashing          |
| JWT                 | Stateless authentication token   |
| OAuth2              | Authorization framework          |
| Spring Security     | Authentication & authorization   |
| RBAC                | Role-based permissions           |
| Rate Limiting       | Prevent abuse                    |
| CORS                | Browser cross-origin policy      |
| Prepared Statements | Prevent SQL injection            |
| Input Validation    | Reject invalid or malicious data |
| Secrets Manager     | Secure storage for credentials   |


---



# Senior Interview Scenario

Interviewer

> **"How would you secure a banking API?"**

A senior-level answer:

> "I'd enforce HTTPS for all communication and use Spring Security with stateless JWT authentication. Passwords would be hashed with BCrypt and never stored in plain text. Authorization would be role-based, ensuring users can only access their own accounts or permitted administrative functions. I'd validate all incoming input and rely on parameterized queries or JPA to prevent SQL injection. To protect the API from abuse, I'd implement rate limiting at the edge using Cloudflare or an API gateway. CORS would be configured to allow only trusted frontend origins, and secrets such as database credentials and JWT signing keys would be stored in environment variables or a dedicated secrets manager rather than in source control."

That answer naturally covers multiple layers of security instead of focusing only on authentication.

---



# Senior Interview Cheat Sheet


| Topic              | Key Takeaway                                    |
| ------------------ | ----------------------------------------------- |
| Authentication     | Verify identity                                 |
| Authorization      | Verify permissions                              |
| HTTPS              | Encrypt client-server traffic                   |
| BCrypt             | Hash passwords securely                         |
| JWT                | Stateless authentication token                  |
| OAuth2             | Authorization framework                         |
| RBAC               | Control access based on roles                   |
| SQL Injection      | Prevent with prepared statements/JPA            |
| XSS                | Escape output and validate input                |
| CSRF               | Primarily protects cookie-based authentication  |
| CORS               | Browser cross-origin policy, not authentication |
| Rate Limiting      | Protect against abuse and denial-of-service     |
| Secrets Management | Keep credentials out of source code             |
| Logging            | Never log sensitive information                 |


---



# Common Senior-Level Questions



### Q1. Why use JWT instead of server sessions?

JWT enables stateless authentication, allowing any application instance behind a load balancer to authenticate requests without shared session storage.

---



### Q2. Why shouldn't JWTs contain sensitive information?

JWT payloads are only Base64 encoded, not encrypted. Anyone possessing the token can read its contents, even though they cannot modify it without invalidating the signature.

---



### Q3. Why do we need both HTTPS and JWT?

They solve different problems. HTTPS encrypts data while it's in transit. JWT authenticates the user and carries identity information. A JWT sent over plain HTTP could still be intercepted.

---



### Q4. Why isn't CORS a security mechanism?

CORS only controls how browsers enforce cross-origin requests. Attackers using tools like Postman or server-to-server requests are not restricted by CORS, so authentication and authorization are still required.

---



### Q5. Where would you implement rate limiting?

For internet-facing APIs, implement it as early as possible—typically at Cloudflare, an API gateway, or a reverse proxy like Nginx—to stop abusive traffic before it consumes application resources. Application-level rate limiting can provide an additional layer when needed.