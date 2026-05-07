package bg.uni.fmi.theatre.ai;

import bg.uni.fmi.theatre.dto.*;
import bg.uni.fmi.theatre.service.ShowService;
import bg.uni.fmi.theatre.vo.Genre;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for AI-powered theatre features.
 *
 * @since Week 06 LLM Seminar
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiShowService aiShowService;
    private final ShowService showService;

    public AiController(AiShowService aiShowService, ShowService showService) {
        this.aiShowService = aiShowService;
        this.showService = showService;
    }

    // ── Lecture demo endpoints (for reference) ──────────────────────────────

    @GetMapping("/recommend")
    public String recommend(@RequestParam String preferences) {
        return aiShowService.recommendShow(preferences);
    }

    @GetMapping("/recommend/structured")
    public ShowRecommendationResponse recommendStructured(@RequestParam String preferences) {
        return aiShowService.recommendShowStructured(preferences);
    }

    @GetMapping("/recommend/experiment")
    public ShowRecommendationResponse experiment(
            @RequestParam String preferences,
            @RequestParam(defaultValue = "0.7") double temperature) {
        return aiShowService.recommendWithTemperature(preferences, temperature);
    }

    // ── Task 1 — Show summary ───────────────────────────────────────────────

    @GetMapping("/shows/{id}/summary")
    public ShowSummaryResponse showSummary(@PathVariable Long id) {
        return aiShowService.generateSummary(id);
    }

    // ── Task 2 — Natural language search ────────────────────────────────────

    @GetMapping("/search")
    public PageResponse<ShowResponse> naturalLanguageSearch(
            @RequestParam String q) {
        SearchFiltersResponse filters = aiShowService.parseSearchQuery(q);
        Genre genre = null;
        // change the example from unstructured to structured
        // to the DB Tool call
        if (filters.genre() != null) {
            try { genre = Genre.valueOf(filters.genre()); }
            catch (IllegalArgumentException ignored) {}
        }
        return showService.searchShows(filters.titleKeyword(), genre, 0, 10);
    }

    // ── Bonus — Compare two shows ───────────────────────────────────────────

    @GetMapping("/compare")
    public ShowComparisonResponse compareShows(
            @RequestParam Long showId1,
            @RequestParam Long showId2,
            @RequestParam(defaultValue = "evening out") String occasion) {
        return aiShowService.compareShows(showId1, showId2, occasion);
    }
}
