package dataflow;

import cohesion.entity.Pattern;

import java.util.*;

public class PatternEventsFlowExtractor {
    private List<String> readActions;
    private List<String> bridgeActions;
    private List<String> writeActions;

    public PatternEventsFlowExtractor() {
        writeActions = new ArrayList<>(Collections.singletonList("editField"));
        bridgeActions = new ArrayList<>(Collections.singletonList("paste"));
        readActions = new ArrayList<>(Collections.singletonList("copyCell"));
    }

    public Map<String, List<String>> extractWriteEventsPerReadEvent(Pattern pattern) {
        Map<String, List<String>> writesPerRead = new HashMap<>();
        String readEvent = "";
        String bridgeContext = "";

        for (String event : pattern.getItems()) {
            String eventType = event.split("\\+")[0];
            String eventContext = event.split("\\+")[1];

            if (readActions.contains(eventType)) {
                readEvent = event;
                bridgeContext = "";
                writesPerRead.put(readEvent, new ArrayList<>());
            } else if (bridgeActions.isEmpty() || bridgeActions.contains(eventType)) {
                bridgeContext = eventContext;
            } else if (writeActions.contains(eventType) && eventContext.equals(bridgeContext) && writesPerRead.containsKey(readEvent)) {
                writesPerRead.get(readEvent).add(event);
            }
        }

        return writesPerRead;
    }
}
