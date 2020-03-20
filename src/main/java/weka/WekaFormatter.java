package weka;

import cohesion.entity.Pattern;
import log.entity.Event;
import weka.entity.WekaData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WekaFormatter {

    public void createWekaFile(Map<String, List<Event>> cases, Pattern pattern) {
        WekaData wekaData = WekaParser.getWekaData(cases, pattern);

        File file = new File("C:\\Foo\\thesis\\cohesion\\CohesionMembership\\src\\main\\resources\\weka\\data.txt");
        try {
            FileWriter writer = new FileWriter(file);

            StringBuilder sb = new StringBuilder();
            sb.append("@relation").append(" data\n");
            sb.append("\n").append(getFormattedAttributes(wekaData.getValuesPerAttribute()));
            sb.append("\n@data\n").append(getFormattedInstances(wekaData.getInstances()));

            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder getFormattedInstances(List<List<String>> instances) {
        StringBuilder sb = new StringBuilder();
        instances.forEach(instance -> {
            sb.append(instance.stream().collect(Collectors.joining(",", "", "")));
            sb.append("\n");
        });

        return sb;
    }

    private StringBuilder getFormattedAttributes(Map<String, Set<String>> valuesPerAttribute) {
        StringBuilder sb = new StringBuilder();
        valuesPerAttribute.entrySet().stream()
                .filter(entry -> !entry.getValue().contains(null))
                .forEach(entry -> sb.append("@attribute ")
                        .append(entry.getKey())
                        .append(entry.getValue().stream().collect(Collectors.joining(",", " {", "}\n"))));

        return sb;
    }
}
