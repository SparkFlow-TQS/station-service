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
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.OpenChargeMapTestConfig;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {StationServiceApplication.class, TestConfig.class, TestcontainersConfiguration.class, OpenChargeMapTestConfig.class},
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
    baseUrl = "http://localhost:" + port + "/api/v1/stations";
    stationRepository.deleteAll();
  }

  @Test
  @XrayTest(key = "STATION-IT-1")
  @Requirement("STATION-IT-1")
  void whenCreatingStation_thenStationIsCreated() {
    // Given
    Station station = createTestStation("Test Station");

    // When
    ResponseEntity<Station> response = restTemplate.postForEntity(baseUrl, station, Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    Station responseStation = response.getBody();
    assertThat(responseStation).isNotNull()
        .satisfies(s -> {
            assertThat(s.getName()).isEqualTo(station.getName());
            assertThat(s.getConnectorType()).isEqualTo(station.getConnectorType());
        });
  }

  @Test
  @XrayTest(key = "STATION-IT-2")
  @Requirement("STATION-IT-2")
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
  @XrayTest(key = "STATION-IT-3")
  @Requirement("STATION-IT-3")
  void whenGettingStationById_thenReturnsStation() {
    // Given
    Station station = createTestStation("Test Station");
    station = stationRepository.save(station);
    final String expectedName = station.getName();

    // When
    ResponseEntity<Station> response = restTemplate.getForEntity(baseUrl + "/" + station.getId(),
        Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    Station responseStation = response.getBody();
    assertThat(responseStation).isNotNull()
        .satisfies(s -> {
            assertThat(s.getName()).isEqualTo(expectedName);
        });
  }

  @Test
  @XrayTest(key = "STATION-IT-4")
  @Requirement("STATION-IT-4")
  void whenGettingNonExistentStationById_thenReturnsNotFound() {
    // When
    ResponseEntity<Station> response = restTemplate.getForEntity(baseUrl + "/999", Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @XrayTest(key = "STATION-IT-5")
  @Requirement("STATION-IT-5")
  void whenDeletingStation_thenStationIsDeleted() {
    // Given
    Station station = createTestStation("Test Station");
    station = stationRepository.save(station);

    // When
    restTemplate.delete(baseUrl + "/" + station.getId());

    // Then
    assertThat(stationRepository.findById(station.getId())).isEmpty();
  }

  @Test
  @XrayTest(key = "STATION-IT-7")
  @Requirement("STATION-IT-7")
  void whenCreateStation_thenReturnCreatedStation() {
    // Given
    Station station = new Station("Test Station", "Test Address", "Lisbon", 38.7223, -9.1393,
        "Type 2", "Available");

    // When
    ResponseEntity<Station> response = restTemplate.postForEntity(baseUrl, station, Station.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    Station responseStation = response.getBody();
    assertThat(responseStation).isNotNull()
        .satisfies(s -> {
            assertThat(s.getName()).isEqualTo(station.getName());
            assertThat(s.getLatitude()).isEqualTo(station.getLatitude());
            assertThat(s.getLongitude()).isEqualTo(station.getLongitude());
        });
  }

  private Station createTestStation(String name) {
    Station station = new Station();
    station.setName(name);
    station.setAddress("Test Address");
    station.setCity("Test City");
    station.setCountry("Test Country");
    station.setLatitude(38.7223);
    station.setLongitude(-9.1393);
    station.setConnectorType("Type 2");
    station.setPower(22);
    station.setStatus("Available");
    return station;
  }
}
