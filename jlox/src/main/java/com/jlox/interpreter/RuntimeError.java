package com.jlox.interpreter;

import com.jlox.error.LoxError;

public class RuntimeError extends LoxError {

    private final RuntimeErrorType type;

    public RuntimeError(RuntimeErrorType type, String message) {
        super(message, type.getCode(), -1);
        this.type = type;
    }

    public RuntimeErrorType getType() {
        return type;
    }
}
