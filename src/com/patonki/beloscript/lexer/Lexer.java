package com.patonki.beloscript.lexer;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.errors.ExpectedCharError;
import com.patonki.beloscript.errors.IllegalCharError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static com.patonki.beloscript.lexer.TokenType.*;
/*
 Jakaa koodin osiin, joita kutsutaan nimellä: "Token". Tämän jälkeen koodia on helpompi käsitellä.
 Katso: https://www.freecodecamp.org/news/the-programming-language-pipeline-91d3f449c919/
 */
public class Lexer {
    // merkkijonot, jotka ovat varattu ohjelmointikielen toteutukseen. Esimerkiksi muuttujia ei saa nimetä näillä nimillä.
    private static final String[] KEYWORDS = new String[]{
            "and", "or", "not", "if","else","elif",
            "for", "while","function","return","continue",
            "break","in","try", "catch","import","export", "class","enum", "static",
            "switch", "case", "default", "private", "public", "protected", "extends", "final", "throw",
            "instanceof"
    };
    //käsiteltävä teksti
    private final String text;
    //kohta tekstistä, jota käsitellään
    private final Position pos;
    //tiedosto, jota käsittellään
    private final String fileName;
    private char curChar;
    /*
    sisältää tiedon siitä kuinka syvällä suluissa ollaan. Eli kuinka monta avautuvaa suljetta '(' ollaan löydetty, joille
    ei olla löydetty vastaavaa sulkevaa suljetta ')'
    */
    private int paramDeepness = 0;

    public Lexer(String fileName, String text) {
        this.text = text;
        this.fileName = fileName;
        //aloitetaan index -1, koska advance metodi kasvattaa sen heti nollaan
        pos = new Position(-1, 0, -1, fileName, text);
        curChar = 0;
        advance();
    }

    //siirtyy seuraavaan merkkiin. Jos merkkiä ei löydy, merkiksi laitetaan arvo 0 (null)
    private void advance() {
        pos.advance(curChar);
        curChar = pos.index < text.length() ? text.charAt(pos.index) : 0;
    }
    //ainut public metodi, joka palauttaa LexResultin, joka sisältää token listan ja mahdolliset virheet suorituksessa
    public LexResult makeTokens() {
        ArrayList<Token> tokens = new ArrayList<>();
        //Käydään kaikki tiedoston merkit läpi.
        while (curChar != 0) { // ei olla käsitelty kaikkia merkkejä
            if (curChar == ' ' | curChar == '\t') {
                //välilyönnit ja tabit jätetään huomiotta
                advance();
            }
            else if (curChar == '\n' || curChar == ';') {
                //Sulkujen sisällä olevat välilyönnit jätetään huomiotta
                if (paramDeepness == 0) {
                    tokens.add(new Token(NEWLINE, pos));
                }
                advance();
            }
            else if (curChar == '{') {
                tokens.add(new Token(OPENING_BRACKET, pos));
                advance();
            }
            else if (curChar == '}') {
                tokens.add(new Token(CLOSING_BRACKET, pos));
                advance();
            }
            else if (curChar == '[') {
                tokens.add(new Token(OPENING_SQUARE, pos));
                advance();
            }
            else if (curChar == ']') {
                tokens.add(new Token(CLOSING_SQUARE, pos));
                advance();
            }
            else if (curChar == ':') {
                tokens.add(new Token(DOUBLEDOT, pos));
                advance();
            }
            else if (curChar == '.') {
                tokens.add(new Token(DOT, pos));
                advance();
            }
            else if (curChar == '#') {
                skipComment();
            }
            else if (curChar == ',') {
                tokens.add(new Token(COMMA,pos));
                advance();
            }
            else if (curChar == '"') {
                tokens.add(makeString());
            }
            else if (curChar == '/') {
                tokens.add(makeDivOrEQ());
            }
            else if (curChar == '+') {
                tokens.add(makePlusOrEQorPlusplus());
            }
            else if (curChar == '-') {
                tokens.add(makeMinusOrArrowOrMinEQ());
            }
            else if (curChar == '*') {
                tokens.add(makeMulOrEQ());
            }
            else if (curChar == '^') {
                tokens.add(makePowOrEQ());
            }
            else if (curChar == '%') {
                tokens.add(makeRemainderOrEQ());
            }
            else if (curChar == '(') {
                paramDeepness++;
                tokens.add(new Token(TokenType.LPAREN, pos));
                advance();
            }
            else if (curChar == ')') {
                paramDeepness--;
                tokens.add(new Token(TokenType.RPAREN, pos));
                advance();
            }
            else if (curChar == '!') {
                LexResult r = makeNotEquals();
                if (r.hasError()) return r;
                tokens.add(r.getTokens().get(0));
            }
            else if (curChar == '=') {
                tokens.add(makeEquals());
            }
            else if (curChar == '<') {
                tokens.add(makeLessThan());
            }
            else if (curChar == '>') {
                tokens.add(makeGreaterThan());
            }
            else if (Character.isDigit(curChar)) {
                tokens.add(makeNumber());
            }
            else if (Character.isLetter(curChar)) {
                tokens.add(make_identifier());
            }
            else {
                //Virhe
                Position start = pos.copy();
                char ch = curChar;
                advance();
                return new LexResult(new ArrayList<>(), new IllegalCharError(start, pos, "'" + ch + "' at file: "+fileName));
            }
        }
        tokens.add(new Token(TokenType.EOF, pos)); // EOF = end of file
        return new LexResult(tokens, null);
    }

