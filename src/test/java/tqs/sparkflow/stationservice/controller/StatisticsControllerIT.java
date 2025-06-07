package tqs.sparkflow.stationservice.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {StationServiceApplication.class, TestConfig.class, TestcontainersConfiguration.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
class StatisticsControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ChargingSessionRepository chargingSessionRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        baseUrl = "/api/statistics";
        
        // Clean up data
        chargingSessionRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    @XrayTest(key = "STATS-IT-1")
    @Requirement("SPARKFLOW-18")
    void whenGettingCurrentMonthStatistics_thenReturnsStatistics() {
        Long userId = 1L;
        createTestChargingSession(userId);

        given()
            .auth().basic("user", "password")
            .when()
            .get(baseUrl + "/current-month/{userId}", userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("totalSessions", greaterThanOrEqualTo(0))
            .body("totalCost", greaterThanOrEqualTo(0.0f))
            .body("estimatedKwh", greaterThanOrEqualTo(0.0f))
            .body("co2Saved", greaterThanOrEqualTo(0.0f))
            .body("avgCostPerSession", greaterThanOrEqualTo(0.0f));
    }

    @Test
    @XrayTest(key = "STATS-IT-2")
    @Requirement("SPARKFLOW-18")
    void whenGettingMonthlyStatistics_thenReturnsStatistics() {
        Long userId = 1L;
        int year = 2024;
        int month = 1;
        createTestChargingSession(userId);
        createTestBooking(userId);

        given()
            .auth().basic("user", "password")
            .param("year", year)
            .param("month", month)
            .when()
            .get(baseUrl + "/monthly/{userId}", userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("month", notNullValue())
            .body("fullMonth", notNullValue())
            .body("sessions", greaterThanOrEqualTo(0))
            .body("cost", greaterThanOrEqualTo(0.0f))
            .body("duration", greaterThanOrEqualTo(0.0f))
            .body("kwh", greaterThanOrEqualTo(0.0f))
            .body("reservations", notNullValue());
    }

    @Test
    @XrayTest(key = "STATS-IT-3")
    @Requirement("SPARKFLOW-18")
    void whenGettingWeeklyStatistics_thenReturnsStatistics() {
        Long userId = 1L;
        String startDate = "2024-01-15";
        createTestChargingSession(userId);
        createTestBooking(userId);

        given()
            .auth().basic("user", "password")
            .param("startDate", startDate)
            .when()
            .get(baseUrl + "/weekly/{userId}", userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("week", notNullValue())
            .body("sessions", greaterThanOrEqualTo(0))
            .body("cost", greaterThanOrEqualTo(0.0f))
            .body("dateRange", notNullValue())
            .body("reservations", notNullValue());
    }

    @Test
    @XrayTest(key = "STATS-IT-4")
    @Requirement("SPARKFLOW-18")
    void whenGettingCostTrend_thenReturnsTrend() {
        Long userId = 1L;
        int months = 3;
        createTestChargingSession(userId);

        given()
            .auth().basic("user", "password")
            .param("months", months)
            .when()
            .get(baseUrl + "/cost-trend/{userId}", userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(months))
            .body("[0].month", notNullValue())
            .body("[0].cost", greaterThanOrEqualTo(0.0f))
            .body("[0].sessions", greaterThanOrEqualTo(0));
    }

    @Test
    @XrayTest(key = "STATS-IT-5")
    @Requirement("SPARKFLOW-18")
    void whenGettingPeriodDetails_thenReturnsDetails() {
        Long userId = 1L;
        String startDate = "2024-01-01T00:00:00";
        String endDate = "2024-01-31T23:59:59";
        createTestChargingSession(userId);
        createTestBooking(userId);

        given()
            .auth().basic("user", "password")
            .param("startDate", startDate)
            .param("endDate", endDate)
            .when()
            .get(baseUrl + "/period/{userId}", userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("totalReservations", greaterThanOrEqualTo(0))
            .body("totalCost", greaterThanOrEqualTo(0.0f))
            .body("avgCostPerSession", greaterThanOrEqualTo(0.0f))
            .body("reservations", notNullValue());
    }

    @Test
    @XrayTest(key = "STATS-IT-6")
    @Requirement("SPARKFLOW-18")
    void whenGettingRecentSessions_thenReturnsSessions() {
        Long userId = 1L;
        int limit = 5;
        createTestChargingSession(userId);

        given()
            .auth().basic("user", "password")
            .param("limit", limit)
            .when()
            .get(baseUrl + "/recent-sessions/{userId}", userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(lessThanOrEqualTo(limit)));
    }

    @Test
    @XrayTest(key = "STATS-IT-7")
    @Requirement("SPARKFLOW-18")
    void whenCalculatingEstimatedCost_thenReturnsCost() {
        long durationMinutes = 90;

        given()
            .auth().basic("user", "password")
            .param("durationMinutes", durationMinutes)
            .when()
            .get(baseUrl + "/calculate-cost")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", greaterThan(0.0f));
    }

    @Test
    @XrayTest(key = "STATS-IT-8")
    @Requirement("SPARKFLOW-18")
    void whenCalculatingCo2Saved_thenReturnsCo2() {
        Double kwh = 25.0;

        given()
            .auth().basic("user", "password")
            .param("kwh", kwh)
            .when()
            .get(baseUrl + "/calculate-co2")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", greaterThan(0.0f));
    }

    @Test
    @XrayTest(key = "STATS-IT-9")
    @Requirement("SPARKFLOW-18")
    void whenEstimatingKwhConsumed_thenReturnsKwh() {
        long durationMinutes = 90;

        given()
            .auth().basic("user", "password")
            .param("durationMinutes", durationMinutes)
            .when()
            .get(baseUrl + "/estimate-kwh")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", greaterThan(0.0f));
    }

    @Test
    @XrayTest(key = "STATS-IT-10")
    @Requirement("SPARKFLOW-18")
    void whenGettingStatisticsWithInvalidUserId_thenReturnsBadRequest() {
        given()
            .auth().basic("user", "password")
            .when()
            .get(baseUrl + "/current-month/{userId}", "invalid")
            .then()
            .statusCode(400);
    }

    @Test
    @XrayTest(key = "STATS-IT-11")
    @Requirement("SPARKFLOW-18")
    void whenGettingWeeklyStatisticsWithInvalidDate_thenReturnsBadRequest() {
        Long userId = 1L;
        String invalidDate = "invalid-date";

        given()
            .auth().basic("user", "password")
            .param("startDate", invalidDate)
            .when()
            .get(baseUrl + "/weekly/{userId}", userId)
            .then()
            .statusCode(400);
    }

    @Test
    @XrayTest(key = "STATS-IT-12")
    @Requirement("SPARKFLOW-18")
    void whenAccessingWithoutAuthentication_thenReturnsUnauthorized() {
        Long userId = 1L;

        given()
            .when()
            .get(baseUrl + "/current-month/{userId}", userId)
            .then()
            .statusCode(401);
    }

    private void createTestChargingSession(Long userId) {
        ChargingSession session = new ChargingSession();
        session.setUserId(String.valueOf(userId));
        session.setStationId("1");
        session.setStartTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        session.setEndTime(LocalDateTime.of(2024, 1, 15, 11, 30));
        session.setFinished(true);
        chargingSessionRepository.save(session);
    }

    private void createTestBooking(Long userId) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setStationId(1L);
        booking.setStartTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        booking.setEndTime(LocalDateTime.of(2024, 1, 15, 11, 30));
        bookingRepository.save(booking);
    }
} 