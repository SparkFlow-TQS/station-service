package tqs.sparkflow.stationservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Base class containing common fields for station-related models. These fields are common between
 * internal stations and OpenChargeMap stations.
 */
@MappedSuperclass
public abstract class BaseStationFields {
    @Column(name = "city", nullable = true)
    protected String city;

    @Column(name = "country", nullable = true)
    protected String country;

    @NotNull(message = "Latitude cannot be null")
    @Min(value = -90, message = "Latitude must be between -90 and 90 degrees")
    @Max(value = 90, message = "Latitude must be between -90 and 90 degrees")
    @Column(name = "latitude")
    protected Double latitude;

    @NotNull(message = "Longitude cannot be null")
    @Min(value = -180, message = "Longitude must be between -180 and 180 degrees")
    @Max(value = 180, message = "Longitude must be between -180 and 180 degrees")
    @Column(name = "longitude")
    protected Double longitude;

    @Column(name = "price")
    protected Double price;

    @Column(name = "is_operational")
    protected Boolean isOperational;

    @NotBlank(message = "Status cannot be empty")
    @Column(name = "status")
    protected String status;

    protected String address;

    @Column(name = "name", nullable = true)
    protected String name;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getIsOperational() {
        return isOperational;
    }

    public void setIsOperational(Boolean isOperational) {
        this.isOperational = isOperational;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BaseStationFields that = (BaseStationFields) o;
        return Objects.equals(city, that.city) && Objects.equals(country, that.country)
                && Objects.equals(latitude, that.latitude)
                && Objects.equals(longitude, that.longitude) && Objects.equals(price, that.price)
                && Objects.equals(isOperational, that.isOperational)
                && Objects.equals(status, that.status) && Objects.equals(address, that.address)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, country, latitude, longitude, price, isOperational, status,
                address, name);
    }
}
