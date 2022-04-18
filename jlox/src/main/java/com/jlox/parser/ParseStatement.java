package com.jlox.parser;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.error.LoxError;
import com.jlox.expression.Expression;
import com.jlox.scanner.TokenType;
import com.jlox.statement.ExprStatement;
import com.jlox.statement.PrintStatement;
import com.jlox.statement.Statement;


public class ParseStatement extends AbstractParser<Statement> {

    private TokenSource tokens;
    private final IErrorHandler handler;
    private final ParseExpression exprParser;

    /**
     * Default constructor where console handler is used for error handling
     */
    public ParseStatement () {
        this.handler = new ConsoleHandler();
        this.exprParser = new ParseExpression(this.handler);
    }

    /**
     * Create a ParseStatement that uses the provided error handler
     * @param handler the desired error handler
     */
    public ParseStatement(IErrorHandler handler) {
        this.handler = handler;
        this.exprParser = new ParseExpression(this.handler);
    }


    /**
     * Parse a list of tokens as a single statement
     * @param tokens tokens to be parsed
     * @return a statement defined by the tokens
     */
    public Statement parse(TokenSource tokens) {
        this.tokens = tokens;
        return statement();
    }

    private Statement statement() {
        if (check(TokenType.PRINT)) {
            tokens.advance();
            return printStatement();
        }
        return expressionStatement();
    }


    private Statement printStatement() {
        Expression value = exprParser.parse(this.tokens);
        if (!check(TokenType.SEMICOLON)) {
            throw new ParseLoxError("Expected ';' after value", ParseErrorCode.MISSING_SEMICOLON,tokens.previous().offset);
        }
        return new PrintStatement(value);

    }


    private Statement expressionStatement() {
        Expression value = exprParser.parse(this.tokens);
        if (!check(TokenType.SEMICOLON)) {
            throw new ParseLoxError("Expected ';' after value", ParseErrorCode.MISSING_SEMICOLON,tokens.previous().offset);
        }
        return new ExprStatement(value);
    }
}
