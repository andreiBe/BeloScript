package com.patonki.beloscript.parser.nodes;

public class PairNode extends Node {
    private final Node key;
    private final Node value;

    public PairNode(Node key, Node value) {
        this.key = key;
        this.value = value;
        this.start = key.getStart();
        this.end = value.getEnd();
    }

    public Node getKey() {
        return key;
    }

    public Node getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{pair:"+key+" value:"+value+"}";
    }
}
