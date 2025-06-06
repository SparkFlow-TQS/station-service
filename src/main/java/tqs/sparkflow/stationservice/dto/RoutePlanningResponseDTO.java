package tqs.sparkflow.stationservice.dto;

import java.util.List;
import java.util.Objects;
import tqs.sparkflow.stationservice.model.Station;

public class RoutePlanningResponseDTO {
    private List<Station> stations;
    private Double distance;
    private Double batteryUsage;

    public RoutePlanningResponseDTO() {
    }

    public RoutePlanningResponseDTO(List<Station> stations, Double distance, Double batteryUsage) {
        this.stations = stations;
        this.distance = distance;
        this.batteryUsage = batteryUsage;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getBatteryUsage() {
        return batteryUsage;
    }

    public void setBatteryUsage(Double batteryUsage) {
        this.batteryUsage = batteryUsage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutePlanningResponseDTO that = (RoutePlanningResponseDTO) o;
        return Objects.equals(stations, that.stations) &&
               Objects.equals(distance, that.distance) &&
               Objects.equals(batteryUsage, that.batteryUsage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, distance, batteryUsage);
    }

    @Override
    public String toString() {
        return "RoutePlanningResponseDTO{" +
               "stations=" + stations +
               ", distance=" + distance +
               ", batteryUsage=" + batteryUsage +
               "}";
    }
} 