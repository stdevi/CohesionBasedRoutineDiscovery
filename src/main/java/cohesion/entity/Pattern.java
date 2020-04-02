package cohesion.entity;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString
public class Pattern {
    private List<PatternItem> items;
    private int support;
    private int cohesionScore;
    private boolean automatable = false;
    private Map<Pair<PatternItem, PatternItem>, String> transformations;
    private List<ItemsDependency> itemsDependencies;
    private double coverage;
    private double RAI;

    public Pattern() {
        items = new ArrayList<>();
        transformations = new HashMap<>();
    }

    public Pattern(List<PatternItem> items) {
        this.items = items;
        transformations = new HashMap<>();
    }

    public Pattern(List<PatternItem> items, int support) {
        this.items = items;
        this.support = support;
        transformations = new HashMap<>();
    }

    public List<String> getItemsValues() {
        return items.stream().map(PatternItem::getValue).collect(Collectors.toList());
    }

    public List<Pair<PatternItem, PatternItem>> getEmptyTransformationPairs() {
        return transformations.entrySet().stream()
                .filter(transformation -> transformation.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
