package cohesion.entity;

import java.util.List;

public class Sequence {
    private int id;
    private List<String> items;

    public Sequence(int id, List<String> items) {
        this.items = items;
        this.id = id;
    }

    public Sequence(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Sequence{" +
                "items=" + items +
                ", id=" + id +
                '}';
    }
}
