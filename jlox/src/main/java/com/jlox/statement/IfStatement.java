package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;
import java.util.List;


public class IfStatement extends Statement {
    public final Expression condition;
    public final Statement thenBranch;
    public final Statement elseBranch;

    public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitIfStatement(this);
    }
}
