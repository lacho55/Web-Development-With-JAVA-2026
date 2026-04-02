# Week 06 — REST API + DTOs + validation + error handling

## Objectives
- Convert from CLI-first to REST-first
- Introduce controllers, DTOs, bean validation, and consistent error responses
- Document endpoints with Swagger UI (OpenAPI)

## Project snapshot
`week06/project/` — `spring-boot-starter-web` added. Full REST API with CRUD for shows, performances list, validated request DTOs, global exception handler, and Swagger UI (OpenAPI) documentation.

---

## Tasks

### Task 1 — Add Spring Web and first endpoints
Add `spring-boot-starter-web` + `spring-boot-starter-validation` to `pom.xml`.

Create `ShowController` with endpoints:
```
GET  /api/shows                       — list all shows (with title/genre filters, pagination)
GET  /api/shows/{id}                  — get show by ID
```

Create `PerformanceController`:
```
GET  /api/performances?showId={id}    — list performances (optional show filter)
```

**Acceptance criteria**
- JSON responses
- `GET /api/shows` returns all seeded shows in dev profile
- `GET /api/shows/999` returns 404 with a JSON error body (not Spring's default white-label)

<details>
<summary>💡 Hint — Task 1</summary>

Annotate the controller class with `@RestController` and `@RequestMapping("/api/shows")`.
Inject the service via constructor — avoid field-level `@Autowired`.

```java
@RestController
@RequestMapping("/api/shows")
public class ShowController {
    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/{id}")
    public ShowResponse getShow(@PathVariable Long id) {
        return showService.getShowById(id);
    }
}
```

Throw `NotFoundException` from the service when a Show is missing — the global exception handler (Task 3) converts it to a 404 JSON response.
</details>

---

### Task 2 — DTOs + validation
Create:

**`ShowRequest`** (for POST/PUT):
- `@NotBlank` + `@Size(max=100)` on `title`
- `@Positive` on `durationMinutes`

**`ShowResponse`** (for GET responses):
- Maps `Show` → response JSON

**`PerformanceResponse`** — maps `Performance` → response JSON

Add full CRUD for shows:
```
POST   /api/shows          — create show (validated body)
PUT    /api/shows/{id}     — update show (validated body)
DELETE /api/shows/{id}     — delete show
```

**Acceptance criteria**
- `POST /api/shows` with blank title → 400 with message `"title is required"`
- `POST /api/shows` with `durationMinutes: -5` → 400 with message `"durationMinutes must be positive"`
- `POST /api/shows` with valid body → 201 Created + `ShowResponse` body

<details>
<summary>💡 Hint — Task 2</summary>

Add `@Valid` on the `@RequestBody` parameter so Spring triggers Bean Validation before the method body runs:

```java
@PostMapping
public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(showService.addShow(req));
}
```

Key constraints in `ShowRequest`:
```java
@NotBlank(message = "title is required")
@Size(max = 100, message = "title must be at most 100 characters")
private String title;

@Positive(message = "durationMinutes must be positive")
private int durationMinutes;
```

Use a static factory method in `ShowResponse` to assemble the DTO from the entity — keep all mapping logic out of the controller.
</details>

---

### Task 3 — Global exception handler
Create `web/GlobalExceptionHandler.java` with `@RestControllerAdvice`:

Handle:
- `NotFoundException` → 404
- `ValidationException` → 400
- `MethodArgumentNotValidException` → 400 (bean validation failures)
- `Exception` → 500

All responses use this consistent JSON structure:
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 404,
  "message": "Show not found with id: 99",
  "path": "/api/shows/99"
}
```

**Acceptance criteria**
- Every error returns the above JSON (not Spring's default error format)
- 404 and 400 are consistent across all endpoints

<details>
<summary>💡 Hint — Task 3</summary>

Annotate the handler class with `@RestControllerAdvice`. Handle each exception type with `@ExceptionHandler`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).findFirst().orElse("Validation failed");
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, msg, req.getRequestURI()));
    }
}
```

Create a simple `ErrorResponse` class (or record) with `timestamp`, `status`, `message`, and `path` fields.
</details>

---

### Task 4 — Pagination response
Create `PageResponse<T>` wrapping list results:
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 5
}
```

`GET /api/shows?page=0&size=10&title=ham` returns a `PageResponse<ShowResponse>`.

**Acceptance criteria**
- Pagination parameters work correctly
- `totalElements` reflects the total count before pagination

<details>
<summary>💡 Hint — Task 4</summary>

`PageResponse<T>` is a plain generic class — not a Spring type:

```java
public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    // constructor + getters
}
```

In week 06 (in-memory repository), implement filtering and slicing in the service:
```java
List<ShowResponse> all = // filter from repository
long total = all.size();
List<ShowResponse> slice = all.subList(
    page * size, Math.min((page + 1) * size, all.size()));
return new PageResponse<>(slice, page, size, total);
```

From week 08 onward this is replaced by Spring Data's `Page<T>` backed by a database query.
</details>

---

### Task 5 — Swagger UI (OpenAPI documentation)
Add `springdoc-openapi-starter-webmvc-ui` to `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

Annotate **`ShowController`** with OpenAPI annotations:
- `@Tag` on the class — groups all endpoints under "Shows"
- `@Operation` on each method — summary + description
- `@ApiResponse` — document status codes (200, 201, 204, 400, 404)
- `@Parameter` on query parameters — describe filters and pagination

Leave `PerformanceController` without annotations — Swagger UI will still pick it up via classpath scanning, but without enriched descriptions. This lets students compare annotated vs. unannotated output.

**Acceptance criteria**
- `http://localhost:8080/swagger-ui.html` opens the interactive API explorer
- ShowController endpoints show summaries, parameter descriptions, and response codes
- PerformanceController endpoints appear but with default (unannotated) documentation
- Students can execute requests directly from Swagger UI (try one valid create + one invalid to see 201 and 400)

<details>
<summary>💡 Hint — Task 5</summary>

The `springdoc-openapi-starter-webmvc-ui` dependency auto-configures everything — no `@Bean` or YAML config needed.

Key annotations to import from `io.swagger.v3.oas.annotations`:

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
```

Example on the controller class:
```java
@RestController
@RequestMapping("/api/shows")
@Tag(name = "Shows", description = "CRUD operations for theatre shows")
public class ShowController { ... }
```

Example on a method:
```java
@GetMapping("/{id}")
@Operation(summary = "Get show by ID")
@ApiResponse(responseCode = "200", description = "Show found")
@ApiResponse(responseCode = "404", description = "Show not found")
public ShowResponse getShow(@PathVariable Long id) { ... }
```

Swagger UI is available at `http://localhost:8080/swagger-ui.html`.
The raw OpenAPI spec is at `http://localhost:8080/v3/api-docs`.
</details>

---

## Submission
- Push code
- Include a screenshot of Swagger UI showing the annotated ShowController endpoints
