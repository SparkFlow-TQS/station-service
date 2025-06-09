-- Define constants for common values
SET @COUNTRY = 'Portugal';
SET @STATUS = 'Available';
SET @OPERATIONAL = true;

-- Insert test charging stations along Porto to Lisbon route
INSERT INTO stations (
    name, 
    address, 
    city, 
    country, 
    latitude, 
    longitude, 
    status, 
    quantity_of_chargers,
    power,
    is_operational, 
    price,
    number_of_chargers,
    min_power,
    max_power
) VALUES
('Porto Central Station', 'Rua Central 123', 'Porto', @COUNTRY, 41.1579, -8.6291, @STATUS, 2, 50, @OPERATIONAL, 0.25, 2, 22, 50),
('Aveiro Charging Hub', 'Avenida Principal 45', 'Aveiro', @COUNTRY, 40.6443, -8.6455, @STATUS, 3, 150, @OPERATIONAL, 0.30, 3, 50, 150),
('Coimbra Supercharger', 'Rua da Universidade 78', 'Coimbra', @COUNTRY, 40.2033, -8.4103, @STATUS, 4, 250, @OPERATIONAL, 0.35, 4, 150, 250),
('Leiria Fast Charge', 'Avenida da Liberdade 90', 'Leiria', @COUNTRY, 39.7477, -8.8070, @STATUS, 2, 100, @OPERATIONAL, 0.28, 2, 50, 100),
('Lisbon Central Station', 'Avenida da República 150', 'Lisbon', @COUNTRY, 38.7223, -9.1393, @STATUS, 3, 150, @OPERATIONAL, 0.30, 3, 50, 150); 