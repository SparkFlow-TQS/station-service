package tqs.sparkflow.stationservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "charging_sessions")
public class ChargingSession {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String stationId;
  private String userId;
  private ChargingSessionStatus status;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String errorMessage;

  public enum ChargingSessionStatus {
    CREATED,
    UNLOCKED,
    CHARGING,
    COMPLETED,
    ERROR
  }

  // Default constructor required by JPA
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