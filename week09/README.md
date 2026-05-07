# Weeks 09: JPA Relationships, Transactions & Testing

These three labs build on the Week 08 database migration. By the end you will have
a fully relational schema with foreign keys, transactional reservation logic, and
automated integration tests that run against a real database.

> **Pre-requisite:** Week 08 completed — `Show` is a JPA entity, `ShowRepository`
> extends `JpaRepository`, Liquibase manages the `theatre.show` table, and PostgreSQL
> is running (Docker or local).

---

## ER Diagram — target schema after Week 09

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│     hall     │       │   performance    │       │     show     │
├──────────────┤       ├──────────────────┤       ├──────────────┤
│ id      (PK) │◄──────│ hall_id     (FK) │       │ id      (PK) │
│ name         │       │ id          (PK) │       │ title        │
│ capacity     │       │ show_id     (FK) │──────►│ description  │
└──────────────┘       │ start_time       │       │ genre        │
                       │ status           │       │ duration_min │
       ┌───────────────│ version          │       │ age_rating   │
       │               └──────────────────┘       └──────────────┘
       │
       │  (added in Week 10)
       ▼
┌──────────────────┐
│   reservation    │
├──────────────────┤
│ id          (PK) │
│ performance_id(FK│
│ seat_label       │
│ customer_name    │
│ status           │
│ reserved_at      │
│ version          │
└──────────────────┘
```

---

# Week 09 — JPA Relationships

## Objectives
- Create Liquibase changesets for `hall` and `performance` tables with foreign keys
- Turn `Hall` and `Performance` into JPA entities
- Replace `Long showId` / `Long hallId` with `@ManyToOne` object references
- Migrate `PerformanceRepository` to Spring Data JPA
- Update the service and DTO layers to work with entity relationships

---

### Task 1 — Liquibase changeset for the `hall` table

Create `src/main/resources/db/changelog/changes/002-create-hall-table.sql`:

```sql
--liquibase formatted sql

--changeset fmi:002-create-hall-table
CREATE TABLE hall (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL
);

--rollback DROP TABLE hall;
```

Register it in `db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-create-show-table.sql
  - include:
      file: db/changelog/changes/002-create-hall-table.sql
```

Start the app and verify:

```sql
SELECT * FROM theatre.hall;
```

---

### Task 2 — Liquibase changeset for the `performance` table

Create `src/main/resources/db/changelog/changes/003-create-performance-table.sql`:

```sql
--liquibase formatted sql

--changeset fmi:003-create-performance-table
CREATE TABLE performance (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL,
    hall_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_performance_show FOREIGN KEY (show_id) REFERENCES show(id),
    CONSTRAINT fk_performance_hall FOREIGN KEY (hall_id) REFERENCES hall(id)
);

--rollback DROP TABLE performance;
```

Register in `db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-create-show-table.sql
  - include:
      file: db/changelog/changes/002-create-hall-table.sql
  - include:
      file: db/changelog/changes/003-create-performance-table.sql
```

Start the app. Liquibase should create both tables with foreign keys. Verify:

```sql
\dt theatre.*
-- You should see: hall, performance, show, plus the databasechangelog tables
```

---

### Task 3 — Turn `Hall` into a JPA entity

The current `Hall` class has a `final Long id` and no no-args constructor. JPA needs
both to be changed. Here is the target:

```java
@Entity
@Table(name = "hall")
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int capacity;

    protected Hall() {}  // JPA requires a no-args constructor

    public Hall(String name, int capacity) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.name = name;
        this.capacity = capacity;
    }

    // getters, setters, equals/hashCode (unchanged)
}
```

Key changes compared to the old class:
- `id` is no longer `final` — JPA sets it after `persist()`
- The all-args constructor no longer takes `id` — the DB generates it
- A `protected` no-args constructor was added for JPA proxy creation
- Added a `capacity` field (it was missing — a hall needs a seat count)

---

### Task 4 — Turn `Performance` into a JPA entity with relationships

This is the core of the week. The old `Performance` stores `Long showId` and
`Long hallId` — plain foreign key values. JPA entities model relationships as
**object references** instead.

```java
@Entity
@Table(name = "performance")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceStatus status;

    protected Performance() {}

    public Performance(Show show, Hall hall, LocalDateTime startTime) {
        if (show == null) throw new IllegalArgumentException("show is required");
        if (hall == null) throw new IllegalArgumentException("hall is required");
        if (startTime == null) throw new IllegalArgumentException("startTime is required");
        this.show = show;
        this.hall = hall;
        this.startTime = startTime;
        this.status = PerformanceStatus.SCHEDULED;
    }

    // getters, setters, equals/hashCode
    public Long getId() { return id; }
    public Show getShow() { return show; }
    public Hall getHall() { return hall; }
    public LocalDateTime getStartTime() { return startTime; }
    public PerformanceStatus getStatus() { return status; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setStatus(PerformanceStatus status) { this.status = status; }
}
```

**Why `FetchType.LAZY`?**
By default, `@ManyToOne` uses `EAGER` loading — every time you load a `Performance`,
Hibernate immediately joins and loads the related `Show` and `Hall`. With `LAZY`, they
are only fetched when you actually call `getShow()` or `getHall()`. This matters when
you load a list of 100 performances but only need the start times.

**What about the `@OneToMany` side?**
Optionally, add the inverse side to `Show`:

```java
// inside Show.java
@OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Performance> performances = new ArrayList<>();
```

This is **not required** for the FK to work — the FK lives on the `performance` table
and is fully managed by the `@ManyToOne` in `Performance`. The `@OneToMany` side is a
convenience for navigating from a `Show` to its performances in Java. Add it only if
your service layer needs `show.getPerformances()`.

> **Discussion point:** When would you use `CascadeType.ALL` vs `CascadeType.PERSIST`
> vs no cascade? Think about what happens when you delete a Show that has performances.

---

### Task 5 — Migrate repositories

#### 5.1 Create `HallRepository`

```java
package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {
}
```

#### 5.2 Migrate `PerformanceRepository` to Spring Data JPA

Replace the current interface with:

```java
package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Performance;
import bg.uni.fmi.theatre.domain.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findByShow(Show show);

    List<Performance> findByShowId(Long showId);
}
```

Spring Data derives the query from the method name: `findByShowId` generates
`SELECT p FROM Performance p WHERE p.show.id = :showId`. No implementation class needed.

#### 5.3 Delete `InMemoryPerformanceRepository`

Remove the class (or strip its `@Repository` annotation). With the JPA interface in
place, Spring Data provides the proxy bean automatically.

#### 5.4 Create a `HallSeeder` + `PerformanceSeeder`

Similar to `ShowSeeder`, add `CommandLineRunner` beans that populate initial data
only when the tables are empty:

```java
@Component
public class HallSeeder implements CommandLineRunner {

