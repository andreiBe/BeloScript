package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;
import com.patonki.beloscript.lexer.TokenType;

import static com.patonki.beloscript.lexer.TokenType.*;

public class UnaryOperationNode extends Node{
    private final Token operatorToken;
    private final Node node;
    private final Unary command;

    private interface Unary {
        BeloClass calculate(BeloClass number, Context context);
    }
    public UnaryOperationNode(Token operatorToken, VarAccessNode node) {
        this.operatorToken = operatorToken;
        this.node = node;
        if (operatorToken.getType() == PLUSPLUS) {
            String varName = node.getVarName();
            command = (number,c) -> {
                BeloClass val = number.prePlus();
                //TODO changed
                c.getSymboltable().change(varName, val);
                return val;
            };
        }
        else if (operatorToken.getType() == MINUSMINUS){
            String varName = node.getVarName();
            command = (number,c) -> {
                BeloClass val = number.preMinus();
                //TODO changed
                c.getSymboltable().change(varName, val);
                return val;
            };
        } else {
            throw new IllegalArgumentException("Not a valid token: "+operatorToken);
        }
        this.visitMethod = this::visit;
    }
    public UnaryOperationNode(Token operatorToken, Node node) {
        this.operatorToken = operatorToken;
        this.node = node;

        this.start = operatorToken.getStart();
        this.end = node.getEnd();
        this.visitMethod = this::visit;
        TokenType type = operatorToken.getType();
        if (type == MINUS) {
            command = (number,c) -> number.multiply(new BeloDouble(-1));
        }
        else if (operatorToken.matches(KEYWORD, "not")) {
            command = (number, c) -> number.not();
        }
        else if (type == PLUS) {
            command = (number,c) -> number;
        }
        else {
            throw new IllegalArgumentException("Not a valid token: "+operatorToken);
        }
    }
    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();

        BeloClass number = res.register(getNode().visitMethod.visit(context,interpreter));
        if (res.shouldReturn()) return res;
        number = command.calculate(number,context);

        if (number.hasError()) {
            return res.failure(number.getError());
        } else {
            return res.success(number.setPos(node.getStart(),node.getEnd()));
        }
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "{"+operatorToken+", "+node+"}";
    }
}