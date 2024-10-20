package com.patonki.beloscript.errors;


import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.oop.BeloClassObject;
import com.patonki.beloscript.interpreter.Context;

public class RunTimeError extends BeloScriptError {
    private Context context;
    private BeloClass errorObject = null;

    public RunTimeError(Position posStart, Position posEnd, String details, Context context) {
        super(posStart, posEnd, "Runtime Error", details);
        this.context = context;
    }
    public RunTimeError(Position posStart, Position posEnd, String details, Context context, String errorName) {
        super(posStart, posEnd, errorName, details);
        this.context = context;
    }
    public RunTimeError(BeloScriptError error,Context context) {
        super(error.posStart,error.posEnd, error.errorName, error.details);
        this.context = context;
    }

    public RunTimeError setContext(Context context) {
        this.context = context;
        return this;
    }
    public void setErrorObject(BeloClass errorObject) {
        this.errorObject = errorObject;
    }

    public BeloClass getErrorObject() {
        return errorObject;
    }

    @Override
    public String toString() {
        if (errorObject != null && errorObject instanceof BeloClassObject) {
            String errorString = ((BeloClassObject) errorObject).getErrorString();
            if (errorString != null) {
                return errorString;
            }
        }
        String result = generateTraceback();
        assert posStart != null; //ei pitäisi tapahtua
        result += errorName+": "+details
                +"\n" + StringWithArrows.stringWithArrows(
                posStart.fileContent, posStart, posEnd
        );
        return result;
    }

    private String generateTraceback() {
        StringBuilder result = new StringBuilder();
        Position pos = posStart;
        Context context = this.context;
        StringBuilder res = new StringBuilder();
        while (context != null) {
            result.setLength(0);
            if (pos == null) break;
            result.append("File ").append(pos.fileName).append(", line ")
                    .append(pos.lineNumber + 1).append(", in ")
                    .append(context.getDisplayName()).append(System.lineSeparator());
            pos = context.getParentEntyPosition();
            context = context.getParent();
            res.insert(0, result);
        }
        return res.toString();
    }

}
