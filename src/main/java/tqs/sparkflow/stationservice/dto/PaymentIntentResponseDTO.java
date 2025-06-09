package tqs.sparkflow.stationservice.dto;

public class PaymentIntentResponseDTO extends BasePaymentIntentDTO {

    private String id;
    private String clientSecret;
    private String status;

    // Constructors
    public PaymentIntentResponseDTO() {
        super();
    }

    public PaymentIntentResponseDTO(String id, String clientSecret, Long amount, String currency, String status, String description) {
        super(amount, currency, description);
        this.id = id;
        this.clientSecret = clientSecret;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentIntentResponseDTO that = (PaymentIntentResponseDTO) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(clientSecret, that.clientSecret) &&
                java.util.Objects.equals(status, that.status) &&
                equalsBase(that);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, clientSecret, status, hashCodeBase());
    }

    @Override
    public String toString() {
        return "PaymentIntentResponseDTO{" +
                "id='" + id + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", status='" + status + '\'' +
                ", " + toStringBase() +
                '}';
    }
}