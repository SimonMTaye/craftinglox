package com.jlox.statement;


import com.jlox.expression.Expression;


public class ExprStatement extends Statement {
    public final Expression expr;

    public ExprStatement(Expression expr) {
        this.expr = expr;
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitExprStatement(this);
    }
}