    private final HallRepository halls;

    public HallSeeder(HallRepository halls) { this.halls = halls; }

    @Override
    public void run(String... args) {
        if (halls.count() > 0) return;

        halls.save(new Hall("Main Stage", 500));
        halls.save(new Hall("Studio Theatre", 120));
        halls.save(new Hall("Open Air Amphitheatre", 800));
    }
}
```

The `PerformanceSeeder` needs both `ShowRepository` and `HallRepository` to look up
the seeded entities and link them:

```java
@Component
public class PerformanceSeeder implements CommandLineRunner {

    private final PerformanceRepository performances;
    private final ShowRepository shows;
    private final HallRepository halls;

    // constructor injection ...

    @Override
    public void run(String... args) {
        if (performances.count() > 0) return;

        Show hamlet = shows.findAll().stream()
                .filter(s -> s.getTitle().equals("Hamlet"))
                .findFirst().orElseThrow();
        Hall mainStage = halls.findAll().stream()
                .filter(h -> h.getName().equals("Main Stage"))
                .findFirst().orElseThrow();

        performances.save(new Performance(hamlet, mainStage,
                LocalDateTime.of(2026, 6, 15, 19, 0)));
        performances.save(new Performance(hamlet, mainStage,
                LocalDateTime.of(2026, 6, 22, 19, 0)));

        // ... add more as needed
    }
}
```

> **Tip:** If seeder ordering matters (halls before performances), use
> `@Order(1)`, `@Order(2)` on the seeders, or combine them into a single runner.

---

### Task 6 — Update the service and DTO layers

The `PerformanceService` currently receives a raw `Performance` with `Long showId`.
After the refactor, the service should accept a DTO and build the entity internally.

Create `PerformanceRequest`:

```java
public class PerformanceRequest {
    @NotNull private Long showId;
    @NotNull private Long hallId;
    @NotNull private LocalDateTime startTime;

