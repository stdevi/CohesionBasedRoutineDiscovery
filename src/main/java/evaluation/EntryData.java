package evaluation;

import log.entity.Event;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EntryData {
    private Element data;
    private Event event;
    private Document doc;
    private String sourceValue;

    public EntryData(Event event, Document doc) {
        this.event = event;
        this.doc = doc;
        buildDataElement();
    }

    private void buildDataElement() {
        data = doc.createElement("Data");

        setTimestamp();
        setEntrySource();
        setTransition();
        setConceptName();

        if (sourceValue.equals("Excel")) {
            setColumn();
            setRow();
        } else if (sourceValue.equals("Chrome")) {
            setLabel();
            setUrl();
        }

        setValue();
    }

    private void setUrl() {
        Attribute url = new Attribute("Url", doc);
        url.setTextContent(event.getPayload().get("url"));

        data.appendChild(url.getElement());
    }

    private void setTimestamp() {
        Attribute timestamp = new Attribute("time:timestamp", doc);
        timestamp.setTextContent(event.getTimestamp());

        data.appendChild(timestamp.getElement());
    }

    private void setLabel() {
        Attribute label = new Attribute("Label", doc);
        label.setTextContent(event.getPayload().get("target.name"));

        data.appendChild(label.getElement());
    }

    private void setValue() {
        Attribute value = new Attribute("Value", doc);
        value.setTextContent(event.getPayload().get("target.value") != null ?
                event.getPayload().get("target.value") :
                event.getPayload().get("content")
        );

        data.appendChild(value.getElement());
    }

    private void setRow() {
        Attribute row = new Attribute("Row", doc);
        row.setTextContent(event.getPayload().get("target.row"));

        data.appendChild(row.getElement());
    }

    private void setColumn() {
        Attribute column = new Attribute("Column", doc);
        column.setTextContent(event.getPayload().get("target.column"));

        data.appendChild(column.getElement());
    }

    private void setEntrySource() {
        Attribute source = new Attribute("source", doc);
        sourceValue = event.getPayload().get("targetApp");
        source.setTextContent(sourceValue);

        data.appendChild(source.getElement());
    }

    private void setTransition() {
        Attribute transition = new Attribute("lifecycle:transition", doc);
        transition.setTextContent("complete");

        data.appendChild(transition.getElement());
    }

    private void setConceptName() {
        Attribute conceptName = new Attribute("concept:name", doc);
        conceptName.setTextContent(event.getEventType());

        data.appendChild(conceptName.getElement());
    }

    public Element getElement() {
        return data;
    }
}
