package com.jlox.interpreter;

public class BreakException extends RuntimeError {
    public BreakException() {
        super(RuntimeErrorType.BREAK_EXCEPTION, "break");
    }
}
