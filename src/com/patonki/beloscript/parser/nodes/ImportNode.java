package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class ImportNode extends Node{
    private final String path;

    public ImportNode(Token stringToken, Position start) {
        this.path = stringToken.getValue();
        this.start = start;
        this.end = stringToken.getEnd();
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass object = res.register(interpreter.importFile(this,context));
        if (res.shouldReturn()) return res;
        return res.success(object, getStart(), getEnd());
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public String toString() {
        return "{import: "+path+"}";
    }
}
