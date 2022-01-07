package com.jlox.error;


public class LoxError extends RuntimeException {
    private final String message;
    private final String code;
    private final int offset;

    public LoxError(String message, String code, int offset) {
        this.code = code;
        this.message = message;
        this.offset = offset;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getOffset() {
        return offset;
    }
}
