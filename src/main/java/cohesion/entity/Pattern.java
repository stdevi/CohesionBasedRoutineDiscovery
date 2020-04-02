package cohesion.entity;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Pattern {
    private List<PatternItem> items;
    private int support;
    private int cohesionScore;
    private boolean automatable = false;
    private Map<Pair<PatternItem, PatternItem>, String> transformations = new HashMap<>();
    private List<ItemsDependency> itemsDependencies;
    private double coverage;

    public Pattern() {
        items = new ArrayList<>();
    }

    public Pattern(List<PatternItem> items) {
        this.items = items;
    }

    public Pattern(List<PatternItem> items, int support) {
        this.items = items;
        this.support = support;
    }

    public void add(PatternItem item) {
        items.add(item);
    }

    public List<String> getItemsValues() {
        return items.stream().map(PatternItem::getValue).collect(Collectors.toList());
    }

    public int getLength() {
        return items.size();
    }

    public double getRAI() {
        return (double) transformations.entrySet().stream().filter(entry -> entry.getValue() != null).count() / transformations.size();
    }

    public List<Pair<PatternItem, PatternItem>> getEmptyTransformationPairs() {
        return transformations.entrySet().stream()
                .filter(transformation -> transformation.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "items=" + items +
                ", support=" + support +
                ", cohesionScore=" + cohesionScore +
                '}';
    }
}
