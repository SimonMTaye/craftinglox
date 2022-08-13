package com.jlox.interpreter;

import com.jlox.scanner.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> mappings = new HashMap<>();
    private final Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Environment() {
        parent = null;
    }

    public Environment getHigherScope() {
        if (this.parent != null) {
            return this.parent;
        }
        return this;
    }

    /**
     * Get the value of a variable. Throws and error if the variable doesn't exist.
     *
     * @param name the name of the variable
     * @return the value stored for the variable
     */
    public Object getValue(Token name) {
        return getValue(name.lexeme);
    }

    private Object getValue(String key) {
        if (this.mappings.containsKey(key)) {
            return mappings.get(key);
        }
        if (this.parent != null) {
            return parent.getValue(key);
        }
        throw new RuntimeError(key + " is an undefined variable");
    }

    /**
     * Set the value of a variable. If the variable has already been declared, this
     * will throw an error.
     *
     * @param name  the variable to define
     * @param value the value of the variable
     */
    public void defineVariable(Token name, Object value) {
        if (mappings.containsKey(name.lexeme)) {
            throw new RuntimeError(name + " has already been declared");
        }
        mappings.put(name.lexeme, value);
    }

    /**
     * Change the value of a variable. Throws an error if the value doesn't already
     * exist in itself or its parents.
     *
     * @param name  the variable to be reassigned
     * @param value the new value
     */
    public void changeValue(Token name, Object value) {
        if (!mappings.containsKey(name.lexeme)) {
            if (parent == null) {
                throw new RuntimeError("Undefined variable " + name.lexeme);
            }
            parent.changeValue(name, value);
        }
        mappings.put(name.lexeme, value);
    }

    protected void defineInterpreterGlobal(String name, Object value) {
        mappings.put(name, value);
    }
}
