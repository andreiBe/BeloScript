package com.patonki.beloscript.errors;


import com.patonki.beloscript.Position;

public class BeloScriptError {
    protected final String errorName;
    protected final String details;
    protected final Position posStart;
    protected final Position posEnd;

    public BeloScriptError(Position posStart, Position posEnd,String errorName, String details) {
        if (posStart == null || posEnd == null || errorName== null || details==null) {
            throw new NullPointerException(
                    "One of the values is null: pos-start: " + posStart
                    + " pos-end: "+posEnd+" errorName: "+errorName+" details: "+details
            );
        }
        this.errorName = errorName;
        this.details = details;
        this.posStart = posStart;
        this.posEnd = posEnd;
    }
    public BeloScriptError(String errorName, String details) {
        this.errorName = errorName;
        this.details = details;
        this.posStart = null;
        this.posEnd = null;
    }

    @Override
    public String toString() {
        if (posStart == null) {
            return errorName+": "+details;
        }
        return errorName+": "+details
                +"\nFile "+posStart.fileName+", line "+(posStart.lineNumber+1)
                +"\n" + StringWithArrows.stringWithArrows(
            posStart.fileContent, posStart, posEnd
        );
    }
    public String getErrorDefails() {
        return this.details;
    }
}
