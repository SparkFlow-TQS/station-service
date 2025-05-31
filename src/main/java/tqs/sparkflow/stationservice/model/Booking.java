package tqs.sparkflow.stationservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "bookings")
@Schema(description = "Booking entity representing a charging station reservation")
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier of the booking")
  private Long id;

  @Column(name = "station_id", nullable = false)
  @Schema(description = "ID of the charging station")
  private Long stationId;

  @Column(name = "user_id", nullable = false)
  @Schema(description = "ID of the user making the booking")
  private Long userId;

  @Column(name = "start_time", nullable = false)
  @Schema(description = "Start time of the booking")
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
  @Schema(description = "End time of the booking")
  private LocalDateTime endTime;

  @ElementCollection
  @CollectionTable(name = "booking_recurring_days", joinColumns = @JoinColumn(name = "booking_id"))
  @Column(name = "day_of_week")
  @Schema(description = "Set of days of the week for recurring bookings (0-6, where 0 is Sunday)")
  private Set<Integer> recurringDays;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Current status of the booking")
  private BookingStatus status;

  /**
   * Default constructor required by JPA.
   * 
   * <p>
   * This constructor is used by JPA to create entity instances during persistence operations.
   * It should not be used directly in application code.
   *
   * This method is intentionally left empty.
   */
  @SuppressWarnings("java:S1186")
  public Booking() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getStationId() {
    return stationId;
  }

  public void setStationId(Long stationId) {
    this.stationId = stationId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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