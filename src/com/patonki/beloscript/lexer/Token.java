package com.patonki.beloscript.lexer;

import com.patonki.beloscript.Position;
import com.patonki.datatypes.Pair;

public class Token {
    private final TokenType type;
    private final String value;
    private final Position start;
    private final Position end;


    public Token(TokenType type, String value, Position start, Position end) {
        this.type = type;
        this.value = value;
        this.start = start.copy();
        this.end = end.copy();
    }

    public Token(TokenType type, Position start) {
        this(type, "", start);
    }

    public Token(TokenType type, String value, Position start) {
        this(type,value,start.copy(), start.copy().advance((char)0));
    }
    public Token(TokenType type, Position start, Position end) {
        this(type,null,start,end);
    }

    @Override
    public String toString() {
        if (value == null || value.isEmpty()) {
            return type.toString();
        }
        return type+":"+value;
    }
    public boolean matches(TokenType type, String value) {
        return type == this.type && value.equals(this.value);
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    public double getNumValue() {return Double.parseDouble(value);};

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public boolean typeInList(TokenType... list) {
        for (TokenType tokenType : list) {
            if (type == tokenType) return true;
        }
        return false;
    }
    @SafeVarargs
    public final boolean typeAndValueMatches(Pair<TokenType, String>... list) {
        for (Pair<TokenType,String> token : list) {
            if (type == token.first() && value.equals(token.second())) return true;
        }
        return false;
    }
}
