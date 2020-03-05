package dataflow.utils;

import utils.PropertyValues;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonExecutor {

    public static String execPython() {
        String foofahPythonScriptPath = PropertyValues.getProperty("foofahPythonScriptPath");
        String transformationFilePath = PropertyValues.getProperty("transformationFilePath");
        String output = null;

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "python " + foofahPythonScriptPath +
                " --input " + transformationFilePath + " --timeout 3600");

        try {
            StringBuilder sb = new StringBuilder();
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0 && sb.toString().length() != 0 && !sb.toString().contains("Not Found")) {
                output = sb.substring(sb.indexOf("#\n# Data Transformation\n#") + 26);
                output = output.equals("\n") ? "Equality" : output;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
}
