package com.jlox.interpreter;

import com.jlox.expression.*;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;

import java.util.ArrayList;

public class ExpressionEvaluator implements ExpressionVisitor<Object> {

    private final Interpreter interpreter;

    public ExpressionEvaluator(Interpreter  interpreter) {
        this.interpreter = interpreter;
    }

    public Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public Object visitUnary(Unary unary) {
        switch (unary.operator.type) {
            case BANG:
                return !isTruthy(unary.right.accept(this));
            case MINUS: {
                Object right = unary.right.accept(this);
                if (right instanceof Double) {
                    return -1 * (Double)right;
                }
                if (right instanceof Integer) {
                    return -1 * (Integer)right;
                }
                throw new RuntimeError( "Expected a number.");
            }
            default:
                throw new RuntimeError( String.format("Unknown operator '%s'", unary.operator.lexeme));
        }
    }

    @Override
    public Object visitVariable(Variable variable) {
        return interpreter.getScope().getValue(variable.name);
    }

    @Override
    public Object visitCall(Call call) {
        Object callee = evaluate(call.callee);
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(callee.toString() + "is not callable");
        }
        LoxCallable function = (LoxCallable)callee;

        ArrayList<Object> arguments = new ArrayList<>();
        for (Expression expr : call.arguments) {
            arguments.add(evaluate(expr));
        }

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(String.format("Expected %d arguments but got %d.", function.arity(), arguments.size()));
        }

        return function.call(interpreter, arguments);
    }

    @Override
    public Object visitBinary(Binary binary) {
        Object left = binary.left.accept(this);
        Object right = binary.right.accept(this);
        switch (binary.operator.type) {
            case COMMA:
                return right;
            case EQUAL_EQUAL:
                return left.equals(right);
            case BANG_EQUAL:
                return !left.equals(right);
            case SLASH:
            case STAR:
            case MINUS:
            case GREATER:
            case GREATER_EQUAL:
            case LESS_EQUAL:
            case LESS:
                return operate(left, right, binary.operator);
            case PLUS: {
                if (left instanceof String || right instanceof String) {
                    return left.toString() + right.toString();
                }
                return operate(left, right, binary.operator);
            }
        }
        throw new RuntimeError( String.format("%s is not supported for %s and %s", binary.operator.lexeme, left.getClass(), right.getClass()));
    }

    // Logical operator with short-circuiting
    @Override
    public Object visitLogical(Logical logical) {
        Object left = evaluate(logical.left);
        boolean truthy = isTruthy(left);
        if (logical.operator.type == TokenType.OR) {
            if (truthy) {
                return left;
            }
        } else if (logical.operator.type == TokenType.AND) {
            if (!truthy) {
                return left;
            }
        }
        return evaluate(logical.right);
    }

    @Override
    public Object visitTernary(Ternary ternary) {
        if (isTruthy(ternary.condition.accept(this))) {
            return ternary.left.accept(this);
        }
        return ternary.right.accept(this);
    }

    @Override
    public Object visitGrouping(Grouping grouping) {
        return grouping.expr.accept(this);
    }

    @Override
    public Object visitLiteral(Literal literal) {
        return literal.value;
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        throw new RuntimeError( "Expected a boolean.");
    }

    private Object operate(Object left, Object right, Token operator) {
        if (left instanceof Double || right instanceof Double) {
            Number leftNum = (Number)left;
            Number rightNum = (Number)right;
            return operate(leftNum.doubleValue(), rightNum.doubleValue(), operator.type);
        }
        if (!(left instanceof Integer) || !(right instanceof Integer)) {
            throw new RuntimeError( String.format("%s is not supported on %s and %s",
                    operator.lexeme, left.getClass(), right.getClass()));
        }
        return operate((Integer) left, (Integer) right, operator.type);

    }

    private Object operate(Double left, Double right, TokenType operator) {
        switch (operator) {
            case MINUS:
                return left - right;
            case STAR:
                return left * right;
            case PLUS:
                return left + right;
            case SLASH: {
                if (right.equals(0d)) {
                    throw new RuntimeError( "Cannot divide by 0");
                }
                return left / right;
            }
            case GREATER:
                return left > right;
            case GREATER_EQUAL:
                return left >= right;
            case LESS_EQUAL:
                return left <= right;
            case LESS:
                return left < right;
            default:
                return null;
        }
    }

    private Object operate(Integer left, Integer right, TokenType operator) {
        switch (operator) {
            case MINUS:
                return left - right;
            case STAR:
                return left * right;
            case PLUS:
                return left + right;
            case SLASH: {
                if (right.equals(0)) {
                    throw new RuntimeError( "Cannot divide by 0");
                }
                return left / right;
            }
            case GREATER:
                return left > right;
            case GREATER_EQUAL:
                return left >= right;
            case LESS_EQUAL:
                return left <= right;
            case LESS:
                return left < right;
            default:
                return null;
        }
    }
}
