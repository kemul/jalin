package id.co.jalin;

import java.io.IOException;

public class MainLauncher {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the main class to run: ReportScheduler or ReportGenerator");
            return;
        }

        String mainClass = args[0];
        switch (mainClass) {
            case "ReportScheduler":
                try {
                    ReportScheduler.main(new String[] {});
                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case "ReportGenerator":
                ReportGenerator.main(new String[] {});
                break;
            default:
                System.out.println("Unknown main class: " + mainClass);
                System.out.println("Please provide the main class to run: ReportScheduler or ReportGenerator");
                break;
        }
    }
}
