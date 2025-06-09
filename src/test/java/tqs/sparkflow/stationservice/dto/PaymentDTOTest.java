package tqs.sparkflow.stationservice.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tqs.sparkflow.stationservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentDTOTest {

    private PaymentDTO paymentDTO;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        paymentDTO = new PaymentDTO();
    }

    @Test
    @DisplayName("Should create PaymentDTO and set all fields")
    void shouldCreatePaymentDTOAndSetAllFields() {
        // When
        paymentDTO.setId(1L);
        paymentDTO.setBookingId(2L);
        paymentDTO.setStripePaymentIntentId("pi_test_123");
        paymentDTO.setAmount(BigDecimal.valueOf(25.50));
        paymentDTO.setCurrency("EUR");
        paymentDTO.setStatus(PaymentStatus.SUCCEEDED);
        paymentDTO.setDescription("Test payment");
        paymentDTO.setCreatedAt(testTime);
        paymentDTO.setUpdatedAt(testTime);
        paymentDTO.setPaidAt(testTime);

        // Then
        assertThat(paymentDTO.getId()).isEqualTo(1L);
        assertThat(paymentDTO.getBookingId()).isEqualTo(2L);
        assertThat(paymentDTO.getStripePaymentIntentId()).isEqualTo("pi_test_123");
        assertThat(paymentDTO.getAmount()).isEqualTo(BigDecimal.valueOf(25.50));
        assertThat(paymentDTO.getCurrency()).isEqualTo("EUR");
        assertThat(paymentDTO.getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(paymentDTO.getDescription()).isEqualTo("Test payment");
        assertThat(paymentDTO.getCreatedAt()).isEqualTo(testTime);
        assertThat(paymentDTO.getUpdatedAt()).isEqualTo(testTime);
        assertThat(paymentDTO.getPaidAt()).isEqualTo(testTime);
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void shouldHandleNullValuesAppropriately() {
        // Given
        PaymentDTO nullDTO = new PaymentDTO();

        // Then
        assertThat(nullDTO.getId()).isNull();
        assertThat(nullDTO.getBookingId()).isNull();
        assertThat(nullDTO.getStripePaymentIntentId()).isNull();
        assertThat(nullDTO.getAmount()).isNull();
        assertThat(nullDTO.getCurrency()).isNull();
        assertThat(nullDTO.getStatus()).isNull();
        assertThat(nullDTO.getDescription()).isNull();
        assertThat(nullDTO.getCreatedAt()).isNull();
        assertThat(nullDTO.getUpdatedAt()).isNull();
        assertThat(nullDTO.getPaidAt()).isNull();
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        PaymentDTO dto1 = new PaymentDTO();
        dto1.setId(1L);
        dto1.setStripePaymentIntentId("pi_test_123");

        PaymentDTO dto2 = new PaymentDTO();
        dto2.setId(1L);
        dto2.setStripePaymentIntentId("pi_test_123");

        PaymentDTO dto3 = new PaymentDTO();
        dto3.setId(2L);
        dto3.setStripePaymentIntentId("pi_test_456");

        // Then
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should handle toString correctly")
    void shouldHandleToStringCorrectly() {
        // Given
        paymentDTO.setId(1L);
        paymentDTO.setStripePaymentIntentId("pi_test_123");
        paymentDTO.setAmount(BigDecimal.valueOf(25.50));

        // When
        String toString = paymentDTO.toString();

        // Then
        assertThat(toString).contains("PaymentDTO");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("stripePaymentIntentId='pi_test_123'");
    }

    @Test
    @DisplayName("Should handle different payment statuses")
    void shouldHandleDifferentPaymentStatuses() {
        // Test all payment statuses
        PaymentStatus[] statuses = PaymentStatus.values();
        
        for (PaymentStatus status : statuses) {
            paymentDTO.setStatus(status);
            assertThat(paymentDTO.getStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should handle different currencies")
    void shouldHandleDifferentCurrencies() {
        // Test common currencies
        String[] currencies = {"EUR", "USD", "GBP", "JPY"};
        
        for (String currency : currencies) {
            paymentDTO.setCurrency(currency);
            assertThat(paymentDTO.getCurrency()).isEqualTo(currency);
        }
    }

    @Test
    @DisplayName("Should handle decimal amounts correctly")
    void shouldHandleDecimalAmountsCorrectly() {
        // Test various decimal amounts
        BigDecimal[] amounts = {
            BigDecimal.valueOf(0.01),
            BigDecimal.valueOf(1.00),
            BigDecimal.valueOf(25.50),
            BigDecimal.valueOf(999.99),
            BigDecimal.valueOf(1000.00)
        };
        
        for (BigDecimal amount : amounts) {
            paymentDTO.setAmount(amount);
            assertThat(paymentDTO.getAmount()).isEqualTo(amount);
        }
    }

    @Test
    @DisplayName("Should create DTO with full constructor")
    void shouldCreateDTOWithFullConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        PaymentDTO dto = new PaymentDTO(
            1L, 2L, "pi_test_123", BigDecimal.valueOf(25.50), 
            "EUR", PaymentStatus.SUCCEEDED, "Test payment", 
            now, now, now
        );

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookingId()).isEqualTo(2L);
        assertThat(dto.getStripePaymentIntentId()).isEqualTo("pi_test_123");
        assertThat(dto.getAmount()).isEqualTo(BigDecimal.valueOf(25.50));
        assertThat(dto.getCurrency()).isEqualTo("EUR");
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(dto.getDescription()).isEqualTo("Test payment");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
        assertThat(dto.getPaidAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle edge case values")
    void shouldHandleEdgeCaseValues() {
        // Test maximum Long values for IDs
        paymentDTO.setId(Long.MAX_VALUE);
        assertThat(paymentDTO.getId()).isEqualTo(Long.MAX_VALUE);

        paymentDTO.setBookingId(Long.MIN_VALUE);
        assertThat(paymentDTO.getBookingId()).isEqualTo(Long.MIN_VALUE);

        // Test very large amounts
        BigDecimal largeAmount = new BigDecimal("999999999.99");
        paymentDTO.setAmount(largeAmount);
        assertThat(paymentDTO.getAmount()).isEqualTo(largeAmount);

        // Test zero amount
        paymentDTO.setAmount(BigDecimal.ZERO);
        assertThat(paymentDTO.getAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields() {
        // Test special characters in string fields
        String specialStripeId = "pi_123_áéíóú_$%&";
        String specialDescription = "Payment with 🎉 émoji and special chars: @#$%^&*()";
        String specialCurrency = "USD"; // Currency codes should be standard

        paymentDTO.setStripePaymentIntentId(specialStripeId);
        paymentDTO.setDescription(specialDescription);
        paymentDTO.setCurrency(specialCurrency);

        assertThat(paymentDTO.getStripePaymentIntentId()).isEqualTo(specialStripeId);
        assertThat(paymentDTO.getDescription()).isEqualTo(specialDescription);
        assertThat(paymentDTO.getCurrency()).isEqualTo(specialCurrency);
    }

    @Test
    @DisplayName("Should handle timestamp edge cases")
    void shouldHandleTimestampEdgeCases() {
        // Test past dates
        LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        paymentDTO.setCreatedAt(pastDate);
        assertThat(paymentDTO.getCreatedAt()).isEqualTo(pastDate);

        // Test future dates
        LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59, 59);
        paymentDTO.setUpdatedAt(futureDate);
        assertThat(paymentDTO.getUpdatedAt()).isEqualTo(futureDate);

        // Test current time
        LocalDateTime now = LocalDateTime.now();
        paymentDTO.setPaidAt(now);
        assertThat(paymentDTO.getPaidAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should maintain immutability of constructor parameters")
    void shouldMaintainImmutabilityOfConstructorParameters() {
        // Given
        LocalDateTime originalTime = LocalDateTime.now();
        PaymentDTO dto = new PaymentDTO(
            1L, 2L, "pi_test_123", BigDecimal.valueOf(25.50), 
            "EUR", PaymentStatus.SUCCEEDED, "Test payment", 
            originalTime, originalTime, originalTime
        );

        // When - modify original time reference
        LocalDateTime modifiedTime = originalTime.plusDays(1);

        // Then - DTO should still have original time
        assertThat(dto.getCreatedAt()).isEqualTo(originalTime);
        assertThat(dto.getCreatedAt()).isNotEqualTo(modifiedTime);
    }

    @Test
    @DisplayName("Should handle equals with different object types")
    void shouldHandleEqualsWithDifferentObjectTypes() {
        // Given
        PaymentDTO dto = new PaymentDTO();
        dto.setId(1L);

        // Then
        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo("not a PaymentDTO");
        assertThat(dto).isNotEqualTo(123);
        assertThat(dto).isEqualTo(dto); // reflexive
    }

    @Test
    @DisplayName("Should handle hashCode consistency")
    void shouldHandleHashCodeConsistency() {
        // Given
        PaymentDTO dto1 = new PaymentDTO();
        dto1.setId(1L);
        dto1.setStripePaymentIntentId("pi_test_123");

        PaymentDTO dto2 = new PaymentDTO();
        dto2.setId(1L);
        dto2.setStripePaymentIntentId("pi_test_123");

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        
        // Multiple calls should return same hash code
        int hash1 = dto1.hashCode();
        int hash2 = dto1.hashCode();
        assertThat(hash1).isEqualTo(hash2);
    }
}