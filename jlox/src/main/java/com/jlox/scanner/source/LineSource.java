package com.jlox.scanner.source;

public class LineSource extends StringSource {

    private String line;

    public LineSource(String line) {
        super(line);
        this.line = line;
    }

    public void changeLine(String line) {
        this.line = line;
    }

    @Override
    protected String getString() {
        return this.line;
    }

    @Override
    public int getLineNumber() {
        return 1;
    }

    @Override
    public int getLineNumber(int offset) {
        return 1;
    }

    @Override
    public int getColNumber() {
        return getOffset();
    }

    @Override
    public int getColNumber(int offset) {
        return offset + 1;
    }

}
