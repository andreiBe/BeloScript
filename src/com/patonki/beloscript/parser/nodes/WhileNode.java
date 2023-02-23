package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;

public class WhileNode extends Node {
    private final Node condition;
    private final Node statements;
    private final boolean multiline;

    public WhileNode(Node condition, Node statements, boolean multiline) {
        this.condition = condition;
        this.statements = statements;
        this.multiline = multiline;
        this.visitMethod = this::visit;
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
        try {
            return res.success(multiline ?
                    List.create(elements).setContext(context) : new BeloDouble(0));
        } catch (BeloException e) {
            e.printStackTrace();
            return res.failure(
                    new RunTimeError(getStart(),getEnd(), "Error with creating list!", context)
            );
        }
    }

    @Override
    public String toString() {
        return "{while statements:"+statements+" condition: "+condition+"}";
    }
}
