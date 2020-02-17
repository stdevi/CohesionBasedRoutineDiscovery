package utils.parser;

import entity.event.Event;

import java.util.List;

public interface LogParser {

    List<Event> parseLogFile(String logFilePath);
}
