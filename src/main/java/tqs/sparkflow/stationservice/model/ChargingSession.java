package tqs.sparkflow.stationservice.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity class representing a charging session.
 * Tracks the lifecycle of a charging session from creation to completion.
 */
@Entity
@Table(name = "charging_sessions")
@Schema(description = "Represents a charging session and its lifecycle")
public class ChargingSession {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier of the charging session", example = "1")
  private Long id;
  
  /**
   * The ID of the charging station associated with this session.
   */
  @Schema(description = "ID of the charging station", example = "STATION-001", required = true)
  private String stationId;
  
  /**
   * The ID of the user who initiated this charging session.
   */
  @Schema(description = "ID of the user who initiated the session", example = "USER-001", required = true)
  private String userId;
  
  /**
   * The current status of the charging session.
   * See {@link ChargingSessionStatus} for possible values.
   */
  @Schema(description = "Current status of the charging session", example = "CHARGING", required = true)
  private ChargingSessionStatus status;
  
  /**
   * The timestamp when charging started.
   * Null until charging begins.
   */
  @Schema(description = "Timestamp when charging started", example = "2024-03-20T10:00:00", nullable = true)
  private LocalDateTime startTime;
  
  /**
   * The timestamp when charging ended.
   * Null until charging is completed.
   */
  @Schema(description = "Timestamp when charging ended", example = "2024-03-20T11:30:00", nullable = true)
  private LocalDateTime endTime;
  
  /**
   * Error message if the session encountered an error.
   * Null if no error has occurred.
   */
  @Schema(description = "Error message if the session encountered an error", example = "Connection error", nullable = true)
  private String errorMessage;

  /**
   * Enum representing the possible states of a charging session.
   */
  @Schema(description = "Possible states of a charging session")
  public enum ChargingSessionStatus {
    /** Initial state when session is created */
    @Schema(description = "Initial state when session is created")
    CREATED,
    /** Station is unlocked and ready for charging */
    @Schema(description = "Station is unlocked and ready for charging")
    UNLOCKED,
    /** Charging is in progress */
    @Schema(description = "Charging is in progress")
    CHARGING,
    /** Charging has been completed */
    @Schema(description = "Charging has been completed")
    COMPLETED,
    /** An error occurred during the session */
    @Schema(description = "An error occurred during the session")
    ERROR
  }

  /**
   * Default constructor required by JPA.
   * Initializes the session with CREATED status.
   */
  public ChargingSession() {
    this.status = ChargingSessionStatus.CREATED;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStationId() {
    return stationId;
  }

  public void setStationId(String stationId) {
    this.stationId = stationId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public ChargingSessionStatus getStatus() {
    return status;
  }

  public void setStatus(ChargingSessionStatus status) {
    this.status = status;
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

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
} 