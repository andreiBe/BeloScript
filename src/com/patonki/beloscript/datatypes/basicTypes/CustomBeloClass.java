package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.errors.LocalizedBeloException;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.datatypes.function.Overloading;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.Settings;
import com.patonki.datatypes.Pair;

import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.patonki.beloscript.ImportUtil.*;


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
        classValues.put(BeloString.create_dont_use_optimized_version(name),newValue);
        return newValue;
    }
    public<T extends CustomBeloClass> T init_self() throws BeloException {
        addMethods(this, collectMethods(this.getClass()).classMethods);
        return (T) this;
    }

    @Override
    public String toString() {
        return "CustomBeloClass{" +
                "classValues=" + classValues +
                '}';
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
                    if (input[inputIndex] != null) inputIndex++;
                    input[inputIndex++] = arg;
                }

                if (input.length != parameterTypes.length)
                    return throwParameterSizeError(res, context, parameterTypes.length, input.length);
                for (int i = 0; i < input.length; i++) {
                    Object arg = input[i];
                    //BeloScript luokkaa vastaava Javan luokka
                    //Esim BeloString -> java.lang.String
                    Object prim = matchingPrimitive(arg, parameterTypes[i]);
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
                    runResult = matchingBeloClass(method.invoke(obj, input));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return throwError(res, context, "Error calling function");
                } catch (InvocationTargetException e) {
                    //oh no this is bad code
                    if (e.getCause() instanceof LocalizedBeloException) {
                        BeloScriptError error = ((LocalizedBeloException)e.getCause()).getError();
                        if (error instanceof RunTimeError) {
                            return res.failure((RunTimeError) error);
                        }
                    }
                    //e.printStackTrace();
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

    private static List<Pair<String, BeloClass>> collectStaticFunctions(ArrayList<Method> staticMethods) {
        return collectOverloads(staticMethods, null).stream()
                .map(p -> new Pair<String,BeloClass>(p.getTypeName(),p))
                .collect(Collectors.toList());
    }
    private static void addMethods(Object o, List<Method> classMethods) throws BeloException {
        List<Overloading> overloads = collectOverloads(classMethods, o);
        if (!(o instanceof CustomBeloClass)) {
            throw new BeloException("Class should inherit CustomBeloClass class");
            //return throwError(res,context,"Class should inherit CustomBeloClass class");
        }
        CustomBeloClass beloClass = (CustomBeloClass) o;

        for (Overloading overload : overloads) {
            beloClass.classValues.put(BeloString.create_dont_use_optimized_version(overload.getTypeName()),overload);
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
                            Object prim = matchingPrimitive(arg, parameterTypes[i]);
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
                    return throwError(res, context, e.getMessage());
                }
                try {
                    addMethods(o, classMethods);
                } catch (BeloException e) {
                    return throwError(res,context,"Class should inherit CustomBeloClass class");
                }
                return res.success((BeloClass) o);
            }
        };
    }

    private static Overloading createOverloadedConstructors(Class<?> clazz, List<Method> classMethods) {
        List<Pair<BeloScriptFunction, Class<?>[]>> overloadedConstructors = new ArrayList<>();

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getAnnotation(BeloScript.class) == null) continue;
            //TODO tähän tarkistus ettei ole public
            constructor.setAccessible(true);
            //löytyi konstruktori, joka käy
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            int settingsIndex = Arrays.asList(parameterTypes).indexOf(Settings.class);
            BeloScriptFunction cons = createConstructor(clazz,constructor,settingsIndex,parameterTypes,classMethods);
            overloadedConstructors.add(new Pair<>(cons,parameterTypes));
        }
        return new Overloading(overloadedConstructors,clazz.getSimpleName());
    }

    private static class Methods {
        ArrayList<Method> classMethods;
        ArrayList<Method> staticFunctions;

        public Methods(ArrayList<Method> classMethods, ArrayList<Method> staticMethods) {
            this.classMethods = classMethods;
            this.staticFunctions = staticMethods;
        }
    }
    private static Methods collectMethods(Class<?> clazz) {
        ArrayList<Method> classMethods = new ArrayList<>();
        ArrayList<Method> staticMethods = new ArrayList<>();
        //käy läpi metodit ja erittelee ne staattisiin ja oliometodeihin
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(BeloScript.class) == null) continue;
            if (isNotValidType(method.getReturnType())) continue;
            if (!Modifier.isStatic(method.getModifiers())) {
                classMethods.add(method);
            } else staticMethods.add(method);
        }
        return new Methods(classMethods, staticMethods);

    }

    private static ArrayList<Pair<String, BeloClass>> collectFields(Class<?> clazz) throws IllegalAccessException {
        ArrayList<Pair<String, BeloClass>> ar = new ArrayList<>();
        //käy kaikki luokan staattiset luokkamuuttujat läpi ja tuo
        //ne BeloScript ohjelman ulottuville
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            //Ei sisällä BeloScript annotaatiota
            if (field.getAnnotation(BeloScript.class) == null) {
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

    public static void addMethodsAndFields(Class<?> clazz, List<Pair<String, BeloClass>> list) throws IllegalAccessException {
        list.addAll(collectFields(clazz));
        Methods methods = collectMethods(clazz);
        list.addAll(collectStaticFunctions(methods.staticFunctions));

        if (methods.classMethods.isEmpty()) return;
        Overloading overloading = createOverloadedConstructors(clazz,methods.classMethods);

        list.add(new Pair<>(clazz.getSimpleName(),overloading));
    }
}
