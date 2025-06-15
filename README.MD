# From Boilerplate Fatigue to Pragmatic Simplicity: My Experience Discovering Javalin

Three years ago, I graduated from college. Two and a half years ago, I started my first job as a Java developer. Interestingly, I didn’t actually learn Java at university. Most of my coursework was done in Python and C, with some optional classes in TypeScript. I mention this to highlight why I never fully internalized the so-called “best practices” commonly promoted—or even enforced—within the Java ecosystem. My background leaned more toward procedural programming, not object-oriented dogma.

That doesn’t mean object-oriented programming (OOP) is foreign to me—on the contrary. I learned Java and OOP on my own terms. I never forced myself to use design patterns that felt unnecessarily abstract or heavyweight. Instead, I gradually adopted the ones that made my code more organized, flexible, and readable—patterns like Singleton, Builder, Strategy, and Observer, as well as core OOP principles like composition over inheritance or encapsulation where used only after I fully understood their importance and that gave a logical idea of how and when to apply them properly.

When I began working professionally, Java 17 had just been released. Features like `var` felt completely natural to me, and Java Records seemed like the obvious choice for most data modeling tasks. Rather than manually creating boilerplate POJOs, I used Records to define concise and immutable data structures. I never extended `Thread` or implemented `Runnable` directly—concurrency was always something handled more elegantly using functional interfaces and lambdas. In general, many classic Java idioms felt outdated or unnecessary in my day-to-day work.

But then reality hit.

\
**The Disillusionment with “Good Practices”**

In the workplace, I quickly noticed a pattern: many of the so-called best practices were reduced to mechanical formalities. For example:



* Classes were filled with setters and getters—even when they didn’t enforce any invariants or perform validation. The result? "Private" fields behaved exactly like public ones. \

* Services were almost always written against interfaces, even when there was only one implementation. These interfaces weren’t even used for testing, where `Mockito` mocks dominated. \

* Builders were used on **mutable** classes, completely defeating the point of the Builder pattern. \


When I asked mentors and senior developers about these inconsistencies, I usually received disappointing answers. Some invoked the idea of “future-proofing,” even when the projected futures never materialized. Others justified the patterns as “standard Java conventions”—just like naming variables in `camelCase`. The more practical ones admitted that frameworks like Hibernate *require* accessors for performance and introspection. Fair enough—but if I’m not encapsulating behavior or validating input inside my data classes, am I really doing OOP, or just pretending?

At some point, I accepted that I had to conform to the practices my team and employer had adopted, even if they often felt more like ritual than reason. I wasn’t going to argue with the hand that feeds me.


### **Struggling With Frameworks**

As I became more involved with frameworks like Spring WebFlux, Spring MVC, and Quarkus, I began to understand their value in large-scale enterprise systems. Standardization can be crucial for onboarding and maintainability across teams. But I also came to realize something else: frameworks like these are effectively **languages in themselves**.

Dependency injection is handled almost entirely through annotations, and a non-trivial amount of logic is orchestrated invisibly by the framework. Developers must memorize an ever-growing catalog of annotations to get anything done, with much of the actual behavior occurring behind the scenes.

This leads to what I call *accidental complexity*. You’re not solving a domain problem—you’re solving a framework puzzle. The behavior is implicit, not explicit. Code is split across annotations, external config files, and framework conventions. For students or developers unfamiliar with the framework, onboarding becomes a game of reading the docs and deciphering magic.

Eventually, I grew disillusioned. It seemed like every problem required a massive framework solution, and every line of boilerplate was justified by doctrine. Even libraries like Lombok, which exist to reduce boilerplate, end up generating pseudo-OOP structures that obscure rather than clarify intent. To be clear Lombok is not a cure for the problem—it’s a treatment for a symptom. By making boilerplate more bearable, it can actually **perpetuate** the underlying issue: a dogmatic approach to OOP that prioritizes form over function. The real problem isn’t the verbosity itself, but the uncritical adherence to patterns and practices that have lost touch with their original purpose.


### **Enter Javalin**

Then I discovered **Javalin**—a lightweight (~8k LOC) web framework built as a thin layer on top of Eclipse Jetty. Jetty itself is a high-performance servlet engine known for its small footprint and efficiency, especially compared to alternatives like Tomcat or GlassFish. What makes Jetty even more compelling today is its compatibility with Java Virtual Threads, which makes it a great match for modern concurrency models.

Here’s what makes **Javalin** stand out for me:



* **No magic**: There’s no annotation-based trickery or hidden behavior. \

* **Code-first, code-only**: All configuration is done explicitly in code. What you see is exactly what runs. \

* **No enforced OOP**: You’re free to structure your application however you see fit—use OOP where it helps, or go functional/procedural when that’s more appropriate. \

* **Functional feel**: Thanks to handler-based routing, concise functional code feels natural. \

* **Built-in validation**: Javalin has a simple but powerful validation API, similar in spirit to middleware validation in ExpressJS. \

* **Embeddable by design**: It's easy to include Javalin in IoT services, CLI tools, or desktop apps, without dealing with DI containers or rigid application lifecycles. \


