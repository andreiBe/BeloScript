package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class TryNode extends Node{
    private String errorVarName;
    private final Node body;
    private Node catchBody;

    public TryNode(String errorVarName, Node body, Node catchBody) {
        this.body = body;
        this.start = body.getStart();
        if (catchBody == null) {
            this.end = body.getEnd();
            this.visitMethod = this::noCatch;
        } else {
            this.errorVarName = errorVarName;
            this.catchBody = catchBody;
            this.end = catchBody.getEnd();
            this.visitMethod = this::visit;
        }
    }

    private RunTimeResult noCatch(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass result = res.register(body.execute(context,interpreter));
        if (res.hasError()) {
            return res.success(new Null());
        }
        if (res.shouldReturn()) return res;
        return res.success(result);
    }

    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass result = res.register(body.execute(context,interpreter));
        if (res.hasError()) {
            //Todo remake
            context.getSymboltable().set(errorVarName, new BeloError(res.getError()));
            result = res.register(catchBody.getVisit().visit(context,interpreter));
            if (res.shouldReturn()) return res;
        }
        if (res.shouldReturn()) return res;
        return res.success(result);
    }

    @Override
    public String toString() {
        return "{try body:"+body+" catch: "+catchBody+"}";
    }
}
