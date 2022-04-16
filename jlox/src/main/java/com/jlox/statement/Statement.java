package com.jlox.statement;


public abstract class Statement {
	abstract <R> R accept(StatementVisitor<R> v);
}