    // getters, setters
}
```

Update `PerformanceService.addPerformance()`:

```java
public PerformanceResponse addPerformance(PerformanceRequest req) {
    Show show = showRepository.findById(req.getShowId())
            .orElseThrow(() -> new NotFoundException("Show", req.getShowId()));
    Hall hall = hallRepository.findById(req.getHallId())
            .orElseThrow(() -> new NotFoundException("Hall", req.getHallId()));

    Performance performance = new Performance(show, hall, req.getStartTime());
    Performance saved = performanceRepository.save(performance);
    return PerformanceResponse.from(saved);
}
```

Update `PerformanceResponse.from()` to pull IDs from the entity references:

```java
public static PerformanceResponse from(Performance p) {
    PerformanceResponse r = new PerformanceResponse();
    r.id = p.getId();
    r.showId = p.getShow().getId();
    r.hallId = p.getHall().getId();
    r.startTime = p.getStartTime();
    r.status = p.getStatus();
    return r;
}
```

### Task 7 — Test through the REST API

Start the app and exercise the endpoints via Swagger UI (`/swagger-ui.html`):

- `GET /api/performances` — should return seeded performances with `showId` / `hallId`
- `GET /api/performances?showId=1` — filter by show
- `POST /api/performances` with `{"showId": 1, "hallId": 2, "startTime": "2026-07-01T20:00:00"}`
- Try `POST` with a non-existent `showId` → expect 404

Restart the app — data persists.

---

# Transactions & Reservations

## Objectives
- Understand `@Transactional` and Spring's transaction management
- Add optimistic locking with `@Version`
- Build a `Reservation` entity and a transactional booking flow
- See what happens under concurrent access

---

### Task 1 — Add `@Version` to `Performance`

Optimistic locking prevents two users from booking the "last seat" simultaneously.
Add to the `Performance` entity:

```java
@Version
private Long version;
```

And add a matching column in a new changeset
`src/main/resources/db/changelog/changes/004-add-version-columns.sql`:

```sql
--liquibase formatted sql

--changeset fmi:004-add-version-to-performance
ALTER TABLE performance ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

--rollback ALTER TABLE performance DROP COLUMN version;
```

Register in `db.changelog-master.yaml`. Start the app — Hibernate validation should pass.

**How it works:** Every `UPDATE` now includes `WHERE version = ?`. If another transaction
changed the row first, the version won't match, Hibernate throws
`OptimisticLockException`, and Spring wraps it as a 409 Conflict (if you handle it in
`GlobalExceptionHandler`).

---

### Task 2 — Create the `Reservation` entity and table

#### 2.1 Liquibase changeset

`src/main/resources/db/changelog/changes/005-create-reservation-table.sql`:

```sql
--liquibase formatted sql

--changeset fmi:005-create-reservation-table
CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    performance_id BIGINT NOT NULL,
    seat_label VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reserved_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_reservation_performance FOREIGN KEY (performance_id) REFERENCES performance(id),
    CONSTRAINT uq_reservation_seat UNIQUE (performance_id, seat_label)
);

--rollback DROP TABLE reservation;
```

The unique constraint on `(performance_id, seat_label)` ensures the same seat cannot be
double-booked for the same performance — this is your safety net at the database level.

#### 2.2 The entity

```java
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Column(name = "seat_label", nullable = false, length = 20)
    private String seatLabel;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt;

    @Version
    private Long version;

    protected Reservation() {}

    public Reservation(Performance performance, String seatLabel, String customerName) {
        this.performance = performance;
        this.seatLabel = seatLabel;
        this.customerName = customerName;
        this.status = ReservationStatus.CONFIRMED;
        this.reservedAt = LocalDateTime.now();
    }

    // getters, setters
}
```

Create the enum:

```java
package bg.uni.fmi.theatre.vo;

