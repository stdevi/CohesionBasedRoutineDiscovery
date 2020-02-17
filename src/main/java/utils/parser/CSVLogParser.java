package utils.parser;

import entity.event.CSVEvent;
import entity.event.Event;
import utils.EventUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVLogParser implements LogParser {

    public List<Event> parseLogFile(String logFilePath) {
        List<Event> events = readCSV(logFilePath);

        HashMap<String, List<Event>> groupedEvents = EventUtils.groupByEventType(events);
        for (String group : groupedEvents.keySet()) {
            EventUtils.setContextAttributes(groupedEvents.get(group), 0.05, true);
        }

        return events;
    }

    private List<Event> readCSV(String path) {
        List<String> attributes = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        int counter = 0;
        int eid = 0;
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                if (Character.codePointAt(line, 0) == 0xFEFF) {
                    line = line.substring(1);
                }
                String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (counter == 0) {
                    counter++;
                    Arrays.setAll(row, i -> row[i].replaceAll("^\"(.*)\"$", "$1"));
                    Collections.addAll(attributes, row);
                } else {
                    Arrays.setAll(row, i -> row[i].matches("\"+") ? "\"\"" : row[i].replaceAll("^\"(.*)\"$", "$1"));
                    events.add(new CSVEvent(attributes, row, eid));
                    eid++;
                    counter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }
}