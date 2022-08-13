package com.jlox.interpreter;

import com.jlox.scanner.Token;
import com.jlox.scanner.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestEnvironment {

    private Token getIdentifier(String name) {
        return new Token(TokenType.IDENTIFIER, name, name, 0);
    }

    /**
     * Test that a global environment (i.e. one with no parents) assigns value as
     * expected
     */
    @Test
    void testGlobalAssign() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Token test1 = getIdentifier("test1");
        globalEnv.defineVariable(test1, "Hello World");
        assertEquals("Hello World", globalEnv.getValue(test1));
    }

    @Test
    void testGlobalReAssign() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Token test1 = getIdentifier("test1");
        globalEnv.defineVariable(test1, "Hello World");
        globalEnv.changeValue(test1, true);
        assertEquals(true, globalEnv.getValue(test1));
    }

    @Test
    void testScopedAssignAndReassign() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Token test1 = getIdentifier("test1");
        globalEnv.defineVariable(test1, "Hello World");
        Environment scopedEnv = new Environment(globalEnv);
        // Test that scopedEnv can read from its parent
        assertEquals("Hello World", scopedEnv.getValue(test1));

        // Test that scopedEnv variable can be redefined
        scopedEnv.defineVariable(test1, "Blue");
        assertEquals("Blue", scopedEnv.getValue(test1));

        scopedEnv.changeValue(test1, "Simon");
        assertEquals("Simon", scopedEnv.getValue(test1));
        // Test that globalEnv variable is unaffected by redefinition
        assertEquals("Hello World", globalEnv.getValue(test1));

        Token test2 = getIdentifier("test2");
        scopedEnv.defineVariable(test2, true);
        assertEquals(true, scopedEnv.getValue(test2));

        // Test that variables defined in scopedEnv aren't available in the parent
        assertThrows(
                RuntimeError.class,
                () -> globalEnv.getValue(test2));
    }

    /**
     * Test that a global environment (i.e. one with no parents) throws an error
     * when fetching or changing a variable
     * that isn't defined and re-defining an existing variable
     */
    @Test
    void testGlobalAssignError() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Token test1 = getIdentifier("test1");
        assertThrows(
                RuntimeError.class,
                () -> globalEnv.getValue(test1));

        assertThrows(
                RuntimeError.class,
                () -> globalEnv.changeValue(test1, "Hello World"));

        globalEnv.defineVariable(test1, "Hello World");
        assertThrows(
                RuntimeError.class,
                () -> globalEnv.defineVariable(test1, "Simon"));
    }

    /**
     * Test that a global environment throws and error when reassigning a variable
     * that doesn't exist.
     */
    @Test
    void testScopedError() {
        // Check that a value is assigned correctly
        Environment globalEnv = new Environment();
        Token test1 = getIdentifier("test1");
        Environment scopedEnv = new Environment(globalEnv);
        assertThrows(
                RuntimeError.class,
                () -> scopedEnv.changeValue(test1, "Hello World"));
        assertThrows(
                RuntimeError.class,
                () -> scopedEnv.getValue(test1));

        scopedEnv.defineVariable(test1, "Hello World");
        assertThrows(
                RuntimeError.class,
                () -> scopedEnv.defineVariable(test1, "Simon"));
    }

}
