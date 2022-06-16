package com.jlox.interpreter;

import com.jlox.error.ConsoleHandler;
import com.jlox.error.IErrorHandler;
import com.jlox.error.LoxError;
import com.jlox.statement.Statement;

import java.util.List;

public class Interpreter {

    private final StatementEvaluator stmtEval = new StatementEvaluator();
    private final IErrorHandler handler;

    public Interpreter(IErrorHandler handler) {
        this.handler = handler;
    }

    public Interpreter() {
        this.handler = new ConsoleHandler();
    }


    public Void run(List<Statement> stmts) {
        try {
            for (Statement stmt : stmts) {
                stmtEval.execute(stmt);
            }
        }
        catch (LoxError e) {
            handler.error(e);
        }
        return null;
    }
}
