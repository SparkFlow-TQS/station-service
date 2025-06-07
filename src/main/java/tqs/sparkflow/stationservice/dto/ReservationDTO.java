package tqs.sparkflow.stationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Reservation information for statistics")
public class ReservationDTO {
    @Schema(description = "Unique identifier of the reservation")
    private Long id;

    @Schema(description = "ID of the charging station")
    private Long stationId;

    @Schema(description = "ID of the user who made the reservation")
    private Long userId;

    @Schema(description = "Start time of the reservation")
    private LocalDateTime startTime;

    @Schema(description = "End time of the reservation")
    private LocalDateTime endTime;

    @Schema(description = "Estimated cost of the reservation")
    private Double estimatedCost;

    @Schema(description = "Status of the reservation")
    private String status;

    /**
     * Default constructor.
     */
    public ReservationDTO() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id The unique identifier
     * @param stationId The station ID
     * @param userId The user ID
     * @param startTime The start time
     * @param endTime The end time
     * @param estimatedCost The estimated cost
     * @param status The reservation status
     */
    public ReservationDTO(Long id, Long stationId, Long userId, LocalDateTime startTime, 
                         LocalDateTime endTime, Double estimatedCost, String status) {
        this.id = id;
        this.stationId = stationId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimatedCost = estimatedCost;
        this.status = status;
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

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 