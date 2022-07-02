package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;
import java.util.List;


public class Block extends Statement {
	public final List<Statement> stmts;

	public Block (List<Statement> stmts) {
		this.stmts = stmts;
	}

	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visitBlock(this);
	}
}
