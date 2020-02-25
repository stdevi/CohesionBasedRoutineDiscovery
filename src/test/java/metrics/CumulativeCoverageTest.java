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
    private List<Sequence> sequences;
    private List<Pattern> patterns;
    private Coverage coverage = new CumulativeCoverage();

    @BeforeEach
    void setUp() {
        sequences = new ArrayList<>();
        patterns = new ArrayList<>();
    }

    @Test
    void singlePattern_withoutOutliersTest() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList("A", "B", "C", "D"));
        patterns.add(pattern);

        coverage.findCoveragePerPattern(patterns, sequences);

        assertEquals(1.0, coverage.getCoveragePerPattern().get(pattern));
    }

    @Test
    void singlePattern_withOutliersTest() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "NOISE", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "NOISE", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList("A", "B", "C", "D"));
        patterns.add(pattern);

        coverage.findCoveragePerPattern(patterns, sequences);

        assertEquals((double) 8 / 10, coverage.getCoveragePerPattern().get(pattern));
    }

    @Test
    void multiplePatterns_withoutOutliersTest() {
        this.sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("C", "D")),
                new Sequence(Arrays.asList("C", "D"))
        ));

        Pattern pattern1 = new Pattern(Arrays.asList("A", "B", "C", "D"));
        Pattern pattern2 = new Pattern(Arrays.asList("C", "D"));
        patterns.addAll(Arrays.asList(pattern1, pattern2));

        coverage.findCoveragePerPattern(patterns, sequences);

        assertEquals((double) 8 / 12, coverage.getCoveragePerPattern().get(pattern1));
        assertEquals((double) 8 / 12, coverage.getCoveragePerPattern().get(pattern2));
        assertNotEquals(1.0, coverage.getCoveragePerPattern().get(pattern1) +
                coverage.getCoveragePerPattern().get(pattern2));
    }
}