package com.jlox.expression;


import com.jlox.scanner.Token;


public class Variable extends Expression {
    public final Token name;

    public Variable(Token name) {
        this.name = name;
    }

    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitVariable(this);
    }
}
