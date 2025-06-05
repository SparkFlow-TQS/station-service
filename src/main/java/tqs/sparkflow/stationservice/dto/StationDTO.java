package tqs.sparkflow.stationservice.dto;

import tqs.sparkflow.stationservice.model.Station;

public class StationDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String connectorType;
    private String status;
    private Integer power;
    private Boolean isOperational;
    private Double price;

    public StationDTO() {
    }

    public StationDTO(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.address = station.getAddress();
        this.city = station.getCity();
        this.country = station.getCountry();
        this.latitude = station.getLatitude();
        this.longitude = station.getLongitude();
        this.connectorType = station.getConnectorType();
        this.status = station.getStatus();
        this.power = station.getPower();
        this.isOperational = station.getIsOperational();
        this.price = station.getPrice();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
} 