package com.jlox.parser;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.Expression;
import com.jlox.expression.Literal;
import com.jlox.expression.Variable;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import com.jlox.statement.*;

import javax.swing.plaf.nimbus.State;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.jlox.scanner.TokenType.FUN;
import static com.jlox.scanner.TokenType.VAR;


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
        ParseLoxError e = new ParseLoxError("Expected Identifier after var keyword",  tokens.previous().offset);
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
        e = new ParseLoxError("Expected semicolon after variable declaration",  tokens.previous().offset);
        // Check for semicolon
        checkAndAdvance(TokenType.SEMICOLON, e);

        return variable;
    }

    private Statement funDeclaration() {
        // Consume fun token
        tokens.advance();

        ParseLoxError e = new ParseLoxError("Expected Identifier after fun keyword",  tokens.previous().offset);
        Token name = checkAndAdvance(TokenType.IDENTIFIER, e);

        e = new ParseLoxError("Expected '( after fun keyword",  tokens.previous().offset);
        checkAndAdvance(TokenType.LEFT_PAREN, e);

        ParseLoxError expectedID = new ParseLoxError("Expected a valid Identifier after fun keyword",  tokens.previous().offset);
        List<Token> params = new ArrayList<>();

        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (params.size() >= 255) {
                    throw new ParseLoxError("Cannot have more than 255 parameters", tokens.previous().offset);
                }

                params.add(checkAndAdvance(TokenType.IDENTIFIER, expectedID));

                // Stop iterating if next token is not comma
                if (!check(TokenType.COMMA)) break;
                tokens.advance();
            } while (true);
        }

        ParseLoxError missingParen = new ParseLoxError("Expected closing ')'",  tokens.previous().offset);
        checkAndAdvance(TokenType.RIGHT_PAREN, missingParen);

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



    private Statement printStatement() {
        tokens.advance();
        Expression value = exprParser.parse(this.tokens);
        if (!check(TokenType.SEMICOLON)) {
            throw new ParseLoxError("Expected ';' after value", tokens.previous().offset);
        }
        return new PrintStatement(value);

    }

    // Grammar IF Expression (Block | '\n'Statement) (ELSE Statement)?
    private Statement ifStatement() {
        tokens.advance();
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
        tokens.advance();
        Expression condition = exprParser.parse(this.tokens);
        return new WhileStatement(condition, controlFlowStatement());
    }


    // Grammar FOR (Expression)?; (Expression)? ; (Expression)? (Block | '\n'Statement)
    private Statement forStatement() {
        tokens.advance();
        Statement init = forGetStatement();

        Expression condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = exprParser.parse(this.tokens);
            if (!check(TokenType.SEMICOLON)) {
                throw new ParseLoxError("Expected ';' after condition",  tokens.previous().offset);
            }
        }
        // Consume the semicolon
        tokens.advance();

        Statement post = forGetStatement();
        Statement body = controlFlowStatement();

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

    // (Block | '\n'Statement)
    private Statement controlFlowStatement() {
        // Check if the next character is a left brace or new line. If so, proceed
        if (!(check(TokenType.LEFT_BRACE) || check(TokenType.NEW_LINE))) {
            throw new ParseLoxError("Expected '{' or new line after while condition",  tokens.previous().offset);
        }

        // Consume the new line if it exists
        if (check(TokenType.NEW_LINE))
            tokens.advance();

        return breakStatement();
    }

    /**
     * Helper function for parsing statements where break is allowed (i.e. for and while loops)
     * @return a statement
     */
    private Statement breakStatement() {
        if (check(TokenType.BREAK)) {
            tokens.advance();
            ParseLoxError e = new ParseLoxError("Expected semicolon after break statement",  tokens.previous().offset);
            checkAndAdvance(TokenType.SEMICOLON, e);
            return new BreakStatement();
        }
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
                throw new ParseLoxError("Expected ';' after value",  tokens.previous().offset);
        }
    }

    private Statement assignStatement(Expression lvalue) {
        if (lvalue instanceof Variable) {
            Variable var = (Variable) lvalue;
            Expression rvalue = exprParser.parse(this.tokens);
            ParseLoxError err =  new ParseLoxError("Expected ';' after value",  tokens.previous().offset);
            checkAndAdvance(TokenType.SEMICOLON, err);
            return new VarAssign(var.name, rvalue);

        }
        throw new ParseLoxError("Cannot assign value to " + lvalue.toString(),  tokens.previous().offset);

    }

    private Statement returnStatement() {
        tokens.advance();
        Expression value = exprParser.parse(this.tokens);
        ParseLoxError err =  new ParseLoxError("Expected ';' after value",  tokens.previous().offset);
        checkAndAdvance(TokenType.SEMICOLON, err);
        return new ReturnStatement(value);
    }
}
