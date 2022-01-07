package com.jlox.expression;

public class Ternary extends Expression {
    public final Expression condition;
    public final Expression left;
    public final Expression right;

    public Ternary(Expression condition, Expression left, Expression right) {
        this.condition = condition;
        this.left = left;
        this.right = right;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> v) {
        return v.visitTernary(this);
    }
}
