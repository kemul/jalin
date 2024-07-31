CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_code VARCHAR(50),
    report_code VARCHAR(50),
    transaction_date DATE,
    transaction_time TIME,
    status VARCHAR(20),
    nominal_tagihan DOUBLE,
    admin_fee DOUBLE,
    nominal_transaksi DOUBLE
);

INSERT INTO transactions (client_code, report_code, transaction_date, transaction_time, status, nominal_tagihan, admin_fee, nominal_transaksi) VALUES
('JLN', 'ACT-01', '2023-01-01', '08:00:00', 'SUCCESS', 100000, 5000, 105000),
('JLN', 'ACT-01', '2023-01-01', '09:00:00', 'SUCCESS', 150000, 7500, 157500),
('OTHER', 'ACT-02', '2023-01-01', '10:00:00', 'SUCCESS', 200000, 10000, 210000);
