package com.jlox.parser;

import com.jlox.error.IErrorHandler;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import com.jlox.statement.Statement;

/**
 * Interface for parsing a list of tokens
 * Each implementation is responsible for parsing a specific part of the language (i.e. Expression, Statement...)
 * @param <R> The return type of the parse statement
 */
public abstract class AbstractParser<R> {

    protected TokenSource tokens;

    public R parse(Iterable<Token> token) {
        return parse(new TokenSource(token));
    }

    abstract R parse(TokenSource tokens);
    /**
     * Check if the next token matches one of the provided types
     */
    protected boolean checkMultiple(TokenType... types) {
        for (TokenType type : types) {
            if (check(type))
                return true;
        }
        return false;
    }

    /**
     * Check if the next token matches the desired type
     */
    protected boolean check(TokenType type) {
        if (tokens.isAtEnd()) {
            return false;
        }
        return tokens.peek().type == type;
    }

}
