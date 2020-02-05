package cohesion;

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
    private List<List<String>> itemsets;

    public void parseSequences(String fileName) {
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
    }

    public void parseItemsets(String fileName) {
        itemsets = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            itemsets = readItemsets(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemsets = getSplitedItemsets(itemsets);
    }

    private List<List<String>> readItemsets(Stream<String> stream) {
        List<List<String>> streamItemsets = new ArrayList<>();
        stream.forEach(line -> {
            List<String> itemset = new ArrayList<>();
            itemset.add(line);
            streamItemsets.add(itemset);
        });

        return streamItemsets;
    }

    private List<List<String>> getSplitedItemsets(List<List<String>> itemsets) {
        itemsets = itemsets.stream()
                .map(itemset -> new ArrayList<>(Arrays.asList(itemset.get(0)
                        .replaceAll(" -1 {2}#SUP:.*", "")
                        .split(" -1 "))))
                .collect(Collectors.toList());

        return itemsets;
    }

    public List<String> getSequences() {
        return sequences;
    }

    public List<List<String>> getItemsets() {
        return itemsets;
    }
}
