package bg.uni.fmi.theatre.domain;
import bg.uni.fmi.theatre.vo.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Performance {
    private final Long id;
    private final Long showId;
    private final Long hallId;
    private LocalDateTime startTime;
    private PerformanceStatus status;

    public Performance(Long id, Long showId, Long hallId, LocalDateTime startTime) {
        if (showId == null) throw new IllegalArgumentException("showId is required");
        if (hallId == null) throw new IllegalArgumentException("hallId is required");
        if (startTime == null) throw new IllegalArgumentException("startTime is required");
        this.id = id;
        this.showId = showId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.status = PerformanceStatus.SCHEDULED;
    }

    public Long getId() { return id; }
    public Long getShowId() { return showId; }
    public Long getHallId() { return hallId; }
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
        return "Performance{id=" + id + ", showId=" + showId + ", start=" + startTime + ", status=" + status + "}";
    }
}
