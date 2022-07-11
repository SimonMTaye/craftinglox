package com.jlox.expression;


import com.jlox.scanner.Token;
import java.util.List;


public class Ternary extends Expression {
	public final Expression condition;
	public final Expression left;
	public final Expression right;

	public Ternary (Expression condition, Expression left, Expression right) {
		this.condition = condition;
		this.left = left;
		this.right = right;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitTernary(this);
	}
}
