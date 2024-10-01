package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.datatypes.Pair;

import java.util.List;

public class SwitchNode extends Node{
    private final List<Pair<List<Node>, Node>> cases;
    private final Node defaultCase;
    private final Node var;

    public SwitchNode(List<Pair<List<Node>, Node>> cases, Node defaultCase, Node var, Position start, Position end) {
        this.cases = cases;
        this.defaultCase = defaultCase;
        this.var = var;
        this.start = start;
        this.end = end;
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass toCompare = res.register(this.var.execute(context, interpreter));
        if (res.shouldReturn()) return res;

        for (Pair<List<Node>, Node> aCase : cases) {
            for (Node compareAgainstNode : aCase.first()) {
                BeloClass toCompareAgainst = res.register(compareAgainstNode.execute(context, interpreter));
                if (toCompare.compare(toCompareAgainst) == 0) {
                    res.register(aCase.second().execute(context, interpreter));
                    if (res.hasError()) return res;
                    BeloClass returnValue = res.getFunctionReturnValue();
                    if (returnValue == null) returnValue = new Null();
                    return res.success(returnValue);
                }
            }
        }
        if (defaultCase != null) {
            res.register(defaultCase.execute(context, interpreter));
            if (res.hasError()) return res;

            BeloClass returnValue = res.getFunctionReturnValue();
            if (returnValue == null) returnValue = new Null();
            return res.success(returnValue);
        }
        return res.success(new Null());
    }
}
