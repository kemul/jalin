
// Report.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

public class Report {

    public static void run() {
        /*
         * database : config related to database connection
         * template : template message for notify
         * output : path result report location
         */ String configFilePath = "config/database.properties";
        String formatFilePath = "template/report_transaction_success.txt";
        String outputFilePath = "output/report_transaction_success.txt";

        // Load the Config file
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
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

        // Get the database connection details from the properties file
        String dbUrl = properties.getProperty("db.url");
        String dbUser = properties.getProperty("db.user");
        String dbPassword = properties.getProperty("db.password");

        try {
            // Establish connection to the database
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Create a statement object to perform a query
            Statement stmt = conn.createStatement();

            // Execute a query and get a result set
            String query = "SELECT * FROM transactions";
            ResultSet rs = stmt.executeQuery(query);

            // Variables to calculate summary totals
            int totalTransactions = 0;
            double totalNominalTagihan = 0;
            double totalAdminFee = 0;
            double totalNominalTransaksi = 0;

            // StringBuilder to accumulate transaction details
            StringBuilder transactionDetails = new StringBuilder();

            // Iterate through the result set and accumulate the results
            while (rs.next()) {
                String transactionDate = formatDate(rs.getDate("transaction_date"));
                String transactionTime = rs.getTime("transaction_time").toString();
                String status = translateStatus(rs.getString("status"));
                double nominalTagihan = rs.getDouble("nominal_tagihan");
                double adminFee = rs.getDouble("admin_fee");
                double nominalTransaksi = rs.getDouble("nominal_transaksi");

                // Accumulate transaction details
                transactionDetails.append(String.format("%-20s %-20s %-20s %-20s %-10s %-20s\n",
                        transactionDate, transactionTime, status,
                        formatCurrency(nominalTagihan), formatCurrency(adminFee), formatCurrency(nominalTransaksi)));

                // Update summary totals
                totalTransactions++;
                totalNominalTagihan += nominalTagihan;
                totalAdminFee += adminFee;
                totalNominalTransaksi += nominalTransaksi;
            }

            // Close the result set, statement, and connection
            rs.close();
            stmt.close();
            conn.close();

            // Generate the formatted report using the loaded format
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
