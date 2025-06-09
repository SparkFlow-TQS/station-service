-- Database initialization script for SparkFlow Station Service
-- Creates all necessary tables for the charging station management system

-- Create stations table
CREATE TABLE stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL CHECK (latitude >= -90 AND latitude <= 90),
    longitude DOUBLE NOT NULL CHECK (longitude >= -180 AND longitude <= 180),
    status VARCHAR(50) NOT NULL,
    quantity_of_chargers INTEGER NOT NULL CHECK (quantity_of_chargers >= 1),
    power INTEGER,
    is_operational BOOLEAN,
    price DOUBLE CHECK (price >= 0),
    number_of_chargers INTEGER,
    min_power INTEGER,
    max_power INTEGER
);

-- Create bookings table
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status ENUM('ACTIVE', 'CANCELLED', 'COMPLETED') NOT NULL
);

-- Create booking_recurring_days table for ElementCollection
CREATE TABLE booking_recurring_days (
    booking_id BIGINT NOT NULL,
    day_of_week INTEGER,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- Create charging_sessions table
CREATE TABLE charging_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id VARCHAR(255),
    user_id VARCHAR(255),
    finished BOOLEAN NOT NULL DEFAULT FALSE,
    start_time DATETIME,
    end_time DATETIME
);

-- Create indexes for better performance
CREATE INDEX idx_stations_location ON stations(latitude, longitude);
CREATE INDEX idx_stations_status ON stations(status);
CREATE INDEX idx_stations_city ON stations(city);
CREATE INDEX idx_bookings_station_id ON bookings(station_id);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_start_time ON bookings(start_time);
CREATE INDEX idx_charging_sessions_station_id ON charging_sessions(station_id);
CREATE INDEX idx_charging_sessions_user_id ON charging_sessions(user_id);
