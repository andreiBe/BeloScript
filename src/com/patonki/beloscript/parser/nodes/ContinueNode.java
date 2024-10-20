package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class ContinueNode extends Node {
    public ContinueNode(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        return new RunTimeResult().successContinue();
    }

    @Override
    public String toString() {
        return "{continue}";
    }

    @Override
    public String convertToJavaCode() {
        return "continue;";
    }
}
