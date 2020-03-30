package fd.service;

import cohesion.entity.Pattern;
import fd.TaneExecutor;
import fd.entity.TaneDependency;
import log.entity.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TaneService {
    private List<List<String>> instances;

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

            IntStream.range(0, dependee.size()).forEach(i -> {
                if (i >= dependeeValuesPerDepender.get(dependerValue).size()) {
                    dependeeValuesPerDepender.get(dependerValue).add(new ArrayList<>());
                }
                Integer d = dependee.get(i);
                dependeeValuesPerDepender.get(dependerValue).get(i).add(d == -1 ? instance.get(dependerIdx) : instance.get(d));
            });
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
