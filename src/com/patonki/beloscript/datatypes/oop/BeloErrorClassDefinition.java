package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeloErrorClassDefinition extends BeloClass implements ClassDefinition{
    private static final String instanceString = BeloErrorClassDefinition.class.getName();
    @Override
    public RunTimeResult execute(List<BeloClass> args) {
        RunTimeResult res = new RunTimeResult();
        if (args.size() != 1) {
            return res.failure(new RunTimeError(getStart(), getEnd(),
                    "Argument count does not match. Should be " + 1
                            + " but was " + args.size()
                    , this.getContext()));
        }

        return createObject(res, args, AccessModifier.PUBLIC);
    }

    private RunTimeResult createObject(RunTimeResult res, List<BeloClass> args, AccessModifier accessModifier) {
        BeloClass details = args.get(0);
        return res.success(new BeloClassExceptionObject(instanceString, details, accessModifier));
    }

    @Override
    public List<String> getParameters() {
        ArrayList<String> params = new ArrayList<>();
        params.add("message");
        return params;
    }

    @Override
    public String getClassName() {
        return "Error";
    }

    @Override
    public BeloClass copyWithAccessModifier(AccessModifier accessModifier) {
        return new BeloErrorClassDefinition().setContext(context);
    }

    @Override
    public ClassDefinition getParent() {
        return null;
    }

    @Override
    public RunTimeResult getAsParent(RunTimeResult res, List<BeloClass> params) {
        return createObject(res, params, AccessModifier.PROTECTED);
    }

    @SuppressWarnings("StringEquality")
    @Override
    public boolean isInstanceOf(BeloClass beloClass) {
        if (!(beloClass instanceof BeloClassExceptionObject)) {
            return false;
        }
        //TODO kinda hacking!
        return ((BeloClassExceptionObject) beloClass).getInstanceString() == instanceString;
    }
}
