package evaluation;

import com.opencsv.CSVWriter;
import log.entity.CSVEvent;
import log.entity.Event;
import sequence.service.SequenceService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class XmlToCsvConverter {
    private static SequenceService sequenceService;

    public static void main(String[] args) {
        String xmlFilePath = args[0];
        String csvFilePath = args[1];

        sequenceService = SequenceService.getInstance();
        sequenceService.parseCases(xmlFilePath);
        createCSVFile(csvFilePath);
    }

    private static void createCSVFile(String path) {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(path),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            csvWriter.writeNext(CSVEvent.getColumns());
            for (Map.Entry<String, List<Event>> entry : sequenceService.getCases().entrySet()) {
                for (Event e : entry.getValue()) {
                    String[] assembledAction = assembleEvent(e);
                    csvWriter.writeNext(assembledAction);
                    if (assembledAction[4].equals("paste")) {
                        csvWriter.writeNext(addEditFieldAction(assembledAction));
                    }
                }
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] addEditFieldAction(String[] assembledPasteAction) {
        assembledPasteAction[4] = "editField";
        return assembledPasteAction;
    }

    private static String[] assembleEvent(Event event) {
        String caseID = event.getCaseID();
        String timestamp = event.getTimestamp();
        String userID = "user";

        String targetApp = "";
        if (event.getPayload().containsKey("source")) {
            targetApp = event.getPayload().get("source");
        }

        String eventType = "";
        if (event.getPayload().containsKey("concept:name")) {
            switch (event.getPayload().get("concept:name")) {
                case "insertValue":
                    if (targetApp.equals("Web")) {
                        eventType = "editField";
                    } else if (targetApp.equals("Excel")) {
                        eventType = "editCell";
                    }
                    break;
                case "copy":
                    if (targetApp.equals("Web")) eventType = "copy";
                    else if (targetApp.equals("Excel")) eventType = "copyCell";
                    break;
                case "paste":
                    if (targetApp.equals("Web")) eventType = "paste";
                    else if (targetApp.equals("Excel")) eventType = "pasteIntoCell";
                    break;
                default:
                    eventType = event.getPayload().get("concept:name");
            }
        }

        String url = "";
        if (event.getPayload().containsKey("Url")) {
            url = event.getPayload().get("Url");
        } else if (event.getPayload().containsKey("FileName")) {
            url = event.getPayload().get("FileName");
        }

        String content = "";
        if (event.getPayload().containsKey("Value")) {
            content = event.getPayload().get("Value");
        }

        String targetID = "";
        if (targetApp.equals("Excel") && event.getPayload().containsKey("Column") && event.getPayload().containsKey("Row")) {
            targetID = event.getPayload().get("Column") + event.getPayload().get("Row");
        } else if (targetApp.equals("Web") && event.getPayload().get("Label") != null) {
            targetID = event.getPayload().get("Label");
        }

        String targetValue = "";
        if (event.getPayload().containsKey("Value")) {
            targetValue = event.getPayload().get("Value");
        }

        return new String[]{caseID, timestamp, userID, targetApp, eventType, url, content, "", "", targetID,
                "", "", "", "", targetValue, "", "", "", "", "", ""};
    }
}
