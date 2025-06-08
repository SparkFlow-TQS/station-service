package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

  @Test
  void testEquals() {
    Station station1 = new Station();
    station1.setId(1L);
    station1.setExternalId("ext1");
    station1.setPower(50);
    station1.setQuantityOfChargers(2);
    // Set base fields to ensure super.equals() passes
    station1.setName("Test Station");
    station1.setAddress("Test Address");
    station1.setCity("Test City");
    station1.setCountry("Test Country");
    station1.setLatitude(40.0);
    station1.setLongitude(-8.0);
    station1.setStatus("Available");

    // Test same object
    assertEquals(station1, station1);

    // Test null
    assertNotEquals(station1, null);

    // Test different class
    assertNotEquals(station1, "Not a Station");

    // Test different id
    Station station2 = new Station();
    station2.setId(2L);
    station2.setExternalId("ext1");
    station2.setPower(50);
    station2.setQuantityOfChargers(2);
    // Copy base fields
    station2.setName(station1.getName());
    station2.setAddress(station1.getAddress());
    station2.setCity(station1.getCity());
    station2.setCountry(station1.getCountry());
    station2.setLatitude(station1.getLatitude());
    station2.setLongitude(station1.getLongitude());
    station2.setStatus(station1.getStatus());
    assertNotEquals(station1, station2);

    // Test different externalId
    Station station3 = new Station();
    station3.setId(1L);
    station3.setExternalId("ext2");
    station3.setPower(50);
    station3.setQuantityOfChargers(2);
    // Copy base fields
    station3.setName(station1.getName());
    station3.setAddress(station1.getAddress());
    station3.setCity(station1.getCity());
    station3.setCountry(station1.getCountry());
    station3.setLatitude(station1.getLatitude());
    station3.setLongitude(station1.getLongitude());
    station3.setStatus(station1.getStatus());
    assertFalse(station1.equals(station3));

    // Test different power
    Station station4 = new Station();
    station4.setId(1L);
    station4.setExternalId("ext1");
    station4.setPower(100);
    station4.setQuantityOfChargers(2);
    // Copy base fields
    station4.setName(station1.getName());
    station4.setAddress(station1.getAddress());
    station4.setCity(station1.getCity());
    station4.setCountry(station1.getCountry());
    station4.setLatitude(station1.getLatitude());
    station4.setLongitude(station1.getLongitude());
    station4.setStatus(station1.getStatus());
    assertNotEquals(station1, station4);

    // Test different quantityOfChargers
    Station station5 = new Station();
    station5.setId(1L);
    station5.setExternalId("ext1");
    station5.setPower(50);
    station5.setQuantityOfChargers(4);
    // Copy base fields
    station5.setName(station1.getName());
    station5.setAddress(station1.getAddress());
    station5.setCity(station1.getCity());
    station5.setCountry(station1.getCountry());
    station5.setLatitude(station1.getLatitude());
    station5.setLongitude(station1.getLongitude());
    station5.setStatus(station1.getStatus());
    assertNotEquals(station1, station5);

    // Test equal stations
    Station station6 = new Station();
    station6.setId(1L);
    station6.setExternalId("ext1");
    station6.setPower(50);
    station6.setQuantityOfChargers(2);
    // Copy base fields
    station6.setName(station1.getName());
    station6.setAddress(station1.getAddress());
    station6.setCity(station1.getCity());
    station6.setCountry(station1.getCountry());
    station6.setLatitude(station1.getLatitude());
    station6.setLongitude(station1.getLongitude());
    station6.setStatus(station1.getStatus());
    assertTrue(station1.equals(station6));

    // Test null fields
    Station station7 = new Station();
    station7.setId(1L);
    station7.setExternalId(null);
    station7.setPower(null);
    station7.setQuantityOfChargers(null);
    // Copy base fields
    station7.setName(station1.getName());
    station7.setAddress(station1.getAddress());
    station7.setCity(station1.getCity());
    station7.setCountry(station1.getCountry());
    station7.setLatitude(station1.getLatitude());
    station7.setLongitude(station1.getLongitude());
    station7.setStatus(station1.getStatus());

    Station station8 = new Station();
    station8.setId(1L);
    station8.setExternalId(null);
    station8.setPower(null);
    station8.setQuantityOfChargers(null);
    // Copy base fields
    station8.setName(station1.getName());
    station8.setAddress(station1.getAddress());
    station8.setCity(station1.getCity());
    station8.setCountry(station1.getCountry());
    station8.setLatitude(station1.getLatitude());
    station8.setLongitude(station1.getLongitude());
    station8.setStatus(station1.getStatus());

    assertEquals(station7, station8);
  }

  @Test
  void testHashCode() {
    Station station1 = new Station();
    station1.setId(1L);
    station1.setExternalId("ext1");
    station1.setPower(50);
    station1.setQuantityOfChargers(2);

    Station station2 = new Station();
    station2.setId(1L);
    station2.setExternalId("ext1");
    station2.setPower(50);
    station2.setQuantityOfChargers(2);

    // Equal objects should have equal hash codes
    assertEquals(station1.hashCode(), station2.hashCode());

    // Test with null fields
    Station station3 = new Station();
    station3.setId(1L);
    station3.setExternalId(null);
    station3.setPower(null);
    station3.setQuantityOfChargers(null);

    Station station4 = new Station();
    station4.setId(1L);
    station4.setExternalId(null);
    station4.setPower(null);
    station4.setQuantityOfChargers(null);

    assertEquals(station3.hashCode(), station4.hashCode());
  }
}
