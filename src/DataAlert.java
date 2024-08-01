import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class DataAlert {

    public static void run() {
        // Define the root directory
        String rootDirectory = "";

        // Define the paths to the input file, config file, and template file
        String inputFilePath = rootDirectory + "input/DataAlert.txt";
        String configFilePath = rootDirectory + "config/config.properties";
        String templateFilePath = rootDirectory + "template/report_environement_down.txt";

        // Read whitelist bank codes from configuration file, example BNI, MDR
        List<String> whitelistBankCodes = readWhitelistBankCodes(configFilePath);

        // Read and process the input file
        List<String> lines = readFile(inputFilePath);
        Map<String, List<String>> alerts = parseData(lines, whitelistBankCodes);

        // Read the template file
        String template = readTemplate(templateFilePath);

        // Generate output messages for each whitelisted bank
        for (String bankCode : whitelistBankCodes) {
            String outputMessage = putMessageToInstitutions(bankCode, alerts, template);

            // In This Case, Send Message to Institution in console output
            System.out.println(outputMessage);
        }
    }

    private static List<String> readWhitelistBankCodes(String configFilePath) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
            String bankCodes = properties.getProperty("whitelistBankCodes", "");
            return Arrays.asList(bankCodes.split(","));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<String> readFile(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static String readTemplate(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Map<String, List<String>> parseData(List<String> lines, List<String> whitelistBankCodes) {
        Map<String, List<String>> alerts = new HashMap<>();

        for (String line : lines) {
            String[] parts = line.split(";");
            String bankCode = parts[0].trim();
            if (whitelistBankCodes.contains(bankCode)) {
                String alert = String.format("- Envi MP Port %s terpantau %s", parts[2].trim(), parts[4].trim());
                // System.out.println("Alert -->: " + alert);
                alerts.computeIfAbsent(bankCode, k -> new ArrayList<>()).add(alert);
            }
        }

        return alerts;
    }

    private static String putMessageToInstitutions(String bankCode, Map<String, List<String>> alerts, String template) {
        List<String> bankAlerts = alerts.getOrDefault(bankCode, Collections.emptyList());
        String alertMessages = bankAlerts.stream()
                .collect(Collectors.joining("\n"));

        return String.format(template, bankCode, alertMessages);
    }

}
