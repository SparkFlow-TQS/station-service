package tqs.sparkflow.station_service.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StationTest {

    @Test
    void whenCreatingEmptyStation_thenAllFieldsAreNull() {
        // When
        Station station = new Station();

        // Then
        assertThat(station.getId()).isNull();
        assertThat(station.getName()).isNull();
        assertThat(station.getAddress()).isNull();
        assertThat(station.getLatitude()).isNull();
        assertThat(station.getLongitude()).isNull();
        assertThat(station.getStatus()).isNull();
        assertThat(station.getConnectorType()).isNull();
    }

    @Test
    void whenCreatingStationWithAllFields_thenAllFieldsAreSet() {
        // Given
        String id = "1";
        String name = "Test Station";
        String address = "Test Address";
        String latitude = "38.7223";
        String longitude = "-9.1393";
        String status = "Available";
        String connectorType = "Type2";

        // When
        Station station = new Station(id, name, address, latitude, longitude, status, connectorType);

        // Then
        assertThat(station.getId()).isEqualTo(id);
        assertThat(station.getName()).isEqualTo(name);
        assertThat(station.getAddress()).isEqualTo(address);
        assertThat(station.getLatitude()).isEqualTo(latitude);
        assertThat(station.getLongitude()).isEqualTo(longitude);
        assertThat(station.getStatus()).isEqualTo(status);
        assertThat(station.getConnectorType()).isEqualTo(connectorType);
    }

    @Test
    void whenSettingStationFields_thenFieldsAreUpdated() {
        // Given
        Station station = new Station();
        String id = "1";
        String name = "Test Station";
        String address = "Test Address";
        String latitude = "38.7223";
        String longitude = "-9.1393";
        String status = "Available";
        String connectorType = "Type2";

        // When
        station.setId(id);
        station.setName(name);
        station.setAddress(address);
        station.setLatitude(latitude);
        station.setLongitude(longitude);
        station.setStatus(status);
        station.setConnectorType(connectorType);

        // Then
        assertThat(station.getId()).isEqualTo(id);
        assertThat(station.getName()).isEqualTo(name);
        assertThat(station.getAddress()).isEqualTo(address);
        assertThat(station.getLatitude()).isEqualTo(latitude);
        assertThat(station.getLongitude()).isEqualTo(longitude);
        assertThat(station.getStatus()).isEqualTo(status);
        assertThat(station.getConnectorType()).isEqualTo(connectorType);
    }

    @Test
    void whenCallingToString_thenReturnsCorrectString() {
        // Given
        Station station = new Station("1", "Test Station", "Test Address", "38.7223", "-9.1393", "Available", "Type2");

        // When
        String toString = station.toString();

        // Then
        assertThat(toString).isEqualTo("Station{id='1', name='Test Station'}");
    }
} 