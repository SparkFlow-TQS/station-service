package tqs.sparkflow.stationservice.service;

import org.springframework.stereotype.Service;
import tqs.sparkflow.stationservice.dto.StatisticsDTO;

import java.util.Collections;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public StatisticsDTO.CurrentMonthStats getCurrentMonthStatistics(Long userId) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<StatisticsDTO.MonthlyData> getMonthlyData(Long userId, int months) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<StatisticsDTO.WeeklyData> getWeeklyDataCurrentMonth(Long userId) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<StatisticsDTO.CostTrendData> getCostTrendData(Long userId, int months) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public StatisticsDTO.PeriodDetails getPeriodDetails(Long userId, String type, String value) {
        throw new RuntimeException("Not implemented yet");
    }
}