public enum ReservationStatus { CONFIRMED, CANCELLED }
```

#### 2.3 The repository

```java
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByPerformanceId(Long performanceId);

    boolean existsByPerformanceIdAndSeatLabel(Long performanceId, String seatLabel);
}
```

---

### Task 3 — Build the transactional booking service

```java
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PerformanceRepository performanceRepository;

    // constructor injection ...

    /**
     * Books a seat for a performance.
     *
     * The entire method runs in a single transaction:
     * 1. Load and lock the Performance (optimistic lock via @Version)
     * 2. Check the seat is not already taken
     * 3. Create and persist the Reservation
     *
     * If two users try to book the same seat concurrently, one will get
     * an OptimisticLockException (from @Version) or a unique constraint
     * violation (from the DB) — both result in an error, not a double booking.
     */
    @Transactional
    public ReservationResponse bookSeat(ReservationRequest req) {
        Performance performance = performanceRepository.findById(req.getPerformanceId())
                .orElseThrow(() -> new NotFoundException("Performance", req.getPerformanceId()));

        if (performance.getStatus() != PerformanceStatus.SCHEDULED) {
            throw new ValidationException("Cannot book seats for a "
                    + performance.getStatus().name().toLowerCase() + " performance");
        }

        if (reservationRepository.existsByPerformanceIdAndSeatLabel(
                req.getPerformanceId(), req.getSeatLabel())) {
            throw new ValidationException("Seat " + req.getSeatLabel() + " is already booked");
        }

        Reservation reservation = new Reservation(
                performance, req.getSeatLabel(), req.getCustomerName());
        Reservation saved = reservationRepository.save(reservation);

        return ReservationResponse.from(saved);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation", id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        // no explicit save() needed — the entity is managed within the transaction,
        // so Hibernate's dirty checking flushes the change at commit time.
    }
}
```

**Key concepts to discuss in lab:**

- **`@Transactional` scope:** The annotation makes the entire method atomic. If
  `reservationRepository.save()` fails, the whole method rolls back — the performance
  lookup doesn't leave a dangling state.

- **Managed vs detached entities:** Inside a `@Transactional` method, entities loaded
  via `findById()` are *managed* — Hibernate tracks changes and flushes them on commit.
  That's why `cancelReservation()` doesn't call `save()`. Outside a transaction (or after
  the method returns), the entity is *detached* and changes are lost unless you
  explicitly `save()`.

- **`save()` on a new vs existing entity:** `JpaRepository.save()` calls `persist()`
  for new entities (no ID) and `merge()` for existing ones (ID present). After a `merge()`,
  the *returned* object is the managed copy — the original stays detached. This is a
  common source of bugs.

---

### Task 4 — Wire up the REST endpoint

Create `ReservationRequest` and `ReservationResponse` DTOs, then add a controller:

```java
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Seat booking operations")
public class ReservationController {

    private final ReservationService reservationService;

