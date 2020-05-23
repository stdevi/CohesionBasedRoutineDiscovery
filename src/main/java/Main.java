import fd.tane.utils.TaneExecutor;
import foofah.utils.FoofahExecutor;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("3 arguments requeired.");
        }
        String logFilePath = args[0];
        String foofahPath = args[1];
        String tanePath = args[2];

        FoofahExecutor.setFoofahPath(foofahPath);
        TaneExecutor.setTanePath(tanePath);

        Runner roboprom = new Runner();
        roboprom.run(logFilePath);
    }
}
