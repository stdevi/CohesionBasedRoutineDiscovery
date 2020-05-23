package log.parser;

import org.apache.commons.io.FilenameUtils;

public class LogParserFactory {

    public static LogParser getLogParser(String logFile) {
        if (FilenameUtils.isExtension(logFile, "mxml")) {
            return new XMLLogParser();
        } else if (FilenameUtils.isExtension(logFile, "csv")) {
            return new CSVLogParser();
        } else {
            throw new IllegalArgumentException("Wrong log file extension!");
        }
    }
}
