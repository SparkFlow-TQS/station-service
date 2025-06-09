package tqs.sparkflow.stationservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tqs.sparkflow.stationservice.dto.PaymentDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentRequestDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentResponseDTO;
import tqs.sparkflow.stationservice.model.Payment;
import tqs.sparkflow.stationservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceInterfaceTest {

    @Test
    @DisplayName("PaymentService interface should define all expected methods")
    void paymentServiceInterfaceShouldDefineAllExpectedMethods() throws NoSuchMethodException {
        // Verify all expected methods exist in the interface
        Class<?> serviceInterface = PaymentService.class;

        // Check core payment intent methods
        assertThat(serviceInterface.getMethod("createPaymentIntent", PaymentIntentRequestDTO.class))
            .isNotNull();
        assertThat(serviceInterface.getMethod("confirmPayment", String.class))
            .isNotNull();

        // Check webhook handling
        assertThat(serviceInterface.getMethod("handleWebhookEvent", String.class, String.class))
            .isNotNull();

        // Check CRUD operations
        assertThat(serviceInterface.getMethod("getPaymentById", Long.class))
            .isNotNull();
        assertThat(serviceInterface.getMethod("getPaymentByStripeId", String.class))
            .isNotNull();
        assertThat(serviceInterface.getMethod("getPaymentsByBookingId", Long.class))
            .isNotNull();
        assertThat(serviceInterface.getMethod("getPaymentHistory"))
            .isNotNull();

        // Check utility methods
        assertThat(serviceInterface.getMethod("hasSuccessfulPayment", Long.class))
            .isNotNull();
        assertThat(serviceInterface.getMethod("convertToDTO", Payment.class))
            .isNotNull();
        assertThat(serviceInterface.getMethod("updatePaymentFromStripeEvent", com.stripe.model.PaymentIntent.class))
            .isNotNull();
    }

    @Test
    @DisplayName("PaymentService interface should have correct return types")
    void paymentServiceInterfaceShouldHaveCorrectReturnTypes() throws NoSuchMethodException {
        Class<?> serviceInterface = PaymentService.class;

        // Check return types
        assertThat(serviceInterface.getMethod("createPaymentIntent", PaymentIntentRequestDTO.class).getReturnType())
            .isEqualTo(PaymentIntentResponseDTO.class);
        
        assertThat(serviceInterface.getMethod("confirmPayment", String.class).getReturnType())
            .isEqualTo(PaymentDTO.class);
        
        assertThat(serviceInterface.getMethod("getPaymentById", Long.class).getReturnType())
            .isEqualTo(PaymentDTO.class);
        
        assertThat(serviceInterface.getMethod("getPaymentByStripeId", String.class).getReturnType())
            .isEqualTo(PaymentDTO.class);
        
        assertThat(serviceInterface.getMethod("getPaymentsByBookingId", Long.class).getReturnType())
            .isEqualTo(List.class);
        
        assertThat(serviceInterface.getMethod("getPaymentHistory").getReturnType())
            .isEqualTo(List.class);
        
        assertThat(serviceInterface.getMethod("hasSuccessfulPayment", Long.class).getReturnType())
            .isEqualTo(boolean.class);
        
        assertThat(serviceInterface.getMethod("convertToDTO", Payment.class).getReturnType())
            .isEqualTo(PaymentDTO.class);
        
        assertThat(serviceInterface.getMethod("updatePaymentFromStripeEvent", com.stripe.model.PaymentIntent.class).getReturnType())
            .isEqualTo(Payment.class);
    }

    @Test
    @DisplayName("PaymentDTO should be properly structured for data transfer")
    void paymentDTOShouldBeProperlyStructuredForDataTransfer() {
        // Test that PaymentDTO can be created and used for data transfer
        PaymentDTO dto = new PaymentDTO();
        
        // Set all fields
        dto.setId(1L);
        dto.setBookingId(2L);
        dto.setStripePaymentIntentId("pi_test_123");
        dto.setAmount(BigDecimal.valueOf(25.50));
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.SUCCEEDED);
        dto.setDescription("Test payment");
        
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setPaidAt(now);

        // Verify all fields are accessible
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
    @DisplayName("PaymentIntentRequestDTO should handle validation constraints")
    void paymentIntentRequestDTOShouldHandleValidationConstraints() {
        // Test that PaymentIntentRequestDTO has proper structure for validation
        PaymentIntentRequestDTO request = new PaymentIntentRequestDTO();
        
        // Set required fields
        request.setBookingId(1L);
        request.setAmount(2550L); // 25.50 EUR in cents
        request.setCurrency("EUR");
        request.setDescription("Test payment");

        // Verify fields are set correctly
        assertThat(request.getBookingId()).isEqualTo(1L);
        assertThat(request.getAmount()).isEqualTo(2550L);
        assertThat(request.getCurrency()).isEqualTo("EUR");
        assertThat(request.getDescription()).isEqualTo("Test payment");
    }

    @Test
    @DisplayName("PaymentIntentResponseDTO should contain all necessary response data")
    void paymentIntentResponseDTOShouldContainAllNecessaryResponseData() {
        // Test that PaymentIntentResponseDTO has proper structure for API responses
        PaymentIntentResponseDTO response = new PaymentIntentResponseDTO();
        
        // Set response fields
        response.setId("pi_test_123");
        response.setClientSecret("pi_test_123_secret_xyz");
        response.setAmount(2550L);
        response.setCurrency("EUR");
        response.setStatus("requires_payment_method");
        response.setDescription("Test payment");

        // Verify fields are set correctly
        assertThat(response.getId()).isEqualTo("pi_test_123");
        assertThat(response.getClientSecret()).isEqualTo("pi_test_123_secret_xyz");
        assertThat(response.getAmount()).isEqualTo(2550L);
        assertThat(response.getCurrency()).isEqualTo("EUR");
        assertThat(response.getStatus()).isEqualTo("requires_payment_method");
        assertThat(response.getDescription()).isEqualTo("Test payment");
    }

    @Test
    @DisplayName("Payment model should support all required payment statuses")
    void paymentModelShouldSupportAllRequiredPaymentStatuses() {
        // Test that Payment model works with all PaymentStatus values
        Payment payment = new Payment();
        
        // Test each status
        for (PaymentStatus status : PaymentStatus.values()) {
            payment.setStatus(status);
            assertThat(payment.getStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Service interface should be properly typed")
    void serviceInterfaceShouldBeProperlyTyped() {
        // Verify the interface is actually an interface
        assertThat(PaymentService.class.isInterface()).isTrue();
        
        // Verify it's in the correct package
        assertThat(PaymentService.class.getPackage().getName())
            .isEqualTo("tqs.sparkflow.stationservice.service");
        
        // Verify it has methods (not empty)
        assertThat(PaymentService.class.getDeclaredMethods()).isNotEmpty();
    }
}