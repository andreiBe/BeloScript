package com.patonki.beloscript.datatypes.function;


import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.parser.nodes.Node;

import java.util.List;

public class BeloFunction extends BaseFunction{
    private final Node body;
    private final List<String> argNames;
    private final boolean shouldAutoReturn;

    public BeloFunction(String name, Node body, List<String> argNames, boolean shouldAutoReturn) {
        super(name);
        this.body = body;
        this.argNames = argNames;
        this.shouldAutoReturn = shouldAutoReturn;
    }
    @Override
    public RunTimeResult execute(List<BeloClass> args) {
        RunTimeResult res = new RunTimeResult();
        Interpreter interpreter = new Interpreter();
        System.out.println("OLD Context: " + context);
        Context newContext = generateNewContext();
        System.out.println("NEW Context: " + newContext);

        res.register(checkAndPopulateArgs(argNames,args,newContext));
        if (res.shouldReturn()) return res;
        BeloClass value = res.register(interpreter.execute(body,newContext));
        if (res.shouldReturn() && res.getFunctionReturnValue() == null){
            return res;
        }
        BeloClass returnValue = (shouldAutoReturn ? value :
                res.getFunctionReturnValue() != null ?
                res.getFunctionReturnValue() :
                new Null());
        return res.success(returnValue, getStart(), getEnd(),context);
    }

    @Override
    public BeloClass copy() {
        BeloClass copy = new BeloFunction(this.name,this.body,this.argNames,this.shouldAutoReturn);
        copy.setContext(context);
        return copy;
    }

    @Override
    public String toString() {
        return "Func("
                + argNames +
                ')';
    }
}
