package utils;

import entity.Action;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVLogParser {

    public static List<Action> parseLogFile(String filePath) {
        List<Action> actions = new ArrayList<>();

        try {
            File input = new File(filePath);
            InputStream inputStream = new FileInputStream(input);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            actions = bufferedReader.lines().skip(1).map(CSVLogParser::getAction).collect(Collectors.toList());
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return actions;
    }

    private static Action getAction(String line) {
        String[] p = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        List<String> attributes = Arrays.stream(p).map(el -> {
            if (el == null || el.equals("")) {
                return null;
            } else {
                return el.substring(1, el.length() - 1);
            }
        }).collect(Collectors.toList());
        attributes.remove(0);

        Action action = new Action();
        action.setTimestamp(attributes.get(0));
        action.setConceptName(attributes.get(3));
        action.setType(attributes.get(11));
        action.setLabel(attributes.get(12));
        if (attributes.get(8) != null && !attributes.get(8).equals("")) {
            String address = attributes.get(8);
            action.setColumn(address.replaceAll("[0-9]", ""));
            action.setRow(address.replaceAll("[a-zA-Z]", ""));
        }

        return action;
    }
}