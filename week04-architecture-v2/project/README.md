# Week 02 — Java Domain + In-Memory Catalogue

## What's new this week
- Domain classes: `Show`, `Hall`, `Seat`, `Performance` with constructor validation
- Enums: `Genre`, `AgeRating`, `PerformanceStatus`
- Repository interfaces: `ShowRepository`, `PerformanceRepository`
- In-memory implementations backed by `HashMap<Long, T>`
- `CatalogueService` with search (title + genre filter), pagination, sorted by title
- Unit tests for `CatalogueService` (11 test cases)

## No Spring DI yet
All wiring is manual. Spring DI will be introduced in Week 05.

## How to run tests
```bash
mvn test
```
