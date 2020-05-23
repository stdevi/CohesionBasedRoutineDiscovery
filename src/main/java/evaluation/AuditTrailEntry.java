package evaluation;

import log.entity.Event;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AuditTrailEntry {
    private Element auditTrailEntry;
    private Document doc;
    private Event event;

    public AuditTrailEntry(Event event, Document doc) {
        this.doc = doc;
        this.event = event;

        auditTrailEntry = doc.createElement("AuditTrailEntry");
        setData();
        setWorkflowModelElement();
        setEventType();
    }

    private void setEventType() {
        Element eventType = doc.createElement("EventType");
        eventType.setTextContent("complete");
        auditTrailEntry.appendChild(eventType);
    }

    private void setWorkflowModelElement() {
        Element workflowModelElement = doc.createElement("WorkflowModelElement");
        workflowModelElement.setTextContent(event.getEventType());
        auditTrailEntry.appendChild(workflowModelElement);
    }

    private void setData() {
        EntryData data = new EntryData(event, doc);
        auditTrailEntry.appendChild(data.getElement());
    }

    public Element getElement() {
        return auditTrailEntry;
    }
}
