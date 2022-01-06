package com.jlox.parser;

import com.jlox.scanner.Token;

/**
 * Interface for parsing a list of tokens
 * Each implementation is responsible for parsing a specific part of the language (i.e. Expression, Statement...)
 * @param <R> The return type of the parse statement
 */
public interface IParser<R> {
    R parse(Iterable<Token> tokens);
}
