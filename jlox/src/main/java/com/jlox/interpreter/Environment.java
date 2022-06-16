package com.jlox.interpreter;

import com.jlox.expression.Expression;
import com.jlox.expression.Variable;
import com.jlox.parser.ParseErrorCode;
import com.jlox.parser.ParseLoxError;

import java.util.HashMap;

public class Environment {

    private final HashMap<String, Expression> mappings = new HashMap<>();
    private final Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Environment() {
        parent = null;
    }

    public Environment getHigherScope() {
        if (this.parent != null) return this.parent;
        return this;
    }

    /**
     * Get the value of a variable. Throws and error if the variable doesn't exist
     * @param variable the variable
     * @return the value stored for the variable
     */
    public Expression getValue(Variable variable) {
        return getValue(variable.getName(), variable.getOffset());
    }

    /**
     * Set the value of a variable. If the variable has already
     * @param variable the variable to define
     * @param value the value of the variable
     */
    public void defineVarible(Variable variable, Expression value) {
        if (mappings.containsKey(variable.getName())) {
            throw new ParseLoxError(variable.getName() + " has already been declared",ParseErrorCode.EXISTING_VARIABLE, variable.getOffset());
        }
        mappings.put(variable.getName(), value);
    }

    /**
     * Change the value of a variable. Throws an error if the value doesn't already exist in itself or its parents
     * @param variable the variable to be reassigned
     * @param value the new value
     */
    public void changeValue(Variable variable, Expression value) {
        if (!mappings.containsKey(variable.getName())) {
            if (parent == null)
                throw new ParseLoxError(variable.getName() + " has not been declared",ParseErrorCode.UNDEFINED_VALUE, variable.getOffset());
            parent.changeValue(variable, value);
        }
        mappings.put(variable.getName(), value);
    }

    private Expression getValue(String key, int offset) {
        if (this.mappings.containsKey(key)) return mappings.get(key);
        if (this.parent != null) return parent.getValue(key, offset);
        throw new ParseLoxError(key + " is an undefined variable", ParseErrorCode.UNDEFINED_VALUE, offset);
    }

}
