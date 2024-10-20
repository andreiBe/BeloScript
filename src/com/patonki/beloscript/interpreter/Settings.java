package com.patonki.beloscript.interpreter;

import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.util.FileUtil;
import com.patonki.beloscript.util.Reader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {
    private String jsondata;
    private PrintStream output = System.out;
    private Reader input = new Reader();
    private boolean logLexResult;
    private boolean logParseResult;
    private final String[] args;
    private final String rootPath;

    private static final ArrayList<Reader> inputReaders = new ArrayList<>();
    private static final HashMap<File, PrintStream> printStreams = new HashMap<>();

    private Reader createInputReader(String arg, String rootPath) throws BeloException {
        //komento alkaa input:, joten se otetaan pois
        String inputFileName = arg.substring(6).trim();
        if (! new File(inputFileName).isAbsolute()) {
            inputFileName = rootPath+inputFileName;
        }
        try {
            Reader reader = new Reader(inputFileName);
            inputReaders.add(reader);
            return reader;
        } catch (IOException e) {
            throw new BeloException("Input file not found:" + inputFileName);
        }
    }
    private PrintStream createOutputPrinter(String arg, String rootPath) throws BeloException {
        String outputFileName = arg.substring(7).trim();
        File f = new File(outputFileName);
        if (printStreams.containsKey(f)) {
            return printStreams.get(f);
        }

        if (! f.isAbsolute()) outputFileName = rootPath+outputFileName;
        FileUtil.writeToFile(outputFileName, "");
        try {
            PrintStream p = new PrintStream(outputFileName);
            printStreams.put(f, p);
            return p;
        } catch (FileNotFoundException e) {
           throw new BeloException("Failed to create outputstream from file:"+outputFileName);
        }
    }
    private String readJsonFile(String arg, String rootPath) throws BeloException {
        String path = arg.substring(9).trim();
        File f = new File(path);
        if (! f.isAbsolute()) path = rootPath+path;
        if (new File(path).exists()) {
            return FileUtil.readFile(path);
        } else {
            throw new BeloException("Json-input file not found: "+path);
        }
    }
    public Settings(String[] args, String root) throws BeloException {
        this.args = args;
        this.rootPath = root;
        inputReaders.add(this.input);
        for (String arg : args) {
            if (arg.equals("logLex")) {
                logLexResult = true;
            }
            else if (arg.equals("logParse")) {
                logParseResult = true;
            }
            else if (arg.startsWith("input:")) {
                this.input = createInputReader(arg,rootPath);
            }
            else if (arg.startsWith("json:")) {
                this.jsondata = arg.substring(5).trim();
            }
            else if (arg.startsWith("jsonfile:")) {
                this.jsondata = readJsonFile(arg,rootPath);
            }
            else if (arg.startsWith("output:")) {
                this.output = createOutputPrinter(arg,rootPath);
            }
            else {
                throw new BeloException("Not a valid option " + arg);
            }
        }
    }

    public PrintStream getOutput() {
        return output;
    }

    public Reader getInput() {
        return input;
    }

    public boolean logLexResult() {
        return logLexResult;
    }

    public boolean logParseResult() {
        return logParseResult;
    }

    public String[] getArgs() {
        return args;
    }

    public String getJsondata() {
        return jsondata;
    }

    public String getRootPath() {
        return rootPath;
    }

    public static void close() {
        for (PrintStream value : printStreams.values()) {
            value.close();
        }
        printStreams.clear();

        for (Reader inputReader : inputReaders) {
            try {
                inputReader.close();
            } catch (IOException ignored) {}
        }
    }
}
