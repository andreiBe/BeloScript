package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.interpreter.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@BeloScript
public class IO extends CustomBeloClass {
    @BeloScript
    public static String input(String prompt, Settings settings) throws IOException {
        settings.getOutput().println(prompt);
        return input(settings);
    }
    @BeloScript
    public static String input(Settings settings) throws IOException {
        return settings.getInput().nextLine();
    }
    @BeloScript
    public static int inputInt(Settings settings) throws IOException {
        return settings.getInput().nextInt();
    }
    @BeloScript
    public static String inputChar(Settings settings) throws IOException {
        return String.valueOf(settings.getInput().nextChar());
    }
    @BeloScript
    public static double inputFloat(Settings settings) throws IOException {
        return settings.getInput().nextDouble();
    }

    private static String getPath(String path, Settings settings) {
        Path p = Paths.get(path);
        if (!p.isAbsolute()) {
            String newPath = settings.getRootPath() + "/" + path;
            newPath = newPath.replace("//", "/");
            return newPath;
        }
        return path;
    }
    @BeloScript
    public static String read_file(Settings settings, String path) {
        path = getPath(path, settings);
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new BeloException("Can't read file: " + e.getMessage());
        }
    }
    @BeloScript
    public static void write_file(String path, Settings settings, String content) {
        path = getPath(path, settings);
        try (PrintWriter w = new PrintWriter(path, "UTF-8")){
            w.write(content);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new BeloException("Can't write file: " + e.getMessage());
        }
    }
    @BeloScript
    public static void print(BeloClass b, Settings settings) {
        settings.getOutput().println(b.asString());
    }
}
