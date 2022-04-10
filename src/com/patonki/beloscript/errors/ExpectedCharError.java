package com.patonki.beloscript.errors;


import com.patonki.beloscript.Position;

public class ExpectedCharError extends BeloScriptError{
    public ExpectedCharError(Position posStart, Position posEnd, String details) {
        super(posStart, posEnd, "Expected character", details);
    }
}
