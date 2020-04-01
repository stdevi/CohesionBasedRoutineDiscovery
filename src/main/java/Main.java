import cohesion.CohesionParser;
import cohesion.CohesionScorer;
import cohesion.Cutoff;
import cohesion.entity.Pattern;
import cohesion.entity.Sequence;
import cohesion.service.PatternService;
import log.entity.Event;
import log.parser.LogParser;
import log.parser.LogParserFactory;
import noise.NoiseGenerator;
import spmf.executor.SPMFAlgorithmName;
import spmf.executor.SPMFExecutor;
import spmf.parser.SPMFParser;
import spmf.utils.SPMFFormatter;
import utils.PropertyValues;
import utils.Writer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String logFile = args[0];

        NoiseGenerator noiseGenerator = new NoiseGenerator();
        noiseGenerator.readLogFile(logFile);
        noiseGenerator.addNoise(100);
        noiseGenerator.writeLogFileWithNoise();

        String logFileWithNoise = args[1];
        LogParser logParser = LogParserFactory.getLogParser(logFile);
        List<Event> events = logParser.parseLogFile(logFile);
        StringBuilder spmfInput = SPMFParser.transformActionsToSPMF(events);

        // TODO: Delete the sulist in the production version, for now it is used for dev purposes.
        Map<String, List<Event>> cases = events.subList(0, 300).stream().collect(Collectors.groupingBy(Event::getCaseID));

        // Test converter
//        Converter converter = new Converter();
//        converter.parseCases(cases);
//        converter.print();

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

        // Find top 15% patterns
        double cutOffThreshold = 20;
        List<Pattern> topPatterns = Cutoff.cutPatterns(scoredPatterns, cutOffThreshold);
        System.out.println("\nTop patterns (cut-off = " + cutOffThreshold + "%): ");
        topPatterns.forEach(System.out::println);

        // Find coverage
//        Coverage coverage = new CumulativeCoverage();
//        coverage.findCumulativeCoveragePerPattern(topPatterns, sequences);
//        coverage.printCoverage();

        //JRipper Conditioning
//        WekaExecutor wekaExecutor = new WekaExecutor();
//        wekaExecutor.findRulesFromWekaFile(cases, topPatterns.get(1));

        // Find pattern dependencies
        PatternService patternService = new PatternService();
        patternService.setTransformations(cases, topPatterns.get(0));
        patternService.setDependencies(cases, topPatterns.get(0));
        patternService.setAutomatability(topPatterns.get(0));
        System.out.println(topPatterns.get(0).isAutomatable());
    }

    private static void writeInputFile(StringBuilder data) {
        StringBuilder spmfFormattedInput = SPMFFormatter.formatData(data);
        Writer.writeFile(spmfFormattedInput, PropertyValues.getProperty("spmfFormattedInputFilePath"));
    }
}
