package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.oop.BeloEnumDefinition;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class EnumDefNode extends Node{
    private final String enumName;
    private final List<String> enums;

    public EnumDefNode(String enumName, List<Token> parameters,Position start, Position end) {
        this.enumName = enumName;
        this.start = start;
        this.end = end;
        this.enums = parameters.stream().map(Token::getValue).collect(Collectors.toList());
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloEnumDefinition enumDefinition = new BeloEnumDefinition(this.enums);
        context.getSymboltable().set(enumName, enumDefinition);
        enumDefinition.setContext(context);
        return res.success(enumDefinition,getStart(),getEnd());
    }
}
