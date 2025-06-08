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
('Porto Central Station', 'Rua Central 123', 'Porto', 'Portugal', 41.1579, -8.6291, 'Available', 2, 50, true, 0.25, 2, 22, 50),
('Aveiro Charging Hub', 'Avenida Principal 45', 'Aveiro', 'Portugal', 40.6443, -8.6455, 'Available', 3, 150, true, 0.30, 3, 50, 150),
('Coimbra Supercharger', 'Rua da Universidade 78', 'Coimbra', 'Portugal', 40.2033, -8.4103, 'Available', 4, 250, true, 0.35, 4, 150, 250),
('Leiria Fast Charge', 'Avenida da Liberdade 90', 'Leiria', 'Portugal', 39.7477, -8.8070, 'Available', 2, 100, true, 0.28, 2, 50, 100),
('Lisbon Central Station', 'Avenida da República 150', 'Lisbon', 'Portugal', 38.7223, -9.1393, 'Available', 3, 150, true, 0.30, 3, 50, 150); 