import cohesion.CohesionParser;
import cohesion.CohesionScorer;
import entity.Action;
import entity.Pattern;
import entity.Sequence;
import spmf.SPMFAlgorithmName;
import spmf.SPMFExecutor;
import spmf.SPMFParser;
import utils.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String logFormat = args[0];
        SPMFParser spmfParser = new SPMFParser();
        StringBuilder spmfInput;

        if (logFormat.equals("-xml")) {
            spmfInput = getSPMFInputFromXML(args[1], spmfParser);
        } else if (logFormat.equals("-csv")) {
            spmfInput = getSPMFInputFromCSV(args[1], spmfParser);
        } else {
            throw new IllegalArgumentException("Specify first argument: '-xml' or '-csv'.");
        }

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
        scoredPatterns.forEach(System.out::println);
    }

    private static StringBuilder getSPMFInputFromCSV(String logFilePath, SPMFParser spmfParser) {
        List<Action> actions = CSVLogParser.parseLogFile(logFilePath);
        return spmfParser.transformActionsToSPMF(actions);
    }

    private static StringBuilder getSPMFInputFromXML(String logFilePath, SPMFParser spmfParser) {
        List<List<Action>> sequences = XMLLogParser.parseLogFile(logFilePath);
        return spmfParser.transformSequencesToSPMF(sequences);
    }

    private static void writeInputFile(StringBuilder data) {
        StringBuilder spmfFormattedInput = Formatter.formatData(data);
        Writer.writeFile(spmfFormattedInput, PropertyValues.getProperty("spmfFormattedInputFilePath"));
    }
}
