package com.jlox.expression;


import com.jlox.scanner.Token;


public class Unary extends Expression {
	private final Token operator;
	private final Expression right;

	public Unary (Token operator, Expression right) {
		this.operator = operator;
		this.right = right;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitUnary(this);
	}

    public Token getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}
