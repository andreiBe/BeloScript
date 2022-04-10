package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;

public class BeloNull extends BeloClass {

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
        return another instanceof BeloNull ? 0 : -1;
    }
}
