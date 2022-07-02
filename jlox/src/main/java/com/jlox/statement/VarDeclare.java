package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;
import java.util.List;


public class VarDeclare extends Statement {
	public final Token name;
	public final Expression init;

	public VarDeclare (Token name, Expression init) {
		this.name = name;
		this.init = init;
	}

	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visitVarDeclare(this);
	}
}
