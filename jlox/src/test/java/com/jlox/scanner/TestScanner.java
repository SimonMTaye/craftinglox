package com.jlox.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.scanner.source.ISource;
import com.jlox.scanner.source.LineSource;

import org.junit.jupiter.api.Test;

public class TestScanner {

    private List<Token> runScanner(String input) {
        ISource line = new LineSource(input);
        IErrorHandler error = new ConsoleHandler();

        LoxScanner scanner = new LoxScanner(line, error);
        return scanner.scanTokens();
    }

    @Test
    void TestSingleCharScanner() {
        List<Token> tokens = runScanner(". ? : ;");
        assertEquals(5, tokens.size());

        Token token1 = new Token(TokenType.DOT, ".", null, 0);
        Token token2 = new Token(TokenType.QUESTION_MARK, "?", null, 2);
        Token token3 = new Token(TokenType.COLON, ":", null, 4);
        Token token4 = new Token(TokenType.SEMICOLON, ";", null, 6);

        assertEquals(token1, tokens.get(0));
        assertEquals(token2, tokens.get(1));
        assertEquals(token3, tokens.get(2));
        assertEquals(token4, tokens.get(3));
    }

    @Test
    void TestIdentifier() {
        List<Token> tokens = runScanner("word and orchid");
        assertEquals(4, tokens.size());

        Token token1 = new Token(TokenType.IDENTIFIER, "word", null, 3);
        Token token2 = new Token(TokenType.AND, "and", null, 7);
        Token token3 = new Token(TokenType.IDENTIFIER, "orchid", null, 14);
        assertEquals(token1, tokens.get(0));
        assertEquals(token2, tokens.get(1));
        assertEquals(token3, tokens.get(2));

    }

    @Test
    void TestStringScan() {
        List<Token> tokens = runScanner("\"Boo Yeah\"");
        // One string and one eof
        assertEquals(tokens.size(), 2);
        Token expectedToken = new Token(TokenType.STRING, "\"Boo Yeah\"", "Boo Yeah", 10);
        if (tokens.size() == 1) {
            assertEquals(expectedToken, tokens.get(0));
        }
    }

    @Test
    void TestIntScan() {
        List<Token> tokens = runScanner("123");
        assertEquals(tokens.size(), 2);
        Token expectedToken = new Token(TokenType.INTEGER, "123", 123, 2);
        assertEquals(expectedToken, tokens.get(0));

    }

    @Test
    void TestFloatFScan() {
        List<Token> tokens = runScanner("123d");
        assertEquals(tokens.size(), 2);
        Token expectedToken = new Token(TokenType.DOUBLE, "123d", 123d, 3);
        assertEquals(expectedToken, tokens.get(0));

    }

    @Test
    void TestFloatDecimalScan() {
        List<Token> tokens = runScanner("12.3");
        assertEquals(tokens.size(), 2);
        Token expectedToken = new Token(TokenType.DOUBLE, "12.3", 12.3, 3);
        assertEquals(expectedToken, tokens.get(0));

    }

    @Test
    void TestMultipleScan() {
        List<Token> tokens = runScanner("123 12.3 \"Hello World\" > >= ");
        assertEquals(6, tokens.size());
        Token token1 = new Token(TokenType.INTEGER, "123", 123, 2);
        Token token2 = new Token(TokenType.DOUBLE, "12.3", 12.3, 7);
        // Compare Literal here as well since it is important
        Token token3 = new Token(TokenType.STRING, "\"Hello World\"", "Hello World", 21);
        Token token4 = new Token(TokenType.GREATER, ">", null, 23);
        Token token5 = new Token(TokenType.GREATER_EQUAL, ">=", null, 26);

        assertEquals(token1, tokens.get(0));
        assertEquals(token2, tokens.get(1));
        assertEquals(token3, tokens.get(2));
        assertEquals(token4, tokens.get(3));
        assertEquals(token5, tokens.get(4));

    }

    @Test
    void TestMultiLineScan() {
        List<Token> tokens = runScanner("123 12.3 \"Hello World\" > >= \n;123 12.3 \"Hello World\" > >=");
        assertEquals(13, tokens.size());
        Token token1 = new Token(TokenType.INTEGER, "123", 123, 2);
        Token token2 = new Token(TokenType.DOUBLE, "12.3", 12.3, 7);
        // Compare Literal here as well since it is important
        Token token3 = new Token(TokenType.STRING, "\"Hello World\"", "Hello World", 21);
        Token token4 = new Token(TokenType.GREATER, ">", null, 23);
        Token token5 = new Token(TokenType.GREATER_EQUAL, ">=", null, 26);
        Token token6 = new Token(TokenType.NEW_LINE, "\n", null, 28);
        Token token7 = new Token(TokenType.SEMICOLON, ";", null, 29);

        //assertTrue(compareTokens(token1, tokens.get(0)));
        assertEquals(token1, tokens.get(0));
        assertEquals(token2, tokens.get(1));
        assertEquals(token3, tokens.get(2));
        assertEquals(token4, tokens.get(3));
        assertEquals(token5, tokens.get(4));
        assertEquals(token6, tokens.get(5));
        assertEquals(token7, tokens.get(6));

    }
}
