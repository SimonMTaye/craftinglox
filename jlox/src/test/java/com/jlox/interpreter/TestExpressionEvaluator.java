package com.jlox.interpreter;

import com.jlox.expression.*;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestExpressionEvaluator {

    ExpressionEvaluator getEvaluator(Environment scope) {
        Interpreter interpreter = new Interpreter(scope);
        return new ExpressionEvaluator(interpreter);
    }

    ExpressionEvaluator getEvaluator() {
        Interpreter interpreter = new Interpreter();
        return new ExpressionEvaluator(interpreter);
    }
    @Test
    void TestUnary() {
        Unary unary = new Unary(new Token(TokenType.BANG, "!", null, 0), new Literal(true));
        assertEquals(false, evaluate(unary), "Expected '!true' to evaluate to false");

        unary = new Unary(new Token(TokenType.MINUS, "-", null, 0), new Literal(10));
        assertEquals(-10, evaluate(unary), "Expected '-10' to evalute to -10");

        unary = new Unary(new Token(TokenType.MINUS, "-", null, 0), new Literal(10d));
        assertEquals(-10d, evaluate(unary), "Expected '-10' to evalute to -10");

        Unary bad = new Unary(new Token(TokenType.PLUS, "+", null, 0), new Literal(10d));
        assertThrows(RuntimeError.class, () -> evaluate(bad));

        Unary badTruthy = new Unary(new Token(TokenType.BANG, "!", null, 0), new Literal(10d));
        assertThrows(RuntimeError.class, () -> evaluate(badTruthy));

        Unary badNegation = new Unary(new Token(TokenType.MINUS, "-", null, 0), new Literal("Hello"));
        assertThrows(RuntimeError.class, () -> evaluate(badNegation));
    }
    @Test
    void TestBinary() {
        Binary binary = new Binary(new Literal(10), typeOnlyToken(TokenType.PLUS), new Literal(10));
        assertEquals(20, evaluate(binary));

        binary = new Binary(new Literal(10d), typeOnlyToken(TokenType.PLUS), new Literal(10));
        assertEquals(20d, evaluate(binary));

        binary = new Binary(new Literal(10d), typeOnlyToken(TokenType.STAR), new Literal(10));
        assertEquals(100d, evaluate(binary));

        binary = new Binary(new Literal(10d), typeOnlyToken(TokenType.MINUS), new Literal(10));
        assertEquals(0d, evaluate(binary));

        binary = new Binary(new Literal(10d), typeOnlyToken(TokenType.SLASH), new Literal(10));
        assertEquals(1d, evaluate(binary));

        Binary divZero = new Binary(new Literal(10d), typeOnlyToken(TokenType.SLASH), new Literal(0));
        assertThrows(RuntimeError.class, () -> evaluate(divZero));

        binary = new Binary(new Literal("Hello "), typeOnlyToken(TokenType.PLUS), new Literal("World"));
        assertEquals("Hello World", evaluate(binary));

        Binary invalidType = new Binary(new Literal(true), typeOnlyToken(TokenType.SLASH), new Literal(10));
        assertThrows(RuntimeError.class, () -> evaluate(invalidType));

        Binary comma = new Binary(new Literal(true), typeOnlyToken(TokenType.COMMA), new Literal(10));
        assertEquals(10, evaluate(comma));

        binary = new Binary(new Literal(true), typeOnlyToken(TokenType.EQUAL_EQUAL), new Literal(10));
        assertEquals(false, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.EQUAL_EQUAL), new Literal(10));
        assertEquals(true, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.BANG_EQUAL), new Literal(10));
        assertEquals(false, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.GREATER), new Literal(0));
        assertEquals(true, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.GREATER), new Literal(10));
        assertEquals(false, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.GREATER_EQUAL), new Literal(10));
        assertEquals(true, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.LESS), new Literal(0));
        assertEquals(false, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.LESS_EQUAL), new Literal(10));
        assertEquals(true, evaluate(binary));

        binary = new Binary(new Literal(10), typeOnlyToken(TokenType.LESS), new Literal(100));
        assertEquals(true, evaluate(binary));
    }
    @Test
    void TestTernary() {
        Ternary ternary = new Ternary(new Literal(true), new Literal(1), new Literal(2));
        assertEquals(1, evaluate(ternary));

        ternary = new Ternary(new Literal(false), new Literal(1), new Literal(2));
        assertEquals(2, evaluate(ternary));

        ternary = new Ternary(new Literal(true), new Ternary(new Literal(true), new Literal(1), new Literal(2)), new Literal(3));
        assertEquals(1, evaluate(ternary));

        ternary = new Ternary(new Literal(true), new Ternary(new Literal(false), new Literal(1), new Literal(2)), new Literal(3));
        assertEquals(2, evaluate(ternary));

        ternary = new Ternary(new Literal(false), new Ternary(new Literal(true), new Literal(1), new Literal(2)), new Literal(3));
        assertEquals(3, evaluate(ternary));

        Ternary invalid = new Ternary(new Literal("Hello"), new Ternary(new Literal(true), new Literal(1), new Literal(2)), new Literal(3));
        assertThrows(RuntimeError.class, () -> evaluate(invalid));
    }

    @Test
    void TestVariables() {
        Environment scope = new Environment();
        Variable test1 = new Variable(getIdentifier("test1"));
        scope.defineVariable(test1.name, true);
        ExpressionEvaluator e = getEvaluator(scope);
        assertEquals(true, e.evaluate(test1));
    }

    Object evaluate(Expression expr) {
        ExpressionEvaluator e = getEvaluator();
        return e.evaluate(expr);
    }

    private Token typeOnlyToken(TokenType type) {
        return new Token(type, "", null, 0);
    }

    private Token getIdentifier(String name) {
        return new Token(TokenType.IDENTIFIER, name, name, 0);
    }
}
