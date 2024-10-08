package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.errors.RunTimeError;

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
        BeloClass resultOfParent = this.parent.setClassValue(name.toString(), value);
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

    @Override
    public BeloClass setClassValue(String name, BeloClass newValue) {
        if (!this.properties.containsProperty(name)) {
            return tryToSetInParent(BeloString.create(name), newValue);
        }
        AccessModifier target = this.properties.getAccessModifier(name);

        if (!this.accessModifier.canAccess(target))
            return createCannotAccessError(BeloString.create(name), target);
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
        return this.properties.toString();
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

