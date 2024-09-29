package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class ReturnNode extends Node {
    private final Node expr;

    public ReturnNode(Node expr, Position start, Position end) {
        this.expr = expr;
        this.start = start;
        this.end = end;
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass value = Null.NULL;
        if (expr != null) {
            value = res.register(expr.execute(context,interpreter));
            if (res.shouldReturn()) return res;
        }
        return res.successReturn(value);
    }

    @Override
    public String toString() {
        return "{return"+expr+"}";
    }
}
