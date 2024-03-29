package com.jlox.statement;


import com.jlox.expression.Expression;


public class ReturnStatement extends Statement {
    public final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitReturnStatement(this);
    }
}
