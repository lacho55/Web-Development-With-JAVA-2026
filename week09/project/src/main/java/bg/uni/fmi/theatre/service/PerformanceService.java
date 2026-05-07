package bg.uni.fmi.theatre.service;

import bg.uni.fmi.theatre.config.AppLogger;
import bg.uni.fmi.theatre.domain.*;
import bg.uni.fmi.theatre.dto.*;
import bg.uni.fmi.theatre.exception.*;
import bg.uni.fmi.theatre.repository.*;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Performance operations.
 * Now uses JPA entities with relationships instead of Long IDs.
 *
 * @since Week 06, Task 1 (updated for Week 09)
 */
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final ShowRepository showRepository;
    private final HallRepository hallRepository;
    private final AppLogger logger;

    public PerformanceService(PerformanceRepository performanceRepository,
                               ShowRepository showRepository,
                               HallRepository hallRepository,
                               AppLogger logger) {
        this.performanceRepository = performanceRepository;
        this.showRepository = showRepository;
        this.hallRepository = hallRepository;
        this.logger = logger;
    }

    /**
     * Persists a new Performance using PerformanceRequest DTO.
     *
     * @param req the PerformanceRequest containing showId, hallId, and startTime
     * @return the saved Performance as a {@link bg.uni.fmi.theatre.dto.PerformanceResponse}
     * @throws bg.uni.fmi.theatre.exception.NotFoundException if the referenced Show or Hall does not exist
     * @since Week 09
     */
    public PerformanceResponse addPerformance(PerformanceRequest req) {
        Show show = showRepository.findById(req.getShowId())
                .orElseThrow(() -> new NotFoundException("Show", req.getShowId()));
        Hall hall = hallRepository.findById(req.getHallId())
                .orElseThrow(() -> new NotFoundException("Hall", req.getHallId()));

        Performance performance = new Performance(show, hall, req.getStartTime());
        Performance saved = performanceRepository.save(performance);
        logger.info("Performance added: id=" + saved.getId());
        return PerformanceResponse.from(saved);
    }

    /**
     * Returns all Performances for a given Show.
     * Validates the Show exists before querying.
     *
     * @param showId must not be {@code null}
     * @throws bg.uni.fmi.theatre.exception.NotFoundException if no Show exists for {@code showId}
     * @since Week 06, Task 1 (updated for Week 09)
     */
    public List<PerformanceResponse> findPerformancesByShow(Long showId) {
        if (showId == null) throw new ValidationException("showId must not be null");
        // Validate show exists (throws 404 if not found)
        showRepository.findById(showId)
                .orElseThrow(() -> new NotFoundException("Show", showId));
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
