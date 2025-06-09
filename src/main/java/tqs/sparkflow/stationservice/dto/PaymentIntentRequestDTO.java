package tqs.sparkflow.stationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PaymentIntentRequestDTO extends BasePaymentIntentDTO {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    // Constructors
    public PaymentIntentRequestDTO() {
        super();
    }

    public PaymentIntentRequestDTO(Long bookingId, Long amount, String currency, String description) {
        super(amount, currency, description);
        this.bookingId = bookingId;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    // Validation annotations for inherited fields
    @Override
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    public Long getAmount() {
        return super.getAmount();
    }

    @Override
    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    public String getCurrency() {
        return super.getCurrency();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentIntentRequestDTO that = (PaymentIntentRequestDTO) o;
        return java.util.Objects.equals(bookingId, that.bookingId) && equalsBase(that);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(bookingId, hashCodeBase());
    }

    @Override
    public String toString() {
        return "PaymentIntentRequestDTO{" +
                "bookingId=" + bookingId +
                ", " + toStringBase() +
                '}';
    }
}