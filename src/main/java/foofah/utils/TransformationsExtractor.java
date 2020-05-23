package foofah.utils;

import foofah.entity.Transformation;
import log.entity.Event;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import pattern.entity.Pattern;
import pattern.entity.PatternItem;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformationsExtractor {
    private List<String> readActions;
    //    private List<String> bridgeActions;
    private List<String> writeActions;

    public TransformationsExtractor() {
        writeActions = new ArrayList<>(Arrays.asList("editField", "editCell"));
//        bridgeActions = new ArrayList<>(Collections.singletonList("paste"));
        readActions = new ArrayList<>(Arrays.asList("copyCell", "copy"));
    }

    public Map<Pair<PatternItem, PatternItem>, List<Transformation>> getPatternTransformations(Map<String, List<Event>> cases, Pattern pattern) {
        PatternEventsFlowExtractor extractor = new PatternEventsFlowExtractor();
        Map<PatternItem, List<PatternItem>> writesPerReadEvents = extractor.extractWriteEventsPerReadEvent(pattern);
        Map<Pair<PatternItem, PatternItem>, List<Transformation>> transformationsPerReadWrite = new LinkedHashMap<>();
        List<Transformation> examples = extractAllTransformations(cases);

        writesPerReadEvents.forEach((key, value) -> value.forEach(writeEvent -> {
            if (key != null) {
                List<Transformation> transformations = examples.stream()
                        .filter(e -> e.getSource().contains(key.getValue()) && e.getTarget().contains(writeEvent.getValue()))
                        .collect(Collectors.toList());
                if (!transformations.isEmpty()) {
                    Pair<PatternItem, PatternItem> readWrite = new ImmutablePair<>(key, writeEvent);
                    transformationsPerReadWrite.putIfAbsent(readWrite, transformations);
                }
            } else {
                Pair<PatternItem, PatternItem> readWrite = new ImmutablePair<>(new PatternItem(""), writeEvent);
                transformationsPerReadWrite.putIfAbsent(readWrite, new ArrayList<>());
            }
        }));

        return transformationsPerReadWrite;
    }

    public HashMap<String, List<Transformation>> getTransformationsGroupByTarget(List<Transformation> transformations) {
        HashMap<String, List<Transformation>> data = new HashMap<>();

        transformations.forEach(transformationExample -> {
            String target = transformationExample.getTarget();
            data.put(target, data.containsKey(target) ?
                    Stream.concat(data.get(target).stream(), Stream.of(transformationExample)).collect(Collectors.toList()) :
                    Collections.singletonList(transformationExample));
        });

        return data;
    }

    public List<Transformation> extractAllTransformations(Map<String, List<Event>> cases) {
        List<Transformation> transformations = new ArrayList<>();

        for (String caseID : cases.keySet()) {
            List<Event> events = new ArrayList<>(cases.get(caseID));
            List<String> targets = new ArrayList<>();

            for (int i = events.size() - 1; i >= 0; i--) {
                if (writeActions.contains(events.get(i).getEventType()) /*&& !targets.contains(events.get(i).payload.get("target.name"))*/) {
                    String target = events.get(i).payload.containsKey("target.name") ? events.get(i).payload.get("target.name") : events.get(i).payload.get("target.id");
                    String output = events.get(i).payload.get("target.value").replaceAll("\\P{Print}", " ");

                    StringBuilder source = new StringBuilder();
                    List<String> input = new ArrayList<>();
                    targets.add(target);

                    for (int j = 0; j < i; j++) {
                        Event e = events.get(j);
                        if (events.get(j).getEventType().contains("paste") &&
                                (e.payload.getOrDefault("target.name", "").equals(target)) ||
                                (e.payload.getOrDefault("target.id", "").equals(target)) || i == j + 1) {
                            for (int k = j; k >= 0; k--) {
                                if (readActions.contains(events.get(k).getEventType())) {
                                    if (source.toString().equals("")) {
                                        if (events.get(k).payload.containsKey("target.name"))
                                            source = new StringBuilder(events.get(k).getEventType() + "+" + events.get(k).payload.get("target.name"));
                                        else
                                            source = new StringBuilder(events.get(k).getEventType() + "+" + events.get(k).payload.get("target.id"));
                                    } else {
                                        if (events.get(k).payload.containsKey("target.name"))
                                            source = new StringBuilder(events.get(k).getEventType() + "+" + events.get(k).payload.get("target.name"));
//                                        source.append(",").append(events.get(k).getEventType()).append("+").append(events.get(k).payload.get("target.name"));
                                        else
                                            source = new StringBuilder(events.get(k).getEventType() + "+" + events.get(k).payload.get("target.id"));
//                                            source.append(",").append(events.get(k).getEventType()).append("+").append(events.get(k).payload.get("target.id"));

                                    }
                                    input.add(events.get(k).payload.get("target.value").replaceAll("\\P{Print}", " "));
                                    break;
                                }
                            }
                        }
                    }

                    if (input.size() > 0) {
                        if (input.size() > 1)
                            transformations.add(new Transformation(caseID, source.toString(), events.get(i).getEventType() + "+" + target, input, Collections.singletonList(output)));
                        else
                            transformations.add(new Transformation(caseID, source.toString(), events.get(i).getEventType() + "+" + target, input.get(0), output));
                    }
                }
            }
        }

        return transformations;
    }
}
