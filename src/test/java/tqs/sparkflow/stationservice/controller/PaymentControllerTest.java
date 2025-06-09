package tqs.sparkflow.stationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import tqs.sparkflow.stationservice.dto.PaymentDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentRequestDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentResponseDTO;
import tqs.sparkflow.stationservice.model.PaymentStatus;
import tqs.sparkflow.stationservice.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@ContextConfiguration(classes = {PaymentController.class, PaymentControllerTest.TestSecurityConfig.class})
@ActiveProfiles("test")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentIntentRequestDTO testRequest;
    private PaymentIntentResponseDTO testResponse;
    private PaymentDTO testPayment;

    @BeforeEach
    void setUp() {
        testRequest = new PaymentIntentRequestDTO();
        testRequest.setBookingId(1L);
        testRequest.setAmount(2550L);
        testRequest.setCurrency("EUR");
        testRequest.setDescription("Test payment");

        testResponse = new PaymentIntentResponseDTO(
            "pi_test_123",
            "pi_test_123_secret",
            2550L,
            "EUR",
            "requires_payment_method",
            "Test payment"
        );

        testPayment = new PaymentDTO();
        testPayment.setId(1L);
        testPayment.setBookingId(1L);
        testPayment.setStripePaymentIntentId("pi_test_123");
        testPayment.setAmount(BigDecimal.valueOf(25.50));
        testPayment.setCurrency("EUR");
        testPayment.setStatus(PaymentStatus.SUCCEEDED);
        testPayment.setDescription("Test payment");
        testPayment.setCreatedAt(LocalDateTime.now());
        testPayment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create payment intent successfully")
    void shouldCreatePaymentIntentSuccessfully() throws Exception {
        // Given
        when(paymentService.createPaymentIntent(any(PaymentIntentRequestDTO.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/create-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("pi_test_123"))
            .andExpect(jsonPath("$.clientSecret").value("pi_test_123_secret"))
            .andExpect(jsonPath("$.amount").value(2550L))
            .andExpect(jsonPath("$.currency").value("EUR"))
            .andExpect(jsonPath("$.status").value("requires_payment_method"));
    }

    @Test
    @DisplayName("Should return bad request for invalid payment intent data")
    void shouldReturnBadRequestForInvalidPaymentIntentData() throws Exception {
        // Given
        when(paymentService.createPaymentIntent(any(PaymentIntentRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Invalid booking ID"));

        // When & Then
        mockMvc.perform(post("/api/payments/create-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return conflict when payment already exists")
    void shouldReturnConflictWhenPaymentAlreadyExists() throws Exception {
        // Given
        when(paymentService.createPaymentIntent(any(PaymentIntentRequestDTO.class)))
            .thenThrow(new IllegalStateException("Payment already exists"));

        // When & Then
        mockMvc.perform(post("/api/payments/create-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should confirm payment successfully")
    void shouldConfirmPaymentSuccessfully() throws Exception {
        // Given
        when(paymentService.confirmPayment("pi_test_123")).thenReturn(testPayment);

        // When & Then
        mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentIntentId\":\"pi_test_123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.payment.id").value(1L))
            .andExpect(jsonPath("$.payment.stripePaymentIntentId").value("pi_test_123"));
    }

    @Test
    @DisplayName("Should return bad request for missing payment intent ID")
    void shouldReturnBadRequestForMissingPaymentIntentId() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Payment intent ID is required"));
    }

    @Test
    @DisplayName("Should return not found when payment not found during confirmation")
    void shouldReturnNotFoundWhenPaymentNotFoundDuringConfirmation() throws Exception {
        // Given
        when(paymentService.confirmPayment("pi_test_123"))
            .thenThrow(new IllegalArgumentException("Payment not found"));

        // When & Then
        mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentIntentId\":\"pi_test_123\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get payment by ID successfully")
    void shouldGetPaymentByIdSuccessfully() throws Exception {
        // Given
        when(paymentService.getPaymentById(1L)).thenReturn(testPayment);

        // When & Then
        mockMvc.perform(get("/api/payments/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.stripePaymentIntentId").value("pi_test_123"));
    }

    @Test
    @DisplayName("Should return not found when payment not found by ID")
    void shouldReturnNotFoundWhenPaymentNotFoundById() throws Exception {
        // Given
        when(paymentService.getPaymentById(1L))
            .thenThrow(new IllegalArgumentException("Payment not found"));

        // When & Then
        mockMvc.perform(get("/api/payments/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get payments by booking ID successfully")
    void shouldGetPaymentsByBookingIdSuccessfully() throws Exception {
        // Given
        when(paymentService.getPaymentsByBookingId(1L)).thenReturn(List.of(testPayment));

        // When & Then
        mockMvc.perform(get("/api/payments/booking/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].bookingId").value(1L));
    }

    @Test
    @DisplayName("Should get payment history successfully")
    void shouldGetPaymentHistorySuccessfully() throws Exception {
        // Given
        when(paymentService.getPaymentHistory()).thenReturn(List.of(testPayment));

        // When & Then
        mockMvc.perform(get("/api/payments/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Should check payment status successfully")
    void shouldCheckPaymentStatusSuccessfully() throws Exception {
        // Given
        when(paymentService.hasSuccessfulPayment(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/payments/status/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasSuccessfulPayment").value(true));
    }

    @Test
    @DisplayName("Should handle webhook successfully")
    void shouldHandleWebhookSuccessfully() throws Exception {
        // Given
        String payload = "{\"type\":\"payment_intent.succeeded\"}";
        String signature = "test_signature";

        // When & Then
        mockMvc.perform(post("/api/payments/webhook")
                .header("Stripe-Signature", signature)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(content().string("Webhook processed successfully"));
    }

    @Test
    @DisplayName("Should handle webhook with invalid signature")
    void shouldHandleWebhookWithInvalidSignature() throws Exception {
        // Given
        String payload = "{\"type\":\"payment_intent.succeeded\"}";
        String signature = "invalid_signature";

        doThrow(new RuntimeException("Invalid signature"))
                .when(paymentService).handleWebhookEvent(eq(payload), eq(signature));

        // When & Then
        mockMvc.perform(post("/api/payments/webhook")
                .header("Stripe-Signature", signature)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Invalid signature"));
    }

    @Test
    @DisplayName("Should handle general exception during payment intent creation")
    void shouldHandleGeneralExceptionDuringPaymentIntentCreation() throws Exception {
        // Given
        when(paymentService.createPaymentIntent(any(PaymentIntentRequestDTO.class)))
            .thenThrow(new RuntimeException("General error"));

        // When & Then
        mockMvc.perform(post("/api/payments/create-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle general exception during payment confirmation")
    void shouldHandleGeneralExceptionDuringPaymentConfirmation() throws Exception {
        // Given
        when(paymentService.confirmPayment("pi_test_123"))
            .thenThrow(new RuntimeException("General error"));

        // When & Then
        mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentIntentId\":\"pi_test_123\"}"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("Failed to confirm payment"));
    }

    @Test
    @DisplayName("Should handle webhook with general exception")
    void shouldHandleWebhookWithGeneralException() throws Exception {
        // Given
        String payload = "{\"type\":\"payment_intent.succeeded\"}";
        String signature = "test_signature";

        doThrow(new RuntimeException("General webhook error"))
                .when(paymentService).handleWebhookEvent(eq(payload), eq(signature));

        // When & Then
        mockMvc.perform(post("/api/payments/webhook")
                .header("Stripe-Signature", signature)
                .content(payload))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("Error processing webhook"));
    }

    @Test
    @DisplayName("Should handle payment confirmation with empty payment intent ID")
    void shouldHandlePaymentConfirmationWithEmptyPaymentIntentId() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentIntentId\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Payment intent ID is required"));
    }

    @Test
    @DisplayName("Should handle payment confirmation with whitespace-only payment intent ID")
    void shouldHandlePaymentConfirmationWithWhitespaceOnlyPaymentIntentId() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentIntentId\":\"   \"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Payment intent ID is required"));
    }

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/payments/**").permitAll()
                    .anyRequest().authenticated());
            return http.build();
        }
    }
}