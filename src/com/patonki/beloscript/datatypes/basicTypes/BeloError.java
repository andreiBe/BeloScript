package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;

public class BeloError extends CustomBeloClass {
    public BeloError(RunTimeError e) {
        this.setError(e);
    }
    @Override
    public String toString() {
        return this.getError().toString();
    }


    @Override
    public BeloClass classValue(BeloClass name) {
        if (name.toString().equals("details"))
            return BeloString.create(this.getError().getErrorDefails());
        return createNotAMemberOfClassError(name);
    }
}