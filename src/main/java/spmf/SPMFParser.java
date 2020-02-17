package spmf;

import entity.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SPMFParser {
    private static Map<String, List<Event>> sequencePerCaseId;

    public SPMFParser() {
        sequencePerCaseId = new HashMap<>();
    }

    public static StringBuilder transformActionsToSPMF(List<Event> events) {
        sequencePerCaseId = events.stream().collect(Collectors.groupingBy(Event::getCaseID));

        StringBuilder spmfSequences = new StringBuilder();
        sequencePerCaseId.forEach((caseId, sequence) -> {
            sequence.forEach(event -> assembleEvent(spmfSequences, event));
            spmfSequences.append("-2\n");
        });

        return spmfSequences;
    }

    private static void assembleEvent(StringBuilder result, Event event) {
        result.append(event.getEventType());
        String context = event.getContext().values().isEmpty() ? "" : String.valueOf(event.getContext().values().toArray()[0]);
        result.append("+").append(context).append(" -1 ");
    }
}
