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
    assertThat(station.getConnectorType()).isNull();
  }

  @Test
  void whenCreatingStationWithAllFields_thenAllFieldsAreSet() {
    // Given
    Long id = 1L;
    String name = "Test Station";
    String address = "Test Address";
    double latitude = 38.7223;
    double longitude = -9.1393;
    String status = "Available";
    String connectorType = "Type2";

    // When
    Station station =
        new Station(name, address, "Test City", latitude, longitude, connectorType, status);
    station.setId(id);

    // Then
    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getStatus()).isEqualTo(status);
    assertThat(station.getConnectorType()).isEqualTo(connectorType);
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
    String connectorType = "Type2";

    // When
    station.setId(id);
    station.setName(name);
    station.setAddress(address);
    station.setLatitude(latitude);
    station.setLongitude(longitude);
    station.setStatus(status);
    station.setConnectorType(connectorType);

    // Then
    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getStatus()).isEqualTo(status);
    assertThat(station.getConnectorType()).isEqualTo(connectorType);
  }

  @Test
  void whenCallingToString_thenReturnsCorrectString() {
    // Given
    Station station =
        new Station(
            "Test Station", "Test Address", "Test City", 38.7223, -9.1393, "Type2", "Available");
    station.setId(1L);

    // When
    String toString = station.toString();

    // Then
    assertThat(toString).isEqualTo("Station{id=1, name='Test Station'}");
  }

  @Test
  void testStationCreation() {
    Long id = 1L;
    String name = "Test Station";
    String address = "Test Address";
    double latitude = 38.7223;
    double longitude = -9.1393;
    String status = "Available";
    String connectorType = "Type 2";

    Station station =
        new Station(name, address, "Test City", latitude, longitude, connectorType, status);
    station.setId(id);

    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getStatus()).isEqualTo(status);
    assertThat(station.getConnectorType()).isEqualTo(connectorType);
  }

  @Test
  void testStationSetters() {
    Station station = new Station();
    Long id = 1L;
    String name = "Test Station";
    String address = "Test Address";
    double latitude = 38.7223;
    double longitude = -9.1393;
    String status = "Available";
    String connectorType = "Type 2";

    station.setId(id);
    station.setName(name);
    station.setAddress(address);
    station.setLatitude(latitude);
    station.setLongitude(longitude);
    station.setStatus(status);
    station.setConnectorType(connectorType);

    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getStatus()).isEqualTo(status);
    assertThat(station.getConnectorType()).isEqualTo(connectorType);
  }

  @Test
  void whenCreatingStation_thenAllFieldsAreSet() {
    // Given
    Long id = 1L;
    String name = "Test Station";
    String address = "Test Address";
    String city = "Test City";
    String country = "Test Country";
    double latitude = 38.7223;
    double longitude = -9.1393;
    String connectorType = "Type 2";
    String status = "Available";

    // When
    Station station = new Station(name, address, city, latitude, longitude, connectorType, status);
    station.setId(id);
    station.setCountry(country);

    // Then
    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getConnectorType()).isEqualTo(connectorType);
    assertThat(station.getStatus()).isEqualTo(status);
  }

  @Test
  void whenCreatingStationWithExternalId_thenAllFieldsAreSet() {
    // Given
    String externalId = "EXT123";
    String name = "Test Station";
    String address = "Test Address";
    String city = "Test City";
    String country = "Test Country";
    Double latitude = 38.7223;
    Double longitude = -9.1393;
    String connectorType = "Type 2";
    Integer power = 50;
    Boolean isOperational = true;

    // When
    Station station =
        new Station(
            externalId,
            name,
            address,
            city,
            country,
            latitude,
            longitude,
            connectorType,
            power,
            isOperational);

    // Then
    assertThat(station.getExternalId()).isEqualTo(externalId);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getConnectorType()).isEqualTo(connectorType);
    assertThat(station.getPower()).isEqualTo(power);
    assertThat(station.getIsOperational()).isEqualTo(isOperational);
  }
}
