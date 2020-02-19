package cohesion;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CohesionScorerTest {
    private CohesionScorer scorer;
    private List<Sequence> sequences;
    private List<Pattern> patterns;

    @BeforeEach
    void setUp() {
        scorer = new CohesionScorer();
        sequences = new ArrayList<>();
        patterns = new ArrayList<>();
    }

    @Test
    void withoutOutliersTest() {
        Sequence sequence1 = new Sequence(Arrays.asList("copyCell+A", "pasteCell+B", "copyCell+C", "pasteCell+B", "click"));
        sequences.add(sequence1);
        Sequence sequence2 = new Sequence(Arrays.asList("copyCell+C", "pasteCell+B", "copyCell+A", "pasteCell+B", "click"));
        sequences.add(sequence2);

        Pattern pattern = new Pattern(Arrays.asList("copyCell+A", "copyCell+C", "pasteCell+B", "click"));
        patterns.add(pattern);

        List<Pattern> scoredPatterns = scorer.scorePattern(patterns, sequences);

        assertEquals(1, scoredPatterns.size());
        assertEquals(4, scoredPatterns.get(0).getCohesionScore());
    }

    @Test
    void withOutliersTest() {
        Sequence sequence1 = new Sequence(Arrays.asList("copyCell+A", "pasteCell+B", "someNoise", "copyCell+C", "pasteCell+B", "click"));
        sequences.add(sequence1);
        Sequence sequence2 = new Sequence(Arrays.asList("copyCell+C", "pasteCell+B", "copyCell+A", "pasteCell+B", "someNoise", "click"));
        sequences.add(sequence2);

        Pattern pattern = new Pattern(Arrays.asList("copyCell+A", "copyCell+C", "pasteCell+B", "click"));
        patterns.add(pattern);

        List<Pattern> scoredPatterns = scorer.scorePattern(patterns, sequences);

        assertEquals(1, scoredPatterns.size());
        assertEquals(3, scoredPatterns.get(0).getCohesionScore());
    }

    @Test
    void sequenceWithoutPatternTest() {
        Sequence sequence1 = new Sequence(Arrays.asList("copyCell+A", "pasteCell+B", "copyCell+C", "pasteCell+B", "click"));
        sequences.add(sequence1);
        Sequence sequence2 = new Sequence(Arrays.asList("copyCell+C", "pasteCell+B", "copyCell+A", "pasteCell+B", "click"));
        sequences.add(sequence2);

        Pattern pattern = new Pattern(Arrays.asList("unique+action1", "unique+action2"));
        patterns.add(pattern);

        List<Pattern> scoredPatterns = scorer.scorePattern(patterns, sequences);

        assertEquals(1, scoredPatterns.size());
        assertEquals(2, scoredPatterns.get(0).getCohesionScore());
    }
}