package bg.uni.fmi.theatre.dto;

import java.util.List;

/**
 * Structured LLM response for an AI-generated event summary.
 *
 * @since Week 06 LLM Seminar, Task 1
 */
public record ShowSummaryResponse(
    String summary,
    String targetAudience,
    List<String> highlights
) {}
