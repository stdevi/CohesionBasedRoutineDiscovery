package log.entity;

import java.util.List;
import java.util.Map;

public abstract class Event {
    public Map<String, String> payload;
    public Map<String, String> context;
    String caseID;
    String eventType;
    String timestamp;
    List<String> attributes;

    public Map<String, String> getPayload() {
        return payload;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public String getCaseID() {
        return caseID;
    }

    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public String getEventNameAndContext() {
        if (getContext().size() > 0) {
            return getEventType() + "+" + getContext().values().toArray()[0];
        } else {
            return getEventType();
        }
    }

    public String toString() {
        return "({" + this.caseID + "}, " + ", " + this.eventType + ", " + this.timestamp + ", " + payload + ")";
    }
}
