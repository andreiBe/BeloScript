package com.patonki.beloscript.datatypes.function;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.basicTypes.JavaClassWrapper;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.Settings;
import com.patonki.datatypes.Pair;

import java.util.*;

public class Overloading extends BeloScriptFunction{
    private final List<Pair<BeloScriptFunction,List<Class<?>>>> possibleFunctions = new ArrayList<>();

    public Overloading(List<Pair<BeloScriptFunction,Class<?>[]>> possibleFunctions, String name) {
        super(name);

        for (Pair<BeloScriptFunction, Class<?>[]> p : possibleFunctions) {
            Class<?>[] ar = p.second();
            BeloScriptFunction f = p.first();
            List<Class<?>> theKey = new ArrayList<>();
            for (Class<?> arg : ar) {
                if (arg.equals(Settings.class)) continue;
                theKey.add(matchingBeloClassClass(arg));
            }
            this.possibleFunctions.add(new Pair<>(f,theKey));
        }
    }
    private static Class<?> matchingBeloClassClass(Class<?> clazz) {
        if (clazz == Integer.class || clazz == int.class
        || clazz == Double.class || clazz == double.class
        || clazz == Byte.class || clazz == byte.class
        || clazz == Long.class || clazz == long.class
        || clazz == Float.class || clazz == float.class
        || clazz == Short.class || clazz == short.class)
            return BeloDouble.class;
        if (clazz == String.class) return BeloString.class;
        return clazz;
    }
    @Override
    public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
        for (Pair<BeloScriptFunction, List<Class<?>>> possibleFunction : possibleFunctions) {
            List<Class<?>> expectedClasses = possibleFunction.second();
            if (expectedClasses.size() != args.size()) {
                continue;
            }
            boolean fail = false;
            for (int i = 0; i < expectedClasses.size(); i++) {
                Class<?> expected = expectedClasses.get(i);
                if (isMatching(expected, args.get(i)) == null) {
                    fail = true;
                    break;
                }
            }
            if (!fail) {
                BeloScriptFunction function = possibleFunction.first();
                function.setPos(getStart(),getEnd());
                return function.execute(context,args,res);
            }
        }
        return throwError(res,context,"Didn't find a function with these parameters");
    }
    private BeloClass isMatching(Class<?> expected, BeloClass value) {
        if (expected.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (value instanceof JavaClassWrapper && expected == ((JavaClassWrapper) value).getWrappedObject().getClass()) {
            return value;
        }
        if (expected == Character.class || expected == char.class
        && value instanceof BeloString && ((BeloString) value).size() == 1) {
            return value;
        }

        if (expected == Boolean.class || expected == boolean.class
        && value instanceof BeloDouble && (value.intValue() == 0 || value.intValue() == 1)) {
            return value;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Pair<BeloScriptFunction, List<Class<?>>> possibleFunction : possibleFunctions) {
            builder.append(possibleFunction.first()).append("\n");
        }
        return builder.toString();
    }
}
