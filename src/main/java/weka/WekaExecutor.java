package weka;

import pattern.entity.Pattern;
import log.entity.Event;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class WekaExecutor {

    public void findRulesFromWekaFile(Map<String, List<Event>> cases, Pattern pattern) {
        try {
            WekaFormatter wekaFormatter = new WekaFormatter();
            wekaFormatter.createWekaFile(cases, pattern);

//            String wekaFilePath = PropertyValues.getProperty("wekFilePath");
//            BufferedReader datafile = readDataFile(wekaFilePath);
//
//            Instances data = new Instances(datafile);
//            data.setClassIndex(data.numAttributes() - 1);
//
//            String[] options = new String[3];
//            options[0] = "-N";
//            options[1] = "1.0";
//            options[2] = "-P";
//
//            JRip classifier = new JRip();
//            classifier.setOptions(options);
//            classifier.buildClassifier(data);

//            System.out.println(classifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }
}
