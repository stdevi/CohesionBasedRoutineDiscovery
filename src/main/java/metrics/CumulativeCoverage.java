package metrics;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;

import java.util.List;

public class CumulativeCoverage implements Coverage {

    private static void countIndividualPatternCoverage(Pattern pattern, List<Sequence> sequences, int totalNumberOfEvents) {
        int patternEventCoverage = sequences.stream().mapToInt(sequence ->
                sequence.containsPattern(pattern) ? pattern.getLength() : 0).sum();
        coveragePerPattern.put(pattern, (double) patternEventCoverage / totalNumberOfEvents);
    }

    private static void countCumulativePatternCoverage(Pattern pattern, List<Sequence> sequences, int totalNumberOfEvents) {
        int patternEventCoverage = sequences.stream().mapToInt(sequence ->
                sequence.removePatternElements(pattern).size()).sum();
        coveragePerPattern.put(pattern, (double) patternEventCoverage / totalNumberOfEvents);
    }

    public void findIndividualCoveragePerPattern(List<Pattern> patterns, List<Sequence> sequences) {
        int totalNumberOfEvents = sequences.stream().mapToInt(sequence -> sequence.getItems().size()).sum();
        patterns.forEach(pattern -> countIndividualPatternCoverage(pattern, sequences, totalNumberOfEvents));
    }

    public void findCumulativeCoveragePerPattern(List<Pattern> patterns, List<Sequence> sequences) {
        int totalNumberOfEvents = sequences.stream().mapToInt(sequence -> sequence.getItems().size()).sum();
        patterns.forEach(pattern -> countCumulativePatternCoverage(pattern, sequences, totalNumberOfEvents));
    }
}
