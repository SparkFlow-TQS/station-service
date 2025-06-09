package tqs.sparkflow.stationservice.model;

import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

/** Station model for OpenChargeMap API. */
public class OpenChargeMapStation extends BaseStationFields {
    private List<Connection> connections;

    @NotNull(message = "Quantity of chargers cannot be null")
    @Min(value = 1, message = "Quantity of chargers must be at least 1")
    private Integer quantityOfChargers;

    private Integer power;

    @NotBlank(message = "Connector type cannot be empty")
    private String connectorType;

    private String id;

    /**
     * Gets the power of the station.
     *
     * @return The power of the station
     */
    public Integer getPower() {
        return power;
    }

    /**
     * Sets the power of the station.
     *
     * @param power The power of the station
     */
    public void setPower(Integer power) {
        this.power = power;
    }

    /**
     * Gets the connections of the station.
     *
     * @return The connections of the station
     */
    public List<Connection> getConnections() {
        return connections;
    }

    /**
     * Sets the connections of the station.
     *
     * @param connections The connections of the station
     */
    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    /**
     * Gets the quantity of chargers of the station.
     *
     * @return The quantity of chargers of the station
     */
    public Integer getQuantityOfChargers() {
        return quantityOfChargers;
    }

    /**
     * Sets the quantity of chargers of the station.
     *
     * @param quantityOfChargers The quantity of chargers of the station
     */
    public void setQuantityOfChargers(Integer quantityOfChargers) {
        this.quantityOfChargers = quantityOfChargers;
    }

    /**
     * Gets the connector type of the station.
     *
     * @return The connector type of the station
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * Sets the connector type of the station.
     *
     * @param connectorType The connector type of the station
     */
    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    /**
     * Calculates the total quantity of chargers based on the connections data. Each connection can
     * specify a quantity, and if unspecified, it counts as one charger.
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

        /**
         * Gets the quantity of the connection.
         *
         * @return The quantity of the connection
         */
        public Integer getQuantity() {
            return quantity;
        }

        /**
         * Sets the quantity of the connection.
         *
         * @param quantity The quantity of the connection
         */
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Connection that = (Connection) o;
            return Objects.equals(quantity, that.quantity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(quantity);
        }
    }

    /**
     * Gets the id of the station.
     *
     * @return The id of the station
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the station.
     *
     * @param id The id of the station
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        OpenChargeMapStation that = (OpenChargeMapStation) o;
        return Objects.equals(connections, that.connections)
                && Objects.equals(quantityOfChargers, that.quantityOfChargers)
                && Objects.equals(power, that.power)
                && Objects.equals(connectorType, that.connectorType) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), connections, quantityOfChargers, power, connectorType,
                id);
    }
}
