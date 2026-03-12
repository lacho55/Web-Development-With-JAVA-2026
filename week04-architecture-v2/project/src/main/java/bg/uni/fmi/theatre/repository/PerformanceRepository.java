package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Performance;
import java.util.List;
import java.util.Optional;

public interface PerformanceRepository {
    Performance save(Performance performance);
    Optional<Performance> findById(Long id);
    List<Performance> findAll();
    List<Performance> findByShowId(Long showId);
    void deleteById(Long id);
}
