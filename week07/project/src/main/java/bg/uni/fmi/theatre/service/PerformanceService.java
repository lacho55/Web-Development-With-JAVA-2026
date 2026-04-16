package bg.uni.fmi.theatre.service;

import bg.uni.fmi.theatre.config.AppLogger;
import bg.uni.fmi.theatre.domain.*;
import bg.uni.fmi.theatre.dto.*;
import bg.uni.fmi.theatre.exception.*;
import bg.uni.fmi.theatre.repository.*;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Performance read operations.
 * Uses {@link ShowService} for show-existence validation (service-to-service pattern);
 * never accesses {@link bg.uni.fmi.theatre.repository.ShowRepository} directly.
 *
 * <p>Layering: {@code PerformanceService} → {@code PerformanceRepository} + {@code ShowService}.
 *
 * @since Week 06, Task 1
 */
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final ShowService showService;
    private final AppLogger logger;

    public PerformanceService(PerformanceRepository performanceRepository,
                               ShowService showService,
                               AppLogger logger) {
        this.performanceRepository = performanceRepository;
        this.showService = showService;
        this.logger = logger;
    }

    /**
     * Persists a new Performance, first verifying the associated Show exists via {@link ShowService}.
     *
     * @param performance the Performance to save; showId must reference an existing Show
     * @return the saved Performance as a {@link bg.uni.fmi.theatre.dto.PerformanceResponse}
     * @throws bg.uni.fmi.theatre.exception.NotFoundException if the referenced Show does not exist
     * @since Week 06, Task 1
     */
    public PerformanceResponse addPerformance(Performance performance) {
        if (performance == null) throw new ValidationException("Performance must not be null");
        // Validate the show exists via ShowService — not ShowRepository
        showService.getShowById(performance.getShowId());
        logger.debug("Adding performance for show: " + performance.getShowId());
        Performance saved = performanceRepository.save(performance);
        logger.info("Performance added: id=" + saved.getId());
        return PerformanceResponse.from(saved);
    }

    /**
     * Returns all Performances for a given Show.
     * Validates the Show exists via {@link ShowService#getShowById(Long)} before querying.
     *
     * @param showId must not be {@code null}
     * @throws bg.uni.fmi.theatre.exception.NotFoundException if no Show exists for {@code showId}
     * @since Week 06, Task 1
     */
    public List<PerformanceResponse> findPerformancesByShow(Long showId) {
        if (showId == null) throw new ValidationException("showId must not be null");
        // Validate show exists via service (throws 404 if not found)
        showService.getShowById(showId);
        logger.debug("Fetching performances for show: " + showId);
        return performanceRepository.findByShowId(showId).stream()
                .map(PerformanceResponse::from).toList();
    }

    /**
     * Returns all Performances without filtering.
     * Called by {@code GET /api/performances} when no {@code showId} parameter is supplied.
     *
     * @since Week 06, Task 1
     */
    public List<PerformanceResponse> getAllPerformances() {
        return performanceRepository.findAll().stream()
                .map(PerformanceResponse::from).toList();
    }
}
