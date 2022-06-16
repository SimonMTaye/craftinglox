package com.jlox.parser;

public enum ParseErrorCode {
    INVALID_INDENTIFIER("ERR_BAD_INDETIFIER"),
    UNCLOSED_PAREN("ERR_UNCLOSED_PARENT"),
    NO_EXPRESSION("ERR_EXPECTED_AN_EXPRESSION"),
    UNDEFINED_VALUE("ERR_VARIABLE_NOT_FOUND"),
    EXISTING_VARIABLE("ERR_VARIABLE_DEFINED"),
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
