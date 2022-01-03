package com.jlox.expression;

import com.jlox.scanner.Token;

public class Binary extends Expression {
	final Expression left;
	final Token operator;
	final Expression right;

	public Binary (Expression left, Token operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitBinary(this);
	}
}
