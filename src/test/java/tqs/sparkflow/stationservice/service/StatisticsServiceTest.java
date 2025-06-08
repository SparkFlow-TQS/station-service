package tqs.sparkflow.stationservice.service;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.sparkflow.stationservice.dto.StatisticsDTO;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Requirement("SPARKFLOW-18")
class StatisticsServiceTest {

    @Mock
    private ChargingSessionRepository chargingSessionRepository;
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private StationRepository stationRepository;
    
    @InjectMocks
    private StatisticsServiceImpl statisticsService;
    
    private Station testStation;
    private ChargingSession testSession;
    private Booking testBooking;
    
    @BeforeEach
    void setUp() {
        testStation = new Station();
        testStation.setId(1L);
        testStation.setName("Test Station");
        testStation.setPrice(0.35);
        
        testSession = new ChargingSession();
        testSession.setId(1L);
        testSession.setStationId("1");
        testSession.setUserId("123");
        testSession.setStartTime(LocalDateTime.now().minusHours(2));
        testSession.setEndTime(LocalDateTime.now().minusHours(1));
        testSession.setFinished(true);
        
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setStationId(1L);
        testBooking.setUserId(123L);
        testBooking.setStartTime(LocalDateTime.now().minusHours(2));
        testBooking.setEndTime(LocalDateTime.now().minusHours(1));
        testBooking.setStatus(BookingStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should calculate current month statistics successfully")
    void shouldCalculateCurrentMonthStatisticsSuccessfully() {
        // Given
        Long userId = 123L;
        when(chargingSessionRepository.findFinishedSessionsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testSession));
        when(stationRepository.findById(1L))
            .thenReturn(Optional.of(testStation));
        
        // When
        StatisticsDTO.CurrentMonthStats result = statisticsService.getCurrentMonthStatistics(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalSessions());
        assertTrue(result.getTotalCost() > 0);
        assertTrue(result.getEstimatedKwh() > 0);
        assertTrue(result.getCo2Saved() > 0);
        assertTrue(result.getAvgCostPerSession() > 0);
    }

    @Test
    @DisplayName("Should return empty statistics when no sessions found")
    void shouldReturnEmptyStatisticsWhenNoSessionsFound() {
        // Given
        Long userId = 123L;
        when(chargingSessionRepository.findFinishedSessionsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList());
        
        // When
        StatisticsDTO.CurrentMonthStats result = statisticsService.getCurrentMonthStatistics(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalSessions());
        assertEquals(0.0, result.getTotalCost());
        assertEquals(0, result.getEstimatedKwh());
        assertEquals(0, result.getCo2Saved());
        assertEquals(0.0, result.getAvgCostPerSession());
    }

    @Test
    @DisplayName("Should get monthly data successfully")
    void shouldGetMonthlyDataSuccessfully() {
        // Given
        Long userId = 123L;
        int months = 3;
        when(chargingSessionRepository.findFinishedSessionsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testSession));
        when(bookingRepository.findBookingsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testBooking));
        when(stationRepository.findById(1L))
            .thenReturn(Optional.of(testStation));
        
        // When
        List<StatisticsDTO.MonthlyData> result = statisticsService.getMonthlyData(userId, months);
        
        // Then
        assertNotNull(result);
        assertEquals(months, result.size());
        
        StatisticsDTO.MonthlyData firstMonth = result.get(0);
        assertNotNull(firstMonth.getMonth());
        assertNotNull(firstMonth.getFullMonth());
        assertEquals(1, firstMonth.getSessions());
        assertTrue(firstMonth.getCost() > 0);
        assertEquals(1, firstMonth.getReservations().size());
    }

    @Test
    @DisplayName("Should get weekly data for current month successfully")
    void shouldGetWeeklyDataForCurrentMonthSuccessfully() {
        // Given
        Long userId = 123L;
        when(chargingSessionRepository.findFinishedSessionsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testSession));
        when(stationRepository.findById(1L))
            .thenReturn(Optional.of(testStation));
        
        // When
        List<StatisticsDTO.WeeklyData> result = statisticsService.getWeeklyDataCurrentMonth(userId);
        
        // Then
        assertNotNull(result);
        // Results depend on current month's sessions, could be 0 or more
    }

    @Test
    @DisplayName("Should get cost trend data successfully")
    void shouldGetCostTrendDataSuccessfully() {
        // Given
        Long userId = 123L;
        int months = 6;
        when(chargingSessionRepository.findFinishedSessionsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testSession));
        when(stationRepository.findById(1L))
            .thenReturn(Optional.of(testStation));
        
        // When
        List<StatisticsDTO.CostTrendData> result = statisticsService.getCostTrendData(userId, months);
        
        // Then
        assertNotNull(result);
        assertEquals(months, result.size());
        
        StatisticsDTO.CostTrendData firstTrend = result.get(0);
        assertNotNull(firstTrend.getMonth());
        assertTrue(firstTrend.getCost() >= 0);
        assertTrue(firstTrend.getSessions() >= 0);
    }

    @Test
    @DisplayName("Should get period details for month successfully")
    void shouldGetPeriodDetailsForMonthSuccessfully() {
        // Given
        Long userId = 123L;
        String type = "month";
        String value = "2024-01";
        when(chargingSessionRepository.findFinishedSessionsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testSession));
        when(bookingRepository.findBookingsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testBooking));
        when(stationRepository.findById(1L))
            .thenReturn(Optional.of(testStation));
        
        // When
        StatisticsDTO.PeriodDetails result = statisticsService.getPeriodDetails(userId, type, value);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalReservations());
        assertTrue(result.getTotalCost() > 0);
        assertTrue(result.getAvgCostPerSession() > 0);
        assertEquals(1, result.getReservations().size());
    }

    @Test
    @DisplayName("Should throw exception for invalid period type")
    void shouldThrowExceptionForInvalidPeriodType() {
        // Given
        Long userId = 123L;
        String type = "invalid";
        String value = "2024-01";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            statisticsService.getPeriodDetails(userId, type, value);
        });
    }

    @Test
    @DisplayName("Should use default price when station not found")
    void shouldUseDefaultPriceWhenStationNotFound() {
        // Given
        Long userId = 123L;
        when(chargingSessionRepository.findFinishedSessionsByUserInPeriod(anyString(), any(), any()))
            .thenReturn(Arrays.asList(testSession));
        when(stationRepository.findById(1L))
            .thenReturn(Optional.empty());
        
        // When
        StatisticsDTO.CurrentMonthStats result = statisticsService.getCurrentMonthStatistics(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalSessions());
        assertTrue(result.getTotalCost() > 0); // Should use default price
    }
}