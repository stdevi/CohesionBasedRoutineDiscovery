import evaluation.MetricService;
import metrics.CoverageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pattern.entity.Pattern;
import pattern.service.PatternService;
import sequence.service.SequenceService;
import spmf.service.SPMFAlgorithmName;
import spmf.service.SPMFService;

import java.util.List;
import java.util.stream.Collectors;

public class Runner {
    private static final Logger LOGGER = LogManager.getLogger(Runner.class);

    private SPMFService spmfService;
    private SequenceService sequenceService;
    private PatternService patternService;
    private CoverageService coverageService;
    private MetricService metricService;

    public void run(String logFilePath) {
        initServices();

        LOGGER.info("Parsing log file...");
        sequenceService.parseCases(logFilePath);
        LOGGER.info("Complete parsing log.");

        LOGGER.info("Running SPMF...");
        spmfService.runSPMFAlgorithm(SPMFAlgorithmName.CloFast, 10);
        LOGGER.info("Complete SPMF processing.");

        LOGGER.info("Parsing sequences from SPMF input...");
        sequenceService.parsedSequences();
        LOGGER.info("Complete parsing sequences from SPMF input.");

        LOGGER.info("Parsing patterns from SPMF output...");
        patternService.parsePatterns();
        LOGGER.info("Complete parsing patterns from SPMF output.");

        LOGGER.info("Setting patterns cohesion score...");
        patternService.getPatterns().forEach(patternService::setCohesionScore);
        LOGGER.info("Complete setting patterns cohesion score.");

        List<Pattern> cutOffPatterns = patternService.getCutOffPatterns(20);

        LOGGER.info("Setting cut-off patterns coverages...");
        coverageService.setPatternsCumulativeCoverage(cutOffPatterns);
        cutOffPatterns = cutOffPatterns.stream().filter(p -> p.getCoverage() > 0).collect(Collectors.toList());
        LOGGER.info("Complete setting cut-off patterns coverages.");

        LOGGER.info("Setting cut-off patterns transformations...");
        cutOffPatterns.forEach(patternService::setTransformations);
        LOGGER.info("Complete setting cut-off patterns transformations.");

        LOGGER.info("Setting cut-off patterns function dependencies...");
        cutOffPatterns.forEach(patternService::setDependencies);
        LOGGER.info("Complete setting cut-off patterns function dependencies...");

        LOGGER.info("Setting cut-off patterns automatability...");
        cutOffPatterns.forEach(patternService::setAutomatability);
        LOGGER.info("Complete setting cut-off patterns automatability.");

        LOGGER.info("Setting cut-off patterns RAI...");
        cutOffPatterns.forEach(patternService::setRAI);
        LOGGER.info("Complete setting cut-off patterns RAI.");

        LOGGER.debug("Cut-off patterns metrics:");
        System.out.println("Supports: " + cutOffPatterns.stream().mapToDouble(Pattern::getSupport).mapToObj(String::valueOf).collect(Collectors.joining(", ", "", "")));
        System.out.println("Lengths: " + cutOffPatterns.stream().mapToDouble(p -> p.getItems().size()).mapToObj(String::valueOf).collect(Collectors.joining(", ", "", "")));
        System.out.println("RAIs: " + cutOffPatterns.stream().mapToDouble(Pattern::getRAI).mapToObj(String::valueOf).collect(Collectors.joining(", ", "", "")));
        System.out.println("Total coverage: " + cutOffPatterns.stream().mapToDouble(Pattern::getCoverage).sum());
        metricService.printPatternsMetrics(cutOffPatterns);

        LOGGER.debug("Assembled cut-off patterns: ");
        cutOffPatterns.forEach(System.out::println);
    }

    private void initServices() {
        sequenceService = new SequenceService();
        spmfService = new SPMFService(sequenceService);
        patternService = new PatternService(sequenceService);
        coverageService = new CoverageService(sequenceService);
        metricService = new MetricService(sequenceService);
    }
}
