package com.jlox.parser;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.*;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;

import java.util.function.Function;

/**
 * Expression parser
 */
public class ParseExpression implements IParser<Expression, Token> {

    private final IErrorHandler errorHandler;
    private TokenSource tokens;

    public ParseExpression(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;

    }

    // Use the console error handler if none is provided
    public ParseExpression() {
        this.errorHandler = new ConsoleHandler();
    }


    /**
     * Parses and arbitrarily nested expression and returns it. In the case of a syntactically incorrect sequence of
     * tokens, null is returned and the error is reported to the error handler
     *
     * @param tokens the sequence of tokens to parse
     */
    @Override
    public Expression parse(Iterable<Token> tokens) {
        this.tokens = new TokenSource(tokens);
        return parse();
    }

    public Expression parse(TokenSource tokens) {
        this.tokens = tokens;
        return parse();
    }

    private Expression parse() {
        try {
            return expression();
        } catch (ParseLoxError error) {
            errorHandler.error(error);
            return null;
        }
    }

    private Expression expression() {
        return comma();
    }

    // comma -> ternary (',' ternary) *
    private Expression comma() {
        return binaryHelper((Void none) -> ternary(), TokenType.COMMA);
    }
    // ternary -> equality ? ternary : ternary | equality
    private Expression ternary() {
        Expression expr = equality();
        if (check(TokenType.QUESTION_MARK)) {
            tokens.advance();
            Expression left = ternary();
            if (check(TokenType.COLON)) {
                tokens.advance();
                Expression right = ternary();
                return new Ternary(expr, left, right);
            }
            throw  new ParseLoxError("Expected ':' to match '?'", ParseErrorCode.MISSING_COLON, tokens.previous().offset);
        }
        return expr;
    }
    // equality -> comparison ( ('!=' | '==') comparison) *
    private Expression equality() {
        return binaryHelper((Void none) -> comparison(), TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL);
    }
    // comparison -> term ( ('>' | '>=' | '<' | '<=' ) term) *
    private Expression comparison() {
        return binaryHelper((Void none) -> term(), TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);

    }
    // term -> factor ( ('-' | '+') factor ) *
    private Expression term() {
        return binaryHelper((Void none) -> factor(), TokenType.MINUS, TokenType.PLUS);
    }
    // factor -> unary ( ('/' | '*') unary ) *
    private Expression factor() {
        return binaryHelper((Void none) -> unary(), TokenType.SLASH, TokenType.STAR);
    }
    // unary -> ('!' | '-') unary | primary
    private Expression unary() {
        if (checkMultiple(TokenType.BANG, TokenType.MINUS)) {
            Token operator = tokens.advance();
            return new Unary(operator, unary());
        }
        return primary();
    }
    // primary -> NUMBER | STRING | 'true | 'false' | 'nil' | '(' expression ')'
    private Expression primary() {
        if (checkMultiple(TokenType.INTEGER, TokenType.DOUBLE, TokenType.STRING, TokenType.TRUE, TokenType.FALSE, TokenType.NIL)) {
            Token token = tokens.advance();
            return new Literal(token);
        }
        if (check(TokenType.LEFT_PAREN)) {
            tokens.advance();
            Expression expr = expression();
            Expression grouping = new Grouping(expr);
            if (check(TokenType.RIGHT_PAREN)) {
                tokens.advance();
                return grouping;
            }
            throw new ParseLoxError("Expected a ')'", ParseErrorCode.UNCLOSED_PAREN, tokens.previous().offset);
        }
        throw new ParseLoxError("Expected an expression", ParseErrorCode.NO_EXPRESSION, tokens.previous().offset);
    }

    // Helper method for matching pattern used in equality, comparison, term and factor
    private Expression binaryHelper(Function<Void, Expression> operand, TokenType... operators) {
        Expression expr = operand.apply(null);
        // Get the next token if it matches what we want
        while (checkMultiple(operators)) {
            Token operator = tokens.advance();
            // Get the next expression for our binary expression
            Expression right = operand.apply(null);
            expr = new Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Check if the next token matches one of the provided types
     */
    private boolean checkMultiple(TokenType... types) {
        for (TokenType type : types) {
            if (check(type))
                return true;
        }
        return false;
    }

    /**
     * Check if the next token matches the desired type
     */
    private boolean check(TokenType type) {
        if (tokens.isAtEnd()) {
            return false;
        }
        return tokens.peek().type == type;
    }
}
