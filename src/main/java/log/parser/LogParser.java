package log.parser;

import log.entity.Event;

import java.util.List;

public interface LogParser {

    List<Event> parseLogFile(String logFilePath);
}
