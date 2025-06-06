package tqs.sparkflow.stationservice.dto;

import java.util.Objects;
import tqs.sparkflow.stationservice.model.BaseStationFields;
import tqs.sparkflow.stationservice.model.Station;

public class StationDTO extends BaseStationFields {
    private Long id;
    private Integer quantityOfChargers;
    private Integer power;

    public StationDTO() {
    }

    public StationDTO(Station station) {
        this.id = station.getId();
        setName(station.getName());
        setAddress(station.getAddress());
        setCity(station.getCity());
        setCountry(station.getCountry());
        setLatitude(station.getLatitude());
        setLongitude(station.getLongitude());
        setStatus(station.getStatus());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StationDTO stationDTO = (StationDTO) o;
        return Objects.equals(id, stationDTO.id) &&
               Objects.equals(quantityOfChargers, stationDTO.quantityOfChargers) &&
               Objects.equals(power, stationDTO.power);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, quantityOfChargers, power);
    }

    @Override
    public String toString() {
        return "StationDTO{" +
               "id=" + id +
               ", name='" + getName() + '\'' +
               ", address='" + getAddress() + '\'' +
               ", city='" + getCity() + '\'' +
               ", country='" + getCountry() + '\'' +
               ", latitude=" + getLatitude() +
               ", longitude=" + getLongitude() +
               ", status='" + getStatus() + '\'' +
               ", isOperational=" + getIsOperational() +
               ", price=" + getPrice() +
               ", quantityOfChargers=" + quantityOfChargers +
               ", power=" + power +
               '}';
    }
} 