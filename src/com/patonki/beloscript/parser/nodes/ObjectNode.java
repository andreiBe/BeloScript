package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloObject;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

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
        BeloObject object = new BeloObject();
        for (PairNode pair : this.pairs) {
            BeloClass value = res.register(pair.getValue().execute(context,interpreter));
            if (res.shouldReturn()) return res;
            String key = pair.getKey();
            object.put(key,value);
        }
        return res.success(object);
    }

    @Override
    public String toString() {
        return "{object:"+pairs+"}";
    }
}
