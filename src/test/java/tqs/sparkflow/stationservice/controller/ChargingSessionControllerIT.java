package tqs.sparkflow.stationservice.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {StationServiceApplication.class, TestConfig.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
class ChargingSessionControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ChargingSessionRepository chargingSessionRepository;

    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        chargingSessionRepository.deleteAll();
        stationRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "1")
    void whenStartSession_thenReturnSuccess() {
        // Create a test station first
        Station station = createTestStation("Test Station");
        station = stationRepository.save(station);

        given().contentType(ContentType.JSON).queryParam("stationId", station.getId().toString())
                .queryParam("userId", "1").when().post("/api/v1/charging-sessions/start").then()
                .statusCode(200).body("stationId", equalTo(station.getId().toString()))
                .body("userId", equalTo("1")).body("finished", equalTo(false));
    }

    @Test
    @WithMockUser(username = "1")
    void whenEndSession_thenReturnSuccess() {
        // Create a test station first
        Station station = createTestStation("Test Station");
        station = stationRepository.save(station);

        // First create a session
        ChargingSession session = given().contentType(ContentType.JSON)
                .queryParam("stationId", station.getId().toString()).queryParam("userId", "1")
                .when().post("/api/v1/charging-sessions/start").then().statusCode(200).extract()
                .as(ChargingSession.class);

        // Then end it
        given().contentType(ContentType.JSON).when()
                .post("/api/v1/charging-sessions/{sessionId}/end", session.getId()).then()
                .statusCode(200).body("finished", equalTo(true));
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetSession_thenReturnSession() {
        // Create a test station first
        Station station = createTestStation("Test Station");
        station = stationRepository.save(station);

        // First create a session
        ChargingSession session = given().contentType(ContentType.JSON)
                .queryParam("stationId", station.getId().toString()).queryParam("userId", "1")
                .when().post("/api/v1/charging-sessions/start").then().statusCode(200).extract()
                .as(ChargingSession.class);

        // Then retrieve it
        given().contentType(ContentType.JSON).when()
                .get("/api/v1/charging-sessions/{sessionId}", session.getId()).then()
                .statusCode(200).body("id", equalTo(session.getId().intValue()))
                .body("stationId", equalTo(station.getId().toString()))
                .body("userId", equalTo("1"));
    }

    @Test
    @WithMockUser(username = "1")
    void whenStartSession_withNoFreeChargers_thenReturnBadRequest() {
        // Create a station with only 1 charger
        Station station = createTestStation("Test Station");
        station.setQuantityOfChargers(1);
        station = stationRepository.save(station);

        // Create a session to occupy the only charger
        ChargingSession existingSession = new ChargingSession(station.getId().toString(), "2");
        existingSession.setStartTime(LocalDateTime.now());
        chargingSessionRepository.save(existingSession);

        // Try to start another session - should fail because no chargers are free
        given().contentType(ContentType.JSON).queryParam("stationId", station.getId().toString())
                .queryParam("userId", "1").when().post("/api/v1/charging-sessions/start").then()
                .statusCode(400).body("message", containsString(
                        "Cannot start session: no booking or free chargers available"));
    }

    @Test
    @WithMockUser(username = "1")
    void whenEndSession_withNonExistentSession_thenReturnNotFound() {
        given().contentType(ContentType.JSON).when()
                .post("/api/v1/charging-sessions/{sessionId}/end", "999").then().statusCode(404);
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetSession_withNonExistentSession_thenReturnNotFound() {
        given().contentType(ContentType.JSON).when()
                .get("/api/v1/charging-sessions/{sessionId}", "999").then().statusCode(404);
    }

    private ChargingSession createTestSession(String stationId, String userId) {
        ChargingSession session = new ChargingSession();
        session.setStationId(stationId);
        session.setUserId(userId);
        return session;
    }

    private Station createTestStation(String name) {
        Station station = new Station();
        station.setName(name);
        station.setExternalId("1234567890");
        station.setAddress("Test Address");
        station.setCity("Test City");
        station.setCountry("Test Country");
        station.setLatitude(38.7223);
        station.setLongitude(-9.1393);
        station.setQuantityOfChargers(5); // Default to 5 chargers
        station.setPower(22);
        station.setStatus("Available");
        return station;
    }
}
