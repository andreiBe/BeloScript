package com.patonki.beloscript.datatypes.oop;

public enum AccessModifier {
    PUBLIC,PRIVATE,PROTECTED;

    public boolean canAccess(AccessModifier target) {
        if (this == PUBLIC) return target == PUBLIC;
        if (this == PROTECTED) return target == PUBLIC || target == PROTECTED;
        return true;
    }
}
