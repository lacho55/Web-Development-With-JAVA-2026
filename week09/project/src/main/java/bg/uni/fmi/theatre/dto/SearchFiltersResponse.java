package bg.uni.fmi.theatre.dto;

/**
 * Structured LLM response for natural language → search filter extraction.
 *
 * @since Week 06 LLM Seminar, Task 3
 */
public record SearchFiltersResponse(
    String titleKeyword,
    String genre,
    Integer maxDurationMinutes
) {}
