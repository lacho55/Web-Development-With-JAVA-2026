# Spring Boot + DB (Theatre Ticketing)

For today's lab we will plug a real database into our **Theatre Ticketing** system.
Up to now every repository (e.g. `ShowRepository`, `PerformanceRepository`) has an in-memory,
`HashMap`-based implementation under `repository/inmemory/`. By the end of the lab the
`ShowRepository` will be backed by PostgreSQL, with its table managed by **Liquibase**.

# Task 0 — Get a PostgreSQL instance running

Pick **one** of the following options.

### Option A — Install PostgreSQL locally
If you do not already have PostgreSQL installed, follow this
[w3 schools guide](https://www.w3schools.com/postgresql/postgresql_install.php). Or search for similar resource.

***Note:*** You may use MySQL or any other relational DB if you prefer — just adjust the
driver / dialect / URL in the later steps.

### Option B — Run PostgreSQL with Docker
If you have Docker installed, the fastest way is:

```bash
docker run --name theatre-pg \
  -e POSTGRES_USER=theatre \
  -e POSTGRES_PASSWORD=theatre \
  -e POSTGRES_DB=theatre \
  -p 5432:5432 \
  -d postgres:17
```

Or drop this `docker-compose.yml` next to the project and run `docker compose up -d`:

```yaml
services:
  postgres:
    image: postgres:17
    container_name: theatre-pg
    environment:
      POSTGRES_USER: theatre
      POSTGRES_PASSWORD: theatre
      POSTGRES_DB: theatre
    ports:
      - "5432:5432"
    volumes:
      - theatre-pg-data:/var/lib/postgresql/data
volumes:
  theatre-pg-data:
```

Verify the DB is reachable:
```bash
docker exec -it theatre-pg psql -U theatre -d theatre -c "select version();"
```

### Create a schema
Inside the `theatre` database create a dedicated schema the application will use:

```sql
CREATE SCHEMA IF NOT EXISTS theatre;
```

You can run it via `psql`, DBeaver, IntelliJ's Database tool, or pgAdmin.

### Create the application user and grant privileges

If you installed PostgreSQL locally (Option A), you also need to create the `theatre`
user that the application will connect as. Connect as a superuser (e.g. `postgres`) and run:

```sql
CREATE USER theatre WITH PASSWORD 'theatre';
GRANT ALL PRIVILEGES ON DATABASE theatre TO theatre;
GRANT ALL ON SCHEMA theatre TO theatre;
```

From a shell with `psql` available:

```bash
psql -U postgres -c "CREATE USER theatre WITH PASSWORD 'theatre';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE theatre TO theatre;"
psql -U postgres -d theatre -c "GRANT ALL ON SCHEMA theatre TO theatre;"
```

> If you used the Docker option (B), the `theatre` user is already created by the
> `POSTGRES_USER` / `POSTGRES_PASSWORD` env vars — you can skip this step.

---

# Task 1 — Connect the Spring Boot app to the database

### 1.1 Add the required dependencies

Add the following to `project/pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

### 1.2 Configure the datasource

Add the datasource, JPA and Liquibase configuration to the existing
`project/src/main/resources/application.yml` — once we migrate the repository, the
application will always talk to the database, so there is no need for a separate profile.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/theatre?currentSchema=theatre
    username: theatre
    password: theatre
    driver-class-name: org.postgresql.Driver

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      # Liquibase owns the schema — do NOT let Hibernate create/alter tables.
      ddl-auto: validate
    properties:
      hibernate:
        default_schema: theatre
        format_sql: true
    show-sql: true

  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    default-schema: theatre
```

> Keep `ddl-auto: validate`. The schema is owned by Liquibase (Task 2). Hibernate is only
> there to check that our JPA entity matches the table.

---

# Task 2 — Create the `show` table with Liquibase

We will manage the schema with Liquibase changesets instead of letting Hibernate generate
DDL. This is the way real projects do it: reviewable, versioned, reproducible migrations.

### 2.1 Create the master changelog

`project/src/main/resources/db/changelog/db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-create-show-table.yaml
```

### 2.2 Create the first changeset — the `show` table

The `Show` domain class
(`src/main/java/bg/uni/fmi/theatre/domain/Show.java`) has the following fields:

| Field            | Java type   | Notes                                    |
|------------------|-------------|------------------------------------------|
| `id`             | `Long`      | primary key, generated                   |
| `title`          | `String`    | required, max length 100                 |
| `description`    | `String`    | free text                                |
| `genre`          | `Genre` enum| stored as `VARCHAR`                      |
| `durationMinutes`| `int`       | must be > 0                              |
| `ageRating`      | `AgeRating` enum | stored as `VARCHAR`                  |

`project/src/main/resources/db/changelog/changes/001-create-show-table.yaml`:

```yaml
databaseChangeLog:
  - changeSet:
      id: 001-create-show-table
      author: fmi
      changes:
        - createTable:
            tableName: show
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: title
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
              - column:
                  name: description
                  type: TEXT
              - column:
                  name: genre
                  type: VARCHAR(32)
              - column:
                  name: duration_minutes
                  type: INT
                  constraints:
                    nullable: false
              - column:
                  name: age_rating
                  type: VARCHAR(16)
```

Start the app. You should see Liquibase logs followed by a
`theatre.show` table in your database. Confirm with:

```sql
\dt theatre.*
SELECT * FROM theatre.show;
```

---

# Task 3 — Turn `Show` into a JPA entity

The easiest path is to annotate the existing `Show` domain class. If you prefer to keep the
pure domain class untouched, create a separate `ShowEntity` + a mapper — both approaches are
fine. The annotations you need:

- `@Entity` — marks the class as persistent. `@Table(name = "show")` if the class name
  differs from the table name.
- `@Id` — marks the primary-key field.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` — matches the `autoIncrement: true`
  column we just created.
- `@Column` — used to map non-default column names (e.g. `duration_minutes`) or constraints.
- `@Enumerated(EnumType.STRING)` — so enum values are stored as text, matching `VARCHAR`.

Sketch:

```java
@Entity
@Table(name = "show")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_rating")
    private AgeRating ageRating;

    // JPA requires a no-args constructor
    protected Show() {}

    // keep existing constructor + getters/setters/validations
}
```

Start the app again — because `ddl-auto: validate` is set, Hibernate will fail fast if the
entity and the Liquibase-managed table disagree. Fix any mismatches here.

---

# Task 4 — Migrate `ShowRepository` to the database

Today the `ShowRepository` interface
(`src/main/java/bg/uni/fmi/theatre/repository/ShowRepository.java`) is implemented by
`DevInMemoryShowRepository` (a `HashMap<Long, Show>` with a `@PostConstruct` seed). We will
replace the in-memory flavour with a Spring Data JPA one — and we do it with **zero changes
to the services or controllers**.

The trick: the methods on our custom `ShowRepository` (`save`, `findById`, `findAll`,
`deleteById`, `existsById`) are a subset of what `JpaRepository<Show, Long>` already
provides. So we can simply make `ShowRepository` **extend** `JpaRepository` — Spring Data
will generate the proxy for us, no adapter class needed.

### 4.1 Turn `ShowRepository` into a Spring Data JPA repository

Change the interface so it extends `JpaRepository<Show, Long>` and drop the now-redundant
method declarations (they all come from `JpaRepository`):

```java
package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
```

Spring Data will automatically create a proxy bean for this interface at startup. The
services already inject `ShowRepository`, so they pick up the new DB-backed bean with no
code changes. When you later need custom queries, add methods here using the
[Spring Data method-name conventions](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
(e.g. `List<Show> findByGenre(Genre genre);`) or `@Query`.

### 4.2 Seed sample shows conditionally

We still want the five sample shows from `DevInMemoryShowRepository.seed()` to be available
when the DB is empty (e.g. first run, or after a `docker compose down -v`). Extract the
seed logic into a dedicated bean that **only inserts rows when the table is empty** — that
way restarts don't duplicate data, and a DBA-loaded DB is never overwritten.

> Do this step **before** section 4.3 — you'll copy the five `save(new Show(...))` calls
> straight out of `DevInMemoryShowRepository.seed()`, so keep that class around for now.

`src/main/java/bg/uni/fmi/theatre/repository/ShowSeeder.java`:

```java
package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ShowSeeder implements CommandLineRunner {

    private final ShowRepository shows;

    public ShowSeeder(ShowRepository shows) {
        this.shows = shows;
    }

    @Override
    public void run(String... args) {
        if (shows.count() > 0) {
            return; // already seeded — do nothing
        }

        shows.save(new Show(null, "Hamlet",
                "William Shakespeare's timeless tragedy...",
                Genre.DRAMA, 180, AgeRating.PG_16));

        shows.save(new Show(null, "Chicago",
                "Set in the jazz age of the 1920s...",
                Genre.MUSICAL, 135, AgeRating.PG_12));

        // ... copy the remaining shows from DevInMemoryShowRepository.seed()
    }
}
```

> Pass `null` (or drop the id from the constructor) — the DB generates it via
> `GenerationType.IDENTITY`. The old in-memory `nextId()` is no longer needed.

### 4.3 Delete the in-memory implementation

Once the seeder is in place, there must be **exactly one** bean implementing
`ShowRepository`, so delete `DevInMemoryShowRepository` entirely (or remove its
`@Repository` annotation). With the JPA-backed interface from 4.1, Spring will now wire
the Spring Data proxy into every service.

The service layer depends on the `ShowRepository` interface, so swapping `HashMap` for
Postgres requires **no changes in the services or controllers**. The new DB-backed bean is
always wired in, regardless of the active profile (`dev`, `stage`, `prod`, …), because we
put the datasource configuration in the default `application.yml`.

> Alternative: add an `INSERT` changeset after `001-create-show-table.yaml` — this moves
> the seed data into Liquibase and makes it part of the schema migration history. Pick
> whichever feels more natural for your project; the `CommandLineRunner` approach is
> simpler for a lab.

---

# Task 5 — Test through the REST API

Run the app and exercise `ShowController` via Postman
(or Swagger UI at `/swagger-ui.html`):

- `POST /shows` — create a show; confirm it appears in `theatre.show`.
- `GET /shows` — list all shows from the DB.
- `GET /shows/{id}` — fetch a single show.
- `PUT /shows/{id}` — update and re-fetch.
- `DELETE /shows/{id}` — delete and confirm the row is gone.

Restart the app and call `GET /shows` again — this time the data is still there, because
the repository is no longer a `HashMap`.

---

# Task 6 — Next time (or homework)

Once `ShowRepository` is on the DB, repeat the exercise for `PerformanceRepository`
(`Performance` references a `Show` and a `Hall`). That will introduce foreign keys and JPA
relationships (`@ManyToOne`, `@OneToMany`) — the topic of the next lab.
