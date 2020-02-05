package spmf;

import utils.PropertyValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SPMFExecutor {

    public static void runSPMFAlgorithm(SPMFAlgorithmName algorithmName, int minSupport) {
        Process p;

        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(getCommandList(algorithmName, minSupport));
            p = pb.start();

            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getCommandList(SPMFAlgorithmName algorithmName, int minSupport) {
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
