package bg.uni.fmi.theatre.dto;

import bg.uni.fmi.theatre.domain.*;
import bg.uni.fmi.theatre.vo.*;

/**
 * Outbound DTO representing a {@link bg.uni.fmi.theatre.domain.Show}.
 * Assembled inside the service layer via {@link #from(bg.uni.fmi.theatre.domain.Show)};
 * the entity itself never crosses the service boundary.
 *
 * @since Week 06, Task 2
 */
public class ShowResponse {
    private Long id;
    private String title;
    private String description;
    private Genre genre;
    private int durationMinutes;
    private AgeRating ageRating;

    /**
     * Converts a {@link bg.uni.fmi.theatre.domain.Show} entity to a {@code ShowResponse}.
     * Called exclusively by the service layer.
     */
    public static ShowResponse from(Show show) {
        ShowResponse r = new ShowResponse();
        r.id = show.getId();
        r.title = show.getTitle();
        r.description = show.getDescription();
        r.genre = show.getGenre();
        r.durationMinutes = show.getDurationMinutes();
        r.ageRating = show.getAgeRating();
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Genre getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public AgeRating getAgeRating() { return ageRating; }
}
