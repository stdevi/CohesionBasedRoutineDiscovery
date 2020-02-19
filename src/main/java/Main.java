import cohesion.CohesionParser;
import cohesion.CohesionScorer;
import entity.Pattern;
import entity.Sequence;
import entity.event.Event;
import org.apache.commons.io.FilenameUtils;
import spmf.SPMFAlgorithmName;
import spmf.SPMFExecutor;
import spmf.SPMFParser;
import utils.Cutoff;
import utils.Formatter;
import utils.PropertyValues;
import utils.Writer;
import utils.parser.CSVLogParser;
import utils.parser.LogParser;
import utils.parser.XMLLogParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String logFile = args[0];

        LogParser logParser = getLogParser(logFile);
        List<Event> events = logParser.parseLogFile(logFile);
        StringBuilder spmfInput = SPMFParser.transformActionsToSPMF(events);

        // Write formatted input with aliases
        writeInputFile(spmfInput);

        // Execute SPMF Algorithm
        SPMFExecutor.runSPMFAlgorithm(SPMFAlgorithmName.BIDE, 10);

        // Parse sequences and itemsets
        CohesionParser cohesionParser = new CohesionParser();
        List<Sequence> sequences = cohesionParser.parseFormattedSequences(PropertyValues.getProperty("spmfFormattedInputFilePath"));
        List<Pattern> patterns = cohesionParser.parsePatterns(PropertyValues.getProperty("spmfOutputFilePath"));

        // Generate scores
        CohesionScorer scorer = new CohesionScorer();
        List<Pattern> scoredPatterns = scorer.scorePattern(patterns, sequences);
        System.out.println("\nAll patterns: ");
        scoredPatterns.forEach(System.out::println);

        double cutOffThreshold = 15;
        List<Pattern> topPatterns = Cutoff.cutPatterns(scoredPatterns, cutOffThreshold);
        System.out.println("\nTop patterns (cut-off = " + cutOffThreshold + "%): ");
        topPatterns.forEach(System.out::println);
    }

    private static void writeInputFile(StringBuilder data) {
        StringBuilder spmfFormattedInput = Formatter.formatData(data);
        Writer.writeFile(spmfFormattedInput, PropertyValues.getProperty("spmfFormattedInputFilePath"));
    }

    private static LogParser getLogParser(String logFile) {
        LogParser logParser;

        if (FilenameUtils.isExtension(logFile, "mxml")) {
            logParser = new XMLLogParser();
        } else if (FilenameUtils.isExtension(logFile, "csv")) {
            logParser = new CSVLogParser();
        } else {
            throw new IllegalArgumentException("Wrong log file extension!");
        }

        return logParser;
    }
}
