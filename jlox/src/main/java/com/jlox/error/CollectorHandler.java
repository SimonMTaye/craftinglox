package com.jlox.error;

import java.util.ArrayList;

/**
 * Collects all the errors it recieves.
 */
public class CollectorHandler implements IErrorHandler {

    private final ArrayList<LoxError> reportedLoxErrors = new ArrayList<>();

    @Override
    public void error(String message, String code) {
        LoxError err = new LoxError(message, code, -1);
        reportedLoxErrors.add(err);

    }

    @Override
    public void error(LoxError loxError) {
        reportedLoxErrors.add(loxError);
    }

    @Override
    public boolean hasError() {
        return !reportedLoxErrors.isEmpty();
    }
}