    // constructor ...

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse bookSeat(@Valid @RequestBody ReservationRequest req) {
        return reservationService.bookSeat(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }

    @GetMapping
    public List<ReservationResponse> listByPerformance(@RequestParam Long performanceId) {
        return reservationService.findByPerformance(performanceId);
    }
}
```

### Task 5 — Handle the optimistic lock conflict

Add to `GlobalExceptionHandler`:

```java
@ExceptionHandler(OptimisticLockingFailureException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public ErrorResponse handleOptimisticLock(OptimisticLockingFailureException ex,
                                          HttpServletRequest request) {
    return new ErrorResponse(409,
            "The resource was modified by another request. Please retry.",
            request.getRequestURI());
}
```

### Task 6 — Manual concurrency test

Open two terminal windows and send simultaneous booking requests for the **same seat**:

```bash
# Terminal 1
curl -X POST http://localhost:8099/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"performanceId": 1, "seatLabel": "A1", "customerName": "Alice"}'

# Terminal 2 (run at the same time)
curl -X POST http://localhost:8099/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"performanceId": 1, "seatLabel": "A1", "customerName": "Bob"}'
```

One succeeds with 201, the other gets 400 (seat taken) or 409 (version conflict).
**Neither produces a double booking.** Verify:

```sql
SELECT * FROM theatre.reservation WHERE performance_id = 1 AND seat_label = 'A1';
-- Exactly one row
```

---

# Testing the Database Layer

## Objectives
- Write repository-level integration tests with `@DataJpaTest`
- Write service-level integration tests with `@SpringBootTest`
- Use Testcontainers for a real PostgreSQL in tests
- Replace the old `TestInMemoryShowRepository` with proper integration tests

---

### Task 0 — Add test dependencies

Add to `pom.xml`:

```xml
<!-- Testcontainers — runs a real Postgres in Docker for tests -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
```

> **Why not H2?** H2 is simpler to set up (no Docker needed), but it behaves
> differently from PostgreSQL in subtle ways — e.g. `VARCHAR` semantics, `TEXT` column
> support, locking behaviour, and enum handling. Testcontainers gives you the **exact
> same database engine** you use in production. If Docker isn't available on your
> machine, H2 is a reasonable fallback — see the appendix at the end.

---

### Task 1 — Create a shared Testcontainers configuration

Create a base class that all DB tests extend:

`src/test/java/bg/uni/fmi/theatre/AbstractDatabaseTest.java`:

```java
package bg.uni.fmi.theatre;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractDatabaseTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("theatre_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.default-schema", () -> "public");
    }
}
```

The `@Container` annotation starts a fresh PostgreSQL Docker container before the
test class runs. `@DynamicPropertySource` overrides `application.yml` so Spring Boot
connects to the container instead of your local database.

---

### Task 2 — Repository tests with `@DataJpaTest`

`@DataJpaTest` loads **only** the JPA slice — repositories, entities, Liquibase, and
the datasource. No controllers, no services, no web layer. Tests run fast.

`src/test/java/bg/uni/fmi/theatre/repository/ShowRepositoryTest.java`:

```java
package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.AbstractDatabaseTest;
import bg.uni.fmi.theatre.domain.Show;
import bg.uni.fmi.theatre.vo.AgeRating;
import bg.uni.fmi.theatre.vo.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShowRepositoryTest extends AbstractDatabaseTest {

    @Autowired
    private ShowRepository showRepository;

    @Test
    void save_and_findById_roundTrip() {
        Show show = new Show("Test Show", "A test description",
                Genre.COMEDY, 90, AgeRating.ALL);

        Show saved = showRepository.save(show);

        assertThat(saved.getId()).isNotNull();

        Optional<Show> found = showRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Show");
        assertThat(found.get().getGenre()).isEqualTo(Genre.COMEDY);
    }

    @Test
    void findAll_returnsAllSavedShows() {
        showRepository.save(new Show("Show A", "desc", Genre.DRAMA, 120, AgeRating.PG_16));
        showRepository.save(new Show("Show B", "desc", Genre.BALLET, 100, AgeRating.ALL));

        List<Show> all = showRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).extracting(Show::getTitle).contains("Show A", "Show B");
    }

    @Test
    void deleteById_removesShow() {
        Show saved = showRepository.save(
                new Show("To Delete", "desc", Genre.OPERA, 150, AgeRating.PG_12));

        showRepository.deleteById(saved.getId());

        assertThat(showRepository.findById(saved.getId())).isEmpty();
    }
}
```

**`@AutoConfigureTestDatabase(replace = NONE)`** is critical — without it,
`@DataJpaTest` tries to replace your datasource with an embedded H2. Since we're
using Testcontainers, we want Spring to keep our configured datasource.

Each `@DataJpaTest` method runs in a transaction that is **automatically rolled back**
after the test — so tests don't interfere with each other.

---

### Task 3 — Repository tests for relationships

`src/test/java/bg/uni/fmi/theatre/repository/PerformanceRepositoryTest.java`:

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PerformanceRepositoryTest extends AbstractDatabaseTest {

    @Autowired private PerformanceRepository performanceRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private HallRepository hallRepository;

    @Test
    void findByShowId_returnsOnlyMatchingPerformances() {
        Show hamlet = showRepository.save(
                new Show("Hamlet", "tragedy", Genre.DRAMA, 180, AgeRating.PG_16));
        Show chicago = showRepository.save(
                new Show("Chicago", "musical", Genre.MUSICAL, 135, AgeRating.PG_12));
        Hall hall = hallRepository.save(new Hall("Main Stage", 500));

        performanceRepository.save(new Performance(hamlet, hall,
                LocalDateTime.of(2026, 7, 1, 19, 0)));
        performanceRepository.save(new Performance(hamlet, hall,
                LocalDateTime.of(2026, 7, 8, 19, 0)));
        performanceRepository.save(new Performance(chicago, hall,
                LocalDateTime.of(2026, 7, 2, 20, 0)));

        List<Performance> hamletPerformances =
                performanceRepository.findByShowId(hamlet.getId());

        assertThat(hamletPerformances).hasSize(2);
        assertThat(hamletPerformances)
                .allMatch(p -> p.getShow().getId().equals(hamlet.getId()));
    }

    @Test
    void save_setsIdAndPreservesRelationships() {
        Show show = showRepository.save(
                new Show("Swan Lake", "ballet", Genre.BALLET, 140, AgeRating.ALL));
        Hall hall = hallRepository.save(new Hall("Studio", 120));

        Performance saved = performanceRepository.save(
                new Performance(show, hall, LocalDateTime.of(2026, 8, 1, 18, 0)));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getShow().getTitle()).isEqualTo("Swan Lake");
        assertThat(saved.getHall().getName()).isEqualTo("Studio");
    }
}
```

