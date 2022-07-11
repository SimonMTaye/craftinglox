package com.jlox.interpreter;

import com.jlox.error.LoxError;

public class RuntimeError extends LoxError {

    public RuntimeError(String message) {
        super(message, "RUNTIME_ERROR", -1);
    }

}
