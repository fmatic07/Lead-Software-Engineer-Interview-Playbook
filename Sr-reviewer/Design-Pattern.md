Spring Boot Design Patterns – Senior/Lead Java Reviewer

This reviewer focuses on patterns you will actually encounter in Spring Boot applications, especially in interviews and enterprise systems.


---

1. What is a Design Pattern?

A design pattern is a proven solution to a recurring software problem.

It is not code.

It is a way of organizing code.

Example:

Instead of creating objects manually everywhere:

UserService service = new UserService();

Spring manages it.

@Autowired
private UserService service;

This is because Spring heavily uses the Dependency Injection pattern.


---

Spring Boot uses dozens of design patterns internally.

The important ones are:

1. Dependency Injection (IoC)


2. Singleton


3. Factory


4. Builder


5. Proxy


6. Strategy


7. Template Method


8. Observer (Event Driven)


9. Repository


10. MVC


11. Adapter


12. Decorator


13. Facade


14. Command


15. Chain of Responsibility



Master these and you'll understand almost every Spring Boot project.


---

2. Dependency Injection (Most Important)

Problem

Without DI:

public class UserController {

    private UserService service = new UserService();

}

Problems:

tightly coupled

impossible to mock

difficult testing

hard replacement



---

With DI

@RestController
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }
}

Spring injects it.

You never instantiate it.


---

Why?

Because Spring Container manages every bean.

Spring Container

   creates UserService

           ↓

injects into

UserController


---

Constructor Injection (Recommended)

@Service
public class UserService{

}

@RestController
public class UserController{

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

}

Advantages

immutable

easier testing

no nulls

official recommendation



---

Interview Question

Why constructor injection over field injection?

Answer:

immutable dependency

unit testing

dependency explicitly declared

easier maintenance

no reflection



---

3. Singleton Pattern

One object.

One instance.

Entire application.

Spring beans are Singleton by default.

@Bean

↓

created once

↓

shared everywhere

Example

@Service
public class UserService{

}

This object exists only once.

Every controller receives the same instance.


---

Changing scope

@Scope("prototype")
@Service

Now Spring creates a new instance every request.


---

Interview

Default bean scope?

Answer:

Singleton.


---

4. Factory Pattern

Instead of using

new UserService();

The factory creates it.

Spring itself is a gigantic Factory.

Example

ApplicationContext context;

UserService service =
context.getBean(UserService.class);

You never construct objects manually.

The ApplicationContext acts as the Factory.


---

Another example

@Bean
public PasswordEncoder encoder(){

    return new BCryptPasswordEncoder();

}

Spring calls this method.

Not you.


---

5. Builder Pattern

Used for complex object creation.

Instead of

User u = new User();

u.setName("John");
u.setAge(25);
u.setAddress(...);
u.setPhone(...);

Builder

User.builder()
    .name("John")
    .age(25)
    .address(...)
    .build();

Much cleaner.


---

Lombok

@Builder
public class User{

}

Spring developers use Builder constantly.

Especially

DTOs

Responses

Entities

Configurations


---

Interview

Advantages

immutable objects

readable

avoids telescoping constructors



---

6. Strategy Pattern

Probably the second most common.

Problem

Different payment methods.

Instead of

if(type.equals("GCASH")){

}
else if(type.equals("CARD")){

}
else if(type.equals("PAYPAL")){

}

Use strategies.

Interface

public interface PaymentStrategy{

    void pay();

}

GCash

@Service
public class GCashPayment
implements PaymentStrategy{

}

Card

@Service
public class CardPayment
implements PaymentStrategy{

}

Usage

strategy.pay();

No if statements.


---

Real Spring Example

Authentication Providers

Password Encoders

Message Converters

Validation

Serialization


---

Interview

Why Strategy?

Open/Closed Principle.

New payment?

Just add another implementation.

No existing code changes.


---

7. Proxy Pattern

Spring AOP is built on Proxy.

Suppose

userService.save();

Looks simple.

Actually

Controller

↓

Proxy

↓

Transaction

↓

Security

↓

Logging

↓

Caching

↓

Real Method

You are calling the proxy.

Not the real object.


---

Annotations using Proxy

@Transactional

@Cacheable

@Async

@Secured

@PreAuthorize

All Proxy based.


---

Interview

How does @Transactional work?

Spring creates a Proxy.

The proxy starts transaction before method.

Calls actual method.

Commits.

Rolls back if exception.


---

8. Template Method Pattern

Spring's JdbcTemplate.

Without

Connection

PreparedStatement

ResultSet

finally

close()

catch

Hundreds of lines.

With

jdbcTemplate.query(...)

Spring handles boilerplate.

You provide only

SQL

Mapper

Done.


---

Other examples

RestTemplate

RedisTemplate

KafkaTemplate

MongoTemplate


---

Interview

Why Template?

Removes duplicated boilerplate.


---

9. Observer Pattern

Spring Events.

Publisher

publisher.publishEvent(new UserCreatedEvent(user));

Listener

@Component
public class UserListener{

    @EventListener
    public void handle(UserCreatedEvent event){

    }

}

Publisher doesn't know listener.

Loose coupling.


---

Use Cases

Email

SMS

Audit

Notifications

Analytics

Logging


---

Interview

Difference between Observer and Strategy?

Strategy

Choose one behavior.

Observer

Notify many listeners.


---

10. Repository Pattern

Hide persistence logic.

Instead of

entityManager.persist(...)

Simply

interface UserRepository
extends JpaRepository<User,Long>{}

Spring generates implementation.

Amazing.


---

Interview

Benefits

abstraction

testing

cleaner services

