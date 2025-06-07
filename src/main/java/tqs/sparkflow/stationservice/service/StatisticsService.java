package tqs.sparkflow.stationservice.service;

import tqs.sparkflow.stationservice.dto.StatisticsDTO;

import java.util.List;

public interface StatisticsService {
    StatisticsDTO.CurrentMonthStats getCurrentMonthStatistics(Long userId);
    List<StatisticsDTO.MonthlyData> getMonthlyData(Long userId, int months);
    List<StatisticsDTO.WeeklyData> getWeeklyDataCurrentMonth(Long userId);
    List<StatisticsDTO.CostTrendData> getCostTrendData(Long userId, int months);
    StatisticsDTO.PeriodDetails getPeriodDetails(Long userId, String type, String value);
}