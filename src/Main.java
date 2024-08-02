// Main.java
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No Option Picked");
            return;
        }

        String processType = args[0];

        switch (processType) {
            case "readFile":
                ReadFile.run();
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
