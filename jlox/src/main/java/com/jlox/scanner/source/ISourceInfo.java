package com.jlox.scanner.source;

/**
 * A source code that provides methods for getting line number and column number
 */
public interface ISourceInfo extends ISource {
    /**
     * Get the line number of the current character
     * 
     * @return the line number of the current char
     */
    int getLineNumber();

    /**
     * Get the line number of the specified char
     * 
     * @param offset the position of the desired character
     * @return the line number
     */
    int getLineNumber(int offset);

    /**
     * Get the column number of the current char
     * 
     * @return the column number
     */
    int getColNumber();

    /**
     * Get the column of the speicifed char
     * 
     * @param offset the position of the desired char
     * @return the column number
     */
    int getColNumber(int offset);

}
