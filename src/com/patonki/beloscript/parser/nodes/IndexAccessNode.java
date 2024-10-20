package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class IndexAccessNode extends Node {
    private final Node target;
    private final Node index;

    public IndexAccessNode(Node target, Node index, Position end) {
        this.target = target;
        this.index = index;
        this.start = target.getStart();
        this.end = end;
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();

        BeloClass target = res.register(this.target.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        BeloClass index = res.register(this.index.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        //System.out.println("Index: " + index);
        BeloClass result = target.index(index);
        if (result.hasError()) {
            return res.failure(result.getError());
        }
        return res.success(result, getStart(), getEnd());
    }
    public Node getTarget() {
        return target;
    }

    public Node getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "{" + target + "["+index+"]" + "}";
    }
}
