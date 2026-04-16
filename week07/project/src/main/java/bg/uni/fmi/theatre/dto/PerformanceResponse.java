package bg.uni.fmi.theatre.dto;

import bg.uni.fmi.theatre.domain.*;
import bg.uni.fmi.theatre.vo.*;
import java.time.LocalDateTime;

/**
 * Outbound DTO representing a scheduled Performance returned by
 * {@code GET /api/performances} and {@code GET /api/performances?showId={id}}.
 *
 * @since Week 06, Task 1
 * @see bg.uni.fmi.theatre.service.PerformanceService#getAllPerformances()
 * @see bg.uni.fmi.theatre.service.PerformanceService#findPerformancesByShow(Long)
 */
public class PerformanceResponse {
    private Long id;
    private Long showId;
    private Long hallId;
    private LocalDateTime startTime;
    private PerformanceStatus status;

    /**
     * Converts a {@link bg.uni.fmi.theatre.domain.Performance} entity to a {@code PerformanceResponse}.
     * Called exclusively by the service layer.
     */
    public static PerformanceResponse from(Performance p) {
        PerformanceResponse r = new PerformanceResponse();
        r.id = p.getId();
        r.showId = p.getShowId();
        r.hallId = p.getHallId();
        r.startTime = p.getStartTime();
        r.status = p.getStatus();
        return r;
    }

    public Long getId() { return id; }
    public Long getShowId() { return showId; }
    public Long getHallId() { return hallId; }
    public LocalDateTime getStartTime() { return startTime; }
    public PerformanceStatus getStatus() { return status; }
}
