package com.jlox.interpreter;

import java.util.List;

interface LoxCallable {
    int arity();
    Object call(List<Object> arguments);
}
