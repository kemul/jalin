package id.co.jalin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReportGenerator {

    private static String REPORT_TEMPLATE;

    public static void main(String[] args) {
        try {
            loadTemplate("templates/report_template.txt");
            System.out.println("Generating Report...");
            generateReport("JLN", "ACT-01", LocalDate.now());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadTemplate(String templatePath) throws IOException {
        try (InputStream inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IOException("Template file not found: " + templatePath);
            }
            REPORT_TEMPLATE = new String(inputStream.readAllBytes());
        }
    }

    public static void generateReport(String clientCode, String reportCode, LocalDate reportDate) throws SQLException {
        System.out.println("generateReport called with parameters:");
        System.out.println("clientCode: " + clientCode);
        System.out.println("reportCode: " + reportCode);
        System.out.println("reportDate: " + reportDate);

        String query = "SELECT * FROM transactions WHERE client_code = ? AND report_code = ? AND transaction_date = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<String> reportData = new ArrayList<>();
        int totalTransactions = 0;
        double totalNominalTagihan = 0;
        double totalAdminFee = 0;
        double totalNominalTransaksi = 0;

        try {
            connection = SimpleConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, clientCode);
            preparedStatement.setString(2, reportCode);
            preparedStatement.setDate(3, Date.valueOf(reportDate));

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String transactionDate = resultSet.getDate("transaction_date").toString().replace("-", "");
                String transactionTime = resultSet.getTime("transaction_time").toString();
                String status = resultSet.getString("status");
                double nominalTagihan = resultSet.getDouble("nominal_tagihan");
                double adminFee = resultSet.getDouble("admin_fee");
                double nominalTransaksi = resultSet.getDouble("nominal_transaksi");

                reportData.add(String.format("%-20s %-20s %-20s %-20.3f %-10.3f %-20.3f",
                        transactionDate, transactionTime, status, nominalTagihan, adminFee, nominalTransaksi));

                totalTransactions++;
                totalNominalTagihan += nominalTagihan;
                totalAdminFee += adminFee;
                totalNominalTransaksi += nominalTransaksi;
            }

            String reportBody = String.join("\n", reportData);
            String summary = String.format("                                        * * * SUMMARY TOTAL * * *\n" +
                    "                                        TOTAL TRANSAKSI         = %d\n" +
                    "                                        TOTAL NOMINAL TAGIHAN   = RP %.3f\n" +
                    "                                        TOTAL ADMIN FEE         = RP %.3f\n" +
                    "                                        TOTAL NOMINAL TRANSAKSI = RP %.3f\n",
                    totalTransactions, totalNominalTagihan, totalAdminFee, totalNominalTransaksi);

            String report = String.format(REPORT_TEMPLATE, clientCode, reportCode,
                    reportDate.toString().replace("-", ""),
                    reportBody, totalTransactions, totalNominalTagihan, totalAdminFee, totalNominalTransaksi);

            writeReport(report);
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (connection != null)
                SimpleConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    private static void writeReport(String reportContent) {
        String reportPath = "report.txt";

        try {
            Files.write(Paths.get(reportPath), reportContent.getBytes());
            System.out.println("Report generated successfully: " + reportPath);
        } catch (IOException e) {
            System.err.println("Failed to write the report: " + e.getMessage());
        }
    }
}
