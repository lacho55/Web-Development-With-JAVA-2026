package bg.uni.fmi.theatre.service;

import bg.uni.fmi.theatre.config.AppLogger;
import bg.uni.fmi.theatre.config.TheatreProperties;
import bg.uni.fmi.theatre.domain.*;
import bg.uni.fmi.theatre.vo.*;
import bg.uni.fmi.theatre.dto.*;
import bg.uni.fmi.theatre.exception.*;
import bg.uni.fmi.theatre.repository.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service for Show CRUD operations.
 * Accepts {@link bg.uni.fmi.theatre.dto.ShowRequest} DTOs and returns {@link bg.uni.fmi.theatre.dto.ShowResponse} DTOs;
 * the {@link bg.uni.fmi.theatre.domain.Show} entity stays internal.
 *
 * <p>Layering: {@code ShowService} → {@code ShowRepository} only.
 *
 * @since Week 06, Task 1
 */
@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final AppLogger logger;
    private final int defaultPageSize;
    // ID generation belongs in the service layer, not the controller
    private final AtomicLong idSeq = new AtomicLong(100);

    public ShowService(ShowRepository showRepository,
                       AppLogger logger,
                       TheatreProperties properties) {
        this.showRepository = showRepository;
        this.logger = logger;
        this.defaultPageSize = properties.getDefaultPageSize();
    }

    /**
     * Persists a new Show.
     * Generates an ID via an in-memory sequence (week 06 uses an in-memory repository).
     *
     * @param req the Show data; must not be {@code null}
     * @return the saved Show as a {@link bg.uni.fmi.theatre.dto.ShowResponse}
     * @throws bg.uni.fmi.theatre.exception.ValidationException if {@code req} is {@code null}
     * @since Week 06, Task 2
     */
    public ShowResponse addShow(ShowRequest req) {
        if (req == null) throw new ValidationException("Show must not be null");
        Show show = new Show(idSeq.getAndIncrement(), req.getTitle(), req.getDescription(),
                req.getGenre(), req.getDurationMinutes(), req.getAgeRating());
        logger.debug("Adding show: " + show.getTitle());
        Show saved = showRepository.save(show);
        logger.info("Show added: [" + saved.getId() + "] " + saved.getTitle());
        return ShowResponse.from(saved);
    }

    /**
     * Returns a Show by its identifier.
     * Also used by {@link PerformanceService} for show-existence validation.
     *
     * @param id must not be {@code null}
     * @return the matching {@link bg.uni.fmi.theatre.dto.ShowResponse}
     * @throws bg.uni.fmi.theatre.exception.NotFoundException if no Show exists for {@code id}
     * @since Week 06, Task 1
     */
    public ShowResponse getShowById(Long id) {
        if (id == null) throw new ValidationException("id must not be null");
        logger.debug("Fetching show by id: " + id);
        return showRepository.findById(id).map(ShowResponse::from).orElseThrow(() -> {
            logger.error("Show not found: id=" + id);
            return new NotFoundException("Show", id);
        });
    }

    /**
     * Optionally returns a Show; returns {@link java.util.Optional#empty()} instead of throwing when absent.
     *
     * @since Week 06, Task 1
     */
    public Optional<ShowResponse> findShowById(Long id) {
        return showRepository.findById(id).map(ShowResponse::from);
    }

    /**
     * Searches Shows by optional title substring and genre, returning a paginated slice.
     * Results are sorted alphabetically by title.
     *
     * @param titleQuery case-insensitive title substring (nullable/blank = no filter)
     * @param genre genre filter (null = all genres)
     * @param page zero-based page index; must be &gt;= 0
     * @param size results per page; must be &gt; 0
     * @since Week 06, Task 4
     */
    public PageResponse<ShowResponse> searchShows(String titleQuery, Genre genre, int page, int size) {
        if (page < 0) throw new ValidationException("page must not be negative");
        if (size <= 0) throw new ValidationException("size must be positive");
        logger.debug("Searching shows — title='" + titleQuery + "', genre=" + genre + ", page=" + page);

        List<ShowResponse> allResults = showRepository.findAll().stream()
                .filter(s -> titleQuery == null || titleQuery.isBlank()
                        || s.getTitle().toLowerCase().contains(titleQuery.toLowerCase()))
                .filter(s -> genre == null || genre.equals(s.getGenre()))
                .sorted(Comparator.comparing(Show::getTitle))
                .map(ShowResponse::from)
                .collect(Collectors.toList());

        long total = allResults.size();
        logger.info("Search returned " + total + " total results");
        int fromIndex = page * size;
        List<ShowResponse> pageContent = fromIndex >= allResults.size()
                ? List.of()
                : allResults.subList(fromIndex, Math.min(fromIndex + size, allResults.size()));
        return new PageResponse<>(pageContent, page, size, total);
    }

    public PageResponse<ShowResponse> searchShows(String titleQuery, Genre genre) {
        return searchShows(titleQuery, genre, 0, defaultPageSize);
    }

    /**
     * Returns all Shows without pagination.
     *
     * @since Week 06, Task 1
     */
    public List<ShowResponse> getAllShows() {
        logger.trace("getAllShows called");
        return showRepository.findAll().stream().map(ShowResponse::from).toList();
    }

    /**
     * Replaces all mutable fields of an existing Show (fetch → mutate → save).
     *
     * @throws bg.uni.fmi.theatre.exception.NotFoundException if no Show exists for {@code id}
     * @since Week 06, Task 2
     */
    public ShowResponse updateShow(Long id, ShowRequest req) {
        Show existing = showRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Show", id));
        existing.setTitle(req.getTitle());
        existing.setDescription(req.getDescription());
        existing.setGenre(req.getGenre());
        existing.setDurationMinutes(req.getDurationMinutes());
        existing.setAgeRating(req.getAgeRating());
        logger.info("Show updated: id=" + id);
        return ShowResponse.from(showRepository.save(existing));
    }

    /**
     * Deletes a Show by its identifier.
     *
     * @throws bg.uni.fmi.theatre.exception.NotFoundException if no Show exists for {@code id}
     * @since Week 06, Task 2
     */
    public void deleteShow(Long id) {
        showRepository.findById(id).orElseThrow(() -> new NotFoundException("Show", id));
        showRepository.deleteById(id);
        logger.info("Show deleted: id=" + id);
    }
}
