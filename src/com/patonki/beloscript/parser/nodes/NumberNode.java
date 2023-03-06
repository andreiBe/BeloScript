package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class NumberNode extends Node {
    private final Token token;

    public NumberNode(Token token) {
        this.token = token;

        this.start = token.getStart();
        this.end = token.getEnd();

        this.value = new BeloDouble(this.token.getNumValue());
        this.res = new RunTimeResult().success(value, getStart(),getEnd());
        this.visitMethod = this::visit;
    }

    private final BeloClass value;
    private final RunTimeResult res;

    public RunTimeResult visit(Context context, Interpreter interpreter) {
        value.setContext(context);
        return res;
    }
    @Override
    public String toString() {
        return token.toString();
    }
}
