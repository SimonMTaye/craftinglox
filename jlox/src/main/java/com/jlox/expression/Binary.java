package com.jlox.expression;


import com.jlox.scanner.Token;


public class Binary extends Expression {
	public final Expression left;
	public final Token operator;
	public final Expression right;

	public Binary (Expression left, Token operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitBinary(this);
	}
}
