package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;

import java.util.LinkedHashMap;

public class RuntimeProperties {
    private final LinkedHashMap<String, RuntimeProperty> properties;

    public RuntimeProperties(LinkedHashMap<String, RuntimeProperty> properties) {
        this.properties = properties;
    }

    public void setProperty(String key, BeloClass value) {
        properties.get(key).value = value;
    }
    public BeloClass getPropertyValue(String key) {
        return properties.get(key).value;
    }
    public AccessModifier getAccessModifier(String key) {
        return properties.get(key).accessModifier;
    }
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }
    public boolean isFinal(String key) {
        return properties.get(key).isFinal;
    }
    public static class RuntimeProperty {
        private BeloClass value;
        private final AccessModifier accessModifier;
        private final boolean isFinal;

        public RuntimeProperty(BeloClass value, AccessModifier accessModifier, boolean isFinal) {
            this.value = value;
            this.accessModifier = accessModifier;
            this.isFinal = isFinal;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        boolean hasStarted = false;
        for (String key : properties.keySet()) {
            if (hasStarted) builder.append(", ");
            builder.append(key).append(':').append(properties.get(key).value);
            hasStarted = true;
        }
        builder.append('}');
        return builder.toString();
    }
}
