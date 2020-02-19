package cohesion;

import cohesion.entity.Pattern;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cutoff {

    public static List<Pattern> cutPatterns(List<Pattern> patterns, double cutOffThreshold) {
        patterns.sort(Comparator.comparingInt(Pattern::getCohesionScore).thenComparing(Pattern::getSupport));
        Collections.reverse(patterns);
        int topCohesionScore = patterns.get(0).getCohesionScore();
        Function<Integer, Boolean> isScoreDropDown = score -> (100.0 - (score * 100.0 / topCohesionScore)) < cutOffThreshold;

        return patterns.stream()
                .filter(pattern -> isScoreDropDown.apply(pattern.getCohesionScore()))
                .collect(Collectors.toList());
    }
}
