package com.jlox.expression;

public class AstPrinter implements ExpressionVisitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinary(Binary binary) {
        return String.format("(%s %s %s)", binary.operator.lexme, binary.left.accept(this), binary.right.accept(this));
    }

    @Override
    public String visitGrouping(Grouping grouping) {
        return String.format("(%s)", grouping.expr.accept(this));
    }

    @Override
    public String visitLiteral(Literal literal) {
        if (literal.value == null) return "nil";
        return literal.value.toString();
    }

    @Override
    public String visitUnary(Unary unary) {
        return String.format("(%s %s)", unary.operator.lexme, unary.right.accept(this));
    }
}
