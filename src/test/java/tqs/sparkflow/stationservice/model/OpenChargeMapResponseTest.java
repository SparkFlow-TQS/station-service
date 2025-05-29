package tqs.sparkflow.stationservice.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class OpenChargeMapResponseTest {
    @Test
    void testGettersAndSetters() {
        OpenChargeMapResponse response = new OpenChargeMapResponse();
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setId("1");
        response.setStations(List.of(station));
        assertThat(response.getStations()).hasSize(1);
        assertThat(response.getStations().get(0).getId()).isEqualTo("1");
    }
} 