package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloList;
import com.patonki.beloscript.datatypes.basicTypes.BeloNull;
import com.patonki.beloscript.datatypes.interfaces.IterableBeloClass;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;
import java.util.List;

public class ForEachNode extends Node {
    private final String varName;
    private final Node list;
    private final Node body;
    private final boolean b;

    public ForEachNode(VarAccessNode startValue, Node list, Node body, boolean b) {
        this.varName = startValue.getVarName();
        //TODO not defining start and end
        this.list = list;
        this.body = body;
        this.b = b;
        this.visitMethod = this::visit;
    }
    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        ArrayList<BeloClass> elements = new ArrayList<>();

        BeloClass classList = res.register(list.execute(context,interpreter));
        if (!(classList instanceof IterableBeloClass)) {
            return res.failure(new RunTimeError(
                    list.getStart(),list.getEnd(),
                    "Value not iterable",context
            ));
        }
        IterableBeloClass itList = (IterableBeloClass)classList;
        List<BeloClass> iterableList = itList.iterableList();

        for (BeloClass value : iterableList) {
            context.getSymboltable().set(varName, value);
            BeloClass val = res.register(body.execute(context,interpreter));

            if (res.shouldReturn() && !res.isShouldContinue() && !res.isShouldBreak()) return res;
            if (res.isShouldContinue()) continue;
            if (res.isShouldBreak()) break;
            elements.add(val);
        }
        return res.success(b ? new BeloNull() :
                new BeloList(elements).setContext(context));
    }

    @Override
    public String toString() {
        return "{foreach var:"+varName+" body: "+body+" list: "+list+"}";
    }
}
