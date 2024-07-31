package id.co.jalin;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReportScheduler {

    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        try {
            Properties schedulerProperties = new Properties();
            try (var input = ReportScheduler.class.getClassLoader()
                    .getResourceAsStream("config/scheduler.properties")) {
                if (input == null) {
                    throw new IOException("Scheduler properties file not found");
                }
                schedulerProperties.load(input);
            }

            String[] clientCodes = { "JLN" };
            for (String clientCode : clientCodes) {
                scheduleClientReport(clientCode, schedulerProperties);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scheduleClientReport(String clientCode, Properties schedulerProperties) {
        String reportCode = "ACT-01"; // Example report code
        String timeString = schedulerProperties.getProperty("client." + clientCode + ".time");

        LocalTime targetTime = LocalTime.parse(timeString);
        long initialDelay = computeInitialDelay(targetTime);
        long period = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                ReportGenerator.generateReport(clientCode, reportCode, LocalDateTime.now().toLocalDate());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    private static long computeInitialDelay(LocalTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(targetTime);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).toMillis();
    }
}
