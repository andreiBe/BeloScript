package com.patonki.beloscript.datatypes.basicTypes;

public class JavaClassWrapper extends CustomBeloClass{
    private final Object wrappedObject;

    public JavaClassWrapper(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public Object getWrappedObject() {
        return wrappedObject;
    }
}
