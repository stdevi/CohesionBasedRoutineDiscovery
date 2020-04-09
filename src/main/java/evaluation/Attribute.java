package evaluation;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Attribute {
    private Element attribute;

    public Attribute(String name, Document doc) {
        attribute = doc.createElement("attribute");
        attribute.setAttribute("name", name);
    }

    public Element getElement() {
        return attribute;
    }

    public void setTextContent(String sourceValue) {
        attribute.setTextContent(sourceValue);
    }
}
