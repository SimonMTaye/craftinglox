package com.jlox.parser;

import com.jlox.scanner.Token;

import java.util.Iterator;

class TokenSource {
    private final Iterator<Token> tokenIterator;

    private Token previous = null;
    private Token next = null;

    TokenSource(Iterable<Token> tokens) {
        this.tokenIterator = tokens.iterator();
    }

    public boolean isAtEnd() {
        return next == null && !tokenIterator.hasNext();
    }

    public Token advance() {
        // If next != null, then we have consumed a token from the iterator that hasn't been returned yet
        if (next != null) {
            previous = next;
            next = null;
            return previous;
        }
        if (isAtEnd())
            throw new IllegalStateException("Cannot advance: no tokens remaining");
        previous = tokenIterator.next();
        return previous;
    }

    public Token previous() {
        if (previous == null)
            throw new IllegalStateException("previous() can't be used on first call");
        return previous;
    }

    public Token peek() {
        if (isAtEnd()) {
            throw new IllegalStateException("Cannot peek: no tokens remaining");
        }
        if (next == null) {
            next = tokenIterator.next();
        }
        return next;
    }
}

