package com.jlox.interpreter;

import com.jlox.statement.FunDeclare;

import java.util.List;

public class LoxFunction implements LoxCallable {

    private final FunDeclare declaration;

    public LoxFunction(FunDeclare declaration) {
        this.declaration = declaration;
    }


    @Override
    public int arity() {
        return this.declaration.params.size();
    }

    @Override
    public Object call(StatementEvaluator smtEval, List<Object> arguments) {
        Environment environment = new Environment(smtEval.scope);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.defineVariable(declaration.params.get(i), arguments.get(i));
        }
        smtEval.executeScoped(declaration.body, environment);
        return null;
    }
}
