package evaluation;

import pattern.entity.Pattern;
import pattern.entity.PatternItem;
import sequence.Sequence;
import sequence.service.SequenceService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetricService {

    private SequenceService sequenceService;

    public MetricService(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    public void printPatternsMetrics(List<Pattern> patterns) {
        List<List<String>> uniqueSequences = sequenceService.getSequences().stream()
                .map(Sequence::getItems).distinct().collect(Collectors.toList());

        int TP = 0;
        int FN = 0;

        for (int i = 0; i < patterns.size(); i++) {
            System.out.println("pattern index: " + i);
            for (int j = 0; j < uniqueSequences.size(); j++) {
                List<String> list = patterns.get(i).getItems().stream().map(PatternItem::getValue).collect(Collectors.toList());
                if (uniqueSequences.get(j).containsAll(list)) {
                    System.out.println("sequence index: " + j);
                    TP += patterns.get(i).getItems().size();
                    FN += (uniqueSequences.get(j).size() - patterns.get(i).getItems().size());
                }
            }

            System.out.println("| TP: " + TP);
            System.out.println("| FN: " + FN);
            System.out.println();
            TP = 0;
            FN = 0;
        }
    }

    public void printExternalPatternsMetrics(String path) {
        List<List<String>> patterns = readExternalPatterns(path);
        List<List<String>> uniqueSequences = sequenceService.getSequences().stream()
                .map(Sequence::getItems).distinct().collect(Collectors.toList());

        int TP = 0;
        int FN = 0;

        for (int i = 0; i < patterns.size(); i++) {
            List<String> pattern = patterns.get(i);
            System.out.println("pattern index: " + i);
            for (int j = 0; j < uniqueSequences.size(); j++) {
                List<String> sequence = uniqueSequences.get(j);
                if (fillCoveredItems(sequence, pattern).size() != 0) {
//                    System.out.println("sequence index: " + j);
                    TP += pattern.size();
                    FN += (sequence.size() - patterns.get(i).size());
                }
            }

            System.out.println("| External pattern TP: " + TP);
            System.out.println("| External pattern FN: " + FN);
            System.out.println();
            TP = 0;
            FN = 0;
        }
    }

    public double getExternalPatternsCoverage(String path) {
        List<List<String>> patterns = readExternalPatterns(path);
        List<Sequence> coverageSequences = new ArrayList<>();
        sequenceService.getSequences().forEach(sequence -> coverageSequences.add(new Sequence(sequence)));

        int totalNumberOfEvents = coverageSequences.stream().mapToInt(sequence -> sequence.getItems().size()).sum();

        return patterns.stream().mapToInt(patternItems ->
                coverageSequences.stream().mapToInt(sequence -> {
                    List<String> coveredItems = fillCoveredItems(sequence.getItems(), patternItems);
                    sequence.getItems().removeAll(coveredItems);
                    return coveredItems.size();
                }).sum())
                .mapToDouble(patternEventCoverage -> (double) patternEventCoverage / totalNumberOfEvents).sum();
    }

    private List<List<String>> readExternalPatterns(String path) {
        List<List<String>> patterns = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            stream.forEach(line -> patterns.add(Arrays.asList(line.split(" "))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return patterns;
    }

    private List<String> fillCoveredItems(List<String> sequence, List<String> pattern) {
        List<String> coveredItems = new ArrayList<>();
        for (int j = 0, i = 0; i < sequence.size() && j < pattern.size(); i++) {
            if (i == sequence.size() - 1 && j < pattern.size() - 1) {
                return Collections.emptyList();
            }
            String si = sequence.get(i);
            String pi = pattern.get(j);
            if (si.contains(pi)) {
                coveredItems.add(si);
                j++;
            }
        }

        return coveredItems;
    }
}
