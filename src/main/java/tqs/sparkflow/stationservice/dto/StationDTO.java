package tqs.sparkflow.stationservice.dto;

import tqs.sparkflow.stationservice.model.BaseStationFields;
import tqs.sparkflow.stationservice.model.Station;

public class StationDTO extends BaseStationFields {
    private Long id;
    private String name;
    private String address;
    private String status;
    private Integer quantityOfChargers;
    private Integer power;
    private Boolean isOperational;
    private Double price;

    public StationDTO() {
    }

    public StationDTO(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.address = station.getAddress();
        setCity(station.getCity());
        setCountry(station.getCountry());
        setLatitude(station.getLatitude());
        setLongitude(station.getLongitude());
        this.status = station.getStatus();
        this.quantityOfChargers = station.getQuantityOfChargers();
        this.power = station.getPower();
        setIsOperational(station.getIsOperational());
        setPrice(station.getPrice());
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
} 