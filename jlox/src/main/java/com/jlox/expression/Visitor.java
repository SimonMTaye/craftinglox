package com.jlox.expression;

interface Visitor<R> {
	R visitBinary(Binary binary);
	R visitGrouping(Grouping grouping);
	R visitLiteral(Literal literal);
	R visitUnary(Unary unary);
}