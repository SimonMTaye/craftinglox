package com.jlox.parser;

import com.jlox.error.CollectorHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.expression.*;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestParser {
    @Test
    void TestLiteral() {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.STRING, "\"Hello World\"", "Hello World", 10));
        IParser<Expression> parser = new ParseExpression();
        Expression result = parser.parse(tokens);
        //TODO Use evaluate visitor to implement tests
        assertInstanceOf(Literal.class, result, "Expect evaluated expression to be a literal");
    }

    @Test
    void TestLiteralGrouping() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        IParser<Expression> parser = new ParseExpression(handler);
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
        IParser<Expression> parser = new ParseExpression(handler);

        tokens.add(new Token(TokenType.MINUS, "-", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "10", null, 1));

        Expression result = parser.parse(tokens);

        assertInstanceOf(Unary.class, result, "Expect evaluated expression to be a unary");

    }

    @Test
    void TestBinary() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        IParser<Expression> parser = new ParseExpression(handler);

        tokens.add(new Token(TokenType.INTEGER, "10", null, 1));
        tokens.add(new Token(TokenType.PLUS, "+", null, 1));
        tokens.add(new Token(TokenType.INTEGER, "20", null, 1));

        Expression result = parser.parse(tokens);

        assertInstanceOf(Binary.class, result, "Expect evaluated expression to be a binary");

    }

    @Test
    void TestMultiple() {
        ArrayList<Token> tokens = new ArrayList<>();
        IErrorHandler handler = new CollectorHandler();
        IParser<Expression> parser = new ParseExpression(handler);
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
}
