package com.jlox.parser;

import com.jlox.error.Error;
import com.jlox.error.IErrorHandler;

public class ParseError extends Error {

    public ParseError(String message, ParseErrorCode type, int offset) {
        super(message, type.getCode(), offset);
    }

}
