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

    /**
     * Builder pattern for creating PaymentDTO instances.
     * Reduces constructor complexity from 10 parameters.
     */
    public static class Builder {
        private Long id;
        private Long bookingId;
        private String stripePaymentIntentId;
        private BigDecimal amount;
        private String currency;
        private PaymentStatus status;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime paidAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder bookingId(Long bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder stripePaymentIntentId(String stripePaymentIntentId) {
            this.stripePaymentIntentId = stripePaymentIntentId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder paidAt(LocalDateTime paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public PaymentDTO build() {
            PaymentDTO dto = new PaymentDTO();
            dto.setId(id);
            dto.setBookingId(bookingId);
            dto.setStripePaymentIntentId(stripePaymentIntentId);
            dto.setAmount(amount);
            dto.setCurrency(currency);
            dto.setStatus(status);
            dto.setDescription(description);
            dto.setCreatedAt(createdAt);
            dto.setUpdatedAt(updatedAt);
            dto.setPaidAt(paidAt);
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // All getters and setters are inherited from BasePaymentFields
    
    // Legacy constructor kept for backward compatibility but deprecated
    @Deprecated(since = "0.5.0", forRemoval = true)
    public PaymentDTO(Long id, Long bookingId, String stripePaymentIntentId, BigDecimal amount, 
                     String currency, PaymentStatus status, String description, 
                     LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime paidAt) {
        this();
        setId(id);
        setBookingId(bookingId);
        setStripePaymentIntentId(stripePaymentIntentId);
        setAmount(amount);
        setCurrency(currency);
        setStatus(status);
        setDescription(description);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setPaidAt(paidAt);
    }

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