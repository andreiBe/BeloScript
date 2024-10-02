package com.patonki.beloscript.parser;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.oop.AccessModifier;
import com.patonki.beloscript.errors.InvalidSyntaxError;
import com.patonki.beloscript.lexer.Token;
import com.patonki.beloscript.lexer.TokenType;
import com.patonki.beloscript.parser.nodes.*;
import com.patonki.datatypes.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.patonki.beloscript.lexer.TokenType.*;

/*
 Luo token listasta syntax puun, jota on helppo lukea ja selvittää operaatioiden järjestys suoritettaessa.
 Esim. 5 + 3 * 2 --> {5+{3*2}}
 Katso: https://www.freecodecamp.org/news/the-programming-language-pipeline-91d3f449c919/
*/

public class Parser {
    private final List<Token> tokens;

    private int tokenIndex;
    private Token curToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        tokenIndex = -1;
        advance();
    }

    //palauttaa ParseResultin, joka sisältää puun tai virheen
    public ParseResult parse() {
        ParseResult res = statements(); //käy ensin uloimman kerroksen eli rivit läpi
        //jos parser ei päässyt tiedoston loppuun (EOF = end of file)
        //en pysty tuottamaan tilannetta missä tämä tapahtuisi, mutta pidän sen silti tässä
        if (!res.hasError() && curToken.getType() != TokenType.EOF) {
            return res.failure(new InvalidSyntaxError(curToken.getStart(), curToken.getEnd(),
                    "Line parsing failed! " + curToken));
        }
        return res;
    }
    // Siirtyy seuraavaan tokeniin
    private void advance() {
        tokenIndex++;
        if (tokenIndex < tokens.size()) {
            curToken = tokens.get(tokenIndex);
        }
    }
    // testaa funktion errorien varalta
    private ParseResult handleErrors(ParseResult res, Supplier<ParseResult> func) {
        Node node = res.register(func.get());
        if (res.hasError()) return res;
        return res.success(node);
    }

    //käy läpi rivejä, kunnes vastaan tulee tiedoston loppu tai '}' merkki
    private ParseResult statements() {
        ParseResult res = new ParseResult();
        Position start = curToken.getStart().copy();

        List<Node> statements = new ArrayList<>();
        Predicate<Token> checkIfShouldStop = token ->
                token.typeInList(CLOSING_BRACKET, EOF)
                || token.matches(KEYWORD, "case")
                || token.matches(KEYWORD, "default");

        while (!checkIfShouldStop.test(curToken)) {
            while (curToken.getType() == NEWLINE) {
                //skipataan
                res.registerAdvancement();
                advance();
            }
            if (checkIfShouldStop.test(curToken)) break;

            Node statement = res.register(statement());
            if (res.hasError()) return res;
            statements.add(statement);
        }
        return res.success(new ListNode(statements,start, curToken.getEnd().copy()));
    }
    /*
    Käy läpi rivin. Rivi voi koostua seuraavista asioista:
        - return ?
        - continue
        - break
        - expression()
     */
    private ParseResult statement() {
        ParseResult res = new ParseResult();
        Position start = curToken.getStart();

        if (curToken.matches(KEYWORD, "return")) {
            res.registerAdvancement();
            advance();
            // return lauseke ei välttämättä palauta mitään
            if (curToken.getType() == NEWLINE) return res.success(new ReturnNode(null, start, curToken.getEnd().copy()));

            Node expr = res.register(expression());
            if (res.hasError()) {
                return res;
                //reverse(res.getToReverseCount());
            }
            return res.success(new ReturnNode(expr, start, curToken.getEnd().copy()));
        }
        if (curToken.matches(KEYWORD, "continue")) {
            res.registerAdvancement();
            advance();
            return res.success(new ContinueNode(start,curToken.getEnd().copy()));
        }
        if (curToken.matches(KEYWORD, "break")) {
            res.registerAdvancement();
            advance();
            return res.success(new BreakNode(start,curToken.getEnd().copy()));
        }
        Node expr = res.register(expression());
        if (res.hasError()) return res;
        return res.success(expr);
    }
    /*
     Expression: comp() =? comp()?
     */
    private ParseResult expression() {
        return handleErrors(new ParseResult(), () ->
                anyOperator(this::comp, this::expression, (left, token, right) -> {
                    ParseResult res = new ParseResult();
                    if (!(left instanceof VarAccessNode)
                            && !(left instanceof IndexAccessNode)
                            && !(left instanceof DotNode)
                    ) {
                        return res.failure(new InvalidSyntaxError(left.getStart(),left.getEnd(),
                                "Expected a value that can be set"));
                    }
                    return res.success(new VarAssignNode(left,token,right));
                },SETTERS));
    }
    /*
    Comp: compExpression() and? compExpression()?
     */
    private ParseResult comp() {
        return handleErrors(new ParseResult(), () ->
                binaryOperator(
                        this::compExpression,
                        this::compExpression,
                        new Pair<>(KEYWORD, "and"),
                        new Pair<>(KEYWORD, "or")
                ));
    }
    /*
        CompExpression:
            not CompExpression()
            arithmeticExpression() ==? arithmeticExpression()?
     */
    private ParseResult compExpression() {
        ParseResult res = new ParseResult();

        if (curToken.matches(KEYWORD,"not")) {
            Token token = curToken;
            res.registerAdvancement();
            advance();
            //kutsutaan funktiota recursiivisesti
            Node node = res.register(compExpression());
            if (res.hasError()) return res;
            return res.success(new UnaryOperationNode(token,node));
        }
        return handleErrors(res, () -> binaryOperator(
                this::arithmeticExpression, this::arithmeticExpression, EE,NE,LT,GT,LTE,GTE
        ));
    }
    /*
        ArithmeticExpression: term() +? term()?
     */
    private ParseResult arithmeticExpression() {
        return binaryOperator(this::term, this::term, PLUS, MINUS);
    }
    /*
     Term: factor() *? factor()?
     */
    private ParseResult term() {
        return binaryOperator(this::factor, this::factor, TokenType.MUL, TokenType.DIV, REMAINDER, INTDIV);
    }
    /*
        Factor:
            --factor()
            indexAccess()
     */
    private ParseResult factor() {
        ParseResult res = new ParseResult();
        Token token = curToken;

        if (token.typeInList(PLUS,MINUS, PLUSPLUS, MINUSMINUS)) {
            res.registerAdvancement();
            advance();
            //kutsutaan recurssiivisesti
            Node factor = res.register(factor());
            if (res.hasError()) return res;

            if (token.typeInList(PLUSPLUS,MINUSMINUS)) {
                if (! (factor instanceof VarAccessNode)) {
                    return res.failure(new InvalidSyntaxError(
                            token.getStart(),factor.getEnd(),
                            "++ and -- only work with variables"));
                }
                return res.success(new UnaryOperationNode(token,(VarAccessNode) factor));
            }
            return res.success(new UnaryOperationNode(token, factor));
        }
        return indexAccessSyntax();
    }
    /*
     indexAccess:
        squares(power())
        power()
     */
    private ParseResult indexAccessSyntax() {
        ParseResult res = new ParseResult();
        Node left = res.register(power());
        if (res.hasError()) return res;
        while (curToken.typeInList(OPENING_SQUARE, LPAREN, DOT)) {
            if (curToken.getType() == OPENING_SQUARE) {
                left = res.register(squares(left));
                if (res.hasError()) return res;
            }
            if (curToken.getType() == LPAREN) {
                left = res.register(functionBuilder(left));
                if (res.hasError()) return res;
            }
            if (curToken.getType() == DOT) {
                advance();
                if (!curToken.typeInList(IDENTIFIER,KEYWORD)) {
                    return res.failure(new InvalidSyntaxError(curToken.getStart(),curToken.getEnd(),
                            "Unexpected token type: "+curToken.getType()+" expected either identifier or keyword"));
                }
                Token name = curToken; advance();
                if (res.hasError()) return res;
                left = new DotNode(left,name);
            }
        }
        return res.success(left);
    }
    /*
        Löytää [] sulkeet target noden jälkeen ja luo indexAccessNoden
     */
    private ParseResult squares(Node target) {
        ParseResult res = new ParseResult();

        res.registerAdvancement();
        advance();
        //sulkeiden sisällä oleva osa
        Node index = res.register(expression());
        if (res.hasError()) return res;

        if (curToken.getType() != CLOSING_SQUARE) {
            return res.failure(new InvalidSyntaxError(curToken.getStart(),curToken.getEnd(),
                    "Expected ]"));
        } else {
            res.registerAdvancement();
            advance();
            Node indexAccNode = new IndexAccessNode(target,index, curPos());
            //kutsutaan recursiivisesti, kun kyseessä on esim. moniulotteinen taulukko[][]
            if (curToken.getType() == OPENING_SQUARE) {
                indexAccNode = res.register(squares(indexAccNode));
                if (res.hasError()) return res;
            }
            return res.success(indexAccNode);
        }
    }

    /*
        dot() ^? factor()?
     */
    private ParseResult power() {
        return binaryOperator(this::postAndPre, this::factor, TokenType.POW);
    }

    /*
        call() .? identifier()?

    private ParseResult dot() {
        return binaryOperator(this::call, this::call, DOT);
    }
    */
    //TODO move this somewhere else
    private ParseResult postAndPre() {
        ParseResult res = new ParseResult();
        Node left = res.register(atom());
        if (res.hasError()) return res;
        while (curToken.typeInList(PLUSPLUS,MINUSMINUS)) {
            if (curToken.typeInList(PLUSPLUS,MINUSMINUS)) {
                Token copy = curToken; advance();
                if (! (left instanceof VarAccessNode)) {
                    return res.failure(new InvalidSyntaxError(left.getStart(),copy.getEnd(),
                            "Post operations only work with variables"));
                }
                left = new PostOperatorNode((VarAccessNode) left,copy);
            }
        }
        return res.success(left);
    }
    /*
    Palauttaa callNoden, jolle on etsitty tarvittavat parametrit
     */
    private ParseResult functionBuilder(Node target) {
        ParseResult res = new ParseResult();
        res.registerAdvancement();
        advance();
        List<Node> args = new ArrayList<>();
        //etsitään parametrit, jos funktiolla on niitä
        if (curToken.getType() != RPAREN) {
            args.add(res.register(expression()));
            if (res.hasError()) return res;

            while (curToken.getType() == COMMA) {
                res.registerAdvancement();
                advance();

                args.add(res.register(expression()));
                if (res.hasError()) return res;
            }

            if (curToken.getType() != RPAREN) {
                return res.failure(new InvalidSyntaxError(curToken.getStart(), curToken.getEnd(),
                        "Expected: ',' or ')'"));
            }
        }
        res.registerAdvancement();
        advance();
        Node callNode = new CallNode(target,args,curToken.getEnd());
        if (curToken.getType() == LPAREN) {
            //kutsutaan recursiivisesti. Jos tilanne on esim. hello()()
            Node node = res.register(functionBuilder(callNode));
            if (res.hasError()) return res;
            return res.success(node);
        }
        else return res.success(callNode);
    }
    /*
    Ohjelmointikielen pienin yksikkö esim. numero, string, identifier tai myös monimutkaisemmat for-silmukat
     */
    private ParseResult atom() {
        ParseResult res = new ParseResult();
        Token token = curToken;
        if (token.typeInList(TokenType.FLOAT, TokenType.INT)) {
            res.registerAdvancement();
            advance(); //TODO register
            return res.success(new NumberNode(token));
        }
        else if (token.getType() == IDENTIFIER) {
            res.registerAdvancement();
            advance();
            return res.success(new VarAccessNode(token));
        }
        else if (token.matches(KEYWORD, "if")) {
            return handleErrors(res, this::ifExpr);
        }
        else if (token.matches(KEYWORD, "while")) {
            return handleErrors(res, this::whileExpression);
        }
        else if (token.matches(KEYWORD, "function")) {
            return handleErrors(res,this::functionDefinition);
        }
        else if (token.matches(KEYWORD, "class")) {
            return handleErrors(res, this::classDefinition);
        }
        else if (token.matches(KEYWORD, "enum")) {
            return handleErrors(res, this::enumDefinition);
        }
        else if (token.matches(KEYWORD, "for")) {
            return handleErrors(res, this::forExpression);
        }
        else if (token.matches(KEYWORD, "try")) {
            return handleErrors(res, this::tryExpression);
        }
        else if (token.matches(KEYWORD, "import")) {
            return handleErrors(res, this::importExpression);
        }
        else if (token.matches(KEYWORD, "switch")) {
            return handleErrors(res, this::switchExpression);
        }
        else if (token.matches(KEYWORD, "export")) {
            return handleErrors(res,this::exportExpression);
        }
        else if (token.getType() == TokenType.LPAREN) {
            res.registerAdvancement();
            advance();
            Node expression = res.register(expression());
            if (res.hasError()) return res;
            if (curToken.getType() == TokenType.RPAREN) {
                res.registerAdvancement();
                advance();
                return res.success(expression);
            } else {
                return res.failure(new InvalidSyntaxError(curToken.getStart(),curToken.getStart().copy().advance('a'),
                        "Expected ) "));
            }
        }
        else if (token.getType() == OPENING_SQUARE) {
            return handleErrors(res, this::listExpression);
        }
        else if (token.getType() == OPENING_BRACKET) {
            return handleErrors(res, this::objectExpression);
        }
        else if (token.getType() == STRING) {
            res.registerAdvancement();
            advance();
            return res.success(new StringNode(token));
        }
        return res.failure(new InvalidSyntaxError(token.getStart(), token.getEnd(),
                "Can't read token: "+ token.getType()));
    }

    private ParseResult exportExpression() {
        ParseResult res = new ParseResult();
        Position start = curPos();
        res.registerAdvancement();
        advance();
        Node object = res.register(expression());
        if (res.hasError()) return res;

        return res.success(new ExportNode(object,start));
    }

    private ParseResult importExpression() {
        ParseResult res = new ParseResult();
        Position start = curPos();
        res.registerAdvancement();
        advance();
        if (curToken.getType() != STRING) {
            return res.failure(new InvalidSyntaxError(curToken.getStart(),
                    curToken.getEnd(), "Expected string"));
        }
        Token path = curToken;
        res.registerAdvancement();
        advance();
        return res.success(new ImportNode(path, start));
    }


    private ParseResult objectExpression() {
        ParseResult res = new ParseResult();
        Position start = curToken.getStart().copy();
        List<Node> pairs = new ArrayList<>();

        res.registerAdvancement();
        do {
            advance();
        } while (curToken.getType() == NEWLINE);

        if (curToken.getType() != CLOSING_BRACKET) {
            Node pair = res.register(getObjectPair());
            if (res.hasError()) return res;
            pairs.add(pair);
            while (curToken.getType() == COMMA) {
                res.registerAdvancement();
                do advance();
                while (curToken.getType() == NEWLINE);
                pairs.add(res.register(getObjectPair()));
                if (res.hasError()) return res;
                while(curToken.getType()==NEWLINE) advance();
            }
            while (curToken.getType() == NEWLINE) advance();
            if (curToken.getType() != CLOSING_BRACKET) {
                //System.out.println("Bruh!");
                return res.failure(new InvalidSyntaxError(
                        curToken.getStart(),curToken.getEnd(),
                        "Expected '}' or ','"
                ));
            }
        }
        res.registerAdvancement();
        advance();
        return res.success(new ObjectNode(pairs,start,curPos()));
    }

    private ParseResult getObjectPair() {
        ParseResult res = new ParseResult();
        Node keyNode = res.register(expression());
        if (res.hasError()) return res;
        if (curToken.getType() != DOUBLEDOT) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(),curToken.getEnd(),
                    "Expected :"
            ));
        }
        res.registerAdvancement();
        advance();
        Node value = res.register(expression());
        if (res.hasError()) return res;
        return res.success(new PairNode(keyNode,value));
    }

    private ParseResult listExpression() {
        ParseResult res = new ParseResult();
        List<Node> elements = new ArrayList<>();
        Position start = curToken.getStart().copy();

        res.registerAdvancement();
        advance();

        if (curToken.getType() != CLOSING_SQUARE) {
            elements.add(res.register(expression()));
            if (res.hasError()) return res;
            while (curToken.getType() == COMMA) {
                res.registerAdvancement();
                advance();

                elements.add(res.register(expression()));
                if (res.hasError()) return res;
            }
            if (curToken.getType() != CLOSING_SQUARE) {
                return res.failure(new InvalidSyntaxError(
                        curToken.getStart(), curToken.getEnd(),
                        "Expected ]"
                ));
            }
        }
        res.registerAdvancement();
        advance();
        return res.success(new ListNode(elements, start,
                curToken.getEnd().copy()));
    }

    private ParseResult ifExpr() {
        return  ifExprCases();
    }

    private ParseResult ifExprElse() {
        ParseResult res = new ParseResult();
        Node elseCase;

        res.registerAdvancement();
        advance();

        if (curToken.getType() == OPENING_BRACKET) {
            Node statements = res.register(block());
            if (res.hasError()) return res;
            elseCase = new ElseNode(true,statements);
            return res.success(elseCase);
        }
        else {
            Node expr = res.register(statement());
            if (res.hasError()) return res;
            elseCase = new ElseNode(false,expr);
            return res.success(elseCase);
        }
    }

    private ParseResult ifExprElifOrElse(Position start) {
        ParseResult res = new ParseResult();
        List<Case> cases = new ArrayList<>();
        ElseNode elseCase = null;
        while (curToken.getType() == NEWLINE) {
            res.registerAdvancement();
            advance();
        }
        if (curToken.matches(KEYWORD, "elif")) {
            IfNode allCases = (IfNode) res.register(ifExprElif());
            if (res.hasError()) return res;
            cases = allCases.getCases();
            elseCase = allCases.getElseCase();
        } else if (curToken.matches(KEYWORD, "else")) {
            ElseNode allCases = (ElseNode) res.register(ifExprElse());
            if (res.hasError()) return res;
            elseCase = allCases;
        }
        return res.success(new IfNode(cases,elseCase,start, curPos()));
    }

    private ParseResult ifExprElif() {
        return ifExprCases();
    }
    private ParseResult ifExprCases() {
        ParseResult res = new ParseResult();
        List<Case> cases = new ArrayList<>();
        ElseNode elseCase;

        Position start = curPos();
        res.registerAdvancement();
        advance();

        Node condition = res.register(expression());
        if (res.hasError()) return res;
        if (curToken.getType() == OPENING_BRACKET) {
            Node statements = res.register(block());
            if (res.hasError()) return res;
            cases.add(new Case(condition, statements, true));

            IfNode allCases = (IfNode) res.register(ifExprElifOrElse(start));
            if (res.hasError()) return res;
            cases.addAll(allCases.getCases());
            elseCase = allCases.getElseCase();
        } else {
            Node node = res.register(statement());
            if (res.hasError()) return res;
            cases.add(new Case(condition,node,false));

            IfNode ifNode = (IfNode) res.register(ifExprElifOrElse(start));
            if (res.hasError()) return res;
            cases.addAll(ifNode.getCases());
            elseCase = ifNode.getElseCase();
        }
        return res.success(new IfNode(cases,elseCase, start, curPos()));
    }
    private Position curPos() {
        return curToken.getStart().copy();
    }
    private ParseResult whileExpression() {
        ParseResult res = new ParseResult();
        Position start = curPos();

        res.registerAdvancement();
        advance();

        Node condition = res.register(expression());
        if (res.hasError())return res;

        if (curToken.getType() == OPENING_BRACKET) {
            Node statements = res.register(block());
            if (res.hasError()) return res;
            return res.success(new WhileNode(condition, statements, start, curPos()));
        }
        Node statement = res.register(statement());
        if (res.hasError()) return res;

        return res.success(new WhileNode(condition,statement, start, curPos()));
    }
    private ParseResult enumDefinition() {
        ParseResult res = new ParseResult();
        Position start = curPos();
        res.registerAdvancement();
        advance();

        if (curToken.getType() != IDENTIFIER) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(), curToken.getEnd(),
                    "Expected identifier"
            ));
        }
        String enumName = curToken.getValue();
        res.registerAdvancement();
        advance();
        if (curToken.getType() != LPAREN) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(), curToken.getEnd(),
                    "Expected ("
            ));
        }
        res.registerAdvancement();
        advance();

        ArrayList<Token> arguments = arguments(res);
        if (res.hasError()) return res;
        if (curToken.getType() != RPAREN) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(), curToken.getEnd(),
                    "Expected )"
            ));
        }
        res.registerAdvancement();
        advance();
        return res.success(new EnumDefNode(enumName, arguments, start, curToken.getEnd()));
    }
    private ParseResult classDefinition() {
        ParseResult res = new ParseResult();
        Position start = curPos();
        res.registerAdvancement();
        advance();
        if (curToken.getType() != IDENTIFIER) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(), curToken.getEnd(),
                    "Expected identifier"
            ));
        }
        String className = curToken.getValue();
        res.registerAdvancement();
        advance();
        ArrayList<Token> arguments;
        if (curToken.getType() == LPAREN) {
            res.registerAdvancement();
            advance();
            arguments = arguments(res);
            if (res.hasError()) return res;
            if (curToken.getType() != RPAREN) {
                return res.failure(new InvalidSyntaxError(
                        curToken.getStart(), curToken.getEnd(),
                        "Expected )"
                        ));
            }
            res.registerAdvancement();
            advance();
        } else {
            arguments = new ArrayList<>();
        }
        ArrayList<ClassDefNode.ClassProperty> properties = new ArrayList<>();
        Node parent = null;

        if (curToken.matches(KEYWORD, "extends")) {
            res.registerAdvancement();
            advance();

            parent = res.register(this.expression());
            if (res.hasError()) return res;
        }
        if (curToken.getType() == NEWLINE) {
            this.skipNewlines(res);
            return res.success(new ClassDefNode(className, arguments,
                    properties, parent, start, curToken.getEnd()));
        }
        this.expect(res, OPENING_BRACKET);
        if (res.hasError()) return res;

        boolean constructorFound = false;
        while (curToken.typeInList(IDENTIFIER, NEWLINE) || curToken.matches(KEYWORD, "static")) {
            while (curToken.getType() == NEWLINE) {
                res.registerAdvancement();
                advance();
            }
            if (curToken.getType() == CLOSING_BRACKET) {
                res.registerAdvancement();
                advance();
                break;
            }
            boolean isStatic = false;
            AccessModifier accessModifier = AccessModifier.PUBLIC;
            if (curToken.matches(KEYWORD, "static")) {
                isStatic = true;
            }
            else if (curToken.matches(KEYWORD, "private")) {
                accessModifier = AccessModifier.PRIVATE;
            }
            else if (curToken.matches(KEYWORD, "protected")) {
                accessModifier = AccessModifier.PROTECTED;
            }
            else if (curToken.getType() == KEYWORD && !curToken.matches(KEYWORD, "public")) {
                return res.failure(new InvalidSyntaxError(
                        curToken.getStart(), curToken.getEnd(),
                        "Did not expect keyword of type: " + curToken.getValue()
                ));
            }
            if (curToken.getType() == KEYWORD) {
                res.registerAdvancement();
                advance();
            }

            Token nameOfProperty = this.expect(res, IDENTIFIER);
            if (res.hasError()) return res;

            if (nameOfProperty.getValue().equals(className)) {
                if (constructorFound) {
                    return res.failure(new InvalidSyntaxError(
                            curToken.getStart(), curToken.getEnd(),
                            "Only one constructor allowed!"
                    ));
                }

                constructorFound = true;
                Node constructor = res.register(functionBlock());
                if (res.hasError()) return res;
                properties.add(new ClassDefNode.ClassProperty(isStatic, accessModifier, nameOfProperty.getValue(), constructor));
                //properties.put(new StringNode(nameOfProperty), constructor);
                continue;
            }
            if (curToken.getType() == EQ) {
                //variable
                res.registerAdvancement();
                advance();
                Node value = res.register(comp());
                if (res.hasError()) return res;

                properties.add(new ClassDefNode.ClassProperty(isStatic, accessModifier, nameOfProperty.getValue(),value));
                res.registerAdvancement();
                advance();
            }
            else if (curToken.getType() == LPAREN) {
                //func
                Node func = res.register(functionDefinition(true, true));
                if (res.hasError()) return res;

                properties.add(new ClassDefNode.ClassProperty(isStatic, accessModifier, nameOfProperty.getValue(),func));
            }
            else if (curToken.getType() == NEWLINE) {
                properties.add(new ClassDefNode.ClassProperty(isStatic, accessModifier, nameOfProperty.getValue(),null));
            }
            else {
                return res.failure(new InvalidSyntaxError(
                        curToken.getStart(),curToken.getEnd(),
                        "Expected ( or = or } but got: " + curToken));
            }
        }

        return res.success(new ClassDefNode(className, arguments,
                properties, parent, start, curToken.getEnd()));
    }
    private ParseResult functionDefinition() {
        return functionDefinition(false, false);
    }
    private ParseResult functionDefinition(boolean mustBeAnonymous, boolean skipFirst) {
        ParseResult res = new ParseResult();
        Position start = curPos();
        if (!skipFirst) {
            res.registerAdvancement();
            advance();
        }

        Token varName = null;
        if (curToken.getType() == IDENTIFIER && !mustBeAnonymous) {
            varName = curToken;
            res.registerAdvancement();
            advance();
            if (curToken.getType() != LPAREN) {
                return res.failure(new InvalidSyntaxError(
                        curToken.getStart(),curToken.getEnd(),
                        "Expected ("));
            }
        } else if (curToken.getType() != LPAREN){
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(),curToken.getEnd(),
                    "Expected ( or identifier"));
        }
        res.registerAdvancement();
        advance();

        List<Token> argumentNames = arguments(res);
        if (res.hasError()) return res;

        if (curToken.getType() != RPAREN) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(),curToken.getEnd(),
                    "Expected )"
            ));
        }
        res.registerAdvancement();
        advance();

        boolean isAutoReturn = curToken.getType() == ARROW;
        Node body = res.register(functionBlock());
        if (res.hasError()) return res;
        return res.success(new FuncDefNode(
           varName, argumentNames, body,isAutoReturn, start, curPos()
        ));
    }
    private ArrayList<Token> arguments(ParseResult res) {
        ArrayList<Token> argumentNames = new ArrayList<>();
        if (curToken.getType() == IDENTIFIER) {
            argumentNames.add(curToken);
            res.registerAdvancement();
            advance();

            while (curToken.getType() == COMMA) {
                res.registerAdvancement();
                advance();

                if (curToken.getType() != IDENTIFIER) {
                    res.failure(new InvalidSyntaxError(
                            curToken.getStart(),curToken.getEnd(),
                            "Expected identifier"
                    ));
                }
                argumentNames.add(curToken);
                res.registerAdvancement();
                advance();
            }
        }
        return argumentNames;
    }
    private ParseResult functionBlock() {
        ParseResult res = new ParseResult();
        if (curToken.getType() == ARROW) {
            res.registerAdvancement();
            advance();

            Node body = res.register(expression());
            if (res.hasError()) return res;

            return res.success(body);
        }

        if (curToken.getType() != OPENING_BRACKET) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(),curToken.getEnd(),
                    "Expected -> or {"
            ));
        }
        Node body = res.register(block());
        if (res.hasError()) return res;
        return res.success(body);
    }
    private ParseResult switchExpression() {
        ParseResult res = new ParseResult();
        Position start = curPos();
        res.registerAdvancement();
        advance();

        this.expect(res, LPAREN);
        if (res.hasError()) return res;

        Node var = res.register(this.expression());
        if (res.hasError()) return res;

        this.expect(res, RPAREN);
        if (res.hasError()) return res;

        this.expect(res, OPENING_BRACKET);
        if (res.hasError()) return res;

        ArrayList<Pair<List<Node>, Node>> cases = new ArrayList<>();
        Node defaultCase = null;
        skipNewlines(res);

        ArrayList<Node> conditions = new ArrayList<>();
        while (curToken.matches(KEYWORD, "case") || curToken.matches(KEYWORD, "default")) {
            boolean isDefault = curToken.getValue().equals("default");

            res.registerAdvancement();
            advance();
            if (!isDefault) {
                Node condition = res.register(this.expression());
                if (res.hasError()) return res;
                conditions.add(condition);
            }
            this.expect(res, DOUBLEDOT);
            if (res.hasError()) return res;

            skipNewlines(res);
            if (curToken.matches(KEYWORD, "case")) {
                continue;
            }
            Node body = res.register(statements());
            if (res.hasError()) return res;

            if (isDefault) {
                defaultCase = body;
                break;
            } else {
                cases.add(new Pair<>(new ArrayList<>(conditions), body));
                conditions.clear();
            }
            skipNewlines(res);
        }
        this.expect(res, CLOSING_BRACKET);
        if (res.hasError()) return res;
        return res.success(new SwitchNode(cases, defaultCase, var, start, curPos()));
    }
    private ParseResult tryExpression() {
        ParseResult res = new ParseResult();
        Position start = curPos();
        res.registerAdvancement();
        advance();

        Node body;
        if (curToken.getType() != OPENING_BRACKET) {
            body = res.register(expression());
        } else {
            body = res.register(block());
        }
        if (res.hasError()) return res;
        while (curToken.getType() == NEWLINE) advance();

        if (!curToken.matches(KEYWORD, "catch")) {
            return res.success(new TryNode(null,body,null, start, curPos()));
        }
        res.registerAdvancement();
        advance();
        if (curToken.getType() != LPAREN) {
            return res.failure(new InvalidSyntaxError(curToken.getStart(),curToken.getEnd(),
                    "Expected '('"));
        }
        res.registerAdvancement();
        advance();
        if (curToken.getType() != IDENTIFIER) {
            return res.failure(new InvalidSyntaxError(curToken.getStart(),curToken.getEnd(),
                    "Expected identifier"));
        }
        String errorVariableName = curToken.getValue();
        res.registerAdvancement();
        advance();
        if (curToken.getType() != RPAREN) {
            return res.failure(new InvalidSyntaxError(curToken.getStart(),curToken.getEnd(),
                    "Expected )"));
        }
        res.registerAdvancement();
        advance();

        Node catchBody;
        if (curToken.getType() != OPENING_BRACKET) {
            catchBody = res.register(expression());
        } else {
            catchBody = res.register(block());
        }
        if (res.hasError()) return res;

        return res.success(new TryNode(errorVariableName,body,catchBody, start,curPos()));
    }
    private ParseResult forExpression() {
        ParseResult res = new ParseResult();

        Position start = curPos();
        res.registerAdvancement();
        advance();
        if (curToken.getType() != LPAREN) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(), curToken.getEnd(),
                    "Expected ("
            ));
        }
        res.registerAdvancement();
        advance();

        Node startValue = res.register(expression());
        if (res.hasError()) return res;
        //foreach loop
        if (curToken.matches(KEYWORD, "in")) {
            if (! (startValue instanceof VarAccessNode)) {
                return res.failure(new InvalidSyntaxError(startValue.getStart(),startValue.getEnd(),
                        "Expected identifier"));
            }
            VarAccessNode startVal= (VarAccessNode) startValue;
            res.registerAdvancement();
            advance();

            Node list = res.register(expression());
            if (res.hasError()) return res;
            if (curToken.getType() != RPAREN) {
                return res.failure(new InvalidSyntaxError(
                        curToken.getStart(), curToken.getEnd(),
                        "Expected )"
                ));
            }
            res.registerAdvancement();
            advance();
            if (curToken.getType() == OPENING_BRACKET) {
                Node body = res.register(block());
                if (res.hasError()) return res;
                return res.success(new ForEachNode(startVal, list, body, true, start, curPos()));
            }
            Node body = res.register(statement());
            if (res.hasError()) return res;

            return res.success(new ForEachNode(startVal,list,body,false, start, curPos()));
        }
        if (curToken.getType() != DOUBLEDOT) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(),curToken.getEnd(),
                    "Expected :"
            ));
        }
        res.registerAdvancement();
        advance();

        Node condition = res.register(expression());
        if (res.hasError()) return res;

        if (curToken.getType() != DOUBLEDOT) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(),curToken.getEnd(),
                    "Expected :"
            ));
        }
        res.registerAdvancement();
        advance();

        Node change = res.register(expression());
        if (res.hasError()) return res;

        if (curToken.getType() != RPAREN) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(), curToken.getEnd(),
                    "Expected )"
            ));
        }
        res.registerAdvancement();
        advance();

        if (curToken.getType() == OPENING_BRACKET) {
            Node body = res.register(block());
            if (res.hasError()) return res;
            return res.success(new ForNode(startValue, condition, change, body, true,start,curPos()));
        }
        Node body = res.register(statement());
        if (res.hasError()) return res;

        return res.success(new ForNode(startValue,condition,change,body,false, start, curPos()));
    }

    private ParseResult block() {
        ParseResult res = new ParseResult();
        res.registerAdvancement();
        advance();
        Node statements = res.register(statements());
        if (res.hasError())return res;

        if (curToken.getType() != CLOSING_BRACKET) {
            return res.failure(new InvalidSyntaxError(
                    curToken.getStart(),curToken.getEnd(),
                    "Expected }"
            ));
        }
        res.registerAdvancement();
        advance();
        return res.success(statements);
    }

    private ParseResult binaryOperator(Supplier<ParseResult> func, Supplier<ParseResult> func2, TokenType... tokens) {
        return anyOperator(func, func2, (left, token, right) -> {
            ParseResult res = new ParseResult();
            return res.success(new BinaryOperatorNode(left,token,right));
        }, tokens);
    }
    private interface NodeConstructor {
        ParseResult construct(Node left, Token token, Node right);
    }
    private ParseResult anyOperator(Supplier<ParseResult> leftfunc, Supplier<ParseResult> rightfunc, NodeConstructor cons,TokenType... tokens) {
        ParseResult res = new ParseResult();
        Node left = res.register(leftfunc.get());
        if (res.hasError()) return res;
        while (curToken.typeInList(tokens)) {
            Token operatorToken = curToken;
            res.registerAdvancement(); advance();
            Node right = res.register(rightfunc.get());
            if (res.hasError()) return res;
            left = res.register(cons.construct(left,operatorToken,right));
            if (res.hasError()) return res;
        }
        return res.success(left);
    }
    @SafeVarargs
    private final ParseResult binaryOperator(Supplier<ParseResult> func,
                                             Supplier<ParseResult> func2,
                                             Pair<TokenType, String>... tokens) {
        ParseResult res = new ParseResult();
        Node left = res.register(func.get());
        if (res.hasError()) return res;
        while (curToken.typeAndValueMatches(tokens)) {
            Token operatorToken = curToken;
            res.registerAdvancement();
            advance();
            Node right = res.register(func2.get());
            if (res.hasError()) return res;
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return res.success(left);
    }
    private Token expect(ParseResult res, TokenType tokenType) {
        return expect(res, tokenType, null);
    }
    private Token expect(ParseResult res, TokenType tokenType, String value) {
        if (curToken.getType() != tokenType || (value != null && !curToken.getValue().equals(value))) {
            String errorMsg = "Expected " + tokenType;
            if (value != null) {
                errorMsg += " of value " + value;
            }
            errorMsg += " but got " + curToken;
            res.failure(new InvalidSyntaxError(curToken.getStart(), curToken.getEnd(),
                    errorMsg));
        }
        Token token = curToken;
        res.registerAdvancement();
        advance();
        return token;
    }
    private void skipNewlines(ParseResult res) {
        while (curToken.getType() == NEWLINE) {
            res.registerAdvancement();
            advance();
        }
    }
}
