package com.jlox.error;

import java.util.ArrayList;

/**
 * Collects all the errors it recieves
 */
public class CollectorHandler implements IErrorHandler {

    private final ArrayList<Error> reportedErrors = new ArrayList<>();

    @Override
    public void error(String message, String code) {
        Error err = new Error(message, code, -1);
        reportedErrors.add(err);

    }

    @Override
    public void error(Error error) {
        reportedErrors.add(error);
    }

    @Override
    public boolean hasError() {
        return !(reportedErrors.size() == 0);
    }
}
