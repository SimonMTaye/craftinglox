package com.jlox.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jlox.error.IErrorHandler;
import com.jlox.scanner.source.ISource;
import com.jlox.scanner.source.ISourceInfo;


public class LoxScanner {

    // Error Codes
    public static final String ERROR_UNTERMSTRING = "ERR_UNTERMINATED_STRING";
    public static final String ERROR_UNKOWNCHAR = "ERR_UNKNOWN_CHAR";

    private static final Map<Character, TokenType> singleCharTokens;
    private static final Map<String, TokenType> keywords;
    static {
        // Single Char List
        singleCharTokens = new HashMap<>();
        singleCharTokens.put('(', TokenType.LEFT_PAREN);
        singleCharTokens.put(')', TokenType.RIGHT_PAREN);
        singleCharTokens.put('{', TokenType.LEFT_BRACE);
        singleCharTokens.put('}', TokenType.RIGHT_BRACE);
        singleCharTokens.put('.', TokenType.DOT);
        singleCharTokens.put(',', TokenType.COMMA);
        singleCharTokens.put('-', TokenType.MINUS);
        singleCharTokens.put('+', TokenType.PLUS);
        singleCharTokens.put('*', TokenType.STAR);
        singleCharTokens.put(';', TokenType.SEMICOLON);
        singleCharTokens.put('\n', TokenType.NEW_LINE);
        singleCharTokens.put('?', TokenType.QUESTION_MARK);
        singleCharTokens.put(':', TokenType.COLON);
        // Keyword list
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("break", TokenType.BREAK);
    }


    private final ISource source;
    private final IErrorHandler reporter;
    private final List<Token> tokens;
    // A source with extra information for error reporting
    private final ISourceInfo sourceI;
    private final boolean hasInfo;


    public LoxScanner(ISource source, IErrorHandler reporter) {
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
        // Use the handleMethods to deal with the char
        // The order of the handlers matter (the first three must remain as-is and handle unkown should always
        // come last. The rest can be switched around).
        boolean handeled = handleWhiteSpace(c);
        if (handeled) {
            return;
        }
        handeled = handleSingleChar(c);
        if (handeled) {
            return;
        }
        handeled = handleTwoChar(c);
        if (handeled) {
            return;
        }
        handeled = handleSlash(c);
        if (handeled) {
            return;
        }
        handeled = handleString(c);
        if (handeled) {
            return;
        }
        handeled = handleNumbers(c);
        if (handeled) {
            return;
        }

        handeled = handleIdentifiers(c);
        if (handeled) {
            return;
        }
        // If all other handleres don't know what to do, report the char as an error
        handleUnknown(c);
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
        TokenType type = singleCharTokens.getOrDefault(c, null);
        if (type == null)
            return false;
        addToken(type);
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

    private boolean handleSlash(char c) {
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
        return false;
    }

    private boolean handleString(char c) {
        // 2 since we have consumed the initial ' " ' to get here and we just advanced
        // in the previous line
        if (c != '"')
            return false;
        int length = 1;
        char last = c;
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
            reporter.error("Unterminated string",ERROR_UNTERMSTRING);

        String literal = source.get((offset - length) + 2, offset);

        addToken(TokenType.STRING, literal, length);
        return true;
    }

    private boolean handleNumbers(char c) {

        if (!Character.isDigit(c))
            return  false;

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
        if (source.peek() == 'd') {
            isFloat = true;
            source.advance();
            length++;
        }
        if (isFloat)
            addToken(TokenType.DOUBLE, Double.parseDouble(literal), length);
        else
            addToken(TokenType.INTEGER, Integer.parseInt(literal), length);
        return true;
    }
    // Handle keywords and identifiers
    private boolean handleIdentifiers(char c) {
        if (!Character.isAlphabetic(c))
            return false;

        int length = 1;
        while (Character.isAlphabetic(source.peek()) || Character.isDigit(source.peek())) {
            source.advance();
            length++;
        }
        String identifier = source.get((source.getOffset()  - length) + 1, source.getOffset() + 1);
        TokenType type = keywords.getOrDefault(identifier, TokenType.IDENTIFIER);
        addToken(type, null, length);
        return true;
    }
    // Handle tokens that don't fit anywhere else
    private void handleUnknown(char c) {
        if (hasInfo) {
            assert sourceI != null;
            reporter.error(String.format("Unexpected char %c at line %d col %d", c,
                sourceI.getLineNumber(), sourceI.getColNumber()), ERROR_UNKOWNCHAR);
        }
        else
            reporter.error(String.format("Unexpected char %c", c), ERROR_UNKOWNCHAR);
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
