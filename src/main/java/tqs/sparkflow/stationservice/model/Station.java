package tqs.sparkflow.stationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Represents a charging station. */
@Entity
@Table(name = "stations")
@Inheritance(strategy = InheritanceType.JOINED)
public class Station extends BaseStationFields {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String externalId;

  @NotBlank(message = "Station name cannot be empty")
  private String name;

  @NotBlank(message = "Station address cannot be empty")
  private String address;

  @NotBlank(message = "City cannot be empty")
  private String city;

  @NotBlank(message = "Country cannot be empty")
  private String country;

  @NotNull(message = "Latitude cannot be null")
  @Min(value = -90, message = "Latitude must be between -90 and 90 degrees")
  @Max(value = 90, message = "Latitude must be between -90 and 90 degrees")
  private Double latitude;

  @NotNull(message = "Longitude cannot be null")
  @Min(value = -180, message = "Longitude must be between -180 and 180 degrees")
  @Max(value = 180, message = "Longitude must be between -180 and 180 degrees")
  private Double longitude;

  @NotBlank(message = "Station status cannot be empty")
  private String status;

  @NotNull(message = "Quantity of chargers cannot be null")
  @Min(value = 1, message = "Quantity of chargers must be at least 1")
  private Integer quantityOfChargers;

  private Integer power;
  private Boolean isOperational;
  
  /** Creates a new Station. */
  public Station() {
    setPrice(null);
  }

  /**
   * Creates a new Station with the given details.
   *
   * @param externalId The external ID from OpenChargeMap
   * @param name The name of the station
   * @param address The address of the station
   * @param city The city where the station is located
   * @param latitude The latitude of the station
   * @param longitude The longitude of the station
   * @param quantityOfChargers The number of chargers available at the station
   * @param status The status of the station
   */
  public Station(
      String externalId,
      String name,
      String address,
      String city,
      String country,
      double latitude,
      double longitude,
      Integer quantityOfChargers,
      String status) {
    this.externalId = externalId;
    this.name = name;
    this.address = address;
    this.city = city;
    this.country = country;
    this.latitude = latitude;
    this.longitude = longitude;
    this.quantityOfChargers = quantityOfChargers;
    this.status = status;
    setPrice(null);
  }

  /**
   * Creates a new Station with the given details.
   *
   * @param externalId The external ID from OpenChargeMap
   * @param name The name of the station
   * @param address The address of the station
   * @param city The city where the station is located
   * @param country The country where the station is located
   * @param latitude The latitude of the station
   * @param longitude The longitude of the station
   * @param quantityOfChargers The number of chargers available at the station
   * @param power The power rating of the station in kW
   * @param isOperational Whether the station is operational
   */
  public Station(
      String externalId,
      String name,
      String address,
      String city,
      String country,
      Double latitude,
      Double longitude,
      Integer quantityOfChargers,
      Integer power,
      Boolean isOperational) {
    this.externalId = externalId;
    this.name = name;
    this.address = address;
    this.city = city;
    this.country = country;
    this.latitude = latitude;
    this.longitude = longitude;
    this.quantityOfChargers = quantityOfChargers;
    this.power = power;
    this.isOperational = isOperational;
    setPrice(null);
  }

  /**
   * Creates a new Station with the given details.
   *
   * @param externalId The external ID from OpenChargeMap
   * @param name The name of the station
   * @param address The address of the station
   * @param city The city where the station is located
   * @param country The country where the station is located
   * @param latitude The latitude of the station
   * @param longitude The longitude of the station
   * @param quantityOfChargers The number of chargers available at the station
   * @param power The power rating of the station in kW
   * @param isOperational Whether the station is operational
   * @param price The price per kWh in euros
   */
  public Station(
      String externalId,
      String name,
      String address,
      String city,
      String country,
      Double latitude,
      Double longitude,
      Integer quantityOfChargers,
      Integer power,
      Boolean isOperational,
      Double price) {
    this.externalId = externalId;
    this.name = name;
    this.address = address;
    this.city = city;
    this.country = country;
    this.latitude = latitude;
    this.longitude = longitude;
    this.quantityOfChargers = quantityOfChargers;
    this.power = power;
    this.isOperational = isOperational;
    setPrice(price);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
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

  @Override
  public Double getLatitude() {
    return latitude;
  }

  @Override
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  @Override
  public Double getLongitude() {
    return longitude;
  }

  @Override
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getQuantityOfChargers() {
    return quantityOfChargers;
  }

  public void setQuantityOfChargers(Integer quantityOfChargers) {
    this.quantityOfChargers = quantityOfChargers;
  }

  public Integer getPower() {
    return power;
  }

  public void setPower(Integer power) {
    this.power = power;
  }

  public Boolean getIsOperational() {
    return isOperational;
  }

  public void setIsOperational(Boolean isOperational) {
    this.isOperational = isOperational;
  }

  @Override
  public String toString() {
    return "Station{id=" + id + ", name='" + name + "'}";
  }

  /**
   * Builder for creating Station instances.
   */
  public static class Builder {
    private final Station station;

    public Builder() {
      station = new Station();
    }

    public Builder externalId(String externalId) {
      station.setExternalId(externalId);
      return this;
    }

    public Builder name(String name) {
      station.setName(name);
      return this;
    }

    public Builder address(String address) {
      station.setAddress(address);
      return this;
    }

    public Builder city(String city) {
      station.setCity(city);
      return this;
    }

    public Builder country(String country) {
      station.setCountry(country);
      return this;
    }

    public Builder latitude(Double latitude) {
      station.setLatitude(latitude);
      return this;
    }

    public Builder longitude(Double longitude) {
      station.setLongitude(longitude);
      return this;
    }

    public Builder quantityOfChargers(Integer quantityOfChargers) {
      station.setQuantityOfChargers(quantityOfChargers);
      return this;
    }

    public Builder power(Integer power) {
      station.setPower(power);
      return this;
    }

    public Builder isOperational(Boolean isOperational) {
      station.setIsOperational(isOperational);
      return this;
    }

    public Builder price(Double price) {
      station.setPrice(price);
      return this;
    }

    public Builder status(String status) {
      station.setStatus(status);
      return this;
    }

    public Station build() {
      return station;
    }
  }
}
