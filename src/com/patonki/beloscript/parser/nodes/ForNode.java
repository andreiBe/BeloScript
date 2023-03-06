package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ForNode extends Node {
    private final Node startValue;
    private final Node condition;
    private final Node change;
    private final Node body;
    private final boolean shouldReturnNull;

    public ForNode(Node startValue, Node condition, Node change, Node body, boolean shouldReturnNull, Position start, Position end) {
        this.startValue = startValue;
        this.condition = condition;
        this.change = change;
        this.body = body;
        this.shouldReturnNull = shouldReturnNull;
        this.visitMethod = this::visit;
        this.start = start;
        this.end = end;
    }
    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        ArrayList<BeloClass> elements = new ArrayList<>();

        res.register(startValue.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        Supplier<Boolean> conditionFunc = () -> {
            BeloClass b = res.register(condition.execute(context,interpreter));
            return b.isTrue();
        };
        while (conditionFunc.get()) {
            BeloClass value = res.register(body.execute(context,interpreter));
            if (res.shouldReturn() && !res.isShouldContinue() && !res.isShouldBreak()) return res;

            if (res.isShouldBreak()) {
                break;
            }
            if (res.isShouldContinue()) {
                res.register(change.execute(context,interpreter));
                if (res.shouldReturn()) return res;
                continue;
            }
            res.register(change.execute(context,interpreter));
            if (res.shouldReturn()) return res;
            elements.add(value);
        }
        return res.success(List.create(elements),getStart(),getEnd(), context);
    }

    @Override
    public String toString() {
        return "{fornode: "+body+" "+startValue+" "+condition+" "+change+"}";
    }
}
