package tqs.sparkflow.stationservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StatisticsControllerTest {

    @Test
    @DisplayName("Should fail to get current month statistics")
    void shouldFailToGetCurrentMonthStatistics() {
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
    @DisplayName("Should fail to get weekly data")
    void shouldFailToGetWeeklyData() {
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