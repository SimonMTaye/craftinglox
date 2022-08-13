package com.jlox.scanner;

import java.util.Objects;

public class Token {

    public final String lexeme;
    public final Object literal;
    public final TokenType type;
    public final int offset;

    public Token(TokenType type, String lexeme, Object literal, int offset) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.offset = offset;
    }

    public String toString() {
        return String.format("Type: %s\t Lexme: %s\t Offset: %d\t Literal: %s", type, lexeme, offset, (literal != null) ? literal.toString() : "none");
    }

    @Override
    public boolean equals(Object oth) {
        if (!(oth instanceof Token)) {
            return false;
        }

        Token otherTk = (Token) oth;
        return Objects.equals(literal, otherTk.literal)
                && type == otherTk.type
                && offset == otherTk.offset
                && lexeme.equals(otherTk.lexeme);
    }
}
