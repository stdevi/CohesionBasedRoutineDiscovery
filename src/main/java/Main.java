import cohesion.CohesionParser;
import cohesion.CohesionScorer;
import entity.Action;
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

        StringBuilder spmfFormattedInput = Formatter.formatData(spmfInput);
        // Write input with strings
        Writer.writeFile(spmfInput, PropertyValues.getProperty("spmfInputFilePath"));
        // Write formatted input with aliases
        Writer.writeFile(spmfFormattedInput, PropertyValues.getProperty("spmfFormattedInputFilePath"));

        // Execute SPMF Algorithm
        SPMFExecutor.runSPMFAlgorithm(SPMFAlgorithmName.BIDE, 5);

        // Parse sequences and itemsets
        CohesionParser cohesionParser = new CohesionParser();
        cohesionParser.parseSequences(PropertyValues.getProperty("spmfInputFilePath"));
        cohesionParser.parseItemsets(PropertyValues.getProperty("spmfOutputFilePath"));

        // Generate scores
        CohesionScorer scorer = new CohesionScorer(cohesionParser.getSequences(), cohesionParser.getItemsets());
        scorer.findScores();
        scorer.printSortedScores();
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
