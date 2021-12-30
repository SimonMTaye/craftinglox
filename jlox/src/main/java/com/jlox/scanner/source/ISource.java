package com.jlox.scanner.source;

/**
 * Wrapper for source code. Get the required chars and current position
 */
public interface ISource {
    /**
     * Peek at the next character in the source file without advancing
     * 
     * @return the next char
     */
    char peek();

    /**
     * Get the next char in the file
     * 
     * @return the next char
     */
    char advance();

    /**
     * Return the substring between start and end
     * 
     * @param start the start poisiton (inclusive)
     * @param end   the end position (exclusive)
     * @return the string located between start and end
     */
    String get(int start, int end);

    /**
     * Check if the whole find has been scanned
     * 
     * @return if the whole find has been scanned
     */
    boolean isAtEnd();

    /**
     * Get the offset from the start of the source file of the current char
     * 
     * @return the offset of the current char
     */
    int getOffset();
}
