package com.patonki.beloscript.datatypes.function.builtIn;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.List;

public class PrintCommand extends BeloScriptFunction{
    public PrintCommand(String name) {
        super(name);
    }

    @Override
    public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
        BeloClass toPrint = args.get(0);
        context.getSettings().getOutput().println(toPrint.toString());
        return res.success(new Null());
    }
}
