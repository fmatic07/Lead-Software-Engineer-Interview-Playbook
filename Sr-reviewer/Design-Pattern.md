---

Spring Boot Design Patterns

A Practical Guide for Java Backend Developers


---

1. Dependency Injection (DI)

What is it?

Dependency Injection is a design pattern where an object's dependencies are provided from the outside instead of the object creating them itself.

In Spring Boot, the IoC (Inversion of Control) Container creates and manages objects (Beans) and injects them where needed.


---

The Problem

Without DI:

public class UserController {

    private UserService userService = new UserService();

}

Problems:

Tight coupling

Hard to test

Cannot easily replace implementations

Violates SOLID (Dependency Inversion)



---

Spring Solution

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}

Spring automatically injects the UserService.


---

Real World Example

Imagine building a restaurant.

Without DI:

The chef personally buys vegetables every morning.

With DI:

The supplier delivers fresh ingredients every day.

The chef only cooks.

The chef doesn't care where the ingredients came from.


---

Spring Boot Examples

@Service

@Repository

@Controller

Constructor Injection

@Autowired



---

When to Use

Always.

Almost every Spring Boot application relies on DI.


---

Interview Takeaway

> Constructor Injection is preferred because it creates immutable dependencies, improves testability, and clearly defines required dependencies.




---

2. Singleton Pattern

What is it?

Only one instance of an object exists throughout the application.

This is Spring's default bean scope.


---

Spring Example

@Service
public class UserService {

}

Only one UserService object is created.

Every controller shares it.


---

Real World Example

A company's HR department.

There is only one HR office.

Everyone goes to the same office.

You don't build a new HR office every time an employee has a question.


---

Benefits

Saves memory

Faster object reuse

Easy dependency sharing



---

Be Careful

Singleton beans must be stateless.

Avoid storing request-specific data.

❌ Bad

private User currentUser;

✔ Good

public User findUser(Long id)


---

Interview Takeaway

Spring Beans are Singleton by default.


---

3. Factory Pattern

What is it?

A Factory creates objects for you instead of you creating them manually.


---

Without Factory

UserService service = new UserService();


---

With Spring

UserService service =
context.getBean(UserService.class);

Spring creates it.


---

Real World Example

Buying a car.

You don't manufacture it yourself.

The factory does.

You simply request one.


---

Spring Examples

@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

Spring decides when to create it.


---

Benefits

Centralized object creation

Easier maintenance

Loose coupling



---

Interview Takeaway

The ApplicationContext is essentially a massive Factory.


---

4. Builder Pattern

What is it?

Builder simplifies creating complex objects with many optional fields.


---

Instead of

User user = new User();

user.setName("John");
user.setAge(25);
user.setAddress("Manila");

Use

User.builder()
    .name("John")
    .age(25)
    .address("Manila")
    .build();


---

Real World Example

Ordering coffee.

Choose:

Size

Milk

Sugar

Syrup

Ice


Instead of having dozens of constructors.


---

Spring Usage

Mostly with

DTOs

Response Objects

Configuration Objects

Entities



---

Benefits

Readable

Immutable

Cleaner code



---

Interview Takeaway

Builder eliminates telescoping constructors and improves readability.


---

5. Strategy Pattern

What is it?

A Strategy allows multiple algorithms to implement the same interface.

The application chooses which one to use at runtime.


---

Problem

Instead of

if(payment.equals("GCASH")){

}
else if(payment.equals("CARD")){

}

Create strategies.

public interface PaymentStrategy {

    void pay();

}

@Service
class GCashPayment implements PaymentStrategy

@Service
class CardPayment implements PaymentStrategy


---

Real World Example

Google Maps.

You choose:

Drive

Walk

Bike

Public Transport


Same destination.

Different strategy.


---

Spring Examples

PasswordEncoder

AuthenticationProvider

Payment Gateway

Notification Services



---

Benefits

No giant if-else blocks

Easy extension

Open/Closed Principle



---

Interview Takeaway

Strategy lets you add new behaviors without modifying existing code.


---

6. Proxy Pattern

What is it?

A Proxy sits between the caller and the real object.

It performs extra work before or after calling the real method.


---

Spring Example

@Transactional
public void saveOrder() {

}

What actually happens:

Client

↓

Spring Proxy

↓

Start Transaction

↓

Execute Method

↓

Commit/Rollback


---

Real World Example

A receptionist.

Visitors don't enter the CEO's office directly.

The receptionist checks appointments first.


---

Spring Features Using Proxy

@Transactional

@Cacheable

@Async

@PreAuthorize



---

Interview Takeaway

Most Spring AOP features are implemented using Proxies.


---

7. Template Method Pattern

What is it?

The framework handles repetitive steps while you provide only the custom logic.


---

Without JdbcTemplate

Open Connection

Create Statement

Execute Query

Read Result

Close Resources

With JdbcTemplate

jdbcTemplate.query(...)


---

Real World Example

A washing machine.

You press "Start."

The machine handles:

Fill water

Wash

Rinse

Spin


You only choose the program.


---

Spring Examples

JdbcTemplate

RestTemplate

RedisTemplate

KafkaTemplate



---

Benefits

Removes repetitive boilerplate code.


---

Interview Takeaway

Template Method standardizes common workflows while allowing customization.


---

8. Observer Pattern

What is it?

One object publishes an event.

Multiple listeners react independently.


---

Publisher