---

### Task 4 — Service-level integration test with `@SpringBootTest`

Unlike `@DataJpaTest`, `@SpringBootTest` loads the **full application context** —
services, repositories, controllers, everything. Use this to test end-to-end flows.

`src/test/java/bg/uni/fmi/theatre/service/ReservationServiceIntegrationTest.java`:

```java
@SpringBootTest
class ReservationServiceIntegrationTest extends AbstractDatabaseTest {

    @Autowired private ReservationService reservationService;
    @Autowired private PerformanceRepository performanceRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private HallRepository hallRepository;

    private Performance testPerformance;

    @BeforeEach
    void setUp() {
        Show show = showRepository.save(
                new Show("Test Show", "desc", Genre.COMEDY, 90, AgeRating.ALL));
        Hall hall = hallRepository.save(new Hall("Test Hall", 100));
        testPerformance = performanceRepository.save(
                new Performance(show, hall, LocalDateTime.of(2026, 9, 1, 19, 0)));
    }

    @Test
    void bookSeat_succeeds_forAvailableSeat() {
        ReservationRequest req = new ReservationRequest();
        req.setPerformanceId(testPerformance.getId());
        req.setSeatLabel("A1");
        req.setCustomerName("Alice");

        ReservationResponse response = reservationService.bookSeat(req);

        assertThat(response.getSeatLabel()).isEqualTo("A1");
        assertThat(response.getCustomerName()).isEqualTo("Alice");
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void bookSeat_fails_whenSeatAlreadyBooked() {
        ReservationRequest req = new ReservationRequest();
        req.setPerformanceId(testPerformance.getId());
        req.setSeatLabel("B2");
        req.setCustomerName("Alice");

        reservationService.bookSeat(req); // first booking succeeds

        req.setCustomerName("Bob");

        assertThatThrownBy(() -> reservationService.bookSeat(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void cancelReservation_setsStatusToCancelled() {
        ReservationRequest req = new ReservationRequest();
        req.setPerformanceId(testPerformance.getId());
        req.setSeatLabel("C3");
        req.setCustomerName("Charlie");

        ReservationResponse booked = reservationService.bookSeat(req);

        reservationService.cancelReservation(booked.getId());

        // verify via direct DB query
        // (the service doesn't expose a getById — that's fine for a test)
    }
}
```

