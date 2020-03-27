package cohesion.entity;

import java.util.List;
import java.util.Map;

public class ItemsDependency {
    private PatternItem depender;
    private List<PatternItem> dependee;
    private Map<String, List<List<String>>> dependeeValuesPerDepender;

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

    public Map<String, List<List<String>>> getDependeeValuesPerDepender() {
        return dependeeValuesPerDepender;
    }

    public void setDependeeValuesPerDepender(Map<String, List<List<String>>> dependeeValuesPerDepender) {
        this.dependeeValuesPerDepender = dependeeValuesPerDepender;
    }
}
