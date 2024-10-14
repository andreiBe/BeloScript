package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.parser.nodes.Node;

import java.util.LinkedHashMap;
import java.util.Set;

public class Properties implements PropertiesAccess {
    private final LinkedHashMap<String, Property<Node>> properties = new LinkedHashMap<>();
    private final LinkedHashMap<String, Property<BeloClass>> staticProperties = new LinkedHashMap<>();

    public void addStaticProperty(String key, Property<BeloClass> property) {
        this.staticProperties.put(key, property);
    }
    public void addProperty(String key, Property<Node> property) {
        this.properties.put(key, property);
    }
    public boolean containsProperty(String key) {
        return this.properties.containsKey(key);
    }
    @Override
    public Node getPropertyValue(String key) {
        Property<Node> property = this.properties.get(key);
        if (property == null) return null;
        return property.value;
    }
    @Override
    public BeloClass getStaticProperty(String key) {
        BeloClass val = this.staticProperties.get(key).value;
        return val == null ? Null.NULL : val;
    }

    @Override
    public BeloClass setStaticProperty(String key, BeloClass value) {
        this.staticProperties.get(key).value = value;
        return value;
    }

    @Override
    public boolean hasStaticProperty(String key) {
        return staticProperties.containsKey(key);
    }

    @Override
    public AccessModifier getAccessModifierOfStaticProperty(String key) {
        return staticProperties.get(key).accessModifier;
    }

    @Override
    public boolean staticPropertyIsFinal(String key) {
        return staticProperties.get(key).isFinal;
    }

    public RuntimeProperties getRuntimeProperties(RunTimeResult res,
                                                  Interpreter interpreter,
                                                  Context context,
                                                  String className) {
        LinkedHashMap<String, RuntimeProperties.RuntimeProperty> runtimeProperties = new LinkedHashMap<>();
        for (String key : properties.keySet()) {
            Node propertyValue = properties.get(key).value;
            if (key.equals(className)) {
                continue;
            }

            BeloClass value = propertyValue == null
                    ? new Null()
                    : res.register(interpreter.execute(propertyValue, context));
            if (res.shouldReturn()) return null;

            runtimeProperties.put(key,
                    new RuntimeProperties.RuntimeProperty(value, properties.get(key).accessModifier,
                            properties.get(key).isFinal)
            );
        }
        return new RuntimeProperties(runtimeProperties);
    }
    public static class Property<T> {
        private T value;
        private final AccessModifier accessModifier;
        private final boolean isFinal;

        public Property(T value, AccessModifier accessModifier, boolean isFinal) {
            this.value = value;
            this.accessModifier = accessModifier;
            this.isFinal = isFinal;
        }
    }

}
