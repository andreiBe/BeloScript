package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;

public class BeloError extends BeloClass {
    public BeloError(RunTimeError e) {
        super(e);
    }

    @Override
    public String toString() {
        return this.getError().toString();
    }

    @Override
    public BeloClass classValue(BeloClass name) {
        if (name.toString().equals("details"))
            return new BeloString(this.getError().getErrorDefails());
        return createNotAMemberOfClassError(name);
    }
}