    // % merkki voi esiintyä yksinään tai '%='
    private Token makeRemainderOrEQ() {
        Position start = pos.copy();
        advance();
        if (curChar == '=') {
            advance();
            return new Token(REMEQ, start, pos.copy());
        }
        return new Token(REMAINDER,pos.copy());
    }
    // + merkki voi esiintyä yksinään, '+=' tai '++'
    private Token makePlusOrEQorPlusplus() {
        Position start = pos.copy();
        advance();
        if (curChar == '=') {
            advance();
            return new Token(PLUSEQ,start,pos.copy());
        }
        if (curChar == '+') {
            advance();
            return new Token(PLUSPLUS, start,pos.copy());
        }
        return new Token(PLUS,pos.copy());
    }
    // * merkki voi esiintyä yksinään tai '*='
    private Token makeMulOrEQ() {
        Position start = pos.copy();
        advance();
        if (curChar == '=') {
            advance();
            return new Token(MULEQ,start,pos.copy());
        }
        return new Token(MUL,pos.copy());
    }
    // ^ merkki voi esiintyä yksinään tai '^='
    private Token makePowOrEQ() {
        Position start = pos.copy();
        advance();
        if (curChar == '=') {
            advance();
            return new Token(POWEQ, start,pos.copy());
        }
        return new Token(POW,pos.copy());
    }
    // / merkki voi esiintyä: '/', '//', '/=' tai '//='
    private Token makeDivOrEQ() {
        Position start = pos.copy();
        advance();
        if (curChar == '/') {
            advance();
            if (curChar == '=') {
                advance();
                return new Token(INTDIVEQ, start,pos.copy());
            }
            return new Token(INTDIV,start,pos.copy());
        }
        if (curChar == '=') {
            advance();
            return new Token(DIVEQ, start,pos.copy());
        }
        return new Token(DIV, pos.copy());
    }
    // - merkki voi esiintyä: '-', '->', '--' tai '-='
    private Token makeMinusOrArrowOrMinEQ() {
        TokenType type = MINUS;
        Position start = pos.copy();
        advance();
        if (curChar == '=') {
            advance();
            type = MINUSEQ;
        }
        if (curChar == '>') {
            advance();
            type = ARROW;
        }
        if (curChar == '-') {
            advance();
            return new Token(MINUSMINUS, start,pos.copy());
        }
        return new Token(type, start, pos.copy());
    }