In contrast, frameworks like Spring or Quarkus are often so opinionated and heavyweight that they **insist** on being the center of your application. Embedding them in non-web-centric contexts is a painful endeavor.


### **Minimalism That Works**

Javalin’s development style will feel immediately familiar to anyone who has used ExpressJS or Go's Fiber framework. It’s pragmatic, minimalistic, and focused on getting things done—without ceremony or abstraction for its own sake. It’s also especially well-suited for students coming from JavaScript or Python backgrounds, where direct and straightforward coding is the norm. With Javalin, learners can focus on **understanding the Java language itself**, as well as the fundamentals of web and software development—**without needing to master a heavyweight framework like Spring Boot first**, which is often taught alongside Java in many modern courses. This lowers the barrier to entry and encourages a more incremental, practical learning path.

This makes it not only great for production systems, but also ideal for students. Instead of hiding logic behind annotation processors and "magic" defaults, Javalin keeps everything explicit. This transparency makes code easier to reason about, debug, and maintain. And it allows developers to introduce OOP principles **when they’re needed**, not before.


### **A Quick Example**

Here’s how simple it is to get started with Javalin:


```java
void main() {
    var app = Javalin.create(config -> {
        config.useVirtualThreads = true;
    }).start(8088);

    app.get("/hello", ctx -> ctx.result("Hello, world!"));

    app.post("/echo", ctx -> {
        var body = ctx.body();
        ctx.result("You said: " + body);
    });
}
```


What this snippet demonstrates:



* **Straightforward**: No annotations, no XML, no hidden config—just code. \

* **Explicit setup**: You can clearly see what’s being configured (e.g. virtual threads). \

* **Concise and readable**: HTTP endpoints are defined right where the server is created. \

* **Functional style**: Handlers are written as lambdas, making route definitions clean and expressive. \

* **Ready to run**: You can paste this into a file and have a running server immediately. \



### **Natural Growth, Not Premature Abstraction**

One of Javalin’s strengths is how naturally it allows an application to evolve as complexity increases. Starting from a single file with inlined handlers, it’s easy to scale up without being forced to re-architect everything. Here’s how that progression might look when we add database support and separate handler logic:


```java
public class DataBaseConfig {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/umbrella";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "user";
    private static final HikariDataSource dataSource;
    private DataBaseConfig() {}

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(64);
        dataSource = new HikariDataSource(config);
    }

    public static <T> T execute(Function<DSLContext, T> f) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            var dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            return f.apply(dslContext);
        }
    }
}
```


This utility encapsulates connection pooling and database access logic, without relying on annotations or Spring-style inversion of control. The database interaction is transparent and explicit, which provides a great learning opportunity—especially for students and self-taught developers. Instead of having the framework manage everything behind the scenes, you're forced to understand and implement key software engineering concepts yourself, such as **utility classes**, **the singleton pattern**, **generics, lambdas** and **safe connection handling**.

More importantly, it exposes the concept of **connection pooling** in a hands-on way. In real-world applications, opening a new connection to a database is expensive—it involves network latency, authentication overhead, and often resource locking on the server side. A connection pool like HikariCP maintains a set of pre-established connections and reuses them efficiently across multiple operations, drastically reducing overhead and increasing throughput. This is critical in any application that performs frequent interactions with external systems like databases, resources in external servers, etc.

By managing the pool explicitly, developers can fine-tune parameters such as maximum concurrent connections, idle timeouts, or statement caching—important skills when working on high-load or latency-sensitive systems. This level of visibility and control also helps reinforce responsible resource management, which is often abstracted away in high-level frameworks but an essential concept for writing robust and efficient applications.


```java
  public class UserHandlers {
    private static final ObjectMapper json = new ObjectMapper();

    public static void getAllUsers(Context ctx) throws SQLException {
        var users = DataBaseConfig.execute(
            dsl -> dsl.select(DSL.field("id"), DSL.field("name"))
                      .from("users"))
            .fetchInto(UserModels.User.class);

        ctx.json(Map.of("users", new ResponseModel<>(users, null)));
    }

    public static void createUser(Context ctx) throws JsonProcessingException, SQLException {
        var newUser = json.readValue(ctx.body(), UserModels.User.class);
        var res = DataBaseConfig.execute(dsl -> {
            try {
                var result = dsl.insertInto(DSL.table("users"))
                    .columns(DSL.field("email"), DSL.field("password"))
                    .values(newUser.email(), newUser.password())
                    .execute();
                return new ResponseModel<>(result, null);
            } catch (RuntimeException e) {
                return new ResponseModel<>(null, new Errors(List.of(e.getMessage())));
            }
        });

        if (res.errors() != null) {
            ctx.status(500).json(res.errors());
        } else {
            ctx.status(201).json(Map.of("message", "User created successfully"));
        }
    }
}


```



```java
void main() {
    var app = Javalin.create(config -> config.useVirtualThreads = true).start(8088);

    app.get("/hello", UserHandlers::getAllUsers);
    app.post("/echo", UserHandlers::createUser);
}
```


