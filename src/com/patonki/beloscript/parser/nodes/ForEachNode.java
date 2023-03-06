package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;
import java.util.Iterator;

public class ForEachNode extends Node {
    private final String varName;
    private final Node list;
    private final Node body;
    private final boolean b;

    public ForEachNode(VarAccessNode startValue, Node list, Node body, boolean b, Position start, Position end) {
        this.varName = startValue.getVarName();
        this.start = start;
        this.end = end;
        this.list = list;
        this.body = body;
        this.b = b;
        this.visitMethod = this::visit;
    }
    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        ArrayList<BeloClass> elements = new ArrayList<>();

        BeloClass classList = res.register(list.execute(context,interpreter));
        if (!(classList instanceof Iterable)) {
            return res.failure(new RunTimeError(
                    list.getStart(),list.getEnd(),
                    "Value not iterable",context
            ));
        }
        Iterable<BeloClass> itList;
        try {
            itList = (Iterable<BeloClass>) classList;
        } catch (ClassCastException e) {
            return res.failure(new RunTimeError(
                    list.getStart(),list.getEnd(),
                    "Value not iterable (isn't iterating BeloClass)",context
            ));
        }
        for (BeloClass value : itList) {
            context.getSymboltable().set(varName, value);
            BeloClass val = res.register(body.execute(context, interpreter));

            if (res.shouldReturn() && !res.isShouldContinue() && !res.isShouldBreak()) return res;
            if (res.isShouldContinue()) continue;
            if (res.isShouldBreak()) break;
            elements.add(val);
        }
        return res.success(List.create(elements), getStart(),getEnd(),context);
    }

    @Override
    public String toString() {
        return "{foreach var:"+varName+" body: "+body+" list: "+list+"}";
    }
}
