package com.patonki.beloscript;

import com.patonki.beloscript.builtInLibraries.*;
import com.patonki.beloscript.datatypes.BeloClass;
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

import static com.patonki.beloscript.ClassImporter.addAllFieldsFromClass;
import static com.patonki.beloscript.ClassImporter.addMarkedFieldsFromClass;

public class Import {
    private Import() {

    }
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
        addMarkedFieldsFromClass(BeloRandom.class, imported);
        addMarkedFieldsFromClass(com.patonki.beloscript.datatypes.basicTypes.List.class, imported);
        addMarkedFieldsFromClass(Set.class, imported);
        addMarkedFieldsFromClass(RangeCommand.class, imported);
        addMarkedFieldsFromClass(IO.class, imported);
        addMarkedFieldsFromClass(Util.class, imported);

        addAllFieldsFromClass(StringBuilder.class, imported);
        addAllFieldsFromClass(Thread.class, imported);
        addAllFieldsFromClass(URL.class, imported);
        addAllFieldsFromClass(File.class, imported);
        addAllFieldsFromClass(Character.class, imported);

        LibJson json = new LibJson();
        Import.libraries.add(json);
    }

    private static void importJarFile(File jar, ClassLoader urc) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException, BeloException {
        try (JarFile jarFile = new JarFile(jar)) {
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
                        if (BeloLibrary.class.isAssignableFrom(clazz)) {
                            BeloLibrary newClass = (BeloLibrary) clazz.newInstance();
                            Import.libraries.add(newClass);
                        } else {
                            addMarkedFieldsFromClass(clazz,imported);
                        }
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


}
