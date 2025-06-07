package tqs.sparkflow.stationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Period details for selected month or week")
public class PeriodDetailsDTO {
    
    @Schema(description = "Total number of reservations", example = "15")
    private int totalReservations;
    
    @Schema(description = "Total cost for the period", example = "187.50")
    private double totalCost;
    
    @Schema(description = "Average cost per session", example = "12.50")
    private double avgCostPerSession;
    
    @Schema(description = "List of reservations for the period")
    private List<ReservationDTO> reservations;

    public PeriodDetailsDTO() {}

    public PeriodDetailsDTO(int totalReservations, double totalCost, double avgCostPerSession, 
                          List<ReservationDTO> reservations) {
        this.totalReservations = totalReservations;
        this.totalCost = totalCost;
        this.avgCostPerSession = avgCostPerSession;
        this.reservations = reservations;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getAvgCostPerSession() {
        return avgCostPerSession;
    }

    public void setAvgCostPerSession(double avgCostPerSession) {
        this.avgCostPerSession = avgCostPerSession;
    }

    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }
} 