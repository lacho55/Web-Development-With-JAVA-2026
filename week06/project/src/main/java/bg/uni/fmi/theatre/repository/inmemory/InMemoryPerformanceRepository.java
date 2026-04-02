package bg.uni.fmi.theatre.repository.inmemory;

import bg.uni.fmi.theatre.domain.Performance;
import bg.uni.fmi.theatre.repository.PerformanceRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryPerformanceRepository implements PerformanceRepository {
    private final Map<Long, Performance> store = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override public Performance save(Performance p) { store.put(p.getId(), p); return p; }
    @Override public Optional<Performance> findById(Long id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<Performance> findAll() { return List.copyOf(store.values()); }
    @Override public List<Performance> findByShowId(Long showId) {
        return store.values().stream().filter(p -> p.getShowId().equals(showId)).collect(Collectors.toList());
    }
    @Override public void deleteById(Long id) { store.remove(id); }
    public long nextId() { return seq.getAndIncrement(); }
}
