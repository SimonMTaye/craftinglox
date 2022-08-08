package com.jlox.parser;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.Expression;
import com.jlox.expression.Literal;
import com.jlox.expression.Variable;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import com.jlox.statement.*;

import java.util.ArrayList;
import java.util.List;

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
            ParseLoxError err = new ParseLoxError("Expected closing '}'",  tokens.previous().offset);
            checkAndAdvance(TokenType.RIGHT_PAREN, err);
            return new Block(stmts);
        }
        return declaration();
    }

    private Statement declaration() {
        Token next = tokens.peek();
        switch (next.type) {
           case VAR:
                return varDeclaration();
           case FUN:
                return funDeclaration();
           default:
                return statement();
        }
    }

    private Statement varDeclaration() {
        // Consume var token
        tokens.advance();
        Token name = checkAndAdvance(TokenType.IDENTIFIER,newError("Expected Identifier after var keyword"));
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
        // Check for semicolon
        checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after variable declaration"));
        return variable;
    }

    private Statement funDeclaration() {
        // Consume fun token
        tokens.advance();
        Token name = checkAndAdvance(TokenType.IDENTIFIER, newError("Expected identifier after function keyword"));
        checkAndAdvance(TokenType.LEFT_PAREN, newError("Expected '(' after function name"));
        List<Token> params = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (params.size() >= 255) {
                    throw new ParseLoxError("Cannot have more than 255 parameters", tokens.previous().offset);
                }
                Token param = checkAndAdvance(TokenType.IDENTIFIER, newError("Expected a valid Identifier after fun keyword"));
                params.add(param);
                // Stop iterating if next token is not comma
                if (!check(TokenType.COMMA)) break;
                tokens.advance();
            } while (true);
        }
        checkAndAdvance(TokenType.RIGHT_PAREN, newError("Expected closing ')'"));
        return new FunDeclare(name, params, (Block) block());

    }

    private Statement statement() {
        Token next = tokens.peek();
        switch (next.type) {
            case PRINT:
                return printStatement();
            case IF:
                return ifStatement();
            case WHILE:
                return whileStatement();
            case FOR:
                return forStatement();
            case RETURN:
                return returnStatement();
            case BREAK:
                tokens.advance();
                throw new ParseLoxError("break statements may only appear within a for or while loop",  next.offset);
            default:
                return expressionStatement();
        }
    }
    private Statement returnStatement() {
        tokens.advance();
        Expression value = exprParser.parse(this.tokens);
        checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after value"));
        return new ReturnStatement(value);
    }

    private Statement printStatement() {
        tokens.advance();
        Expression value = exprParser.parse(this.tokens);
        checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after value"));
        return new PrintStatement(value);
    }

    // Grammar IF Expression (Block | '\n'Statement) (ELSE Statement)?
    private Statement ifStatement() {
        tokens.advance();
        Expression condition = exprParser.parse(this.tokens);
        Statement ifBranch = breakStatement();

        Statement elseBranch = null;
        if (check(TokenType.ELSE)) {
            tokens.advance();
            elseBranch = statement();
        }
        return new IfStatement(condition, ifBranch, elseBranch);

    }

    // Grammar WHILE Expression (Block | '\n'Statement)
    private Statement whileStatement() {
        tokens.advance();
        checkAndAdvance(TokenType.LEFT_PAREN, newError("Expected '(' after while keyword"));
        Expression condition = check(TokenType.RIGHT_PAREN) ? null: exprParser.parse(this.tokens);
        checkAndAdvance(TokenType.RIGHT_PAREN, newError("Expected ')' after expression"));
        return new WhileStatement(condition, breakStatement());
    }


    // Grammar FOR (Expression)?; (Expression)? ; (Expression)? (Block | '\n'Statement)
    private Statement forStatement() {
        tokens.advance();

        checkAndAdvance(TokenType.LEFT_PAREN, newError("Expected '(' after for keyword"));

        Statement init = check(TokenType.SEMICOLON) ? null : new ExprStatement(exprParser.parse(this.tokens));
        checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after initializer"));
        tokens.advance();

        Expression condition = check(TokenType.SEMICOLON) ? null : exprParser.parse(this.tokens);
        checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after condition"));
        tokens.advance();

        Statement post = check(TokenType.SEMICOLON) ? null : new ExprStatement(exprParser.parse(this.tokens));
        checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after post statement"));
        tokens.advance();

        checkAndAdvance(TokenType.RIGHT_PAREN, newError("Expected ')' after post for statement"));

        Statement body = breakStatement();

        // Transform into a while loop
        ArrayList<Statement> whileBody = new ArrayList<>();
        whileBody.add(body);
        if (post != null) whileBody.add(post);

        WhileStatement whileStmt  = new WhileStatement(condition, new Block(whileBody));
        ArrayList<Statement> forStmts = new ArrayList<>();
        if (init != null) forStmts.add(init);
        forStmts.add(whileStmt);
        return new Block(forStmts);
    }

    /**
     * Helper function for parsing statements where break is allowed (i.e. for and while loops)
     * @return a statement
     */
    private Statement breakStatement() {
        if (check(TokenType.BREAK)) {
            tokens.advance();
            checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after break statement"));
            return new BreakStatement();
        }
        return statement();
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
                throw new ParseLoxError("Expected ';' after value",  tokens.previous().offset);
        }
    }

    private Statement assignStatement(Expression lvalue) {
        if (lvalue instanceof Variable) {
            Variable var = (Variable) lvalue;
            Expression rvalue = exprParser.parse(this.tokens);
            checkAndAdvance(TokenType.SEMICOLON, newError("Expected ';' after value"));
            return new VarAssign(var.name, rvalue);

        }
        throw new ParseLoxError("Cannot assign value to " + lvalue.toString(),  tokens.previous().offset);

    }


}
