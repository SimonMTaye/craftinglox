package com.jlox.expression;


import java.util.List;


public class Call extends Expression {
	public final Expression callee;
	public final List<Expression> arguments;

	public Call (Expression callee, List<Expression> arguments) {
		this.callee = callee;
		this.arguments = arguments;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitCall(this);
	}
}
