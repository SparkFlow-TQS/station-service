package tqs.sparkflow.stationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PaymentIntentRequestDTO {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Long amount; // Amount in cents

    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    private String currency;

    private String description;

    // Constructors
    public PaymentIntentRequestDTO() {}

    public PaymentIntentRequestDTO(Long bookingId, Long amount, String currency, String description) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentIntentRequestDTO that = (PaymentIntentRequestDTO) o;
        return java.util.Objects.equals(bookingId, that.bookingId) &&
                java.util.Objects.equals(amount, that.amount) &&
                java.util.Objects.equals(currency, that.currency) &&
                java.util.Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(bookingId, amount, currency, description);
    }

    @Override
    public String toString() {
        return "PaymentIntentRequestDTO{" +
                "bookingId=" + bookingId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}