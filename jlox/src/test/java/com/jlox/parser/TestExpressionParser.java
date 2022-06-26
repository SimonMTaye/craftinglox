package com.jlox.parser;

import com.jlox.error.CollectorHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.*;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestExpressionParser {
    @Test
    void TestLiteral() {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.STRING, "\"Hello World\"", "Hello World", 10));
        ParseExpression parser = new ParseExpression();
        Expression result = parser.parse(tokens);
        assertInstanceOf(Literal.class, result, "Expect evaluated expression to be a literal");
    }

    @Test
    void TestLiteralGrouping() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);
        tokens.add(new Token(TokenType.LEFT_PAREN, "(", null, 1));
        tokens.add(new Token(TokenType.STRING, "\"Hello World\"", "Hello World", 10));
        // Test that unclosed grouping throws an exception
        Expression result = parser.parse(tokens);
        assertNull(result, "Expect parse to return null");
        assertTrue(handler.hasError());
        // Add the right paren, making the expression syntactically correct and try parsing again
        tokens.add(new Token(TokenType.RIGHT_PAREN, ")", null, 1));
        result = parser.parse(tokens);
        assertInstanceOf(Grouping.class, result);
        Grouping res = (Grouping)  result;
        assertInstanceOf(Literal.class, res.expr);
    }

    @Test
    void TestUnary() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);

        tokens.add(new Token(TokenType.MINUS, "-", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "10", null, 1));

        Expression result = parser.parse(tokens);

        assertInstanceOf(Unary.class, result, "Expect evaluated expression to be a unary");

    }

    @Test
    void TestTerm() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);

        tokens.add(new Token(TokenType.INTEGER, "10", null, 1));
        tokens.add(new Token(TokenType.PLUS, "+", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "20", null, 1));

        Expression result = parser.parse(tokens);

        assertInstanceOf(Binary.class, result, "Expect evaluated expression to be a binary");

    }

    // Tests factor, grouping, term and equality
    @Test
    void TestMultiple() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);
        // 10 == (1 + 15 / 5)
        tokens.add(new Token(TokenType.INTEGER, "10", null, 1));
        tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", null, 1));
        tokens.add(new Token(TokenType.LEFT_PAREN, "(", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "1", null, 1));
        tokens.add(new Token(TokenType.PLUS, "+", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "15", null, 1));
        tokens.add(new Token(TokenType.SLASH, "/", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "5", null, 1));
        tokens.add(new Token(TokenType.RIGHT_PAREN, "(", null, 1));

        Expression result = parser.parse(tokens);

        assertInstanceOf(Binary.class, result, "Expect evaluated expression to be a binary");
        assertInstanceOf(Grouping.class, ((Binary) result).right);

        Grouping grouping = (Grouping) ((Binary) result).right;

        assertInstanceOf(Binary.class, grouping.expr);

        // This should contain 1 + (15 / 5)
        Binary binary = (Binary) grouping.expr;
        assertInstanceOf(Literal.class, binary.left);
        assertEquals(TokenType.PLUS, binary.operator.type);
        assertInstanceOf(Binary.class, binary.right);
        assertEquals(TokenType.SLASH, ((Binary) binary.right).operator.type);
    }

    @Test
    void TestTernary() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);
        // Simple ternary expression
        tokens.add(new Token(TokenType.FALSE, "false", null, 1));
        tokens.add(new Token(TokenType.QUESTION_MARK, "?", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "10", null, 1));
        tokens.add(new Token(TokenType.COLON, ":", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "1", null, 1));

        Expression result = parser.parse(tokens);

        assertInstanceOf(Ternary.class, result, "Expect evaluated expression to be a ternary");
        assertInstanceOf(Literal.class, ((Ternary) result).condition);
        assertInstanceOf(Literal.class, ((Ternary) result).left);
        assertInstanceOf(Literal.class, ((Ternary) result).right);
    }

    @Test
    void TestNestedTernary() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);
        // false ? (false ? (false ? 1 : 2) : 3) : (true ? 4 : 5)
        tokens.add(new Token(TokenType.FALSE, "false", null, 1));
        tokens.add(new Token(TokenType.QUESTION_MARK, "?", null, 1));

        tokens.add(new Token(TokenType.FALSE, "false", null, 1));
        tokens.add(new Token(TokenType.QUESTION_MARK, "?", null, 1));

        tokens.add(new Token(TokenType.FALSE, "false", null, 1));
        tokens.add(new Token(TokenType.QUESTION_MARK, "?", null, 1));

        tokens.add(new Token(TokenType.INTEGER, "1", null, 1));
        tokens.add(new Token(TokenType.COLON, ":", null, 1));

        tokens.add(new Token(TokenType.INTEGER, "2", null, 1));
        tokens.add(new Token(TokenType.COLON, ":", null, 1));

        tokens.add(new Token(TokenType.INTEGER, "3", null, 1));
        tokens.add(new Token(TokenType.COLON, ":", null, 1));

        tokens.add(new Token(TokenType.FALSE, "true", null, 1));
        tokens.add(new Token(TokenType.QUESTION_MARK, "?", null, 1));

        tokens.add(new Token(TokenType.INTEGER, "4", null, 1));
        tokens.add(new Token(TokenType.COLON, ":", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "5", null, 1));

        Expression result = parser.parse(tokens);
        assertInstanceOf(Ternary.class, result, "Expect evaluated expression to be a ternary");

        Ternary first = (Ternary) result;
        assertInstanceOf(Literal.class, first.condition, "Expect evaluated expression to be a ternary");
        assertInstanceOf(Ternary.class, first.left, "Expect evaluated expression to be a ternary");
        assertInstanceOf(Ternary.class, first.right, "Expect evaluated expression to be a ternary");

        Ternary right = (Ternary) first.right;
        assertInstanceOf(Literal.class, right.left, "Expect right.left expression to be a literal");
        assertInstanceOf(Literal.class, right.right, "Expect left.right expression to be a literal");

        Ternary left = (Ternary) first.left;
        assertInstanceOf(Literal.class, left.right, "Expect left.right expression to be a literal");
        assertInstanceOf(Ternary.class, left.left, "Expect left.right expression to be a literal");
        // End of checks. While this is not exhaustive,
        // it is unlikely that there will be errors we haven't caught already
    }

    @Test
    void TestComma() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);
        // 1,2
        tokens.add(new Token(TokenType.INTEGER, "1", null, 1));
        tokens.add(new Token(TokenType.COMMA, ",", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "2", null, 1));

        Expression result = parser.parse(tokens);

        assertInstanceOf(Binary.class, result, "Expect evaluated expression to be a binary");
        assertEquals(TokenType.COMMA, ((Binary) result).operator.type);
    }

    @Test
    void TestComparison() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        ParseExpression parser = new ParseExpression(handler);

        tokens.add(new Token(TokenType.INTEGER, "10", 10, 0));
        tokens.add(new Token(TokenType.GREATER, ">", 5, 0));
        tokens.add(new Token(TokenType.INTEGER, "5", 5, 0));

        Expression result = parser.parse(tokens);
        assertInstanceOf(Binary.class, result);
        assertEquals(TokenType.GREATER, ((Binary) result).operator.type);


    }
}
