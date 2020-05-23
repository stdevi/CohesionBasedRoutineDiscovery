package weka.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WekaData {

    private Map<String, Set<String>> valuesPerAttribute;
    private List<List<String>> instances;

    public Map<String, Set<String>> getValuesPerAttribute() {
        return valuesPerAttribute;
    }

    public List<List<String>> getInstances() {
        return instances;
    }

    public void setValuesPerAttribute(Map<String, Set<String>> valuesPerAttribute) {
        this.valuesPerAttribute = valuesPerAttribute;
    }

    public void setInstances(List<List<String>> instances) {
        this.instances = instances;
    }
}
