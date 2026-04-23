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
replace that in-memory implementation with a DB-backed one. There must be **exactly one**
bean implementing `ShowRepository`, so either delete `DevInMemoryShowRepository` or comment
out its `@Repository` annotation. (An alternative would be to use a dedicated profile for DB and conditional beans)

### 4.1 Create a Spring Data JPA repository

`src/main/java/bg/uni/fmi/theatre/repository/jpa/ShowJpaRepository.java`:

```java
package bg.uni.fmi.theatre.repository.jpa;

import bg.uni.fmi.theatre.domain.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowJpaRepository extends JpaRepository<Show, Long> {
}
```

### 4.2 Implement `ShowRepository` on top of it

`src/main/java/bg/uni/fmi/theatre/repository/jpa/DbShowRepository.java`:

```java
package bg.uni.fmi.theatre.repository.jpa;

import bg.uni.fmi.theatre.domain.Show;
import bg.uni.fmi.theatre.repository.ShowRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbShowRepository implements ShowRepository {

    private final ShowJpaRepository jpa;

    public DbShowRepository(ShowJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Show save(Show show) { return jpa.save(show); }
    @Override public Optional<Show> findById(Long id) { return jpa.findById(id); }
    @Override public List<Show> findAll() { return jpa.findAll(); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
}
```

The service layer depends on the `ShowRepository` interface, not on any implementation, so
swapping `HashMap` for Postgres requires **no changes in the services or controllers**. The
new DB-backed bean is always wired in, regardless of the active profile (`dev`, `stage`,
`prod`, …), because we put the datasource configuration in the default `application.yml`.

### 4.3 Optional — seed some data

Either add an `INSERT` changeset after `001-create-show-table.yaml`, or copy the seed
shows from `DevInMemoryShowRepository.seed()` into a small `CommandLineRunner` that inserts
them when the table is empty.

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
