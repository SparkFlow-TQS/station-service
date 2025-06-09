package tqs.sparkflow.stationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tqs.sparkflow.stationservice.dto.StatisticsDTO;
import tqs.sparkflow.stationservice.service.StatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Statistics API for user charging data")
public class StatisticsController {

  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/current-month")
  @Operation(summary = "Get current month statistics",
      description = "Returns current month charging statistics for a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = StatisticsDTO.CurrentMonthStats.class))),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")})
  public ResponseEntity<StatisticsDTO.CurrentMonthStats> getCurrentMonthStatistics(
      @Parameter(description = "User ID", required = true) @RequestParam Long userId) {
    StatisticsDTO.CurrentMonthStats stats = statisticsService.getCurrentMonthStatistics(userId);
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/monthly")
  @Operation(summary = "Get monthly data",
      description = "Returns monthly charging data for a specified number of months")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Monthly data retrieved successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid months parameter"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  public ResponseEntity<List<StatisticsDTO.MonthlyData>> getMonthlyData(
      @Parameter(description = "User ID", required = true) @RequestParam Long userId,
      @Parameter(description = "Number of months to retrieve",
          example = "12") @RequestParam(defaultValue = "12") int months) {
    List<StatisticsDTO.MonthlyData> data = statisticsService.getMonthlyData(userId, months);
    return ResponseEntity.ok(data);
  }

  @GetMapping("/weekly-current-month")
  @Operation(summary = "Get weekly data for current month",
      description = "Returns weekly charging data for the current month")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Weekly data retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  public ResponseEntity<List<StatisticsDTO.WeeklyData>> getWeeklyDataCurrentMonth(
      @Parameter(description = "User ID", required = true) @RequestParam Long userId) {
    List<StatisticsDTO.WeeklyData> data = statisticsService.getWeeklyDataCurrentMonth(userId);
    return ResponseEntity.ok(data);
  }

  @GetMapping("/cost-trend")
  @Operation(summary = "Get cost trend data",
      description = "Returns cost trend data over a specified number of months")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cost trend data retrieved successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid months parameter"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  public ResponseEntity<List<StatisticsDTO.CostTrendData>> getCostTrendData(
      @Parameter(description = "User ID", required = true) @RequestParam Long userId,
      @Parameter(description = "Number of months for trend analysis",
          example = "8") @RequestParam(defaultValue = "8") int months) {
    List<StatisticsDTO.CostTrendData> data = statisticsService.getCostTrendData(userId, months);
    return ResponseEntity.ok(data);
  }

  @GetMapping("/period-details")
  @Operation(summary = "Get period details",
      description = "Returns detailed statistics for a specific period (month or week)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Period details retrieved successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid period type or value"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  public ResponseEntity<StatisticsDTO.PeriodDetails> getPeriodDetails(
      @Parameter(description = "User ID", required = true) @RequestParam Long userId,
      @Parameter(description = "Period type (month or week)",
          example = "month") @RequestParam String type,
      @Parameter(description = "Period value (e.g., '2024-01' for month, '1' for week)",
          example = "2024-01") @RequestParam String value) {
    StatisticsDTO.PeriodDetails details = statisticsService.getPeriodDetails(userId, type, value);
    return ResponseEntity.ok(details);
  }
}
