package com.jlox.parser;

public enum ParseErrorCode {
    UNCLOSED_PAREN("ERR_UNCLOSED_PARENT"),
    NO_EXPRESSION("ERR_EXPECTED_AN_EXPRESSION"),
    MISSING_COLON("ERR_EXPECTED_COLON"),
    MISSING_SEMICOLON("ERR_EXPECTED_SEMICOLON");


    ParseErrorCode(String code) {
        this.code = code;
    }
    private final String code;

    public String getCode() {
        return code;
    }
}
