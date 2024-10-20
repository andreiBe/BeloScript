package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.List;

public class IfNode extends Node {
    private final List<Case> cases;
    private final ElseNode elseCase;

    public IfNode(List<Case> cases, ElseNode elseCase, Position start, Position end) {
        this.cases = cases;
        this.elseCase = elseCase;
        if (cases.isEmpty()) {
            return;
        }
        this.start = start;
        this.end = end;
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        for (Case cas : cases) {
            BeloClass conditionValue = res.register(cas.getCondition().execute(context,interpreter));
            if (res.shouldReturn()) return res;

            if (conditionValue.isTrue()) {
                BeloClass linesValue = res.register(cas.getStatements().execute(context,interpreter));
                if (res.shouldReturn()) return res;
                return res.success(cas.getShouldReturnNull() ? new Null() : linesValue,
                        getStart(), getEnd(), context);
            }
        }
        if (elseCase != null) {
            BeloClass linesValue = res.register(elseCase.getStatements().execute(context,interpreter));
            if (res.shouldReturn()) return res;
            return res.success(elseCase.getShouldReturnNull() ? new Null(): linesValue,
                    getStart(),getEnd(),context);
        }
        return  res.success(new Null(),getStart(),getEnd(),context);
    }

    @Override
    public String toString() {
        if (elseCase != null) {
            return "\n{"+cases+" : else "+elseCase+"}\n";
        }
        return "\n{"+cases+"}\n";
    }

    public List<Case> getCases() {
        return cases;
    }

    public ElseNode getElseCase() {
        return elseCase;
    }

    @Override
    public String convertToJavaCode() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cases.size(); i++) {
            Case c = cases.get(i);
            result.append(i == 0 ? "if" : "else if");
            String condition = c.getCondition().convertToJavaCode();
            String content = c.getStatements().convertToJavaCode();
            result.append(String.format("(%s) {%s}", condition, content));
        }
        if (elseCase != null) {
            String content = elseCase.getStatements().convertToJavaCode();
            result.append(String.format("else {%s}", content));
        }
        return result.toString();
    }
}
