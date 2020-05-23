package spmf.service;

import log.entity.Event;
import sequence.service.SequenceService;
import spmf.utils.SPMFFormatter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SPMFService {
    private static final SPMFService INSTANCE = new SPMFService();

    private File inputSPMFFile;
    private File outputSPMFFile;

    private SPMFService() {
        try {
            inputSPMFFile = File.createTempFile("spmf-input", null);
            inputSPMFFile.deleteOnExit();
            outputSPMFFile = File.createTempFile("spmf-output", null);
            outputSPMFFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SPMFService getInstance() {
        return INSTANCE;
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

        try (FileWriter fileWriter = new FileWriter(inputSPMFFile)) {
            fileWriter.write(spmfFormattedInput.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder transformActionsToSPMF() {
        Map<String, List<Event>> sequencePerCaseId = SequenceService.getInstance().getCases();
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
        cmdList.add(inputSPMFFile.getAbsolutePath());
        cmdList.add(outputSPMFFile.getAbsolutePath());
        cmdList.add(minSupport + "%");

        return cmdList;
    }

    public File getInputSPMFFile() {
        return inputSPMFFile;
    }

    public File getOutputSPMFFile() {
        return outputSPMFFile;
    }
}
