package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
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
    private final String funcName;
    private final List<String> argNames;
    private final boolean shouldAutoReturn;

    public FuncDefNode(Token varName, List<Token> argumentNames, Node body, boolean shouldAutoReturn, Position start, Position end) {
        this.varName = varName;
        this.body = body;
        this.start = start;
        this.end = end;
        this.argNames = argumentNames.stream().map(Token::getValue).collect(Collectors.toList());
        this.funcName = varName != null ? varName.getValue() : null;
        this.shouldAutoReturn = shouldAutoReturn;
    }
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloFunction function = new BeloFunction(funcName, body,argNames, shouldAutoReturn);
        if (varName != null) {
            context.getSymboltable().set(varName.getValue(), function);
        }
        function.setContext(context);
        return res.success(function,getStart(),getEnd(), context);
    }

    @Override
    public String toString() {
        return "{funcdef body:"+body+"}";
    }
}
