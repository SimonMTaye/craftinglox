package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;


public class VarDeclare extends Statement {
	private final Token name;

    public Token getName() {
        return name;
    }

    public Expression getInit() {
        return init;
    }

    private final Expression init;

	public VarDeclare(Token name, Expression init) {
		this.name = name;
		this.init = init;
	}



	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visitVarDeclare(this);
	}
}
