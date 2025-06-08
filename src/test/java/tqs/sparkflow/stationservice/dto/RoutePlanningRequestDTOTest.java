package tqs.sparkflow.stationservice.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class RoutePlanningRequestDTOTest {

    @Test
    @DisplayName("Test RoutePlanningRequestDTO constructor and getters")
    void testRoutePlanningRequestDTOConstructorAndGetters() {
        // Given
        RoutePlanningRequestDTO requestDTO = new RoutePlanningRequestDTO();
        Double startLatitude = 41.1579;
        Double startLongitude = -8.6291;
        Double destLatitude = 38.7223;
        Double destLongitude = -9.1393;
        Double batteryCapacity = 50.0;
        Double carAutonomy = 0.2;

        // When
        requestDTO.setStartLatitude(startLatitude);
        requestDTO.setStartLongitude(startLongitude);
        requestDTO.setDestLatitude(destLatitude);
        requestDTO.setDestLongitude(destLongitude);
        requestDTO.setBatteryCapacity(batteryCapacity);
        requestDTO.setCarAutonomy(carAutonomy);

        // Then
        assertThat(requestDTO.getStartLatitude()).isEqualTo(startLatitude);
        assertThat(requestDTO.getStartLongitude()).isEqualTo(startLongitude);
        assertThat(requestDTO.getDestLatitude()).isEqualTo(destLatitude);
        assertThat(requestDTO.getDestLongitude()).isEqualTo(destLongitude);
        assertThat(requestDTO.getBatteryCapacity()).isEqualTo(batteryCapacity);
        assertThat(requestDTO.getCarAutonomy()).isEqualTo(carAutonomy);
    }

    @Test
    @DisplayName("Test RoutePlanningRequestDTO equals and hashCode")
    void testRoutePlanningRequestDTOEqualsAndHashCode() {
        // Given
        RoutePlanningRequestDTO requestDTO1 = new RoutePlanningRequestDTO();
        requestDTO1.setStartLatitude(41.1579);
        requestDTO1.setStartLongitude(-8.6291);
        requestDTO1.setDestLatitude(38.7223);
        requestDTO1.setDestLongitude(-9.1393);
        requestDTO1.setBatteryCapacity(50.0);
        requestDTO1.setCarAutonomy(0.2);

        RoutePlanningRequestDTO requestDTO2 = new RoutePlanningRequestDTO();
        requestDTO2.setStartLatitude(41.1579);
        requestDTO2.setStartLongitude(-8.6291);
        requestDTO2.setDestLatitude(38.7223);
        requestDTO2.setDestLongitude(-9.1393);
        requestDTO2.setBatteryCapacity(50.0);
        requestDTO2.setCarAutonomy(0.2);

        RoutePlanningRequestDTO requestDTO3 = new RoutePlanningRequestDTO();
        requestDTO3.setStartLatitude(42.1579);
        requestDTO3.setStartLongitude(-7.6291);
        requestDTO3.setDestLatitude(39.7223);
        requestDTO3.setDestLongitude(-8.1393);
        requestDTO3.setBatteryCapacity(60.0);
        requestDTO3.setCarAutonomy(0.3);

        // Then
        assertThat(requestDTO1).isEqualTo(requestDTO2).isNotEqualTo(requestDTO3)
                .hasSameHashCodeAs(requestDTO2).doesNotHaveSameHashCodeAs(requestDTO3);
    }

    @Test
    @DisplayName("Test RoutePlanningRequestDTO toString")
    void testRoutePlanningRequestDTOToString() {
        // Given
        RoutePlanningRequestDTO requestDTO = new RoutePlanningRequestDTO();
        requestDTO.setStartLatitude(41.1579);
        requestDTO.setStartLongitude(-8.6291);
        requestDTO.setDestLatitude(38.7223);
        requestDTO.setDestLongitude(-9.1393);
        requestDTO.setBatteryCapacity(50.0);
        requestDTO.setCarAutonomy(0.2);

        // When
        String toString = requestDTO.toString();

        // Then
        assertThat(toString).contains("startLatitude=41.1579").contains("startLongitude=-8.6291")
                .contains("destLatitude=38.7223").contains("destLongitude=-9.1393")
                .contains("batteryCapacity=50.0").contains("carAutonomy=0.2");
    }
}
