package spmf.service;

import log.entity.Event;
import sequence.service.SequenceService;
import spmf.utils.SPMFFormatter;
import utils.PropertyValues;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SPMFService {
    private SequenceService sequenceService;

    public SPMFService(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    public void runSPMFAlgorithm(SPMFAlgorithmName algorithmName, int minSupport) {
        writeSPMFInputFile();
        Process p;

        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(getCommandList(algorithmName, minSupport));
            p = pb.start();

            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeSPMFInputFile() {
        StringBuilder data = transformActionsToSPMF();
        StringBuilder spmfFormattedInput = SPMFFormatter.formatData(data);
        Writer.writeFile(spmfFormattedInput, PropertyValues.getProperty("spmfFormattedInputFilePath"));
    }

    private StringBuilder transformActionsToSPMF() {
        Map<String, List<Event>> sequencePerCaseId = sequenceService.getCases();
        StringBuilder spmfSequences = new StringBuilder();
        sequencePerCaseId.forEach((caseId, sequence) -> {
            sequence.forEach(event -> assembleEvent(spmfSequences, event));
            spmfSequences.append("-2\n");
        });

        return spmfSequences;
    }

    private void assembleEvent(StringBuilder result, Event event) {
        result.append(event.getEventType());
        String context = "";
        if (!event.getContext().values().isEmpty()) {
            if (event.getContext().containsKey("target.name")) {
                context = event.getContext().get("target.name");
            } else if (event.getContext().containsKey("target.id")) {
                context = event.getContext().get("target.id");
            } else {
                context = String.valueOf(event.getContext().values().toArray()[0]);
            }
        }
        result.append("+").append(context).append(" -1 ");
    }

    private List<String> getCommandList(SPMFAlgorithmName algorithmName, int minSupport) {
        List<String> cmdList = new ArrayList<>();
        cmdList.add("cmd");
        cmdList.add("/c");
        cmdList.add("java");
        cmdList.add("-jar");
        cmdList.add("spmf.jar");
        cmdList.add("run");
        cmdList.add(algorithmName.value);
        cmdList.add(PropertyValues.getProperty("spmfFormattedInputFilePath"));
        cmdList.add(PropertyValues.getProperty("spmfOutputFilePath"));
        cmdList.add(minSupport + "%");

        return cmdList;
    }
}
