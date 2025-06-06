package tqs.sparkflow.stationservice.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class StationFilterDTOTest {

    @Test
    @DisplayName("Test StationFilterDTO constructor and getters")
    void testStationFilterDTOConstructorAndGetters() {
        // Given
        String name = "Test Station";
        String address = "Test Address";
        String city = "Test City";
        String country = "Test Country";
        Double latitude = 41.1579;
        Double longitude = -8.6291;
        String status = "Available";
        Boolean isOperational = true;
        Double price = 0.5;
        Double minPrice = 0.0;
        Double maxPrice = 1.0;
        Integer radius = 10;
        Integer minPower = 20;
        Integer maxPower = 50;

        // When
        StationFilterDTO filterDTO = new StationFilterDTO(name, address, city, country, latitude, longitude, status, isOperational, price, minPrice, maxPrice, radius, minPower, maxPower);

        // Then
        assertThat(filterDTO.getName()).isEqualTo(name);
        assertThat(filterDTO.getAddress()).isEqualTo(address);
        assertThat(filterDTO.getCity()).isEqualTo(city);
        assertThat(filterDTO.getCountry()).isEqualTo(country);
        assertThat(filterDTO.getLatitude()).isEqualTo(latitude);
        assertThat(filterDTO.getLongitude()).isEqualTo(longitude);
        assertThat(filterDTO.getStatus()).isEqualTo(status);
        assertThat(filterDTO.getIsOperational()).isEqualTo(isOperational);
        assertThat(filterDTO.getPrice()).isEqualTo(price);
        assertThat(filterDTO.getMinPrice()).isEqualTo(minPrice);
        assertThat(filterDTO.getMaxPrice()).isEqualTo(maxPrice);
        assertThat(filterDTO.getRadius()).isEqualTo(radius);
        assertThat(filterDTO.getMinPower()).isEqualTo(minPower);
        assertThat(filterDTO.getMaxPower()).isEqualTo(maxPower);
    }

    @Test
    @DisplayName("Test StationFilterDTO setters")
    void testStationFilterDTOSetters() {
        // Given
        StationFilterDTO filterDTO = new StationFilterDTO();
        String name = "Test Station";
        String address = "Test Address";
        String city = "Test City";
        String country = "Test Country";
        Double latitude = 41.1579;
        Double longitude = -8.6291;
        String status = "Available";
        Boolean isOperational = true;
        Double price = 0.5;
        Double minPrice = 0.0;
        Double maxPrice = 1.0;
        Integer radius = 10;
        Integer minPower = 20;
        Integer maxPower = 50;

        // When
        filterDTO.setName(name);
        filterDTO.setAddress(address);
        filterDTO.setCity(city);
        filterDTO.setCountry(country);
        filterDTO.setLatitude(latitude);
        filterDTO.setLongitude(longitude);
        filterDTO.setStatus(status);
        filterDTO.setIsOperational(isOperational);
        filterDTO.setPrice(price);
        filterDTO.setMinPrice(minPrice);
        filterDTO.setMaxPrice(maxPrice);
        filterDTO.setRadius(radius);
        filterDTO.setMinPower(minPower);
        filterDTO.setMaxPower(maxPower);

        // Then
        assertThat(filterDTO.getName()).isEqualTo(name);
        assertThat(filterDTO.getAddress()).isEqualTo(address);
        assertThat(filterDTO.getCity()).isEqualTo(city);
        assertThat(filterDTO.getCountry()).isEqualTo(country);
        assertThat(filterDTO.getLatitude()).isEqualTo(latitude);
        assertThat(filterDTO.getLongitude()).isEqualTo(longitude);
        assertThat(filterDTO.getStatus()).isEqualTo(status);
        assertThat(filterDTO.getIsOperational()).isEqualTo(isOperational);
        assertThat(filterDTO.getPrice()).isEqualTo(price);
        assertThat(filterDTO.getMinPrice()).isEqualTo(minPrice);
        assertThat(filterDTO.getMaxPrice()).isEqualTo(maxPrice);
        assertThat(filterDTO.getRadius()).isEqualTo(radius);
        assertThat(filterDTO.getMinPower()).isEqualTo(minPower);
        assertThat(filterDTO.getMaxPower()).isEqualTo(maxPower);
    }

    @Test
    @DisplayName("Test StationFilterDTO equals and hashCode")
    void testStationFilterDTOEqualsAndHashCode() {
        // Given
        StationFilterDTO filterDTO1 = new StationFilterDTO();
        filterDTO1.setName("Test Station");
        filterDTO1.setAddress("Test Address");
        filterDTO1.setCity("Test City");
        filterDTO1.setCountry("Test Country");
        filterDTO1.setLatitude(41.1579);
        filterDTO1.setLongitude(-8.6291);
        filterDTO1.setStatus("Available");
        filterDTO1.setIsOperational(true);
        filterDTO1.setPrice(0.5);
        filterDTO1.setMinPrice(0.0);
        filterDTO1.setMaxPrice(1.0);
        filterDTO1.setRadius(10);
        filterDTO1.setMinPower(20);
        filterDTO1.setMaxPower(50);

        StationFilterDTO filterDTO2 = new StationFilterDTO();
        filterDTO2.setName("Test Station");
        filterDTO2.setAddress("Test Address");
        filterDTO2.setCity("Test City");
        filterDTO2.setCountry("Test Country");
        filterDTO2.setLatitude(41.1579);
        filterDTO2.setLongitude(-8.6291);
        filterDTO2.setStatus("Available");
        filterDTO2.setIsOperational(true);
        filterDTO2.setPrice(0.5);
        filterDTO2.setMinPrice(0.0);
        filterDTO2.setMaxPrice(1.0);
        filterDTO2.setRadius(10);
        filterDTO2.setMinPower(20);
        filterDTO2.setMaxPower(50);

        StationFilterDTO filterDTO3 = new StationFilterDTO();
        filterDTO3.setName("Different Station");
        filterDTO3.setAddress("Different Address");
        filterDTO3.setCity("Different City");
        filterDTO3.setCountry("Different Country");
        filterDTO3.setLatitude(38.7223);
        filterDTO3.setLongitude(-9.1393);
        filterDTO3.setStatus("Unavailable");
        filterDTO3.setIsOperational(false);
        filterDTO3.setPrice(0.7);
        filterDTO3.setMinPrice(0.5);
        filterDTO3.setMaxPrice(2.0);
        filterDTO3.setRadius(20);
        filterDTO3.setMinPower(30);
        filterDTO3.setMaxPower(60);

        // Then
        assertThat(filterDTO1).isEqualTo(filterDTO2);
        assertThat(filterDTO1).isNotEqualTo(filterDTO3);
        assertThat(filterDTO1.hashCode()).isEqualTo(filterDTO2.hashCode());
        assertThat(filterDTO1.hashCode()).isNotEqualTo(filterDTO3.hashCode());
    }

    @Test
    @DisplayName("Test StationFilterDTO toString")
    void testStationFilterDTOToString() {
        // Given
        StationFilterDTO filterDTO = new StationFilterDTO();
        filterDTO.setName("Test Station");
        filterDTO.setAddress("Test Address");
        filterDTO.setCity("Test City");
        filterDTO.setCountry("Test Country");
        filterDTO.setLatitude(41.1579);
        filterDTO.setLongitude(-8.6291);
        filterDTO.setStatus("Available");
        filterDTO.setIsOperational(true);
        filterDTO.setPrice(0.5);
        filterDTO.setMinPrice(0.0);
        filterDTO.setMaxPrice(1.0);
        filterDTO.setRadius(10);
        filterDTO.setMinPower(20);
        filterDTO.setMaxPower(50);

        // When
        String toString = filterDTO.toString();

        // Then
        String expectedContent = "StationFilterDTO{name='Test Station', address='Test Address', city='Test City', country='Test Country', latitude=41.1579, longitude=-8.6291, status='Available', isOperational=true, price=0.5, minPrice=0.0, maxPrice=1.0, radius=10, minPower=20, maxPower=50}";
        assertThat(toString).startsWith(expectedContent);
    }
} 