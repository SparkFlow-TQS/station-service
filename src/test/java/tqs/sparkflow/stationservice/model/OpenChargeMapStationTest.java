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
    assertThat(station.getStatus()).isNull();
  }

  @Test
  void whenSettingAllFields_thenAllFieldsAreUpdated() {
    // Given
    String id = "123";
    String name = "Test Station";
    String address = "123 Test St";
    String city = "Test City";
    String country = "Test Country";
    double latitude = 40.7128;
    double longitude = -74.0060;
    Integer quantityOfChargers = 2;
    String status = "Available";

    // When
    station.setId(id);
    station.setName(name);
    station.setAddress(address);
    station.setCity(city);
    station.setCountry(country);
    station.setLatitude(latitude);
    station.setLongitude(longitude);
    station.setQuantityOfChargers(quantityOfChargers);
    station.setStatus(status);

    // Then
    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
    assertThat(station.getStatus()).isEqualTo(status);
  }

  @Test
  void whenSettingNullValues_thenFieldsAreNull() {
    // Given
    station.setId("123");
    station.setName("Test Station");
    station.setAddress("123 Test St");
    station.setCity("Test City");
    station.setCountry("Test Country");
    station.setLatitude(40.7128);
    station.setLongitude(-74.0060);
    station.setQuantityOfChargers(2);
    station.setStatus("Available");

    // When
    station.setId(null);
    station.setName(null);
    station.setAddress(null);
    station.setCity(null);
    station.setCountry(null);
    station.setQuantityOfChargers(null);
    station.setStatus(null);

    // Then
    assertThat(station.getId()).isNull();
    assertThat(station.getName()).isNull();
    assertThat(station.getAddress()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
    assertThat(station.getQuantityOfChargers()).isNull();
    assertThat(station.getStatus()).isNull();
  }

  @Test
  void whenSettingCoordinates_thenValuesAreUpdated() {
    // Given
    double latitude = 40.7128;
    double longitude = -74.0060;

    // When
    station.setLatitude(latitude);
    station.setLongitude(longitude);

    // Then
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
  }
} 