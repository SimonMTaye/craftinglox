package com.jlox.interpreter;

import com.jlox.expression.Expression;
import com.jlox.expression.Literal;
import com.jlox.expression.Variable;
import com.jlox.parser.ParseLoxError;
import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestEnvironment {

    private static final ExpressionEvaluator eval = new ExpressionEvaluator();

    private Token getIdentifier(String name) {
        return new Token(TokenType.IDENTIFIER, name, name, 0);
    }

    private Object evalExpression(Expression expr) {
        return eval.evaluate(expr);
    }

    /**
     * Test that a global environment (i.e. one with no parents) assigns value as expected
     */
    @Test
    void TestGlobalAssign() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Variable test1 = new Variable(getIdentifier("test1"));
        globalEnv.defineVarible(test1, new Literal("Hello World"));
        assertEquals("Hello World", evalExpression(globalEnv.getValue(test1)));
    }

    @Test
    void TestGlobalReAssign() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Variable test1 = new Variable(getIdentifier("test1"));
        globalEnv.defineVarible(test1, new Literal("Hello World"));
        globalEnv.changeValue(test1, new Literal(true));
        assertEquals(true, evalExpression(globalEnv.getValue(test1)));
    }

    @Test
    void TestScopedAssignAndReassign() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Variable test1 = new Variable(getIdentifier("test1"));
        globalEnv.defineVarible(test1, new Literal("Hello World"));
        Environment scopedEnv = new Environment(globalEnv);
        // Test that scopedEnv can read from its parent
        assertEquals("Hello World", evalExpression(scopedEnv.getValue(test1)));

        // Test that scopedEnv variable can be redefined
        scopedEnv.defineVarible(test1, new Literal("Blue"));
        assertEquals("Blue", evalExpression(scopedEnv.getValue(test1)));

        scopedEnv.changeValue(test1, new Literal("Simon"));
        assertEquals("Simon", evalExpression(scopedEnv.getValue(test1)));
        // Test that globalEnv variable is unaffected by redefinition
        assertEquals("Hello World", evalExpression(globalEnv.getValue(test1)));

        Variable test2 = new Variable(getIdentifier("test2"));
        scopedEnv.defineVarible(test2, new Literal(true));
        assertEquals(true, evalExpression(scopedEnv.getValue(test2)));

        // Test that varibles defined in scopedEnv aren't available in the parent
        assertThrows(
            ParseLoxError.class,
            () -> globalEnv.getValue(test2)
        );
    }
    /**
     * Test that a global environment (i.e. one with no parents) throws an error when fetching or changing a variable
     * that isn't defined and re-defining an exisiting variable
     */
    @Test
    void TestGlobalAssignError() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Variable test1 = new Variable(getIdentifier("test1"));
        assertThrows(
            ParseLoxError.class,
            () -> globalEnv.getValue(test1)
        );

        assertThrows(
            ParseLoxError.class,
            () -> globalEnv.changeValue(test1, new Literal("Hello World"))
        );

        globalEnv.defineVarible(test1, new Literal("Hello World"));
        assertThrows(
            ParseLoxError.class,
            () -> globalEnv.defineVarible(test1, new Literal("Simon"))
        );
    }
    /**
     * Test that a global environment throws and error when reassigning a varible that doesn't exist
     */
    @Test
    void TestScopedError() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Variable test1 = new Variable(getIdentifier("test1"));
        Environment scopedEnv = new Environment(globalEnv);
        assertThrows(
            ParseLoxError.class,
            () -> scopedEnv.changeValue(test1, new Literal("Hello World"))
        );
        assertThrows(
            ParseLoxError.class,
            () -> scopedEnv.getValue(test1)
        );

        scopedEnv.defineVarible(test1, new Literal("Hello World"));
        assertThrows(
            ParseLoxError.class,
            () -> scopedEnv.defineVarible(test1, new Literal("Simon"))
        );
    }

}
