# Week 02 — Java domain + in-memory catalogue (no Spring)

## Objectives
- Practice OOP modeling and collections/streams
- Build the domain logic that will be reused in all subsequent weeks
- Write first unit tests with JUnit 5

## Project snapshot
`week02/project/` — Plain Maven project (no Spring Boot). Domain classes, repositories, service, tests.

---

## Tasks

### Task 1 — Create Maven project + domain classes
Create a Maven project (Java 21, no Spring Boot).

Package: `bg.uni.fmi.theatre`

Implement in `domain/`:
- `Show(id, title, description, genre, durationMinutes, ageRating)`
- `Hall(id, name)`
- `Seat(id, hallId, rowLabel, seatNumber, zoneCode)`
- `Performance(id, showId, hallId, startTime)`
- `Genre` enum: DRAMA, COMEDY, MUSICAL, OPERA, BALLET, THRILLER, CHILDREN
- `AgeRating` enum: ALL, PG_12, PG_16, R_18
- `PerformanceStatus` enum: SCHEDULED, CANCELLED, FINISHED

**Validation rules (constructor checks):**
- `title` required, max 100 chars
- `durationMinutes > 0`
- `seatNumber > 0`
- `showId`, `hallId`, `startTime` required (not null)

**Acceptance criteria**
- Validation throws `IllegalArgumentException` with a meaningful message
- `equals()` and `hashCode()` based on `id`
- `toString()` returns human-readable representation

---

### Task 2 — In-memory repositories
Create package `bg.uni.fmi.theatre.repository`

Implement:
- `ShowRepository` (interface): `save`, `findById`, `findAll`, `deleteById`, `existsById`
- `PerformanceRepository` (interface): `save`, `findById`, `findAll`, `findByShowId`, `deleteById`

Create package `bg.uni.fmi.theatre.repository.inmemory`:
- `InMemoryShowRepository` — backed by `Map<Long, Show>`
- `InMemoryPerformanceRepository` — backed by `Map<Long, Performance>`

Both repositories include a `nextId()` helper using `AtomicLong`.

**Acceptance criteria**
- All CRUD methods work with in-memory data
- `findByShowId` filters correctly by showId

---

### Task 3 — CatalogueService (search + filter)
Create `bg.uni.fmi.theatre.service.CatalogueService`:

Methods:
- `addShow(Show)` — saves show, throws if null
- `getShowById(Long)` — throws `IllegalArgumentException` if not found
- `getAllShows()` — returns all shows
- `searchShows(String titleQuery, Genre genre, int page, int size)`:
  - Case-insensitive title substring match
  - Optional genre filter (null = no filter)
  - Sorted alphabetically by title
  - Returns the correct page
- `addPerformance(Performance)` — validates show exists first
- `findPerformancesByShow(Long showId)` — validates show exists

**Acceptance criteria**
- `searchShows` with blank titleQuery returns all (filtered by genre only)
- Page out of bounds returns empty list
- Invalid page (`< 0`) or size (`<= 0`) throws `IllegalArgumentException`
- Adding a performance for non-existent show throws exception

---

### Task 4 — Unit tests
Write `CatalogueServiceTest` using JUnit 5 (no Spring context needed):

Cover these cases:
1. `addShow` — valid show is saved and retrievable
2. `addShow(null)` — throws exception
3. `searchShows` by title (case-insensitive, partial match)
4. `searchShows` by genre only
5. `searchShows` with empty query returns all shows
6. `searchShows` page out of bounds returns empty list
7. `searchShows` negative page throws exception
8. `searchShows` zero size throws exception
9. `searchShows` pagination — correct page 0, page 1, page 2 sizes
10. `addPerformance` for non-existent show throws exception
11. `findPerformancesByShow` returns correct performances

**Acceptance criteria**
- All 11 tests pass: `mvn test`

---

### Bonus — Streams variants
Rewrite `searchShows` sorting and filtering using only Java Streams (no `for` loops).

---

## Submission
- Push code in `/backend` (plain Maven module, no Spring)
- Run `mvn test` and include the output or a screenshot
