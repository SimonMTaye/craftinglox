package com.jlox.statement;


public class BreakStatement extends Statement {

    public BreakStatement() {
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitBreakStatement(this);
    }
}
