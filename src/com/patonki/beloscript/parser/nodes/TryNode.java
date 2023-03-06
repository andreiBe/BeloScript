package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.SymbolTable;

public class TryNode extends Node{
    private String errorVarName;
    private final Node body;
    private Node catchBody;

    public TryNode(String errorVarName, Node body, Node catchBody, Position start, Position end) {
        this.body = body;
        this.start = start;
        this.end = end;
        if (catchBody == null) {
            this.visitMethod = this::noCatch;
        } else {
            this.errorVarName = errorVarName;
            this.catchBody = catchBody;
            this.visitMethod = this::visit;
        }
    }

    private RunTimeResult noCatch(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass result = res.register(body.execute(context,interpreter));
        if (res.hasError()) {
            return res.success(new Null(),getStart(),getEnd());
        }
        if (res.shouldReturn()) return res;
        //result.setPos(body.getStart(),body.getEnd());
        return res.success(result, getStart(), getEnd());
    }

    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass result = res.register(body.execute(context,interpreter));
        if (res.hasError()) {
            Context newContext = new Context("catch block", context, this.catchBody.getStart());
            newContext.setSymboltable(new SymbolTable(newContext.getParent().getSymboltable()));
            newContext.getSymboltable().set(errorVarName, new BeloError(res.getError()));
            result = res.register(catchBody.execute(newContext,interpreter));
            if (res.shouldReturn()) return res;
        }
        if (res.shouldReturn()) return res;
        return res.success(result, getStart(), getEnd());
    }

    @Override
    public String toString() {
        return "{try body:"+body+" catch: "+catchBody+"}";
    }
}
