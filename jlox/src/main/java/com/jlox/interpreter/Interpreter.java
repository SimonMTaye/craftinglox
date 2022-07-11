package com.jlox.interpreter;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.error.LoxError;
import com.jlox.statement.Statement;

import java.util.List;

public class Interpreter {

    private final StatementEvaluator stmtEval;
    private final IErrorHandler handler;

    public Interpreter(IErrorHandler handler) {
        this.handler = handler;
        this.stmtEval = new StatementEvaluator();
    }

    public Interpreter() {
        this.handler = new ConsoleHandler();
        Environment globals = new Environment();
        this.stmtEval = new StatementEvaluator(globals);
        globals.defineInterpreterGlobal("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(List<Object> arguments) {
                return System.currentTimeMillis();
            }
        });
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

