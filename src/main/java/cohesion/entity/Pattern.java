package cohesion.entity;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Pattern {
    private List<PatternItem> items;
    private int support;
    private int cohesionScore;
    private boolean automatable = false;
    private Map<Pair<PatternItem, PatternItem>, String> transformations = new HashMap<>();
    private List<ItemsDependency> itemsDependencies;

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

    public List<PatternItem> getItems() {
        return items;
    }

    public void setItems(List<PatternItem> items) {
        this.items = items;
    }

    public List<String> getItemsValues() {
        return items.stream().map(PatternItem::getValue).collect(Collectors.toList());
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getCohesionScore() {
        return cohesionScore;
    }

    public void setCohesionScore(int cohesionScore) {
        this.cohesionScore = cohesionScore;
    }

    public int getLength() {
        return items.size();
    }

    public Map<Pair<PatternItem, PatternItem>, String> getTransformations() {
        return transformations;
    }

    public boolean isAutomatable() {
        return automatable;
    }

    public void setAutomatable(boolean automatable) {
        this.automatable = automatable;
    }

    public double getRAI() {
        return (double) transformations.entrySet().stream().filter(entry -> entry.getValue() != null).count() / transformations.size();
    }

    public List<ItemsDependency> getItemsDependencies() {
        return itemsDependencies;
    }

    public void setItemsDependencies(List<ItemsDependency> itemsDependencies) {
        this.itemsDependencies = itemsDependencies;
    }

    public List<Pair<PatternItem, PatternItem>> getEmptyTransformationPairs() {
        return transformations.entrySet().stream()
                .filter(transformation -> transformation.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void setTransformations(Map<Pair<PatternItem, PatternItem>, String> transformations) {
        this.transformations = transformations;
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
