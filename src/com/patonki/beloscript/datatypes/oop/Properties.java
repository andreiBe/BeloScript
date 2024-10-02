package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.Null;
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
    @Override
    public Set<String> getPropertyKeys() {
        return this.properties.keySet();
    }
    @Override
    public Node getPropertyValue(String key) {
        return this.properties.get(key).value;
    }
    @Override
    public BeloClass getStaticProperty(String key) {
        BeloClass val = this.staticProperties.get(key).value;
        return val == null ? Null.NULL : val;
    }

    public static class Property<T> {
        private final T value;
        private final AccessModifier accessModifier;

        public Property(T value, AccessModifier accessModifier) {
            this.value = value;
            this.accessModifier = accessModifier;
        }
    }

}
