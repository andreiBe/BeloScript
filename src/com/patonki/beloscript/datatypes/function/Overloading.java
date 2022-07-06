package com.patonki.beloscript.datatypes.function;

import com.patonki.beloscript.Import;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.BeloNull;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.Settings;
import com.patonki.datatypes.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Overloading extends BeloScriptFunction{
    private final HashMap<List<Class<?>>, BeloScriptFunction> map = new HashMap<>();

    public Overloading(List<Pair<BeloScriptFunction,Class<?>[]>> possibleFunctions, String name) {
        super(name);
        for (Pair<BeloScriptFunction, Class<?>[]> p : possibleFunctions) {
            Class<?>[] ar = p.second();
            BeloScriptFunction f = p.first();
            ArrayList<Class<?>> theKey = new ArrayList<>();
            for (Class<?> arg : ar) {
                if (arg.equals(Settings.class)) continue;
                theKey.add(matchingBeloClassClass(arg));
            }
            map.put(theKey, f);
        }
    }
    private static Class<?> matchingBeloClassClass(Class<?> clazz) {
        if (clazz == Integer.class || clazz == int.class
        || clazz == Double.class || clazz == double.class
        || clazz == Byte.class || clazz == byte.class
        || clazz == Long.class || clazz == long.class
        || clazz == Float.class || clazz == float.class
        || clazz == Short.class || clazz == short.class
        || clazz == Boolean.class || clazz == boolean.class)
            return BeloDouble.class;
        if (clazz == Character.class || clazz == char.class
        || clazz == String.class) return BeloString.class;
        if (BeloClass.class.isAssignableFrom(clazz)) return clazz;
        return null;
    }
    @Override
    public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
        List<Class<?>> classList = new ArrayList<>();
        for (BeloClass arg : args) {
            classList.add(arg.getClass());
        }
        BeloScriptFunction function = map.get(classList);
        if (function == null) {
            for (List<Class<?>> classes : map.keySet()) {
                System.out.println(classes);
            }
            System.out.println(classList);
            return throwError(res,context,"Didn't find function with these parameters");
        }
        return function.execute(context,args,res);
    }

}
