package pattern.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fd.patternitem.entity.ItemsDependency;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Data
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
        itemsDependencies = new ArrayList<>();
    }

    public Pattern(List<PatternItem> items) {
        this.items = items;
        this.transformations = new HashMap<>();
        this.itemsDependencies = new ArrayList<>();
    }

    public Pattern(List<PatternItem> items, int support) {
        this.items = items;
        this.support = support;
        this.transformations = new HashMap<>();
        this.itemsDependencies = new ArrayList<>();
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

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode items = mapper.createArrayNode();
        this.items.forEach(i -> items.add(i.getValue()));

        ArrayNode transformations = mapper.createArrayNode();
        this.transformations.forEach((k, v) -> {
            ObjectNode tn = mapper.createObjectNode();
            ArrayNode ts = mapper.createArrayNode();
            Arrays.asList(v.split("\n")).forEach(t -> {
                if (!t.isBlank()) ts.add(t.trim());
            });
            tn.set(k.getLeft() + " -> " + k.getRight(), ts);
            transformations.add(tn);
        });

        ArrayNode dependencies = mapper.createArrayNode();
        itemsDependencies.forEach(d -> {
            ObjectNode itemDep = mapper.createObjectNode();

            if (d.getDependee() != null) {
                ArrayNode dependee = mapper.createArrayNode();
                d.getDependee().forEach(dep -> dependee.add(dep.getValue()));
                itemDep.set("dependee", dependee);
            }

            if (d.getDepender() != null) {
                itemDep.put("depender", d.getDepender().getValue());
            }

            if (d.getDependerPerDependee() != null) {
                ArrayNode values = mapper.createArrayNode();
                d.getDependerPerDependee().forEach((k, v) -> {
                    ObjectNode value = mapper.createObjectNode();

                    if (d.getDependee() != null) {
                        ArrayNode dependeeValues = mapper.createArrayNode();
                        for (int i = 0; i < k.size(); i++) {
                            ObjectNode dependeeValue = mapper.createObjectNode();
                            dependeeValue.put(d.getDependee().get(i).getValue(), k.get(i));
                            dependeeValues.add(dependeeValue);
                        }
                        value.set("dependee", dependeeValues);
                    }

                    if (d.getDepender() != null) {
                        value.put("depender", v);
                    }

                    values.add(value);
                });
                itemDep.set("values", values);
            }
            dependencies.add(itemDep);
        });

        ObjectNode pattern = mapper.createObjectNode();
        pattern.set("items", items);
        pattern.put("support", support);
        pattern.put("cohesion score", cohesionScore);
        pattern.set("transformations", transformations);
        pattern.set("functional dependencies", dependencies);
        pattern.put("automatable", automatable);
        pattern.put("coverage", coverage);
        pattern.put("RAI", RAI);

        ObjectNode root = mapper.createObjectNode();
        root.set("pattern@" + hashCode(), pattern);

        return root.toPrettyString();
    }

    private String transformationsToString() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        transformations.forEach((k, v) -> {
            str.append("\n\t\t\t\t\t\t{\n");
            str.append(k.getLeft()).append(" -> ").append(k.getRight()).append(": ");
            str.append(v.trim().replaceAll("\n", "; "));
            str.append("\n\t\t\t\t\t\t},");
        });
        str.append("\n\t\t\t\t\t]");

        return str.toString();
    }
}