> **Note:** `@SpringBootTest` does **not** auto-rollback transactions. If tests create
> data, it persists across test methods in the same class. Use `@BeforeEach` to set up
> clean state, or add `@Transactional` on the test class (which brings back auto-rollback
> but may mask real transaction bugs).

---

### Task 5 — Delete `TestInMemoryShowRepository`

With real integration tests in place, the
`repository/inmemory/TestInMemoryShowRepository` class is no longer needed. Delete it.

The in-memory implementations (`DevInMemoryShowRepository`,
`InMemoryPerformanceRepository`) should already be gone from Week 09. If any remain,
remove them now.

---

### Task 6 — Update `application-test.yml`

The test profile no longer needs to suppress DB output — Testcontainers handles the
database. But you may want to silence Liquibase and Hibernate noise:

```yaml
spring:
  config:
    activate:
      on-profile: test
  jpa:
    show-sql: false

theatre:
  log-level: ERROR
  log-file: logs/theatre-test.log

logging:
  level:
    root: WARN
    bg.uni.fmi.theatre: ERROR
    org.testcontainers: INFO
    liquibase: WARN
```

---

### Bonus — Concurrent booking test

Test that two threads booking the same seat don't produce a double booking:

```java
@Test
void concurrentBooking_onlyOneSucceeds() throws Exception {
    ReservationRequest req = new ReservationRequest();
    req.setPerformanceId(testPerformance.getId());
    req.setSeatLabel("D4");

    ExecutorService executor = Executors.newFixedThreadPool(2);

    AtomicInteger successes = new AtomicInteger();
    AtomicInteger failures = new AtomicInteger();

    Runnable booking = () -> {
        try {
            ReservationRequest r = new ReservationRequest();
            r.setPerformanceId(testPerformance.getId());
            r.setSeatLabel("D4");
            r.setCustomerName(Thread.currentThread().getName());
            reservationService.bookSeat(r);
            successes.incrementAndGet();
        } catch (Exception e) {
            failures.incrementAndGet();
        }
    };

    Future<?> f1 = executor.submit(booking);
    Future<?> f2 = executor.submit(booking);
    f1.get();
    f2.get();
    executor.shutdown();

    assertThat(successes.get()).isEqualTo(1);
    assertThat(failures.get()).isEqualTo(1);
}
```

---

## Appendix — H2 fallback (if Docker is unavailable)

If you can't run Docker, use H2 as a test database. Add to `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

Replace the `AbstractDatabaseTest` approach with a simpler `application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:theatre_test;MODE=PostgreSQL
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
  liquibase:
    enabled: false
```

With `ddl-auto: create-drop`, Hibernate generates the schema from the JPA entities
instead of Liquibase. This is **not** production-equivalent (Liquibase changesets are
skipped), but it's better than no tests at all. Be aware of dialect differences — a
test passing on H2 does not guarantee it works on PostgreSQL.

---

## Summary of new files across Week 09

```
src/main/resources/db/changelog/changes/
   002-create-hall-table.sql
   003-create-performance-table.sql
   004-add-version-columns.sql
   005-create-reservation-table.sql   

src/main/java/bg/uni/fmi/theatre/
   domain/Reservation.java             
   vo/ReservationStatus.java           
   repository/HallRepository.java      
   repository/HallSeeder.java          
   repository/PerformanceSeeder.java   
   dto/PerformanceRequest.java         
   dto/ReservationRequest.java         
   dto/ReservationResponse.java        
   service/ReservationService.java     
   web/ReservationController.java      

src/test/java/bg/uni/fmi/theatre/
   AbstractDatabaseTest.java                         
   repository/ShowRepositoryTest.java                    
   repository/PerformanceRepositoryTest.java            
   service/ReservationServiceIntegrationTest.java       

Deleted:
   repository/inmemory/DevInMemoryShowRepository.java    
   repository/inmemory/InMemoryPerformanceRepository.java 
   repository/inmemory/TestInMemoryShowRepository.java    
```
