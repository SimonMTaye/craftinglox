package com.jlox.parser;

public enum ParseErrorCode {
    UNCLOSED_PAREN("ERR_UNCLOSED_PARENT"),
    NO_EXPRESSION("ERR_EXPECTED_AN_EXPRESSION");


    ParseErrorCode(String code) {
        this.code = code;
    }
    private final String code;

    public String getCode() {
        return code;
    }
}
