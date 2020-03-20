package cohesion.entity;

import log.entity.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sequence {
    private int id;
    private List<String> items;

    public Sequence(int id, List<String> items) {
        this.items = items;
        this.id = id;
    }

    public Sequence(Sequence sequence) {
        this.id = sequence.getId();
        this.items = new ArrayList<>(sequence.getItems());
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

    public boolean contains(Pattern pattern) {
        List<String> patternEvents = new ArrayList<>(pattern.getItems());
        List<String> sequenceEvents = new ArrayList<>(items);

        for (String patternEvent : patternEvents) {
            if (sequenceEvents.contains(patternEvent)) {
                sequenceEvents.remove(patternEvent);
            } else {
                return false;
            }
        }

        return true;
    }

    public boolean containsPattern(Pattern pattern) {
        List<String> sequenceItems = new ArrayList<>(items);
        for (String patternItem : pattern.getItems()) {
            if (sequenceItems.contains(patternItem)) {
                sequenceItems.remove(patternItem);
            } else {
                return false;
            }
        }

        return true;
    }

    public List<String> removePatternElements(Pattern pattern) {
        List<String> sequenceItems = new ArrayList<>(items);
        if (containsPattern(pattern)) {
            pattern.getItems().forEach(sequenceItems::remove);
            setItems(sequenceItems);

            return pattern.getItems();
        }

        return new ArrayList<>();
    }
}
