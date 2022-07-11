package com.jlox.interpreter;


import com.jlox.statement.*;

public class StatementEvaluator implements StatementVisitor<Void> {

    protected Environment scope;
    private final ExpressionEvaluator exprEval;



    public StatementEvaluator() {
        this.scope = new Environment();
        this.exprEval = new ExpressionEvaluator(this);
    }

    public StatementEvaluator(Environment global) {
        this.scope = global;
        this.exprEval = new ExpressionEvaluator(this);
    }


    // Handle Scoping

    /**
     * Create a new nested scope
     */
    private void nestScope() {
        scope = new Environment(scope);
    }

    /**
     * Get to one higher scope
     */
    private void unnestScope() {
        scope = scope.getHigherScope();
    }


    public Void execute(Statement stmt) {
        stmt.accept(this);
        return null;
    }

    public void executeScoped(Statement stmt, Environment scope) {
        Environment oldScope = this.scope;
        this.scope = scope;
        execute(stmt);
        this.scope = oldScope;
    }

    @Override
    public Void visitPrintStatement(PrintStatement printstatement) {
        System.out.println(exprEval.evaluate(printstatement.expr));
        return null;
    }

    @Override
    public Void visitExprStatement(ExprStatement exprstatement) {
        exprEval.evaluate(exprstatement.expr);
        return null;
    }

    @Override
    public Void visitVarDeclare(VarDeclare varDeclare) {
        scope.defineVariable(varDeclare.name, exprEval.evaluate(varDeclare.init));
        return null;
    }

    @Override
    public Void visitVarAssign(VarAssign varAssign) {
        scope.changeValue(varAssign.name, exprEval.evaluate(varAssign.newVal));
        return null;
    }

    @Override
    public Void visitBlock(Block block) {
        nestScope();
        try {
            for (Statement stmt : block.stmts) {
                execute(stmt);
            }
        } finally {
            unnestScope();
        }
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement ifstatement) {
        try {
            if (exprEval.evaluate(ifstatement.condition).equals(true)) {
                execute(ifstatement.thenBranch);
            } else if (ifstatement.elseBranch != null) {
                execute(ifstatement.elseBranch);
            }
        } catch (BreakException ignored) {
        }
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement whilestatement) {
        while (whilestatement.condition == null || exprEval.evaluate(whilestatement.condition).equals(true)) {
            try {
                execute(whilestatement.body);
            } catch (BreakException ignored) {
            }
        }
        return null;
    }

    @Override
    public Void visitBreakStatement(BreakStatement breakstatement) {
        throw new BreakException();
    }

    @Override
    public Void visitFunDeclare(FunDeclare fundeclare) {
        scope.defineVariable(fundeclare.name, new LoxFunction(fundeclare));
        return null;
    }


}
