package com.jlox.interpreter;

public class BreakException extends RuntimeError {
    public BreakException() {
        super("break");
    }
}
