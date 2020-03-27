package cohesion.service;

import cohesion.entity.ItemsDependency;
import cohesion.entity.Pattern;
import log.entity.Event;

import java.util.List;
import java.util.Map;

public class PatternService {

    private ItemsDependencyService dependencyService;

    public PatternService() {
        dependencyService = new ItemsDependencyService();
    }

    public void setDependencies(Map<String, List<Event>> cases, Pattern pattern) {
        List<ItemsDependency> dependencies = dependencyService.findDependencies(cases, pattern);
        pattern.setItemsDependencies(dependencies);
    }
}
