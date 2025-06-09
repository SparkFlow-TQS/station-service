package tqs.sparkflow.stationservice.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentIntentResponseDTOTest {

    private PaymentIntentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new PaymentIntentResponseDTO(
            "pi_test_123",
            "pi_test_123_secret",
            2550L,
            "EUR",
            "requires_payment_method",
            "Test payment"
        );
    }

    @Test
    @DisplayName("Should create PaymentIntentResponseDTO with constructor")
    void shouldCreatePaymentIntentResponseDTOWithConstructor() {
        // Given & When
        PaymentIntentResponseDTO dto = new PaymentIntentResponseDTO(
            "pi_test_456",
            "pi_test_456_secret",
            5000L,
            "USD",
            "succeeded",
            "Another test payment"
        );

        // Then
        assertThat(dto.getId()).isEqualTo("pi_test_456");
        assertThat(dto.getClientSecret()).isEqualTo("pi_test_456_secret");
        assertThat(dto.getAmount()).isEqualTo(5000L);
        assertThat(dto.getCurrency()).isEqualTo("USD");
        assertThat(dto.getStatus()).isEqualTo("succeeded");
        assertThat(dto.getDescription()).isEqualTo("Another test payment");
    }

    @Test
    @DisplayName("Should get all fields correctly")
    void shouldGetAllFieldsCorrectly() {
        // Then
        assertThat(responseDTO.getId()).isEqualTo("pi_test_123");
        assertThat(responseDTO.getClientSecret()).isEqualTo("pi_test_123_secret");
        assertThat(responseDTO.getAmount()).isEqualTo(2550L);
        assertThat(responseDTO.getCurrency()).isEqualTo("EUR");
        assertThat(responseDTO.getStatus()).isEqualTo("requires_payment_method");
        assertThat(responseDTO.getDescription()).isEqualTo("Test payment");
    }

    @Test
    @DisplayName("Should set all fields correctly")
    void shouldSetAllFieldsCorrectly() {
        // Given
        PaymentIntentResponseDTO dto = new PaymentIntentResponseDTO(
            "", "", 0L, "", "", ""
        );

        // When
        dto.setId("pi_new_789");
        dto.setClientSecret("pi_new_789_secret");
        dto.setAmount(7500L);
        dto.setCurrency("GBP");
        dto.setStatus("processing");
        dto.setDescription("New payment");

        // Then
        assertThat(dto.getId()).isEqualTo("pi_new_789");
        assertThat(dto.getClientSecret()).isEqualTo("pi_new_789_secret");
        assertThat(dto.getAmount()).isEqualTo(7500L);
        assertThat(dto.getCurrency()).isEqualTo("GBP");
        assertThat(dto.getStatus()).isEqualTo("processing");
        assertThat(dto.getDescription()).isEqualTo("New payment");
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void shouldHandleNullValuesAppropriately() {
        // Given
        PaymentIntentResponseDTO nullDTO = new PaymentIntentResponseDTO(
            null, null, null, null, null, null
        );

        // Then
        assertThat(nullDTO.getId()).isNull();
        assertThat(nullDTO.getClientSecret()).isNull();
        assertThat(nullDTO.getAmount()).isNull();
        assertThat(nullDTO.getCurrency()).isNull();
        assertThat(nullDTO.getStatus()).isNull();
        assertThat(nullDTO.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        PaymentIntentResponseDTO dto1 = new PaymentIntentResponseDTO(
            "pi_test_123", "secret", 2550L, "EUR", "status", "desc"
        );
        PaymentIntentResponseDTO dto2 = new PaymentIntentResponseDTO(
            "pi_test_123", "secret", 2550L, "EUR", "status", "desc"
        );
        PaymentIntentResponseDTO dto3 = new PaymentIntentResponseDTO(
            "pi_test_456", "secret2", 5000L, "USD", "status2", "desc2"
        );

        // Then
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should handle toString correctly")
    void shouldHandleToStringCorrectly() {
        // When
        String toString = responseDTO.toString();

        // Then
        assertThat(toString).contains("PaymentIntentResponseDTO");
        assertThat(toString).contains("id='pi_test_123'");
        assertThat(toString).contains("amount=2550");
        assertThat(toString).contains("currency='EUR'");
        assertThat(toString).contains("status='requires_payment_method'");
    }

    @Test
    @DisplayName("Should handle different payment intent statuses")
    void shouldHandleDifferentPaymentIntentStatuses() {
        // Test common Stripe payment intent statuses
        String[] statuses = {
            "requires_payment_method",
            "requires_confirmation",
            "requires_action",
            "processing",
            "requires_capture",
            "canceled",
            "succeeded"
        };
        
        for (String status : statuses) {
            responseDTO.setStatus(status);
            assertThat(responseDTO.getStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should handle different currencies")
    void shouldHandleDifferentCurrencies() {
        // Test common currencies
        String[] currencies = {"EUR", "USD", "GBP", "JPY", "CAD", "AUD"};
        
        for (String currency : currencies) {
            responseDTO.setCurrency(currency);
            assertThat(responseDTO.getCurrency()).isEqualTo(currency);
        }
    }

    @Test
    @DisplayName("Should handle different amounts")
    void shouldHandleDifferentAmounts() {
        // Test various amounts (in cents/smallest currency unit)
        Long[] amounts = {1L, 50L, 100L, 2550L, 999999L};
        
        for (Long amount : amounts) {
            responseDTO.setAmount(amount);
            assertThat(responseDTO.getAmount()).isEqualTo(amount);
        }
    }

    @Test
    @DisplayName("Should handle empty and special strings")
    void shouldHandleEmptyAndSpecialStrings() {
        // Test empty strings
        responseDTO.setId("");
        assertThat(responseDTO.getId()).isEmpty();

        responseDTO.setClientSecret("");
        assertThat(responseDTO.getClientSecret()).isEmpty();

        // Test strings with special characters
        responseDTO.setDescription("Special chars: àáâãäåæçèéêë");
        assertThat(responseDTO.getDescription()).isEqualTo("Special chars: àáâãäåæçèéêë");

        // Test long strings
        String longDescription = "A".repeat(1000);
        responseDTO.setDescription(longDescription);
        assertThat(responseDTO.getDescription()).isEqualTo(longDescription);
    }

    @Test
    @DisplayName("Should create DTO with default constructor")
    void shouldCreateDTOWithDefaultConstructor() {
        // When
        PaymentIntentResponseDTO defaultDTO = new PaymentIntentResponseDTO();

        // Then
        assertThat(defaultDTO).isNotNull();
        assertThat(defaultDTO.getId()).isNull();
        assertThat(defaultDTO.getClientSecret()).isNull();
        assertThat(defaultDTO.getAmount()).isNull();
        assertThat(defaultDTO.getCurrency()).isNull();
        assertThat(defaultDTO.getStatus()).isNull();
        assertThat(defaultDTO.getDescription()).isNull();
    }
}