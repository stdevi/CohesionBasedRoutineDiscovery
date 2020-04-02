package cohesion.entity;

import java.util.List;
import java.util.Map;

public class ItemsDependency {
    private PatternItem depender;
    private List<PatternItem> dependee;
    private Map<List<String>, String> dependerPerDependeeValues;

    public PatternItem getDepender() {
        return depender;
    }

    public void setDepender(PatternItem depender) {
        this.depender = depender;
    }

    public List<PatternItem> getDependee() {
        return dependee;
    }

    public void setDependee(List<PatternItem> dependee) {
        this.dependee = dependee;
    }

    public Map<List<String>, String> getDependerPerDependeeValues() {
        return dependerPerDependeeValues;
    }

    public void setDependerPerDependee(Map<List<String>, String> dependerPerDependeeValues) {
        this.dependerPerDependeeValues = dependerPerDependeeValues;
    }
}
