package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.function.BeloFunction;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;

public class BeloClassObject extends BeloClass {
    private final RuntimeProperties properties;
    private final AccessModifier accessModifier;
    private final BeloClassObject parent;
    private final String className;

    public BeloClassObject(RuntimeProperties properties,
                           AccessModifier accessModifier,
                           BeloClassObject parent, String className) {
        this.properties = properties;
        this.accessModifier = accessModifier;

        this.parent = parent;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public RunTimeError getErrorRecursive() {
        if (this.parent != null) {
            return this.parent.getErrorRecursive();
        }
        return null;
    }
    public boolean isErrorClass() {
        return this.parent != null && this.parent.isErrorClass();
    }
    public void initError() {
        if (this.parent != null) {
            this.parent.setPos(this.getStart(), this.getEnd());
            this.parent.initError();
        }
    }

    private BeloClass tryToFindFromParent(BeloClass name) {
        if (this.parent == null) return createNotAMemberOfClassError(name);
        BeloClass resultOfParent = this.parent.classValue(name);
        if (resultOfParent instanceof NotMemberOfClassError) {
            return createNotAMemberOfClassError(name);
        }
        return resultOfParent;
    }
    private BeloClass tryToSetInParent(BeloClass name, BeloClass value) {
        if (this.parent == null) return createNotAMemberOfClassError(name);
        BeloClass resultOfParent = this.parent.setClassValue(name, value);
        if (resultOfParent instanceof NotMemberOfClassError) {
            return createNotAMemberOfClassError(name);
        }
        return resultOfParent;
    }
    //NÄITÄ EI TODELLAKAAN OLE TARKOITUS YLIKIRJOITTAA, MUTTA OBJEKTIT OVAT POIKKEUS
    @Override
    public BeloClass classValue(BeloClass name) {
        if (!this.properties.containsProperty(name.toString())) {
            return this.tryToFindFromParent(name);
        }
        AccessModifier target = this.properties.getAccessModifier(name.toString());
        if (!this.accessModifier.canAccess(target))
            return createCannotAccessError(name, target);
        return this.properties.getPropertyValue(name.toString());
    }

    public String getErrorString() {
        BeloClass toString = this.classValue(BeloString.create("toString"));
        if (toString.hasError()) return null;
        if (toString instanceof BeloFunction) {
            RunTimeResult res = toString.execute(new ArrayList<>());
            if (res.hasError()) return res.getError().toString();
            //lol
            return res.register(res).toString();
        }
        return null;
    }

    @Override
    public BeloClass setClassValue(BeloClass nameClass, BeloClass newValue) {
        String name = nameClass.toString();
        if (!this.properties.containsProperty(name)) {
            return tryToSetInParent(BeloString.create(name), newValue);
        }
        AccessModifier target = this.properties.getAccessModifier(name);

        if (!this.accessModifier.canAccess(target))
            return createCannotAccessError(nameClass, target);
        if (this.properties.isFinal(name)) {
            return createCannotAssignToFinalVariableError(name);
        }
        this.properties.setProperty(name, newValue);
        return newValue;
    }

    private BeloError createCannotAccessError(BeloClass name, AccessModifier target) {
        return new CannotAccessError(new RunTimeError(name.getStart(),name.getEnd(),
                "Cannot access class member: '"+ name+"' class: "+this.getClass().getSimpleName()
                        + " because it is " + target,this.context));
    }
    @Override
    protected BeloError createNotAMemberOfClassError(BeloClass name) {
        return new NotMemberOfClassError(new RunTimeError(getStart(),getEnd(),
                "Not a member of class: '"+ name+"' class: "+this.className,this.context));
    }
    private BeloError createCannotAssignToFinalVariableError(String name) {
        return new BeloError(new RunTimeError(getStart(), getEnd(),
                "Cannot asign to final variable: " + name, this.context));
    }

    @Override
    public String toString() {
        if (getErrorRecursive() != null) {
            return getErrorRecursive().toString();
        }
        return this.properties.toString();
    }

    @Override
    public BeloClass asString() {
        if (getErrorRecursive() != null) {
            return BeloString.create(getErrorRecursive().toString());
        }
        if (this.properties == null) {
            return super.asString();
        }
        if (!this.properties.containsProperty("toString")) {
            return super.asString();
        }
        BeloClass toString = this.properties.getPropertyValue("toString");
        if (toString instanceof BeloFunction) {
            RunTimeResult res = toString.execute(new ArrayList<>());
            if (res.hasError()) return new BeloError(res.getError());
            //lol
            return res.register(res);
        }
        return super.asString();
    }

    private static class CannotAccessError extends BeloError {
        public CannotAccessError(RunTimeError e) {
            super(e);
        }
    }
    private static class NotMemberOfClassError extends BeloError {
        public NotMemberOfClassError(RunTimeError e) {
            super(e);
        }
    }
}

