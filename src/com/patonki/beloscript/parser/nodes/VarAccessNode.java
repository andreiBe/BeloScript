package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class VarAccessNode extends Node {
    private final Token token;
    private final boolean isFinal;
    private final String varName;

    public VarAccessNode(Token varName, boolean isFinal) {
        this.token = varName;
        this.isFinal = isFinal;
        this.start = token.getStart().copy();
        this.end = token.getEnd().copy();
        this.varName = token.getValue();
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();

        BeloClass value = context.getSymboltable().get(varName);

        if (value == null) {
            return res.failure(new RunTimeError(
                    getStart(), getEnd(),
                    varName + " is not defined",context
            ));
        }
        return res.success(value, getStart(), getEnd(), context);
    }

    public String getVarName() {
        return varName;
    }

    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public String toString() {
        return "{var " + this.token + "}";
    }
}
