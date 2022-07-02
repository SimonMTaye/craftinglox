package com.jlox.parser;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.Expression;
import com.jlox.expression.Literal;
import com.jlox.expression.Variable;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import com.jlox.statement.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.jlox.parser.ParseErrorCode.INVALID_INDENTIFIER;


public class ParseStatement extends AbstractParser<Statement> {

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
     * Parse tokens continuously until token stream is empty
     * @param tokens token iterable
     * @return List of statements
     */
    public ArrayList<Statement> parseAll(Iterable<Token> tokens) {
        TokenSource tokensSource = new TokenSource(tokens);
        return parseAll(tokensSource);
    }

    /**
     * Parse tokens continuously until token stream is empty
     * @param tokens token source
     * @return List of statements
     */
    public ArrayList<Statement> parseAll(TokenSource tokens) {
        this.tokens = tokens;
        ArrayList<Statement> stmts = new ArrayList<>();
        while (!this.tokens.isAtEnd()) {
            stmts.add(parse());
        }
        return stmts;
    }


    /**
     * Parse an iterable stream of tokens
     * @param tokenIterable iterable tokens
     * @return Statement
     */
    public Statement parse(Iterable<Token> tokenIterable) {
        TokenSource tokens = new TokenSource(tokenIterable);
        return parse(tokens);
    }
    /**
     * Parse a list of tokens as a single statement
     * @param tokens tokens to be parsed
     * @return a statement defined by the tokens
     */
    public Statement parse(TokenSource tokens) {
        this.tokens = tokens;
        return parse();
    }

    private Statement parse() {
        return block();
    }

    private Statement block() {
        if (check(TokenType.LEFT_BRACE)) {
            List<Statement> stmts = new ArrayList<>();
            while (!check(TokenType.RIGHT_PAREN) && !tokens.isAtEnd()) {
                stmts.add(parse());
            }
            ParseLoxError err = new ParseLoxError("Expected closing '}'", ParseErrorCode.UNCLOSED_BRACE, tokens.previous().offset);
            checkAndAdvance(TokenType.RIGHT_PAREN, err);
            return new Block(stmts);
        }
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
        Token next = tokens.advance();
        switch (next.type) {
            case PRINT:
                return printStatement();
            case IF:
                return ifStatement();
            case WHILE:
                return whileStatement();
            case FOR:
                return forStatement();
            default:
                return expressionStatement();
        }
    }


    private Statement printStatement() {
        Expression value = exprParser.parse(this.tokens);
        if (!check(TokenType.SEMICOLON)) {
            throw new ParseLoxError("Expected ';' after value", ParseErrorCode.MISSING_SEMICOLON,tokens.previous().offset);
        }
        return new PrintStatement(value);

    }

    // Grammar IF Expression (Block | '\n'Statement) (ELSE Statement)?
    private Statement ifStatement() {
        Expression condition = exprParser.parse(this.tokens);
        Statement ifBranch = controlFlowStatement();

        Statement elseBranch = null;
        if (check(TokenType.ELSE)) {
            tokens.advance();
            elseBranch = statement();
        }
        return new IfStatement(condition, ifBranch, elseBranch);

    }

    // Grammar WHILE Expression (Block | '\n'Statement)
    private Statement whileStatement() {
        Expression condition = exprParser.parse(this.tokens);
        return new WhileStatement(condition, controlFlowStatement());
    }


    // Grammar FOR (Expression)?; (Expression)? ; (Expression)? (Block | '\n'Statement)
    private Statement forStatement() {
        Statement init = forGetStatement();

        Expression condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = exprParser.parse(this.tokens);
            if (!check(TokenType.SEMICOLON)) {
                throw new ParseLoxError("Expected ';' after condition", ParseErrorCode.MISSING_SEMICOLON, tokens.previous().offset);
            }
        }
        // Consume the semicolon
        tokens.advance();

        Statement post = forGetStatement();
        Statement body = controlFlowStatement();

        // Transform into a while loop
        ArrayList<Statement> whileBody = new ArrayList<>();
        whileBody.add(body);
        whileBody.add(post);

        WhileStatement whileStmt  = new WhileStatement(condition, new Block(whileBody));
        ArrayList<Statement> forStmts = new ArrayList<>();
        forStmts.add(init);
        forStmts.add(whileStmt);
        return new Block(forStmts);
    }

    // (Block | '\n'Statement)
    private Statement controlFlowStatement() {
        // Check if the next character is a left brace or new line. If so, proceed
        if (!(check(TokenType.LEFT_BRACE) || check(TokenType.NEW_LINE))) {
            throw new ParseLoxError("Expected '{' or new line after while condition", ParseErrorCode.MISSING_LEFT_BRACE_OR_NL, tokens.previous().offset);
        }

        // Consume the new line if it exists
        if (check(TokenType.NEW_LINE))
            tokens.advance();

        return statement();
    }

    /**
     * Helper function for parsing for statement syntax. Parses (statement | ';')
     * @return statement or null
     */
    private Statement forGetStatement() {
        Statement stmt = null;
        if (!check(TokenType.SEMICOLON)) {
            stmt = statement();
        }
        return stmt;
    }


    private Statement expressionStatement() {
        Expression value = exprParser.parse(this.tokens);
        Token next = tokens.peek();
        switch (next.type) {
            case SEMICOLON:
                tokens.advance();
                return new ExprStatement(value);
            case EQUAL:
                tokens.advance();
                return assignStatement(value);
            default:
                throw new ParseLoxError("Expected ';' after value", ParseErrorCode.MISSING_SEMICOLON, tokens.previous().offset);
        }
    }

    private Statement assignStatement(Expression lvalue) {
        if (lvalue instanceof Variable) {
            Variable var = (Variable) lvalue;
            Expression rvalue = exprParser.parse(this.tokens);
            ParseLoxError err =  new ParseLoxError("Expected ';' after value", ParseErrorCode.MISSING_SEMICOLON, tokens.previous().offset);
            checkAndAdvance(TokenType.SEMICOLON, err);
            return new VarAssign(var.name, rvalue);

        }
        throw new ParseLoxError("Cannot assign value to " + lvalue.toString(), ParseErrorCode.INVALID_LVALUE, tokens.previous().offset);

    }
}
