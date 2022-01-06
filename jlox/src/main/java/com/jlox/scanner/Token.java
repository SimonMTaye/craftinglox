package com.jlox.scanner;

import java.util.Objects;

public class Token {

    public final String lexme;
    public final Object literal;
    public final TokenType type;
    public final int offset;

    public Token(TokenType type, String lexme, Object literal, int offset) {
        this.type = type;
        this.lexme = lexme;
        this.literal = literal;
        this.offset = offset;
    }

    public String toString() {
        return String.format("Type: %s\t Lexme: %s\t Offset: %d\t Literal: %s", type, lexme, offset, (literal != null) ? literal.toString(): "none");
    }

    @Override
    public boolean equals(Object oth) {
        if (!(oth instanceof Token))
            return false;

        Token otherTk = (Token) oth;
        return Objects.equals(literal, otherTk.literal)
            && type == otherTk.type
            && offset == otherTk.offset
            && lexme.equals(otherTk.lexme);
    }
}
