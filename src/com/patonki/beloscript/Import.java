package com.patonki.beloscript;

import com.patonki.beloscript.builtInLibraries.LibJson;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.datatypes.function.Overloading;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.Settings;
import com.patonki.beloscript.interpreter.SymbolTable;
import com.patonki.datatypes.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.patonki.beloscript.ImportUtil.*;

public class Import {
    private static final ArrayList<BeloLibrary> libraries = new ArrayList<>();
    private static boolean haveBeenImported = false;

    public static void closeEverything() {
        for (BeloLibrary library : Import.libraries) {
            library.close();
        }
        libraries.clear();
        haveBeenImported = false;
    }

    public static void importEverything(SymbolTable globalSymbolTable, String root) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        importBuiltInLibraries(globalSymbolTable);
        importEveryJarFile(globalSymbolTable, root);
    }

    private static void importBuiltInLibraries(SymbolTable globalSymbolTable) {
        LibJson json = new LibJson();
        json.addToSymbolTable(globalSymbolTable);
    }

    private static void importEveryJarFile(SymbolTable globalSymbolTable, String root) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (haveBeenImported) {
            for (BeloLibrary library : Import.libraries) {
                library.addToSymbolTable(globalSymbolTable);
            }
            return;
        }
        File libraries = new File(root + "lib");
        if (!libraries.exists()) return;
        //Ohjelman oma class loader
        ClassLoader main = Import.class.getClassLoader();
        File[] jars = libraries.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null) throw new NullPointerException("Can't find files");
        URL[] urls = new URL[jars.length];
        for (int i = 0; i < jars.length; i++) {
            urls[i] = jars[i].toURI().toURL();
        }
        //Luodaan URL classloader
        ClassLoader urc = URLClassLoader.newInstance(urls, main);
        for (File jar : jars) {
            JarFile jarFile = new JarFile(jar);
            Enumeration<JarEntry> e = jarFile.entries();

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory()) {
                    continue;
                }
                if (je.getName().endsWith(".class")) {
                    //.class pois
                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');
                    if (!className.contains("$")) {
                        Class<?> clazz = urc.loadClass(className);
                        if (clazz.isInstance(BeloLibrary.class)) {
                            BeloLibrary newClass = (BeloLibrary) clazz.newInstance();

                            Import.libraries.add(newClass);
                            newClass.addToSymbolTable(globalSymbolTable);
                        } else {
                            addMarkedFieldsFromClass(clazz, globalSymbolTable);
                        }
                    }
                }
            }
        }
        haveBeenImported = true;
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
                    if (!arg.getClass().equals(parameterTypes[i])) {
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
                    return throwError(res, context, "Error calling function");
                } catch (InvocationTargetException e) {
                    return throwError(res, context, e.getCause().getMessage());
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
                String name = list.get(0).first().getName();
                Overloading overloading = new Overloading(list, name);

                res.add(overloading);
                list = new ArrayList<>();
                list.add(new Pair<>(function,method.getParameterTypes()));
            }
            prevName = method.getName();
        }
        if (!list.isEmpty()) {
            String name = list.get(0).first().getName();
            Overloading overloading = new Overloading(list, name);
            res.add(overloading);
        }
        return res;
    }

    private static void addStaticFunctions(ArrayList<Method> staticMethods, SymbolTable symbolTable) {
        List<Overloading> overloadings = collectOverloads(staticMethods, null);
        for (Overloading overloading : overloadings) {
            symbolTable.defineFunction(overloading.getName(), overloading);
        }
    }

    private static void addMarkedFieldsFromClass(Class<?> clazz, SymbolTable symbolTable) throws IllegalAccessException {
        if (clazz.getAnnotation(BeloScript.class) == null) return;
        collectFields(clazz, symbolTable);
        collectMethods(clazz, symbolTable);
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
                            if (!arg.getClass().equals(parameterTypes[i])) {
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
                List<Overloading> overloads = collectOverloads(classMethods, o);
                if (!(o instanceof CustomBeloClass)) {
                    return throwError(res,context,"Class should inherit CustomBeloClass class");
                }
                CustomBeloClass beloClass = (CustomBeloClass) o;

                for (Overloading overload : overloads) {
                    beloClass.classValues.put(new BeloString(overload.getName()),overload);
                }
                return res.success(beloClass);
            }
        };
    }

    private static Overloading createOverloadedConstructors(Class<?> clazz, List<Method> classMethods) {
        List<Pair<BeloScriptFunction, Class<?>[]>> overloadedConstructors = new ArrayList<>();

        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getAnnotation(BeloScript.class) == null) continue;
            //löytyi konstruktori, joka käy
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            int settingsIndex = Arrays.asList(parameterTypes).indexOf(Settings.class);
            BeloScriptFunction cons = createConstructor(clazz,constructor,settingsIndex,parameterTypes,classMethods);
            overloadedConstructors.add(new Pair<>(cons,parameterTypes));
        }
        return new Overloading(overloadedConstructors,clazz.getSimpleName());
    }

    private static void collectMethods(Class<?> clazz, SymbolTable symbolTable) {
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
        addStaticFunctions(staticMethods, symbolTable);

        if (classMethods.isEmpty()) return;

        Overloading overloading = createOverloadedConstructors(clazz,classMethods);
        symbolTable.defineFunction(clazz.getSimpleName(), overloading);
    }

    private static void collectFields(Class<?> clazz, SymbolTable symbolTable) throws IllegalAccessException {
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
            symbolTable.set(field.getName(), arvo);
        }
    }
}
