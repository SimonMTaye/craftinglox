package com.jlox.interpreter;


import com.jlox.expression.Variable;
import com.jlox.statement.*;

public class StatementEvaluator implements StatementVisitor<Void> {

    private Environment scope;
    private final ExpressionEvaluator exprEval;



    public StatementEvaluator() {
        this.scope = new Environment();
        this.exprEval = new ExpressionEvaluator(scope);
    }

    public StatementEvaluator(Environment global) {
        this.scope = global;
        this.exprEval = new ExpressionEvaluator(scope);
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
        scope.defineVariable(new Variable(varDeclare.name), varDeclare.init);
        return null;
    }

    @Override
    public Void visitVarAssign(VarAssign varAssign) {
        scope.changeValue(new Variable(varAssign.name), varAssign.newVal);
        return null;
    }

    @Override
    public Void visitBlock(Block block) {
        nestScope();
        for (Statement stmt : block.stmts) {
            execute(stmt);
        }
        unnestScope();
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement ifstatement) {
        if (exprEval.evaluate(ifstatement.condition).equals(true)) {
            execute(ifstatement.thenBranch);
        } else if (ifstatement.elseBranch != null) {
            execute(ifstatement.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement whilestatement) {
        while (whilestatement.condition == null || exprEval.evaluate(whilestatement.condition).equals(true)) {
            execute(whilestatement.body);
        }
        return null;
    }


}
