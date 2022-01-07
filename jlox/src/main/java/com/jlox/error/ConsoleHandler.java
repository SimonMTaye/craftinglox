package com.jlox.error;

public class ConsoleHandler implements IErrorHandler {

    private boolean hasError = false;

    @Override
    public void error(String message, String code) {
        hasError = true;
        System.out.println(message);

    }

    public void error(LoxError loxError) {
        hasError = true;
        System.out.println(loxError.getMessage());

    }

    @Override
    public boolean hasError() {
        return hasError;
    }

}
