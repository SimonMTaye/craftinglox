package com.jlox.interpreter;

import com.jlox.expression.*;

public class AstPrinter implements ExpressionVisitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinary(Binary binary) {
        return String.format("(%s %s %s)", binary.operator.lexeme, binary.left.accept(this), binary.right.accept(this));
    }

    @Override
    public String visitLogical(Logical logical) {
        return String.format("(%s %s %s)",
                logical.operator.lexeme, logical.left.accept(this), logical.right.accept(this));
    }

    @Override
    public String visitTernary(Ternary ternary) {
        return String.format("(if %s %s else %s)",
                ternary.condition.accept(this),
                ternary.left.accept(this),
                ternary.right.accept(this));
    }

    @Override
    public String visitGrouping(Grouping grouping) {
        return String.format("(%s)", grouping.expr.accept(this));
    }

    @Override
    public String visitLiteral(Literal literal) {
        if (literal.value == null) {
            return "nil";
        }
        return literal.value.toString();
    }

    @Override
    public String visitUnary(Unary unary) {
        return String.format("(%s %s)", unary.operator.lexeme, unary.right.accept(this));
    }

    @Override
    public String visitVariable(Variable variable) {
        return String.format("$(%s)", variable.name.lexeme);
    }

    @Override
    public String visitCall(Call call) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("(%s", call.callee.accept(this)));
        for (Expression arg : call.arguments) {
            builder.append(String.format(" %s", arg.accept(this)));
        }
        builder.append(")");
        return builder.toString();
    }
}
