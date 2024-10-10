package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class NullNode extends Node{
    private final RunTimeResult res = new RunTimeResult();

    public NullNode() {
        this.res.success(Null.NULL);
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        return res;
    }
}
