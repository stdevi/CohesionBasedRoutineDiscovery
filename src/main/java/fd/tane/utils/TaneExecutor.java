package fd.tane.utils;

import fd.tane.entity.TaneDependency;
import log.entity.Event;
import pattern.entity.Pattern;
import utils.PropertyValues;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaneExecutor {
    private List<List<String>> instances;
    private Map<String, List<Event>> cases;
    private Pattern pattern;

    public TaneExecutor(Map<String, List<Event>> cases, Pattern pattern) {
        this.cases = cases;
        this.pattern = pattern;
    }

    public List<List<String>> getInstances() {
        return instances;
    }

    public void createInstancesFile() {
        this.instances = InstanceParser.getInstances(cases, pattern);
        File file = new File(PropertyValues.getProperty("fdDataOriginFilePath"));

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(getFormattedInstances(instances));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTaneDataFiles() {
        try {
            ProcessBuilder pb = new ProcessBuilder()
                    .directory(new File(PropertyValues.getProperty("taneOriginalDirPath")))
                    .command("bash", "-c", "../bin/select.perl ../descriptions/data.dsc");
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<TaneDependency> getFunctionalDependencies() {
        List<TaneDependency> functionalDependencies = new ArrayList<>();
        createFDFile();
        try (Stream<String> stream = Files.lines(Paths.get(PropertyValues.getProperty("taneDirPath") +
                "/functional_dependencies.txt"))) {
            stream.filter(line -> line.contains("->") && !line.contains("key")).forEach(line -> {
                String[] dependency = line.split("->");
                if (dependency[0].split(" ").length > 1) {
                    for (String s : dependency[0].split(" ")) {
                        int dependee = s.isEmpty() ? -1 : Integer.parseInt(s.trim()) - 1;
                        int depender = dependency[1].isEmpty() ? -1 : Integer.parseInt(dependency[1].trim()) - 1;
                        functionalDependencies.add(new TaneDependency(dependee, depender));
                    }
                } else {
                    int dependee = dependency[0].isEmpty() ? -1 : Integer.parseInt(dependency[0].trim()) - 1;
                    int depender = dependency[1].isEmpty() ? -1 : Integer.parseInt(dependency[1].trim()) - 1;
                    functionalDependencies.add(new TaneDependency(dependee, depender));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return functionalDependencies;
    }

    private void createFDFile() {
        try {
            ProcessBuilder pb = new ProcessBuilder()
                    .directory(new File(PropertyValues.getProperty("taneDirPath")))
                    .command(Arrays.asList("bash", "-c", String.format("bin/tane 20 %d %d data/data.dat > functional_dependencies.txt",
                            instances.size(), pattern.getItems().size())));
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getFormattedInstances(List<List<String>> instances) {
        StringBuilder sb = new StringBuilder();
        instances.forEach(instance -> {
            sb.append(instance.stream().collect(Collectors.joining(",", "", "")));
            sb.append("\n");
        });

        return sb.toString();
    }
}
