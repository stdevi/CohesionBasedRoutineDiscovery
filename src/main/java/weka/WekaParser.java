package weka;

import cohesion.entity.Pattern;
import cohesion.entity.Sequence;
import log.entity.Event;
import weka.entity.WekaData;

import java.util.*;
import java.util.stream.Collectors;

public class WekaParser {

    // Event attributes that should be given to JRipper
    private static final List<String> payloadAttributes = Arrays.asList("url", "target.value", "content");

    private static Map<String, Set<String>> valuesPerAttribute = new LinkedHashMap<>();
    private static List<List<String>> instances = new ArrayList<>();

    private static Map<String, List<Event>> cases;
    private static Pattern pattern;

    public static WekaData getWekaData(Map<String, List<Event>> cases, Pattern pattern) {
        setCases(cases);
        setPattern(pattern);

        // Fill instances with empty lists
        initInstances();
        // Find weka attributes and instances
        Map<String, Integer> patternItemsCounts = new HashMap<>();
        pattern.getItems().forEach(patternItem -> extractWekaData(patternItemsCounts, patternItem));

        WekaData wekaData = new WekaData();
        wekaData.setInstances(instances);
        wekaData.setValuesPerAttribute(valuesPerAttribute);

        return wekaData;
    }

    private static void initInstances() {
        cases.entrySet().stream()
                .filter(entry -> getSequence(entry.getValue()).contains(pattern))
                .forEach(entry -> instances.add(new ArrayList<>()));
    }

    private static void extractWekaData(Map<String, Integer> patternItemsCounts, String patternItem) {
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
                    // Check if event payload contains predefined payload attribute
                    if (payload.containsKey(payloadAttribute)) {
                        String attributeName = patternItem + "." + patternItemIndex + "." + payloadAttribute;
                        String attributeValue = payload.get(payloadAttribute);
                        // Fill attributes map and instances list
                        valuesPerAttribute.putIfAbsent(attributeName, new LinkedHashSet<>());
                        valuesPerAttribute.get(attributeName).add(attributeValue.replaceAll(" |,| ,", "|"));
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

    public static void setCases(Map<String, List<Event>> cases) {
        WekaParser.cases = cases;
    }

    public static void setPattern(Pattern pattern) {
        WekaParser.pattern = pattern;
    }

    //    public Map<String, Set<String>> getValuesPerAttribute(Map<String, List<Event>> cases, Pattern pattern) {
//        Map<String, Integer> indexPerEvent = new HashMap<>();
//        int[] instanceIndex = {-1};
//
//        pattern.getItems().forEach(patternItem -> {
//            // Determine the indices of events with same names
//            indexPerEvent.computeIfPresent(patternItem, (key, val) -> val + 1);
//            indexPerEvent.putIfAbsent(patternItem, 0);
//
//            instanceIndex[0] = -1;
//
//            cases.forEach((caseId, caseEvents) -> {
//                // Create sequence from case events
//                Sequence sequence = new Sequence(caseEvents.stream()
//                        .map(Event::getEventNameAndContext)
//                        .collect(Collectors.toList()));
//
//                if (sequence.contains(pattern)) {
//
//                    instanceIndex[0]++;
//                    if (instances.size() <= instanceIndex[0]) {
//                        instances.add(new ArrayList<>());
//                    }
//
//                    // Find events in the sequence that matches the current pattern item
//                    List<Event> matchCaseEvents = caseEvents.stream()
//                            .filter(caseEvent -> patternItem.equals(caseEvent.getEventNameAndContext()))
//                            .collect(Collectors.toList());
//
//                    // Among defined payload attributes find that are present and add them to the valuesPerAttribute map
//                    payloadAttributes.forEach(payloadAttribute -> {
//                        Map<String, String> payload = matchCaseEvents.get(indexPerEvent.get(patternItem)).getPayload();
//                        if (payload.containsKey(payloadAttribute)) {
//                            String attributeName = patternItem + "." + indexPerEvent.get(patternItem) + "." + payloadAttribute;
//                            String attributeValue = payload.get(payloadAttribute);
//                            valuesPerAttribute.putIfAbsent(attributeName, new LinkedHashSet<>());
//                            valuesPerAttribute.get(attributeName).add(attributeValue);
//                            instances.get(instanceIndex[0]).add(attributeName);
//                        }
//                    });
//                }
//            });
//        });
//
//        return valuesPerAttribute;
//    }
}
