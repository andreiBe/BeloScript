package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallNode extends Node {
    private final Node atom;
    private final List<Node> args;

    public CallNode(Node atom, List<Node> args, Position end) {
        this.atom = atom;
        this.args = args;
        this.start = atom.getStart();
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" + atom.toString() + " args: " + args.toString()+ "}";
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass funcToCall = res.register(atom.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        List<BeloClass> args = new ArrayList<>();
        //todo maybe edit
        //Position last = this.args.isEmpty() ? atom.getEnd() : this.args.get(this.args.size()-1).getEnd();
        funcToCall.setPos(atom.getStart(),atom.getEnd());//.setContext(context);

        //if (funcToCall.getContext() == null) funcToCall.setContext(context);

        for (Node argNode : this.args) {
            args.add(res.register(argNode.execute(context,interpreter)));
            if (res.shouldReturn()) return res;
        }
        if (funcToCall instanceof BeloScriptFunction) {
            funcToCall.setContext(context);
        }
        BeloClass returnValue = res.register(funcToCall.execute(args));
        if (res.shouldReturn()) return res;

        return res.success(returnValue, getStart(), getEnd(), context);
    }

    @Override
    public String convertToJavaCode() {
        String funcToCall = "("+this.atom.convertToJavaCode()+")";
        String parameters =
                this.args.stream().map(Node::convertToJavaCode).collect(Collectors.joining(","));
        return String.format("%s.execute(%s)", funcToCall, parameters);
    }
}
