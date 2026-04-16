package bg.uni.fmi.theatre.dto;

/**
 * Structured response from the LLM for a show recommendation.
 * Spring AI maps the LLM's JSON output directly into this record.
 *
 * @since Week 06 LLM Seminar, Task 3
 */
public record ShowRecommendationResponse(
    String title,
    String genre,
    String targetAudience,
    String reason,
    int estimatedDurationMinutes
) {}
