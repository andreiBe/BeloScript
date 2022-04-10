package com.patonki.beloscript.lexer;

import com.patonki.beloscript.Calculation;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;

public enum TokenType {
    INT, FLOAT,
    PLUS, MINUS, MUL, DIV, INTDIV, REMAINDER,POW,
    LPAREN, RPAREN,
    EOF, IDENTIFIER, KEYWORD,
    EQ, PLUSEQ, MINUSEQ, DIVEQ,INTDIVEQ, MULEQ, POWEQ, REMEQ,
    PLUSPLUS, MINUSMINUS,
    EE, NE,LT,GT,LTE,GTE, NEWLINE,
    OPENING_BRACKET, CLOSING_BRACKET, STRING, ARROW,
    OPENING_SQUARE, CLOSING_SQUARE, DOUBLEDOT, DOT,
    COMMA;
    public static final TokenType[] SETTERS = new TokenType[] {EQ, PLUSEQ, MINUSEQ, DIVEQ,INTDIVEQ, MULEQ, POWEQ, REMEQ};
    public static Calculation getMatchingCalculation(TokenType type) {
        switch (type) {
            case PLUS:
            case PLUSEQ:
                return BeloClass::add;
            case MINUS:
            case MINUSEQ:
                return BeloClass::substract;
            case MUL:
            case MULEQ:
                return BeloClass::multiply;
            case DIV:
            case DIVEQ:
                return BeloClass::divide;
            case INTDIV:
            case INTDIVEQ:
                return BeloClass::intdiv;
            case REMAINDER:
            case REMEQ:
                return BeloClass::remainder;
            case POW:
            case POWEQ:
                return BeloClass::power;
            case EE:
                return (first, second) -> new BeloDouble(first.compare(second) == 0);
            case LTE:
                return ((first, second) -> new BeloDouble(first.compare(second) <= 0));
            case GTE:
                return ((first, second) -> new BeloDouble(first.compare(second) >= 0));
            case LT:
                return ((first, second) -> new BeloDouble(first.compare(second) < 0));
            case GT:
                return ((first, second) -> new BeloDouble(first.compare(second) > 0));
            case NE:
                return ((first, second) -> new BeloDouble(first.compare(second) != 0));
        }
        return null;
    }
}
