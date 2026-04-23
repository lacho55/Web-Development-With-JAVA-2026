package bg.uni.fmi.theatre.repository.inmemory;

import bg.uni.fmi.theatre.domain.Show;
import bg.uni.fmi.theatre.repository.ShowRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("test")
public class TestInMemoryShowRepository implements ShowRepository {
    private final Map<Long, Show> store = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override public Show save(Show s) { store.put(s.getId(), s); return s; }
    @Override public Optional<Show> findById(Long id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<Show> findAll() { return List.copyOf(store.values()); }
    @Override public void deleteById(Long id) { store.remove(id); }
    @Override public boolean existsById(Long id) { return store.containsKey(id); }
    public long nextId() { return seq.getAndIncrement(); }
}
