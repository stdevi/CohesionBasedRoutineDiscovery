package cohesion.service;

import cohesion.entity.ItemsDependency;
import cohesion.entity.Pattern;
import cohesion.entity.PatternItem;
import fd.entity.TaneDependency;
import fd.service.TaneService;
import log.entity.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemsDependencyService {

    private TaneService taneService;

    public ItemsDependencyService() {
        taneService = new TaneService();
    }

    public List<ItemsDependency> findDependencies(Map<String, List<Event>> cases, Pattern pattern) {
        List<ItemsDependency> itemsDependencies = new ArrayList<>();
        List<TaneDependency> functionalDependencies = taneService.getFunctionalDependencies(cases, pattern);
        var emptyTransformationPairs = pattern.getEmptyTransformationPairs();

        emptyTransformationPairs.forEach(pair -> {
            int dependerIdx = pair.getRight().getIndex();
            var dependeeValuesPerDepender = taneService.getDependeeValuesPerDepender(dependerIdx, functionalDependencies);
            List<Integer> dependeeIndices = taneService.getDependeeByDepender(functionalDependencies, dependerIdx);
            List<PatternItem> dependee = dependeeIndices.contains(-1) ? null : dependeeIndices.stream()
                    .map(idx -> pattern.getItems().get(idx)).collect(Collectors.toList());
            itemsDependencies.add(getItemsDependency(pair.getRight(), dependee, dependeeValuesPerDepender));
        });

        return itemsDependencies;
    }

    private ItemsDependency getItemsDependency(PatternItem depender,
                                               List<PatternItem> dependee,
                                               Map<String, List<List<String>>> dependeeValuesPerDepender) {
        ItemsDependency dependency = new ItemsDependency();
        dependency.setDepender(depender);
        dependency.setDependee(dependee);
        dependency.setDependeeValuesPerDepender(dependeeValuesPerDepender);

        return dependency;
    }
}
