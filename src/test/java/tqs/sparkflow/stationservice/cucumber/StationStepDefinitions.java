package tqs.sparkflow.stationservice.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StationStepDefinitions {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private StationRepository stationRepository;

  private ResponseEntity<List<Station>> response;
  private ResponseEntity<Station> singleResponse;

  @Given("there are stations in the system")
  public void thereAreStationsInTheSystem() {
    // Add test stations to the repository
    Station station1 = new Station.Builder()
        .name("Test Station 1")
        .address("Test Address 1")
        .city("Test City")
        .country("Test Country")
        .latitude(38.7223)
        .longitude(-9.1393)
        .connectorType("Type 2")
        .power(22)
        .status("Available")
        .isOperational(true)
        .price(0.30)
        .build();
    station1.setId(1L);
    stationRepository.save(station1);

    Station station2 = new Station.Builder()
        .name("Test Station 2")
        .address("Test Address 2")
        .city("Test City")
        .country("Test Country")
        .latitude(38.7223)
        .longitude(-9.1393)
        .connectorType("Type 2")
        .power(22)
        .status("Available")
        .isOperational(true)
        .price(0.30)
        .build();
    station2.setId(2L);
    stationRepository.save(station2);
  }

  @When("I request all stations")
  public void iRequestAllStations() {
    response = restTemplate.exchange("http://localhost:" + port + "/api/v1/stations", HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Station>>() {});
  }

  @Then("I should receive a list of stations")
  public void iShouldReceiveAListOfStations() {
    assertNotNull(response, "Response should not be null");
    assertEquals(200, response.getStatusCode().value(), "Status code should be 200");
    List<Station> body = response.getBody();
    assertNotNull(body, "Response body should not be null");
    assertFalse(body.isEmpty(), "Response body should not be empty");
    assertEquals(2, body.size(), "Should have 2 stations in the response");
  }

  @Given("there is a station with ID {string}")
  public void thereIsAStationWithId(String id) {
    Station station = new Station.Builder()
        .name("Test Station " + id)
        .address("Test Address")
        .city("Test City")
        .country("Test Country")
        .latitude(38.7223)
        .longitude(-9.1393)
        .connectorType("Type 2")
        .power(22)
        .status("Available")
        .isOperational(true)
        .price(0.30)
        .build();
    station.setId(Long.parseLong(id));
    stationRepository.save(station);
  }

  @When("I request the station with ID {string}")
  public void iRequestTheStationWithId(String id) {
    singleResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/v1/stations/" + id, Station.class);
  }

  @Then("I should receive the station details")
  public void iShouldReceiveTheStationDetails() {
    assertNotNull(singleResponse, "Response should not be null");
    assertEquals(200, singleResponse.getStatusCode().value(), "Status code should be 200");
    Station body = singleResponse.getBody();
    assertNotNull(body, "Response body should not be null");
    assertEquals(1L, body.getId(), "Station ID should match");
    assertEquals("Test Station 1", body.getName(), "Station name should match");
  }
}
