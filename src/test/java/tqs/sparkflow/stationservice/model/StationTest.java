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
    Station station = new Station();

    // Then
    assertThat(station.getId()).isNull();
    assertThat(station.getExternalId()).isNull();
    assertThat(station.getName()).isNull();
    assertThat(station.getAddress()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
    assertThat(station.getLatitude()).isNull();
    assertThat(station.getLongitude()).isNull();
    assertThat(station.getPrice()).isNull();
    assertThat(station.getPower()).isNull();
    assertThat(station.getIsOperational()).isNull();
    assertThat(station.getStatus()).isNull();
    assertThat(station.getQuantityOfChargers()).isNull();
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
    Station station = new Station();
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
    station.setId(id);
    station.setExternalId(externalId);
    station.setName(name);
    station.setAddress(address);
    station.setCity(city);
    station.setCountry(country);
    station.setLatitude(latitude);
    station.setLongitude(longitude);
    station.setPrice(price);
    station.setPower(power);
    station.setIsOperational(isOperational);
    station.setStatus(status);
    station.setQuantityOfChargers(quantityOfChargers);

    // Then
    assertThat(station.getId()).isEqualTo(id);
    assertThat(station.getExternalId()).isEqualTo(externalId);
    assertThat(station.getName()).isEqualTo(name);
    assertThat(station.getAddress()).isEqualTo(address);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getPrice()).isEqualTo(price);
    assertThat(station.getPower()).isEqualTo(power);
    assertThat(station.getIsOperational()).isEqualTo(isOperational);
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
    Station station = new Station(externalId, name, address, city, country, latitude, longitude,
        quantityOfChargers, power, isOperational, price);

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
    Station station = new Station.Builder().externalId(externalId).name(name).address(address)
        .city(city).country(country).latitude(latitude).longitude(longitude)
        .quantityOfChargers(quantityOfChargers).power(power).isOperational(isOperational)
        .price(price).status(status).build();

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
    Station station = new Station.Builder().name(name).address(address).latitude(latitude).build();

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
