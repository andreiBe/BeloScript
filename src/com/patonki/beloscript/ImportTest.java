package com.patonki.beloscript;

import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.errors.BeloException;
import org.junit.jupiter.api.Test;

class ImportTest {
    @Test
    void importCustomClass() throws IllegalAccessException, BeloException {
        Import.addMarkedFieldsFromClass(List.class);

        BeloScript.run("list = BeloList2();list + 43;print(list)","test","src/com/test");
    }
}