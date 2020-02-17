package entity.event;

import java.util.ArrayList;
import java.util.HashMap;

public class XMLEvent extends Event {

    public XMLEvent(int eid, String caseId) {
        this.eid = eid;
        this.caseID = caseId;
        this.attributes = new ArrayList<>();
        this.payload = new HashMap<>();
    }
}
