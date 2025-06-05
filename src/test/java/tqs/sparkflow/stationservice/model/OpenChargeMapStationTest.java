package tqs.sparkflow.stationservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

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
} 