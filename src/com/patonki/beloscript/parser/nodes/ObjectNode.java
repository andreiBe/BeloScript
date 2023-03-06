package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.Obj;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ObjectNode extends Node {
    private final List<PairNode> pairs = new ArrayList<>();

    public ObjectNode(List<Node> pairs, Position start, Position end) {
        for (Node pair : pairs) {
            this.pairs.add((PairNode) pair);
        }
        this.start = start;
        this.end = end;
        this.visitMethod = this::visit;
    }

    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        Obj object = Obj.create();
        Context newContext = new Context("Object", context, this.getStart());
        newContext.setSymboltable(new SymbolTable(newContext.getParent().getSymboltable()));
        newContext.getSymboltable().set("self", object);

        for (PairNode pair : this.pairs) {
            BeloClass key = res.register(pair.getKey().execute(newContext,interpreter));
            if (res.shouldReturn()) return res;
            BeloClass value = res.register(pair.getValue().execute(newContext,interpreter));
            if (res.shouldReturn()) return res;

            object.put(key,value);
        }
        return res.success(object, getStart(), getEnd());
    }

    @Override
    public String toString() {
        return "{object:"+pairs+"}";
    }
}
