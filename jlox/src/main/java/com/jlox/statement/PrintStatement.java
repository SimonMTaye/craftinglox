package com.jlox.statement;


import com.jlox.expression.Expression;


public class PrintStatement extends Statement {
	final Expression expr;

	public PrintStatement (Expression expr) {
		this.expr = expr;
	}

	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visitPrintStatement(this);
	}
}
