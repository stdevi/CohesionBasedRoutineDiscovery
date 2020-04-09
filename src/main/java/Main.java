public class Main {

    public static void main(String[] args) {
        String logFilePath = args[0];

        Runner roboprom = new Runner();
        roboprom.run(logFilePath);
    }
}
