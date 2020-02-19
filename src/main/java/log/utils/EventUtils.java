package log.utils;

import log.entity.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventUtils {

    public static List<String> getContextAttributes(List<Event> events, Double threshold, Boolean considerMissingValues) {
        List<String> context = new ArrayList<>();
        List<String> attributes = new ArrayList<>(events.get(0).getAttributes());
        if (events.get(0).payload.containsKey("target.row")) {
            attributes.add("target.row");
        }
        if (events.get(0).payload.containsKey("target.column")) {
            attributes.add("target.column");
        }

        attributes.stream().filter(attribute -> !attribute.equals("timeStamp") && !attribute.equals("eventType")).forEach(attribute -> {
            List<String> uniqueValues = events.stream().map(el -> el.payload.get(attribute)).distinct().collect(Collectors.toList());
            double variance = (double) (uniqueValues.size() - 1) / events.size();
            if ((attribute.equals("target.innerText") || attribute.equals("target.name") || variance > 0.0) && variance < threshold) {
                if (!considerMissingValues) {
                    if (uniqueValues.size() - 1 > 1 || attribute.equals("target.innerText") || attribute.equals("target.name")) {
                        context.add(attribute);
                    }
                } else {
                    context.add(attribute);
                }
            }
        });

        return context;
    }

    public static void setContextAttributes(List<Event> events, Double threshold, Boolean considerMissingValues) {
        List<String> contextAttributes = getContextAttributes(events, threshold, considerMissingValues);
        events.forEach(event -> {
            HashMap<String, String> context = new HashMap<>();
            for (String attribute : event.payload.keySet()) {
                if (contextAttributes.contains(attribute))
                    context.put(attribute, event.payload.get(attribute));
            }
            event.context = new HashMap<>(context);
        });
    }

    public static HashMap<String, List<Event>> groupByEventType(List<Event> events) {
        HashMap<String, List<Event>> groupedEvents = new HashMap<>();
        events.forEach(event -> {
            if (!groupedEvents.containsKey(event.getEventType())) {
                groupedEvents.put(event.getEventType(), Collections.singletonList(event));
            } else {
                groupedEvents.put(event.getEventType(), Stream.concat(groupedEvents.get(event.getEventType()).stream(),
                        Stream.of(event)).collect(Collectors.toList()));
            }
        });

        return groupedEvents;
    }
}
