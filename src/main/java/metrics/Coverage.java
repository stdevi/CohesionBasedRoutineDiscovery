package metrics;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Coverage {

    Map<Pattern, Double> coveragePerPattern = new HashMap<>();

    default void printCoverage() {
        NumberFormat formatter = new DecimalFormat("#0.000");
        System.out.println("\n" + this.getClass().getName().split("\\.")[1] + ": ");

        coveragePerPattern.forEach((pattern, coverage) ->
                System.out.println(pattern + " | coverage = " + formatter.format(coverage)));

        System.out.println("Sum pattern coverage equals: " +
                formatter.format(coveragePerPattern.values().stream().mapToDouble(Double::doubleValue).sum()));
    }

    default Map<Pattern, Double> getCoveragePerPattern() {
        return coveragePerPattern;
    }


    void findIndividualCoveragePerPattern(List<Pattern> patterns, List<Sequence> sequences);

    void findCumulativeCoveragePerPattern(List<Pattern> patterns, List<Sequence> sequences);

}
