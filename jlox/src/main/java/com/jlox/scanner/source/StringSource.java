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
        if (isAtEnd())
            return '\0';
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

    // TODO Make more efficent
    @Override
    public int getLineNumber(int offset) {
        String str = getString().substring(0, offset);
        String[] lines = str.split("\n");
        return lines.length;
    }

    protected String getString() {
        return source;
    }

    // TODO Make more efficient
    @Override
    public int getColNumber(int offset) {
        String str = getString().substring(0, offset);
        String[] lines = str.split("\n");
        String last = lines[lines.length - 1];
        return last.length();
    }

}
