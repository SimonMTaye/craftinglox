package com.jlox.expression;

public class AstPrinter implements ExpressionVisitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinary(Binary binary) {
        return String.format("(%s %s %s)", binary.getOperator().lexme, binary.getLeft().accept(this), binary.getRight().accept(this));
    }

    @Override
    public String visitTernary(Ternary ternary) {
        return String.format("(if %s %s else %s)",
            ternary.condition.accept(this),
            ternary.left.accept(this),
            ternary.right.accept(this)
        );
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
        return String.format("(%s %s)", unary.getOperator().lexme, unary.getRight().accept(this));
    }

    @Override
    public String visitVariable(Variable variable) {
        return String.format("$(%s)", variable.getName());
    }
}
