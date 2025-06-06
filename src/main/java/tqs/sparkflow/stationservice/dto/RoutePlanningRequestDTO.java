package tqs.sparkflow.stationservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class RoutePlanningRequestDTO {
    @NotNull(message = "Start latitude cannot be null")
    @Min(value = -90, message = "Start latitude must be between -90 and 90 degrees")
    @Max(value = 90, message = "Start latitude must be between -90 and 90 degrees")
    private Double startLatitude;

    @NotNull(message = "Start longitude cannot be null")
    @Min(value = -180, message = "Start longitude must be between -180 and 180 degrees")
    @Max(value = 180, message = "Start longitude must be between -180 and 180 degrees")
    private Double startLongitude;

    @NotNull(message = "Destination latitude cannot be null")
    @Min(value = -90, message = "Destination latitude must be between -90 and 90 degrees")
    @Max(value = 90, message = "Destination latitude must be between -90 and 90 degrees")
    private Double destLatitude;

    @NotNull(message = "Destination longitude cannot be null")
    @Min(value = -180, message = "Destination longitude must be between -180 and 180 degrees")
    @Max(value = 180, message = "Destination longitude must be between -180 and 180 degrees")
    private Double destLongitude;

    @NotNull(message = "Battery capacity cannot be null")
    @Min(value = 0, message = "Battery capacity must be positive")
    private Double batteryCapacity; // in kWh

    @NotNull(message = "Car autonomy cannot be null")
    @Min(value = 0, message = "Car autonomy must be positive")
    private Double carAutonomy; // in km/kWh

    // Getters and Setters
    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getDestLatitude() {
        return destLatitude;
    }

    public void setDestLatitude(Double destLatitude) {
        this.destLatitude = destLatitude;
    }

    public Double getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(Double destLongitude) {
        this.destLongitude = destLongitude;
    }

    public Double getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(Double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public Double getCarAutonomy() {
        return carAutonomy;
    }

    public void setCarAutonomy(Double carAutonomy) {
        this.carAutonomy = carAutonomy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutePlanningRequestDTO that = (RoutePlanningRequestDTO) o;
        return Objects.equals(startLatitude, that.startLatitude) &&
               Objects.equals(startLongitude, that.startLongitude) &&
               Objects.equals(destLatitude, that.destLatitude) &&
               Objects.equals(destLongitude, that.destLongitude) &&
               Objects.equals(batteryCapacity, that.batteryCapacity) &&
               Objects.equals(carAutonomy, that.carAutonomy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startLatitude, startLongitude, destLatitude, destLongitude, batteryCapacity, carAutonomy);
    }

    @Override
    public String toString() {
        return "RoutePlanningRequestDTO{" +
               "startLatitude=" + startLatitude +
               ", startLongitude=" + startLongitude +
               ", destLatitude=" + destLatitude +
               ", destLongitude=" + destLongitude +
               ", batteryCapacity=" + batteryCapacity +
               ", carAutonomy=" + carAutonomy +
               '}';
    }
} 