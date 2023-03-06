package com.patonki.beloscript.parser.nodes;

public class Case extends Node {
    private final Node condition;
    private final Node statements;
    private final Boolean shouldReturnNull;

    public Case(Node condition, Node statements, boolean shouldReturnNull) {
        this.condition = condition;
        this.statements = statements;
        this.shouldReturnNull = shouldReturnNull;
        this.start = condition.getStart();
        this.end = statements.getEnd();
    }

    public Node getCondition() {
        return condition;
    }

    public Node getStatements() {
        return statements;
    }

    public Boolean getShouldReturnNull() {
        return shouldReturnNull;
    }

    @Override
    public String toString() {
        return "\n{ if:"+condition+" then:\n "+statements+"}\n";
    }
}