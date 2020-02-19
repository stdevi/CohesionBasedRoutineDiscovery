package log.parser;

import log.entity.Event;
import log.entity.XMLEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import log.utils.EventUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLLogParser implements LogParser {

    public List<Event> parseLogFile(String filePath) {
        List<Event> events = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            document.normalizeDocument();
            NodeList processInstances = document.getElementsByTagName("ProcessInstance");
            for (int i = 0; i < processInstances.getLength(); i++) {
                Element processInstance = (Element) processInstances.item(i);
                addEntriesToList(processInstance, events, i);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        setEventsContext(events);

        return events;
    }

    private void addEntriesToList(Element processInstance, List<Event> events, int caseId) {
        NodeList auditTrailEntries = processInstance.getElementsByTagName("AuditTrailEntry");

        for (int i = 0; i < auditTrailEntries.getLength(); i++) {
            Element auditTrailEntry = (Element) auditTrailEntries.item(i);
            events.add(parseAuditTrailEntry(auditTrailEntry, caseId));
        }
    }

    private Event parseAuditTrailEntry(Element auditTrailEntry, int caseId) {
        Element data = (Element) auditTrailEntry.getElementsByTagName("Data").item(0);
        NodeList attributes = data.getElementsByTagName("attribute");
        Event event = new XMLEvent(String.valueOf(caseId));
        for (int j = 0; j < attributes.getLength(); j++) {
            Element attribute = (Element) attributes.item(j);
            assembleActionFromAttribute(attribute, event);
        }

        return event;
    }

    private void assembleActionFromAttribute(Element attribute, Event event) {
        String attributeName = attribute.getAttribute("name");
        String attributeTextContent = attribute.getTextContent();

        event.getAttributes().add(attributeName);
        event.getPayload().put(attributeName, attributeTextContent);

        if ("concept:name".equals(attributeName)) {
            event.setEventType(attributeTextContent);
        } else if ("time:timestamp".equals(attributeName)) {
            event.setTimestamp(attributeTextContent);
        }
    }

    private void setEventsContext(List<Event> events) {
        HashMap<String, List<Event>> groupedEvents = EventUtils.groupByEventType(events);
        for (String group : groupedEvents.keySet())
            groupedEvents.get(group).forEach(event -> {
                HashMap<String, String> context = new HashMap<>();
                for (String attribute : event.payload.keySet()) {
                    switch (attribute) {
                        case "Column":
                        case "Row":
                        case "Label":
                            context.put(attribute, event.payload.get(attribute));
                            break;
                    }
                }
                event.setContext(context);
            });
    }
}
