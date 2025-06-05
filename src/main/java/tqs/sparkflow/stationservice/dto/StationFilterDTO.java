package tqs.sparkflow.stationservice.dto;

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

  private Boolean isOperational;
  private String status;

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
}