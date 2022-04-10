package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public abstract class Node {
    public interface VisitMethod {
        RunTimeResult visit(Context context, Interpreter interpreter);
    }
    protected Position start;
    protected Position end;
    protected VisitMethod visitMethod;

    public final VisitMethod getVisit() {
        return visitMethod;
    }

    public RunTimeResult execute(Context context, Interpreter interpreter) {
        return this.visitMethod.visit(context,interpreter);
    }
    public final Position getStart() {
        return start;
    }

    public final Position getEnd() {
        return end;
    }
}
