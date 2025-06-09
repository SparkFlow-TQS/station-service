package tqs.sparkflow.stationservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment(
            1L,
            "pi_test_123",
            BigDecimal.valueOf(25.50),
            "EUR",
            "Test payment"
        );
    }

    @Test
    @DisplayName("Should create payment with constructor")
    void shouldCreatePaymentWithConstructor() {
        // Given & When
        Payment testPayment = new Payment(
            2L,
            "pi_test_456",
            BigDecimal.valueOf(50.00),
            "USD",
            "Another test payment"
        );

        // Then
        assertThat(testPayment.getBookingId()).isEqualTo(2L);
        assertThat(testPayment.getStripePaymentIntentId()).isEqualTo("pi_test_456");
        assertThat(testPayment.getAmount()).isEqualTo(BigDecimal.valueOf(50.00));
        assertThat(testPayment.getCurrency()).isEqualTo("USD");
        assertThat(testPayment.getDescription()).isEqualTo("Another test payment");
        assertThat(testPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Should set and get ID")
    void shouldSetAndGetId() {
        // When
        payment.setId(123L);

        // Then
        assertThat(payment.getId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("Should set and get booking ID")
    void shouldSetAndGetBookingId() {
        // When
        payment.setBookingId(456L);

        // Then
        assertThat(payment.getBookingId()).isEqualTo(456L);
    }

    @Test
    @DisplayName("Should set and get Stripe payment intent ID")
    void shouldSetAndGetStripePaymentIntentId() {
        // When
        payment.setStripePaymentIntentId("pi_new_789");

        // Then
        assertThat(payment.getStripePaymentIntentId()).isEqualTo("pi_new_789");
    }

    @Test
    @DisplayName("Should set and get amount")
    void shouldSetAndGetAmount() {
        // When
        payment.setAmount(BigDecimal.valueOf(100.75));

        // Then
        assertThat(payment.getAmount()).isEqualTo(BigDecimal.valueOf(100.75));
    }

    @Test
    @DisplayName("Should set and get currency")
    void shouldSetAndGetCurrency() {
        // When
        payment.setCurrency("GBP");

        // Then
        assertThat(payment.getCurrency()).isEqualTo("GBP");
    }

    @Test
    @DisplayName("Should set and get status")
    void shouldSetAndGetStatus() {
        // When
        payment.setStatus(PaymentStatus.SUCCEEDED);

        // Then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
    }

    @Test
    @DisplayName("Should set and get Stripe charge ID")
    void shouldSetAndGetStripeChargeId() {
        // When
        payment.setStripeChargeId("ch_test_123");

        // Then
        assertThat(payment.getStripeChargeId()).isEqualTo("ch_test_123");
    }

    @Test
    @DisplayName("Should set and get description")
    void shouldSetAndGetDescription() {
        // When
        payment.setDescription("Updated description");

        // Then
        assertThat(payment.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should set and get created at")
    void shouldSetAndGetCreatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        payment.setCreatedAt(now);

        // Then
        assertThat(payment.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set and get updated at")
    void shouldSetAndGetUpdatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        payment.setUpdatedAt(now);

        // Then
        assertThat(payment.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set and get paid at")
    void shouldSetAndGetPaidAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        payment.setPaidAt(now);

        // Then
        assertThat(payment.getPaidAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        Payment payment1 = new Payment(1L, "pi_test_123", BigDecimal.valueOf(25.50), "EUR", "Test");
        payment1.setId(1L);
        
        Payment payment2 = new Payment(1L, "pi_test_123", BigDecimal.valueOf(25.50), "EUR", "Test");
        payment2.setId(1L);
        
        Payment payment3 = new Payment(2L, "pi_test_456", BigDecimal.valueOf(50.00), "USD", "Different");
        payment3.setId(2L);

        // Then - equals() and hashCode() should work based on content, not timestamps
        assertThat(payment1).isEqualTo(payment2);
        assertThat(payment1).isNotEqualTo(payment3);
        assertThat(payment1.hashCode()).isEqualTo(payment2.hashCode());
    }

    @Test
    @DisplayName("Should handle toString correctly")
    void shouldHandleToStringCorrectly() {
        // Given
        payment.setId(1L);

        // When
        String toString = payment.toString();

        // Then
        assertThat(toString).contains("Payment");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("bookingId=1");
        assertThat(toString).contains("stripePaymentIntentId='pi_test_123'");
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void shouldHandleNullValuesAppropriately() {
        // Given
        Payment nullPayment = new Payment();

        // When & Then
        assertThat(nullPayment.getId()).isNull();
        assertThat(nullPayment.getBookingId()).isNull();
        assertThat(nullPayment.getStripePaymentIntentId()).isNull();
        assertThat(nullPayment.getAmount()).isNull();
        assertThat(nullPayment.getCurrency()).isNull();
        assertThat(nullPayment.getStatus()).isEqualTo(PaymentStatus.PENDING); // Default constructor sets to PENDING
        assertThat(nullPayment.getStripeChargeId()).isNull();
        assertThat(nullPayment.getDescription()).isNull();
        assertThat(nullPayment.getCreatedAt()).isNotNull(); // Default constructor sets timestamp
        assertThat(nullPayment.getUpdatedAt()).isNotNull(); // Default constructor sets timestamp
        assertThat(nullPayment.getPaidAt()).isNull();
    }

    @Test
    @DisplayName("Should create payment with default constructor")
    void shouldCreatePaymentWithDefaultConstructor() {
        // When
        Payment defaultPayment = new Payment();

        // Then
        assertThat(defaultPayment).isNotNull();
        assertThat(defaultPayment.getId()).isNull();
        assertThat(defaultPayment.getStatus()).isEqualTo(PaymentStatus.PENDING); // Default constructor sets to PENDING
        assertThat(defaultPayment.getCreatedAt()).isNotNull(); // Default constructor sets timestamp
        assertThat(defaultPayment.getUpdatedAt()).isNotNull(); // Default constructor sets timestamp
    }
}