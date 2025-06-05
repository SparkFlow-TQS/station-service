package tqs.sparkflow.stationservice.dto;

import tqs.sparkflow.stationservice.model.BaseStationFields;
import tqs.sparkflow.stationservice.model.Station;

public class StationDTO extends BaseStationFields<Long> {
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