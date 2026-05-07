package bg.uni.fmi.theatre.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * Inbound DTO for creating new reservations.
 * Used by POST /api/reservations endpoint.
 */
public class ReservationRequest {
    @NotNull
    private Long performanceId;

    @NotBlank
    private String seatLabel;

    @NotBlank
    private String customerName;

    public Long getPerformanceId() { return performanceId; }
    public void setPerformanceId(Long performanceId) { this.performanceId = performanceId; }

    public String getSeatLabel() { return seatLabel; }
    public void setSeatLabel(String seatLabel) { this.seatLabel = seatLabel; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}