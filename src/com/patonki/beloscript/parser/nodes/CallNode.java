package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;
import java.util.List;

public class CallNode extends Node {
    private final Node atom;
    private final List<Node> args;

    public CallNode(Node atom, List<Node> args, Position end) {
        this.atom = atom;
        this.args = args;
        this.start = atom.getStart();
        this.end = end;
        this.visitMethod = this::visit;
    }

    @Override
    public String toString() {
        return "{" + atom.toString() + " args: " + args.toString()+ "}";
    }

    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        List<BeloClass> args = new ArrayList<>();
        BeloClass funcToCall = res.register(atom.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        funcToCall.setPos(atom.getStart(),atom.getEnd()).setContext(context);

        for (Node argNode : this.args) {
            args.add(res.register(argNode.execute(context,interpreter)));
            if (res.shouldReturn()) return res;
        }
        BeloClass returnValue = res.register(funcToCall.execute(args));
        if (res.shouldReturn()) return res;

        returnValue.setContext(context).setPos(getStart(),getEnd());
        return res.success(returnValue);
    }
}
