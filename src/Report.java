import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Report {

    public static void run() {
        /*
         * configFilePath : application configuration (ex, whitelistClient,
         * whitelistBank)
         * databaseConfigFilePath : MySql DB Configuration
         * template : template message for Report Client
         * output : result report location for report Client
         */
        String configFilePath = "config/config.properties";
        String databaseConfigFilePath = "config/database.properties";
        String formatFilePath = "template/report_transaction_success.txt";
        String outputFilePath = "output/report_transaction_success.txt";

        // Load application config file
        Properties appProperties = new Properties();
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            appProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Load database config file
        Properties dbProperties = new Properties();
        try (FileInputStream input = new FileInputStream(databaseConfigFilePath)) {
            dbProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Load the format from the format file
        String format;
        try {
            format = new String(Files.readAllBytes(Paths.get(formatFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Whitelist client to be generated , comma separated
        String whitelistClient = appProperties.getProperty("whitelistClient");
        List<String> clientCodes = Arrays.asList(whitelistClient.split(","));

        // Get the database connection details from the database config file
        String dbUrl = dbProperties.getProperty("db.url");
        String dbUser = dbProperties.getProperty("db.user");
        String dbPassword = dbProperties.getProperty("db.password");

        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            Statement stmt = conn.createStatement();

            // Fetch only whitelist by configuration
            // Limit can be improve depend on requirement report and Data Size
            String clientCodesInClause = clientCodes.stream()
                    .map(code -> "'" + code + "'")
                    .collect(Collectors.joining(", "));
            String query = "SELECT * FROM transactions WHERE client_code IN (" + clientCodesInClause + ") and status = "SUCCESS" LIMIT 10";
            ResultSet rs = stmt.executeQuery(query);

            // Kalkulasi totals
            int totalTransactions = 0;
            double totalNominalTagihan = 0;
            double totalAdminFee = 0;
            double totalNominalTransaksi = 0;

            // Get Transaction details
            StringBuilder transactionDetails = new StringBuilder();

            while (rs.next()) {
                String transactionDate = formatDate(rs.getDate("transaction_date"));
                String transactionTime = rs.getTime("transaction_time").toString();
                String status = translateStatus(rs.getString("status"));
                double nominalTagihan = rs.getDouble("nominal_tagihan");
                double adminFee = rs.getDouble("admin_fee");
                double nominalTransaksi = rs.getDouble("nominal_transaksi");

                transactionDetails.append(String.format("%-20s %-20s %-20s %-20s %-10s %-20s\n",
                        transactionDate, transactionTime, status,
                        formatCurrency(nominalTagihan), formatCurrency(adminFee), formatCurrency(nominalTransaksi)));

                totalTransactions++;
                totalNominalTagihan += nominalTagihan;
                totalAdminFee += adminFee;
                totalNominalTransaksi += nominalTransaksi;
            }

            rs.close();
            stmt.close();
            conn.close();

            // Generate report with formated template
            String reportContent = String.format(
                    format,
                    "JLN", "1", "ACT-01", "2023-08-01", transactionDetails.toString(),
                    totalTransactions, formatCurrency(totalNominalTagihan), formatCurrency(totalAdminFee),
                    formatCurrency(totalNominalTransaksi));

            // Write the report to the output file
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                writer.write(reportContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatCurrency(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###.000", symbols);
        return df.format(value);
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        return sdf.format(date);
    }

    private static String translateStatus(String status) {
        if ("SUCCESS".equals(status)) {
            return "BERHASIL";
        } else if ("FAILED".equals(status)) {
            return "GAGAL";
        } else {
            return status;
        }
    }
}
