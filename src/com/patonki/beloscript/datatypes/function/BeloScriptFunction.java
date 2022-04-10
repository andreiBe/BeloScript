package com.patonki.beloscript.datatypes.function;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.List;

public abstract class BeloScriptFunction extends BaseFunction{
    public BeloScriptFunction(String name) {
        super(name);
    }

    public abstract RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res);

    @Override
    public RunTimeResult execute(List<BeloClass> args) {
        RunTimeResult res = new RunTimeResult();
        Context context = generateNewContext();

        BeloClass returnValue = res.register(this.execute(context,args, new RunTimeResult()));
        if (res.shouldReturn()) return res;
        return res.success(returnValue);
    }

    protected RunTimeResult throwError(RunTimeResult res, Context context, String message) {
        return res.failure(new RunTimeError(getStart(), getEnd(), message, context));
    }

    protected RunTimeResult throwParameterSizeError(RunTimeResult res, Context context, int expectedSize, int size) {
        return res.failure(
                new RunTimeError(
                        getStart()
                        , getEnd()
                        , "Expected " + expectedSize + " parameters, but got: " + size
                        , context));
    }
}
