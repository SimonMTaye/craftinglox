package com.jlox.statement;


public interface StatementVisitor<R> {
	R visitPrintStatement(PrintStatement printstatement);
	R visitExprStatement(ExprStatement exprstatement);
	R visitVarDeclare(VarDeclare varAssign);
}
