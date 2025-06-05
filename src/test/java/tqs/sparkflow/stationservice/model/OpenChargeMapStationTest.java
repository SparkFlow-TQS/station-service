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

  @Test
  void testGettersAndSetters() {
    OpenChargeMapStation station = new OpenChargeMapStation();
    
    // Test ID
    station.setId("123");
    assertThat(station.getId()).isEqualTo("123");
    
    // Test Name
    station.setName("Test Station");
    assertThat(station.getName()).isEqualTo("Test Station");
    
    // Test Address
    station.setAddress("123 Test St");
    assertThat(station.getAddress()).isEqualTo("123 Test St");
    
    // Test City
    station.setCity("Test City");
    assertThat(station.getCity()).isEqualTo("Test City");
    
    // Test Country
    station.setCountry("Test Country");
    assertThat(station.getCountry()).isEqualTo("Test Country");
    
    // Test Latitude
    station.setLatitude(40.7128);
    assertThat(station.getLatitude()).isEqualTo(40.7128);
    
    // Test Longitude
    station.setLongitude(-74.0060);
    assertThat(station.getLongitude()).isEqualTo(-74.0060);
    
    // Test Quantity of Chargers
    station.setQuantityOfChargers(5);
    assertThat(station.getQuantityOfChargers()).isEqualTo(5);
    
    // Test Status
    station.setStatus("Available");
    assertThat(station.getStatus()).isEqualTo("Available");
  }

  @Test
  void testNullValues() {
    OpenChargeMapStation station = new OpenChargeMapStation();
    
    assertThat(station.getId()).isNull();
    assertThat(station.getName()).isNull();
    assertThat(station.getAddress()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
    assertThat(station.getLatitude()).isZero();
    assertThat(station.getLongitude()).isZero();
    assertThat(station.getQuantityOfChargers()).isNull();
    assertThat(station.getStatus()).isNull();
  }
} 