package metrics;

import cohesion.entity.Pattern;
import cohesion.entity.PatternItem;
import cohesion.entity.Sequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CoverageServiceTest {
    private List<Sequence> sequences;
    private List<Pattern> patterns;
    private CoverageService coverageService;

    @BeforeEach
    void setUp() {
        coverageService = new CoverageService();
        sequences = new ArrayList<>();
        patterns = new ArrayList<>();
    }

    @Test
    void givenSinglePattern_withoutOutliersTest_findIndividualCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList(
                new PatternItem("A"),
                new PatternItem("B"),
                new PatternItem("C"),
                new PatternItem("D"))
        );
        patterns.add(pattern);

        coverageService.setPatternsIndividualCoverage(sequences, patterns);

        assertEquals(1.0, pattern.getCoverage());
    }

    @Test
    void givenSinglePattern_withOutliersTest_findIndividualCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "NOISE", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "NOISE", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList(
                new PatternItem("A"),
                new PatternItem("B"),
                new PatternItem("C"),
                new PatternItem("D"))
        );
        patterns.add(pattern);

        coverageService.setPatternsIndividualCoverage(sequences, patterns);

        assertEquals((double) 8 / 10, pattern.getCoverage());
    }

    @Test
    void givenMultiplePatterns_withoutOutliersTest_findIndividualCoverage() {
        this.sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("C", "D")),
                new Sequence(Arrays.asList("C", "D"))
        ));

        Pattern pattern1 = new Pattern(Arrays.asList(
                new PatternItem("A"),
                new PatternItem("B"),
                new PatternItem("C"),
                new PatternItem("D"))
        );
        Pattern pattern2 = new Pattern(Arrays.asList(
                new PatternItem("C"),
                new PatternItem("D"))
        );
        patterns.addAll(Arrays.asList(pattern1, pattern2));

        coverageService.setPatternsIndividualCoverage(sequences, patterns);

        assertEquals((double) 8 / 12, pattern1.getCoverage());
        assertEquals((double) 8 / 12, pattern2.getCoverage());
        assertNotEquals(1.0, pattern1.getCoverage() + pattern2.getCoverage());
    }

    @Test
    void givenSinglePattern_withoutOutliersTest_findCumulativeCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList(
                new PatternItem("A"),
                new PatternItem("B"),
                new PatternItem("C"),
                new PatternItem("D"))
        );
        patterns.add(pattern);

        coverageService.setPatternsCumulativeCoverage(sequences, patterns);

        assertEquals(1.0, pattern.getCoverage());
    }

    @Test
    void givenSinglePattern_withOutliersTest_findCumulativeCoverage() {
        sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "NOISE", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "NOISE", "C", "D"))
        ));

        Pattern pattern = new Pattern(Arrays.asList(
                new PatternItem("A"),
                new PatternItem("B"),
                new PatternItem("C"),
                new PatternItem("D"))
        );
        patterns.add(pattern);

        coverageService.setPatternsCumulativeCoverage(sequences, patterns);

        assertEquals((double) 8 / 10, pattern.getCoverage());
    }

    @Test
    void givenMultiplePatterns_withoutOutliersTest_findCumulativeCoverage() {
        this.sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("A", "B", "C", "D")),
                new Sequence(Arrays.asList("C", "D")),
                new Sequence(Arrays.asList("C", "D"))
        ));

        Pattern pattern1 = new Pattern(Arrays.asList(
                new PatternItem("A"),
                new PatternItem("B"),
                new PatternItem("C"),
                new PatternItem("D"))
        );
        Pattern pattern2 = new Pattern(Arrays.asList(
                new PatternItem("C"),
                new PatternItem("D"))
        );
        patterns.addAll(Arrays.asList(pattern1, pattern2));

        coverageService.setPatternsCumulativeCoverage(sequences, patterns);

        assertEquals((double) 8 / 12, pattern1.getCoverage());
        assertEquals((double) 4 / 12, pattern2.getCoverage());
        assertEquals(1.0, pattern1.getCoverage() + pattern2.getCoverage());
    }

    @Test
    void givenMultiplePatterns_withOutliersTest_findCumulativeCoverage() {
        this.sequences.addAll(Arrays.asList(
                new Sequence(Arrays.asList("A", "B", "C", "D", "NOISE")),
                new Sequence(Arrays.asList("A", "NOISE", "B", "C", "D")),
                new Sequence(Arrays.asList("NOISE", "C", "D")),
                new Sequence(Arrays.asList("NOISE", "C", "D"))
        ));

        Pattern pattern1 = new Pattern(Arrays.asList(
                new PatternItem("A"),
                new PatternItem("B"),
                new PatternItem("C"),
                new PatternItem("D"))
        );
        Pattern pattern2 = new Pattern(Arrays.asList(
                new PatternItem("C"),
                new PatternItem("D"))
        );
        patterns.addAll(Arrays.asList(pattern1, pattern2));

        coverageService.setPatternsCumulativeCoverage(sequences, patterns);

        assertEquals((double) 8 / 16, pattern1.getCoverage());
        assertEquals((double) 4 / 16, pattern2.getCoverage());
        assertEquals(0.75, pattern1.getCoverage() + pattern2.getCoverage());
    }
}