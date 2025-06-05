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
  @Schema(description = "ID of the charging station", example = "STATION-001")
  private String stationId;
  
  /**
   * The ID of the user who initiated this charging session.
   */
  @Schema(description = "ID of the user who initiated the session", example = "USER-001")
  private String userId;
  
  /**
   * Flag indicating whether the charging session has finished.
   * False when session is active, true when completed.
   */
  @Schema(description = "Whether the charging session has finished", example = "false")
  private boolean finished = false;
  
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
   * Default constructor required by JPA.
   * Initializes the session as not finished.
   */
  public ChargingSession() {
    this.finished = false;
  }

  /**
   * Constructor that creates a new charging session and immediately starts it.
   * 
   * @param stationId The ID of the charging station
   * @param userId The ID of the user starting the session
   */
  public ChargingSession(String stationId, String userId) {
    this.stationId = stationId;
    this.userId = userId;
    this.finished = false;
    this.startTime = LocalDateTime.now();
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

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
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

} 