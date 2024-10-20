package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.ClassImporter;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.errors.BeloException;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;

public class CustomBeloClass extends BeloClass{
    public final HashMap<BeloClass,BeloClass> classValues = new HashMap<>();

    @Override
    public BeloClass classValue(BeloClass name) {
        BeloClass b = classValues.get(name);
        if (b == null) {
            return createNotAMemberOfClassError(name);
        }
        return b;
    }

    public static final Predicate<AccessibleObject> filter = accessibleObject ->
            accessibleObject.getAnnotation(BeloScript.class) != null;

    @SuppressWarnings("unchecked")
    public<T extends CustomBeloClass> T init_self() throws BeloException {
        ClassImporter.addMethods(this,
                ClassImporter.collectMethods(this.getClass(), filter).getClassMethods());
        return (T) this;
    }

    @Override
    public String toString() {
        return "CustomBeloClass{" +
                "classValues=" + classValues +
                '}';
    }
}
