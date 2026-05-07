package bg.uni.fmi.theatre.dto;

import bg.uni.fmi.theatre.vo.AgeRating;
import bg.uni.fmi.theatre.vo.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Inbound DTO for creating or updating a {@link bg.uni.fmi.theatre.domain.Show}.
 * Bean Validation constraints are enforced by Spring before the request reaches the service layer.
 *
 * @since Week 06, Task 2
 * @see bg.uni.fmi.theatre.service.ShowService#addShow(ShowRequest)
 * @see bg.uni.fmi.theatre.service.ShowService#updateShow(Long, ShowRequest)
 */
public class ShowRequest {
    @NotBlank(message = "title is required")
    @Size(max = 100, message = "title must be at most 100 characters")
    private String title;
    private String description;
    private Genre genre;
    @Positive(message = "durationMinutes must be positive")
    private int durationMinutes;
    private AgeRating ageRating;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public AgeRating getAgeRating() { return ageRating; }
    public void setAgeRating(AgeRating ageRating) { this.ageRating = ageRating; }
}
