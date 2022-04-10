package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class StringNode extends Node {
    private final Token token;
    private final RunTimeResult res = new RunTimeResult();
    private final BeloString val;

    public StringNode(Token token) {
        this.token = token;
        this.start = token.getStart();
        this.end = token.getEnd();
        this.val = new BeloString(token.getValue());
        this.val.setPos(token.getStart(),token.getEnd());
        res.success(val);
        this.visitMethod = (context, interpreter) -> {
            val.setContext(context);
            return res;
        };
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "{"+token.toString()+"}";
    }
}
