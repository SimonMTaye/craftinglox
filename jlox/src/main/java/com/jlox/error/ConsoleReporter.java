package com.jlox.error;

public class ConsoleReporter implements IErrorReporter {

    private boolean hasError = false;

    @Override
    public void error(int offset, String message) {
        hasError = true;
        System.out.println(message);

    }

    @Override
    public boolean hasError() {
        return hasError;
    }

}
