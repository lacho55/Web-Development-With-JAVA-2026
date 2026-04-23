package bg.uni.fmi.theatre.dto;

import java.util.List;

/**
 * Structured LLM response for comparing two shows.
 *
 * @since Week 06 LLM Seminar, Bonus
 */
public record ShowComparisonResponse(
    String show1Title,
    String show2Title,
    String recommendation,
    String reasoning,
    List<String> prosShow1,
    List<String> prosShow2
) {}
