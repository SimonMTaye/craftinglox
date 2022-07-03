package com.jlox.scanner;

public enum TokenType {
    // Single Character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, MINUS,
    PLUS, SLASH, STAR, SEMICOLON, NEW_LINE, COLON, QUESTION_MARK,
    // One or two character tokens
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,
    // Literals
    DOUBLE, INTEGER, IDENTIFIER, STRING,
    // Keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE, BREAK,

    EOF
}
