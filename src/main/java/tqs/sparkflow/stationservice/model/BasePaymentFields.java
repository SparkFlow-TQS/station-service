package tqs.sparkflow.stationservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base class containing common fields and methods for Payment entities and DTOs.
 * This class reduces code duplication between Payment model and PaymentDTO.
 */
public abstract class BasePaymentFields {

    protected Long id;
    protected Long bookingId;
    protected String stripePaymentIntentId;
    protected BigDecimal amount;
    protected String currency;
    protected PaymentStatus status;
    protected String description;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected LocalDateTime paidAt;

    // Default constructor
    protected BasePaymentFields() {}

    // Constructor with common fields
    protected BasePaymentFields(Long bookingId, String stripePaymentIntentId, BigDecimal amount, 
                               String currency, String description) {
        this.bookingId = bookingId;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }

    // Common getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    // Helper methods for equals implementation
    protected boolean equalsBaseFields(BasePaymentFields that) {
        return Objects.equals(id, that.id) &&
               Objects.equals(bookingId, that.bookingId) &&
               Objects.equals(stripePaymentIntentId, that.stripePaymentIntentId) &&
               Objects.equals(amount, that.amount) &&
               Objects.equals(currency, that.currency) &&
               status == that.status &&
               Objects.equals(description, that.description);
    }

    protected boolean equalsBaseFieldsWithTimestamps(BasePaymentFields that) {
        return equalsBaseFields(that) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt) &&
               Objects.equals(paidAt, that.paidAt);
    }

    // Helper methods for hashCode implementation  
    protected int hashCodeBaseFields() {
        return Objects.hash(id, bookingId, stripePaymentIntentId, amount, currency, status, description);
    }

    protected int hashCodeBaseFieldsWithTimestamps() {
        return Objects.hash(id, bookingId, stripePaymentIntentId, amount, currency, status, description, 
                           createdAt, updatedAt, paidAt);
    }

    // Helper method for toString implementation
    protected String toStringBaseFields() {
        return "id=" + id +
               ", bookingId=" + bookingId +
               ", stripePaymentIntentId='" + stripePaymentIntentId + '\'' +
               ", amount=" + amount +
               ", currency='" + currency + '\'' +
               ", status=" + status +
               ", description='" + description + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", paidAt=" + paidAt;
    }
}