package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class StringNode extends Node {
    private final Token token;
    private final RunTimeResult res = new RunTimeResult();

    public StringNode(Token token) {
        this.token = token;
        this.start = token.getStart();
        this.end = token.getEnd();
        BeloString val = BeloString.create(token.getValue());
        res.success(val, getStart(), getEnd());
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        return res;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "{"+token.toString()+"}";
    }

    @Override
    public String convertToJavaCode() {
        return String.format("\"%s\"", token.getValue());
    }
}
