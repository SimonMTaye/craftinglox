package com.jlox.interpreter;


import com.jlox.statement.*;

public class StatementEvaluator implements StatementVisitor<Void> {

    private final ExpressionEvaluator exprEval;
    private final Interpreter interpreter;


    public StatementEvaluator(Interpreter interpreter) {
        this.interpreter = interpreter;
        this.exprEval = new ExpressionEvaluator(interpreter);
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
        this.interpreter.getScope().defineVariable(varDeclare.name, exprEval.evaluate(varDeclare.init));
        return null;
    }

    @Override
    public Void visitVarAssign(VarAssign varAssign) {
        this.interpreter.getScope().changeValue(varAssign.name, exprEval.evaluate(varAssign.newVal));
        return null;
    }

    @Override
    public Void visitBlock(Block block) {
        this.interpreter.nestScope();
        try {
            for (Statement stmt : block.stmts) {
                execute(stmt);
            }
        } finally {
            this.interpreter.unnestScope();
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
        this.interpreter.getScope().defineVariable(fundeclare.name, new LoxFunction(fundeclare));
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement returnstatement) {
        throw new ReturnException(exprEval.evaluate(returnstatement.value));
    }


}
