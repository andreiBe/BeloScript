package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class VarAccessNode extends Node {
    private final Token token;
    private final String varName;

    public VarAccessNode(Token varName) {
        this.token = varName;
        this.start = token.getStart().copy();
        this.end = token.getEnd().copy();
        this.varName = token.getValue();
        this.visitMethod = this::visit;
    }

    public RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();

        BeloClass value = context.getSymboltable().get(varName);

        if (value == null) {
            return res.failure(new RunTimeError(
                    getStart(), getEnd(),
                    varName + " is not defined",context
            ));
        }
        value.setPos(getStart(),getEnd()).setContext(context);
        return res.success(value);
    }

    public String getVarName() {
        return varName;
    }


    @Override
    public String toString() {
        return "{var " + this.token + "}";
    }
}
