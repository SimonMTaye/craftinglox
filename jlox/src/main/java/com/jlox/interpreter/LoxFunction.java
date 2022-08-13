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
    public Object call(Interpreter interpreter, List<Object> arguments) {
        interpreter.nestScope();
        Environment environment = interpreter.getScope();
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.defineVariable(declaration.params.get(i), arguments.get(i));
        }
        try {
            interpreter.execute(declaration.body);
        } catch (ReturnException returnValue) {
            return returnValue.value;
        }
        return null;
    }
}
