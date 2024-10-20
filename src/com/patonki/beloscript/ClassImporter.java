package com.patonki.beloscript;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.basicTypes.JavaClassWrapper;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.datatypes.function.Overloading;
import com.patonki.beloscript.datatypes.function.OverloadingConstructor;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.errors.LocalizedBeloException;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.Settings;
import com.patonki.datatypes.Pair;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.patonki.beloscript.ImportUtil.*;

public class ClassImporter {

    public static class Methods {
        private final ArrayList<Method> classMethods;
        private final ArrayList<Method> staticFunctions;

        public Methods(ArrayList<Method> classMethods, ArrayList<Method> staticMethods) {
            this.classMethods = classMethods;
            this.staticFunctions = staticMethods;
        }

        public ArrayList<Method> getClassMethods() {
            return classMethods;
        }
    }
    public static Methods collectMethods(Class<?> clazz, Predicate<AccessibleObject> filter) {
        ArrayList<Method> classMethods = new ArrayList<>();
        ArrayList<Method> staticMethods = new ArrayList<>();
        //käy läpi metodit ja erittelee ne staattisiin ja oliometodeihin
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (!filter.test(method)) continue;
            if (!Modifier.isStatic(method.getModifiers())) {
                classMethods.add(method);
            } else staticMethods.add(method);
        }
        return new Methods(classMethods, staticMethods);
    }

    private static ArrayList<Pair<String, BeloClass>> collectFields(
            Class<?> clazz,
            Predicate<AccessibleObject> filter
    ) throws IllegalAccessException {
        ArrayList<Pair<String, BeloClass>> ar = new ArrayList<>();
        //käy kaikki luokan staattiset luokkamuuttujat läpi ja tuo
        //ne BeloScript ohjelman ulottuville
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (!filter.test(field)) {
                continue;
            }
            //Ei ole arvo, jonka voi muuttaa BeloScript objektiksi
            if (isNotValidType(field.getType())) continue;
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!Modifier.isFinal(field.getModifiers())) continue;

            //asetetaan arvo
            BeloClass arvo = matchingBeloClass(field.get(null));

            ar.add(new Pair<>(field.getName(),arvo));
        }
        return ar;
    }

    public static void addMethodsAndFields(Class<?> clazz, List<Pair<String, BeloClass>> list,
                                           Predicate<AccessibleObject> filter) throws IllegalAccessException {
        list.addAll(collectFields(clazz, filter));
        Methods methods = collectMethods(clazz, filter);
        list.addAll(collectStaticFunctions(methods.staticFunctions));

        //no need to instantiate class
        if (methods.classMethods.isEmpty()) return;
        Overloading overloading = createOverloadedConstructors(clazz,methods.classMethods, filter);

        list.add(new Pair<>(clazz.getSimpleName(),overloading));
    }
    private static List<Pair<String, BeloClass>> collectStaticFunctions(ArrayList<Method> staticMethods) {
        return collectOverloads(staticMethods, null).stream()
                .map(p -> new Pair<String,BeloClass>(p.getTypeName(),p))
                .collect(Collectors.toList());
    }

    private static BeloScriptFunction createFunction(Method method, Object obj) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        int settingsIndex = Arrays.asList(parameterTypes).indexOf(Settings.class);
        return new BeloScriptFunction(method.getName()) {
            @Override
            public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
                Object[] input = new Object[args.size() + (settingsIndex != -1 ? 1 : 0)];
                if (settingsIndex != -1) input[settingsIndex] = context.getSettings();
                //täytetään input array
                int inputIndex = 0;
                for (BeloClass arg : args) {
                    //skipping settings
                    if (input[inputIndex] != null) inputIndex++;
                    input[inputIndex++] = arg;
                }

                if (input.length != parameterTypes.length)
                    return throwParameterSizeError(res, context, parameterTypes.length, input.length);
                for (int i = 0; i < input.length; i++) {
                    Object arg = input[i];
                    //BeloScript luokkaa vastaava Javan luokka
                    //Esim BeloString -> java.lang.String
                    Object prim = matchingJavaClass(arg, parameterTypes[i]);
                    // ei sama luokka
                    if (!parameterTypes[i].isAssignableFrom(arg.getClass())) {
                        //primitiivinen arvo on sama
                        if (prim != null && prim.getClass().equals(objectArvo(parameterTypes[i]))) {
                            input[i] = prim; //päivitetään listaan oikea arvo
                        } else {
                            return throwError(res, context,
                                    "Parameter " + (i + 1) + " should be "
                                            + parameterTypes[i].getName() + " but was " + arg.getClass().getName());
                        }
                    }
                }
                BeloClass runResult;
                try {
                    Object returnValue = method.invoke(obj, input);
                    runResult = matchingBeloClass(returnValue);
                    if (runResult == null) {
                        runResult = copyMethodsToCustom(returnValue, collectMethods(returnValue.getClass(),
                                (e) -> true).classMethods);
                    }
                } catch (IllegalAccessException e) {
                    return throwError(res, context, "IllegalAccess Error calling function");
                } catch (InvocationTargetException e) {
                    //oh, no this is bad code
                    if (e.getCause() instanceof LocalizedBeloException) {
                        BeloScriptError error = ((LocalizedBeloException)e.getCause()).getError();
                        if (error instanceof RunTimeError) {
                            return res.failure((RunTimeError) error);
                        }
                    }
                    String message = e.getCause().getMessage();
                    if (message == null) message = e.getCause().toString();
                    return throwError(res, context, message);
                }
                return res.success(runResult);
            }
        };
    }

    private static List<Overloading> collectOverloads(List<Method> methods, Object obj) {
        ArrayList<Overloading> res = new ArrayList<>();
        methods.sort(Comparator.comparing(Method::getName));
        List<Pair<BeloScriptFunction, Class<?>[]>> list = new ArrayList<>();
        String prevName = null;
        for (Method method : methods) {
            BeloScriptFunction function = createFunction(method, obj);
            if (prevName == null || method.getName().equals(prevName)) {
                list.add(new Pair<>(function, method.getParameterTypes()));
            } else {
                String name = list.get(0).first().getTypeName();
                Overloading overloading = new Overloading(list, name);

                res.add(overloading);
                list = new ArrayList<>();
                list.add(new Pair<>(function,method.getParameterTypes()));
            }
            prevName = method.getName();
        }
        if (!list.isEmpty()) {
            String name = list.get(0).first().getTypeName();
            Overloading overloading = new Overloading(list, name);
            res.add(overloading);
        }
        return res;
    }

    public static CustomBeloClass copyMethodsToCustom(Object o, List<Method> classMethods) {
        JavaClassWrapper customBeloClass = new JavaClassWrapper(o);
        List<Overloading> overloads = collectOverloads(classMethods, o);
        for (Overloading overload : overloads) {
            customBeloClass.classValues.put(BeloString.create_optimized(overload.getTypeName()),overload);
        }
        return customBeloClass;
    }
    public static void addMethods(CustomBeloClass beloClass, List<Method> classMethods) throws BeloException {
        List<Overloading> overloads = collectOverloads(classMethods, beloClass);

        for (Overloading overload : overloads) {
            beloClass.classValues.put(BeloString.create_optimized(overload.getTypeName()),overload);
        }
    }
    private static BeloScriptFunction createConstructor(Class<?> clazz, Constructor<?> constructor,
                                                        int settingsIndex, Class<?>[] parameterTypes,
                                                        List<Method> classMethods) {
        return new BeloScriptFunction(clazz.getSimpleName()) {
            @Override
            public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
                Object o; //objekti, joka luodaan joko konstruktorilla tai ilman konstruktoria
                try {
                    //ilman konstruktoria
                    if (constructor == null) {
                        o = clazz.newInstance();
                    } else { //konstruktorilla
                        Object[] input = new Object[args.size() + (settingsIndex != -1 ? 1 : 0)];
                        if (settingsIndex != -1) input[settingsIndex] = context.getSettings();
                        //täytetään input array
                        int inputIndex = 0;
                        for (BeloClass arg : args) {
                            if (input[inputIndex] != null) inputIndex++;
                            input[inputIndex++] = arg;
                        }
                        if (input.length != parameterTypes.length)
                            return throwParameterSizeError(res, context, parameterTypes.length, input.length);

                        for (int i = 0; i < input.length; i++) {
                            Object arg = input[i];
                            //BeloScript luokkaa vastaava Javan luokka
                            //Esim BeloString -> java.lang.String
                            Object prim = matchingJavaClass(arg, parameterTypes[i]);
                            // ei sama luokka
                            if (!parameterTypes[i].isAssignableFrom(arg.getClass())) {
                                //primitiivinen arvo on sama
                                if (prim != null && prim.getClass().equals(objectArvo(parameterTypes[i]))) {
                                    input[i] = prim; //päivitetään listaan oikea arvo
                                } else {
                                    return throwError(res, context,
                                            "Parameter " + (i + 1) + " should be "
                                                    + parameterTypes[i].getName() + " but was " + arg.getClass().getName());
                                }
                            }
                        }
                        o = constructor.newInstance(input);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    if (e instanceof InvocationTargetException) {
                        return throwError(res, context, e.getCause().getMessage());
                    }
                    return throwError(res, context, e.getMessage());
                }
                if (o instanceof CustomBeloClass) {
                    addMethods((CustomBeloClass) o, classMethods);
                    return res.success((BeloClass) o);
                } else {
                    return res.success(copyMethodsToCustom(o, classMethods));
                }
            }
        };
    }

    private static Overloading createOverloadedConstructors(Class<?> clazz, List<Method> classMethods,
                                                            Predicate<AccessibleObject> filter) {
        List<Pair<BeloScriptFunction, Class<?>[]>> overloadedConstructors = new ArrayList<>();

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (!filter.test(constructor)) continue;
            constructor.setAccessible(true);
            //löytyi konstruktori, joka käy
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            int settingsIndex = Arrays.asList(parameterTypes).indexOf(Settings.class);
            BeloScriptFunction cons = createConstructor(clazz,constructor,settingsIndex,parameterTypes,classMethods);
            overloadedConstructors.add(new Pair<>(cons,parameterTypes));
        }
        return new OverloadingConstructor(overloadedConstructors,clazz.getSimpleName(), clazz);
    }
    public static void addMarkedFieldsFromClass(Class<?> clazz,  List<Pair<String, BeloClass>> imported) throws IllegalAccessException, BeloException {
        Predicate<AccessibleObject> filter = CustomBeloClass.filter;
        if (clazz.getAnnotation(BeloScript.class) == null) return;

        if (!CustomBeloClass.class.isAssignableFrom(clazz)) {
            throw new BeloException("Class should inherit CustomBeloClass class");
        }
        ClassImporter.addMethodsAndFields(clazz, imported, filter);
    }

    public static void addAllFieldsFromClass(Class<?> clazz,  List<Pair<String, BeloClass>> imported) throws IllegalAccessException {
        ClassImporter.addMethodsAndFields(clazz, imported, a -> true);
    }
}
