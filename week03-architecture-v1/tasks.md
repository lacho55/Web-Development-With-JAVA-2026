# Week 01 — Course kickoff, tooling, repository

## Objectives
- Standardize tools and repository structure across the team
- Define MVP scope and backlog as GitHub Issues
- Write first Java domain classes to get familiar with the domain

## Project snapshot
`week01/project/` — Spring Boot skeleton with folder structure.

---

## Tasks

### Task 0 — Setup repository
**Requirements**
- Create repo `theatre-ticketing-system`
- Add folders: `/backend`, `/frontend`, `/docs`, `/week01..week10`
- Add `.gitignore` (Java + IntelliJ + Node/Angular)
- Add `README.md` with "How to run" placeholder

**Acceptance criteria**
- Repository exists and has an initial commit

---

### Task 1 — Add documentation skeleton
Create:
- `docs/00-project-overview.md`
- `docs/01-db-design.md`
- `docs/02-business-flows.md`
- `docs/03-project-structure.md`

**Acceptance criteria**
- All docs present and readable
- Mermaid diagrams render on GitHub

---

### Task 2 — Define MVP backlog as GitHub Issues
Create issues for:
- domain model (shows, performances, halls/seats)
- CLI runner MVP (Week 3)
- DI + profile-based beans (Week 5)
- REST endpoints (Week 6)
- JPA + Postgres (Week 7)
- Flyway migrations (Week 8)
- reservation + concurrency (Week 9)
- Angular shows list/details (Week 10)

Label issues: `backend`, `frontend`, `docs`, `bonus`

**Acceptance criteria**
- Issues created with correct labels

---

### Task 3 — First domain classes (no Spring, no Maven setup needed)
Create three plain Java classes in your IDE — no Maven project needed yet.

Implement:
- `Show(id, title, genre, durationMinutes)` with:
  - title required, max 100 characters
  - durationMinutes > 0
- `Hall(id, name)` — name required
- `Performance(id, showId, hallId, startTime)` — all fields required

**Acceptance criteria**
- Constructor throws `IllegalArgumentException` on invalid input
- Demo creation of a Show, Hall, and Performance in a `main()` method

**Why this task?**
Familiarity with the domain before Week 02 builds it fully in Maven.

---

## Submission
- Push to main/master
- Provide repository link
