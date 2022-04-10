package com.patonki.beloscript;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.BeloNull;
import com.patonki.beloscript.datatypes.function.builtIn.InputCommand;
import com.patonki.beloscript.datatypes.function.builtIn.NumCommand;
import com.patonki.beloscript.datatypes.function.builtIn.PrintCommand;
import com.patonki.beloscript.errors.BeloScriptError;
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
    private SymbolTable globalSymbolTable;
    private Interpreter interpreter;

    private void reset(String root) throws BeloScriptException {
        globalSymbolTable = new SymbolTable();
        try {
            Import.importEverything(globalSymbolTable, root);
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new BeloScriptException(new BeloScriptError("Import error",e.getMessage()));
        }
        globalSymbolTable.defineFunction("print",new PrintCommand("print"));
        globalSymbolTable.defineFunction("input",new InputCommand("input"));
        globalSymbolTable.defineFunction("toNum",new NumCommand("toNum"));

        globalSymbolTable.set("true",new BeloDouble(1));
        globalSymbolTable.set("false",new BeloDouble(0));
        globalSymbolTable.set("null",new BeloNull());
        this.interpreter = new Interpreter();
    }


    public static BeloClass runFile(String path, String... args) throws BeloScriptException {
        return new BeloScript().executeFile(path,args);
    }
    public static BeloClass run(String script, String fileName,String rootPath, String... args) throws BeloScriptException {
        return new BeloScript().execute(script,rootPath,fileName,args);
    }
    public BeloClass executeFile(String path,String... args) throws BeloScriptException {
        File file = new File(path);
        String name = file.getName();
        String rootPath = "";
        if (file.getParent() != null) rootPath = file.getParent()+"/";
        return execute(new FileHandler(path).currentContent(), rootPath,name,args);
    }
    public BeloClass execute(String script, String rootPath, String fileName, String... args) throws BeloScriptException{
        Settings settings = new Settings(args,rootPath);

        Lexer lexer = new Lexer(fileName == null ? "<anynymous>" : fileName, script);
        long now = System.currentTimeMillis();
        LexResult lexResult = lexer.makeTokens();
        if (lexResult.hasError()) {
            throw new BeloScriptException(lexResult.getError());
        }
        if (settings.isLogLexResult()) {
            System.out.println(lexResult);
        }
        // -------- Parser -----------
        Parser parser = new Parser(lexResult.getTokens());
        ParseResult parseResult = parser.parse();
        if (parseResult.hasError()) {
            throw new BeloScriptException(parseResult.getError());
        }
        if (settings.isLogParseResult()) {
            System.out.println(parseResult.getNode());
        }
        System.out.println("Compiling took: "+(System.currentTimeMillis()-now)+" millis");
        // ------- Interpreter -----------
        return run(settings, rootPath,parseResult.getNode());
    }
    private BeloClass run(Settings settings, String rootPath, Node node) throws BeloScriptException {
        reset(rootPath);
        Context context = new Context("<stdin>");
        context.setSymboltable(globalSymbolTable);
        context.setSettings(settings);
        RunTimeResult res = interpreter.execute(node, context);
        Import.closeEverything();
        if (res.hasError()) {
            throw new BeloScriptException(res.getError());
        }
        return res.getFunctionReturnValue();
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }
}
