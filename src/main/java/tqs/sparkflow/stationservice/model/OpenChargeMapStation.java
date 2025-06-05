package tqs.sparkflow.stationservice.model;

/** Station model for OpenChargeMap API. */
public class OpenChargeMapStation {
  private String id;
  private String name;
  private String address;
  private String city;
  private String country;
  private double latitude;
  private double longitude;
  private Integer quantityOfChargers;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public Integer getQuantityOfChargers() {
    return quantityOfChargers;
  }

  public void setQuantityOfChargers(Integer quantityOfChargers) {
    this.quantityOfChargers = quantityOfChargers;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
