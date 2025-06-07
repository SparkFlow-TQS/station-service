package tqs.sparkflow.stationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current month statistics for a user")
public class CurrentMonthStatisticsDTO {
    
    @Schema(description = "Total cost for current month", example = "125.50")
    private double totalCost;
    
    @Schema(description = "Estimated kWh consumed in current month", example = "45.2")
    private double estimatedKwh;
    
    @Schema(description = "Total number of sessions in current month", example = "12")
    private int totalSessions;
    
    @Schema(description = "CO2 saved in kg compared to gasoline car", example = "18.08")
    private double co2Saved;
    
    @Schema(description = "Average cost per session", example = "10.46")
    private double avgCostPerSession;

    public CurrentMonthStatisticsDTO() {}

    public CurrentMonthStatisticsDTO(double totalCost, double estimatedKwh, int totalSessions, 
                                   double co2Saved, double avgCostPerSession) {
        this.totalCost = totalCost;
        this.estimatedKwh = estimatedKwh;
        this.totalSessions = totalSessions;
        this.co2Saved = co2Saved;
        this.avgCostPerSession = avgCostPerSession;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getEstimatedKwh() {
        return estimatedKwh;
    }

    public void setEstimatedKwh(double estimatedKwh) {
        this.estimatedKwh = estimatedKwh;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public double getCo2Saved() {
        return co2Saved;
    }

    public void setCo2Saved(double co2Saved) {
        this.co2Saved = co2Saved;
    }

    public double getAvgCostPerSession() {
        return avgCostPerSession;
    }

    public void setAvgCostPerSession(double avgCostPerSession) {
        this.avgCostPerSession = avgCostPerSession;
    }
} 