package tqs.sparkflow.stationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Represents a charging station. */
@Entity
@Table(name = "stations")
public class Station {

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

  @NotBlank(message = "Connector type cannot be empty")
  private String connectorType;

  private Integer power;
  private Boolean isOperational;

  /** Creates a new Station. */
  public Station() {}

  /**
   * Creates a new Station with the given details.
   *
   * @param name The name of the station
   * @param address The address of the station
   * @param city The city where the station is located
   * @param latitude The latitude of the station
   * @param longitude The longitude of the station
   * @param connectorType The type of connector available at the station
   * @param status The status of the station
   */
  public Station(
      String name,
      String address,
      String city,
      double latitude,
      double longitude,
      String connectorType,
      String status) {
    this.name = name;
    this.address = address;
    this.city = city;
    this.latitude = latitude;
    this.longitude = longitude;
    this.connectorType = connectorType;
    this.status = status;
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
   * @param connectorType The type of connector available at the station
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
      String connectorType,
      Integer power,
      Boolean isOperational) {
    this.externalId = externalId;
    this.name = name;
    this.address = address;
    this.city = city;
    this.country = country;
    this.latitude = latitude;
    this.longitude = longitude;
    this.connectorType = connectorType;
    this.power = power;
    this.isOperational = isOperational;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getConnectorType() {
    return connectorType;
  }

  public void setConnectorType(String connectorType) {
    this.connectorType = connectorType;
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
    return "Station{" + "id=" + id + ", name='" + name + '\'' + '}';
  }
}
