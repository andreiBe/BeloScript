package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;

public class WhileNode extends Node {
    private final Node condition;
    private final Node statements;

    public WhileNode(Node condition, Node statements, Position start, Position end) {
        this.condition = condition;
        this.statements = statements;
        this.start = start;
        this.end = end;
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        ArrayList<BeloClass> elements = new ArrayList<>();

        while (true) {
            BeloClass conditionValue = res.register(condition.execute(context,interpreter));
            if (res.shouldReturn()) return res;

            if (!conditionValue.isTrue()) break;

            BeloClass value = res.register(statements.execute(context,interpreter));
            if (res.shouldReturn() && !res.isShouldContinue() && !res.isShouldBreak()) return res;
            if (res.isShouldContinue()) continue;
            if (res.isShouldBreak()) break;
            elements.add(value);
        }
        return res.success(List.create(elements),getStart(),getEnd());
    }

    @Override
    public String toString() {
        return "{while statements:"+statements+" condition: "+condition+"}";
    }
}
