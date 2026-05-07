package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {
}