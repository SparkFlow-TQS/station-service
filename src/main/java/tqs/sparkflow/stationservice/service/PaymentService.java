package tqs.sparkflow.stationservice.service;

import tqs.sparkflow.stationservice.dto.PaymentDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentRequestDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentResponseDTO;
import tqs.sparkflow.stationservice.model.Payment;
import com.stripe.model.PaymentIntent;

import java.util.List;

public interface PaymentService {

    /**
     * Create a Stripe payment intent for a booking
     */
    PaymentIntentResponseDTO createPaymentIntent(PaymentIntentRequestDTO request);

    /**
     * Confirm a payment and update booking status
     */
    PaymentDTO confirmPayment(String paymentIntentId);

    /**
     * Handle Stripe webhook events
     */
    void handleWebhookEvent(String payload, String sigHeader);

    /**
     * Get payment by ID
     */
    PaymentDTO getPaymentById(Long paymentId);

    /**
     * Get payment by Stripe payment intent ID
     */
    PaymentDTO getPaymentByStripeId(String stripePaymentIntentId);

    /**
     * Get all payments for a booking
     */
    List<PaymentDTO> getPaymentsByBookingId(Long bookingId);

    /**
     * Get payment history for analytics
     */
    List<PaymentDTO> getPaymentHistory();

    /**
     * Update payment status from Stripe webhook
     */
    Payment updatePaymentFromStripeEvent(PaymentIntent paymentIntent);

    /**
     * Check if booking has successful payment
     */
    boolean hasSuccessfulPayment(Long bookingId);

    /**
     * Convert Payment entity to DTO
     */
    PaymentDTO convertToDTO(Payment payment);
}