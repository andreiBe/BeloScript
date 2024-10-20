package com.test;

import com.patonki.beloscript.lexer.LexResult;
import com.patonki.beloscript.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void illegalChar() {
        //illegalchar
        Lexer lexer = new Lexer("test.bel", "print(¤)");
        LexResult res = lexer.makeTokens();
        assert res.hasError();
        System.out.println(res.getError());

        lexer = new Lexer("test.bel", "print(\"I want a newline: \\nworked?\")\n");
        res = lexer.makeTokens();
        assert !res.hasError();
        assertEquals("I want a newline: \nworked?", res.getTokens().get(2).getValue());
        System.out.println(res); //toString()
        System.out.println(res.getTokens().get(2)); //toString()
        System.out.println(res.getTokens().get(1)); //toString()

        lexer = new Lexer("test.bel", "!!");
        res = lexer.makeTokens();
        assert res.hasError();
        System.out.println(res.getError());
    }
}