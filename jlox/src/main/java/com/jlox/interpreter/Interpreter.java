package com.jlox.interpreter;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.error.LoxError;
import com.jlox.statement.Statement;

import java.util.List;

public class Interpreter {

    private final StatementEvaluator stmtEval;
    private final IErrorHandler handler;

    private Environment scope;

    public Interpreter() {
        this(new ConsoleHandler(), new Environment());
    }
    public Interpreter(Environment scope) {
        this(new ConsoleHandler(), scope);
    }

    public Interpreter(IErrorHandler handler, Environment scope) {
        this.handler = handler;
        this.scope = scope;
        this.stmtEval = new StatementEvaluator(this);
        scope.defineInterpreterGlobal("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return System.currentTimeMillis();
            }
        });
    }


    public void nestScope() {
        scope = new Environment(scope);
    }

    public void unnestScope() {
        scope = scope.getHigherScope();
    }

    public Environment getScope() {
        return scope;
    }

    public void execute(Statement stmt) {
        stmtEval.execute(stmt);
    }

    public void execute(List<Statement> stmts) {
        for (Statement stmt : stmts) {
            stmtEval.execute(stmt);
        }
    }

    public Void run(List<Statement> stmts) {
        try {
            for (Statement stmt : stmts) {
                stmtEval.execute(stmt);
            }
        } catch (LoxError e) {
            handler.error(e);
        }
        return null;
    }
}

