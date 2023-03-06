package com.patonki.beloscript.errors;

import java.io.PrintStream;
import java.io.PrintWriter;

public class LocalizedBeloException extends BeloException {
    private final BeloScriptError error;

    public LocalizedBeloException(BeloScriptError error) {
        super(error.getErrorDefails());
        this.error = error;
    }
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.println(error.toString());
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        s.println(error.toString());
    }


    public BeloScriptError getError() {
        return error;
    }
}
