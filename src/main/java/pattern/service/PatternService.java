package pattern.service;

import cohesion.service.CohesionService;
import fd.patternitem.service.ItemsDependencyService;
import foofah.service.FoofahService;
import lombok.Getter;
import pattern.entity.Pattern;
import pattern.entity.PatternItem;
import sequence.service.SequenceService;
import utils.PropertyValues;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternService {

    @Getter
    private List<Pattern> patterns;

    private SequenceService sequenceService;

    private CohesionService cohesionService;
    private ItemsDependencyService dependencyService;
    private FoofahService foofahService;

    public PatternService(SequenceService sequenceService) {
        this.sequenceService = sequenceService;

        cohesionService = new CohesionService();
        dependencyService = new ItemsDependencyService();
        foofahService = new FoofahService();
    }

    public void parsePatterns() {
        List<String> stringPatterns = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(PropertyValues.getProperty("spmfOutputFilePath")))) {
            stream.forEach(stringPatterns::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        patterns = getSplittedItemsets(stringPatterns);
    }

    public void setCohesionScore(Pattern pattern) {
        int cohesionScore = cohesionService.getCohesionScore(pattern, sequenceService.getSequences());
        pattern.setCohesionScore(cohesionScore);
    }

    public void setTransformations(Pattern pattern) {
        var cases = sequenceService.getCases();
        var transformations = foofahService.findTransformations(cases, pattern);
        pattern.setTransformations(transformations);
    }

    public void setDependencies(Pattern pattern) {
        var cases = sequenceService.getCases();
        var dependencies = dependencyService.findDependencies(cases, pattern);
        pattern.setItemsDependencies(dependencies);
    }

    public void setAutomatability(Pattern pattern) {
        pattern.getItems().forEach(item -> setItemAutomatability(pattern, item));
        if (pattern.getItems().stream().allMatch(PatternItem::isAutomatable)) {
            pattern.setAutomatable(true);
        }
    }

    public void setRAI(Pattern pattern) {
        double automatableItemsCount = (double) pattern.getItems().stream().filter(PatternItem::isAutomatable).count();
        double totalItemsCount = pattern.getItems().size();
        pattern.setRAI(automatableItemsCount / totalItemsCount);
    }

    public List<Pattern> getSortedPatternsByCohesionScore() {
        sortPatternsByCohesionScore();
        return patterns;
    }

    public List<Pattern> getCutOffPatterns(double cutOffThreshold) {
        sortPatternsByCohesionScore();
        Collections.reverse(patterns);
        int topCohesionScore = patterns.get(0).getCohesionScore();
        Function<Integer, Boolean> isScoreDropDown = score -> (100.0 - (score * 100.0 / topCohesionScore)) < cutOffThreshold;

        return patterns.stream()
                .filter(pattern -> isScoreDropDown.apply(pattern.getCohesionScore()))
                .collect(Collectors.toList());
    }

    private void sortPatternsByCohesionScore() {
        patterns.sort(Comparator.comparingInt(Pattern::getCohesionScore).thenComparing(Pattern::getSupport));
    }

    private void setItemAutomatability(Pattern pattern, PatternItem item) {
        pattern.getTransformations().entrySet().stream()
                .filter(entry -> entry.getKey().getRight().equals(item) && entry.getValue().isEmpty())
                .forEach(t -> pattern.getItemsDependencies().stream()
                        .filter(d -> d.getDepender().equals(item) && d.getDependerPerDependee().containsValue(null))
                        .forEach(d -> item.setAutomatable(false)));
    }

    private List<Pattern> getSplittedItemsets(List<String> stringPatterns) {
        return stringPatterns.stream().map(patternLine -> {
            String[] itemsWithSupport = patternLine.split(" -1 ");
            int support = Integer.parseInt(itemsWithSupport[itemsWithSupport.length - 1].replace("#SUP: ", ""));
            List<PatternItem> items = new ArrayList<>();
            int index = 0;
            for (String itemValue : Arrays.asList(itemsWithSupport).subList(0, itemsWithSupport.length - 1)) {
                items.add(new PatternItem(index, itemValue));
                index++;
            }
            return new Pattern(items, support);
        }).collect(Collectors.toList());
    }
}
