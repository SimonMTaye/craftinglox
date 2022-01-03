package com.jlox.expression;

public class Grouping extends Expression {
	final Expression expr;

	public Grouping (Expression expr) {
		this.expr = expr;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitGrouping(this);
	}
}
