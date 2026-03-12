package bg.uni.fmi.theatre.repository.inmemory;

import bg.uni.fmi.theatre.domain.Show;
import bg.uni.fmi.theatre.repository.ShowRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryShowRepository implements ShowRepository {
    private final Map<Long, Show> store = new HashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public Show save(Show show) { store.put(show.getId(), show); return show; }

    @Override
    public Optional<Show> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Show> findAll() { return List.copyOf(store.values()); }

    @Override
    public void deleteById(Long id) { store.remove(id); }

    @Override
    public boolean existsById(Long id) { return store.containsKey(id); }

    public long nextId() { return idSequence.getAndIncrement(); }
}
