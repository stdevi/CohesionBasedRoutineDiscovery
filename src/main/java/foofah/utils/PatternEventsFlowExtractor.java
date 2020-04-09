package foofah.utils;

import pattern.entity.Pattern;
import pattern.entity.PatternItem;

import java.util.*;

public class PatternEventsFlowExtractor {
    private List<String> readActions;
    //    private List<String> bridgeActions;
    private List<String> writeActions;

    public PatternEventsFlowExtractor() {
        writeActions = new ArrayList<>(Collections.singletonList("editField"));
//        bridgeActions = new ArrayList<>(Collections.singletonList("paste"));
        readActions = new ArrayList<>(Collections.singletonList("copyCell"));
    }

    public Map<PatternItem, List<PatternItem>> extractWriteEventsPerReadEvent(Pattern pattern) {
        Map<PatternItem, List<PatternItem>> writesPerRead = new HashMap<>();
        PatternItem readEvent = new PatternItem();
//        String bridgeContext = "";

        for (PatternItem event : pattern.getItems()) {
            String eventType = event.getValue().split("\\+")[0];
//            String eventContext = event.getValue().split("\\+")[1];

            if (readActions.contains(eventType)) {
                readEvent = event;
//                bridgeContext = "";
                writesPerRead.put(readEvent, new ArrayList<>());
//            } else if (bridgeActions.isEmpty() || bridgeActions.contains(eventType)) {
//                bridgeContext = eventContext;
            } else if (writeActions.contains(eventType) /*&& eventContext.equals(bridgeContext)*/ && writesPerRead.containsKey(readEvent)) {
                writesPerRead.get(readEvent).add(event);
            }
        }

        return writesPerRead;
    }
}
