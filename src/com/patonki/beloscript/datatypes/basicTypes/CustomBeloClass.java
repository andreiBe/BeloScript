package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;

import java.util.HashMap;

public class CustomBeloClass extends BeloClass{
    public HashMap<BeloClass,BeloClass> classValues = new HashMap<>();

    @Override
    public BeloClass classValue(BeloClass name) {
        BeloClass b = classValues.get(name);
        if (b == null) {
            return createNotAMemberOfClassError(name);
        }
        return b;
    }

    @Override
    public BeloClass setClassValue(String name, BeloClass newValue) {
        classValues.put(new BeloString(name),newValue);
        return newValue;
    }
}
