package tqs.sparkflow.stationservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import tqs.sparkflow.stationservice.model.Station;

class StationDTOTest {

    @Test
    void testNoArgsConstructor() {
        StationDTO dto = new StationDTO();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getAddress()).isNull();
        assertThat(dto.getCity()).isNull();
        assertThat(dto.getCountry()).isNull();
        assertThat(dto.getLatitude()).isNull();
        assertThat(dto.getLongitude()).isNull();
        assertThat(dto.getConnectorType()).isNull();
        assertThat(dto.getStatus()).isNull();
        assertThat(dto.getPower()).isNull();
        assertThat(dto.getIsOperational()).isNull();
        assertThat(dto.getPrice()).isNull();
    }

    @Test
    void testStationConstructor() {
        // Create a test station
        Station station = new Station();
        station.setId(1L);
        station.setName("Test Station");
        station.setAddress("Test Address");
        station.setCity("Lisbon");
        station.setCountry("Portugal");
        station.setLatitude(38.7223);
        station.setLongitude(-9.1393);
        station.setConnectorType("Type 2");
        station.setStatus("Available");
        station.setPower(22);
        station.setIsOperational(true);
        station.setPrice(0.25);

        // Create DTO from station
        StationDTO dto = new StationDTO(station);

        // Verify all fields are correctly mapped
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Station");
        assertThat(dto.getAddress()).isEqualTo("Test Address");
        assertThat(dto.getCity()).isEqualTo("Lisbon");
        assertThat(dto.getCountry()).isEqualTo("Portugal");
        assertThat(dto.getLatitude()).isEqualTo(38.7223);
        assertThat(dto.getLongitude()).isEqualTo(-9.1393);
        assertThat(dto.getConnectorType()).isEqualTo("Type 2");
        assertThat(dto.getStatus()).isEqualTo("Available");
        assertThat(dto.getPower()).isEqualTo(22);
        assertThat(dto.getIsOperational()).isTrue();
        assertThat(dto.getPrice()).isEqualTo(0.25);
    }

    @Test
    void testSettersAndGetters() {
        StationDTO dto = new StationDTO();

        dto.setId(2L);
        dto.setName("New Station");
        dto.setAddress("New Address");
        dto.setCity("Porto");
        dto.setCountry("Portugal");
        dto.setLatitude(41.1579);
        dto.setLongitude(-8.6291);
        dto.setConnectorType("CCS");
        dto.setStatus("In Use");
        dto.setPower(50);
        dto.setIsOperational(false);
        dto.setPrice(0.30);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("New Station");
        assertThat(dto.getAddress()).isEqualTo("New Address");
        assertThat(dto.getCity()).isEqualTo("Porto");
        assertThat(dto.getCountry()).isEqualTo("Portugal");
        assertThat(dto.getLatitude()).isEqualTo(41.1579);
        assertThat(dto.getLongitude()).isEqualTo(-8.6291);
        assertThat(dto.getConnectorType()).isEqualTo("CCS");
        assertThat(dto.getStatus()).isEqualTo("In Use");
        assertThat(dto.getPower()).isEqualTo(50);
        assertThat(dto.getIsOperational()).isFalse();
        assertThat(dto.getPrice()).isEqualTo(0.30);
    }
} 