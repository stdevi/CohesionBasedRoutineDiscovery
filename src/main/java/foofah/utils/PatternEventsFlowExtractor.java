package foofah.utils;

import pattern.entity.Pattern;
import pattern.entity.PatternItem;

import java.util.*;

public class PatternEventsFlowExtractor {
    private List<String> readActions;
    private List<String> writeActions;

    public PatternEventsFlowExtractor() {
        writeActions = new ArrayList<>(Arrays.asList("editField", "editCell"));
        readActions = new ArrayList<>(Arrays.asList("copyCell", "copy"));
    }

    public Map<PatternItem, List<PatternItem>> extractWriteEventsPerReadEvent(Pattern pattern) {
        Map<PatternItem, List<PatternItem>> writesPerRead = new HashMap<>();
        PatternItem readEvent = new PatternItem();

        for (PatternItem event : pattern.getItems()) {
            String eventType = event.getValue().split("\\+")[0];
            if (readActions.contains(eventType)) {
                readEvent = event;
                writesPerRead.put(readEvent, new ArrayList<>());
            } else if (writeActions.contains(eventType)) {
                if (writesPerRead.containsKey(readEvent)) {
                    writesPerRead.get(readEvent).add(event);
                } else {
                    if (!writesPerRead.containsKey(null)) {
                        writesPerRead.put(null, new ArrayList<>());
                    }
                    writesPerRead.get(null).add(event);
                }
            }
        }

        return writesPerRead;
    }
}
