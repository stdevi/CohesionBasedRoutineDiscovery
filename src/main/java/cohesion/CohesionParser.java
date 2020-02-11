package cohesion;

import entity.Pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CohesionParser {
    private List<String> sequences;
    private List<Pattern> patterns;

    public List<String> parseSequences(String fileName) {
        sequences = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(sequences::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sequences = sequences.stream()
                .map(sequence -> sequence
                        .replace(" -1 -2", "")
                        .replace(" -1 ", ","))
                .collect(Collectors.toList());

        return sequences;
    }

    public List<Pattern> parsePatterns(String fileName) {
        patterns = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            patterns = readItemsets(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        patterns = getSplitedItemsets(patterns);

        return patterns;
    }

    private List<Pattern> readItemsets(Stream<String> stream) {
        List<Pattern> streamPatterns = new ArrayList<>();
        stream.forEach(line -> {
            Pattern pattern = new Pattern();
            pattern.add(line);
            streamPatterns.add(pattern);
        });

        return streamPatterns;
    }

    private List<Pattern> getSplitedItemsets(List<Pattern> patterns) {
        patterns = patterns.stream()
                .map(pattern -> {
                    String[] itemsWithSupport = pattern.getItems().get(0).split(" -1 ");
                    int support = Integer.parseInt(itemsWithSupport[itemsWithSupport.length - 1].replace("#SUP: ", ""));
                    List<String> items = new ArrayList<>(Arrays.asList(itemsWithSupport).subList(0, itemsWithSupport.length - 1));

                    return new Pattern(items, support);
                })
                .collect(Collectors.toList());

        return patterns;
    }

    public List<String> getSequences() {
        return sequences;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }
}
