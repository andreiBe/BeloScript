package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.datatypes.oop.BeloClassExceptionObject;
import com.patonki.beloscript.datatypes.oop.BeloClassObject;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class ThrowNode extends Node{
    private final Node expr;
    public ThrowNode(Node expr, Position start, Position end) {
        this.expr = expr;
        this.start = start;
        this.end = end;
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass value = res.register(expr.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        if (!(value instanceof BeloClassObject) || !((BeloClassObject) value).isErrorClass()) {
            return res.failure(new RunTimeError(
                    getStart(), getEnd(), "Throw value not an error",context
            ));
        }
        value.setPos(getStart(), getEnd());
        value.setContext(context);
        ((BeloClassObject) value).initError();

        RunTimeError error = ((BeloClassObject) value).getErrorRecursive();
        error.setErrorObject(value);
        return res.failure(error);
    }
}
