package cohesion.service;

import cohesion.entity.Pattern;
import cohesion.entity.PatternItem;
import foofah.FoofahService;
import log.entity.Event;

import java.util.List;
import java.util.Map;

public class PatternService {

    private ItemsDependencyService dependencyService;
    private FoofahService foofahService;

    public PatternService() {
        dependencyService = new ItemsDependencyService();
        foofahService = new FoofahService();
    }

    public void setTransformations(Map<String, List<Event>> cases, Pattern pattern) {
        var transformations = foofahService.findTransformations(cases, pattern);
        pattern.setTransformations(transformations);
    }

    public void setDependencies(Map<String, List<Event>> cases, Pattern pattern) {
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

    private void setItemAutomatability(Pattern pattern, PatternItem item) {
        pattern.getTransformations().entrySet().stream()
                .filter(entry -> entry.getKey().getRight().equals(item) && entry.getValue().isEmpty())
                .forEach(t -> pattern.getItemsDependencies().stream()
                        .filter(d -> d.getDepender().equals(item) && d.getDependerPerDependeeValues().containsValue(null))
                        .forEach(d -> item.setAutomatable(false)));
    }
}
