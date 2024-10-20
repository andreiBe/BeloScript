package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;

@BeloScript
public class Util extends CustomBeloClass {
    private Util() {

    }
    @BeloScript
    public static double to_num(String b) {
        return Double.parseDouble(b);
    }
    @BeloScript
    public static String to_str(BeloClass b) {
        return b.toString();
    }
}
