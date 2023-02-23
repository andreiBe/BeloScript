package com.patonki.beloscript.interpreter;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BaseFunction;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, BeloClass> symbols = new HashMap<>();
    private SymbolTable parent;
    public SymbolTable() {}
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }
    public BeloClass get(String name) {
        BeloClass val = symbols.get(name);
        if (val == null && parent != null) {
            return parent.get(name);
        }
        return val;
    }
    public void create(String clazz) {
        symbols.get(clazz).execute(new ArrayList<>());
    }
    public void set(String name, BeloClass value) {
        symbols.put(name, value);
    }
    public void change(String name, BeloClass value) {
        if (symbols.get(name) == null && parent != null) parent.change(name,value);
        else symbols.put(name,value);
    }

    public void defineFunction(String name, BeloScriptFunction func) {
        set(name, func);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("\nPrinting symboltable");
        for (String key : symbols.keySet()) {
            BeloClass value = symbols.get(key);
            if (value instanceof BaseFunction) continue;
            res.append("\n").append(key).append("=").append(value);
        }
        if (parent != null) {
            res.append("\nPrinting parent");
            res.append(parent).append("\n");
        }
        return res.toString();
    }
    public void keys() {
        System.out.println("Printing keys:");
        for (String key : symbols.keySet()) {
            System.out.println(key);
        }
    }
}
