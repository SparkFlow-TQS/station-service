package tqs.sparkflow.stationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Weekly statistics data for current month")
public class WeeklyStatisticsDTO {
    
    @Schema(description = "Week identifier", example = "Week1")
    private String week;
    
    @Schema(description = "Number of sessions in the week", example = "3")
    private int sessions;
    
    @Schema(description = "Total cost for the week", example = "35.75")
    private double cost;
    
    @Schema(description = "Date range of the week", example = "Jan 1-7")
    private String dateRange;
    
    @Schema(description = "List of reservations for the week")
    private List<ReservationDTO> reservations;

    public WeeklyStatisticsDTO() {}

    public WeeklyStatisticsDTO(String week, int sessions, double cost, String dateRange, 
                             List<ReservationDTO> reservations) {
        this.week = week;
        this.sessions = sessions;
        this.cost = cost;
        this.dateRange = dateRange;
        this.reservations = reservations;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }
} 