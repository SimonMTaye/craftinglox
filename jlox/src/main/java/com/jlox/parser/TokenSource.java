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
        if (next != null)
            return false;
        return !tokenIterator.hasNext();
    }

    public Token advance() {
        // We have already moved the iterator forward by using peek. Thus, we shouldn't do
        // so again. Instead we should use the token stored in next
        if (next != null) {
            // Set the previous to the token stored in next
            previous = next;
            // Set next to null, indicating it has been consumed
            next = null;
            // return previous which now contains the token previously stored in next
            return previous;
        }
        // We have no token in next meaning we should advance the iterator
        // We should store the result in previous in case we call previous()
        // and need the token later
        if (isAtEnd())
            throw new IllegalStateException("No tokens remaining");
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
            throw new IllegalStateException("No tokens remaining");
        }
        if (next == null) {
            next = tokenIterator.next();
        }
        return next;
    }
}

