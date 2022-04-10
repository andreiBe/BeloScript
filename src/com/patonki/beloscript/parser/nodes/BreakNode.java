package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.interpreter.RunTimeResult;

public class BreakNode extends Node {
    public BreakNode(Position start, Position end) {
        this.start = start;
        this.end = end;
        this.visitMethod = (c,i) -> new RunTimeResult().successBreak();
    }

    @Override
    public String toString() {
        return "{break}";
    }
}