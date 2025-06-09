package tqs.sparkflow.stationservice.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tqs.sparkflow.stationservice.dto.PaymentDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentRequestDTO;
import tqs.sparkflow.stationservice.dto.PaymentIntentResponseDTO;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.Payment;
import tqs.sparkflow.stationservice.model.PaymentStatus;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.PaymentRepository;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public PaymentIntentResponseDTO createPaymentIntent(PaymentIntentRequestDTO request) {
        try {
            // Verify booking exists
            Optional<Booking> bookingOpt = bookingRepository.findById(request.getBookingId());
            if (bookingOpt.isEmpty()) {
                throw new IllegalArgumentException("Booking not found with ID: " + request.getBookingId());
            }

            Booking booking = bookingOpt.get();

            // Check if payment already exists for this booking
            List<Payment> existingPayments = paymentRepository.findByBookingIdAndStatusOrderByCreatedAtDesc(
                request.getBookingId(), PaymentStatus.SUCCEEDED);
            
            if (!existingPayments.isEmpty()) {
                throw new IllegalStateException("Payment already exists for booking ID: " + request.getBookingId());
            }

            // Create Stripe payment intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())
                .setCurrency(request.getCurrency())
                .setDescription(request.getDescription() != null ? request.getDescription() : 
                    "Charging session at station " + booking.getStationId())
                .putMetadata("booking_id", request.getBookingId().toString())
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Save payment record
            Payment payment = new Payment(
                request.getBookingId(),
                paymentIntent.getId(),
                BigDecimal.valueOf(request.getAmount()).divide(BigDecimal.valueOf(100)), // Convert cents to euros
                request.getCurrency(),
                request.getDescription()
            );

            payment = paymentRepository.save(payment);

            logger.info("Created payment intent {} for booking {}", paymentIntent.getId(), request.getBookingId());

            return new PaymentIntentResponseDTO(
                paymentIntent.getId(),
                paymentIntent.getClientSecret(),
                request.getAmount(),
                request.getCurrency(),
                paymentIntent.getStatus(),
                request.getDescription()
            );

        } catch (StripeException e) {
            logger.error("Stripe error creating payment intent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating payment intent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }

    @Override
    public PaymentDTO confirmPayment(String paymentIntentId) {
        try {
            // Retrieve payment intent from Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            // Update local payment record
            Payment payment = updatePaymentFromStripeEvent(paymentIntent);
            
            // If payment succeeded, update booking status
            if (PaymentStatus.SUCCEEDED.equals(payment.getStatus())) {
                Optional<Booking> bookingOpt = bookingRepository.findById(payment.getBookingId());
                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    // You might want to update booking status here
                    logger.info("Payment confirmed for booking {}", booking.getId());
                }
            }

            return convertToDTO(payment);

        } catch (StripeException e) {
            logger.error("Stripe error confirming payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage());
        }
    }

    @Override
    public void handleWebhookEvent(String payload, String sigHeader) {
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            logger.warn("Webhook secret not configured, skipping signature verification");
            return;
        }

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            
            logger.info("Received webhook event: {}", event.getType());

            switch (event.getType()) {
                case "payment_intent.succeeded":
                case "payment_intent.processing":
                case "payment_intent.requires_action":
                case "payment_intent.canceled":
                case "payment_intent.payment_failed":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                    if (paymentIntent != null) {
                        updatePaymentFromStripeEvent(paymentIntent);
                    }
                    break;
                default:
                    logger.info("Unhandled event type: {}", event.getType());
            }

        } catch (SignatureVerificationException e) {
            logger.error("Invalid webhook signature: {}", e.getMessage());
            throw new RuntimeException("Invalid webhook signature");
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing webhook: " + e.getMessage());
        }
    }

    @Override
    public Payment updatePaymentFromStripeEvent(PaymentIntent paymentIntent) {
        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntent.getId());
        
        if (paymentOpt.isEmpty()) {
            logger.warn("Payment not found for Stripe payment intent: {}", paymentIntent.getId());
            return null;
        }

        Payment payment = paymentOpt.get();
        
        // Update payment status based on Stripe status
        PaymentStatus newStatus = mapStripeStatusToPaymentStatus(paymentIntent.getStatus());
        payment.setStatus(newStatus);

        // Update charge ID if available
        if (paymentIntent.getLatestCharge() != null) {
            payment.setStripeChargeId(paymentIntent.getLatestCharge());
        }

        payment = paymentRepository.save(payment);
        
        logger.info("Updated payment {} with status {}", payment.getId(), newStatus);
        
        return payment;
    }

    private PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        switch (stripeStatus) {
            case "succeeded":
                return PaymentStatus.SUCCEEDED;
            case "processing":
                return PaymentStatus.PROCESSING;
            case "requires_action":
                return PaymentStatus.REQUIRES_ACTION;
            case "canceled":
                return PaymentStatus.CANCELED;
            case "requires_payment_method":
                return PaymentStatus.FAILED;
            default:
                return PaymentStatus.PENDING;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentId);
        }
        return convertToDTO(paymentOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByStripeId(String stripePaymentIntentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with Stripe ID: " + stripePaymentIntentId);
        }
        return convertToDTO(paymentOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByBookingId(Long bookingId) {
        List<Payment> payments = paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentHistory() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSuccessfulPayment(Long bookingId) {
        return paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.SUCCEEDED);
    }

    @Override
    public PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
            payment.getId(),
            payment.getBookingId(),
            payment.getStripePaymentIntentId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getStatus(),
            payment.getDescription(),
            payment.getCreatedAt(),
            payment.getUpdatedAt(),
            payment.getPaidAt()
        );
    }
}