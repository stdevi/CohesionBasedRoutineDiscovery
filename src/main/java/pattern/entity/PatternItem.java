package pattern.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    @Override
    public String toString() {
        return value;
    }
}
