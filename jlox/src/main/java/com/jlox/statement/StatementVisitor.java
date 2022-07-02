package com.jlox.statement;


public interface StatementVisitor<R> {
	public R visitPrintStatement(PrintStatement printstatement);
	public R visitExprStatement(ExprStatement exprstatement);
	public R visitVarDeclare(VarDeclare vardeclare);
	public R visitVarAssign(VarAssign varassign);
	public R visitBlock(Block block);
	public R visitIfStatement(IfStatement ifstatement);
	public R visitWhileStatement(WhileStatement whilestatement);
}