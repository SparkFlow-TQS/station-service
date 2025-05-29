package tqs.sparkflow.stationservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private Long stationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ElementCollection
    @CollectionTable(name = "booking_recurring_days", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "day_of_week")
    private Set<Integer> recurringDays;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public enum BookingStatus {
        ACTIVE,
        CANCELLED,
        COMPLETED,
        REROUTED
    }

    public Booking() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Set<Integer> getRecurringDays() {
        return recurringDays;
    }

    public void setRecurringDays(Set<Integer> recurringDays) {
        this.recurringDays = recurringDays;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
} 