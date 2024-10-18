package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.interpreter.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@BeloScript
public class IO extends CustomBeloClass {
    @BeloScript
    public static String input(String prompt, Settings settings) {
        settings.getOutput().println(prompt);
        return input(settings);
    }
    @BeloScript
    public static String input(Settings settings) {
        try {
            return settings.getInput().nextLine();
        } catch (IOException e) {
            throw new BeloException(e.getMessage());
        }
    }
    @BeloScript
    public static String read_file(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new BeloException("Can't read file: " + e.getMessage());
        }
    }
    @BeloScript
    public static void write_file(String path, String content) {
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
