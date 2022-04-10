package com.test;

import com.patonki.beloscript.lexer.LexResult;
import com.patonki.beloscript.lexer.Lexer;
import com.patonki.beloscript.lexer.Token;
import com.patonki.beloscript.parser.ParseResult;
import com.patonki.beloscript.parser.Parser;
import com.patonki.beloscript.parser.nodes.BinaryOperatorNode;
import com.patonki.helper.FileHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ParserTest {
    private Parser parser;
    private List<Token> tokens(String script) {
        Lexer lexer = new Lexer("test.bel",script);
        LexResult res = lexer.makeTokens();
        assert !res.hasError();
        return res.getTokens();
    }
    @Test
    void errors() {
        //asetetaan arvo asiaan, jonka arvoa ei voi asettaa
        parser = new Parser(tokens("7 = 8"));
        ParseResult res = parser.parse();
        assert res.hasError();
        System.out.println(res.getError());

        //puuttuva sulkeva ]
        parser = new Parser(tokens("list = [6,4,2"));
        res = parser.parse();
        assert res.hasError();
        System.out.println(res.getError());

        //yritetään hakea objektin arvo muulla kuin tekstillä
        parser = new Parser(tokens("hei.7"));
        res = parser.parse();
        assert res.hasError();
        System.out.println(res.getError());

        //puuttuva ) funktiossa
        parser = new Parser(tokens("print(\"hello\""));
        res = parser.parse();
        assert res.hasError();
        System.out.println(res.getError());

        //puuttuva eka parametri
        test("print(,)");

        //puuttuva sulje laskuissa
        test("6 + (8 - 2");

        //importataan jokin muu kuin string
        test("import lib.bel");

        //ei suljettu objekti
        test("obj = {");

        //virheellinen arvojen määrittely
        test("{val,val2}");

        //puuttuvat () merkit funktion määrittelyssä
        test("function sayHello {}");
        test("function {}");

        //puuttuva parametri
        test("function sayHello(name,) {}");

        //puuttuva sulje
        test("function sayHello(name {}");

        //puuttuva avautuva {
        test("function sayHello(name) }");

        //puuttuva ( merkki try lauseekkeessa
        test("try 8/0\ncatch print(e.details)");

        //puuttuva virjeen nimi
        test("try 8/0\ncatch() print(e.details)");

        //puuttuva )
        test("try 8/0\ncatch(e print(e.details)");

        //puuttuva ( for lausekkeessa
        test("for i in list");

        //virheellinen muuttujan nimi
        test("for (7 in list)");

        //puuttuva )
        test("for (i in list");
        test("for ( i = 0 : i < 4 : i++");

        //puuttuva :
        test("for (i = 0 i < 4 i++)");
        test("for (i = 0 : i < 4 i++)");

        //puutteellinen lohko
        test("if (i == 9) {");

        //väärä arvo ++ ja -- operaattorien kanssa
        test("8++");
        test("--8");

    }
    void test(String script) {
        parser = new Parser(tokens(script));
        ParseResult res = parser.parse();
        assert res.hasError();
        System.out.println(res.getError());
    }
    @Test
    void weirdCases() {
        parser = new Parser(tokens("list[6][2]"));
        ParseResult res = parser.parse();
        assert !res.hasError();
        System.out.println(res.getNode());
    }
    @Test
    void toStringMethods() {
        parser = new Parser(tokens(new FileHandler("/com/test/parse.bel").currentContent()));
        ParseResult res = parser.parse();
        if (res.hasError()) {
            System.out.println(res.getError());
            Assertions.fail();
        }

        String result = res.getNode().toString();
        System.out.println(result);
    }
}