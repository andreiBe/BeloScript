package com.patonki.beloscript.interpreter;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;

import java.util.HashMap;
import java.util.HashSet;

public class SymbolTable {
    private final HashMap<String, BeloClass> symbols = new HashMap<>();
    private final HashSet<String> finalVariables = new HashSet<>();
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
    public void set(String name, BeloClass value) {
        symbols.put(name, value);
    }
    public void makeFinal(String name) {
        finalVariables.add(name);
    }
    public boolean isFinal(String name) {
        if (this.parent != null) {
            return this.finalVariables.contains(name) || this.parent.isFinal(name);
        }
        return this.finalVariables.contains(name);
    }
    public void change(String name, BeloClass value) {
        if (symbols.get(name) == null && parent != null)
            parent.change(name,value);
        else symbols.put(name,value);
    }

    public void defineFunction(String name, BeloScriptFunction func) {
        set(name, func);
    }
}
