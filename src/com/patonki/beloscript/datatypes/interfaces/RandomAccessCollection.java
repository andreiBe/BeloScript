package com.patonki.beloscript.datatypes.interfaces;

import com.patonki.beloscript.datatypes.BeloClass;

public interface RandomAccessCollection {
    BeloClass get(int index);
    void set(int index, BeloClass value);
    int size();
}
