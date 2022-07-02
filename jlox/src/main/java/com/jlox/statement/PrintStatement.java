package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;
import java.util.List;


public class PrintStatement extends Statement {
	public final Expression expr;

	public PrintStatement (Expression expr) {
		this.expr = expr;
	}

	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visitPrintStatement(this);
	}
}
