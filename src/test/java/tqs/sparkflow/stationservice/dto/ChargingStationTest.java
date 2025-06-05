package tqs.sparkflow.stationservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChargingStationTest {

    @Test
    void testNoArgsConstructor() {
        ChargingStation station = new ChargingStation();
        assertThat(station).isNotNull();
        assertThat(station.getId()).isNull();
        assertThat(station.getName()).isNull();
        assertThat(station.getLatitude()).isZero();
        assertThat(station.getLongitude()).isZero();
        assertThat(station.getAddress()).isNull();
        assertThat(station.getCity()).isNull();
        assertThat(station.getCountry()).isNull();
        assertThat(station.getConnectorType()).isNull();
        assertThat(station.getStatus()).isNull();
        assertThat(station.isOperational()).isFalse();
    }

    @Test
    void testAllArgsConstructor() {
        ChargingStation station = new ChargingStation(
            1L, "Test Station", 38.7223, -9.1393,
            "Test Address", "Lisbon", "Portugal",
            "Type 2", "Available", true
        );

        assertThat(station.getId()).isEqualTo(1L);
        assertThat(station.getName()).isEqualTo("Test Station");
        assertThat(station.getLatitude()).isEqualTo(38.7223);
        assertThat(station.getLongitude()).isEqualTo(-9.1393);
        assertThat(station.getAddress()).isEqualTo("Test Address");
        assertThat(station.getCity()).isEqualTo("Lisbon");
        assertThat(station.getCountry()).isEqualTo("Portugal");
        assertThat(station.getConnectorType()).isEqualTo("Type 2");
        assertThat(station.getStatus()).isEqualTo("Available");
        assertThat(station.isOperational()).isTrue();
    }

    @Test
    void testSettersAndGetters() {
        ChargingStation station = new ChargingStation();

        station.setId(2L);
        station.setName("New Station");
        station.setLatitude(41.1579);
        station.setLongitude(-8.6291);
        station.setAddress("New Address");
        station.setCity("Porto");
        station.setCountry("Portugal");
        station.setConnectorType("CCS");
        station.setStatus("In Use");
        station.setOperational(false);

        assertThat(station.getId()).isEqualTo(2L);
        assertThat(station.getName()).isEqualTo("New Station");
        assertThat(station.getLatitude()).isEqualTo(41.1579);
        assertThat(station.getLongitude()).isEqualTo(-8.6291);
        assertThat(station.getAddress()).isEqualTo("New Address");
        assertThat(station.getCity()).isEqualTo("Porto");
        assertThat(station.getCountry()).isEqualTo("Portugal");
        assertThat(station.getConnectorType()).isEqualTo("CCS");
        assertThat(station.getStatus()).isEqualTo("In Use");
        assertThat(station.isOperational()).isFalse();
    }
} 