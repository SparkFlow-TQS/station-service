package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class StationTest {

  @Test
  void whenCreatingEmptyStation_thenAllFieldsAreNull() {
    // When
    Station station = new Station();

    // Then
    assertThat(station.getId()).isNull();
    assertThat(station.getName()).isNull();
    assertThat(station.getAddress()).isNull();
    assertThat(station.getLatitude()).isNull();
    assertThat(station.getLongitude()).isNull();
    assertThat(station.getStatus()).isNull();
    assertThat(station.getQuantityOfChargers()).isNull();
  }

  @Test
  void whenSettingStationFields_thenFieldsAreUpdated() {
    // Given
    Station station = new Station();
    Long id = 1L;
    String name = "Test Station";
    String address = "Test Address";
    double latitude = 38.7223;
    double longitude = -9.1393;
    String status = "Available";
    int quantityOfChargers = 1;

    // When
    station.setId(id);
    station.setName(name);
    station.setAddress(address);
    station.setLatitude(latitude);
    station.setLongitude(longitude);
    station.setStatus(status);
    station.setQuantityOfChargers(quantityOfChargers);

    // Then
    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getStatus()).isEqualTo(status);
    assertThat(station.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
  }

  @Test
  void whenCallingToString_thenReturnsCorrectString() {
    // Given
    Station station = new Station();
    station.setId(1L);
    station.setName("Test Station");

    // When
    String toString = station.toString();

    // Then
    assertThat(toString).isEqualTo("Station{id=1, name='Test Station'}");
  }

  @Test
  void whenCreatingStationWithAllFields_thenAllFieldsAreSet() {
    // Given
    String externalId = "ext123";
    String name = "Test Station";
    String address = "123 Test St";
    String city = "Test City";
    String country = "Test Country";
    Double latitude = 40.7128;
    Double longitude = -74.0060;
    int quantityOfChargers = 1;
    Integer power = 50;
    Boolean isOperational = true;
    Double price = 0.35;

    // When
    Station station = new Station(
        externalId,
        name,
        address,
        city,
        country,
        latitude,
        longitude,
        quantityOfChargers,
        power,
        isOperational,
        price
    );

    // Then
    assertThat(station.getExternalId()).isEqualTo(externalId);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
    assertThat(station.getPower()).isEqualTo(power);
    assertThat(station.getIsOperational()).isEqualTo(isOperational);
    assertThat(station.getPrice()).isEqualTo(price);
  }

  @Test
  void whenUsingBuilder_thenCreatesStationWithAllFields() {
    // Given
    String externalId = "ext123";
    String name = "Test Station";
    String address = "123 Test St";
    String city = "Test City";
    String country = "Test Country";
    Double latitude = 40.7128;
    Double longitude = -74.0060;
    int quantityOfChargers = 1;
    Integer power = 50;
    Boolean isOperational = true;
    Double price = 0.35;
    String status = "Available";

    // When
    Station station = new Station.Builder()
        .externalId(externalId)
        .name(name)
        .address(address)
        .city(city)
        .country(country)
        .latitude(latitude)
        .longitude(longitude)
        .quantityOfChargers(quantityOfChargers)
        .power(power)
        .isOperational(isOperational)
        .price(price)
        .status(status)
        .build();

    // Then
    assertThat(station.getExternalId()).isEqualTo(externalId);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getPower()).isEqualTo(power);
    assertThat(station.getIsOperational()).isEqualTo(isOperational);
    assertThat(station.getPrice()).isEqualTo(price);
    assertThat(station.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
    assertThat(station.getStatus()).isEqualTo(status);
  }

  @Test
  void whenUsingBuilderWithPartialFields_thenCreatesStationWithOnlySetFields() {
    // Given
    String name = "Test Station";
    String address = "123 Test St";
    Double latitude = 40.7128;

    // When
    Station station = new Station.Builder()
        .name(name)
        .address(address)
        .latitude(latitude)
        .build();

    // Then
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getExternalId()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
    assertThat(station.getLongitude()).isNull();
    assertThat(station.getPower()).isNull();
    assertThat(station.getIsOperational()).isNull();
    assertThat(station.getPrice()).isNull();
    assertThat(station.getQuantityOfChargers()).isNull();
    assertThat(station.getStatus()).isNull();
  }

  @Test
  void whenUsingBuilderWithEmptyStation_thenCreatesEmptyStation() {
    // When
    Station station = new Station.Builder().build();

    // Then
    assertThat(station.getExternalId()).isNull();
    assertThat(station.getName()).isNull();
    assertThat(station.getAddress()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
    assertThat(station.getLatitude()).isNull();
    assertThat(station.getLongitude()).isNull();
    assertThat(station.getPower()).isNull();
    assertThat(station.getIsOperational()).isNull();
    assertThat(station.getPrice()).isNull();
    assertThat(station.getQuantityOfChargers()).isNull();
    assertThat(station.getStatus()).isNull();
  }
}
