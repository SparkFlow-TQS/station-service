package tqs.sparkflow.stationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cost trend data for chart visualization")
public class CostTrendDTO {
    
    @Schema(description = "Month abbreviation", example = "Jan")
    private String month;
    
    @Schema(description = "Total cost for the month", example = "125.50")
    private double cost;
    
    @Schema(description = "Number of sessions in the month", example = "12")
    private int sessions;

    public CostTrendDTO() {}

    public CostTrendDTO(String month, double cost, int sessions) {
        this.month = month;
        this.cost = cost;
        this.sessions = sessions;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }
} 