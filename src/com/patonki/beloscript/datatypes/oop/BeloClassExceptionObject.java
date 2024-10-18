package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.errors.RunTimeError;

import java.util.LinkedHashMap;

public class BeloClassExceptionObject extends BeloClassObject{
    private final String details;
    private final String instanceString;
    public BeloClassExceptionObject(String instanceString, BeloClass details, AccessModifier accessModifier) {
        super(createRuntimeProperties(details), accessModifier, null, "Error");
        this.details = details.toString();
        this.instanceString = instanceString;
    }

    @Override
    public boolean isErrorClass() {
        return true;
    }

    @Override
    public RunTimeError getErrorRecursive() {
        return getError();
    }

    public String getInstanceString() {
        return instanceString;
    }

    @Override
    public void initError() {
        RunTimeError runTimeError = new RunTimeError(
                getStart(), getEnd(), details, context, "Error"
        );
        this.setError(runTimeError);
    }

    private static RuntimeProperties createRuntimeProperties(BeloClass details) {
        LinkedHashMap<String, RuntimeProperties.RuntimeProperty> map = new LinkedHashMap<>();
        map.put("details", new RuntimeProperties.RuntimeProperty(details, AccessModifier.PUBLIC, true));
        return new RuntimeProperties(map);
    }
}
