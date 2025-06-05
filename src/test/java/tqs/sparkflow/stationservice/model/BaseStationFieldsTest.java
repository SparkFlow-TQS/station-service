package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BaseStationFieldsTest {

  private Validator validator;
  private BaseStationFields baseFields;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    // Create an anonymous subclass to test BaseStationFields
    baseFields = new BaseStationFields() {};
  }

  @Test
  void whenCreatingEmptyBaseFields_thenAllFieldsAreNull() {
    // Create a new instance without setting any values
    BaseStationFields emptyFields = new BaseStationFields() {};
    
    assertThat(emptyFields.getCity()).isNull();
    assertThat(emptyFields.getCountry()).isNull();
    assertThat(emptyFields.getLatitude()).isNull();
    assertThat(emptyFields.getLongitude()).isNull();
    assertThat(emptyFields.getPrice()).isNull();
    assertThat(emptyFields.getIsOperational()).isNull();
    assertThat(emptyFields.getStatus()).isNull();
    assertThat(emptyFields.getAddress()).isNull();
  }

  @Test
  void whenSettingValidLatitude_thenNoValidationErrors() {
    // Given
    baseFields.setLatitude(41.1579);
    baseFields.setLongitude(-8.6291); // Required field
    baseFields.setStatus("Available"); // Required field
    
    // When
    var violations = validator.validate(baseFields);
    
    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  void whenSettingInvalidLatitude_thenValidationError() {
    // Given
    baseFields.setLatitude(91.0);
    baseFields.setLongitude(-8.6291); // Required field
    baseFields.setStatus("Available"); // Required field
    
    // When
    var violations = validator.validate(baseFields);
    
    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Latitude must be between -90 and 90 degrees");
  }

  @Test
  void whenSettingValidLongitude_thenNoValidationErrors() {
    // Given
    baseFields.setLatitude(41.1579); // Required field
    baseFields.setLongitude(-8.6291);
    baseFields.setStatus("Available"); // Required field
    
    // When
    var violations = validator.validate(baseFields);
    
    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  void whenSettingInvalidLongitude_thenValidationError() {
    // Given
    baseFields.setLatitude(41.1579); // Required field
    baseFields.setLongitude(181.0);
    baseFields.setStatus("Available"); // Required field
    
    // When
    var violations = validator.validate(baseFields);
    
    // Then
    assertThat(violations).isNotEmpty();
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Longitude must be between -180 and 180 degrees");
  }

  @Test
  void whenSettingAllBaseFields_thenAllFieldsAreUpdated() {
    // Given
    String city = "New York";
    String country = "USA";
    Double latitude = 40.7128;
    Double longitude = -74.0060;
    Double price = 0.35;
    Boolean isOperational = true;
    String status = "Available";
    String address = "Test Address";

    // When
    baseFields.setCity(city);
    baseFields.setCountry(country);
    baseFields.setLatitude(latitude);
    baseFields.setLongitude(longitude);
    baseFields.setPrice(price);
    baseFields.setIsOperational(isOperational);
    baseFields.setStatus(status);
    baseFields.setAddress(address);

    // Then
    assertThat(baseFields.getCity()).isEqualTo(city);
    assertThat(baseFields.getCountry()).isEqualTo(country);
    assertThat(baseFields.getLatitude()).isEqualTo(latitude);
    assertThat(baseFields.getLongitude()).isEqualTo(longitude);
    assertThat(baseFields.getPrice()).isEqualTo(price);
    assertThat(baseFields.getIsOperational()).isEqualTo(isOperational);
    assertThat(baseFields.getStatus()).isEqualTo(status);
    assertThat(baseFields.getAddress()).isEqualTo(address);
  }
} 