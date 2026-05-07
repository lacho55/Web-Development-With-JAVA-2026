package bg.uni.fmi.theatre.service;

import bg.uni.fmi.theatre.domain.Performance;
import bg.uni.fmi.theatre.domain.Reservation;
import bg.uni.fmi.theatre.dto.ReservationRequest;
import bg.uni.fmi.theatre.dto.ReservationResponse;
import bg.uni.fmi.theatre.exception.NotFoundException;
import bg.uni.fmi.theatre.exception.ValidationException;
import bg.uni.fmi.theatre.repository.PerformanceRepository;
import bg.uni.fmi.theatre.repository.ReservationRepository;
import bg.uni.fmi.theatre.vo.PerformanceStatus;
import bg.uni.fmi.theatre.vo.ReservationStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PerformanceRepository performanceRepository;

    public ReservationService(ReservationRepository reservationRepository,
                               PerformanceRepository performanceRepository) {
        this.reservationRepository = reservationRepository;
        this.performanceRepository = performanceRepository;
    }

    /**
     * Books a seat for a performance.
     *
     * The entire method runs in a single transaction:
     * 1. Load and lock the Performance (optimistic lock via @Version)
     * 2. Check the seat is not already taken
     * 3. Create and persist the Reservation
     *
     * If two users try to book the same seat concurrently, one will get
     * an OptimisticLockException (from @Version) or a unique constraint
     * violation (from the DB) — both result in an error, not a double booking.
     */
    @Transactional
    public ReservationResponse bookSeat(ReservationRequest req) {
        Performance performance = performanceRepository.findById(req.getPerformanceId())
                .orElseThrow(() -> new NotFoundException("Performance", req.getPerformanceId()));

        if (performance.getStatus() != PerformanceStatus.SCHEDULED) {
            throw new ValidationException("Cannot book seats for a "
                    + performance.getStatus().name().toLowerCase() + " performance");
        }

        if (reservationRepository.existsByPerformanceIdAndSeatLabel(
                req.getPerformanceId(), req.getSeatLabel())) {
            throw new ValidationException("Seat " + req.getSeatLabel() + " is already booked");
        }

        Reservation reservation = new Reservation(
                performance, req.getSeatLabel(), req.getCustomerName());
        Reservation saved = reservationRepository.save(reservation);

        return ReservationResponse.from(saved);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation", id));
        reservation.setStatus(ReservationStatus.CANCELLED);
        // no explicit save() needed — the entity is managed within the transaction,
        // so Hibernate's dirty checking flushes the change at commit time.
    }

    public List<ReservationResponse> findByPerformance(Long performanceId) {
        // Validate performance exists
        performanceRepository.findById(performanceId)
                .orElseThrow(() -> new NotFoundException("Performance", performanceId));

        return reservationRepository.findByPerformanceId(performanceId).stream()
                .map(ReservationResponse::from)
                .toList();
    }
}