package com.jlox.expression;


import com.jlox.scanner.Token;


public class Variable extends Expression {
	private final Token name;

	public Variable (Token name) {
		this.name = name;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitVariable(this);
	}

    public int getOffset() {
        return name.offset;
    }

    public String getName() {
        return name.lexme;
    }
}
