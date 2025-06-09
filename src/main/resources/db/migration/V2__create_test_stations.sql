-- Insert test charging stations along Porto to Lisbon route
WITH constants AS (
    SELECT 
        'Portugal' as country,
        'Available' as status,
        true as operational
)
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
)
SELECT 
    name,
    address,
    city,
    c.country,
    latitude,
    longitude,
    c.status,
    quantity_of_chargers,
    power,
    c.operational,
    price,
    number_of_chargers,
    min_power,
    max_power
FROM constants c
CROSS JOIN (VALUES
    ('Porto Central Station', 'Rua Central 123', 'Porto', 41.1579, -8.6291, 2, 50, 0.25, 2, 22, 50),
    ('Aveiro Charging Hub', 'Avenida Principal 45', 'Aveiro', 40.6443, -8.6455, 3, 150, 0.30, 3, 50, 150),
    ('Coimbra Supercharger', 'Rua da Universidade 78', 'Coimbra', 40.2033, -8.4103, 4, 250, 0.35, 4, 150, 250),
    ('Leiria Fast Charge', 'Avenida da Liberdade 90', 'Leiria', 39.7477, -8.8070, 2, 100, 0.28, 2, 50, 100),
    ('Lisbon Central Station', 'Avenida da República 150', 'Lisbon', 38.7223, -9.1393, 3, 150, 0.30, 3, 50, 150)
) AS stations_data (
    name, address, city, latitude, longitude, 
    quantity_of_chargers, power, price, number_of_chargers, min_power, max_power
); 