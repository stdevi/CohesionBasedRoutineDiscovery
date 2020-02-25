package metrics;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;

import java.util.List;

public class IntersectionCoverage implements Coverage {

    private static void countPatternCoverage(Pattern pattern, List<Sequence> sequences, int totalNumberOfEvents) {
        int patternEventCoverage = sequences.stream().mapToInt(sequence ->
                sequence.removePatternElements(pattern).size()).sum();
        coveragePerPattern.put(pattern, (double) patternEventCoverage / totalNumberOfEvents);
    }

    public void findCoveragePerPattern(List<Pattern> patterns, List<Sequence> sequences) {
        int totalNumberOfEvents = sequences.stream().mapToInt(sequence -> sequence.getItems().size()).sum();
        patterns.forEach(pattern -> countPatternCoverage(pattern, sequences, totalNumberOfEvents));
    }
}
