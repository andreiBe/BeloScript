package com.patonki.beloscript.lexer;

import com.patonki.beloscript.errors.BeloScriptError;

import java.util.List;

public class LexResult {
    private List<Token> tokens;
    private BeloScriptError error;

    public LexResult(List<Token> tokens, BeloScriptError error) {
        this.tokens = tokens;
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public BeloScriptError getError() {
        return error;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            if (token.getType() == TokenType.NEWLINE) {
                sb.append("\n");
            }
            else if (token.getType() == TokenType.EOF) {
                sb.append("\n-------- END ---------");
            }else {
                sb.append('(').append(token).append(')').append(" ");
            }
        }
        return sb.toString();
    }
}
