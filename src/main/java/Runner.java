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

    public void run(String logFilePath) {
        PatternService patternService = PatternService.getInstance();

        LOGGER.info("Parsing log file...");
        SequenceService.getInstance().parseCases(logFilePath);
        LOGGER.info("Complete parsing log.");

        LOGGER.info("Running SPMF...");
        SPMFService.getInstance().runSPMFAlgorithm(SPMFAlgorithmName.CloFast, 10);
        LOGGER.info("Complete SPMF processing.");

        LOGGER.info("Parsing sequences from SPMF input...");
        SequenceService.getInstance().parseSequences();
        LOGGER.info("Complete parsing sequences from SPMF input.");

        LOGGER.info("Parsing patterns from SPMF output...");
        patternService.parsePatterns();
        LOGGER.info("Complete parsing patterns from SPMF output.");

        LOGGER.info("Setting patterns cohesion score...");
        patternService.getPatterns().forEach(patternService::setCohesionScore);
        LOGGER.info("Complete setting patterns cohesion score.");

        List<Pattern> cutOffPatterns = patternService.getCutOffPatterns(20);

        LOGGER.info("Setting cut-off patterns coverages...");
        CoverageService.getInstance().setPatternsCumulativeCoverage(cutOffPatterns);
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

        LOGGER.debug("Assembled cut-off patterns: ");
        cutOffPatterns.forEach(System.out::println);
    }
}
