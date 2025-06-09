package tqs.sparkflow.stationservice.controller;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqs.sparkflow.stationservice.dto.StatisticsDTO;
import tqs.sparkflow.stationservice.service.StatisticsService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Requirement("SPARKFLOW-18")
class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    private StatisticsDTO.CurrentMonthStats currentMonthStats;
    private List<StatisticsDTO.MonthlyData> monthlyData;
    private List<StatisticsDTO.WeeklyData> weeklyData;
    private List<StatisticsDTO.CostTrendData> costTrendData;
    private StatisticsDTO.PeriodDetails periodDetails;

    @BeforeEach
    void setUp() {
        currentMonthStats = new StatisticsDTO.CurrentMonthStats();
        currentMonthStats.setTotalCost(45.50);
        currentMonthStats.setEstimatedKwh(130);
        currentMonthStats.setTotalSessions(15);
        currentMonthStats.setCo2Saved(52);
        currentMonthStats.setAvgCostPerSession(3.03);

        StatisticsDTO.MonthlyData monthData = new StatisticsDTO.MonthlyData();
        monthData.setMonth("Jan");
        monthData.setFullMonth("January 2024");
        monthData.setCost(45.50);
        monthData.setSessions(15);
        monthData.setDuration(20.5);
        monthData.setKwh(130);
        monthlyData = Arrays.asList(monthData);

        StatisticsDTO.WeeklyData weekData = new StatisticsDTO.WeeklyData();
        weekData.setWeek("Week 1");
        weekData.setSessions(5);
        weekData.setCost(15.25);
        weekData.setDateRange("Jan 1 - Jan 7");
        weeklyData = Arrays.asList(weekData);

        StatisticsDTO.CostTrendData trendData = new StatisticsDTO.CostTrendData();
        trendData.setMonth("Jan 2024");
        trendData.setCost(45.50);
        trendData.setSessions(15);
        costTrendData = Arrays.asList(trendData);

        periodDetails = new StatisticsDTO.PeriodDetails();
        periodDetails.setTotalReservations(10);
        periodDetails.setTotalCost(35.75);
        periodDetails.setAvgCostPerSession(3.58);
    }

    @Test
    @DisplayName("Should get current month statistics successfully")
    void shouldGetCurrentMonthStatisticsSuccessfully() {
        // Given
        Long userId = 123L;
        when(statisticsService.getCurrentMonthStatistics(userId)).thenReturn(currentMonthStats);

        // When
        ResponseEntity<StatisticsDTO.CurrentMonthStats> response =
                statisticsController.getCurrentMonthStatistics(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(45.50, response.getBody().getTotalCost());
        assertEquals(130, response.getBody().getEstimatedKwh());
        assertEquals(15, response.getBody().getTotalSessions());
        assertEquals(52, response.getBody().getCo2Saved());
        assertEquals(3.03, response.getBody().getAvgCostPerSession());
    }

    @Test
    @DisplayName("Should get monthly data successfully")
    void shouldGetMonthlyDataSuccessfully() {
        // Given
        Long userId = 123L;
        int months = 12;
        when(statisticsService.getMonthlyData(userId, months)).thenReturn(monthlyData);

        // When
        ResponseEntity<List<StatisticsDTO.MonthlyData>> response =
                statisticsController.getMonthlyData(userId, months);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Jan", response.getBody().get(0).getMonth());
        assertEquals("January 2024", response.getBody().get(0).getFullMonth());
        assertEquals(45.50, response.getBody().get(0).getCost());
        assertEquals(15, response.getBody().get(0).getSessions());
    }

    @Test
    @DisplayName("Should get monthly data with default months parameter")
    void shouldGetMonthlyDataWithDefaultMonthsParameter() {
        // Given
        Long userId = 123L;
        when(statisticsService.getMonthlyData(userId, 12)).thenReturn(monthlyData);

        // When
        ResponseEntity<List<StatisticsDTO.MonthlyData>> response =
                statisticsController.getMonthlyData(userId, 12);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should get weekly data for current month successfully")
    void shouldGetWeeklyDataForCurrentMonthSuccessfully() {
        // Given
        Long userId = 123L;
        when(statisticsService.getWeeklyDataCurrentMonth(userId)).thenReturn(weeklyData);

        // When
        ResponseEntity<List<StatisticsDTO.WeeklyData>> response =
                statisticsController.getWeeklyDataCurrentMonth(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Week 1", response.getBody().get(0).getWeek());
        assertEquals(5, response.getBody().get(0).getSessions());
        assertEquals(15.25, response.getBody().get(0).getCost());
    }

    @Test
    @DisplayName("Should get cost trend data successfully")
    void shouldGetCostTrendDataSuccessfully() {
        // Given
        Long userId = 123L;
        int months = 8;
        when(statisticsService.getCostTrendData(userId, months)).thenReturn(costTrendData);

        // When
        ResponseEntity<List<StatisticsDTO.CostTrendData>> response =
                statisticsController.getCostTrendData(userId, months);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Jan 2024", response.getBody().get(0).getMonth());
        assertEquals(45.50, response.getBody().get(0).getCost());
        assertEquals(15, response.getBody().get(0).getSessions());
    }

    @Test
    @DisplayName("Should get cost trend data with default months parameter")
    void shouldGetCostTrendDataWithDefaultMonthsParameter() {
        // Given
        Long userId = 123L;
        when(statisticsService.getCostTrendData(userId, 8)).thenReturn(costTrendData);

        // When
        ResponseEntity<List<StatisticsDTO.CostTrendData>> response =
                statisticsController.getCostTrendData(userId, 8);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should get period details successfully")
    void shouldGetPeriodDetailsSuccessfully() {
        // Given
        Long userId = 123L;
        String type = "month";
        String value = "2024-01";
        when(statisticsService.getPeriodDetails(userId, type, value)).thenReturn(periodDetails);

        // When
        ResponseEntity<StatisticsDTO.PeriodDetails> response =
                statisticsController.getPeriodDetails(userId, type, value);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getTotalReservations());
        assertEquals(35.75, response.getBody().getTotalCost());
        assertEquals(3.58, response.getBody().getAvgCostPerSession());
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() {
        // Given
        Long userId = 123L;
        when(statisticsService.getCurrentMonthStatistics(userId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            statisticsController.getCurrentMonthStatistics(userId);
        });
    }
}
