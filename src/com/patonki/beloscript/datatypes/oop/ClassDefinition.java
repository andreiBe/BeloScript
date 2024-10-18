package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.List;

public interface ClassDefinition {
    List<String> getParameters();
    String getClassName();
    BeloClass copyWithAccessModifier(AccessModifier accessModifier);
    ClassDefinition getParent();

    RunTimeResult getAsParent(RunTimeResult res, List<BeloClass> params);

    BeloClass classValue(BeloClass name);

    BeloClass setClassValue(String string, BeloClass value);

    boolean isInstanceOf(BeloClass beloClass);
}
