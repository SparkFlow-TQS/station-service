package tqs.sparkflow.stationservice.dto;

import tqs.sparkflow.stationservice.model.BasePaymentFields;
import tqs.sparkflow.stationservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO extends BasePaymentFields {

    // Constructors
    public PaymentDTO() {
        super();
    }

    public PaymentDTO(Long id, Long bookingId, String stripePaymentIntentId, BigDecimal amount, 
                     String currency, PaymentStatus status, String description, 
                     LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime paidAt) {
        super(bookingId, stripePaymentIntentId, amount, currency, description);
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paidAt = paidAt;
    }

    // All getters and setters are inherited from BasePaymentFields

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentDTO that = (PaymentDTO) o;
        return equalsBaseFieldsWithTimestamps(that);
    }

    @Override
    public int hashCode() {
        return hashCodeBaseFieldsWithTimestamps();
    }

    @Override
    public String toString() {
        return "PaymentDTO{" + toStringBaseFields() + '}';
    }
}