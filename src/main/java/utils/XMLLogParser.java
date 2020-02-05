package utils;

import entity.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLLogParser {

    public static List<List<Action>> parseLogFile(String filePath) {
        List<List<Action>> sequences = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            document.normalizeDocument();
            NodeList processInstances = document.getElementsByTagName("ProcessInstance");
            for (int i = 0; i < processInstances.getLength(); i++) {
                List<Action> actions = new ArrayList<>();
                Element processInstance = (Element) processInstances.item(i);
                addEntriesToList(processInstance, actions);
                sequences.add(actions);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return sequences;
    }

    private static void addEntriesToList(Element processInstance, List<Action> actions) {
        NodeList auditTrailEntries = processInstance.getElementsByTagName("AuditTrailEntry");

        for (int i = 0; i < auditTrailEntries.getLength(); i++) {
            Element auditTrailEntry = (Element) auditTrailEntries.item(i);
            actions.add(parseAuditTrailEntry(auditTrailEntry));
        }
    }

    private static Action parseAuditTrailEntry(Element auditTrailEntry) {
        Element data = (Element) auditTrailEntry.getElementsByTagName("Data").item(0);
        NodeList attributes = data.getElementsByTagName("attribute");
        Action action = new Action();

        for (int j = 0; j < attributes.getLength(); j++) {
            Element attribute = (Element) attributes.item(j);
            assembleActionFromAttribute(attribute, action);
        }

        return action;
    }

    private static void assembleActionFromAttribute(Element attribute, Action action) {
        String attributeName = attribute.getAttribute("name");
        String attributeTextContent = attribute.getTextContent();

        switch (attributeName) {
            case "concept:name":
                action.setConceptName(attributeTextContent);
                break;
            case "time:timestamp":
                action.setTimestamp(attributeTextContent);
                break;
            case "Label":
                action.setLabel(attributeTextContent);
                break;
            case "Row":
                action.setRow(attributeTextContent);
                break;
            case "Column":
                action.setColumn(attributeTextContent);
                break;
        }
    }
}
