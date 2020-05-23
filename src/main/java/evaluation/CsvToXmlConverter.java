package evaluation;

import log.entity.Event;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sequence.service.SequenceService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.Map;

public class CsvToXmlConverter {
    private Document doc;
    private Element process;

    public static void main(String[] args) {
        CsvToXmlConverter csvToXmlConverter = new CsvToXmlConverter();
        SequenceService.getInstance().parseCases(args[0]);
        csvToXmlConverter.parseCases(SequenceService.getInstance().getCases());
        csvToXmlConverter.print(args[1]);
    }

    public void print(String logPath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File(logPath));
            transformer.transform(source, result);

            System.out.println("Saved!");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void initProcess() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();

            // Create root WorkflowLog element
            Element workflowLog = doc.createElement("WorkflowLog");
            doc.appendChild(workflowLog);

            // Append Source
            Element source = getSource();
            workflowLog.appendChild(source);

            // Append Process
            process = getProcess();
            workflowLog.appendChild(process);

            // Append Process data
            Element data = getProcessData();
            process.appendChild(data);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void parseCases(Map<String, List<Event>> cases) {
        initProcess();
        cases.forEach((caseId, events) -> {
            Element processInstance = getProcessInstance(caseId);
            events.forEach(event -> {
                AuditTrailEntry auditTrailEntry = new AuditTrailEntry(event, doc);
                processInstance.appendChild(auditTrailEntry.getElement());
            });
        });
    }

    private Element getProcessInstanceData(String id) {
        Element data = doc.createElement("Data");

        Element attribute = doc.createElement("attribute");
        attribute.setAttribute("name", "concept:name");
        attribute.setTextContent(id);
        data.appendChild(attribute);

        return data;
    }

    private Element getProcessInstance(String id) {
        Element processInstance = doc.createElement("ProcessInstance");
        processInstance.setAttribute("id", id);
        processInstance.setAttribute("description", "instance with id " + id);
        process.appendChild(processInstance);

        Element data = getProcessInstanceData(id);
        processInstance.appendChild(data);

        return processInstance;
    }

    private Element getSource() {
        Element source = doc.createElement("Source");
        source.setAttribute("program", "XES MXML serialization");
        source.setAttribute("openxes.version", "1.0RC7");

        return source;
    }

    private Element getProcess() {
        Element process = doc.createElement("Process");
        process.setAttribute("id", "ID");
        process.setAttribute("description", "process");

        return process;
    }

    private Element getProcessData() {
        Element data = doc.createElement("Data");

        Element nameAttribute = doc.createElement("attribute");
        nameAttribute.setAttribute("name", "concept:name");
        nameAttribute.setTextContent("");

        Element timestampAttribute = doc.createElement("attribute");
        timestampAttribute.setAttribute("name", "time:timestamp");
        timestampAttribute.setTextContent("1970-01-01T00:00:00");

        data.appendChild(nameAttribute);
        data.appendChild(timestampAttribute);

        return data;
    }
}
