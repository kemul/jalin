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
