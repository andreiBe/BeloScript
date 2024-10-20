package com.patonki.beloscript.util;

import com.patonki.beloscript.errors.BeloException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static void writeToFile(String path, String content) throws BeloException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        } catch (IOException e) {
            throw new BeloException("File can't be written to " + path);
        }
    }

    public static String readFile(String path) throws BeloException {
        try {
            return new String(Files.readAllBytes(Paths.get(path)),StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BeloException("File not found " + path);
        }
    }
}
