package log.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVEvent extends Event {

    public CSVEvent(List<String> attributes, String[] values) {
        String temp;
        this.attributes = new ArrayList<>(attributes);
        this.caseID = attributes.contains("caseID") ? values[attributes.indexOf("caseID")] : "";
        this.eventType = values[attributes.indexOf("eventType")];
        this.timestamp = values[attributes.indexOf("timeStamp")];
        payload = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            temp = values[i].matches("\"+") ? "" : values[i].replaceAll("\"{4}", "\"\"")
                    .replaceAll("\"([^;\"\\[\\]]+)\"", "$1")
                    .replaceAll("^\"(.*)\"$", "$1");

            if (i != attributes.indexOf("eventType") && i != attributes.indexOf("timeStamp") && i != attributes.indexOf("caseID")
                    && ((!temp.equals("\"\"") && !temp.equals("")) || (i == attributes.indexOf("target.value")
                    && (this.eventType.equals("clickTextField") || this.eventType.equals("editField") ||
                    this.eventType.equals("getCell") || this.eventType.equals("editRange"))))) {
                payload.put(attributes.get(i), temp);
            }
        }

        if (payload.containsKey("targetApp") && payload.get("targetApp").equals("Excel")) {
            if (payload.containsKey("target.id")) {
                payload.put("target.row", payload.get("target.id").replaceAll("[A-Za-z]+", ""));
                payload.put("target.column", payload.get("target.id").replaceAll("\\d+", ""));
            }
        }
    }
}