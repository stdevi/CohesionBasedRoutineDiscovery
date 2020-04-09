package noise;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NoiseGenerator {
    private List<String> events;

    public NoiseGenerator() {
        events = new LinkedList<>();
    }

    public static void main(String[] args) {
        String logFile = args[0];

        NoiseGenerator noiseGenerator = new NoiseGenerator();
        noiseGenerator.readLogFile(logFile);
        noiseGenerator.addNoise(1);
        noiseGenerator.writeLogFileWithNoise();
    }

    public void readLogFile(String logFilePath) {
        try (Stream<String> stream = Files.lines(Paths.get(logFilePath))) {
            stream.forEach(events::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLogFileWithNoise() {
        Path path = Paths.get("src/main/resources/csv_logs/noisy-log.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(String.join("\n", events));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNoise(double noisePercentage) {
        IntStream.range(0, (int) ((noisePercentage / 100.0) * events.size())).forEach(i -> {
            int idx = getRandomIndex();
            String noiseEvent = getNoiseEvent(idx);
            events.add(idx, noiseEvent);
        });
    }

    private int getRandomIndex() {
        Random random = new Random();
        int max = events.size() - 1;
        int min = 0;
        return random.nextInt((max - min) + 1) + min;
    }

    private String getNoiseEvent(int idx) {
        String[] split = events.get(idx).split(",");
        String caseID = split[0];
        String timestamp = split[1];
        String userID = split[2];
        String targetApp = "\"noise\"";
        String noiseEventType = String.format("\"%s\"", RandomStringUtils.random(5, true, false));
        String noiseEventValue = String.format("\"%s\"", RandomStringUtils.random(5, true, false));

        return String.format("%s,%s,%s,%s,%s,\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",%s,\"\",\"\",\"\",\"\",\"\",\"\"",
                caseID, timestamp, userID, targetApp, noiseEventType, noiseEventValue);
    }
}
