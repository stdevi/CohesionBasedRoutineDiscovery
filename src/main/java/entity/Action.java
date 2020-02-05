package entity;

public class Action {
    private String timestamp;
    private String conceptName;
    private String label;
    private String column;
    private String row;
    private String type;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Action{" +
                "timestamp='" + timestamp + '\'' +
                ", conceptName='" + conceptName + '\'' +
                ", label='" + label + '\'' +
                ", column='" + column + '\'' +
                ", row='" + row + '\'' +
                '}';
    }
}
