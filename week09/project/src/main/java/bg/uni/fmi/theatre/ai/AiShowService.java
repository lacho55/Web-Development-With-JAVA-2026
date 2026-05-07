package bg.uni.fmi.theatre.ai;

import bg.uni.fmi.theatre.dto.ShowComparisonResponse;
import bg.uni.fmi.theatre.dto.ShowRecommendationResponse;
import bg.uni.fmi.theatre.dto.ShowResponse;
import bg.uni.fmi.theatre.dto.ShowSummaryResponse;
import bg.uni.fmi.theatre.dto.SearchFiltersResponse;
import bg.uni.fmi.theatre.service.ShowService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

/**
 * AI-powered service for the theatre domain.
 *
 * <p>Contains the lecture demo methods (free-text, structured, temperature)
 * plus seminar task methods (summary, NL search, comparison).
 *
 * @since Week 06 LLM Seminar
 */
@Service
public class AiShowService {

    private final ChatClient chatClient;
    private final ShowService showService;

    public AiShowService(ChatClient.Builder builder, ShowService showService) {
        this.chatClient = builder
                .defaultSystem("You are a helpful theatre assistant. "
                        + "You recommend shows based on user preferences. "
                        + "Keep answers concise — 2-3 sentences max.")
                .build();
        this.showService = showService;
    }

    // ── Lecture demo methods (already implemented, for reference) ────────────

    public String recommendShow(String userPreferences) {
        return chatClient.prompt()
                .user("I'm looking for: " + userPreferences
                        + ". Recommend a theatre show and explain why.")
                .call()
                .content();
    }

    public ShowRecommendationResponse recommendShowStructured(String userPreferences) {
        return chatClient.prompt()
                .user("I'm looking for: " + userPreferences
                        + ". Recommend a theatre show.")
                .call()
                .entity(ShowRecommendationResponse.class);
    }

    public ShowRecommendationResponse recommendWithTemperature(String userPreferences, double temperature) {
        return chatClient.prompt()
                .user("I'm looking for: " + userPreferences
                        + ". Recommend a theatre show.")
                .options(ChatOptions.builder().temperature(temperature).build())
                .call()
                .entity(ShowRecommendationResponse.class);
    }

    // ── Seminar Task 1 — Generate a show summary ────────────────────────────

    /**
     * Fetches a show by ID and asks the LLM to generate an event summary.
     */
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

    // ── Seminar Task 2 — Natural language search ────────────────────────────

    /**
     * Parses a natural language query into structured search filters.
     */
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

    // ── Bonus — Compare two shows ───────────────────────────────────────────

    /**
     * Compares two shows for a given occasion.
     */
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
}