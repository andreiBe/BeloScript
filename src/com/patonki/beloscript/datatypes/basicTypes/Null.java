package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;

public class Null extends BeloClass {

    @Override
    public boolean isTrue() {
        return false;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public int compare(BeloClass another) {
        return another instanceof Null ? 0 : -1;
    }
}
