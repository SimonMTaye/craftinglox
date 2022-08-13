package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;


public class VarAssign extends Statement {
    public final Token name;
    public final Expression newVal;

    public VarAssign(Token name, Expression newVal) {
        this.name = name;
        this.newVal = newVal;
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitVarAssign(this);
    }
}
