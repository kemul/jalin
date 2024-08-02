## 6 JAVA - Reading
Sample Code of Java
```java
public class ExtractData {
    private static Properties progProp = PropertiesUtil.getInstance().getProgProp();
    private static Logger log = LogUtil.getLogger(ExtractData.class.getName());

    public static void main(String[] args) {
        try {
            log.info("Start");
            String sDate = null;

            if (args.length == 1 && !args[0].trim().equalsIgnoreCase("")) {
                sDate = args[0];
            } else if (progProp.getProperty("rDate") != null) {
                sDate = progProp.getProperty("rDate");
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, -1);
                Date date = calendar.getTime();
                sDate = DateUtil.convertDateToString(date, "yyyy-MM-dd");
            }

            String rDate = sDate;

            WriteData extract = new WriteData();
            extract.generate(rDate);
        } catch (Exception e) {
            log.error("Error " + e.getMessage());
        }
    }
}
```
### Explanation of Code
The purpose of this Java code is to extract and process a date value, and then use that date to perform an operation defined in another class called WriteData.

This date can be obtained in three ways:
1. From Command-Line Arguments: If an argument is provided and it's not empty, it uses this argument as the date (sDate).
2. From Properties: If the argument is not provided, it checks a properties file for a property named rDate and uses this value if available.
3. Default to Previous Day's Date: If neither of the above conditions is met, it defaults to the previous day's date. This is achieved by creating a Calendar instance, setting it to the current date, subtracting one day, and then formatting the resulting date as a string in yyyy-MM-dd format.

The generate method of WriteData is called with the determined date (rDate) as an argument.

The last one is error handling of any Exception 

### Scenario
| Scenario | Input                                             | Process                                                                                                                         | Output                                 |
|----------|---------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| 1        | Command-line argument: `2024-08-01`               | 1. Checks if an argument is provided and is not empty. <br> 2. Sets `sDate` to `args[0]` which is `2024-08-01`.                 | `Generating data for date: 2024-08-01` |
| 2        | No command-line argument. <br> Property: `rDate=2024-07-31` | 1. No argument provided. <br> 2. Checks properties and finds `rDate` set to `2024-07-31`. <br> 3. Sets `sDate` to `2024-07-31`. | `Generating data for date: 2024-07-31` |
| 3        | No command-line argument. <br> No property `rDate` | 1. No argument provided. <br> 2. Checks properties and does not find `rDate`. <br> 3. Defaults to the previous day's date (`yyyy-MM-dd`). | `Generating data for date: 2024-08-01` |
