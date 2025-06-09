package tqs.sparkflow.stationservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment extends BasePaymentFields {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column(name = "booking_id", nullable = false)
    @Override
    public Long getBookingId() {
        return super.getBookingId();
    }

    @Column(name = "stripe_payment_intent_id", nullable = false, unique = true)
    @Override
    public String getStripePaymentIntentId() {
        return super.getStripePaymentIntentId();
    }

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @Override
    public BigDecimal getAmount() {
        return super.getAmount();
    }

    @Column(name = "currency", nullable = false, length = 3)
    @Override
    public String getCurrency() {
        return super.getCurrency();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Override
    public PaymentStatus getStatus() {
        return super.getStatus();
    }

    @Column(name = "stripe_charge_id")
    private String stripeChargeId;

    @Column(name = "description")
    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Column(name = "created_at", nullable = false)
    @Override
    public LocalDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    @Column(name = "updated_at", nullable = false)
    @Override
    public LocalDateTime getUpdatedAt() {
        return super.getUpdatedAt();
    }

    @Column(name = "paid_at")
    @Override
    public LocalDateTime getPaidAt() {
        return super.getPaidAt();
    }

    // Constructors
    public Payment() {
        super();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public Payment(Long bookingId, String stripePaymentIntentId, BigDecimal amount, String currency, String description) {
        super(bookingId, stripePaymentIntentId, amount, currency, description);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    // Update timestamp on entity changes
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Additional getters and setters for Payment-specific fields
    public String getStripeChargeId() {
        return stripeChargeId;
    }

    public void setStripeChargeId(String stripeChargeId) {
        this.stripeChargeId = stripeChargeId;
    }

    // Override setStatus to handle paidAt logic
    @Override
    public void setStatus(PaymentStatus status) {
        super.setStatus(status);
        if (status == PaymentStatus.SUCCEEDED && this.getPaidAt() == null) {
            this.setPaidAt(LocalDateTime.now());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return equalsBaseFields(payment);
    }

    @Override
    public int hashCode() {
        return hashCodeBaseFields();
    }

    @Override
    public String toString() {
        return "Payment{" + toStringBaseFields() + '}';
    }
}