publisher.publishEvent(
    new UserCreatedEvent(user)
);

Listener

@EventListener
public void handle(UserCreatedEvent event){

}


---

Real World Example

You subscribe to a YouTube channel.

Whenever a new video is uploaded,

You receive a notification.

The creator doesn't know who is watching.


---

Spring Examples

Email Notifications

Audit Logs

Analytics

SMS

Activity Tracking



---

Benefits

Loose coupling.

Publisher doesn't know who receives the event.


---

Interview Takeaway

Observer enables event-driven architecture.


---

9. Repository Pattern

What is it?

The Repository hides database operations behind a simple interface.


---

Instead of writing SQL everywhere

entityManager.persist(user);

Simply write

interface UserRepository
extends JpaRepository<User, Long> {

}


---

Real World Example

A librarian.

You ask for a book.

The librarian knows where it is.

You don't search the shelves yourself.


---

Spring Examples

JpaRepository

CrudRepository

MongoRepository



---

Benefits

Cleaner Service Layer

Easier Testing

Database abstraction



---

Interview Takeaway

Repository separates business logic from persistence logic.


---

10. MVC Pattern

What is it?

Separates application responsibilities into three layers.

Client

↓

Controller

↓

Service

↓

Repository

↓

Database


---

Responsibilities

Controller

Receives HTTP requests.

Service

Contains business rules.

Repository

Handles database access.


---

Real World Example

Restaurant.

Waiter → Controller

Chef → Service

Pantry → Repository


---

Benefits

Clean architecture

Easier maintenance

Better testing



---

Interview Takeaway

Controllers should be thin. Business logic belongs in Services.


---

11. Adapter Pattern

What is it?

Converts one interface into another so incompatible systems can work together.


---

Real World Example

A travel adapter.

Your laptop charger doesn't fit a foreign outlet.

The adapter makes them compatible.


---

Spring Examples

HandlerAdapter

HttpMessageConverter

Third-party API integration



---

Interview Takeaway

Adapters allow existing code to work without modification.


---

12. Decorator Pattern

What is it?

Adds new functionality to an object without changing its original implementation.


---

Real World Example

Coffee.

Start with plain coffee.

Add:

Milk

Chocolate

Caramel


Each topping decorates the same coffee.


---

Java Example

InputStream

↓

BufferedInputStream

↓

DataInputStream


---

Spring Usage

Security filters

Request wrappers

Response wrappers


---

Interview Takeaway

Decorator extends behavior dynamically without inheritance.


---

13. Facade Pattern

What is it?

Provides one simple interface over multiple complex subsystems.


---

Instead of calling

Payment

Inventory

Shipping

Invoice

Email


Call

orderFacade.placeOrder();


---

Real World Example

Hotel Reception.

Instead of talking to housekeeping, billing, and room service separately,

You call the front desk.


---

Benefits

Simplifies APIs

Reduces client complexity



---

Interview Takeaway

Facade hides complexity behind one easy-to-use interface.


---

14. Command Pattern

What is it?

Encapsulates a request as an object.


---

Examples

CreateUserCommand

DeleteUserCommand

RefundOrderCommand


---

Real World Example

Restaurant order ticket.

The waiter writes your order.

The kitchen executes it later.


---

Spring Usage

Kafka messages

RabbitMQ

CQRS

Job Queues



---

Interview Takeaway

Commands make requests reusable, queueable, and auditable.


---

15. Chain of Responsibility

What is it?

A request passes through multiple handlers.

Each handler decides whether to process it or pass it along.


---

Spring Security

Incoming Request

↓

CORS Filter

↓

JWT Filter

↓

Authentication Filter

↓

Authorization Filter

↓

Controller


---

Real World Example

Airport Security.

Passport Check

↓

Security Scan

↓

Immigration

↓

Boarding Gate

Each station performs its task before allowing you to proceed.


---

Benefits

Flexible processing pipeline

Easy to add or remove handlers



---

Interview Takeaway

Spring Security's Filter Chain is the most common example of the Chain of Responsibility pattern.


---

Quick Interview Cheat Sheet

Pattern	Spring Boot Example	Real-World Example

Dependency Injection	Constructor Injection	Chef receiving ingredients from a supplier
Singleton	@Service Bean	One HR department
Factory	ApplicationContext	Car factory
Builder	Lombok @Builder	Custom coffee order
Strategy	PasswordEncoder, Payment Service	Google Maps route selection
Proxy	@Transactional, @Cacheable	Receptionist before the CEO
Template Method	JdbcTemplate	Washing machine program
Observer	ApplicationEventPublisher	YouTube subscribers
Repository	JpaRepository	Librarian finding books
MVC	Controller → Service → Repository	Restaurant waiter → chef → pantry
Adapter	HandlerAdapter	Travel power adapter
Decorator	BufferedInputStream	Coffee with toppings
Facade	OrderFacade	Hotel front desk
Command	Kafka/RabbitMQ messages	Restaurant order ticket
Chain of Responsibility	Spring Security Filter Chain	Airport security checkpoints



---

Final Tip for Senior Interviews

Don't just memorize the definitions. For every pattern, be ready to answer these four questions:

1. What problem does it solve?


2. Where have you used it in Spring Boot?


3. Why is it better than a simpler approach?


4. What are its trade-offs or limitations?



Being able to discuss those points—and relate them to real production experience—is what interviewers typically look for in Senior and Lead Java developers.