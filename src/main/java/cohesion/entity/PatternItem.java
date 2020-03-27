package cohesion.entity;

public class PatternItem {
    private int index;
    private String value;

    public PatternItem() {
    }

    public PatternItem(String value) {
        this.value = value;
    }

    public PatternItem(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "index=" + index +
                ", value='" + value + '\'' +
                '}';
    }
}
