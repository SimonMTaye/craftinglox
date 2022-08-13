package com.jlox.parser;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.*;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Expression parser.
 */
public class ParseExpression extends AbstractParser<Expression> {

    private final IErrorHandler errorHandler;

    public ParseExpression(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;

    }

    // Use the console error handler if none is provided
    public ParseExpression() {
        this.errorHandler = new ConsoleHandler();
    }

    /**
     * Parses and arbitrarily nested expression and returns it. In the case of a
     * syntactically incorrect sequence of
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
        Expression expr = logicalOr();
        if (check(TokenType.QUESTION_MARK)) {
            tokens.advance();
            Expression left = ternary();
            if (check(TokenType.COLON)) {
                tokens.advance();
                Expression right = ternary();
                return new Ternary(expr, left, right);
            }
            throw new ParseLoxError("Expected ':' to match '?'", tokens.previous().offset);
        }
        return expr;
    }

    // logicalOr -> logicalAnd ('or' logicalAnd) *
    private Expression logicalOr() {
        Expression left = logicalAnd();
        if (check(TokenType.OR)) {
            Token op = tokens.advance();
            Expression right = logicalAnd();
            return new Logical(left, op, right);
        }
        return left;
    }

    // logicalAnd -> equality ('and' equality) *
    private Expression logicalAnd() {
        Expression left = equality();
        if (check(TokenType.AND)) {
            Token op = tokens.advance();
            Expression right = equality();
            return new Logical(left, op, right);
        }
        return left;
    }

    // equality -> comparison ( ('!=' | '==') comparison) *
    private Expression equality() {
        return binaryHelper((Void none) -> comparison(), TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL);
    }

    // comparison -> term ( ('>' | '>=' | '<' | '<=' ) term) *
    private Expression comparison() {
        return binaryHelper((Void none) -> term(), TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS,
                TokenType.LESS_EQUAL);

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
        return call();
    }

    private Expression call() {
        Expression expr = primary();

        if (check(TokenType.LEFT_PAREN)) {
            ArrayList<Expression> args = new ArrayList<>();
            while (!check(TokenType.RIGHT_PAREN)) {
                if (args.size() > MAX_ARGS) {
                    throw new ParseLoxError("Cannot have more than 255 arguments", tokens.peek().offset);
                }
                args.add(expression());
                if (check(TokenType.RIGHT_PAREN)) {
                    break;
                }
                if (!check(TokenType.COMMA)) {
                    throw new ParseLoxError("Expected ',' or ')'", tokens.previous().offset);
                }
                // Consume comma
                tokens.advance();
            }
            return new Call(expr, args);
        }
        return expr;
    }

    // primary -> NUMBER | STRING | 'true | 'false' | 'nil' | IDENTIFIER | '('
    // expression ')'
    private Expression primary() {
        if (checkMultiple(TokenType.INTEGER, TokenType.DOUBLE, TokenType.STRING, TokenType.TRUE, TokenType.FALSE,
                TokenType.NIL)) {
            Token token = tokens.advance();
            return new Literal(token);
        }
        if (check(TokenType.IDENTIFIER)) {
            Token token = tokens.advance();
            return new Variable(token);
        }
        if (check(TokenType.LEFT_PAREN)) {
            tokens.advance();
            Expression expr = expression();
            Expression grouping = new Grouping(expr);
            if (check(TokenType.RIGHT_PAREN)) {
                tokens.advance();
                return grouping;
            }
            throw new ParseLoxError("Expected a ')'", tokens.previous().offset);
        }
        throw new ParseLoxError("Expected an expression", tokens.previous().offset);
    }

    // Helper method for matching pattern used in equality, comparison, term and
    // factor
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

}
