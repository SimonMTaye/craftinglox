package com.jlox.expression;

public interface ExpressionVisitor<R> {
    R visitUnary(Unary unary);
	R visitBinary(Binary binary);
    R visitTernary(Ternary ternary);
	R visitGrouping(Grouping grouping);
	R visitLiteral(Literal literal);
}
