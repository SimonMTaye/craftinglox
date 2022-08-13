package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;
import java.util.List;


public class BreakStatement extends Statement {

    public BreakStatement() {
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitBreakStatement(this);
    }
}
