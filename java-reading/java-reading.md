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

Case 1
---
Input
```
java ExtractData
```

Output
```terminal
Aug 01, 2024 4:17:27 PM ExtractData processDate
INFO: Start
Data generated for date: 2024-07-30
```

Case 2
---
Input
```
java ExtractData "2024-08-01"
```

Output
```terminal
Aug 01, 2024 4:15:28 PM ExtractData processDate
INFO: Start
Data generated for date: 2024-08-01
```
