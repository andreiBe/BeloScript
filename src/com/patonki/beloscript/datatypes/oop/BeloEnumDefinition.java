package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.HashMap;
import java.util.List;

public class BeloEnumDefinition extends BeloClass {
    private final HashMap<BeloClass, BeloClass> map = new HashMap<>();

    public BeloEnumDefinition(List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            map.put(BeloString.create(values.get(i)), new BeloDouble(i));
        }
    }

    //TÄTÄ EI TODELLAKAAN OLE TARKOITUS YLIKIRJOITTAA, MUTTA ENUMIT OVAT POIKKEUS
    @Override
    public BeloClass classValue(BeloClass name) {
        if (name instanceof BeloString && name.toString().equals("spread")) {
            return new BeloScriptFunction(null) {
                @Override
                public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
                    for (BeloClass beloClass : map.keySet()) {
                        context.getSymboltable().set(beloClass.toString(), map.get(beloClass));
                    }
                    return res.success(new Null());
                }
            };
        }
        BeloClass val = map.get(name);
        return val == null ? new Null() : val;
    }
}
