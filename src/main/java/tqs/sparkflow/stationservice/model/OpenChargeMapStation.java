package tqs.sparkflow.stationservice.model;

import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tqs.sparkflow.stationservice.dto.BaseStationFields;

/** Station model for OpenChargeMap API. */
public class OpenChargeMapStation extends BaseStationFields<String> {
    private List<Connection> connections;
    
    @NotNull(message = "Quantity of chargers cannot be null")
    @Min(value = 1, message = "Quantity of chargers must be at least 1")
    private Integer quantityOfChargers;
    
    private String status;

    @NotBlank(message = "Connector type cannot be empty")
    private String connectorType;

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public Integer getQuantityOfChargers() {
        return quantityOfChargers;
    }

    public void setQuantityOfChargers(Integer quantityOfChargers) {
        this.quantityOfChargers = quantityOfChargers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    /**
     * Calculates the total quantity of chargers based on the connections data.
     * Each connection can specify a quantity, and if unspecified, it counts as one charger.
     *
     * @return The total quantity of chargers
     */
    public Integer calculateQuantityOfChargers() {
        if (connections == null || connections.isEmpty()) {
            return 1; // Default to 1 if no connections
        }

        int totalChargers = 0;
        for (Connection connection : connections) {
            Integer quantity = connection.getQuantity();
            totalChargers += (quantity != null) ? quantity : 1;
        }
        
        return totalChargers > 0 ? totalChargers : 1; // Ensure at least 1 charger
    }

    /** Represents a connection at an OpenChargeMap station. */
    public static class Connection {
        private Integer quantity;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
