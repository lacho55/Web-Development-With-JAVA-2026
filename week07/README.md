# Week 07 LLM Practicum — Applying Spring AI to the Theatre API

## Objectives
- Set up Spring AI with Ollama in an existing Spring Boot project
- Call an LLM from a service and expose the result via a REST endpoint
- Use **structured output** to map LLM responses to typed Java records
- Practice prompt engineering with different record shapes

## Prerequisites
- You watched the lecture demo (free-text call, structured output, temperature experiment)
- Ollama running locally with `gemma4` model (or an OpenAI/Anthropic API key)

## Project snapshot
`week06_LLM_seminar/project/` — Week 06 project with Spring AI already configured (Ollama starter in pom.xml, `application-dev.yml` pointing to `localhost:11434`). The lecture demo endpoints are included as reference. You'll add new endpoints.

---

## Tasks

### Task 1 — Generate a show summary

When a user opens a show page, we want to display an AI-generated summary of the event. Build an endpoint that fetches a show by ID and asks the LLM to create a summary.

Create a record in `dto/`:
```java
public record ShowSummaryResponse(
    String summary,
    String targetAudience,
    List<String> highlights
) {}
```

Add a method to `AiShowService`:
```java
public ShowSummaryResponse generateSummary(Long showId)
```

This method should:
1. Fetch the show by ID via `ShowService.getShowById(showId)`
2. Send the show's title, genre, duration, and age rating to the LLM
3. Return a structured `ShowSummaryResponse`

Add endpoint to `AiController`:
```
GET /api/ai/shows/{id}/summary
```

**Acceptance criteria**
- `GET /api/ai/shows/1/summary` returns a JSON object with `summary`, `targetAudience`, and `highlights`
- `highlights` contains 2-4 short bullet points about the show
- `GET /api/ai/shows/999/summary` returns 404 (show not found)

<details>
<summary>Hint — Task 1</summary>

Inject `ShowService` into `AiShowService` via constructor:

```java
private final ShowService showService;

public AiShowService(ChatClient.Builder builder, ShowService showService) {
    this.chatClient = builder
            .defaultSystem("You are a helpful theatre assistant.")
            .build();
    this.showService = showService;
}
```

The method:

```java
public ShowSummaryResponse generateSummary(Long showId) {
    ShowResponse show = showService.getShowById(showId);

    return chatClient.prompt()
            .user("Generate a summary for this theatre event:\n"
                  + "Title: " + show.getTitle() + "\n"
                  + "Genre: " + show.getGenre() + "\n"
                  + "Duration: " + show.getDurationMinutes() + " minutes\n"
                  + "Age rating: " + show.getAgeRating() + "\n\n"
                  + "Write a 2-3 sentence summary, identify the target audience, "
                  + "and list 2-4 highlights.")
            .call()
            .entity(ShowSummaryResponse.class);
}
```

`getShowById` throws `NotFoundException` if the show doesn't exist — it propagates to `GlobalExceptionHandler` which returns 404 automatically. No try-catch needed.

The controller:

```java
@GetMapping("/shows/{id}/summary")
public ShowSummaryResponse showSummary(@PathVariable Long id) {
    return aiShowService.generateSummary(id);
}
```
</details>

---

### Task 2 — Natural language search

Users want to search shows by typing natural language: "something short and funny for teenagers". Our existing `GET /api/shows` accepts structured parameters (`title`, `genre`, `page`, `size`). Use the LLM as a **translation layer** between human language and our typed API.

Create a record:
```java
public record SearchFiltersResponse(
    String titleKeyword,
    String genre,
    Integer maxDurationMinutes
) {}
```

Add a method to `AiShowService`:
```java
public SearchFiltersResponse parseSearchQuery(String naturalLanguageQuery)
```

Add endpoint:
```
GET /api/ai/search?q=short comedy for teenagers
```

This endpoint should:
1. Call `parseSearchQuery(q)` to extract filters
2. Pass the filters to `showService.searchShows(...)` 
3. Return the matching shows

**Acceptance criteria**
- `GET /api/ai/search?q=short comedy for teenagers` returns shows filtered by genre/duration
- `GET /api/ai/search?q=hamlet` returns shows matching "hamlet" in the title
- The LLM maps "funny" → COMEDY, "short" → maxDurationMinutes ~120

<details>
<summary>Hint — Task 2</summary>

The prompt must tell the LLM **which values are valid**:

```java
public SearchFiltersResponse parseSearchQuery(String naturalLanguageQuery) {
    return chatClient.prompt()
            .user("Extract search filters from this query: \"" + naturalLanguageQuery + "\"\n\n"
                  + "Available genres: DRAMA, COMEDY, MUSICAL, OPERA, BALLET\n"
                  + "Rules:\n"
                  + "- titleKeyword: a keyword to match show titles, or null\n"
                  + "- genre: one of the available genres, or null if not mentioned\n"
                  + "- maxDurationMinutes: max duration if mentioned (e.g. 'short' = 120), or null\n")
            .call()
            .entity(SearchFiltersResponse.class);
}
```

In the controller, map parsed filters to the existing search:

```java
@GetMapping("/search")
public PageResponse<ShowResponse> naturalLanguageSearch(@RequestParam String q) {
    SearchFiltersResponse filters = aiShowService.parseSearchQuery(q);
    Genre genre = null;
    if (filters.genre() != null) {
        try { genre = Genre.valueOf(filters.genre()); }
        catch (IllegalArgumentException ignored) {}
    }
    return showService.searchShows(filters.titleKeyword(), genre, 0, 10);
}
```
</details>

---

### Bonus — Compare two shows

Create an endpoint that helps a user choose between two shows:

```
GET /api/ai/compare?showId1=1&showId2=2&occasion=date night
```

Create a record:
```java
public record ShowComparisonResponse(
    String show1Title,
    String show2Title,
    String recommendation,
    String reasoning
) {}
```

Fetch both shows, send their details + the occasion to the LLM, return a structured comparison.

**Acceptance criteria**
- The `recommendation` field contains one of the two show titles
- Invalid show IDs return 404

<details>
<summary>Hint — Bonus</summary>

```java
public ShowComparisonResponse compareShows(Long showId1, Long showId2, String occasion) {
    ShowResponse show1 = showService.getShowById(showId1);
    ShowResponse show2 = showService.getShowById(showId2);

    return chatClient.prompt()
            .user("Compare these two theatre shows for a " + occasion + ":\n\n"
                  + "Show 1: " + show1.getTitle() + " (" + show1.getGenre()
                  + ", " + show1.getDurationMinutes() + " min)\n"
                  + "Show 2: " + show2.getTitle() + " (" + show2.getGenre()
                  + ", " + show2.getDurationMinutes() + " min)\n\n"
                  + "Which show is better for this occasion and why?")
            .call()
            .entity(ShowComparisonResponse.class);
}
```
</details>

---

## Running the project

```bash
# Make sure Ollama is running
ollama list   # should show gemma4

# Start the app
cd week06_LLM_seminar/project
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Open Swagger UI
open http://localhost:8099/swagger-ui.html
```

If using OpenAI/Anthropic instead of Ollama, swap the starter in `pom.xml` and update `application-dev.yml`.

## Submission
- Push code
- Include a screenshot showing one show summary (Task 1) and one natural language search result (Task 2)
