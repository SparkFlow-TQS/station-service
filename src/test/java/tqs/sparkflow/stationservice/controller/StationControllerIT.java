package tqs.sparkflow.stationservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {StationServiceApplication.class, TestConfig.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
class StationControllerIT {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private StationRepository stationRepository;

  private String baseUrl;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/stations";
    stationRepository.deleteAll();
  }

  @Test
  void whenCreatingStation_thenStationIsCreated() {
    // Given
    Station station = createTestStation("Test Station");

    // When
    ResponseEntity<Station> response = restTemplate.postForEntity(baseUrl, station, Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getName()).isEqualTo(station.getName());
    assertThat(response.getBody().getConnectorType()).isEqualTo(station.getConnectorType());
  }

  @Test
  void whenGettingAllStations_thenReturnsAllStations() {
    // Given
    Station station1 = createTestStation("Station 1");
    Station station2 = createTestStation("Station 2");
    stationRepository.save(station1);
    stationRepository.save(station2);

    // When
    ResponseEntity<List<Station>> response = restTemplate.exchange(baseUrl, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Station>>() {});

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody()).extracting(Station::getName)
        .containsExactlyInAnyOrder("Station 1", "Station 2");
  }

  @Test
  void whenGettingStationById_thenReturnsStation() {
    // Given
    Station station = createTestStation("Test Station");
    station = stationRepository.save(station);

    // When
    ResponseEntity<Station> response =
        restTemplate.getForEntity(baseUrl + "/" + station.getId(), Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getName()).isEqualTo(station.getName());
  }

  @Test
  void whenGettingNonExistentStationById_thenReturnsNotFound() {
    // When
    ResponseEntity<Station> response = restTemplate.getForEntity(baseUrl + "/999", Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void whenDeletingStation_thenStationIsDeleted() {
    // Given
    Station station = createTestStation("Test Station");
    station = stationRepository.save(station);

    // When
    ResponseEntity<Void> response =
        restTemplate.exchange(baseUrl + "/" + station.getId(), HttpMethod.DELETE, null, Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(stationRepository.findById(station.getId())).isEmpty();
  }

  @Test
  void whenGettingStationsByConnectorType_thenReturnsMatchingStations() {
    // Given
    Station station1 = createTestStation("Type2 Station");
    Station station2 = createTestStation("CCS Station");
    station2.setConnectorType("CCS");
    stationRepository.save(station1);
    stationRepository.save(station2);

    // When
    ResponseEntity<List<Station>> response = restTemplate.exchange(baseUrl + "/connector/Type 2",
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Station>>() {});

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getConnectorType()).isEqualTo("Type 2");
  }

  @Test
  void whenCreateStation_thenReturnCreatedStation() {
    // Given
    Station station = new Station("Test Station", "Test Address", "Lisbon", 38.7223, -9.1393,
        "Type 2", "Available");

    // When
    ResponseEntity<Station> response = restTemplate.postForEntity(baseUrl, station, Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getName()).isEqualTo(station.getName());
    assertThat(response.getBody().getLatitude()).isEqualTo(station.getLatitude());
    assertThat(response.getBody().getLongitude()).isEqualTo(station.getLongitude());
  }

  private Station createTestStation(String name) {
    return new Station(name, "Test Address", "Lisbon", 38.7223, -9.1393, "Type 2", "Available");
  }
}
