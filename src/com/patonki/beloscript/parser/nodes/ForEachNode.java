package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;

public class ForEachNode extends Node {
    private final String varName;
    private final Node list;
    private final Node body;
    private final boolean shouldReturnNull;

    public ForEachNode(VarAccessNode startValue, Node list, Node body, boolean shouldReturnNull, Position start, Position end) {
        this.varName = startValue.getVarName();
        this.start = start;
        this.end = end;
        this.list = list;
        this.body = body;
        this.shouldReturnNull = shouldReturnNull;
    }
    @SuppressWarnings("unchecked")
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();

        BeloClass classList = res.register(list.execute(context,interpreter));
        if (!(classList instanceof Iterable)) {
            return res.failure(new RunTimeError(
                    list.getStart(),list.getEnd(),
                    "Value not iterable",context
            ));
        }
        Iterable<BeloClass> itList = (Iterable<BeloClass>) classList;

        ArrayList<BeloClass> elements = new ArrayList<>();
        for (BeloClass value : itList) {
            context.getSymboltable().set(varName, value);
            BeloClass val = res.register(body.execute(context, interpreter));

            if (res.shouldReturn() && !res.isShouldContinue() && !res.isShouldBreak()) return res;
            if (res.isShouldContinue()) continue;
            if (res.isShouldBreak()) break;
            if (!this.shouldReturnNull) {
                elements.add(val);
            }
        }
        if (this.shouldReturnNull) {
            return res.success(Null.NULL);
        }
        return res.success(List.create(elements), getStart(),getEnd());
    }

    @Override
    public String toString() {
        return "{foreach var:"+varName+" body: "+body+" list: "+list+"}";
    }

    @Override
    public String convertToJavaCode() {
        String list = "(" + this.list.convertToJavaCode() + ")";
        String loopContent = this.body.convertToJavaCode();
        return String.format("for (BeloClass %s : (Iterable<BeloClass>)%s) {%s}", varName, list, loopContent);
    }
}
