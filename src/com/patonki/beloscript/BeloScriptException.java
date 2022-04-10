package com.patonki.beloscript;

import com.patonki.beloscript.errors.BeloScriptError;

import java.io.PrintStream;

public class BeloScriptException extends Exception{
    private final BeloScriptError error;

    public BeloScriptException(BeloScriptError error) {
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

    public BeloScriptError getError() {
        return error;
    }
}
