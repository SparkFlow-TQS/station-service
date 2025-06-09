package tqs.sparkflow.stationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import java.util.Objects;

/** Represents a charging station. */
@Entity
@Table(name = "stations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Station extends BaseStationFields {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String externalId;

  @Column(name = "power")
  private Integer power;

  @NotNull(message = "Quantity of chargers cannot be null")
  @Min(value = 1, message = "Quantity of chargers must be at least 1")
  @Column(name = "quantity_of_chargers")
  private Integer quantityOfChargers;

  /** Creates a new Station. */
  public Station() {}

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
   * @param status The status of the station
   */
  public Station(String externalId, String name, String address, String city, String country,
      double latitude, double longitude, Integer quantityOfChargers, String status) {
    this.externalId = externalId;
    this.name = name;
    this.address = address;
    setCity(city);
    setCountry(country);
    setLatitude(latitude);
    setLongitude(longitude);
    this.quantityOfChargers = quantityOfChargers;
    setStatus(status);
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
  public Station(String externalId, String name, String address, String city, String country,
      Double latitude, Double longitude, Integer quantityOfChargers, Integer power,
      Boolean isOperational) {
    this.externalId = externalId;
    this.name = name;
    this.address = address;
    setCity(city);
    setCountry(country);
    setLatitude(latitude);
    setLongitude(longitude);
    this.quantityOfChargers = quantityOfChargers;
    this.power = power;
    this.isOperational = isOperational;
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
  public Station(String externalId, String name, String address, String city, String country,
      Double latitude, Double longitude, Integer quantityOfChargers, Integer power,
      Boolean isOperational, Double price) {
    this.externalId = externalId;
    this.name = name;
    this.address = address;
    setCity(city);
    setCountry(country);
    setLatitude(latitude);
    setLongitude(longitude);
    this.quantityOfChargers = quantityOfChargers;
    this.power = power;
    this.isOperational = isOperational;
    this.price = price;
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

  @Override
  public String getAddress() {
    return address;
  }

  @Override
  public void setAddress(String address) {
    this.address = address;
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

  @Override
  public String toString() {
    return "Station{id=" + id + ", name='" + name + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    Station station = (Station) o;
    return Objects.equals(id, station.id) && Objects.equals(externalId, station.externalId)
        && Objects.equals(power, station.power)
        && Objects.equals(quantityOfChargers, station.quantityOfChargers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, externalId, power, quantityOfChargers);
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
