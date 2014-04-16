package org.newdawn.j3d.loaders.ac3d;

import javax.media.j3d.Shape3D;

/**
 * Default implementation of <code>NodeFactory</code>
 *
 * @author Jeremy
 * @version $Revision: 1.1 $
 */
public class DefaultNodeFactory implements NodeFactory {

    /** Creates new DefaultNodeFactory */
    public DefaultNodeFactory() {
    }

    /**
     * Create a shape 3d object
     *
     * @return The created shaped 3d object
     */
    public Shape3D createShape3D() {
        return new Shape3D();
    }
    
    /**
     * "Finish off" a shape
     *
     * @param shape The shape to finish off
     */
    public void finishOffShape(Shape3D shape) {
    }
    
}
