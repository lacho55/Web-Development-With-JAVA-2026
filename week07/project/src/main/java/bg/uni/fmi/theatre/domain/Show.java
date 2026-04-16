package bg.uni.fmi.theatre.domain;
import bg.uni.fmi.theatre.vo.*;

import java.util.Objects;

public class Show {
    private final Long id;
    private String title;
    private String description;
    private Genre genre;
    private int durationMinutes;
    private AgeRating ageRating;

    public Show(Long id, String title, String description, Genre genre, int durationMinutes, AgeRating ageRating) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title is required");
        if (title.length() > 100) throw new IllegalArgumentException("title must be at most 100 characters");
        if (durationMinutes <= 0) throw new IllegalArgumentException("durationMinutes must be positive");
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.ageRating = ageRating;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Genre getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public AgeRating getAgeRating() { return ageRating; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setAgeRating(AgeRating ageRating) { this.ageRating = ageRating; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Show s)) return false;
        return Objects.equals(id, s.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() {
        return "Show{id=" + id + ", title='" + title + "', genre=" + genre + ", duration=" + durationMinutes + "min}";
    }
}
