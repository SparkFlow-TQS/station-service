package tqs.sparkflow.stationservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import tqs.sparkflow.stationservice.model.Station;

class StationDTOTest {

    @Test
    void whenConvertingStationToDTO_thenAllFieldsAreMapped() {
        // Given
        Station station = new Station();
        station.setId(1L);
        station.setName("Test Station");
        station.setAddress("Test Address");
        station.setCity("Test City");
        station.setCountry("Test Country");
        station.setLatitude(38.7223);
        station.setLongitude(-9.1393);
        station.setQuantityOfChargers(2);
        station.setStatus("Available");
        station.setIsOperational(true);

        // When
        StationDTO dto = new StationDTO(station);

        // Then
        assertThat(dto.getId()).isEqualTo(station.getId());
        assertThat(dto.getName()).isEqualTo(station.getName());
        assertThat(dto.getAddress()).isEqualTo(station.getAddress());
        assertThat(dto.getCity()).isEqualTo(station.getCity());
        assertThat(dto.getCountry()).isEqualTo(station.getCountry());
        assertThat(dto.getLatitude()).isEqualTo(station.getLatitude());
        assertThat(dto.getLongitude()).isEqualTo(station.getLongitude());
        assertThat(dto.getQuantityOfChargers()).isEqualTo(station.getQuantityOfChargers());
        assertThat(dto.getStatus()).isEqualTo(station.getStatus());
        assertThat(dto.getIsOperational()).isEqualTo(station.getIsOperational());
    }

    @Test
    void whenCreatingEmptyDTO_thenAllFieldsAreNull() {
        // Given
        StationDTO dto = new StationDTO();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getAddress()).isNull();
        assertThat(dto.getCity()).isNull();
        assertThat(dto.getCountry()).isNull();
        assertThat(dto.getLatitude()).isNull();
        assertThat(dto.getLongitude()).isNull();
        assertThat(dto.getQuantityOfChargers()).isNull();
        assertThat(dto.getStatus()).isNull();
        assertThat(dto.getIsOperational()).isNull();
    }

    @Test
    void whenSettingDTOFields_thenAllFieldsAreUpdated() {
        // Given
        StationDTO dto = new StationDTO();

        // When
        dto.setId(1L);
        dto.setName("Test Station");
        dto.setAddress("Test Address");
        dto.setCity("Test City");
        dto.setCountry("Test Country");
        dto.setLatitude(38.7223);
        dto.setLongitude(-9.1393);
        dto.setQuantityOfChargers(2);
        dto.setStatus("Available");
        dto.setIsOperational(true);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Station");
        assertThat(dto.getAddress()).isEqualTo("Test Address");
        assertThat(dto.getCity()).isEqualTo("Test City");
        assertThat(dto.getCountry()).isEqualTo("Test Country");
        assertThat(dto.getLatitude()).isEqualTo(38.7223);
        assertThat(dto.getLongitude()).isEqualTo(-9.1393);
        assertThat(dto.getQuantityOfChargers()).isEqualTo(2);
        assertThat(dto.getStatus()).isEqualTo("Available");
        assertThat(dto.getIsOperational()).isTrue();
    }
} 