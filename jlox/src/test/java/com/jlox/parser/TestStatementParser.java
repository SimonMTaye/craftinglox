package com.jlox.parser;

import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import com.jlox.statement.PrintStatement;
import com.jlox.statement.VarAssign;
import com.jlox.statement.VarDeclare;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestStatementParser {

    @Test
    void testVarDeclration() {
        ParseStatement parser = new ParseStatement();
        final Token[] badTokens = new Token[] { emptyToken(TokenType.VAR), emptyToken(TokenType.IDENTIFIER),
                emptyToken(TokenType.EQUAL), emptyToken(TokenType.INTEGER) };
        assertThrows(ParseLoxError.class, () -> parser.parse(List.of(badTokens)),
                "Expect error due to missing semicolon");

        final Token[] tokens = new Token[] { emptyToken(TokenType.VAR), emptyToken(TokenType.IDENTIFIER),
                emptyToken(TokenType.EQUAL), emptyToken(TokenType.INTEGER), emptyToken(TokenType.SEMICOLON) };
        assertInstanceOf(VarDeclare.class, parser.parse(List.of(tokens)), "Expected variable declaration statement");
    }

    @Test
    void testPrintStatement() {
        ParseStatement parser = new ParseStatement();
        final Token[] badTokens = new Token[] { emptyToken(TokenType.PRINT), emptyToken(TokenType.INTEGER) };
        assertThrows(ParseLoxError.class, () -> parser.parse(List.of(badTokens)),
                "Expect error due to missing semicolon");

        final Token[] tokens = new Token[] { emptyToken(TokenType.PRINT), emptyToken(TokenType.INTEGER),
                emptyToken(TokenType.SEMICOLON) };
        assertInstanceOf(PrintStatement.class, parser.parse(List.of(tokens)),
                "Expected variable declaration statement");
    }

    // Initialize a list of emptyTokens of type identifier, equal, integer
    // Call parser on tokens
    // assertThrows
    @Test
    void testVarAssign() {
        ParseStatement parser = new ParseStatement();
        final Token[] badTokens = new Token[] { emptyToken(TokenType.IDENTIFIER), emptyToken(TokenType.EQUAL),
                emptyToken(TokenType.INTEGER) };
        assertThrows(RuntimeException.class, () -> parser.parse(List.of(badTokens)),
                "Expect error due to missing semicolon");

        final Token[] tokens = new Token[] { emptyToken(TokenType.IDENTIFIER), emptyToken(TokenType.EQUAL),
                emptyToken(TokenType.INTEGER), emptyToken(TokenType.SEMICOLON) };
        assertInstanceOf(VarAssign.class, parser.parse(List.of(tokens)), "Expected variable declaration statement");

    }

    private Token emptyToken(TokenType type) {
        return new Token(type, "", null, 0);
    }

}
