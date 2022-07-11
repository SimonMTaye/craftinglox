package com.jlox.parser;

import com.jlox.error.LoxError;

public class ParseLoxError extends LoxError {

    public ParseLoxError(String message, int offset) {
        super(message, "PARSE_ERROR", offset);
    }

}
