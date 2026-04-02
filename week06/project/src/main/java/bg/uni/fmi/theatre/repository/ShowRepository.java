package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Show;
import java.util.List;
import java.util.Optional;

public interface ShowRepository {
    Show save(Show show);
    Optional<Show> findById(Long id);
    List<Show> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
