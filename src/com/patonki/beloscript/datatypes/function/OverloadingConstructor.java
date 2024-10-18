package com.patonki.beloscript.datatypes.function;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.datatypes.Pair;

import java.util.List;

public class OverloadingConstructor extends Overloading{
    private final Class<?> clazz;
    public OverloadingConstructor(List<Pair<BeloScriptFunction,
            Class<?>[]>> possibleFunctions, String name,
                                  Class<?> clazz) {
        super(possibleFunctions, name);
        this.clazz = clazz;
    }

    @Override
    public boolean parameterIsInstanceOfThis(BeloClass parameter) {
        return this.clazz.isAssignableFrom(parameter.getClass());
    }
}
