package cohesion.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class PatternItem {
    private int index;
    private String value;
    private boolean automatable = true;

    public PatternItem(String value) {
        this.value = value;
    }

    public PatternItem(int index, String value) {
        this.index = index;
        this.value = value;
    }
}
