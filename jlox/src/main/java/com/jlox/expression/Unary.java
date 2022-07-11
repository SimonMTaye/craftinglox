package com.jlox.expression;


import com.jlox.scanner.Token;
import java.util.List;


public class Unary extends Expression {
	public final Token operator;
	public final Expression right;

	public Unary (Token operator, Expression right) {
		this.operator = operator;
		this.right = right;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitUnary(this);
	}
}
