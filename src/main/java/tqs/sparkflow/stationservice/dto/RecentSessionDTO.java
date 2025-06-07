package tqs.sparkflow.stationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO for representing recent charging sessions in statistics
 */
@Schema(description = "Recent charging session information for statistics")
public class RecentSessionDTO {
    
    @Schema(description = "Unique identifier of the charging session", example = "1")
    private Long id;
    
    @Schema(description = "ID of the charging station", example = "1")
    private Long stationId;
    
    @Schema(description = "ID of the user who initiated the session", example = "1")
    private Long userId;
    
    @Schema(description = "Timestamp when charging started", example = "2024-03-20T10:00:00")
    private LocalDateTime startTime;
    
    @Schema(description = "Timestamp when charging ended", example = "2024-03-20T11:30:00")
    private LocalDateTime endTime;
    
    @Schema(description = "Whether the charging session has finished", example = "true")
    private boolean finished;
    
    @Schema(description = "Duration of the session in minutes", example = "90")
    private Long durationMinutes;
    
    @Schema(description = "Estimated cost of the session", example = "15.50")
    private Double estimatedCost;

    // Default constructor
    public RecentSessionDTO() {}

    // Constructor with all fields
    public RecentSessionDTO(Long id, Long stationId, Long userId, LocalDateTime startTime, 
                           LocalDateTime endTime, boolean finished, Long durationMinutes, Double estimatedCost) {
        this.id = id;
        this.stationId = stationId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.finished = finished;
        this.durationMinutes = durationMinutes;
        this.estimatedCost = estimatedCost;
    }

    // Getters and Setters
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
} 