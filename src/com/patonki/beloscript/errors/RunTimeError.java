package com.patonki.beloscript.errors;


import com.patonki.beloscript.Position;
import com.patonki.beloscript.interpreter.Context;

public class RunTimeError extends BeloScriptError {
    private Context context;
    public RunTimeError(Position posStart, Position posEnd, String details, Context context) {
        super(posStart, posEnd, "Runtime Error", details);
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

    @Override
    public String toString() {
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
            if (pos == null) throw new NullPointerException("Position can't be null!");
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
