package bg.uni.fmi.theatre.domain;
import bg.uni.fmi.theatre.vo.*;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "show")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genre genre;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_rating", nullable = false, length = 10)
    private AgeRating ageRating;

    protected Show() {}  // JPA requires a no-args constructor

    public Show(String title, String description, Genre genre, int durationMinutes, AgeRating ageRating) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title is required");
        if (title.length() > 100) throw new IllegalArgumentException("title must be at most 100 characters");
        if (durationMinutes <= 0) throw new IllegalArgumentException("durationMinutes must be positive");
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
