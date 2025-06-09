package tqs.sparkflow.stationservice.dto;

import java.util.Objects;

/**
 * Base class for PaymentIntent DTOs to reduce code duplication.
 * Contains common fields and methods shared between request and response DTOs.
 */
public abstract class BasePaymentIntentDTO {

    protected Long amount;
    protected String currency;
    protected String description;

    // Default constructor
    protected BasePaymentIntentDTO() {}

    // Constructor with common fields
    protected BasePaymentIntentDTO(Long amount, String currency, String description) {
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }

    // Common getters and setters
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Helper method for equals implementation
    protected boolean equalsBase(BasePaymentIntentDTO that) {
        return Objects.equals(amount, that.amount) &&
               Objects.equals(currency, that.currency) &&
               Objects.equals(description, that.description);
    }

    // Helper method for hashCode implementation
    protected int hashCodeBase() {
        return Objects.hash(amount, currency, description);
    }

    // Helper method for toString implementation
    protected String toStringBase() {
        return "amount=" + amount +
               ", currency='" + currency + '\'' +
               ", description='" + description + '\'';
    }
}