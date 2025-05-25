package tqs.sparkflow.station_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stations")
public class Station {
  @Id
  private String id;
  private String name;
  private String address;
  private double latitude;
  private double longitude;
  private String status;
  private String connectorType;

  public Station() {
  }

  public Station(String id, String name, String address, double latitude, double longitude, String status, String connectorType) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
    this.status = status;
    this.connectorType = connectorType;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getStatus() {
    return status;
  }

  public String getConnectorType() {
    return connectorType;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setConnectorType(String connectorType) {
    this.connectorType = connectorType;
  }

  @Override
  public String toString() {
    return "Station{"
        + "id='" + id + '\''
        + ", name='" + name + '\''
        + '}';
  }
}
