package bg.uni.fmi.theatre.dto;

import bg.uni.fmi.theatre.domain.Reservation;
import bg.uni.fmi.theatre.vo.ReservationStatus;

import java.time.LocalDateTime;

/**
 * Outbound DTO representing a Reservation returned by the API.
 */
public class ReservationResponse {
    private Long id;
    private Long performanceId;
    private String seatLabel;
    private String customerName;
    private ReservationStatus status;
    private LocalDateTime reservedAt;

    /**
     * Converts a Reservation entity to a ReservationResponse.
     */
    public static ReservationResponse from(Reservation r) {
        ReservationResponse response = new ReservationResponse();
        response.id = r.getId();
        response.performanceId = r.getPerformance().getId();
        response.seatLabel = r.getSeatLabel();
        response.customerName = r.getCustomerName();
        response.status = r.getStatus();
        response.reservedAt = r.getReservedAt();
        return response;
    }

    public Long getId() { return id; }
    public Long getPerformanceId() { return performanceId; }
    public String getSeatLabel() { return seatLabel; }
    public String getCustomerName() { return customerName; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getReservedAt() { return reservedAt; }
}