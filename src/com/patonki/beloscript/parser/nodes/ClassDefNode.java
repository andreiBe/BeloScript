package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.oop.BeloClassDefinition;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDefNode extends Node {
    private final BeloClassDefinition definition;
    private final String className;
    public ClassDefNode(String className,
                        List<Token> parameters,
                        LinkedHashMap<Node, Node> properties,
                        Position start, Position end) {
        this.start = start;
        this.end = end;
        this.className = className;
        List<String> parametersAsString = parameters.stream().map(Token::getValue).collect(Collectors.toList());
        this.definition = new BeloClassDefinition(parametersAsString, properties, className);
    }
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        context.getSymboltable().set(className, definition);
        return res.success(definition,getStart(),getEnd(), context);
    }

    @Override
    public String toString() {
        return "{classdef}";
    }

}
