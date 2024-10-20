package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class DotNode extends Node{
    private final StringNode right;
    private final Node left;
    public DotNode(Node left, Token name) {
        this.right = new StringNode(name);
        this.left = left;
        this.start = left.getStart();
        this.end = right.getEnd();
    }
    protected String getMemberString() {
        return right.getToken().getValue();
    }

    protected RunTimeResult executeLeft(Context context, Interpreter interpreter) {
        return left.execute(context,interpreter);
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass clazz = res.register(left.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        BeloClass string = res.register(right.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        BeloClass result = clazz.classValue(string);
        if (result.hasError()) {
            return res.failure(result.getError());
        }
        return res.success(result, getStart(), getEnd());
    }

    @Override
    public String toString() {
        return "{"+left+"."+right+"}";
    }

    @Override
    public String convertToJavaCode() {
        String left =  "(" + this.left.convertToJavaCode() + ")";
        String right = this.right.getToken().getValue();
        return String.format("%s.%s", left, right);
    }
}
