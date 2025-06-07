package tqs.sparkflow.stationservice.controller;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.stationservice.dto.StatisticsDTO;
import tqs.sparkflow.stationservice.service.StatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Requirement("SPARKFLOW-18")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/current-month")
    public ResponseEntity<StatisticsDTO.CurrentMonthStats> getCurrentMonthStatistics(@RequestParam Long userId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<StatisticsDTO.MonthlyData>> getMonthlyData(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "12") int months) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/weekly-current-month")
    public ResponseEntity<List<StatisticsDTO.WeeklyData>> getWeeklyDataCurrentMonth(@RequestParam Long userId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/cost-trend")
    public ResponseEntity<List<StatisticsDTO.CostTrendData>> getCostTrendData(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "8") int months) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/period-details")
    public ResponseEntity<StatisticsDTO.PeriodDetails> getPeriodDetails(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam String value) {
        throw new RuntimeException("Not implemented yet");
    }
}