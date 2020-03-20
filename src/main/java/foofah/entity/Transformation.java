package foofah.entity;

import java.util.Collections;
import java.util.List;

public class Transformation {
    private String caseID;
    private String source;
    private String target;
    private List<String> input;
    private List<String> output;

    public Transformation(String caseID, String source, String target, List<String> input, List<String> output) {
        this.caseID = caseID;
        this.source = source;
        this.target = target;
        this.input = input;
        this.output = output;
    }

    public Transformation(String caseID, String source, String target, String input, String output) {
        this.caseID = caseID;
        this.source = source;
        this.target = target;
        this.input = Collections.singletonList(input);
        this.output = Collections.singletonList(output);
    }

    public Transformation(List<String> input, List<String> output) {
        this.input = input;
        this.output = output;
    }

    public String toString() {
        return "<CaseID = " + this.caseID + "> (" + this.source + "," + this.target + "):  " + this.input + " <=> " + this.output;
    }

    public String getTarget() {
        return this.target;
    }

    public String getSource() {
        return this.source;
    }

    public List<String> getInput() {
        return this.input;
    }

    public List<String> getOutput() {
        return this.output;
    }

    public String getCaseID() {
        return this.caseID;
    }
}