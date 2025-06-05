package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpenChargeMapStationTest {

  private OpenChargeMapStation station;

  @BeforeEach
  void setUp() {
    station = new OpenChargeMapStation();
  }

  @Test
  void whenCreatingEmptyStation_thenAllFieldsAreNull() {
    assertThat(station.getId()).isNull();
    assertThat(station.getName()).isNull();
    assertThat(station.getAddress()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
    assertThat(station.getLatitude()).isEqualTo(0.0);
    assertThat(station.getLongitude()).isEqualTo(0.0);
    assertThat(station.getQuantityOfChargers()).isNull();
  }

  @Test
  void whenSettingStationFields_thenFieldsAreUpdated() {
    // Given
    String id = "123";
    String name = "Test Station";
    String address = "Test Address";
    String city = "Test City";
    String country = "Test Country";
    double latitude = 38.7223;
    double longitude = -9.1393;
    int quantityOfChargers = 5;

    // When
    station.setId(id);
    station.setName(name);
    station.setAddress(address);
    station.setCity(city);
    station.setCountry(country);
    station.setLatitude(latitude);
    station.setLongitude(longitude);
    station.setQuantityOfChargers(quantityOfChargers);

    // Then
    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
  }
} 