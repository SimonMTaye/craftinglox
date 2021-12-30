package com.jlox.error;

public interface IErrorReporter {
    void error(int offset, String message);

    boolean hasError();
}
