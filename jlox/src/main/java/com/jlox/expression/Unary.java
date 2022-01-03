package com.jlox.expression;

import com.jlox.scanner.Token;

public class Unary extends Expression {
	final Token operator;
	final Expression right;

	public Unary (Token operator, Expression right) {
		this.operator = operator;
		this.right = right;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitUnary(this);
	}
}
