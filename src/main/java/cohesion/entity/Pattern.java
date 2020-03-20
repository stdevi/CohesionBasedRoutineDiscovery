package cohesion.entity;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pattern {
    private List<String> items;
    private int support;
    private int cohesionScore;
    private boolean automatable = false;
    private Map<Pair<String, String>, String> transformations = new HashMap<>();

    public Pattern() {
        items = new ArrayList<>();
    }

    public Pattern(List<String> items) {
        this.items = items;
    }

    public Pattern(List<String> items, int support) {
        this.items = items;
        this.support = support;
    }

    public void add(String item) {
        items.add(item);
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
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

    public Map<Pair<String, String>, String> getTransformations() {
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

    @Override
    public String toString() {
        return "Pattern{" +
                "items=" + items +
                ", length=" + items.size() +
                ", support=" + support +
                ", cohesionScore=" + cohesionScore +
                '}';
    }
}
