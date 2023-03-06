package com.patonki.beloscript;

import com.patonki.beloscript.builtInLibraries.*;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.datatypes.structures.Set;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.interpreter.SymbolTable;
import com.patonki.datatypes.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Import {
    private static final ArrayList<BeloLibrary> libraries = new ArrayList<>();
    private static final ArrayList<Pair<String,BeloClass>> imported = new ArrayList<>();

    public static void importEverything(SymbolTable globalSymbolTable, String root) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, BeloException {
        if (libraries.isEmpty())
            importBuiltInLibraries();

        importEveryJarFile(root);

        for (Pair<String, BeloClass> pair : imported) {
            globalSymbolTable.set(pair.first(),pair.second());
        }
        for (BeloLibrary library : libraries) {
            library.addToSymbolTable(globalSymbolTable);
        }
    }

    private static void importBuiltInLibraries() throws IllegalAccessException, BeloException {
        addMarkedFieldsFromClass(BeloRandom.class);
        addMarkedFieldsFromClass(com.patonki.beloscript.datatypes.basicTypes.List.class);
        addMarkedFieldsFromClass(Set.class);
        addMarkedFieldsFromClass(RangeCommand.class);
        addMarkedFieldsFromClass(IO.class);
        addMarkedFieldsFromClass(Util.class);

        LibJson json = new LibJson();
        Import.libraries.add(json);
    }

    private static void importJarFile(File jar, ClassLoader urc) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException, BeloException {
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
                        if (!Import.libraries.contains(newClass)) {
                            Import.libraries.add(newClass);
                        }
                    } else {
                        addMarkedFieldsFromClass(clazz);
                    }
                }
            }
        }
    }
    public static void importJarFiles(File[] jars) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, BeloException {
        //Ohjelman oma class loader
        ClassLoader main = Import.class.getClassLoader();
        URL[] urls = new URL[jars.length];
        for (int i = 0; i < jars.length; i++) {
            urls[i] = jars[i].toURI().toURL();
        }
        //Luodaan URL classloader
        ClassLoader urc = URLClassLoader.newInstance(urls, main);
        for (File jar : jars) {
            importJarFile(jar,urc);
        }
    }
    private static void importEveryJarFile(String root) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, BeloException {
        File libraries = new File(root + "lib");
        if (!libraries.exists()) return;
        File[] jars = libraries.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null) throw new NullPointerException("Can't find files");

        importJarFiles(jars);
    }

    public static void addMarkedFieldsFromClass(Class<?> clazz) throws IllegalAccessException, BeloException {
        if (clazz.getAnnotation(BeloScript.class) == null) return;
        if (!CustomBeloClass.class.isAssignableFrom(clazz)) {
            throw new BeloException("Class should inherit CustomBeloClass class");
        }
        CustomBeloClass.addMethodsAndFields(clazz, imported);
    }
}
