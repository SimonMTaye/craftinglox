package com.jlox.expression;


import com.jlox.scanner.Token;


public class Grouping extends Expression {
	public final Expression expr;

	public Grouping (Expression expr) {
		this.expr = expr;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitGrouping(this);
	}
}
