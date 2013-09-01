package org.newdawn.loaders;

/**
 * Exception thrown if the file is not in the correct format
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public class FileFormatException extends java.lang.Exception {

    /**
     * Constructs an <code>FileFormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FileFormatException(String msg) {
        super(msg);
    }
}


