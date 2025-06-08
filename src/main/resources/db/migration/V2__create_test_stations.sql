-- Insert test charging stations along Porto to Lisbon route
INSERT INTO stations (name, address, city, country, latitude, longitude, connector_type, status, power, is_operational, price) VALUES
('Porto Central Station', 'Rua Central 123', 'Porto', 'Portugal', 41.1579, -8.6291, 'Type 2', 'Available', 50, true, 0.25),
('Aveiro Charging Hub', 'Avenida Principal 45', 'Aveiro', 'Portugal', 40.6443, -8.6455, 'CCS', 'Available', 150, true, 0.30),
('Coimbra Supercharger', 'Rua da Universidade 78', 'Coimbra', 'Portugal', 40.2033, -8.4103, 'Tesla', 'Available', 250, true, 0.35),
('Leiria Fast Charge', 'Avenida da Liberdade 90', 'Leiria', 'Portugal', 39.7477, -8.8070, 'Type 2', 'Available', 100, true, 0.28),
('Lisbon Central Station', 'Avenida da República 150', 'Lisbon', 'Portugal', 38.7223, -9.1393, 'CCS', 'Available', 150, true, 0.30); 