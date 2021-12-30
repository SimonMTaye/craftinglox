package com.jlox.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.jlox.error.ConsoleReporter;
import com.jlox.error.IErrorReporter;
import com.jlox.scanner.source.ISource;
import com.jlox.scanner.source.LineSource;

import com.jlox.scanner.source.StringSource;
import org.junit.jupiter.api.Test;

public class TestScanner {

    @Test
    void TestStringScan() {
        ISource line = new LineSource("\"Boo Yeah\"");
        IErrorReporter error = new ConsoleReporter();

        LoxScanner scanner = new LoxScanner(line, error);
        List<Token> tokens = scanner.scanTokens();
        // One string and one eof
        assertEquals(tokens.size(), 2);
        Token expectedToken = new Token(TokenType.STRING, "\"Boo Yeah\"", "Boo Yeah", 10);
        if (tokens.size() == 1) {
            assertEquals(expectedToken, tokens.get(0));
        }
    }

        @Test
        void TestIntScan () {
            ISource line = new LineSource("123");
            IErrorReporter error = new ConsoleReporter();

            LoxScanner scanner = new LoxScanner(line, error);
            List<Token> tokens = scanner.scanTokens();
            assertEquals(tokens.size(), 2);
            Token expectedToken = new Token(TokenType.INTEGER, "123", 123, 4);
            if (tokens.size() == 1) {
                assertEquals( expectedToken, tokens.get(0));
            }

        }

        @Test
        void TestFloatFScan () {
            ISource line = new LineSource("123f");
            IErrorReporter error = new ConsoleReporter();

            LoxScanner scanner = new LoxScanner(line, error);
            List<Token> tokens = scanner.scanTokens();
            assertEquals(tokens.size(), 2);
            Token expectedToken = new Token(TokenType.FLOAT, "123f", 123f, 5);
            if (tokens.size() == 1) {
                assertEquals( expectedToken, tokens.get(0));
            }

        }

        @Test
        void TestFloatDecimalScan () {
            ISource line = new LineSource("12.3");
            IErrorReporter error = new ConsoleReporter();

            LoxScanner scanner = new LoxScanner(line, error);
            List<Token> tokens = scanner.scanTokens();
            assertEquals(tokens.size(), 2);
            Token expectedToken = new Token(TokenType.FLOAT, "12.3", 12.3, 5);
            if (tokens.size() == 1) {
                assertEquals( expectedToken, tokens.get(0));
            }

        }

        @Test
        void TestMultipleScan () {
            ISource line = new LineSource("123 12.3 \"Hello World\" > >= ");
            IErrorReporter error = new ConsoleReporter();

            LoxScanner scanner = new LoxScanner(line, error);
            List<Token> tokens = scanner.scanTokens();
            assertEquals(tokens.size(), 6);
            Token token1 = new Token(TokenType.INTEGER, "123", 123, 2);
            Token token2 = new Token(TokenType.FLOAT, "12.3", 12.3, 7);
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
        void TestMultiLineScan () {
            ISource line = new StringSource("123 12.3 \"Hello World\" > >= \n;123 12.3 \"Hello World\" > >=");
            IErrorReporter error = new ConsoleReporter();

            LoxScanner scanner = new LoxScanner(line, error);
            List<Token> tokens = scanner.scanTokens();
            assertEquals(tokens.size(), 13);
            Token token1 = new Token(TokenType.INTEGER, "123", 123, 2);
            Token token2 = new Token(TokenType.FLOAT, "12.3", 12.3, 7);
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
