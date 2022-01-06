package com.jlox.parser;

import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestTokenSource {

    @Test
    void TestAdvancePreviousPeek() {

        ArrayList<Token> tokens = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Token newToken = new Token(TokenType.BANG, "", null, i);
            tokens.add(newToken);
        }

        TokenSource source = new TokenSource(tokens);

        assertEquals(0, source.advance().offset);
        assertEquals(0, source.previous().offset);
        assertEquals(0, source.previous().offset);

        assertEquals(1, source.peek().offset);
        assertEquals(1, source.advance().offset);
        assertEquals(1, source.previous().offset);

        assertEquals(2, source.peek().offset);
        assertEquals(1, source.previous().offset);
        assertEquals(2, source.advance().offset);


    }

    @Test
    void TestPreviousException() {

        ArrayList<Token> tokens = new ArrayList<>();
        TokenSource source = new TokenSource(tokens);

        assertThrows(IllegalStateException.class, source::previous);

    }

    @Test
    void TestPeekOnFinalException() {
        ArrayList<Token> tokens = new ArrayList<>();
        TokenSource source = new TokenSource(tokens);

        assertThrows(IllegalStateException.class, source::peek);
        assertThrows(IllegalStateException.class, source::advance);
    }

}
