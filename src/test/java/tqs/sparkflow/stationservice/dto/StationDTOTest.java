package tqs.sparkflow.stationservice.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import tqs.sparkflow.stationservice.model.Station;

class StationDTOTest {

    @Test
    @DisplayName("Test StationDTO constructor and getters")
    void testStationDTOConstructorAndGetters() {
        // Given
        Station station = new Station();
        station.setId(1L);
        station.setName("Test Station");
        station.setAddress("Test Address");
        station.setCity("Test City");
        station.setCountry("Test Country");
        station.setLatitude(41.1579);
        station.setLongitude(-8.6291);
        station.setQuantityOfChargers(2);
        station.setPower(50);
        station.setStatus("Available");
        station.setIsOperational(true);
        station.setPrice(0.5);

        // When
        StationDTO stationDTO = new StationDTO(station);

        // Then
        assertThat(stationDTO.getId()).isEqualTo(station.getId());
        assertThat(stationDTO.getName()).isEqualTo(station.getName());
        assertThat(stationDTO.getAddress()).isEqualTo(station.getAddress());
        assertThat(stationDTO.getCity()).isEqualTo(station.getCity());
        assertThat(stationDTO.getCountry()).isEqualTo(station.getCountry());
        assertThat(stationDTO.getLatitude()).isEqualTo(station.getLatitude());
        assertThat(stationDTO.getLongitude()).isEqualTo(station.getLongitude());
        assertThat(stationDTO.getQuantityOfChargers()).isEqualTo(station.getQuantityOfChargers());
        assertThat(stationDTO.getPower()).isEqualTo(station.getPower());
        assertThat(stationDTO.getStatus()).isEqualTo(station.getStatus());
        assertThat(stationDTO.getIsOperational()).isEqualTo(station.getIsOperational());
        assertThat(stationDTO.getPrice()).isEqualTo(station.getPrice());
    }

    @Test
    @DisplayName("Test StationDTO setters")
    void testStationDTOSetters() {
        // Given
        StationDTO stationDTO = new StationDTO();
        Long id = 1L;
        String name = "Test Station";
        String address = "Test Address";
        String city = "Test City";
        String country = "Test Country";
        Double latitude = 41.1579;
        Double longitude = -8.6291;
        Integer quantityOfChargers = 2;
        Integer power = 50;
        String status = "Available";
        Boolean isOperational = true;
        Double price = 0.5;

        // When
        stationDTO.setId(id);
        stationDTO.setName(name);
        stationDTO.setAddress(address);
        stationDTO.setCity(city);
        stationDTO.setCountry(country);
        stationDTO.setLatitude(latitude);
        stationDTO.setLongitude(longitude);
        stationDTO.setQuantityOfChargers(quantityOfChargers);
        stationDTO.setPower(power);
        stationDTO.setStatus(status);
        stationDTO.setIsOperational(isOperational);
        stationDTO.setPrice(price);

        // Then
        assertThat(stationDTO.getId()).isEqualTo(id);
        assertThat(stationDTO.getName()).isEqualTo(name);
        assertThat(stationDTO.getAddress()).isEqualTo(address);
        assertThat(stationDTO.getCity()).isEqualTo(city);
        assertThat(stationDTO.getCountry()).isEqualTo(country);
        assertThat(stationDTO.getLatitude()).isEqualTo(latitude);
        assertThat(stationDTO.getLongitude()).isEqualTo(longitude);
        assertThat(stationDTO.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
        assertThat(stationDTO.getPower()).isEqualTo(power);
        assertThat(stationDTO.getStatus()).isEqualTo(status);
        assertThat(stationDTO.getIsOperational()).isEqualTo(isOperational);
        assertThat(stationDTO.getPrice()).isEqualTo(price);
    }

    @Test
    @DisplayName("Test StationDTO equals and hashCode")
    void testStationDTOEqualsAndHashCode() {
        // Given
        StationDTO stationDTO1 = new StationDTO();
        stationDTO1.setId(1L);
        stationDTO1.setName("Test Station");
        stationDTO1.setAddress("Test Address");
        stationDTO1.setCity("Test City");
        stationDTO1.setCountry("Test Country");
        stationDTO1.setLatitude(41.1579);
        stationDTO1.setLongitude(-8.6291);
        stationDTO1.setQuantityOfChargers(2);
        stationDTO1.setPower(50);
        stationDTO1.setStatus("Available");
        stationDTO1.setIsOperational(true);
        stationDTO1.setPrice(0.5);

        StationDTO stationDTO2 = new StationDTO();
        stationDTO2.setId(1L);
        stationDTO2.setName("Test Station");
        stationDTO2.setAddress("Test Address");
        stationDTO2.setCity("Test City");
        stationDTO2.setCountry("Test Country");
        stationDTO2.setLatitude(41.1579);
        stationDTO2.setLongitude(-8.6291);
        stationDTO2.setQuantityOfChargers(2);
        stationDTO2.setPower(50);
        stationDTO2.setStatus("Available");
        stationDTO2.setIsOperational(true);
        stationDTO2.setPrice(0.5);

        StationDTO stationDTO3 = new StationDTO();
        stationDTO3.setId(2L);
        stationDTO3.setName("Different Station");
        stationDTO3.setAddress("Different Address");
        stationDTO3.setCity("Different City");
        stationDTO3.setCountry("Different Country");
        stationDTO3.setLatitude(38.7223);
        stationDTO3.setLongitude(-9.1393);
        stationDTO3.setQuantityOfChargers(1);
        stationDTO3.setPower(30);
        stationDTO3.setStatus("Unavailable");
        stationDTO3.setIsOperational(false);
        stationDTO3.setPrice(0.7);

        // Then
        assertThat(stationDTO1).isEqualTo(stationDTO2).isNotEqualTo(stationDTO3)
                .hasSameHashCodeAs(stationDTO2).doesNotHaveSameHashCodeAs(stationDTO3);
    }

    @Test
    @DisplayName("Test StationDTO toString")
    void testStationDTOToString() {
        // Given
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId(1L);
        stationDTO.setName("Test Station");
        stationDTO.setAddress("Test Address");
        stationDTO.setCity("Test City");
        stationDTO.setCountry("Test Country");
        stationDTO.setLatitude(41.1579);
        stationDTO.setLongitude(-8.6291);
        stationDTO.setQuantityOfChargers(2);
        stationDTO.setPower(50);
        stationDTO.setStatus("Available");
        stationDTO.setIsOperational(true);
        stationDTO.setPrice(0.5);

        // When
        String toString = stationDTO.toString();

        // Then
        String expectedContent =
                "StationDTO{id=1, name='Test Station', address='Test Address', city='Test City', country='Test Country', latitude=41.1579, longitude=-8.6291, status='Available', isOperational=true, price=0.5, quantityOfChargers=2, power=50}";
        assertThat(toString).startsWith(expectedContent);
    }
}
