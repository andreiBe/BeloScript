package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;

import java.util.List;

public class StatementsNode extends ListNode{
    public StatementsNode(List<Node> statements, Position start, Position end) {
        super(statements, start, end);
    }

    @Override
    public String convertToJavaCode() {
        StringBuilder result = new StringBuilder();
        for (Node statement : this.statements) {
            String string = statement.convertToJavaCode();
            result.append(string).append(";");
        }
        return result.toString();
    }
}
