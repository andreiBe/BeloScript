package com.patonki.beloscript.interpreter;


import com.patonki.beloscript.Position;

public class Context {
    private final String displayName;
    private final Context parent;
    private final Position parentEntyPosition;
    private SymbolTable symboltable;
    private Settings settings;

    public Context(String displayName, Context parent, Position parentEntyPosition) {
        this.displayName = displayName;
        this.parent = parent;
        if (this.parent != null) {
            this.setSettings(this.parent.getSettings());
        }
        this.parentEntyPosition = parentEntyPosition;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public Context(String displayName) {
        this(displayName,null,null);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Context getParent() {
        return parent;
    }

    public Position getParentEntyPosition() {
        return parentEntyPosition;
    }

    public SymbolTable getSymboltable() {
        return symboltable;
    }

    public void setSymboltable(SymbolTable symboltable) {
        this.symboltable = symboltable;
    }
}
