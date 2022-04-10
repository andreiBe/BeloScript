package com.patonki.beloscript.errors;


import com.patonki.beloscript.Position;

public class InvalidSyntaxError extends BeloScriptError{
    public InvalidSyntaxError(Position posStart, Position posEnd, String details) {
        super(posStart, posEnd,"Invalid Syntax", details);
    }
}
