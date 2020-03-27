package cohesion;

import cohesion.entity.Pattern;
import cohesion.entity.PatternItem;
import cohesion.entity.Sequence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CohesionParser {

    public List<Sequence> parseFormattedSequences(String fileName) {
        List<String> sequences = parseRowSequences(fileName);
        sequences = removeSequencesDelimiters(sequences);
        sequences = encodeSequences(sequences);

        return formatSequences(sequences);
    }

    private List<Sequence> formatSequences(List<String> rowSequences) {
        List<Sequence> sequences = new ArrayList<>();
        int id = 1;
        for (String s : rowSequences) {
            List<String> sequence = new ArrayList<>(Arrays.asList(s.split(",")));
            sequences.add(new Sequence(id, sequence));
            id++;
        }

        return sequences;
    }

    private List<String> removeSequencesDelimiters(List<String> sequences) {
        return sequences.stream()
                .map(sequence -> sequence
                        .replace(" -1 -2", "")
                        .replace(" -1 ", ","))
                .collect(Collectors.toList());
    }

    private List<String> parseRowSequences(String fileName) {
        List<String> sequences = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(sequences::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sequences;
    }

    private List<String> encodeSequences(List<String> sequences) {
        Map<String, String> aliasesMap;
        aliasesMap = sequences.stream().filter(el -> el.startsWith("@ITEM"))
                .collect(Collectors.toMap(
                        alias -> alias.replaceAll("@ITEM=([0-9]*)=.*", "$1"),
                        alias -> alias.replaceAll("@ITEM=[0-9]*=(\\w*)", "$1"))
                );

        sequences = sequences.stream().map(sequence -> Arrays.stream(sequence.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .map(actionAlias -> aliasesMap.getOrDefault(actionAlias, actionAlias))
                .collect(Collectors.joining(","))
        ).filter(sequence -> !sequence.startsWith("@")).collect(Collectors.toList());

        return sequences;
    }

    public List<Pattern> parsePatterns(String fileName) {
        List<Pattern> patterns = new ArrayList<>();
        List<String> stringPatterns = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stringPatterns = readItemsets(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        patterns = getSplitedItemsets(stringPatterns);

        return patterns;
    }

    private List<String> readItemsets(Stream<String> stream) {
        List<String> streamPatterns = new ArrayList<>();
        stream.forEach(streamPatterns::add);

        return streamPatterns;
    }

    private List<Pattern> getSplitedItemsets(List<String> stringPatterns) {
        return stringPatterns.stream().map(patternLine -> {
            String[] itemsWithSupport = patternLine.split(" -1 ");
            int support = Integer.parseInt(itemsWithSupport[itemsWithSupport.length - 1].replace("#SUP: ", ""));
            List<PatternItem> items = new ArrayList<>();
            int index = 0;
            for (String itemValue : Arrays.asList(itemsWithSupport).subList(0, itemsWithSupport.length - 1)) {
                items.add(new PatternItem(index, itemValue));
                index++;
            }
            return new Pattern(items, support);
        }).collect(Collectors.toList());
    }
}
