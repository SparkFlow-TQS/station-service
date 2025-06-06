package tqs.sparkflow.stationservice.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.List;
import tqs.sparkflow.stationservice.model.Station;

class RoutePlanningResponseDTOTest {

    @Test
    @DisplayName("Test RoutePlanningResponseDTO constructor and getters")
    void testRoutePlanningResponseDTOConstructorAndGetters() {
        // Given
        List<Station> stations = new ArrayList<>();
        Station station1 = new Station();
        station1.setId(1L);
        station1.setName("Station 1");
        station1.setAddress("Address 1");
        station1.setCity("City 1");
        station1.setCountry("Country 1");
        station1.setLatitude(41.1579);
        station1.setLongitude(-8.6291);
        station1.setQuantityOfChargers(2);
        station1.setPower(50);
        station1.setStatus("Available");
        station1.setIsOperational(true);
        station1.setPrice(0.5);

        Station station2 = new Station();
        station2.setId(2L);
        station2.setName("Station 2");
        station2.setAddress("Address 2");
        station2.setCity("City 2");
        station2.setCountry("Country 2");
        station2.setLatitude(38.7223);
        station2.setLongitude(-9.1393);
        station2.setQuantityOfChargers(1);
        station2.setPower(30);
        station2.setStatus("Available");
        station2.setIsOperational(true);
        station2.setPrice(0.7);

        stations.add(station1);
        stations.add(station2);

        double distance = 100.0;
        double batteryUsage = 25.0;

        // When
        RoutePlanningResponseDTO responseDTO =
                new RoutePlanningResponseDTO(stations, distance, batteryUsage);

        // Then
        assertThat(responseDTO.getStations()).isEqualTo(stations);
        assertThat(responseDTO.getDistance()).isEqualTo(distance);
        assertThat(responseDTO.getBatteryUsage()).isEqualTo(batteryUsage);
    }

    @Test
    @DisplayName("Test RoutePlanningResponseDTO setters")
    void testRoutePlanningResponseDTOSetters() {
        // Given
        RoutePlanningResponseDTO responseDTO = new RoutePlanningResponseDTO();
        List<Station> stations = new ArrayList<>();
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

        stations.add(station);
        double distance = 100.0;
        double batteryUsage = 25.0;

        // When
        responseDTO.setStations(stations);
        responseDTO.setDistance(distance);
        responseDTO.setBatteryUsage(batteryUsage);

        // Then
        assertThat(responseDTO.getStations()).isEqualTo(stations);
        assertThat(responseDTO.getDistance()).isEqualTo(distance);
        assertThat(responseDTO.getBatteryUsage()).isEqualTo(batteryUsage);
    }

    @Test
    @DisplayName("Test RoutePlanningResponseDTO equals and hashCode")
    void testRoutePlanningResponseDTOEqualsAndHashCode() {
        // Given
        List<Station> stations1 = new ArrayList<>();
        Station station1 = new Station();
        station1.setId(1L);
        station1.setName("Station 1");
        station1.setAddress("Address 1");
        station1.setCity("City 1");
        station1.setCountry("Country 1");
        station1.setLatitude(41.1579);
        station1.setLongitude(-8.6291);
        station1.setQuantityOfChargers(2);
        station1.setPower(50);
        station1.setStatus("Available");
        station1.setIsOperational(true);
        station1.setPrice(0.5);

        stations1.add(station1);

        List<Station> stations2 = new ArrayList<>();
        Station station2 = new Station();
        station2.setId(1L);
        station2.setName("Station 1");
        station2.setAddress("Address 1");
        station2.setCity("City 1");
        station2.setCountry("Country 1");
        station2.setLatitude(41.1579);
        station2.setLongitude(-8.6291);
        station2.setQuantityOfChargers(2);
        station2.setPower(50);
        station2.setStatus("Available");
        station2.setIsOperational(true);
        station2.setPrice(0.5);

        stations2.add(station2);

        List<Station> stations3 = new ArrayList<>();
        Station station3 = new Station();
        station3.setId(2L);
        station3.setName("Station 2");
        station3.setAddress("Address 2");
        station3.setCity("City 2");
        station3.setCountry("Country 2");
        station3.setLatitude(38.7223);
        station3.setLongitude(-9.1393);
        station3.setQuantityOfChargers(1);
        station3.setPower(30);
        station3.setStatus("Available");
        station3.setIsOperational(true);
        station3.setPrice(0.7);

        stations3.add(station3);

        RoutePlanningResponseDTO responseDTO1 =
                new RoutePlanningResponseDTO(stations1, 100.0, 25.0);
        RoutePlanningResponseDTO responseDTO2 =
                new RoutePlanningResponseDTO(stations2, 100.0, 25.0);
        RoutePlanningResponseDTO responseDTO3 =
                new RoutePlanningResponseDTO(stations3, 200.0, 50.0);

        // Then
        assertThat(responseDTO1).isEqualTo(responseDTO2);
        assertThat(responseDTO1).isNotEqualTo(responseDTO3);
        assertThat(responseDTO1.hashCode()).isEqualTo(responseDTO2.hashCode());
        assertThat(responseDTO1.hashCode()).isNotEqualTo(responseDTO3.hashCode());

        // Check toString() format
        String expectedContent =
                "RoutePlanningResponseDTO{stations=[Station{id=1, name='Station 1'}], distance=100.0, batteryUsage=25.0}";
        assertThat(responseDTO1.toString()).startsWith(expectedContent);
        assertThat(responseDTO2.toString()).startsWith(expectedContent);
    }

    @Test
    @DisplayName("Test RoutePlanningResponseDTO toString")
    void testRoutePlanningResponseDTOToString() {
        // Given
        List<Station> stations = new ArrayList<>();
        Station station = new Station();
        station.setId(1L);
        station.setName("Station 1");
        station.setAddress("Address 1");
        station.setCity("City 1");
        station.setCountry("Country 1");
        station.setLatitude(41.1579);
        station.setLongitude(-8.6291);
        station.setQuantityOfChargers(2);
        station.setPower(50);
        station.setStatus("Available");
        station.setIsOperational(true);
        station.setPrice(0.5);

        stations.add(station);

        RoutePlanningResponseDTO responseDTO = new RoutePlanningResponseDTO(stations, 100.0, 25.0);

        // When
        String toString = responseDTO.toString();

        // Then
        String expectedContent =
                "RoutePlanningResponseDTO{stations=[Station{id=1, name='Station 1'}], distance=100.0, batteryUsage=25.0}";
        assertThat(toString).startsWith(expectedContent);
    }
}
