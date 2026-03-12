package bg.uni.fmi.theatre.service;

import bg.uni.fmi.theatre.vo.Genre;
import bg.uni.fmi.theatre.domain.Performance;
import bg.uni.fmi.theatre.domain.Show;
import bg.uni.fmi.theatre.repository.PerformanceRepository;
import bg.uni.fmi.theatre.repository.ShowRepository;

import java.util.*;
import java.util.stream.Collectors;

public class CatalogueService {
    private final ShowRepository showRepository;
    private final PerformanceRepository performanceRepository;
    private final int defaultPageSize;

    public CatalogueService(ShowRepository showRepository, PerformanceRepository performanceRepository) {
        this(showRepository, performanceRepository, 10);
    }

    public CatalogueService(ShowRepository showRepository, PerformanceRepository performanceRepository, int defaultPageSize) {
        if (showRepository == null) throw new IllegalArgumentException("showRepository is required");
        if (performanceRepository == null) throw new IllegalArgumentException("performanceRepository is required");
        if (defaultPageSize <= 0) throw new IllegalArgumentException("defaultPageSize must be positive");
        this.showRepository = showRepository;
        this.performanceRepository = performanceRepository;
        this.defaultPageSize = defaultPageSize;
    }

    public Show addShow(Show show) {
        if (show == null) throw new IllegalArgumentException("show must not be null");
        return showRepository.save(show);
    }

    public Optional<Show> findShowById(Long id) {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        return showRepository.findById(id);
    }

    public List<Show> searchShows(String titleQuery, Genre genre, int page, int size) {
        if (page < 0) throw new IllegalArgumentException("page must not be negative");
        if (size <= 0) throw new IllegalArgumentException("size must be positive");

        List<Show> results = showRepository.findAll().stream()
                .filter(s -> titleQuery == null || titleQuery.isBlank()
                        || s.getTitle().toLowerCase().contains(titleQuery.toLowerCase()))
                .filter(s -> genre == null || genre.equals(s.getGenre()))
                .sorted(Comparator.comparing(Show::getTitle))
                .collect(Collectors.toList());

        int fromIndex = page * size;
        if (fromIndex >= results.size()) return List.of();
        int toIndex = Math.min(fromIndex + size, results.size());
        return results.subList(fromIndex, toIndex);
    }

    public List<Show> searchShows(String titleQuery, Genre genre) {
        return searchShows(titleQuery, genre, 0, defaultPageSize);
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public Performance addPerformance(Performance performance) {
        if (performance == null) throw new IllegalArgumentException("performance must not be null");
        if (!showRepository.existsById(performance.getShowId())) {
            throw new IllegalArgumentException("Show not found: " + performance.getShowId());
        }
        return performanceRepository.save(performance);
    }

    public List<Performance> findPerformancesByShow(Long showId) {
        if (showId == null) throw new IllegalArgumentException("showId must not be null");
        if (!showRepository.existsById(showId)) {
            throw new IllegalArgumentException("Show not found: " + showId);
        }
        return performanceRepository.findByShowId(showId);
    }
}
