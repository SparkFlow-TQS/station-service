package tqs.sparkflow.stationservice.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OpenChargeMapStationTest {
    @Test
    void testGettersAndSetters() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setId("1");
        station.setName("Test Station");
        station.setAddress("Test Address");
        station.setCity("Test City");
        station.setCountry("Test Country");
        station.setLatitude(38.7223);
        station.setLongitude(-9.1393);
        station.setQuantityOfChargers(1);

        assertThat(station.getId()).isEqualTo("1");
        assertThat(station.getName()).isEqualTo("Test Station");
        assertThat(station.getAddress()).isEqualTo("Test Address");
        assertThat(station.getCity()).isEqualTo("Test City");
        assertThat(station.getCountry()).isEqualTo("Test Country");
        assertThat(station.getLatitude()).isEqualTo(38.7223);
        assertThat(station.getLongitude()).isEqualTo(-9.1393);
        assertThat(station.getQuantityOfChargers()).isEqualTo(1);
    }
} 