    //kielen tukemat escape merkit. Esim \n tarkoittaa rivinvaihtoa
    private static final HashMap<Character, Character> escapeCharacters = new HashMap<>();
    static {
        escapeCharacters.put('n','\n');
        escapeCharacters.put('t','\t');
    }
    //kerää merkkejä kunnes löytää sulkevan '"' merkin
    private Token makeString() {
        Position start = pos.copy();
        StringBuilder sb = new StringBuilder();
        boolean escapeCharacter = false;
        advance();
        while (curChar != 0 && (curChar != '"' || escapeCharacter)) {
            if (escapeCharacter) {
                sb.append(escapeCharacters.getOrDefault(curChar,curChar));
                escapeCharacter = false;
            } else {
                if (curChar == '\\') {
                    escapeCharacter = true;
                }
                else {
                    sb.append(curChar);
                }
            }
            advance();
        }
        advance();

        return new Token(STRING, sb.toString(), start, pos);
    }
    //jättää merkkejä huomiotta kunnes löytää rivinvaihdon
    private void skipComment() {
        do {
            advance();
        } while (curChar != '\n' && curChar != 0);
    }
    // > merkki voi esiintyä yksinään tai '>='
    private Token makeGreaterThan() {
        Position start = pos.copy();
        advance();
        TokenType type = TokenType.GT;

        if (curChar == '=') {
            advance();
            type = TokenType.GTE;
        }
        return new Token(type, null, start, pos);
    }
    // < merkki voi esiintyä yksinään tai '<='
    private Token makeLessThan() {
        Position start = pos.copy();
        advance();
        TokenType type = TokenType.LT;

        if (curChar == '=') {
            advance();
            type = TokenType.LTE;
        }
        return new Token(type, null, start, pos);
    }
    // = merkki voi esiintyä yksinään tai '=='
    private Token makeEquals() {
        Position start = pos.copy();
        advance();
        TokenType type = TokenType.EQ;

        if (curChar == '=') {
            advance();
            type = TokenType.EE;
        }
        return new Token(type, null, start, pos);
    }
    // ! merkki esiintyy vain '=' merkin kanssa, palauttaa errorin, jos yhtäsuuruus merkkiä ei ole
    private LexResult makeNotEquals() {
        Position start = pos.copy();
        advance();

        if (curChar == '=') {
            advance();
            return new LexResult(
                    Collections.singletonList(
                            new Token(TokenType.NE, null, start, pos)
                    ), null);
        }
        advance();
        return new LexResult(new ArrayList<>(), new ExpectedCharError(start, pos, "'=' (after !)"));
    }
    // identifier voi olla esimerkiksi muuttujan tai funktion nimi
    private Token make_identifier() {
        StringBuilder bu = new StringBuilder();
        Position pos_start = pos.copy();

        while (curChar != 0 && (Character.isLetterOrDigit(curChar) | curChar == '_')) {
            bu.append(curChar);
            advance();
        }
        String str = bu.toString();
        TokenType type = Arrays.asList(KEYWORDS).contains(str) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
        return new Token(type, str, pos_start, pos);
    }
    // numero voi olla desimaalinumero (piste desimaalierotin) tai kokonaisluku
    private Token makeNumber() {
        Position startPos = pos.copy();
        StringBuilder numStr = new StringBuilder();
        int dotCount = 0;

        while (curChar != 0 && Character.isDigit(curChar) || curChar == '.') {
            if (curChar == '.') {
                if (dotCount == 1) break;
                numStr.append('.');
                dotCount++;
            } else numStr.append(curChar);
            advance();
        }
        if (dotCount == 0) {
            return new Token(TokenType.INT, numStr.toString(), startPos, pos);
        } else return new Token(TokenType.FLOAT, numStr.toString(), startPos, pos);
    }
}
