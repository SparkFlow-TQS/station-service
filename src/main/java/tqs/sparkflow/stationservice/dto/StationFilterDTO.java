package tqs.sparkflow.stationservice.dto;

import java.util.Objects;
import jakarta.validation.constraints.Min;
import tqs.sparkflow.stationservice.model.BaseStationFields;

public class StationFilterDTO extends BaseStationFields {
  @Min(value = 0, message = "Minimum price must be non-negative")
  private Double minPrice;
    
  @Min(value = 0, message = "Maximum price must be non-negative")
  private Double maxPrice;
    
  @Min(value = 0, message = "Radius must be positive")
  private Integer radius;

  @Min(value = 0, message = "Minimum power must be non-negative")
  private Integer minPower;

  @Min(value = 0, message = "Maximum power must be non-negative")
  private Integer maxPower;

  public StationFilterDTO() {
  }

  public StationFilterDTO(String name, String address, String city, String country, Double latitude, Double longitude, String status, Boolean isOperational, Double price, Double minPrice, Double maxPrice, Integer radius, Integer minPower, Integer maxPower) {
    setName(name);
    setAddress(address);
    setCity(city);
    setCountry(country);
    setLatitude(latitude);
    setLongitude(longitude);
    setStatus(status);
    setIsOperational(isOperational);
    setPrice(price);
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    this.radius = radius;
    this.minPower = minPower;
    this.maxPower = maxPower;
  }

  public Double getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(Double minPrice) {
    this.minPrice = minPrice;
  }

  public Double getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(Double maxPrice) {
    this.maxPrice = maxPrice;
  }

  public Integer getRadius() {
    return radius;
  }

  public void setRadius(Integer radius) {
    this.radius = radius;
  }

  public Integer getMinPower() {
    return minPower;
  }

  public void setMinPower(Integer minPower) {
    this.minPower = minPower;
  }

  public Integer getMaxPower() {
    return maxPower;
  }

  public void setMaxPower(Integer maxPower) {
    this.maxPower = maxPower;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StationFilterDTO that = (StationFilterDTO) o;
    return Objects.equals(minPrice, that.minPrice) &&
           Objects.equals(maxPrice, that.maxPrice) &&
           Objects.equals(radius, that.radius) &&
           Objects.equals(minPower, that.minPower) &&
           Objects.equals(maxPower, that.maxPower);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), minPrice, maxPrice, radius, minPower, maxPower);
  }

  @Override
  public String toString() {
    return "StationFilterDTO{" +
           "name='" + getName() + '\'' +
           ", address='" + getAddress() + '\'' +
           ", city='" + getCity() + '\'' +
           ", country='" + getCountry() + '\'' +
           ", latitude=" + getLatitude() +
           ", longitude=" + getLongitude() +
           ", status='" + getStatus() + '\'' +
           ", isOperational=" + getIsOperational() +
           ", price=" + getPrice() +
           ", minPrice=" + minPrice +
           ", maxPrice=" + maxPrice +
           ", radius=" + radius +
           ", minPower=" + minPower +
           ", maxPower=" + maxPower +
           "}";
  }
}