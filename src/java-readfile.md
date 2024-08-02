
## 4. Java ReadFile

### Structure Java ReadFile Code
Java without maven, spring, etc. 

```
src
├── config
│   ├── config.properties                   <application configuration>
│   └── database.properties                 <database configuration>
├── input
│   └── DataAlert.txt                       <input file>
├── lib                                     <everything related code lib>
│   └── mysql-connector-java-8.0.26.jar     
├── output                                  <report output>
│   └── report_transaction_success.txt
│   └── report_system_down_MDR.txt
├── template                                <template report>
│   ├── report_environement_down.txt            
│   └── report_transaction_success.txt
├── DataAlert.java                          <Task No 3>    
├── Main.java
└── Report.java                             <Task No 4>    
```

## Task:
### Task: 1 Create a java native program to read input file on path ‘/input’

Input file in this path
```
├── input
│   └── DataAlert.txt                       <input file>
```
- Read File DataAlert.txt
```java

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class DataAlert {

    public static void run() {
        String rootDirectory = "";

        /*
         * inputFilePath : path data input file, DataAlert.txt
         * configFilePath : application configuration (ex, whitelistClient,
         * whitelistBank)
         * templateFilePath : template message for Output to Bank
         * output : result report location for Output System Down Bank
         */
        String inputFilePath = rootDirectory + "input/DataAlert.txt";
        String configFilePath = rootDirectory + "config/config.properties";
        String templateFilePath = rootDirectory + "template/report_environement_down.txt";
        String outputDirectory = rootDirectory + "output/";

        // Read whitelist bank codes from configuration file, example BNI, MDR,
        // comma separated
        List<String> whitelistBankCodes = readWhitelistBankCodes(configFilePath);

        // Read and process the input file
        List<String> lines = readFile(inputFilePath);
        Map<String, List<String>> alerts = parseData(lines, whitelistBankCodes);

        // Read the template file. ex report_environement_down.txt
        String template = readTemplate(templateFilePath);

        // Generate output messages for each whitelisted bank
        for (String bankCode : whitelistBankCodes) {
            String outputMessage = putMessageToInstitutions(bankCode, alerts, template);

            // Print to console
            System.out.println(outputMessage);

            // Write to file
            String outputFilePath = outputDirectory + "report_system_down_" + bankCode + ".txt";
            writeFile(outputFilePath, outputMessage);

            // TODO this file can be sent to External institution Channel, ex : Email,
            // Whatsapp, Telegram SMS dll
        }
    }

    // Mendapatkan list bank
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

    private static void writeFile(String filePath, String content) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

- Main Class
```java
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No Option Picked");
            return;
        }

        String processType = args[0];

        switch (processType) {
            case "dataAlert":
                DataAlert.run();
                break;
            case "transactionReport":
                Report.run();
                break;
            default:
                System.out.println("Unknown processType: " + processType);
                break;
        }
    }
}
```

_Java Code Notes_ : 
- Java Code can be improve with implement Clean Architecture
- Implemented with framework

### How to Run
1. Clone project from this repo https://github.com/kemul/jalin
2. Compile Java File
    ```
    jalin\src> javac -cp lib/mysql-connector-java-8.0.26.jar *.java
    ```
3. Build Jar File
    ```
    jalin\src> jar cfm Jalin.jar manifest.txt *.class config input template
    ```
4. Run Program 
    ```
    java -jar Jalin.jar dataAlert
    ```

### Task: 2 Create output message and send to its institutions.
Output file will perform at `console output` and `file` format with this content, this content can be sent to institution 
    
```
├── output                                  <report output>
│   └── report_system_down_MDR.txt
│   └── report_system_down_BNI.txt
```

Preview File Output
```
Selamat Siang Rekan Bank MDR,

Mohon bantuan untuk Sign on pada envi berikut :

- Envi MP Port 7120 terpantau Offline
- Envi MP Port 8066 terpantau Offline
- Envi MP Port 8081 terpantau Offline
- Envi MP Port 8064 terpantau Offline
- Envi MP Port 2079 terpantau Offline
- Envi MP Port 9191 terpantau Offline
- Envi MP Port 9511 terpantau Offline
- Envi MP Port 5222 terpantau Offline
- Envi MP Port 9097 terpantau Offline

Terima Kasih
```

### Task: 3 The program has parameter to choose whose client to be generated (in this case BNI and MDR).
whitelist client configurable in application configuration 
```
── config
│   ├── config.properties                   <application configuration>
```
Content Configuration with comma separated
```
# whitelist Bank for Notify System Down
whitelistBankCodes=MDR
```
### Task: 4 The program only run once a day by scheduler
to running by scheduler then native java, can elaborate cron job linux or windows
this the configuration for linux
```
crontab -e
```

add new cron transactionReport at 3 AM
```
0 2 * * * cd /path/to/your/application/src && java -cp .:lib/mysql-connector-java-8.0.26.jar Main dataAlert
```