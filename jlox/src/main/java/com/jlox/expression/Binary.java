package com.jlox.expression;


import com.jlox.scanner.Token;


public class Binary extends Expression {
	private final Expression left;
	private final Token operator;
	private final Expression right;

	public Binary (Expression left, Token operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visitBinary(this);
	}

    public Expression getLeft() {
        return left;
    }

    public Token getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}
