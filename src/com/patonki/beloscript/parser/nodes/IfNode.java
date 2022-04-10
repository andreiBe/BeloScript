package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.List;

public class IfNode extends Node {
    private final List<Case> cases;
    private final ElseNode elseCase;

    public IfNode(List<Case> cases, ElseNode elseCase) {
        this.cases = cases;
        this.elseCase = elseCase;
        if (cases.size() == 0) {
            return;
        }
        this.start = cases.get(0).getStatements().getStart();
        if (elseCase != null) {
            end = elseCase.getEnd();
        } else {
            end = cases.get(cases.size()-1).getStatements().getEnd();
        }
        this.visitMethod = this::visit;
    }
    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        for (Case cas : cases) {
            BeloClass conditionValue = res.register(cas.getCondition().getVisit().visit(context,interpreter));
            if (res.shouldReturn()) return res;

            if (conditionValue.isTrue()) {
                BeloClass linesValue = res.register(cas.getStatements().getVisit().visit(context,interpreter));
                if (res.shouldReturn()) return res;
                //TODO actual null value
                return res.success(cas.getShouldReturnNull() ? new BeloDouble(0) : linesValue);
            }
        }
        if (elseCase != null) {
            BeloClass linesValue = res.register(elseCase.getStatements().getVisit().visit(context,interpreter));
            if (res.shouldReturn()) return res;
            //TODO actual null value
            return res.success(elseCase.getShouldReturnNull() ? new BeloDouble(0) : linesValue);
        }
        //TODO actual null value
        return  res.success(new BeloDouble(0));
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
}
