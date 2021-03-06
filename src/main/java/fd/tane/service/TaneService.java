package fd.tane.service;

import fd.tane.entity.TaneDependency;
import fd.tane.utils.TaneExecutor;
import log.entity.Event;
import pattern.entity.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaneService {
    private static final TaneService INSTANCE = new TaneService();
    private List<List<String>> instances;

    private TaneService() {

    }

    public static TaneService getInstance() {
        return INSTANCE;
    }

    public List<TaneDependency> getFunctionalDependencies(Map<String, List<Event>> cases, Pattern pattern) {
        TaneExecutor taneExecutor = new TaneExecutor(cases, pattern);
        taneExecutor.createInstancesFile();
        taneExecutor.createTaneDataFiles();
        this.instances = taneExecutor.getInstances();

        return taneExecutor.getFunctionalDependencies();
    }

    public Map<String, List<List<String>>> getDependeeValuesPerDepender(Integer dependerIdx,
                                                                        List<TaneDependency> functionalDependencies) {
        Map<String, List<List<String>>> dependeeValuesPerDepender = new HashMap<>();
        List<Integer> dependee = getDependeeByDepender(functionalDependencies, dependerIdx);

        instances.forEach(instance -> {
            String dependerValue = instance.get(dependerIdx);
            dependeeValuesPerDepender.putIfAbsent(dependerValue, new ArrayList<>());
            List<String> values = new ArrayList<>();
            dependee.forEach(d -> values.add(d == -1 ? instance.get(dependerIdx) : instance.get(d)));
            dependeeValuesPerDepender.get(dependerValue).add(values);
        });

        return dependeeValuesPerDepender;
    }

    public List<Integer> getDependeeByDepender(List<TaneDependency> dependencies, Integer depender) {
        List<Integer> indexes = new ArrayList<>();
        dependencies.stream().filter(dependency -> dependency.getDependerIdx().equals(depender))
                .forEach(dependency -> indexes.add(dependency.getDependeeIdx()));

        return indexes;
    }
}
