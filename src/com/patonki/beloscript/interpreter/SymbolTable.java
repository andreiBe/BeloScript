package com.patonki.beloscript.interpreter;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;

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
    public void set(String name, BeloClass value) {
        symbols.put(name, value);
    }

    public void defineFunction(String name, BeloScriptFunction func) {
        set(name, func);
    }
}
