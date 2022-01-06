package com.jlox.expression;

public abstract class Expression {
	abstract <R> R accept(ExpressionVisitor<R> v);
}
