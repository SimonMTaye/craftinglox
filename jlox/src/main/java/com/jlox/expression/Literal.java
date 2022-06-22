package com.jlox.expression;


import com.jlox.scanner.Token;


public class Literal extends Expression {
	public final Object value;

	public Literal (Object value) {
		this.value = value;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitLiteral(this);
	}
}
