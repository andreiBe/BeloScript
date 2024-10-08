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
    private static final BeloDouble ONE = new BeloDouble(1);
    private static final BeloDouble ZERO = new BeloDouble(0);

    public BinaryOperatorNode(Node left, Token token, Node right) {
        this.left = left;
        this.right = right;
        this.token = token;
        this.start = left.getStart();
        this.end = right.getEnd();
        this.binOp = TokenType.getMatchingCalculation(token.getType());
        if (binOp != null) {
            this.setVisitMethod(this::visitBinOp);
        }
        else if (token.matches(KEYWORD,"and")) {
            this.setVisitMethod(this::and);
        }
        else if (token.matches(KEYWORD, "or")) {
            this.setVisitMethod(this::or);
        }
        else {
            //ei pitäisi ikinä tapahtua, koska parserin pitäisi tietää mitkä tokenit käy
            throw new IllegalArgumentException("Not an operator "+token.getType());
        }
    }
    private RunTimeResult or(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass left = res.register(this.left.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        if (left.isTrue()) return res.success(ONE, getStart(), getEnd(),context);

        BeloClass right = res.register(this.right.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        return res.success(right.isTrue() ? ONE : ZERO, getStart(),getEnd(),context);
    }
    private RunTimeResult and(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass left = res.register(this.left.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        if (!left.isTrue()) return res.success(ZERO, getStart(), getEnd(), context);

        BeloClass right = res.register(this.right.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        return res.success(right.isTrue() ? ONE : ZERO, getStart(),getEnd(),context);
    }

    private RunTimeResult visitBinOp(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass left = res.register(this.left.execute(context,interpreter));
        if (res.shouldReturn()) return res;
        BeloClass right = res.register(this.right.execute(context,interpreter));
        if (res.shouldReturn()) return res;

        BeloClass result = binOp.calculate(left,right);
        if (result.hasError()) {
            return res.failure(result.getError().setContext(context));
        } else {
            return res.success(result, getStart(), getEnd(), context);
        }
    }

    @Override
    public String toString() {
        return "{"+left.toString()+"," + token.toString()+"," + right.toString()+"}";
    }

}
