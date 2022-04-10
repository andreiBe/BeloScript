package com.patonki.beloscript.datatypes.function.builtIn;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.io.IOException;
import java.util.List;

public class InputCommand extends BeloScriptFunction {
    public InputCommand(String name) {
        super(name);
    }

    @Override
    public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
        BeloClass toPrint = args.get(0);
        context.getSettings().getOutput().println(toPrint);
        try {
            String input = context.getSettings().getInput().nextLine();
            return res.success(new BeloString(input));
        } catch (IOException e) {
            return res.failure(new RunTimeError(
                getStart(),getEnd(),"Failed to open inputstream",context
            ));
        }
    }
}
