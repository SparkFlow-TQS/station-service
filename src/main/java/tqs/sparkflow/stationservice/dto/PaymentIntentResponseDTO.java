package tqs.sparkflow.stationservice.dto;

public class PaymentIntentResponseDTO {

    private String id;
    private String clientSecret;
    private Long amount;
    private String currency;
    private String status;
    private String description;

    // Constructors
    public PaymentIntentResponseDTO() {}

    public PaymentIntentResponseDTO(String id, String clientSecret, Long amount, String currency, String status, String description) {
        this.id = id;
        this.clientSecret = clientSecret;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PaymentIntentResponseDTO{" +
                "id='" + id + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}