package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.parser.nodes.Node;

public interface PropertiesAccess {
    Node getPropertyValue(String key);
    BeloClass getStaticProperty(String key);
    void setStaticProperty(String key, BeloClass value);
    boolean hasStaticProperty(String key);
    AccessModifier getAccessModifierOfStaticProperty(String key);
    boolean staticPropertyIsFinal(String key);

    RuntimeProperties getRuntimeProperties(
            RunTimeResult res, Interpreter interpreter, Context context,
            String className);
}
