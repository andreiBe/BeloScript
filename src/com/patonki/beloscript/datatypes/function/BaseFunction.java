package com.patonki.beloscript.datatypes.function;


import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.SymbolTable;
import com.patonki.beloscript.datatypes.BeloClass;

import java.util.List;

public abstract class BaseFunction extends BeloClass{
    protected final String name;
    public BaseFunction(String name) {
        this.name = name == null ? "<anonymous>" : name;
    }

    protected Context generateNewContext() {
        Context newContext = new Context(this.name, this.context, this.getStart());
        newContext.setSymboltable(new SymbolTable(newContext.getParent().getSymboltable()));
        return newContext;
    }
    protected RunTimeResult checkArgs(List<String> argNames, List<BeloClass> args) {
        RunTimeResult res = new RunTimeResult();

        if (args.size() > argNames.size()) {
            return res.failure(new RunTimeError(
                    getStart(), getEnd(),
                    "Too many args",context
            ));
        }
        if (args.size() < argNames.size()) {
            return res.failure(new RunTimeError(
                    getStart(), getEnd(),
                    "Too few args",context
            ));
        }
        return res.success(null);
    }
    protected void populateArgs(List<String> argNames, List<BeloClass> args, Context context) {
        for (int i = 0; i < args.size(); i++) {
            String name = argNames.get(i);
            BeloClass value = args.get(i);
            value.setContext(context);
            context.getSymboltable().set(name,value);
        }
    }
    protected RunTimeResult checkAndPopulateArgs(List<String> argNames, List<BeloClass> args, Context context) {
        RunTimeResult res = new RunTimeResult();
        res.register(checkArgs(argNames, args));
        if (res.shouldReturn()) return res;
        populateArgs(argNames, args, context);
        return res.success(null);
    }
}
