package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.RunTimeError;
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
        this.visitMethod = this::visit;
        this.start = start;
        this.end = end;
    }
    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        ArrayList<BeloClass> elements = new ArrayList<>();

        while (true) {
            BeloClass conditionValue = res.register(condition.visitMethod.visit(context,interpreter));
            if (res.shouldReturn()) return res;

            if (!conditionValue.isTrue()) break;

            BeloClass value = res.register(statements.visitMethod.visit(context,interpreter));
            if (res.shouldReturn() && !res.isShouldContinue() && !res.isShouldBreak()) return res;
            if (res.isShouldContinue()) continue;
            if (res.isShouldBreak()) break;
            elements.add(value);
        }
        return res.success(List.create(elements),getStart(),getEnd(), context);
    }

    @Override
    public String toString() {
        return "{while statements:"+statements+" condition: "+condition+"}";
    }
}
