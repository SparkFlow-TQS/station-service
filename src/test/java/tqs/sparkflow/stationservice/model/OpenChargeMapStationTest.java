package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OpenChargeMapStationTest {

    @Test
    void whenCreatingEmptyStation_thenAllFieldsAreNull() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        
        assertThat(station.getId()).isNull();
        assertThat(station.getName()).isNull();
        assertThat(station.getAddress()).isNull();
        assertThat(station.getCity()).isNull();
        assertThat(station.getCountry()).isNull();
        assertThat(station.getLatitude()).isNull();
        assertThat(station.getLongitude()).isNull();
        assertThat(station.getQuantityOfChargers()).isNull();
        assertThat(station.getStatus()).isNull();
    }

    @Test
    void whenCreatingStationWithValues_thenAllFieldsAreSet() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setId("1");
        station.setName("Test Station");
        station.setAddress("Test Address");
        station.setCity("Test City");
        station.setCountry("Test Country");
        station.setLatitude(41.1579);
        station.setLongitude(-8.6291);
        station.setQuantityOfChargers(2);
        station.setStatus("Available");

        assertThat(station.getId()).isEqualTo("1");
        assertThat(station.getName()).isEqualTo("Test Station");
        assertThat(station.getAddress()).isEqualTo("Test Address");
        assertThat(station.getCity()).isEqualTo("Test City");
        assertThat(station.getCountry()).isEqualTo("Test Country");
        assertThat(station.getLatitude()).isEqualTo(41.1579);
        assertThat(station.getLongitude()).isEqualTo(-8.6291);
        assertThat(station.getQuantityOfChargers()).isEqualTo(2);
        assertThat(station.getStatus()).isEqualTo("Available");
    }

    @Test
    void testNullValues() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setId(null);
        station.setName(null);
        station.setAddress(null);
        station.setCity(null);
        station.setCountry(null);
        station.setLatitude(null);
        station.setLongitude(null);
        station.setQuantityOfChargers(null);
        station.setStatus(null);

        assertThat(station.getId()).isNull();
        assertThat(station.getName()).isNull();
        assertThat(station.getAddress()).isNull();
        assertThat(station.getCity()).isNull();
        assertThat(station.getCountry()).isNull();
        assertThat(station.getLatitude()).isNull();
        assertThat(station.getLongitude()).isNull();
        assertThat(station.getQuantityOfChargers()).isNull();
        assertThat(station.getStatus()).isNull();
    }
} 