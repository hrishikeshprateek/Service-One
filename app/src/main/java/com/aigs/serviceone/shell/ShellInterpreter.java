package com.aigs.serviceone.shell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellInterpreter {

    public static String executeCommand(String command, String directory) {
        StringBuilder output = new StringBuilder();

        try {
            Process process;
            if (directory != null) {
                process = new ProcessBuilder()
                        .command("sh", "-c", "cd \"" + directory + "\" && " + command)
                        .redirectErrorStream(true)
                        .start();
            } else {
                process = new ProcessBuilder()
                        .command("sh", "-c", command)
                        .redirectErrorStream(true)
                        .start();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

}
