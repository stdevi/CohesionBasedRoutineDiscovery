package metrics;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoverageService {

    public void setPatternsCumulativeCoverage(List<Sequence> sequences, List<Pattern> patterns) {
        Map<Pattern, Double> coveragePerPattern = new HashMap<>();
        List<Sequence> coverageSequences = new ArrayList<>();
        sequences.forEach(sequence -> coverageSequences.add(new Sequence(sequence)));

        int totalNumberOfEvents = coverageSequences.stream().mapToInt(sequence -> sequence.getItems().size()).sum();
        patterns.forEach(pattern -> {
            double patternCoverage = countCumulativePatternCoverage(pattern, coverageSequences, totalNumberOfEvents);
            coveragePerPattern.put(pattern, patternCoverage);
        });

        coveragePerPattern.forEach(Pattern::setCoverage);
    }

    private double countCumulativePatternCoverage(Pattern pattern, List<Sequence> sequences, int totalNumberOfEvents) {
        int patternEventCoverage = sequences.stream().mapToInt(sequence ->
                sequence.removePatternElements(pattern).size()).sum();

        return (double) patternEventCoverage / totalNumberOfEvents;
    }

    public void setPatternsIndividualCoverage(List<Sequence> sequences, List<Pattern> patterns) {
        Map<Pattern, Double> coveragePerPattern = new HashMap<>();
        int totalNumberOfEvents = sequences.stream().mapToInt(sequence -> sequence.getItems().size()).sum();

        patterns.forEach(pattern -> {
            double patternCoverage = countIndividualPatternCoverage(pattern, sequences, totalNumberOfEvents);
            coveragePerPattern.put(pattern, patternCoverage);
        });

        coveragePerPattern.forEach(Pattern::setCoverage);
    }

    private double countIndividualPatternCoverage(Pattern pattern, List<Sequence> sequences, int totalNumberOfEvents) {
        int patternEventCoverage = sequences.stream().mapToInt(sequence ->
                sequence.containsPattern(pattern) ? pattern.getItems().size() : 0).sum();
        return (double) patternEventCoverage / totalNumberOfEvents;
    }
}
