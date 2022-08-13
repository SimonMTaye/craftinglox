package com.jlox.parser;

import com.jlox.error.LoxError;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;

/**
 * Interface for parsing a list of tokens
 * Each implementation is responsible for parsing a specific part of the
 * language (i.e. Expression, Statement...)
 *
 * @param <R> The return type of the parse statement
 */
public abstract class AbstractParser<R> {

    protected static final int MAX_ARGS = 255;
    protected TokenSource tokens;

    public R parse(Iterable<Token> token) {
        return parse(new TokenSource(token));
    }

    abstract R parse(TokenSource inputTokens);

    /**
     * Check if the next token matches one of the provided types.
     */
    protected boolean checkMultiple(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the next token matches the desired type.
     */
    protected boolean check(TokenType type) {
        return !tokens.isAtEnd() && tokens.peek().type == type;
    }

    /**
     * Check if the next token is off the expected type and return it if it is or
     * throw the given error if not.
     *
     * @param type    The desired type
     * @param message The error to throw if the next token is of a different type
     * @return The token whose token matches the desired type
     */
    protected Token checkAndAdvance(TokenType type, LoxError message) {
        if (check(type)) {
            return tokens.advance();
        }
        throw message;
    }

    /**
     * Create a new ParseError pointing to the previous Token with the desired
     * message.
     *
     * @param message error message
     * @return the error
     */
    protected ParseLoxError newError(String message) {
        return new ParseLoxError(message, tokens.previous().offset);
    }

    /**
     * When there is an error parsing, advance the token stream until the next
     * "clean slate"
     * like the declaration of a variable, class, etc.
     */
    protected void synchronize() {
        while (!tokens.isAtEnd()) {
            if (tokens.previous().type == TokenType.SEMICOLON) {
                return;
            }

            switch (tokens.peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
                default:
                    break;
            }

            tokens.advance();
        }
    }

}
