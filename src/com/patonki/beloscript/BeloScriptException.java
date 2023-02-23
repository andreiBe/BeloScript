package com.patonki.beloscript;

import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.errors.BeloScriptError;

import java.io.PrintStream;
import java.io.PrintWriter;

public class BeloScriptException extends BeloException {
    private final BeloScriptError error;

    public BeloScriptException(BeloScriptError error) {
        super(error.getErrorDefails());
        this.error = error;
    }
    public BeloScriptException(String errorName, String message) {
        super(message);
        this.error = new BeloScriptError(errorName,message);
    }
    public BeloScriptException(String message) {
        this("Error",message);
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
