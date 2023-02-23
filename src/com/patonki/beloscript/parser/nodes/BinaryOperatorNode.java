package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Calculation;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;
import com.patonki.beloscript.lexer.TokenType;

import static com.patonki.beloscript.lexer.TokenType.KEYWORD;

public class BinaryOperatorNode extends Node{
    private final Node left;
    private final Node right;
    private final Token token;
    private final Calculation binOp;

    public BinaryOperatorNode(Node left, Token token, Node right) {
        this.left = left;
        this.right = right;
        this.token = token;
        this.start = left.getStart();
        this.end = right.getEnd();
        this.binOp = TokenType.getMatchingCalculation(token.getType());
        if (binOp != null) {
            visitMethod = this::visit;
        }
        else if (token.matches(KEYWORD,"and")) {
            visitMethod = this::and;
        }
        else if (token.matches(KEYWORD, "or")) {
            visitMethod = this::or;
        }
        else {
            throw new IllegalArgumentException("Not an operator "+token.getType());
        }
    }
    private RunTimeResult or(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass left = res.register(this.left.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        if (left.isTrue()) return res.success(new BeloDouble(1));

        BeloClass right = res.register(this.right.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        return res.success(new BeloDouble(right.isTrue() ? 1 : 0));
    }
    private RunTimeResult and(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass left = res.register(this.left.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        if (!left.isTrue()) return res.success(new BeloDouble(0));

        BeloClass right = res.register(this.right.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        return res.success(new BeloDouble(right.isTrue() ? 1 : 0));
    }

    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass left = res.register(this.left.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        BeloClass right = res.register(this.right.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        BeloClass result = binOp.calculate(left,right);
        if (result.hasError()) {
            return res.failure(result.getError().setContext(context));
        } else {
            result.setPos(getStart(),getEnd()).setContext(context);
            return res.success(result);
        }
    }

    @Override
    public String toString() {
        return "{"+left.toString()+"," + token.toString()+"," + right.toString()+"}";
    }

}
