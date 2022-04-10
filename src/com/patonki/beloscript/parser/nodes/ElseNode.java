package com.patonki.beloscript.parser.nodes;

public class ElseNode extends Node {
    private final Boolean shouldReturnNull;
    private final Node statements;

    public ElseNode(Boolean shouldReturnNull, Node statements) {
        this.shouldReturnNull = shouldReturnNull;
        this.statements = statements;
    }

    public Boolean getShouldReturnNull() {
        return shouldReturnNull;
    }

    public Node getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return "\n{else:"+statements+"}\n";
    }
}
