-- This is an empty migration to initialize the database
-- Add your table creation statements here when needed 

-- Create stations table
CREATE TABLE stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    connector_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    power INT NOT NULL,
    is_operational BOOLEAN NOT NULL,
    price DOUBLE NOT NULL
); 