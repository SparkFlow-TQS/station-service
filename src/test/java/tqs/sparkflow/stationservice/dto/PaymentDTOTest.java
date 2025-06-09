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
        assertThat(toString).contains("stripePaymentIntentId=pi_test_123");
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
}