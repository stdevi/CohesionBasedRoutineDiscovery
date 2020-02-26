package metrics;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CumulativeCoverageTest {
    Coverage coverage = new CumulativeCoverage();
    private List<Sequence> sequences;
    private List<Pattern> patterns;

    @BeforeEach
    void setUp() {
        sequences = new ArrayList<>();
        patterns = new ArrayList<>();
    }

    @Test
    void givenSinglePattern_withoutOutliersTest_findIndividualCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList("A", "B", "C", "D"));
        patterns.add(pattern);

        coverage.findIndividualCoveragePerPattern(patterns, sequences);

        assertEquals(1.0, coverage.getCoveragePerPattern().get(pattern));
    }

    @Test
    void givenSinglePattern_withOutliersTest_findIndividualCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "NOISE", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "NOISE", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList("A", "B", "C", "D"));
        patterns.add(pattern);

        coverage.findIndividualCoveragePerPattern(patterns, sequences);

        assertEquals((double) 8 / 10, coverage.getCoveragePerPattern().get(pattern));
    }

    @Test
    void givenMultiplePatterns_withoutOutliersTest_findIndividualCoverage() {
        this.sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("C", "D")),
                new Sequence(Arrays.asList("C", "D"))
        ));

        Pattern pattern1 = new Pattern(Arrays.asList("A", "B", "C", "D"));
        Pattern pattern2 = new Pattern(Arrays.asList("C", "D"));
        patterns.addAll(Arrays.asList(pattern1, pattern2));

        coverage.findIndividualCoveragePerPattern(patterns, sequences);

        assertEquals((double) 8 / 12, coverage.getCoveragePerPattern().get(pattern1));
        assertEquals((double) 8 / 12, coverage.getCoveragePerPattern().get(pattern2));
        assertNotEquals(1.0, coverage.getCoveragePerPattern().get(pattern1) +
                coverage.getCoveragePerPattern().get(pattern2));
    }

    @Test
    void givenSinglePattern_withoutOutliersTest_findCumulativeCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList("A", "B", "C", "D"));
        patterns.add(pattern);

        coverage.findCumulativeCoveragePerPattern(patterns, sequences);

        assertEquals(1.0, coverage.getCoveragePerPattern().get(pattern));
    }

    @Test
    void givenSinglePattern_withOutliersTest_findCumulativeCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "NOISE", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "NOISE", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList("A", "B", "C", "D"));
        patterns.add(pattern);

        coverage.findCumulativeCoveragePerPattern(patterns, sequences);

        assertEquals((double) 8 / 10, coverage.getCoveragePerPattern().get(pattern));
    }

    @Test
    void givenMultiplePatterns_withoutOutliersTest_findCumulativeCoverage() {
        this.sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("C", "D")),
                new Sequence(Arrays.asList("C", "D"))
        ));

        Pattern pattern1 = new Pattern(Arrays.asList("A", "B", "C", "D"));
        Pattern pattern2 = new Pattern(Arrays.asList("C", "D"));
        patterns.addAll(Arrays.asList(pattern1, pattern2));

        coverage.findCumulativeCoveragePerPattern(patterns, sequences);

        assertEquals((double) 8 / 12, coverage.getCoveragePerPattern().get(pattern1));
        assertEquals((double) 4 / 12, coverage.getCoveragePerPattern().get(pattern2));
        assertEquals(1.0, coverage.getCoveragePerPattern().get(pattern1) +
                coverage.getCoveragePerPattern().get(pattern2));
    }

    @Test
    void givenMultiplePatterns_withOutliersTest_findCumulativeCoverage() {
        this.sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D", "NOISE")),
                new Sequence(Arrays.asList("A", "NOISE", "B", "C", "D")),
                new Sequence(Arrays.asList("NOISE", "C", "D")),
                new Sequence(Arrays.asList("NOISE", "C", "D"))
        ));

        Pattern pattern1 = new Pattern(Arrays.asList("A", "B", "C", "D"));
        Pattern pattern2 = new Pattern(Arrays.asList("C", "D"));
        patterns.addAll(Arrays.asList(pattern1, pattern2));

        coverage.findCumulativeCoveragePerPattern(patterns, sequences);

        assertEquals((double) 8 / 16, coverage.getCoveragePerPattern().get(pattern1));
        assertEquals((double) 4 / 16, coverage.getCoveragePerPattern().get(pattern2));
        assertEquals(0.75, coverage.getCoveragePerPattern().get(pattern1) +
                coverage.getCoveragePerPattern().get(pattern2));
    }
}