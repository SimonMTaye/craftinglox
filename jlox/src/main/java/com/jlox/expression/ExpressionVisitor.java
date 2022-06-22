package com.jlox.expression;


public interface ExpressionVisitor<R> {
	public R visitBinary(Binary binary);
	public R visitGrouping(Grouping grouping);
	public R visitLiteral(Literal literal);
	public R visitUnary(Unary unary);
	public R visitTernary(Ternary ternary);
	public R visitVariable(Variable variable);
}