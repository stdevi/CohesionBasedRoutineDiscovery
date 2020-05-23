package fd.patternitem.entity;

import lombok.Data;
import pattern.entity.PatternItem;

import java.util.List;
import java.util.Map;

@Data
public class ItemsDependency {
    private PatternItem depender;
    private List<PatternItem> dependee;
    private Map<List<String>, String> dependerPerDependee;
}
