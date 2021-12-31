package com.jlox.error;

public interface IErrorReporter {
    void error(String message, String code);
    boolean hasError();
}
