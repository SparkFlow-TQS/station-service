package tqs.sparkflow.stationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Monthly statistics data for historical analysis")
public class MonthlyStatisticsDTO {
    
    @Schema(description = "Month abbreviation", example = "Jan")
    private String month;
    
    @Schema(description = "Full month name", example = "January")
    private String fullMonth;
    
    @Schema(description = "Total cost for the month", example = "125.50")
    private double cost;
    
    @Schema(description = "Number of sessions in the month", example = "12")
    private int sessions;
    
    @Schema(description = "Total duration in hours", example = "24.5")
    private double duration;
    
    @Schema(description = "Total kWh consumed", example = "45.2")
    private double kwh;
    
    @Schema(description = "List of reservations for the month")
    private List<ReservationDTO> reservations;

    public MonthlyStatisticsDTO() {}

    public MonthlyStatisticsDTO(String month, String fullMonth, double cost, int sessions, 
                              double duration, double kwh, List<ReservationDTO> reservations) {
        this.month = month;
        this.fullMonth = fullMonth;
        this.cost = cost;
        this.sessions = sessions;
        this.duration = duration;
        this.kwh = kwh;
        this.reservations = reservations;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getFullMonth() {
        return fullMonth;
    }

    public void setFullMonth(String fullMonth) {
        this.fullMonth = fullMonth;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }
} 