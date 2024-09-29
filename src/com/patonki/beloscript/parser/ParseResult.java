package com.patonki.beloscript.parser;


import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.parser.nodes.Node;

public class ParseResult {
    private Node node;
    private BeloScriptError error;
    private int advanceCount = 0;
    private int lastRegisteredAdvanceCount = 0;

    protected Node register(ParseResult res) {
        lastRegisteredAdvanceCount = res.advanceCount;
        advanceCount += res.advanceCount;
        if (res.hasError()) {
            this.error = res.error;
        }
        return res.node;
    }
    protected void registerAdvancement() {
        lastRegisteredAdvanceCount = 1;
        advanceCount++;
    }
    protected ParseResult success(Node node) {
        this.node = node;
        return this;
    }
    protected ParseResult failure(BeloScriptError error) {
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
}
