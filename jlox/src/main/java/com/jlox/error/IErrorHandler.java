package com.jlox.error;

public interface IErrorHandler {
    void error(String message, String code);
    void error(Error error);
    boolean hasError();
}
