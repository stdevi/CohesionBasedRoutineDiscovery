package log.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class XMLEvent extends Event {

    public XMLEvent(String caseId) {
        this.caseID = caseId;
        this.attributes = new ArrayList<>();
        this.payload = new HashMap<>();
    }
}
