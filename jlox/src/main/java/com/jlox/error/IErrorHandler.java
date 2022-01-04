package com.jlox.error;

public interface IErrorHandler {
    void error(String message, String code);
    boolean hasError();
}
