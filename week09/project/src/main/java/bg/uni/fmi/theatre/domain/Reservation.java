package bg.uni.fmi.theatre.domain;

import bg.uni.fmi.theatre.vo.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Column(name = "seat_label", nullable = false, length = 20)
    private String seatLabel;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt;

    @Version
    private Long version;

    protected Reservation() {}

    public Reservation(Performance performance, String seatLabel, String customerName) {
        this.performance = performance;
        this.seatLabel = seatLabel;
        this.customerName = customerName;
        this.status = ReservationStatus.CONFIRMED;
        this.reservedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Performance getPerformance() { return performance; }
    public String getSeatLabel() { return seatLabel; }
    public String getCustomerName() { return customerName; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public Long getVersion() { return version; }

    public void setStatus(ReservationStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation r)) return false;
        return Objects.equals(id, r.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", seat='" + seatLabel + "', customer='" + customerName + "', status=" + status + "}";
    }
}