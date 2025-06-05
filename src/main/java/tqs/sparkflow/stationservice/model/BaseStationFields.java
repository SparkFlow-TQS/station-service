package tqs.sparkflow.stationservice.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@MappedSuperclass
public class BaseStationFields {
  @Min(value = -90, message = "Latitude must be between -90 and 90 degrees")
  @Max(value = 90, message = "Latitude must be between -90 and 90 degrees")
  protected Double latitude;

  @Min(value = -180, message = "Longitude must be between -180 and 180 degrees")
  @Max(value = 180, message = "Longitude must be between -180 and 180 degrees")
  protected Double longitude;

  @Min(value = 0, message = "Price must be non-negative")
  protected Double price;

  protected Integer numberOfChargers;
  protected Integer minPower;
  protected Integer maxPower;
  protected Boolean isOperational;
  protected String status;
  protected String city;
  protected String country;

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
    this.price = price != null ? price : 0.30;
  }

  public Integer getNumberOfChargers() {
    return numberOfChargers;
  }

  public void setNumberOfChargers(Integer numberOfChargers) {
    this.numberOfChargers = numberOfChargers;
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
} 