Despite having grown in functionality—using a connection pool, a PostgreSQL backend via JOOQ, and response models—the application **retains its clarity and minimalism**. There's no need for service layers, interface hierarchies, or complex configuration unless the problem domain demands them.


### **Organic Complexity.**

This example highlights how **structure and abstraction can emerge naturally** as the system grows, instead of being imposed prematurely. You don’t need to scaffold half a dozen layers just to handle a POST request. Instead, abstractions appear *only when and where they provide clear value*. And when that time comes, Java’s native support for modularity and encapsulation is more than sufficient.

The repository showcases straightforward examples of essential software design practices, such as:



* Performing basic validation using the Bean Validation API \

* Defining and handling custom domain-specific exceptions \

* Structuring and layering the application for separation of concerns \

* Implementing configuration management without relying on external frameworks \


Moreover, should the application requirements become more complex, it remains fully viable to integrate additional capabilities such as environment-based configuration, support for `.properties` or `.yaml` files, WebSocket communication, message queues, and more — all without sacrificing the clarity and simplicity established from the beginning.


### **Defying Conventions: Public Fields in Entities**

A widely accepted convention in object-oriented Java programming is that class fields should be encapsulated using private access modifiers, with controlled access provided through getter and setter methods. This approach is often taught as best practice to preserve invariants, support future-proofing, and enforce abstraction boundaries. Consequently, many tools and libraries such as Lombok have emerged to reduce the boilerplate that results from this pattern.

However, in the context of well-structured service applications — particularly those with clear architectural boundaries and data validation at the entry points — this convention can be reconsidered. Specifically, **using public fields in some data based classes such as DB entities is not only viable but can also improve clarity.**


```java
public class UserEntity {
    public Integer id;
    public String email;
    public String password;

    public UserModels.User mapToUserDTO(){
        return new UserModels.User(id, email, null);
    }
}
```



```java
void main(){
    var app = Javalin.create(javalinConfig -> {
        javalinConfig.useVirtualThreads = true;
        javalinConfig.router.apiBuilder(() ->{
            path("users", () ->{
                get("get-all", UserHandlers::getAllUsers);
                post("login", UserHandlers::getSingleUser);
                post("create", ctx ->{
                    UserValidators.userDtoValidator(ctx);
                    UserHandlers.createUser(ctx);
                });
                patch("update", ctx -> {
                    UserValidators.userDtoValidator(ctx);
                    UserHandlers.updateUser(ctx);
                });
            });
        });
}
```
```java
public class UserValidators {

    public static void userDtoValidator(Context ctx) {
        checkJsonIntegrity(ctx);
        ctx.bodyValidator(UserModels.User.class)
            .check(user -> user.email() != null && !user.email().isBlank(),"Email can't be null or blank")
            .check(user -> user.password() != null && !user.password().isBlank(), "Password can't be null or blank")
            .get();
    }

    private static void checkJsonIntegrity(Context ctx) {
        if(ctx.body().isBlank()) {
            throw new CustomExceptions.InvalidBodyException("Body can't be null");
        }
        try {
            var json = new ObjectMapper();
            json.readValue(ctx.body(), Object.class);
        } catch (JsonProcessingException e) {
            throw new CustomExceptions.InvalidBodyException(e.getMessage());
        }
    }
}

```

```java
public static void createUser(Context ctx) throws JsonProcessingException, SQLException {
        var body = ctx.body();
        var newUser = json.readValue(body, UserModels.User.class);
        var newUserHP = newUser.withHashedPassword();
        var res = DB.execute(dslContext -> UserRepository.createUser(dslContext, newUserHP));
        if(res.errors() != null){
            ctx.status(500).json(res.errors());
        }
        ctx.status(201).json(Map.of("Message", "User created successfully"));
    }
```

This entity exposes its fields directly without traditional getters or setters. The rationale behind this design is based on the principle of **boundary validation**: all input data is validated and normalized at the system’s edge (e.g., in a controller or request handler) before being mapped into the entity. By applying straightforward logical transitivity — if **input DTOs (A) are valid** then **intermediate DTOs (B)** are also valid thus **final entities (C)  are valid** — Consequently, the traditional use of getters and setters to guard internal state becomes redundant.

Further transformations and validations are typically — and ideally — performed within the service layer of the application. For example, operations such as arithmetic logic for debit and credit transactions are more appropriately handled outside the entity's internal boundaries. This design choice reinforces the notion that traditional setters and getters often add little value and can be safely omitted.

This is not to suggest that accessors are inherently problematic; in certain contexts — such as when designing a public-facing library or maintaining strict encapsulation across module boundaries — private or protected fields with accessors may be justified. However, the key point remains: unless the specific problem domain demands encapsulation mechanics, introducing them only adds cognitive noise and unnecessary complexity.


### **Final Thoughts**

Javalin has reminded me that Java doesn't have to be bureaucratic. You can write clean, maintainable, production-grade services without drowning in ceremony. While frameworks like Spring and Quarkus have their place—especially in large, standardized environments—it’s refreshing to see how powerful and expressive Java can be when you strip away the noise.

If you're tired of building applications that feel like framework configuration puzzles rather than software development, Javalin might be the breath of fresh air you need.
