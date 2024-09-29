package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

public class PostOperatorNode extends Node {
    private interface Post {
        BeloClass calculate(BeloClass value, Context context);
    }
    private final Post command;
    private final VarAccessNode value;

    public PostOperatorNode(VarAccessNode value, Token post) {
        this.value = value;
        this.start = value.getStart();
        this.end = post.getEnd();
        String varName = this.value.getVarName();
        switch (post.getType()) {
            case PLUSPLUS:
                command = (val,c) -> {
                    BeloClass copy = val.copy();
                    c.getSymboltable().change(varName,val.postPlus());
                    return copy;
                };
                break;
            case MINUSMINUS:
                command = (val,c) -> {
                    BeloClass copy = val.copy();
                    c.getSymboltable().change(varName,val.postMinus());
                    return copy;
                };
                break;
            default:
                throw new IllegalArgumentException("Not a post operator: "+post);
        }
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass val = res.register(value.execute(context, interpreter));
        if (res.shouldReturn()) return res;

        BeloClass val2 = command.calculate(val,context);
        if (val2.hasError()) {
            return res.failure(val2.getError());
        }
        return res.success(val2, getStart(), getEnd(), context);
    }

    @Override
    public String toString() {
        return "{post: "+this.value+"}";
    }
}
