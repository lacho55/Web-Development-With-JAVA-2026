# 🍃 Spring Boot — Project Creation & Bean Management Guide

> A hands-on guide covering everything you need to know about creating Spring Boot projects and mastering Spring's bean lifecycle. Work through the tasks in order!

---

## 📚 Table of Contents

1. [What is Spring Boot?](#what-is-spring-boot)
2. [Project Creation](#project-creation)
3. [Project Structure](#project-structure)
4. [Application Entry Point](#application-entry-point)
5. [Configuration & Profiles](#configuration--profiles)
6. [Understanding Beans](#understanding-beans)
7. [Declaring Beans](#declaring-beans)
8. [Dependency Injection](#dependency-injection)
9. [Bean Scopes](#bean-scopes)
10. [Bean Lifecycle](#bean-lifecycle)
11. [Conditional Beans & Profiles](#conditional-beans--profiles)
12. [Qualifiers & Primary](#qualifiers--primary)
13. [Component Scanning](#component-scanning)
14. [Configuration Classes](#configuration-classes)
15. [Cheatsheet](#cheatsheet)

---

## 1. What is Spring Boot?

Spring Boot is an **opinionated framework** built on top of the Spring Framework. It simplifies the creation of production-ready, stand-alone Spring applications by providing auto-configuration, embedded servers, and starter dependencies.

### Key Ideas
- **Convention over Configuration** — sensible defaults out of the box
- **Starter Dependencies** — curated groups of dependencies for common use cases
- **Auto-Configuration** — Spring Boot configures beans automatically based on what's on the classpath
- **Embedded Server** — no need to deploy to an external Tomcat/Jetty
- **Production-Ready** — health checks, metrics, externalized config

### Spring vs Spring Boot
| Spring Framework | Spring Boot |
|-----------------|-------------|
| Full control, lots of XML/Java config | Auto-configuration, minimal boilerplate |
| Manual dependency management | Starter POMs bundle dependencies |
| External servlet container required | Embedded Tomcat/Jetty/Undertow |
| Flexible but verbose | Opinionated but fast to start |

---

## 2. Project Creation

### Using Spring Initializr (Recommended)

The fastest way to bootstrap a project is via [start.spring.io](https://start.spring.io):

1. Go to **https://start.spring.io**
2. Choose: **Maven** or **Gradle**, **Java** version, **Spring Boot** version
3. Set Group, Artifact, Package name
4. Add dependencies (e.g., Spring Web, Spring Data JPA, Lombok)
5. Click **Generate** → download and unzip

### Using the CLI
```bash
# Install Spring Boot CLI via SDKMAN
sdk install springboot

# Create a project
spring init --dependencies=web,data-jpa,lombok \
  --java-version=21 \
  --build=maven \
  my-project
```

### Using IDE
- **IntelliJ IDEA**: File → New → Project → Spring Initializr
- **Eclipse/STS**: File → New → Spring Starter Project
- **VS Code**: Spring Initializr extension

### Maven from Scratch
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### ✅ Task 1 — Create Your First Project
- [ ] Go to [start.spring.io](https://start.spring.io)
- [ ] Select Maven, Java 21, Spring Boot 3.3.x
- [ ] Add dependencies: Spring Web
- [ ] Generate, unzip, and open in your IDE
- [ ] Run with `./mvnw spring-boot:run`
- [ ] Visit `http://localhost:8080` — you should see a Whitelabel Error Page (that's OK!)

---

## 3. Project Structure

```
my-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/myapp/
│   │   │       ├── MyAppApplication.java        ← Entry point
│   │   │       ├── controller/                   ← REST controllers
│   │   │       ├── service/                      ← Business logic
│   │   │       ├── repository/                   ← Data access
│   │   │       ├── model/                        ← Entities / DTOs
│   │   │       └── config/                       ← Configuration classes
│   │   └── resources/
│   │       ├── application.properties            ← Main configuration
│   │       ├── application.yml                   ← Alternative YAML config
│   │       ├── static/                           ← Static assets
│   │       └── templates/                        ← Thymeleaf templates
│   └── test/
│       └── java/
│           └── com/example/myapp/
│               └── MyAppApplicationTests.java
├── pom.xml                                       ← Maven build file
├── mvnw / mvnw.cmd                               ← Maven wrapper
└── .gitignore
```

### Key Conventions
| Directory/File | Purpose |
|---------------|---------|
| `src/main/java` | Application source code |
| `src/main/resources` | Configuration, static files, templates |
| `src/test/java` | Test source code |
| `application.properties` | Externalized configuration |
| `pom.xml` / `build.gradle` | Build configuration |

---

## 4. Application Entry Point

### The Main Class
```java
package com.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAppApplication.class, args);
    }
}
```

### What @SpringBootApplication Does
`@SpringBootApplication` is a **meta-annotation** that combines three annotations:

| Annotation | Purpose |
|-----------|---------|
| `@Configuration` | Marks this class as a source of bean definitions |
| `@EnableAutoConfiguration` | Enables Spring Boot's auto-configuration mechanism |
| `@ComponentScan` | Scans for components in the current package and sub-packages |

```java
// This is equivalent to:
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.example.myapp")
public class MyAppApplication { ... }
```

### Accessing the ApplicationContext
```java
@SpringBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MyAppApplication.class, args);

        // List all registered beans
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String name : beanNames) {
            System.out.println(name);
        }
    }
}
```

### ✅ Task 2 — Explore the ApplicationContext
- [ ] Print all bean names from `ApplicationContext` on startup
- [ ] Count how many beans Spring Boot auto-configures
- [ ] Find beans related to `tomcat` or `jackson` in the list

---

## 5. Configuration & Profiles

### application.properties
```properties
# Server
server.port=8081
server.servlet.context-path=/api

# Logging
logging.level.root=INFO
logging.level.com.example=DEBUG

# Custom properties
app.name=My Application
app.version=1.0.0
```

### application.yml (alternative)
```yaml
server:
  port: 8081
  servlet:
    context-path: /api

logging:
  level:
    root: INFO
    com.example: DEBUG

app:
  name: My Application
  version: 1.0.0
```

### Reading Custom Properties
```java
@Component
public class AppInfo {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version:0.0.1}")  // with default value
    private String appVersion;

    @Value("${app.description:No description}")
    private String description;
}
```

### Type-Safe Configuration with @ConfigurationProperties
```java
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String version;
    private List<String> allowedOrigins;

    // getters and setters
}
```

### Profiles
```properties
# application-dev.properties
server.port=8080
logging.level.com.example=DEBUG

# application-prod.properties
server.port=80
logging.level.com.example=WARN
```

```bash
# Activate a profile
java -jar app.jar --spring.profiles.active=dev

# Or in application.properties
spring.profiles.active=dev

# Or via environment variable
export SPRING_PROFILES_ACTIVE=prod
```

### ✅ Task 3 — Configuration
- [ ] Change the server port to `9090` in `application.properties`
- [ ] Create a custom property `app.greeting=Hello from Spring Boot!`
- [ ] Inject it into a component using `@Value`
- [ ] Create `application-dev.properties` with a different port
- [ ] Run with `--spring.profiles.active=dev`

---

## 6. Understanding Beans

A **bean** is simply an object that is managed by the **Spring IoC (Inversion of Control) Container**. Spring creates, configures, wires, and manages the lifecycle of beans.

### What is the IoC Container?
```
Traditional approach:
  Service creates its own dependencies
  Service service = new Service(new Repository(), new Config());

IoC approach:
  Container creates everything and wires them together
  @Autowired Service service;  // container provides it
```

### The ApplicationContext
The `ApplicationContext` is Spring's IoC container. It:
- Creates and stores all beans
- Resolves and injects dependencies
- Manages bean lifecycle (init → use → destroy)
- Publishes events

### Two Ways to Register Beans
```
1. COMPONENT SCANNING (stereotype annotations)
   @Component, @Service, @Repository, @Controller
   → Spring discovers them automatically via classpath scan

2. EXPLICIT DECLARATION (@Bean methods)
   @Configuration class with @Bean methods
   → You manually instantiate and return objects
```

---

## 7. Declaring Beans

### Stereotype Annotations (Component Scanning)
```java
@Component                    // Generic Spring-managed component
public class EmailValidator { ... }

@Service                      // Business/service layer
public class UserService { ... }

@Repository                   // Data access layer (adds exception translation)
public class UserRepository { ... }

@Controller                   // Spring MVC controller (returns views)
public class HomeController { ... }

@RestController               // REST API controller (@Controller + @ResponseBody)
public class UserController { ... }
```

### The Stereotype Hierarchy
```
@Component
├── @Service          ← semantic: business logic
├── @Repository       ← semantic: data access + exception translation
├── @Controller       ← semantic: web MVC controller
│   └── @RestController  ← @Controller + @ResponseBody
└── @Configuration    ← semantic: bean definition source
```

> All stereotype annotations are specializations of `@Component`. Functionally, `@Service` and `@Component` behave the same — the difference is **semantic clarity**.

### @Bean Methods in @Configuration
```java
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean(name = "taskExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }
}
```

### When to Use @Component vs @Bean
| Use Case | Approach |
|----------|----------|
| Your own class that you wrote | `@Component` / `@Service` / `@Repository` |
| Third-party library class | `@Bean` method in `@Configuration` |
| Need custom instantiation logic | `@Bean` method |
| Simple, self-contained component | `@Component` |

### ✅ Task 4 — Declare Your First Beans
- [ ] Create a `GreetingService` class annotated with `@Service`
- [ ] Add a `greet(String name)` method that returns `"Hello, " + name`
- [ ] Create a `@Configuration` class with a `@Bean` that returns a `RestTemplate`
- [ ] Print both beans from the `ApplicationContext` on startup

---

## 8. Dependency Injection

Spring supports three forms of dependency injection. **Constructor injection** is the recommended approach.

### Constructor Injection (Recommended)
```java
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // Spring automatically injects dependencies via constructor
    // @Autowired is optional if there's only one constructor (Spring 4.3+)
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User createUser(String name) {
        User user = userRepository.save(new User(name));
        emailService.sendWelcome(user);
        return user;
    }
}
```

### Field Injection (Discouraged)
```java
@Service
public class UserService {

    @Autowired  // Injects directly into field — harder to test
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
}
```

### Setter Injection (Optional Dependencies)
```java
@Service
public class ReportService {

    private CacheService cacheService;

    @Autowired(required = false)  // Optional dependency
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }
}
```

### Why Constructor Injection Wins

| Feature | Constructor | Field | Setter |
|---------|------------|-------|--------|
| Immutability (`final` fields) | ✅ | ❌ | ❌ |
| Required dependencies enforced | ✅ | ❌ | ❌ |
| Easy to unit test | ✅ | ❌ | ✅ |
| No reflection needed | ✅ | ❌ | ✅ |
| Detects circular dependencies early | ✅ | ❌ | ❌ |

### Using Lombok to Reduce Boilerplate
```java
@Service
@RequiredArgsConstructor       // generates constructor for all final fields
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    // No constructor code needed — Lombok generates it
}
```

### ✅ Task 5 — Dependency Injection
- [ ] Create `NotificationService` interface with a `send(String message)` method
- [ ] Create `EmailNotificationService` implementing it, annotated with `@Service`
- [ ] Create `OrderService` that injects `NotificationService` via constructor injection
- [ ] Call `notificationService.send(...)` from a method in `OrderService`
- [ ] Verify it works by calling the method on startup via `CommandLineRunner`

---

## 9. Bean Scopes

Bean scope determines **how many instances** Spring creates and **how long they live**.

### Available Scopes
| Scope | Description | Default? |
|-------|-------------|----------|
| `singleton` | One instance per ApplicationContext | ✅ Yes |
| `prototype` | New instance every time it's requested | |
| `request` | One instance per HTTP request (web only) | |
| `session` | One instance per HTTP session (web only) | |
| `application` | One instance per ServletContext (web only) | |

### Singleton (Default)
```java
@Service  // Singleton by default
public class UserService {
    private int counter = 0;

    public int incrementAndGet() {
        return ++counter;   // Shared state — be careful!
    }
}
```

### Prototype
```java
@Component
@Scope("prototype")
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);   // Each injection gets a fresh cart
    }
}
```

### Verifying Scope Behavior
```java
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        var ctx = SpringApplication.run(App.class, args);

        // Singleton — same instance
        UserService s1 = ctx.getBean(UserService.class);
        UserService s2 = ctx.getBean(UserService.class);
        System.out.println(s1 == s2);  // true

        // Prototype — different instances
        ShoppingCart c1 = ctx.getBean(ShoppingCart.class);
        ShoppingCart c2 = ctx.getBean(ShoppingCart.class);
        System.out.println(c1 == c2);  // false
    }
}
```

### ⚠️ Singleton–Prototype Gotcha
```java
@Service  // singleton
public class OrderService {

    private final ShoppingCart cart;  // prototype

    public OrderService(ShoppingCart cart) {
        this.cart = cart;  // ⚠️ Injected ONCE — always the same cart!
    }
}
```

**Fix: Use `ObjectProvider` or `@Lookup`**
```java
@Service
public class OrderService {

    private final ObjectProvider<ShoppingCart> cartProvider;

    public OrderService(ObjectProvider<ShoppingCart> cartProvider) {
        this.cartProvider = cartProvider;
    }

    public void placeOrder() {
        ShoppingCart cart = cartProvider.getObject();  // new instance each time
    }
}
```

### ✅ Task 6 — Bean Scopes
- [ ] Create a `@Scope("prototype")` bean called `RequestTracker`
- [ ] Get it twice from `ApplicationContext` and verify they are different instances
- [ ] Create a singleton `AnalyticsService` that needs a new `RequestTracker` per call
- [ ] Use `ObjectProvider<RequestTracker>` to fix the scope mismatch

---

## 10. Bean Lifecycle

Spring beans go through a well-defined lifecycle from creation to destruction.

### Lifecycle Phases
```
1. Instantiation          → Constructor called
2. Populate Properties    → Dependencies injected
3. BeanNameAware          → setBeanName() called
4. BeanFactoryAware       → setBeanFactory() called
5. ApplicationContextAware → setApplicationContext() called
6. @PostConstruct         → Custom initialization
7. InitializingBean       → afterPropertiesSet()
8. Custom init-method     → @Bean(initMethod = "init")
   ─── Bean is READY ───
9. @PreDestroy            → Custom cleanup
10. DisposableBean        → destroy()
11. Custom destroy-method  → @Bean(destroyMethod = "cleanup")
```

### @PostConstruct and @PreDestroy
```java
@Service
public class CacheService {

    private Map<String, Object> cache;

    @PostConstruct
    public void init() {
        cache = new ConcurrentHashMap<>();
        System.out.println("CacheService initialized — cache ready");
        // Load initial data, warm up cache, etc.
    }

    @PreDestroy
    public void shutdown() {
        cache.clear();
        System.out.println("CacheService destroyed — cache cleared");
        // Release resources, close connections, etc.
    }
}
```

### InitializingBean and DisposableBean (Interfaces)
```java
@Component
public class DatabasePool implements InitializingBean, DisposableBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Connection pool started");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Connection pool closed");
    }
}
```

### @Bean with initMethod and destroyMethod
```java
@Configuration
public class AppConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MonitoringAgent monitoringAgent() {
        return new MonitoringAgent();
    }
}

// No Spring annotations needed on this class
public class MonitoringAgent {
    public void start() { System.out.println("Agent started"); }
    public void stop()  { System.out.println("Agent stopped"); }
}
```

### CommandLineRunner and ApplicationRunner
```java
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Runs AFTER the ApplicationContext is fully initialized
        userRepository.save(new User("admin", "admin@example.com"));
        System.out.println("Default data loaded!");
    }
}
```

### ✅ Task 7 — Lifecycle Hooks
- [ ] Create a `ConnectionManager` bean with `@PostConstruct` and `@PreDestroy`
- [ ] Print messages in both methods to observe the lifecycle
- [ ] Create a `CommandLineRunner` that logs "Application ready!"
- [ ] Run the app and observe the order: `@PostConstruct` → `CommandLineRunner` → (on shutdown) `@PreDestroy`

---

## 11. Conditional Beans & Profiles

### @Profile — Beans for Specific Environments
```java
@Service
@Profile("dev")
public class MockEmailService implements EmailService {
    public void send(String to, String msg) {
        System.out.println("DEV — Pretending to send email to " + to);
    }
}

@Service
@Profile("prod")
public class SmtpEmailService implements EmailService {
    public void send(String to, String msg) {
        // Actually send email via SMTP
    }
}
```

### @Conditional Annotations
```java
@Configuration
public class FeatureConfig {

    @Bean
    @ConditionalOnProperty(name = "feature.cache.enabled", havingValue = "true")
    public CacheService cacheService() {
        return new RedisCacheService();
    }

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    public CacheService defaultCacheService() {
        return new InMemoryCacheService();
    }

    @Bean
    @ConditionalOnClass(name = "com.zaxxer.hikari.HikariDataSource")
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
```

### Common @Conditional Annotations
| Annotation | Condition |
|-----------|-----------|
| `@ConditionalOnProperty` | Config property has specific value |
| `@ConditionalOnClass` | Class is on the classpath |
| `@ConditionalOnMissingBean` | No bean of that type exists |
| `@ConditionalOnBean` | A bean of that type already exists |
| `@ConditionalOnExpression` | SpEL expression evaluates to true |
| `@Profile` | Active Spring profile matches |

### ✅ Task 8 — Conditional Beans
- [ ] Create an `EmailService` interface
- [ ] Create `MockEmailService` with `@Profile("dev")`
- [ ] Create `RealEmailService` with `@Profile("prod")`
- [ ] Run with `--spring.profiles.active=dev` and verify mock is injected
- [ ] Switch to `prod` and verify the real one is injected

---

## 12. Qualifiers & Primary

When multiple beans implement the same interface, Spring needs help deciding which one to inject.

### The Problem
```java
public interface PaymentGateway {
    void charge(BigDecimal amount);
}

@Service
public class StripeGateway implements PaymentGateway { ... }

@Service
public class PayPalGateway implements PaymentGateway { ... }

@Service
public class OrderService {
    // ❌ FAILS: NoUniqueBeanDefinitionException
    // Spring doesn't know which PaymentGateway to inject!
    public OrderService(PaymentGateway gateway) { ... }
}
```

### Solution 1: @Primary
```java
@Service
@Primary              // This one wins when there's ambiguity
public class StripeGateway implements PaymentGateway { ... }

@Service
public class PayPalGateway implements PaymentGateway { ... }
```

### Solution 2: @Qualifier
```java
@Service
@Qualifier("stripe")
public class StripeGateway implements PaymentGateway { ... }

@Service
@Qualifier("paypal")
public class PayPalGateway implements PaymentGateway { ... }

@Service
public class OrderService {
    public OrderService(@Qualifier("stripe") PaymentGateway gateway) {
        // Explicitly picks StripeGateway
    }
}
```

### Solution 3: Inject All Implementations
```java
@Service
public class PaymentRouter {

    private final List<PaymentGateway> gateways;       // all implementations
    private final Map<String, PaymentGateway> gatewayMap;  // keyed by bean name

    public PaymentRouter(List<PaymentGateway> gateways) {
        this.gateways = gateways;
    }
}
```

### Custom Qualifier Annotations
```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface Stripe {}

@Service
@Stripe
public class StripeGateway implements PaymentGateway { ... }

@Service
public class OrderService {
    public OrderService(@Stripe PaymentGateway gateway) { ... }
}
```

### ✅ Task 9 — Qualifiers
- [ ] Create a `MessageSender` interface
- [ ] Implement `SmsSender` and `EmailSender` (both `@Service`)
- [ ] Try injecting `MessageSender` — observe the error
- [ ] Fix it using `@Primary` on one implementation
- [ ] Then switch to using `@Qualifier` for explicit selection

---

## 13. Component Scanning

### Default Behavior
`@SpringBootApplication` triggers `@ComponentScan` starting from the **package of the main class** and scanning all sub-packages.

```
com.example.myapp/                ← @SpringBootApplication is here
├── controller/                   ← ✅ scanned
├── service/                      ← ✅ scanned
├── repository/                   ← ✅ scanned
└── config/                       ← ✅ scanned

com.example.other/                ← ❌ NOT scanned (sibling package)
```

### Customizing Component Scan
```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.myapp",
    "com.example.shared"           // scan additional packages
})
public class MyAppApplication { ... }
```

### Excluding Components
```java
@ComponentScan(
    basePackages = "com.example",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = LegacyService.class
    )
)
```

### ✅ Task 10 — Component Scanning
- [ ] Create a class in a **sibling** package (outside main class package)
- [ ] Annotate it with `@Service` and try to inject it — observe the error
- [ ] Fix it by adding `@ComponentScan(basePackages = {...})`
- [ ] Alternatively, move the class into a sub-package of the main class

---

## 14. Configuration Classes

### @Configuration Deep Dive
```java
@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService(UserRepository repo) {
        // Spring resolves 'repo' from the context automatically
        return new UserService(repo);
    }

    @Bean
    public AuditService auditService() {
        return new AuditService();
    }

    @Bean
    public AdminService adminService(UserService userService, AuditService auditService) {
        // Inter-bean dependencies resolved automatically
        return new AdminService(userService, auditService);
    }
}
```

### Full vs Lite Mode
```java
// FULL mode (@Configuration) — bean methods are proxied
// Calling userService() multiple times returns the SAME instance
@Configuration
public class FullModeConfig {
    @Bean
    public UserService userService() { return new UserService(); }

    @Bean
    public AdminService adminService() {
        return new AdminService(userService());  // same instance!
    }
}

// LITE mode (@Component) — NO proxying
// Calling userService() creates a NEW instance each time
@Component
public class LiteModeConfig {
    @Bean
    public UserService userService() { return new UserService(); }

    @Bean
    public AdminService adminService() {
        return new AdminService(userService());  // ⚠️ different instance!
    }
}
```

### Importing Configurations
```java
@Configuration
@Import({SecurityConfig.class, CacheConfig.class})
public class AppConfig { ... }
```

### ✅ Task 11 — Configuration Classes
- [ ] Create a `@Configuration` class with two `@Bean` methods where one depends on the other
- [ ] Verify both beans are singletons (call the method twice, compare references)
- [ ] Change `@Configuration` to `@Component` and observe the difference (lite mode)

---

## 15. Cheatsheet

```java
// ── STEREOTYPE ANNOTATIONS ────────────────────────
@Component          // generic Spring-managed bean
@Service            // business logic layer
@Repository         // data access layer + exception translation
@Controller         // MVC web controller
@RestController     // REST controller (@Controller + @ResponseBody)
@Configuration      // bean definition source

// ── BEAN DECLARATION ──────────────────────────────
@Bean               // declare bean in @Configuration class
@Bean(name = "x")   // custom bean name
@Bean(initMethod = "init", destroyMethod = "cleanup")

// ── DEPENDENCY INJECTION ──────────────────────────
@Autowired          // inject dependency (prefer constructor injection)
@Qualifier("name")  // disambiguate when multiple candidates
@Primary            // mark as default when multiple candidates
@Value("${key}")    // inject config value
@Lazy               // create bean on first use, not at startup

// ── SCOPES ────────────────────────────────────────
@Scope("singleton")   // default — one instance
@Scope("prototype")   // new instance each time
@Scope("request")     // one per HTTP request
@Scope("session")     // one per HTTP session

// ── LIFECYCLE ─────────────────────────────────────
@PostConstruct      // run after dependency injection
@PreDestroy         // run before bean destruction
CommandLineRunner   // run after ApplicationContext is ready
ApplicationRunner   // same, with ApplicationArguments

// ── CONDITIONAL ───────────────────────────────────
@Profile("dev")                         // only in dev profile
@ConditionalOnProperty(name = "x")      // if property exists
@ConditionalOnClass(name = "x.Y")       // if class on classpath
@ConditionalOnMissingBean(X.class)      // if no bean of type X

// ── CONFIGURATION ─────────────────────────────────
@ConfigurationProperties(prefix = "app")
@EnableConfigurationProperties(AppProps.class)
@PropertySource("classpath:custom.properties")
@Import(OtherConfig.class)

// ── COMPONENT SCANNING ────────────────────────────
@ComponentScan(basePackages = "com.example")
@ComponentScan(excludeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = X.class))
```

---

## 📖 Resources

| Resource | Link |
|----------|------|
| Official Spring Boot Docs | https://docs.spring.io/spring-boot/docs/current/reference/html/ |
| Spring Guides | https://spring.io/guides |
| Spring Initializr | https://start.spring.io |
| Baeldung Spring Tutorials | https://www.baeldung.com/spring-boot |
| Spring Boot API Docs | https://docs.spring.io/spring-boot/docs/current/api/ |

---

*Happy bean wiring! 🍃*
