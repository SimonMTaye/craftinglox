package com.jlox.statement;


import com.jlox.expression.Expression;
import com.jlox.scanner.Token;
import java.util.List;


public class FunDeclare extends Statement {
    public final Token name;
    public final List<Token> params;
    public final Block body;

    public FunDeclare(Token name, List<Token> params, Block body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitFunDeclare(this);
    }
}
