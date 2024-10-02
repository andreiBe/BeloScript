package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.oop.AccessModifier;
import com.patonki.beloscript.datatypes.oop.BeloClassDefinition;
import com.patonki.beloscript.datatypes.oop.Properties;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class ClassDefNode extends Node {
    private final String className;
    private final List<String> parametersAsString;
    private final List<ClassProperty> properties;
    private final List<ClassProperty> staticProperties;
    private final Node parent;
    public static class ClassProperty {
        private final boolean isStatic;
        private final AccessModifier accessModifier;
        private final String key;
        private final Node value;

        public ClassProperty(boolean isStatic, AccessModifier accessModifier, String key, Node value) {
            this.isStatic = isStatic;
            this.accessModifier = accessModifier;
            this.key = key;
            this.value = value;
        }
    }
    public ClassDefNode(String className,
                        List<Token> parameters,
                        List<ClassProperty> properties,
                        Node parent,
                        Position start, Position end) {
        this.start = start;
        this.end = end;
        this.parent = parent;
        this.className = className;
        this.parametersAsString = parameters.stream().map(Token::getValue).collect(Collectors.toList());
        this.properties = properties.stream().filter(p -> !p.isStatic).collect(Collectors.toList());
        this.staticProperties = properties.stream().filter(p -> p.isStatic).collect(Collectors.toList());
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        Properties properties = new Properties();
        BeloClassDefinition parentClass = null;
        if (parent != null) {
            BeloClass parentObj = res.register(parent.execute(context, interpreter));
            if (res.shouldReturn()) return res;
            if (!(parentObj instanceof BeloClassDefinition)) {
                return res.failure(new RunTimeError(parent.getStart(), parent.getEnd(),
                        "Expected class but was " + parentObj.getClass().getSimpleName(),
                        context
                ));
            }
            parentClass = (BeloClassDefinition) parentObj;
        }
        for (ClassProperty staticClassProperty : this.staticProperties) {
            String key = staticClassProperty.key;

            BeloClass value = res.register(staticClassProperty.value.execute(context,interpreter));
            if (res.shouldReturn()) return res;

            properties.addStaticProperty(key,
                    new Properties.Property<>(value, staticClassProperty.accessModifier)
            );
        }
        for (ClassProperty property : this.properties) {
            properties.addProperty(property.key,
                    new Properties.Property<>(property.value, property.accessModifier)
            );
        }

        BeloClassDefinition definition = new BeloClassDefinition(parametersAsString, properties, className, parentClass);

        context.getSymboltable().set(className, definition);
        definition.setContext(context);
        return res.success(definition,getStart(),getEnd(), context);
    }

    @Override
    public String toString() {
        return "{classdef}";
    }

}
