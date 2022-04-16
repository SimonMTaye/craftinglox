package com.jlox.statement;


interface StatementVisitor<R> {
	R visitPrintStatement(PrintStatement printstatement);
	R visitExprStatement(ExprStatement exprstatement);
}