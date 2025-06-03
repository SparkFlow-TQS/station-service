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
}