package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BaseStationFieldsTest {

  private Validator validator;
  private BaseStationFields station;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    station = new BaseStationFields();
  }

  @Test
  void whenCreatingEmptyStation_thenAllFieldsAreNull() {
    assertThat(station.getLatitude()).isNull();
    assertThat(station.getLongitude()).isNull();
    assertThat(station.getPrice()).isNull();
    assertThat(station.getNumberOfChargers()).isNull();
    assertThat(station.getMinPower()).isNull();
    assertThat(station.getMaxPower()).isNull();
    assertThat(station.getIsOperational()).isNull();
    assertThat(station.getStatus()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
  }

  @Test
  void whenSettingValidLatitude_thenNoValidationErrors() {
    station.setLatitude(40.7128);
    assertThat(validator.validate(station)).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(doubles = {-91.0, 91.0})
  void whenSettingInvalidLatitude_thenValidationError(double invalidLatitude) {
    station.setLatitude(invalidLatitude);
    assertThat(validator.validate(station)).isNotEmpty();
  }

  @Test
  void whenSettingValidLongitude_thenNoValidationErrors() {
    station.setLongitude(-74.0060);
    assertThat(validator.validate(station)).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(doubles = {-181.0, 181.0})
  void whenSettingInvalidLongitude_thenValidationError(double invalidLongitude) {
    station.setLongitude(invalidLongitude);
    assertThat(validator.validate(station)).isNotEmpty();
  }

  @Test
  void whenSettingValidPrice_thenNoValidationErrors() {
    station.setPrice(0.35);
    assertThat(validator.validate(station)).isEmpty();
  }

  @Test
  void whenSettingNegativePrice_thenValidationError() {
    station.setPrice(-0.35);
    assertThat(validator.validate(station)).isNotEmpty();
  }

  @Test
  void whenSettingAllFields_thenAllFieldsAreUpdated() {
    // Given
    Double latitude = 40.7128;
    Double longitude = -74.0060;
    Double price = 0.35;
    Integer numberOfChargers = 2;
    Integer minPower = 50;
    Integer maxPower = 150;
    Boolean isOperational = true;
    String status = "Available";
    String city = "New York";
    String country = "USA";

    // When
    station.setLatitude(latitude);
    station.setLongitude(longitude);
    station.setPrice(price);
    station.setNumberOfChargers(numberOfChargers);
    station.setMinPower(minPower);
    station.setMaxPower(maxPower);
    station.setIsOperational(isOperational);
    station.setStatus(status);
    station.setCity(city);
    station.setCountry(country);

    // Then
    assertThat(station.getLatitude()).isEqualTo(latitude);
    assertThat(station.getLongitude()).isEqualTo(longitude);
    assertThat(station.getPrice()).isEqualTo(price);
    assertThat(station.getNumberOfChargers()).isEqualTo(numberOfChargers);
    assertThat(station.getMinPower()).isEqualTo(minPower);
    assertThat(station.getMaxPower()).isEqualTo(maxPower);
    assertThat(station.getIsOperational()).isEqualTo(isOperational);
    assertThat(station.getStatus()).isEqualTo(status);
    assertThat(station.getCity()).isEqualTo(city);
    assertThat(station.getCountry()).isEqualTo(country);
  }

  @Test
  void whenSettingNullValues_thenFieldsAreNull() {
    // Given
    station.setLatitude(40.7128);
    station.setLongitude(-74.0060);
    station.setPrice(0.35);
    station.setNumberOfChargers(2);
    station.setMinPower(50);
    station.setMaxPower(150);
    station.setIsOperational(true);
    station.setStatus("Available");
    station.setCity("New York");
    station.setCountry("USA");

    // When
    station.setLatitude(null);
    station.setLongitude(null);
    station.setPrice(null);
    station.setNumberOfChargers(null);
    station.setMinPower(null);
    station.setMaxPower(null);
    station.setIsOperational(null);
    station.setStatus(null);
    station.setCity(null);
    station.setCountry(null);

    // Then
    assertThat(station.getLatitude()).isNull();
    assertThat(station.getLongitude()).isNull();
    assertThat(station.getPrice()).isNull();
    assertThat(station.getNumberOfChargers()).isNull();
    assertThat(station.getMinPower()).isNull();
    assertThat(station.getMaxPower()).isNull();
    assertThat(station.getIsOperational()).isNull();
    assertThat(station.getStatus()).isNull();
    assertThat(station.getCity()).isNull();
    assertThat(station.getCountry()).isNull();
  }
} 