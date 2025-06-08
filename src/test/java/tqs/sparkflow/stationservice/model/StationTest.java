package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StationTest {

  private Validator validator;
  private Station station;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    station = new Station();
    // Set all required fields with valid values
    station.setName("Test Station");
    station.setLatitude(41.1579);
    station.setLongitude(-8.6291);
    station.setStatus("Available");
    station.setQuantityOfChargers(1);
  }

  @Test
  void whenCreatingEmptyStation_thenAllFieldsAreNull() {
    // When
    Station testStation = new Station();

    // Then
    assertThat(testStation.getId()).isNull();
    assertThat(testStation.getExternalId()).isNull();
    assertThat(testStation.getName()).isNull();
    assertThat(testStation.getAddress()).isNull();
    assertThat(testStation.getCity()).isNull();
    assertThat(testStation.getCountry()).isNull();
    assertThat(testStation.getLatitude()).isNull();
    assertThat(testStation.getLongitude()).isNull();
    assertThat(testStation.getPrice()).isNull();
    assertThat(testStation.getPower()).isNull();
    assertThat(testStation.getIsOperational()).isNull();
    assertThat(testStation.getStatus()).isNull();
    assertThat(testStation.getQuantityOfChargers()).isNull();
  }

  @Test
  void whenNameIsEmpty_thenValidationError() {
    // Given
    station.setName("");
    station.setLatitude(41.1579); // Keep other required fields valid
    station.setLongitude(-8.6291);
    station.setStatus("Available");
    station.setQuantityOfChargers(1);

    // When
    var violations = validator.validate(station);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream().filter(v -> v.getPropertyPath().toString().equals("name"))
        .findFirst().get().getMessage()).isEqualTo("Station name cannot be empty");
  }

  @Test
  void whenStatusIsEmpty_thenValidationError() {
    // Given
    station.setStatus("");
    station.setName("Test Station"); // Keep other required fields valid
    station.setLatitude(41.1579);
    station.setLongitude(-8.6291);
    station.setQuantityOfChargers(1);

    // When
    var violations = validator.validate(station);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream().filter(v -> v.getPropertyPath().toString().equals("status"))
        .findFirst().get().getMessage()).isEqualTo("Status cannot be empty");
  }

  @Test
  void whenQuantityOfChargersIsLessThanOne_thenValidationError() {
    // Given
    station.setQuantityOfChargers(0);
    station.setName("Test Station"); // Keep other required fields valid
    station.setLatitude(41.1579);
    station.setLongitude(-8.6291);
    station.setStatus("Available");

    // When
    var violations = validator.validate(station);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(
        violations.stream().filter(v -> v.getPropertyPath().toString().equals("quantityOfChargers"))
            .findFirst().get().getMessage()).isEqualTo("Quantity of chargers must be at least 1");
  }

  @Test
  void whenLatitudeIsNull_thenValidationError() {
    // Given
    station.setLatitude(null);
    station.setLongitude(-8.6291); // Keep other required fields valid

    // When
    var violations = validator.validate(station);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream().filter(v -> v.getPropertyPath().toString().equals("latitude"))
        .findFirst().get().getMessage()).isEqualTo("Latitude cannot be null");
  }

  @Test
  void whenLongitudeIsNull_thenValidationError() {
    // Given
    station.setLongitude(null);
    station.setLatitude(41.1579); // Keep other required fields valid

    // When
    var violations = validator.validate(station);

    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.stream().filter(v -> v.getPropertyPath().toString().equals("longitude"))
        .findFirst().get().getMessage()).isEqualTo("Longitude cannot be null");
  }

  @Test
  void whenSettingStationFields_thenFieldsAreUpdated() {
    // Given
    Station testStation = new Station();
    Long id = 1L;
    String externalId = "ext123";
    String name = "Test Station";
    String address = "Test Address";
    String city = "Test City";
    String country = "Test Country";
    double latitude = 38.7223;
    double longitude = -9.1393;
    Double price = 0.35;
    Integer power = 50;
    Boolean isOperational = true;
    String status = "Available";
    int quantityOfChargers = 1;

    // When
    testStation.setId(id);
    testStation.setExternalId(externalId);
    testStation.setName(name);
    testStation.setAddress(address);
    testStation.setCity(city);
    testStation.setCountry(country);
    testStation.setLatitude(latitude);
    testStation.setLongitude(longitude);
    testStation.setPrice(price);
    testStation.setPower(power);
    testStation.setIsOperational(isOperational);
    testStation.setStatus(status);
    testStation.setQuantityOfChargers(quantityOfChargers);

    // Then
    assertThat(testStation.getId()).isEqualTo(id);
    assertThat(testStation.getExternalId()).isEqualTo(externalId);
    assertThat(testStation.getName()).isEqualTo(name);
    assertThat(testStation.getAddress()).isEqualTo(address);
    assertThat(testStation.getCity()).isEqualTo(city);
    assertThat(testStation.getCountry()).isEqualTo(country);
    assertThat(testStation.getLatitude()).isEqualTo(latitude);
    assertThat(testStation.getLongitude()).isEqualTo(longitude);
    assertThat(testStation.getPrice()).isEqualTo(price);
    assertThat(testStation.getPower()).isEqualTo(power);
    assertThat(testStation.getIsOperational()).isEqualTo(isOperational);
    assertThat(testStation.getStatus()).isEqualTo(status);
    assertThat(testStation.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
  }

  @Test
  void whenCallingToString_thenReturnsCorrectString() {
    // Given
    Station testStation = new Station();
    testStation.setId(1L);
    testStation.setName("Test Station");

    // When
    String toString = testStation.toString();

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
    Station testStation = new Station(externalId, name, address, city, country, latitude, longitude,
        quantityOfChargers, power, isOperational, price);

    // Then
    assertThat(testStation.getExternalId()).isEqualTo(externalId);
    assertThat(testStation.getName()).isEqualTo(name);
    assertThat(testStation.getAddress()).isEqualTo(address);
    assertThat(testStation.getCity()).isEqualTo(city);
    assertThat(testStation.getCountry()).isEqualTo(country);
    assertThat(testStation.getLatitude()).isEqualTo(latitude);
    assertThat(testStation.getLongitude()).isEqualTo(longitude);
    assertThat(testStation.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
    assertThat(testStation.getPower()).isEqualTo(power);
    assertThat(testStation.getIsOperational()).isEqualTo(isOperational);
    assertThat(testStation.getPrice()).isEqualTo(price);
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
    Station testStation = new Station.Builder().externalId(externalId).name(name).address(address)
        .city(city).country(country).latitude(latitude).longitude(longitude)
        .quantityOfChargers(quantityOfChargers).power(power).isOperational(isOperational)
        .price(price).status(status).build();

    // Then
    assertThat(testStation.getExternalId()).isEqualTo(externalId);
    assertThat(testStation.getName()).isEqualTo(name);
    assertThat(testStation.getAddress()).isEqualTo(address);
    assertThat(testStation.getCity()).isEqualTo(city);
    assertThat(testStation.getCountry()).isEqualTo(country);
    assertThat(testStation.getLatitude()).isEqualTo(latitude);
    assertThat(testStation.getLongitude()).isEqualTo(longitude);
    assertThat(testStation.getPower()).isEqualTo(power);
    assertThat(testStation.getIsOperational()).isEqualTo(isOperational);
    assertThat(testStation.getPrice()).isEqualTo(price);
    assertThat(testStation.getQuantityOfChargers()).isEqualTo(quantityOfChargers);
    assertThat(testStation.getStatus()).isEqualTo(status);
  }

  @Test
  void whenUsingBuilderWithPartialFields_thenCreatesStationWithOnlySetFields() {
    // Given
    String name = "Test Station";
    String address = "123 Test St";
    Double latitude = 40.7128;

    // When
    Station testStation =
        new Station.Builder().name(name).address(address).latitude(latitude).build();

    // Then
    assertThat(testStation.getName()).isEqualTo(name);
    assertThat(testStation.getAddress()).isEqualTo(address);
    assertThat(testStation.getLatitude()).isEqualTo(latitude);
    assertThat(testStation.getExternalId()).isNull();
    assertThat(testStation.getCity()).isNull();
    assertThat(testStation.getCountry()).isNull();
    assertThat(testStation.getLongitude()).isNull();
    assertThat(testStation.getPower()).isNull();
    assertThat(testStation.getIsOperational()).isNull();
    assertThat(testStation.getPrice()).isNull();
    assertThat(testStation.getQuantityOfChargers()).isNull();
    assertThat(testStation.getStatus()).isNull();
  }

  @Test
  void whenUsingBuilderWithEmptyStation_thenCreatesEmptyStation() {
    // When
    Station testStation = new Station.Builder().build();

    // Then
    assertThat(testStation.getExternalId()).isNull();
    assertThat(testStation.getName()).isNull();
    assertThat(testStation.getAddress()).isNull();
    assertThat(testStation.getCity()).isNull();
    assertThat(testStation.getCountry()).isNull();
    assertThat(testStation.getLatitude()).isNull();
    assertThat(testStation.getLongitude()).isNull();
    assertThat(testStation.getPower()).isNull();
    assertThat(testStation.getIsOperational()).isNull();
    assertThat(testStation.getPrice()).isNull();
    assertThat(testStation.getQuantityOfChargers()).isNull();
    assertThat(testStation.getStatus()).isNull();
  }
}
