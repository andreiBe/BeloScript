package com.patonki.beloscript;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.oop.BeloErrorClassDefinition;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.errors.LocalizedBeloException;
import com.patonki.beloscript.interpreter.*;
import com.patonki.beloscript.lexer.LexResult;
import com.patonki.beloscript.lexer.Lexer;
import com.patonki.beloscript.parser.ParseResult;
import com.patonki.beloscript.parser.Parser;
import com.patonki.beloscript.parser.nodes.Node;
import com.patonki.helper.FileHandler;

import java.io.File;
import java.io.IOException;

public class BeloScript {
    private Interpreter interpreter;

    private SymbolTable createGlobalSymbolTable(String rootPath) {
        SymbolTable symbolTable = new SymbolTable();
        try {
            Import.importEverything(symbolTable, rootPath);
        } catch (BeloException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new LocalizedBeloException(new BeloScriptError("Import error",e.getMessage()));
        }
        symbolTable.set("Error", new BeloErrorClassDefinition());
        symbolTable.set("true",new BeloDouble(1));
        symbolTable.set("false",new BeloDouble(0));
        symbolTable.set("null",new Null());
        return symbolTable;
    }
    private void reset() throws LocalizedBeloException {
        this.interpreter = new Interpreter();
    }


    @SuppressWarnings("UnusedReturnValue")
    public static BeloClass runFile(String path, String... args) throws BeloException {
        return new BeloScript().executeFile(path,args);
    }
    @SuppressWarnings("UnusedReturnValue")
    public static BeloClass run(String script, String fileName,String rootPath, String... args) throws BeloException {
        return new BeloScript().execute(script,rootPath,fileName,args);
    }
    public static String convertFileToJavaCode(String path) {
        return new BeloScript().fileToJavaCode(path);
    }

    public BeloClass executeFile(String path,String... args) throws BeloException {
        File file = new File(path);
        String name = file.getName();
        String rootPath = "";
        if (file.getParent() != null) rootPath = file.getParent()+"/";
        FileHandler fileHandler = new FileHandler(path);
        String content = fileHandler.currentContent();
        IOException possibleError = fileHandler.close();
        if (possibleError != null) {
            throw new BeloException("Can't close file stream");
        }
        return execute(content, rootPath,name,args);
    }
    private Node parse(boolean logLexResult, boolean logParseResult, String fileName, String script) {
        Lexer lexer = new Lexer(fileName == null ? "<anynymous>" : fileName, script);
        //long now = System.currentTimeMillis();
        LexResult lexResult = lexer.makeTokens();
        if (lexResult.hasError()) {
            throw new LocalizedBeloException(lexResult.getError());
        }
        if (logLexResult) {
            System.out.println(lexResult);
        }
        // -------- Parser -----------
        Parser parser = new Parser(lexResult.getTokens());
        ParseResult parseResult = parser.parse();
        if (parseResult.hasError()) {
            throw new LocalizedBeloException(parseResult.getError());
        }
        if (logParseResult) {
            System.out.println(parseResult.getNode());
        }
        return parseResult.getNode();
    }
    private String fileToJavaCode(String path) {
        File file = new File(path);
        String name = file.getName();
        FileHandler fileHandler = new FileHandler(path);
        String content = fileHandler.currentContent();
        IOException possibleError = fileHandler.close();
        if (possibleError != null) {
            throw new BeloException("Can't close file stream");
        }
        return toJavaCode(content, name);
    }

    private String toJavaCode(String script, String fileName) {
        Node parsed = parse(false, false, fileName, script);
        return parsed.convertToJavaCode();
    }
    private BeloClass execute(String script, String rootPath, String fileName, String... args) throws BeloException{
        Settings settings = new Settings(args,rootPath);
        Node parsed = parse(settings.logLexResult(), settings.logParseResult(), fileName, script);

        //System.out.println("Compiling took: "+(System.currentTimeMillis()-now)+" milliseconds file:"+fileName);
        // ------- Interpreter -----------
        return run(settings, rootPath,parsed);
    }

    private BeloClass run(Settings settings, String rootPath, Node rootNode) throws LocalizedBeloException {
        reset();
        Context context = new Context("<stdin>");
        context.setSymboltable(createGlobalSymbolTable(rootPath));
        context.setSettings(settings);
        RunTimeResult res = interpreter.execute(rootNode, context);
        if (res.hasError()) {
            throw new LocalizedBeloException(res.getError());
        }
        return res.getFunctionReturnValue();
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }
}
