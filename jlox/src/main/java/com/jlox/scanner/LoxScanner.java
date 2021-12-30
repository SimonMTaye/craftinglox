package com.jlox.scanner;

import java.util.ArrayList;
import java.util.List;

import com.jlox.error.IErrorReporter;
import com.jlox.scanner.source.ISource;
import com.jlox.scanner.source.ISourceInfo;

public class LoxScanner {

    private final ISource source;
    private final IErrorReporter reporter;
    private final List<Token> tokens;

    // A source with extra information for error reporting
    private final ISourceInfo sourceI;
    private final boolean hasInfo;

    public LoxScanner(ISource source, IErrorReporter reporter) {
        this.source = source;
        if (source instanceof ISourceInfo) {
            sourceI = (ISourceInfo) source;
            hasInfo = true;
        } else {
            sourceI = null;
            hasInfo = false;
        }

        this.reporter = reporter;
        tokens = new ArrayList<>();
    }

    public List<Token> scanTokens() {
        while (!source.isAtEnd())
            scanToken();
        addToken(TokenType.EOF);
        return tokens;
    }

    private void scanToken() {
        char c = source.advance();
        // Check if it is a white space character (not including '\n')
        boolean handeled = handleWhiteSpace(c);
        if (handeled) {
            return;
        }
        // Check if it is a single character token
        handeled = handleSingleChar(c);
        if (handeled) {
            return;
        }
        handeled = handleTwoChar(c);
        if (handeled) {
            return;
        }
        handeled = handleMultiChar(c);
        if (!handeled) {
            handleUnknown(c);
        }
    }

    // Check if it is a white space character (excluding '\n')
    private boolean handleWhiteSpace(char c) {
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    private boolean handleSingleChar(char c) {
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '\n':
                addToken(TokenType.NEW_LINE);
                break;
            default:
                return false;
        }
        return true;
    }

    // Handle two character tokens
    private boolean handleTwoChar(char c) {
        switch (c) {
            case '!':
                addToken(nextMatch('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(nextMatch('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '>':
                addToken(nextMatch('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '<':
                addToken(nextMatch('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            default:
                return false;
        }
        return true;
    }

    // Handle other types of tokens
    private boolean handleMultiChar(char c) {
        if (c == '/') {
            if (nextMatch('/')) {
                // Two '//' indicate a comment. Move forward till we hit the end of the line or
                // the file is over
                while (source.peek() != '\n' && source.isAtEnd())
                    source.advance();
                return true;
            }
            addToken(TokenType.SLASH);
            return true;
        }
        if (c == '"') {
            handleString();
            return true;
        }
        if (Character.isDigit(c)) {
            handleNumbers(c);
            return true;
        }
        return false;
    }

    private void handleString() {
        // 2 since we have consumed the initial ' " ' to get here and we just advanced
        // in the previous line
        int length = 1;
        char last = '\0';
        while (!source.isAtEnd() && source.peek() != '"') {
            length++;
            last = source.advance();
        }
        // If we broke out of the loop because the next character is ' " ', then advance one last time
        if (source.peek() == '"') {
            last = source.advance();
            length++;
        }
        int offset = source.getOffset();
        if (last != '"')
            reporter.error(source.getOffset(), "Unterminated string");

        String literal = source.get((offset - length) + 2, offset);

        addToken(TokenType.STRING, literal, length);
    }

    private void handleUnknown(char c) {
        if (hasInfo) {
            assert sourceI != null;
            reporter.error(source.getOffset(), String.format("Unexpected char %c at line %d col %d", c,
                    sourceI.getLineNumber(), sourceI.getColNumber()));
        }
        else
            reporter.error(source.getOffset(), String.format("Unexpected char %c", c));
    }

    private void handleNumbers(char c) {
        boolean isFloat = false;
        int length = 1;
        // Avoid repeated calls to source.peek()
        char next = source.peek();
        while (!source.isAtEnd() && (Character.isDigit(next) || next == '.')) {
            if (next == '.')
                isFloat = true;
            length++;
            source.advance();
            next = source.peek();
        }
        int offsetPlusOne = source.getOffset() + 1;
        // offsetPlusOne because offset returns a 0-indexed position
        String literal = source.get((offsetPlusOne - length), offsetPlusOne);
        if (source.peek() == 'f') {
            isFloat = true;
            source.advance();
        }
        if (isFloat)
            addToken(TokenType.FLOAT, Double.parseDouble(literal), length);
        else
            addToken(TokenType.INTEGER, Integer.parseInt(literal), length);
    }

    // Add a single/double-length token
    private void addToken(TokenType type) {
        int length;
        switch (type) {
            case BANG_EQUAL:
            case GREATER_EQUAL:
            case LESS_EQUAL:
            case EQUAL_EQUAL:
                length = 2;
                break;
            default:
                length = 1;
        }
        addToken(type, null, length);

    }

    private void addToken(TokenType type, Object literal, int length) {
        int offset = source.getOffset();
        Token token = new Token(type, source.get((offset - length) + 1, offset + 1), literal, offset);
        tokens.add(token);
    }

    /**
     * Checks if the next character matches c. If so, consumes it
     *
     * @param c the character to look for
     * @return true if the character matches what we expect
     */
    private boolean nextMatch(char c) {
        if (source.peek() == c) {
            source.advance();
            return true;
        }
        return false;
    }
}
