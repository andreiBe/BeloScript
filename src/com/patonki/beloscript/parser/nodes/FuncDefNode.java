package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.datatypes.function.BeloFunction;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class FuncDefNode extends Node {
    private final Token varName;
    private final Node body;
    private final BeloFunction function;

    public FuncDefNode(Token varName, List<Token> argumentNames, Node body, boolean b) {
        this.varName = varName;
        this.body = body;
        List<String> argNames = argumentNames.stream().map(Token::getValue).collect(Collectors.toList());
        String funcName = varName != null ? varName.getValue() : null;
        function = new BeloFunction(funcName, body,argNames, b);
        this.visitMethod = this::visit;
    }
    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        function.setContext(context);

        if (varName != null) {
            context.getSymboltable().set(varName.getValue(), function);
        }
        return res.success(function);
    }

    @Override
    public String toString() {
        return "{funcdef body:"+body+"}";
    }
}
