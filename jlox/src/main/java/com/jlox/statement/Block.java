package com.jlox.statement;


import java.util.List;


public class Block extends Statement {
    public final List<Statement> stmts;

    public Block(List<Statement> stmts) {
        this.stmts = stmts;
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitBlock(this);
    }
}
