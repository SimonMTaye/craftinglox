package com.jlox.parser;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.Expression;
import com.jlox.expression.Literal;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import com.jlox.statement.ExprStatement;
import com.jlox.statement.PrintStatement;
import com.jlox.statement.Statement;
import com.jlox.statement.VarDeclare;

import static com.jlox.parser.ParseErrorCode.INVALID_INDENTIFIER;


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
        return declaration();
    }

    private Statement declaration() {
        if (check(TokenType.VAR))
            return varDeclaration();
        return statement();
    }

    private Statement varDeclaration() {
        // Consume var token
        tokens.advance();
        ParseLoxError e = new ParseLoxError("Expected Identifier after var keyword", INVALID_INDENTIFIER, tokens.previous().offset);
        Token name = checkAndAdvance(TokenType.IDENTIFIER, e);
        VarDeclare variable;
        // Initialize variable
        if (check(TokenType.EQUAL)) {
            // Consume the equal
            tokens.advance();
            Expression init = exprParser.parse(this.tokens);
            variable = new VarDeclare(name, init);

        } else {
            // If there's no initializer, create  a null variable
            variable = new VarDeclare(name, new Literal(TokenType.NIL));
        }
        e = new ParseLoxError("Expected semicolon after variable declaration", ParseErrorCode.MISSING_SEMICOLON, tokens.previous().offset);
        // Check for semicolon
        checkAndAdvance(TokenType.SEMICOLON, e);

        return variable;
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
