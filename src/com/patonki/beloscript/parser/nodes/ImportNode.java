package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class ImportNode extends Node{
    private final String path;

    public ImportNode(Token stringToken) {
        this.path = stringToken.getValue();
        this.start = stringToken.getStart();
        this.end = stringToken.getEnd();
        this.visitMethod = this::visit;
    }

    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass object = res.register(interpreter.importFile(this,context));
        if (res.shouldReturn()) return res;

        return res.success(object);
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public String toString() {
        return "{import: "+path+"}";
    }
}
