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
        spmfService.runSPMFAlgorithm(SPMFAlgorithmName.BIDE, 10);
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

        LOGGER.debug("Patterns sorted by cohesion score:");
        patternService.getSortedPatternsByCohesionScore().forEach(System.out::println);

        List<Pattern> cutOffPatterns = patternService.getCutOffPatterns(10);

        LOGGER.debug("Top patterns (cut-off = 20%):");
        cutOffPatterns.forEach(System.out::println);

        LOGGER.info("Setting cut-off patterns coverages...");
        coverageService.setPatternsCumulativeCoverage(cutOffPatterns);
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
        metricService.printPatternsMetrics(cutOffPatterns);

        String externalPatternsFile = "src/main/resources/external_logs/log10.txt";
        LOGGER.debug("Total coverage of the external patterns:");
        System.out.println(metricService.getExternalPatternsCoverage(externalPatternsFile));
        LOGGER.debug("External patterns metrics:");
        metricService.printExternalPatternsMetrics(externalPatternsFile);

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
