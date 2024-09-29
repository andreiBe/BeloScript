package com.patonki.beloscript.lexer;

import com.patonki.beloscript.errors.BeloScriptError;

import java.util.List;

public class LexResult {
    private final List<Token> tokens;
    private final BeloScriptError error;

    /**
     * @param tokens Resulting list of tokens, must not be null
     * @param error possible error, can be null
     */
    public LexResult(List<Token> tokens, BeloScriptError error) {
        this.tokens = tokens;
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    /**
     * @return List of tokens, cannot be null
     */
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
