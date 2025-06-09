package tqs.sparkflow.stationservice.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentIntentRequestDTOTest {

    private PaymentIntentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new PaymentIntentRequestDTO();
    }

    @Test
    @DisplayName("Should create PaymentIntentRequestDTO and set all fields")
    void shouldCreatePaymentIntentRequestDTOAndSetAllFields() {
        // When
        requestDTO.setBookingId(1L);
        requestDTO.setAmount(2550L);
        requestDTO.setCurrency("EUR");
        requestDTO.setDescription("Test payment");

        // Then
        assertThat(requestDTO.getBookingId()).isEqualTo(1L);
        assertThat(requestDTO.getAmount()).isEqualTo(2550L);
        assertThat(requestDTO.getCurrency()).isEqualTo("EUR");
        assertThat(requestDTO.getDescription()).isEqualTo("Test payment");
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void shouldHandleNullValuesAppropriately() {
        // Given
        PaymentIntentRequestDTO nullDTO = new PaymentIntentRequestDTO();

        // Then
        assertThat(nullDTO.getBookingId()).isNull();
        assertThat(nullDTO.getAmount()).isNull();
        assertThat(nullDTO.getCurrency()).isNull();
        assertThat(nullDTO.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        PaymentIntentRequestDTO dto1 = new PaymentIntentRequestDTO();
        dto1.setBookingId(1L);
        dto1.setAmount(2550L);
        dto1.setCurrency("EUR");

        PaymentIntentRequestDTO dto2 = new PaymentIntentRequestDTO();
        dto2.setBookingId(1L);
        dto2.setAmount(2550L);
        dto2.setCurrency("EUR");

        PaymentIntentRequestDTO dto3 = new PaymentIntentRequestDTO();
        dto3.setBookingId(2L);
        dto3.setAmount(5000L);
        dto3.setCurrency("USD");

        // Then
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should handle toString correctly")
    void shouldHandleToStringCorrectly() {
        // Given
        requestDTO.setBookingId(1L);
        requestDTO.setAmount(2550L);
        requestDTO.setCurrency("EUR");

        // When
        String toString = requestDTO.toString();

        // Then
        assertThat(toString).contains("PaymentIntentRequestDTO");
        assertThat(toString).contains("bookingId=1");
        assertThat(toString).contains("amount=2550");
        assertThat(toString).contains("currency='EUR'");
    }

    @Test
    @DisplayName("Should handle different currencies")
    void shouldHandleDifferentCurrencies() {
        // Test common currencies
        String[] currencies = {"EUR", "USD", "GBP", "JPY"};
        
        for (String currency : currencies) {
            requestDTO.setCurrency(currency);
            assertThat(requestDTO.getCurrency()).isEqualTo(currency);
        }
    }

    @Test
    @DisplayName("Should handle different amounts")
    void shouldHandleDifferentAmounts() {
        // Test various amounts (in cents/smallest currency unit)
        Long[] amounts = {1L, 100L, 2550L, 999999L};
        
        for (Long amount : amounts) {
            requestDTO.setAmount(amount);
            assertThat(requestDTO.getAmount()).isEqualTo(amount);
        }
    }

    @Test
    @DisplayName("Should handle different booking IDs")
    void shouldHandleDifferentBookingIds() {
        // Test various booking IDs
        Long[] bookingIds = {1L, 100L, 999L, 123456L};
        
        for (Long bookingId : bookingIds) {
            requestDTO.setBookingId(bookingId);
            assertThat(requestDTO.getBookingId()).isEqualTo(bookingId);
        }
    }

    @Test
    @DisplayName("Should handle empty and null descriptions")
    void shouldHandleEmptyAndNullDescriptions() {
        // Test null description
        requestDTO.setDescription(null);
        assertThat(requestDTO.getDescription()).isNull();

        // Test empty description
        requestDTO.setDescription("");
        assertThat(requestDTO.getDescription()).isEmpty();

        // Test whitespace description
        requestDTO.setDescription("   ");
        assertThat(requestDTO.getDescription()).isEqualTo("   ");
    }

    @Test
    @DisplayName("Should handle constructor with all parameters")
    void shouldHandleConstructorWithAllParameters() {
        // Given
        PaymentIntentRequestDTO dto = new PaymentIntentRequestDTO(1L, 2550L, "EUR", "Test payment");

        // Then
        assertThat(dto.getBookingId()).isEqualTo(1L);
        assertThat(dto.getAmount()).isEqualTo(2550L);
        assertThat(dto.getCurrency()).isEqualTo("EUR");
        assertThat(dto.getDescription()).isEqualTo("Test payment");
    }

    @Test
    @DisplayName("Should handle equals with null and different object types")
    void shouldHandleEqualsWithNullAndDifferentTypes() {
        // Given
        PaymentIntentRequestDTO dto = new PaymentIntentRequestDTO(1L, 2550L, "EUR", "Test");

        // Then
        assertThat(dto.equals(null)).isFalse();
        assertThat(dto.equals("not a DTO")).isFalse();
        assertThat(dto.equals(dto)).isTrue();
    }

    @Test
    @DisplayName("Should handle equals with different field values")
    void shouldHandleEqualsWithDifferentFieldValues() {
        // Given
        PaymentIntentRequestDTO base = new PaymentIntentRequestDTO(1L, 2550L, "EUR", "Test");
        
        PaymentIntentRequestDTO differentBooking = new PaymentIntentRequestDTO(2L, 2550L, "EUR", "Test");
        PaymentIntentRequestDTO differentAmount = new PaymentIntentRequestDTO(1L, 5000L, "EUR", "Test");
        PaymentIntentRequestDTO differentCurrency = new PaymentIntentRequestDTO(1L, 2550L, "USD", "Test");
        PaymentIntentRequestDTO differentDescription = new PaymentIntentRequestDTO(1L, 2550L, "EUR", "Different");

        // Then
        assertThat(base.equals(differentBooking)).isFalse();
        assertThat(base.equals(differentAmount)).isFalse();
        assertThat(base.equals(differentCurrency)).isFalse();
        assertThat(base.equals(differentDescription)).isFalse();
    }

    @Test
    @DisplayName("Should handle equals with null fields")
    void shouldHandleEqualsWithNullFields() {
        // Given
        PaymentIntentRequestDTO dto1 = new PaymentIntentRequestDTO();
        PaymentIntentRequestDTO dto2 = new PaymentIntentRequestDTO();
        
        dto1.setBookingId(null);
        dto1.setAmount(null);
        dto1.setCurrency(null);
        dto1.setDescription(null);
        
        dto2.setBookingId(null);
        dto2.setAmount(null);
        dto2.setCurrency(null);
        dto2.setDescription(null);

        // Then
        assertThat(dto1.equals(dto2)).isTrue();
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}