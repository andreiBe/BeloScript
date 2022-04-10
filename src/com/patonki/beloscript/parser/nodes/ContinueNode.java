package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class ContinueNode extends Node {
    public ContinueNode(Position start, Position end) {
        this.start = start;
        this.end = end;
        this.visitMethod = (c,i) -> new RunTimeResult().successContinue();
    }

    @Override
    public String toString() {
        return "{continue}";
    }
}
