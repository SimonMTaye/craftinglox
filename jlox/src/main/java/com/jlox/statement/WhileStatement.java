package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;
import java.util.List;


public class WhileStatement extends Statement {
	public final Expression condition;
	public final Statement body;

	public WhileStatement (Expression condition, Statement body) {
		this.condition = condition;
		this.body = body;
	}

	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visitWhileStatement(this);
	}
}
