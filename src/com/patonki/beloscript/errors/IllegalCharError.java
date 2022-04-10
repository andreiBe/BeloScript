package com.patonki.beloscript.errors;

import com.patonki.beloscript.Position;

public class IllegalCharError extends BeloScriptError{

    public IllegalCharError(Position posStart, Position posEnd, String details) {
        super(posStart, posEnd, "Illegal Character", details);
    }
}
