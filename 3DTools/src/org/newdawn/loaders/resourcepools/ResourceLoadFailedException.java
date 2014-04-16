package org.newdawn.loaders.resourcepools;

/**
 * Exception that is thrown when a resouce fails to load
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public class ResourceLoadFailedException extends Exception {

    /**
     * Constructs a <code>ResourceLoadFailedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ResourceLoadFailedException(String msg) {
        super(msg);
    }
}


