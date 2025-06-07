package tqs.sparkflow.stationservice.service;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Requirement("SPARKFLOW-18")
class StatisticsServiceTest {

    @Test
    @DisplayName("Should fail to calculate current month statistics")
    void shouldFailToCalculateCurrentMonthStatistics() {
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Not implemented yet");
        });
    }

    @Test
    @DisplayName("Should fail to get monthly data")
    void shouldFailToGetMonthlyData() {
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Not implemented yet");
        });
    }

    @Test
    @DisplayName("Should fail to get weekly data for current month")
    void shouldFailToGetWeeklyDataForCurrentMonth() {
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Not implemented yet");
        });
    }

    @Test
    @DisplayName("Should fail to get cost trend data")
    void shouldFailToGetCostTrendData() {
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Not implemented yet");
        });
    }

    @Test
    @DisplayName("Should fail to get period details")
    void shouldFailToGetPeriodDetails() {
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Not implemented yet");
        });
    }
}