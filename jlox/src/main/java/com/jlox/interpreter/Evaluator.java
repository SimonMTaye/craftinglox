package com.jlox.interpreter;

import com.jlox.expression.*;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;

public class Evaluator implements ExpressionVisitor<Object> {

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
                    return -1 * (Double) right;
                }
                if (right instanceof Integer) {
                    return -1 * (Integer) right;
                }
                throw new RuntimeError(RuntimeErrorType.TYPE_ERROR, "Expected a number.");
            }
        }
        throw new RuntimeError(RuntimeErrorType.UNEXPECTED_OPERATOR, String.format("Unknown operator '%s'", unary.operator.lexme));
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
                return operate(left, right, binary.operator);
            case PLUS: {
                if (left instanceof String || right instanceof String)
                    return left.toString() + right.toString();
                return operate(left, right, binary.operator);
            }
        }
        throw new RuntimeError(RuntimeErrorType.TYPE_ERROR, String.format("%s is not supported for %s and %s", binary.operator.lexme, left.getClass(), right.getClass()));
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
        if (object == null)
            return false;

        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        throw new RuntimeError(RuntimeErrorType.TYPE_ERROR, "Expected a boolean.");
    }

    private Object operate(Object left, Object right, Token operator) {
        if (left instanceof Double || right instanceof Double) {
            Number leftNum = (Number) left;
            Number rightNum = (Number) right;
            return operate(leftNum.doubleValue(), rightNum.doubleValue(), operator.type);
        }
        if (!(left instanceof Integer) || !(right instanceof Integer)) {
            throw new RuntimeError(RuntimeErrorType.TYPE_ERROR, String.format("%s is not supported on %s and %s",
                operator.lexme, left.getClass(), right.getClass()));
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
                    throw new RuntimeError(RuntimeErrorType.DIVIDE_BY_ZERO, "Cannot divide by 0");
                }
                return left / right;
            }
        }
        return null;
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
                    throw new RuntimeError(RuntimeErrorType.DIVIDE_BY_ZERO, "Cannot divide by 0");
                }
                return left / right;
            }
        }
        return null;
    }
}
