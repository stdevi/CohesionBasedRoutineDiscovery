import cohesion.CohesionParser;
import cohesion.CohesionScorer;
import entity.Action;
import entity.Pattern;
import spmf.SPMFAlgorithmName;
import spmf.SPMFExecutor;
import spmf.SPMFParser;
import utils.*;

import java.util.Comparator;
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

        StringBuilder spmfFormattedInput = Formatter.formatData(spmfInput);
        // Write input with strings
        Writer.writeFile(spmfInput, PropertyValues.getProperty("spmfInputFilePath"));
        // Write formatted input with aliases
        Writer.writeFile(spmfFormattedInput, PropertyValues.getProperty("spmfFormattedInputFilePath"));

        // Execute SPMF Algorithm
        SPMFExecutor.runSPMFAlgorithm(SPMFAlgorithmName.BIDE, 10);

        // Parse sequences and itemsets
        CohesionParser cohesionParser = new CohesionParser();
        List<String> spmfSequences = cohesionParser.parseFormattedSequences(PropertyValues.getProperty("spmfFormattedInputFilePath"));
        List<Pattern> spmfPatterns = cohesionParser.parsePatterns(PropertyValues.getProperty("spmfOutputFilePath"));

        // Generate scores
        CohesionScorer scorer = new CohesionScorer();
        List<Pattern> scoredPatterns = scorer.findScoresForPatterns(spmfSequences, spmfPatterns);

        // Print formatted patterns
        scoredPatterns.sort(Comparator.comparingInt(Pattern::getCohesionScore));
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
}
