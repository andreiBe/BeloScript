package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;
import com.patonki.beloscript.lexer.TokenType;

public class KeyWordOperatorNode extends Node {
    private final Node left;
    private final Node right;
    private final Operator operator;

    private interface Operator {
        RunTimeResult calculate(BeloClass left, BeloClass right, RunTimeResult res);
    }

    public KeyWordOperatorNode(Node left, Token token, Node right) {
        this.left = left;
        this.right = right;
        this.start = left.start;
        this.end = right.end;
        if (token.matches(TokenType.KEYWORD, "instanceof")) {
            this.operator = (leftC, rightC, res) -> {
                boolean isInstanceOf = rightC.parameterIsInstanceOfThis(leftC);
                return res.success(BeloDouble.createFromBoolean(isInstanceOf));
            };
        } else {
            throw new IllegalArgumentException("Illegal token: " + token);
        }
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass left = res.register(this.left.execute(context, interpreter));
        if (res.shouldReturn()) return res;

        BeloClass right = res.register(this.right.execute(context, interpreter));
        if (res.shouldReturn()) return res;

        return this.operator.calculate(left, right, res);
    }
}
