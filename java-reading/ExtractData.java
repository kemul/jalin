import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

public class ExtractData {
    private static Properties progProp = PropertiesUtil.getInstance().getProgProp();
    private static Logger Log = LogUtil.getLogger(ExtractData.class.getName());

    public static void main(String[] args) {
        processDate(args);
    }

    public static void processDate(String[] args) {
        try {
            Log.info("Start");
            String sDate = null;

            // Determine the date to use
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

            // Create an instance of WriteData and call the generate method
            WriteData extract = new WriteData();
            extract.generate(rDate);
        } catch (Exception e) {
            Log.severe("Error " + e.getMessage());
        }
    }
}

// Placeholder for the WriteData class
class WriteData {
    public void generate(String date) {
        // Placeholder method to simulate data writing
        System.out.println("Data generated for date: " + date);
    }
}

// Placeholder for the PropertiesUtil class
class PropertiesUtil {
    private static PropertiesUtil instance;
    private Properties progProp;

    private PropertiesUtil() {
        progProp = new Properties();
        progProp.setProperty("rDate", "2024-07-30"); // Example property
    }

    public static synchronized PropertiesUtil getInstance() {
        if (instance == null) {
            instance = new PropertiesUtil();
        }
        return instance;
    }

    public Properties getProgProp() {
        return progProp;
    }
}

// Placeholder for the LogUtil class
class LogUtil {
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
}

// Placeholder for the DateUtil class
class DateUtil {
    public static String convertDateToString(Date date, String format) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        return sdf.format(date);
    }
}
