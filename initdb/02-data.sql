INSERT INTO transactions (client_code, report_code, transaction_date, transaction_time, status, nominal_tagihan, admin_fee, nominal_transaksi) VALUES
('JLN', 'ACT-01', CURDATE(), '08:00:00', 'SUCCESS', 100000, 5000, 105000),
('JLN', 'ACT-01', CURDATE(), '09:00:00', 'SUCCESS', 150000, 7500, 157500),
('OTHER', 'ACT-02', CURDATE(), '10:00:00', 'SUCCESS', 200000, 10000, 210000);
