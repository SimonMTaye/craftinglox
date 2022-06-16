package com.jlox.expression;


public abstract class Expression {
	public abstract <R> R accept(ExpressionVisitor<R> v);
}
