package com.patonki.beloscript.interpreter;

import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.util.Reader;
import com.patonki.helper.FileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class Settings {
    private String jsondata;
    private PrintStream output = System.out;
    private Reader input = new Reader();
    private boolean logLexResult;
    private boolean logParseResult;
    private final String[] args;
    private final String rootPath;

    public Settings(String[] args, String root) throws BeloScriptException {
        this.args = args;
        this.rootPath = root;
        for (String arg : args) {
            if (arg.equals("logLex")) {
                logLexResult = true;
            }
            else if (arg.equals("logParse")) {
                logParseResult = true;
            }
            else if (arg.startsWith("input:")) {
                String inputFileName = arg.substring(6).trim();
                if (! new File(inputFileName).isAbsolute()) {
                    inputFileName = root+inputFileName;
                }
                try {
                    input = new Reader(inputFileName);
                } catch (IOException e) {
                    throw new BeloScriptException(new BeloScriptError("File not found","inputfile not found "+inputFileName));
                }
            }
            else if (arg.startsWith("json:")) {
                this.jsondata = arg.substring(5).trim();
            }
            else if (arg.startsWith("jsonfile:")) {
                String path = arg.substring(9).trim();
                File f = new File(path);
                if (! f.isAbsolute()) path = root+path;
                if (new File(path).exists()) {
                    this.jsondata = new FileHandler(path).currentContent();
                } else {
                    throw new BeloScriptException(new BeloScriptError("File not found","Json file:"+path));
                }
            }
            else if (arg.startsWith("output:")) {
                String outputFileName = arg.substring(7).trim();
                File f = new File(outputFileName);
                if (! f.isAbsolute()) outputFileName = root+outputFileName;
                new FileHandler(outputFileName).write("");
                try {
                    output = new PrintStream(outputFileName);
                } catch (FileNotFoundException e) {
                    throw new BeloScriptException(new BeloScriptError("File not found","Failed to create output file: "+outputFileName));
                }
            }
            else {
                throw new BeloScriptException(new BeloScriptError("Param error","Can't read param: "+arg));
            }
        }
    }

    public PrintStream getOutput() {
        return output;
    }

    public Reader getInput() {
        return input;
    }

    public boolean isLogLexResult() {
        return logLexResult;
    }

    public boolean isLogParseResult() {
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
}