separation of concerns



---

11. MVC Pattern

Spring MVC

Client

↓

Controller

↓

Service

↓

Repository

↓

Database

Controller

Handles HTTP.

Service

Business Logic.

Repository

Database.


---

Example

GET /users

↓

UserController

↓

UserService

↓

UserRepository

↓

MySQL


---

Interview

Never put business logic inside Controller.


---

12. Adapter Pattern

Convert incompatible interfaces.

Example

PaymentGatewayA

↓

Adapter

↓

Your Payment Interface

Spring Examples

HttpMessageConverter

HandlerAdapter

WebMvcConfigurer


---

13. Decorator Pattern

Add behavior without changing code.

Example

InputStream

↓

BufferedInputStream

↓

DataInputStream

Each wraps another.

Spring Security Filters also decorate requests.


---

14. Facade Pattern

Provide one simple interface.

Instead of

Payment

Inventory

Email

Shipping

Invoice

Client calls

OrderFacade.placeOrder();

Facade handles everything.

Very common in microservices.


---

15. Command Pattern

Encapsulate request.

Example

CreateUserCommand

DeleteUserCommand

UpdateUserCommand

Each command executes independently.

Useful

Queues

Kafka

RabbitMQ

CQRS


---

16. Chain of Responsibility

Huge Spring Security topic.

Incoming request

JWT Filter

↓

Cors Filter

↓

Authentication Filter

↓

Authorization Filter

↓

Exception Filter

↓

Controller

Each filter decides

Handle

or

Pass to next.

Exactly Chain of Responsibility.


---

Pattern Mapping Inside Spring Boot

Spring Feature	Pattern

@Autowired	Dependency Injection
@Service	Singleton
ApplicationContext	Factory
@Builder (Lombok)	Builder
@Transactional	Proxy
JdbcTemplate	Template Method
ApplicationEventPublisher	Observer
JpaRepository	Repository
Spring MVC	MVC
HandlerAdapter	Adapter
Security Filter Chain	Chain of Responsibility
PasswordEncoder implementations	Strategy
InputStream wrappers	Decorator
Service Facades	Facade



---

SOLID Principles in Spring Boot

Every design pattern works best when guided by SOLID.

S — Single Responsibility Principle (SRP)

Each class should have one reason to change.

// Good
@Service
public class UserService {
    public User create(UserDto dto) { ... }
}

@Component
public class EmailService {
    public void sendWelcomeEmail(User user) { ... }
}

Avoid mixing business logic, persistence, email, and validation in one class.


---

O — Open/Closed Principle (OCP)

Open for extension, closed for modification.

The Strategy Pattern is the classic example.

public interface PaymentStrategy {
    void pay();
}

@Service
public class CardPayment implements PaymentStrategy { ... }

@Service
public class GCashPayment implements PaymentStrategy { ... }

Adding a new payment method should not require changing existing implementations.


---

L — Liskov Substitution Principle (LSP)

Subtypes should be interchangeable with their parent type.

PaymentStrategy strategy = new CardPayment();
strategy.pay();

The caller should not care which implementation is used.


---

I — Interface Segregation Principle (ISP)

Prefer small, focused interfaces.

public interface EmailSender {
    void sendEmail(...);
}

public interface SmsSender {
    void sendSms(...);
}

Avoid large "god interfaces" that force implementations to provide unused methods.


---

D — Dependency Inversion Principle (DIP)

Depend on abstractions, not concrete implementations.

@Service
public class CheckoutService {

    private final PaymentStrategy paymentStrategy;

    public CheckoutService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}

CheckoutService depends on the PaymentStrategy interface rather than a specific payment class, making it easy to swap implementations and test with mocks.


---

Senior Interview Questions

Explain IoC and DI.

Answer: Inversion of Control means the framework controls object creation and lifecycle. Dependency Injection is the mechanism Spring uses to provide required dependencies to those managed objects.


---

Why is constructor injection preferred?

Makes dependencies explicit

Supports immutable fields

Easier unit testing

Avoids reflection-based field injection

Prevents partially initialized objects



---

Why is @Transactional implemented using proxies?

Spring wraps the target bean with a proxy that begins a transaction before invoking the real method, then commits or rolls back based on the outcome. This keeps transaction management separate from business logic.


---

Explain Strategy with a real example.

Different payment providers, notification channels, authentication mechanisms, or shipping calculators can implement a shared interface. The application chooses the appropriate implementation at runtime without changing client code.


---

What design patterns are used by Spring Boot?

A strong answer would include:

Dependency Injection (IoC)

Singleton

Factory

Builder

Strategy

Proxy

Template Method

Observer

Repository

MVC

Adapter

Decorator

Facade

Command

Chain of Responsibility



---

Senior-Level Tips

For Lead or Senior interviews, don't just name the patterns—connect them to real Spring Boot features. For example:

Dependency Injection: Constructor injection with @Service, @Repository, and @Controller.

Strategy: Multiple PaymentStrategy or NotificationService implementations selected dynamically.

Proxy: @Transactional, @Cacheable, @Async, and Spring Security method security.

Chain of Responsibility: The Spring Security filter chain processing authentication and authorization.

Observer: Domain events published with ApplicationEventPublisher and handled by @EventListener.

Repository: JpaRepository abstracting persistence.

Template Method: JdbcTemplate, RestTemplate, and other *Template classes that encapsulate boilerplate.

Builder: Immutable DTOs and configuration objects with Lombok's @Builder.


Being able to explain why Spring uses each pattern, what problem it solves, and where you've used it in production is what typically differentiates a Senior or Lead engineer from someone who has only memorized the definitions.