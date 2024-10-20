package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class ExportNode extends Node{
    private final Node object;
    public ExportNode(Node object, Position start) {
        this.object = object;
        this.start = start;
        this.end = object.getEnd();
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass obj = res.register(object.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        interpreter.setExported(obj);
        return res.success(obj, getStart(),getEnd());
    }

    @Override
    public String toString() {
        return "{export" + object+"}";
    }
}
