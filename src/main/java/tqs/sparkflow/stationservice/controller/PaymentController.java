package tqs.sparkflow.stationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.stationservice.dto.PaymentDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentRequestDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentResponseDTO;
import tqs.sparkflow.stationservice.service.PaymentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "APIs for managing payments and Stripe integration")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Create payment intent", description = "Create a Stripe payment intent for a booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment intent created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "409", description = "Payment already exists for booking"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create-intent")
    public ResponseEntity<PaymentIntentResponseDTO> createPaymentIntent(
            @Valid @RequestBody PaymentIntentRequestDTO request) {
        
        logger.info("Creating payment intent for booking: {}", request.getBookingId());
        
        try {
            PaymentIntentResponseDTO response = paymentService.createPaymentIntent(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for payment intent: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            logger.error("Payment already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            logger.error("Error creating payment intent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Confirm payment", description = "Confirm a payment and update booking status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment confirmed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment intent ID"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestBody Map<String, String> request) {
        
        String paymentIntentId = request.get("paymentIntentId");
        
        if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment intent ID is required"));
        }

        logger.info("Confirming payment with provided intent ID");
        
        try {
            PaymentDTO payment = paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "payment", payment
            ));
        } catch (IllegalArgumentException e) {
            logger.error("Payment not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error confirming payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to confirm payment"));
        }
    }

    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by payment ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        
        try {
            PaymentDTO payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get payments by booking ID", description = "Retrieve all payments for a specific booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBookingId(
            @Parameter(description = "Booking ID") @PathVariable Long bookingId) {
        
        List<PaymentDTO> payments = paymentService.getPaymentsByBookingId(bookingId);
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Get payment history", description = "Retrieve all payments for analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully")
    })
    @GetMapping("/history")
    public ResponseEntity<List<PaymentDTO>> getPaymentHistory() {
        List<PaymentDTO> payments = paymentService.getPaymentHistory();
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Check payment status", description = "Check if a booking has a successful payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment status checked")
    })
    @GetMapping("/status/{bookingId}")
    public ResponseEntity<Map<String, Boolean>> hasSuccessfulPayment(
            @Parameter(description = "Booking ID") @PathVariable Long bookingId) {
        
        boolean hasPayment = paymentService.hasSuccessfulPayment(bookingId);
        return ResponseEntity.ok(Map.of("hasSuccessfulPayment", hasPayment));
    }

    @Operation(summary = "Stripe webhook endpoint", description = "Handle Stripe webhook events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Webhook processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid webhook signature"),
        @ApiResponse(responseCode = "500", description = "Error processing webhook")
    })
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        logger.info("Received Stripe webhook");
        
        try {
            paymentService.handleWebhookEvent(payload, sigHeader);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (RuntimeException e) {
            logger.error("Error processing webhook: {}", e.getMessage());
            if (e.getMessage().contains("signature")) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing webhook");
        }
    }
}