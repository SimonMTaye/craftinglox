package com.jlox.scanner.source;

public class StringSource implements ISourceInfo {

    private int current = -1;
    private int line = 1;
    private int col = 1;

    private final String source;

    public StringSource(String source) {
        this.source = source;
    }

    @Override
    public char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return getString().charAt(current + 1);
    }

    @Override
    public char advance() {
        if (isAtEnd()) {
            return '\0';
        }
        current++;
        if (getString().charAt(current) == '\n') {
            line += 1;
            col = 1;
        } else {
            col += 1;
        }
        return getString().charAt(current);
    }

    @Override
    public String get(int start, int end) {
        return getString().substring(start, end);
    }

    @Override
    public boolean isAtEnd() {
        return current == (getString().length() - 1);
    }

    @Override
    public int getOffset() {
        return current;
    }

    @Override
    public int getLineNumber() {
        return line;
    }

    @Override
    public int getColNumber() {
        return col;
    }

    @Override
    public int getLineNumber(int offset) {
        String myString = getString();
        int l = myString.length();
        if (offset >= l) {
            throw new RuntimeException("Offset is invalid");
        }

        int acc = 1;
        // Count the number of times we see a new line
        for (int i = 0; i <= offset; i++) {
            acc++;
        }
        return acc;

    }

    protected String getString() {
        return source;
    }

    @Override
    public int getColNumber(int offset) {
        String myString = getString();
        int l = myString.length();
        if (offset >= l) {
            throw new RuntimeException("Offset is invalid");
        }

        myString = myString.substring(0, offset);
        // Get the position of the last new line
        int fin = myString.lastIndexOf('\n');
        // return the distance between that new line and our offset character
        return offset - fin;
    }

}
