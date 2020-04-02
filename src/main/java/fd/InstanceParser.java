package fd;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;
import log.entity.Event;

import java.util.*;
import java.util.stream.Collectors;

public class InstanceParser {

    // Event attributes for instances
    private static final List<String> payloadAttributes = Arrays.asList(/*"url",*/ "target.value", "content");

    private static List<List<String>> instances = new ArrayList<>();

    private static Map<String, List<Event>> cases;
    private static Pattern pattern;

    public static List<List<String>> getInstances(Map<String, List<Event>> cases, Pattern pattern) {
        InstanceParser.cases = cases;
        InstanceParser.pattern = pattern;

        // Fill instances with empty lists
        initInstances();
        // Find instances
        Map<String, Integer> patternItemsCounts = new HashMap<>();
        pattern.getItemsValues().forEach(patternItem -> extractData(patternItemsCounts, patternItem));

        return instances;
    }

    private static void initInstances() {
        cases.entrySet().stream()
                .filter(entry -> getSequence(entry.getValue()).contains(pattern))
                .forEach(entry -> instances.add(new ArrayList<>()));
    }

    private static void extractData(Map<String, Integer> patternItemsCounts, String patternItem) {
        countPatternItems(patternItemsCounts, patternItem);
        int sequenceIndex = -1;
        for (List<Event> caseEvents : cases.values()) {
            Sequence sequence = getSequence(caseEvents);
            if (sequence.contains(pattern)) {
                sequenceIndex++;
                // Find events in the sequence that matches the current pattern item
                List<Event> matchCaseEvents = getEvents(patternItem, caseEvents);
                // Among defined payload attributes find that are present and add them to the valuesPerAttribute map
                for (String payloadAttribute : payloadAttributes) {
                    int patternItemIndex = patternItemsCounts.get(patternItem);
                    Map<String, String> payload = matchCaseEvents.get(patternItemIndex).getPayload();

                    if (payloadAttribute.equals(payloadAttributes.get(1)) && payload.containsKey(payloadAttributes.get(0))) {
                        continue;
                    }

                    // Check if event payload contains predefined payload attribute
                    if (payload.containsKey(payloadAttribute)) {
                        String attributeName = patternItem + "." + patternItemIndex + "." + payloadAttribute;
                        String attributeValue = payload.get(payloadAttribute);
                        // Fill attributes map and instances list
                        instances.get(sequenceIndex).add(attributeValue.replaceAll(" |,| ,", "|"));
                    }
                }
            }
        }
    }

    private static Sequence getSequence(List<Event> caseEvents) {
        return new Sequence(caseEvents.stream()
                .map(Event::getEventNameAndContext)
                .collect(Collectors.toList()));
    }

    private static void countPatternItems(Map<String, Integer> patternItemsCounts, String patternItem) {
        patternItemsCounts.computeIfPresent(patternItem, (key, val) -> val + 1);
        patternItemsCounts.putIfAbsent(patternItem, 0);
    }

    private static List<Event> getEvents(String patternItem, List<Event> caseEvents) {
        return caseEvents.stream()
                .filter(caseEvent -> patternItem.equals(caseEvent.getEventNameAndContext()))
                .collect(Collectors.toList());
    }
}
