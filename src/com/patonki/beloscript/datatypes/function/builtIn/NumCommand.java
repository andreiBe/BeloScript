package com.patonki.beloscript.datatypes.function.builtIn;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.List;

public class NumCommand extends BeloScriptFunction {
    public NumCommand(String name) {
        super(name);
    }

    @Override
    public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
        String toConvert = args.get(0).toString();
        try{
            double num = Double.parseDouble(toConvert);
            return res.success(new BeloDouble(num));
        } catch (NumberFormatException e) {
            return res.failure(new RunTimeError(getStart(),getEnd(),"NumberFormatError",context));
        }
    }
}
