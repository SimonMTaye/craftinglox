package com.jlox.expression;


public interface ExpressionVisitor<R> {
	R visitBinary(Binary binary);
	R visitGrouping(Grouping grouping);
	R visitLiteral(Literal literal);
	R visitUnary(Unary unary);
	R visitVariable(Variable variable);
    R visitTernary(Ternary ternary);
}
