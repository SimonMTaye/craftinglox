package com.jlox.parser;

import com.jlox.error.LoxError;

public class ParseLoxError extends LoxError {

    public ParseLoxError(String message, ParseErrorCode type, int offset) {
        super(message, type.getCode(), offset);
    }

}
