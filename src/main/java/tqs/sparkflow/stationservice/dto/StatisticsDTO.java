package tqs.sparkflow.stationservice.dto;

import java.util.List;

public class StatisticsDTO {

    private StatisticsDTO() {
        // Private constructor to prevent instantiation
    }

    public static class CurrentMonthStats {
        private double totalCost;
        private int estimatedKwh;
        private int totalSessions;
        private int co2Saved;
        private double avgCostPerSession;

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }

        public int getEstimatedKwh() {
            return estimatedKwh;
        }

        public void setEstimatedKwh(int estimatedKwh) {
            this.estimatedKwh = estimatedKwh;
        }

        public int getTotalSessions() {
            return totalSessions;
        }

        public void setTotalSessions(int totalSessions) {
            this.totalSessions = totalSessions;
        }

        public int getCo2Saved() {
            return co2Saved;
        }

        public void setCo2Saved(int co2Saved) {
            this.co2Saved = co2Saved;
        }

        public double getAvgCostPerSession() {
            return avgCostPerSession;
        }

        public void setAvgCostPerSession(double avgCostPerSession) {
            this.avgCostPerSession = avgCostPerSession;
        }
    }

    public static class MonthlyData {
        private String month;
        private String fullMonth;
        private double cost;
        private int sessions;
        private double duration;
        private int kwh;
        private List<BookingDTO> reservations;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getFullMonth() {
            return fullMonth;
        }

        public void setFullMonth(String fullMonth) {
            this.fullMonth = fullMonth;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public int getSessions() {
            return sessions;
        }

        public void setSessions(int sessions) {
            this.sessions = sessions;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public int getKwh() {
            return kwh;
        }

        public void setKwh(int kwh) {
            this.kwh = kwh;
        }

        public List<BookingDTO> getReservations() {
            return reservations;
        }

        public void setReservations(List<BookingDTO> reservations) {
            this.reservations = reservations;
        }
    }

    public static class WeeklyData {
        private String week;
        private int sessions;
        private double cost;
        private String dateRange;
        private List<BookingDTO> reservations;

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public int getSessions() {
            return sessions;
        }

        public void setSessions(int sessions) {
            this.sessions = sessions;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public String getDateRange() {
            return dateRange;
        }

        public void setDateRange(String dateRange) {
            this.dateRange = dateRange;
        }

        public List<BookingDTO> getReservations() {
            return reservations;
        }

        public void setReservations(List<BookingDTO> reservations) {
            this.reservations = reservations;
        }
    }

    public static class CostTrendData {
        private String month;
        private double cost;
        private int sessions;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public int getSessions() {
            return sessions;
        }

        public void setSessions(int sessions) {
            this.sessions = sessions;
        }
    }

    public static class PeriodDetails {
        private int totalReservations;
        private double totalCost;
        private double avgCostPerSession;
        private List<BookingDTO> reservations;

        public int getTotalReservations() {
            return totalReservations;
        }

        public void setTotalReservations(int totalReservations) {
            this.totalReservations = totalReservations;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }

        public double getAvgCostPerSession() {
            return avgCostPerSession;
        }

        public void setAvgCostPerSession(double avgCostPerSession) {
            this.avgCostPerSession = avgCostPerSession;
        }

        public List<BookingDTO> getReservations() {
            return reservations;
        }

        public void setReservations(List<BookingDTO> reservations) {
            this.reservations = reservations;
        }
    }
}
