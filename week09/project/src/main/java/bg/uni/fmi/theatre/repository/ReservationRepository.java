package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByPerformanceId(Long performanceId);

    boolean existsByPerformanceIdAndSeatLabel(Long performanceId, String seatLabel);
}