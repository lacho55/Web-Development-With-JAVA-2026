package bg.uni.fmi.theatre.domain;
import bg.uni.fmi.theatre.vo.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "performance")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceStatus status;

    @Version
    private Long version;

    protected Performance() {}

    public Performance(Show show, Hall hall, LocalDateTime startTime) {
        if (show == null) throw new IllegalArgumentException("show is required");
        if (hall == null) throw new IllegalArgumentException("hall is required");
        if (startTime == null) throw new IllegalArgumentException("startTime is required");
        this.show = show;
        this.hall = hall;
        this.startTime = startTime;
        this.status = PerformanceStatus.SCHEDULED;
    }

    // getters, setters, equals/hashCode
    public Long getId() { return id; }
    public Show getShow() { return show; }
    public Hall getHall() { return hall; }
    public LocalDateTime getStartTime() { return startTime; }
    public PerformanceStatus getStatus() { return status; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setStatus(PerformanceStatus status) { this.status = status; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Performance p)) return false;
        return Objects.equals(id, p.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() {
        return "Performance{id=" + id + ", show=" + show.getTitle() + ", hall=" + hall.getName() + ", start=" + startTime + ", status=" + status + "}";
    }
}
