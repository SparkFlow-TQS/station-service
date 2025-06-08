package tqs.sparkflow.stationservice.service;

import org.springframework.stereotype.Service;
import tqs.sparkflow.stationservice.dto.BookingDTO;
import tqs.sparkflow.stationservice.dto.StatisticsDTO;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final double DEFAULT_KWH_PER_HOUR = 7.5; // Average kWh consumption per hour
    private static final double CO2_SAVED_PER_KWH = 0.4; // kg CO2 saved per kWh (vs gasoline car)

    private final ChargingSessionRepository chargingSessionRepository;
    private final BookingRepository bookingRepository;
    private final StationRepository stationRepository;

    public StatisticsServiceImpl(ChargingSessionRepository chargingSessionRepository,
            BookingRepository bookingRepository, StationRepository stationRepository) {
        this.chargingSessionRepository = chargingSessionRepository;
        this.bookingRepository = bookingRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public StatisticsDTO.CurrentMonthStats getCurrentMonthStatistics(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0)
                .withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23)
                .withMinute(59).withSecond(59);

        // Get finished charging sessions for the current month
        List<ChargingSession> sessions = chargingSessionRepository
                .findFinishedSessionsByUserInPeriod(userId.toString(), startOfMonth, endOfMonth);

        // Calculate statistics
        StatisticsDTO.CurrentMonthStats stats = new StatisticsDTO.CurrentMonthStats();
        stats.setTotalSessions(sessions.size());

        double totalCost = 0.0;
        int totalKwh = 0;

        for (ChargingSession session : sessions) {
            if (session.getStartTime() != null && session.getEndTime() != null) {
                double duration =
                        ChronoUnit.MINUTES.between(session.getStartTime(), session.getEndTime())
                                / 60.0;
                int kwh = (int) Math.round(duration * DEFAULT_KWH_PER_HOUR);
                totalKwh += kwh;

                // Get station price
                Optional<Station> station =
                        stationRepository.findById(Long.parseLong(session.getStationId()));
                double price = station.map(Station::getPrice).orElse(0.35); // Default price per kWh
                totalCost += kwh * price;
            }
        }

        stats.setTotalCost(Math.round(totalCost * 100.0) / 100.0);
        stats.setEstimatedKwh(totalKwh);
        stats.setCo2Saved((int) Math.round(totalKwh * CO2_SAVED_PER_KWH));
        stats.setAvgCostPerSession(
                !sessions.isEmpty() ? Math.round((totalCost / sessions.size()) * 100.0) / 100.0
                        : 0.0);

        return stats;
    }

    @Override
    public List<StatisticsDTO.MonthlyData> getMonthlyData(Long userId, int months) {
        LocalDateTime now = LocalDateTime.now();
        List<StatisticsDTO.MonthlyData> monthlyData = new ArrayList<>();

        for (int i = 0; i < months; i++) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            // Get sessions for this month
            List<ChargingSession> sessions =
                    chargingSessionRepository.findFinishedSessionsByUserInPeriod(userId.toString(),
                            startOfMonth, endOfMonth);

            // Get bookings for this month
            List<Booking> bookings = bookingRepository.findBookingsByUserInPeriod(userId.toString(),
                    startOfMonth, endOfMonth);

            StatisticsDTO.MonthlyData data = new StatisticsDTO.MonthlyData();
            data.setMonth(yearMonth.format(DateTimeFormatter.ofPattern("MMM")));
            data.setFullMonth(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            data.setSessions(sessions.size());

            // Calculate cost and duration
            double totalCost = 0.0;
            double totalDuration = 0.0;
            int totalKwh = 0;

            for (ChargingSession session : sessions) {
                if (session.getStartTime() != null && session.getEndTime() != null) {
                    double duration =
                            ChronoUnit.MINUTES.between(session.getStartTime(), session.getEndTime())
                                    / 60.0;
                    totalDuration += duration;
                    int kwh = (int) Math.round(duration * DEFAULT_KWH_PER_HOUR);
                    totalKwh += kwh;

                    Optional<Station> station =
                            stationRepository.findById(Long.parseLong(session.getStationId()));
                    double price = station.map(Station::getPrice).orElse(0.35);
                    totalCost += kwh * price;
                }
            }

            data.setCost(Math.round(totalCost * 100.0) / 100.0);
            data.setDuration(Math.round(totalDuration * 100.0) / 100.0);
            data.setKwh(totalKwh);
            data.setReservations(bookings.stream().map(this::convertToBookingDTO).toList());

            monthlyData.add(data);
        }

        return monthlyData;
    }

    @Override
    public List<StatisticsDTO.WeeklyData> getWeeklyDataCurrentMonth(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0)
                .withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23)
                .withMinute(59).withSecond(59);

        // Get all sessions for current month
        List<ChargingSession> sessions = chargingSessionRepository
                .findFinishedSessionsByUserInPeriod(userId.toString(), startOfMonth, endOfMonth);

        // Group sessions by week
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<Integer, List<ChargingSession>> sessionsByWeek = sessions.stream()
                .filter(session -> session.getStartTime() != null).collect(Collectors.groupingBy(
                        session -> session.getStartTime().get(weekFields.weekOfYear())));

        List<StatisticsDTO.WeeklyData> weeklyData = new ArrayList<>();

        // Process each week
        for (Map.Entry<Integer, List<ChargingSession>> entry : sessionsByWeek.entrySet()) {
            List<ChargingSession> weekSessions = entry.getValue();

            StatisticsDTO.WeeklyData data = new StatisticsDTO.WeeklyData();
            data.setWeek("Week " + entry.getKey());
            data.setSessions(weekSessions.size());

            // Calculate cost for the week
            double totalCost = 0.0;
            for (ChargingSession session : weekSessions) {
                if (session.getStartTime() != null && session.getEndTime() != null) {
                    double duration =
                            ChronoUnit.MINUTES.between(session.getStartTime(), session.getEndTime())
                                    / 60.0;
                    int kwh = (int) Math.round(duration * DEFAULT_KWH_PER_HOUR);

                    Optional<Station> station =
                            stationRepository.findById(Long.parseLong(session.getStationId()));
                    double price = station.map(Station::getPrice).orElse(0.35);
                    totalCost += kwh * price;
                }
            }

            data.setCost(Math.round(totalCost * 100.0) / 100.0);

            // Set date range for the week
            if (!weekSessions.isEmpty()) {
                LocalDateTime firstSession =
                        weekSessions.stream().map(ChargingSession::getStartTime)
                                .min(LocalDateTime::compareTo).orElse(null);
                LocalDateTime lastSession = weekSessions.stream().map(ChargingSession::getStartTime)
                        .max(LocalDateTime::compareTo).orElse(null);

                if (firstSession != null && lastSession != null) {
                    data.setDateRange(firstSession.format(DateTimeFormatter.ofPattern("MMM d"))
                            + " - " + lastSession.format(DateTimeFormatter.ofPattern("MMM d")));
                }
            }

            data.setReservations(new ArrayList<>()); // No reservations for weekly data in this
                                                     // context
            weeklyData.add(data);
        }

        return weeklyData;
    }

    @Override
    public List<StatisticsDTO.CostTrendData> getCostTrendData(Long userId, int months) {
        LocalDateTime now = LocalDateTime.now();
        List<StatisticsDTO.CostTrendData> trendData = new ArrayList<>();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            List<ChargingSession> sessions =
                    chargingSessionRepository.findFinishedSessionsByUserInPeriod(userId.toString(),
                            startOfMonth, endOfMonth);

            double totalCost = 0.0;
            for (ChargingSession session : sessions) {
                if (session.getStartTime() != null && session.getEndTime() != null) {
                    double duration =
                            ChronoUnit.MINUTES.between(session.getStartTime(), session.getEndTime())
                                    / 60.0;
                    int kwh = (int) Math.round(duration * DEFAULT_KWH_PER_HOUR);

                    Optional<Station> station =
                            stationRepository.findById(Long.parseLong(session.getStationId()));
                    double price = station.map(Station::getPrice).orElse(0.35);
                    totalCost += kwh * price;
                }
            }

            StatisticsDTO.CostTrendData data = new StatisticsDTO.CostTrendData();
            data.setMonth(yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            data.setCost(Math.round(totalCost * 100.0) / 100.0);
            data.setSessions(sessions.size());

            trendData.add(data);
        }

        return trendData;
    }

    @Override
    public StatisticsDTO.PeriodDetails getPeriodDetails(Long userId, String type, String value) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        // Parse the period based on type and value
        if ("month".equals(type)) {
            YearMonth yearMonth =
                    YearMonth.parse(value + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        } else if ("week".equals(type)) {
            // Assume value is week number, use current year
            int weekNumber = Integer.parseInt(value);
            LocalDateTime now = LocalDateTime.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            startDate = now.with(weekFields.weekOfYear(), weekNumber)
                    .with(weekFields.dayOfWeek(), 1).withHour(0).withMinute(0).withSecond(0);
            endDate = startDate.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        } else {
            throw new IllegalArgumentException("Invalid period type: " + type);
        }

        // Get sessions and bookings for the period
        List<ChargingSession> sessions = chargingSessionRepository
                .findFinishedSessionsByUserInPeriod(userId.toString(), startDate, endDate);
        List<Booking> bookings =
                bookingRepository.findBookingsByUserInPeriod(userId.toString(), startDate, endDate);

        // Calculate statistics
        double totalCost = 0.0;
        for (ChargingSession session : sessions) {
            if (session.getStartTime() != null && session.getEndTime() != null) {
                double duration =
                        ChronoUnit.MINUTES.between(session.getStartTime(), session.getEndTime())
                                / 60.0;
                int kwh = (int) Math.round(duration * DEFAULT_KWH_PER_HOUR);

                Optional<Station> station =
                        stationRepository.findById(Long.parseLong(session.getStationId()));
                double price = station.map(Station::getPrice).orElse(0.35);
                totalCost += kwh * price;
            }
        }

        StatisticsDTO.PeriodDetails details = new StatisticsDTO.PeriodDetails();
        details.setTotalReservations(bookings.size());
        details.setTotalCost(Math.round(totalCost * 100.0) / 100.0);
        details.setAvgCostPerSession(
                !sessions.isEmpty() ? Math.round((totalCost / sessions.size()) * 100.0) / 100.0
                        : 0.0);
        details.setReservations(bookings.stream().map(this::convertToBookingDTO).toList());

        return details;
    }

    private BookingDTO convertToBookingDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setStationId(booking.getStationId());
        dto.setUserId(booking.getUserId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setDisplayStatus(booking.getStatus().toString());

        // Calculate estimated cost
        if (booking.getStartTime() != null && booking.getEndTime() != null) {
            double duration =
                    ChronoUnit.MINUTES.between(booking.getStartTime(), booking.getEndTime()) / 60.0;
            int kwh = (int) Math.round(duration * DEFAULT_KWH_PER_HOUR);

            Optional<Station> station = stationRepository.findById(booking.getStationId());
            double price = station.map(Station::getPrice).orElse(0.35);
            dto.setEstimatedCost(Math.round((kwh * price) * 100.0) / 100.0);

            // Set station name
            station.ifPresent(s -> dto.setStationName(s.getName()));
        }

        return dto;
    }
}
