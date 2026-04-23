# Week 06 — REST API + DTOs + Validation + Error Handling

## What's new this week
- `spring-boot-starter-web` + `spring-boot-starter-validation` added
- REST controllers: `ShowController`, `PerformanceController`
- DTOs: `ShowRequest` (with @NotBlank/@Positive validation), `ShowResponse`, `PerformanceResponse`
- `GlobalExceptionHandler` (@RestControllerAdvice) — consistent JSON error format
- `PageResponse<T>` — paginated list wrapper
- Full CRUD for shows: GET/POST/PUT/DELETE

## Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET    | /api/shows?title=&genre=&page=&size= | List/search shows |
| GET    | /api/shows/{id} | Get show by ID |
| POST   | /api/shows | Create show (validated) |
| PUT    | /api/shows/{id} | Update show |
| DELETE | /api/shows/{id} | Delete show |
| GET    | /api/performances?showId= | List performances |

## How to run
```bash
mvn spring-boot:run
# Test:
curl http://localhost:8080/api/shows
curl http://localhost:8080/api/shows/999   # → 404
curl -X POST http://localhost:8080/api/shows -H 'Content-Type: application/json' -d '{}'  # → 400
```
