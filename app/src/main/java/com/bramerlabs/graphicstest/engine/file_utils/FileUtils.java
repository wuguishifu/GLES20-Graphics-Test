package com.bramerlabs.graphicstest.engine.file_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static String loadAsString(InputStream inputStream) {

        // create new string builder
        StringBuilder fileAsString = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // read each line
            String line;
            while ((line = reader.readLine()) != null) {
                fileAsString.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileAsString.toString();

    }

}
