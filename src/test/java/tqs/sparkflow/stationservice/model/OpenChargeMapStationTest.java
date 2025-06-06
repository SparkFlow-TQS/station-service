package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class OpenChargeMapStationTest {

    @Test
    void whenCreatingEmptyStation_thenAllFieldsAreNull() {
        OpenChargeMapStation station = new OpenChargeMapStation();

        assertThat(station.getId()).isNull();
        assertThat(station.getName()).isNull();
        assertThat(station.getAddress()).isNull();
        assertThat(station.getCity()).isNull();
        assertThat(station.getCountry()).isNull();
        assertThat(station.getLatitude()).isNull();
        assertThat(station.getLongitude()).isNull();
        assertThat(station.getQuantityOfChargers()).isNull();
        assertThat(station.getStatus()).isNull();
    }

    @Test
    void whenCreatingStationWithValues_thenAllFieldsAreSet() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setId("1");
        station.setName("Test Station");
        station.setAddress("Test Address");
        station.setCity("Test City");
        station.setCountry("Test Country");
        station.setLatitude(41.1579);
        station.setLongitude(-8.6291);
        station.setQuantityOfChargers(2);
        station.setStatus("Available");

        assertThat(station.getId()).isEqualTo("1");
        assertThat(station.getName()).isEqualTo("Test Station");
        assertThat(station.getAddress()).isEqualTo("Test Address");
        assertThat(station.getCity()).isEqualTo("Test City");
        assertThat(station.getCountry()).isEqualTo("Test Country");
        assertThat(station.getLatitude()).isEqualTo(41.1579);
        assertThat(station.getLongitude()).isEqualTo(-8.6291);
        assertThat(station.getQuantityOfChargers()).isEqualTo(2);
        assertThat(station.getStatus()).isEqualTo("Available");
    }

    @Test
    void testNullValues() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setId(null);
        station.setName(null);
        station.setAddress(null);
        station.setCity(null);
        station.setCountry(null);
        station.setLatitude(null);
        station.setLongitude(null);
        station.setQuantityOfChargers(null);
        station.setStatus(null);

        assertThat(station.getId()).isNull();
        assertThat(station.getName()).isNull();
        assertThat(station.getAddress()).isNull();
        assertThat(station.getCity()).isNull();
        assertThat(station.getCountry()).isNull();
        assertThat(station.getLatitude()).isNull();
        assertThat(station.getLongitude()).isNull();
        assertThat(station.getQuantityOfChargers()).isNull();
        assertThat(station.getStatus()).isNull();
    }

    @Test
    void testConnectionsList() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        List<OpenChargeMapStation.Connection> connections = new ArrayList<>();

        OpenChargeMapStation.Connection connection1 = new OpenChargeMapStation.Connection();
        connection1.setQuantity(2);
        connections.add(connection1);

        OpenChargeMapStation.Connection connection2 = new OpenChargeMapStation.Connection();
        connection2.setQuantity(3);
        connections.add(connection2);

        station.setConnections(connections);

        assertThat(station.getConnections()).hasSize(2);
        assertThat(station.getConnections().get(0).getQuantity()).isEqualTo(2);
        assertThat(station.getConnections().get(1).getQuantity()).isEqualTo(3);
    }

    @Test
    void testCalculateQuantityOfChargers_withNullConnections() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setConnections(null);

        assertThat(station.calculateQuantityOfChargers()).isEqualTo(1);
    }

    @Test
    void testCalculateQuantityOfChargers_withEmptyConnections() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setConnections(new ArrayList<>());

        assertThat(station.calculateQuantityOfChargers()).isEqualTo(1);
    }

    @Test
    void testCalculateQuantityOfChargers_withMultipleConnections() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        List<OpenChargeMapStation.Connection> connections = new ArrayList<>();

        OpenChargeMapStation.Connection connection1 = new OpenChargeMapStation.Connection();
        connection1.setQuantity(2);
        connections.add(connection1);

        OpenChargeMapStation.Connection connection2 = new OpenChargeMapStation.Connection();
        connection2.setQuantity(3);
        connections.add(connection2);

        station.setConnections(connections);

        assertThat(station.calculateQuantityOfChargers()).isEqualTo(5);
    }

    @Test
    void testCalculateQuantityOfChargers_withNullQuantities() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        List<OpenChargeMapStation.Connection> connections = new ArrayList<>();

        OpenChargeMapStation.Connection connection1 = new OpenChargeMapStation.Connection();
        connection1.setQuantity(null);
        connections.add(connection1);

        OpenChargeMapStation.Connection connection2 = new OpenChargeMapStation.Connection();
        connection2.setQuantity(null);
        connections.add(connection2);

        station.setConnections(connections);

        assertThat(station.calculateQuantityOfChargers()).isEqualTo(2);
    }

    @Test
    void testPower() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setPower(50);
        assertThat(station.getPower()).isEqualTo(50);
    }

    @Test
    void testConnectorType() {
        OpenChargeMapStation station = new OpenChargeMapStation();
        station.setConnectorType("Type2");
        assertThat(station.getConnectorType()).isEqualTo("Type2");
    }

    @Test
    void testEqualsAndHashCode() {
        OpenChargeMapStation station1 = new OpenChargeMapStation();
        station1.setId("123");
        station1.setPower(50);
        station1.setStatus("Available");
        station1.setConnectorType("Type2");
        station1.setQuantityOfChargers(2);
        OpenChargeMapStation.Connection conn1 = new OpenChargeMapStation.Connection();
        conn1.setQuantity(1);
        station1.setConnections(List.of(conn1));

        OpenChargeMapStation station2 = new OpenChargeMapStation();
        station2.setId("123");
        station2.setPower(50);
        station2.setStatus("Available");
        station2.setConnectorType("Type2");
        station2.setQuantityOfChargers(2);
        OpenChargeMapStation.Connection conn2 = new OpenChargeMapStation.Connection();
        conn2.setQuantity(1);
        station2.setConnections(List.of(conn2));

        OpenChargeMapStation station3 = new OpenChargeMapStation();
        station3.setId("456");
        station3.setPower(100);
        station3.setStatus("In Use");
        station3.setConnectorType("CCS");
        station3.setQuantityOfChargers(3);
        OpenChargeMapStation.Connection conn3 = new OpenChargeMapStation.Connection();
        conn3.setQuantity(2);
        station3.setConnections(List.of(conn3));

        // Test equals
        assertThat(station1).isEqualTo(station2);
        assertThat(station1).isNotEqualTo(station3);

        // Test hashCode
        assertThat(station1.hashCode()).isEqualTo(station2.hashCode());
        assertThat(station1.hashCode()).isNotEqualTo(station3.hashCode());
    }
}
