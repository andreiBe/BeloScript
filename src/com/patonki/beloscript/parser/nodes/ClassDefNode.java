package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.oop.BeloClassDefinition;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDefNode extends Node {
    private final String className;
    private final List<String> parametersAsString;
    private final LinkedHashMap<Node, Node> properties;
    private final LinkedHashMap<Node, Node> staticProperties;

    public ClassDefNode(String className,
                        List<Token> parameters,
                        LinkedHashMap<Node, Node> properties,
                        LinkedHashMap<Node, Node> staticProperties,
                        Position start, Position end) {
        this.start = start;
        this.end = end;
        this.className = className;
        this.parametersAsString = parameters.stream().map(Token::getValue).collect(Collectors.toList());
        this.properties = properties;
        this.staticProperties = staticProperties;
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        HashMap<BeloClass, BeloClass> staticParams = new HashMap<>();
        for (Node keyNode : staticProperties.keySet()) {
            BeloClass key = res.register(keyNode.execute(context, interpreter));
            if (res.shouldReturn()) return res;
            BeloClass value = res.register(staticProperties.get(keyNode).execute(context,interpreter));
            if (res.shouldReturn()) return res;
            staticParams.put(key, value);
        }

        BeloClassDefinition definition = new BeloClassDefinition(parametersAsString, properties, staticParams, className);

        context.getSymboltable().set(className, definition);
        definition.setContext(context);
        return res.success(definition,getStart(),getEnd(), context);
    }

    @Override
    public String toString() {
        return "{classdef}";
    }

}
