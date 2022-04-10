package com.patonki.beloscript;

import com.patonki.beloscript.builtInLibraries.LibJson;
import com.patonki.beloscript.interpreter.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Import {
    private static boolean haveBeenImported = false;
    private static final ArrayList<BeloLibrary> libraries = new ArrayList<>();

    public static void closeEverything() {
        for (BeloLibrary library : Import.libraries) {
            library.close();
        }
        libraries.clear();
        haveBeenImported = false;
    }
    public static void importEverything(SymbolTable globalSymbolTable, String root) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        importBuiltInLibraries(globalSymbolTable);
        importEveryJarFile(globalSymbolTable,root);
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
        File libraries = new File(root+"lib");
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
        ClassLoader urc = URLClassLoader.newInstance(urls,main);
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
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                    //Viimeisen pisteen jälkeen
                    String simpleName = className.substring(className.lastIndexOf(".")+1);
                    if (!className.contains("$") && simpleName.startsWith("Lib")) {
                        Class<?> clazz = urc.loadClass(className);
                        BeloLibrary newClass = (BeloLibrary) clazz.newInstance();

                        Import.libraries.add(newClass);
                        newClass.addToSymbolTable(globalSymbolTable);
                    }
                }
            }
        }
        haveBeenImported = true;
    }
}
