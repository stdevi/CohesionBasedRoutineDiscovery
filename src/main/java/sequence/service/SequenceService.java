package sequence.service;

import log.entity.Event;
import log.parser.LogParser;
import log.parser.LogParserFactory;
import lombok.Data;
import sequence.Sequence;
import spmf.service.SPMFService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class SequenceService {
    private static final SequenceService INSTANCE = new SequenceService();

    private List<Sequence> sequences;
    private Map<String, List<Event>> cases;

    private SequenceService() {
    }

    public static SequenceService getInstance() {
        return INSTANCE;
    }

    public void parseCases(String logFilePath) {
        LogParser logParser = LogParserFactory.getLogParser(logFilePath);
        this.cases = new LinkedHashMap<>(logParser.parseLogFile(logFilePath).stream()
                .collect(Collectors.groupingBy(Event::getCaseID)));
    }

    public void parseSequences() {
        String path = SPMFService.getInstance().getInputSPMFFile().getAbsolutePath();
        List<String> sequences = parseRowSequences(path);
        sequences = removeSequencesDelimiters(sequences);
        sequences = encodeSequences(sequences);
        this.sequences = formatSequences(sequences);
    }

    private List<Sequence> formatSequences(List<String> rowSequences) {
        List<Sequence> sequences = new ArrayList<>();
        int id = 1;
        for (String s : rowSequences) {
            List<String> sequence = new ArrayList<>(Arrays.asList(s.split(",")));
            sequences.add(new Sequence(id, sequence));
            id++;
        }

        return sequences;
    }

    private List<String> removeSequencesDelimiters(List<String> sequences) {
        return sequences.stream()
                .map(sequence -> sequence
                        .replace(" -1 -2", "")
                        .replace(" -1 ", ","))
                .collect(Collectors.toList());
    }

    private List<String> parseRowSequences(String filePath) {
        List<String> sequences = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(sequences::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sequences;
    }

    private List<String> encodeSequences(List<String> sequences) {
        Map<String, String> aliasesMap;
        aliasesMap = sequences.stream().filter(el -> el.startsWith("@ITEM"))
                .collect(Collectors.toMap(
                        alias -> alias.replaceAll("@ITEM=([0-9]*)=.*", "$1"),
                        alias -> alias.replaceAll("@ITEM=[0-9]*=(\\w*)", "$1"))
                );

        sequences = sequences.stream().map(sequence -> Arrays.stream(sequence.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .map(actionAlias -> aliasesMap.getOrDefault(actionAlias, actionAlias))
                .collect(Collectors.joining(","))
        ).filter(sequence -> !sequence.startsWith("@")).collect(Collectors.toList());

        return sequences;
    }
}
