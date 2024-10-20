package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;

public class ListNode extends Node {
    protected final java.util.List<Node> statements;
    public ListNode(java.util.List<Node> statements, Position start, Position end) {
        this.statements = statements;
        this.start = start;
        this.end = end;
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        ArrayList<BeloClass> list = new ArrayList<>();

        for (Node node : statements) {
            list.add(res.register(node.execute(context,interpreter)));
            if (res.shouldReturn()) return res;
        }
        return res.success(
                List.create(list),getStart(),getEnd(),context
        );
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node statement : statements) {
            sb.append(statement.toString()).append("\n");
        }
        return sb.toString();
    }
}
