package com.jlox.interpreter;

public enum RuntimeErrorType {
    TYPE_ERROR("ERR_INVALID_TYPE"),
    UNEXPECTED_OPERATOR("ERR_UNEXPECTED_OPERATOR"),
    DIVIDE_BY_ZERO("ERR_DIVIDE_BY_ZERO");

    private final String code;
    RuntimeErrorType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
