// Main.java
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <processType>");
            System.out.println("processType: dataAlert or transactionReport");
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
                System.out.println("Usage: java Main <processType>");
                System.out.println("processType: dataAlert or transactionReport");
                break;
        }
    }
}
