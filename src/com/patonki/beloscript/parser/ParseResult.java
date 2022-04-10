package com.patonki.beloscript.parser;


import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.parser.nodes.Node;

public class ParseResult {
    private Node node;
    private BeloScriptError error;
    private int advanceCount = 0;
    private int toReverseCount = 0;
    private int lastRegisteredAdvanceCount = 0;

    public Node register(ParseResult res) {
        lastRegisteredAdvanceCount = res.advanceCount;
        advanceCount += res.advanceCount;
        if (res.hasError()) {
            this.error = res.error;
        }
        return res.node;
    }
    public Node tryRegister(ParseResult res) {
        if (res.hasError()) {
            this.toReverseCount = res.getAdvanceCount();
            return null;
        }
        return register(res);
    }
    public void registerAdvancement() {
        lastRegisteredAdvanceCount = 1;
        advanceCount++;
    }
    public ParseResult success(Node node) {
        this.node = node;
        return this;
    }
    public ParseResult failure(BeloScriptError error) {
        if (this.error == null || lastRegisteredAdvanceCount == 0) {
            this.error = error;
        }
        return this;
    }
    public boolean hasError() {
        return error != null;
    }

    public BeloScriptError getError() {
        return error;
    }

    public Node getNode() {
        return node;
    }

    public int getAdvanceCount() {
        return advanceCount;
    }

    public int getToReverseCount() {
        return toReverseCount;
    }
}
