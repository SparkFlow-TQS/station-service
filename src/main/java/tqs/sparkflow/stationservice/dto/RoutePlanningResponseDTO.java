package tqs.sparkflow.stationservice.dto;

import java.util.List;
import tqs.sparkflow.stationservice.model.Station;

public class RoutePlanningResponseDTO {
    private List<Station> stations;
    private double distance; // in km
    private double batteryUsage; // in kWh

    public RoutePlanningResponseDTO() {
    }

    public RoutePlanningResponseDTO(List<Station> stations, double distance, double batteryUsage) {
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getBatteryUsage() {
        return batteryUsage;
    }

    public void setBatteryUsage(double batteryUsage) {
        this.batteryUsage = batteryUsage;
    }
} 